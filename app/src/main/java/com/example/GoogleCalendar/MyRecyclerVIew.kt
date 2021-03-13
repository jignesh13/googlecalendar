package com.example.GoogleCalendar

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.view.ViewCompat

/**A RecyclerView that allows temporary pausing of casuing its scroll to affect appBarLayout, based on https://stackoverflow.com/a/45338791/878126 */
class MyRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : androidx.recyclerview.widget.RecyclerView(context, attrs, defStyle) {
    private var mAppBarTracking: AppBarTracking? = null
    private var mView: View? = null
    private var mTopPos: Int = 0
    private var lastev: Float = 0f;
    private var current: Int = 0
    private var mLayoutManager: androidx.recyclerview.widget.LinearLayoutManager? = null

    interface AppBarTracking {
        fun isAppBarIdle(): Boolean
        fun isAppBarExpanded(): Boolean
        fun isAppBarClosed(): Boolean
        fun appbaroffset(): Int
    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {

        if (ev!!.action == MotionEvent.ACTION_MOVE) {

        } else {
            if (mAppBarTracking!!.isAppBarExpanded())
                current = mLayoutManager!!.findFirstVisibleItemPosition();
        }
        return super.dispatchTouchEvent(ev);
//        if (mAppBarTracking!!.appbaroffset()!=0){
//
//            return super.dispatchTouchEvent(ev);
//
//        }
//        else{
//            if (ev!!.getY() > lastev) {
//                lastev = ev!!.getY();
//                return false
//            }
//            else {
//                lastev = ev!!.getY();
//                return super.dispatchTouchEvent(ev);
//            }
//        }

//        if (ev!!.action == MotionEvent.ACTION_MOVE) {
//            if (ev!!.getY() > lastev&&!mAppBarTracking!!.isAppBarClosed()) {
//                lastev = ev!!.getY();
//                return false
//            } else {
//
//                lastev = ev!!.getY();
//                return super.dispatchTouchEvent(ev);
//
//            }
//
//        } else {
//            lastev = ev!!.getY();
//            return super.dispatchTouchEvent(ev);
//
//        }
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?, type: Int): Boolean {

        if (type == ViewCompat.TYPE_NON_TOUCH && mAppBarTracking!!.isAppBarIdle()
                && isNestedScrollingEnabled) {
            if (dy > 0) {

                if (mAppBarTracking!!.isAppBarExpanded()) {
                    consumed!![1] = dy
                    return true
                }
            } else {

                mView = mLayoutManager!!.findViewByPosition(mAppBarTracking!!.appbaroffset())
                if (mView != null) {
                    consumed!![1] = dy - mView!!.top + MainActivity.topspace
                }

                return true

            }
        }
        if (dy < 0 && type == ViewCompat.TYPE_TOUCH && mAppBarTracking!!.isAppBarExpanded()) {
            consumed!![1] = dy
            return true
        }

        val returnValue = super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)

        if (offsetInWindow != null && !isNestedScrollingEnabled && offsetInWindow[1] != 0) {
            offsetInWindow[1] = 0

        }
        return returnValue
    }


    override fun setLayoutManager(layout: LayoutManager?) {
        super.setLayoutManager(layout)
        mLayoutManager = layoutManager as androidx.recyclerview.widget.LinearLayoutManager
    }
//     fun setLayoutManager(layout: LayoutManager) {
//        super.setLayoutManager(layout)
//        mLayoutManager = layoutManager as LinearLayoutManager
//    }

    fun setAppBarTracking(appBarTracking: AppBarTracking) {
        mAppBarTracking = appBarTracking
    }

//    override fun fling(velocityX: Int, velocityY: Int): Boolean {
//        stopScroll()
//
//        var velocityY = velocityY
//        if (!mAppBarTracking!!.isAppBarIdle()) {
//            val vc = ViewConfiguration.get(context)
//            velocityY = if (velocityY < 0) -vc.scaledMinimumFlingVelocity
//            else vc.scaledMinimumFlingVelocity
//        }
//
//        return super.fling(velocityX, velocityY)
//    }

}