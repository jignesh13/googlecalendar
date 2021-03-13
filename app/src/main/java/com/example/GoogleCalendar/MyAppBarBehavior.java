package com.example.GoogleCalendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.appbar.AppBarLayout;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

/**
 * Attach this behavior to AppBarLayout to disable the bottom portion of a closed appBar
 * so it cannot be touched to open the appBar. This behavior is helpful if there is some
 * portion of the appBar that displays when the appBar is closed, but should not open the appBar
 * when the appBar is closed.
 */
public class MyAppBarBehavior extends AppBarLayout.Behavior {

    private static final int TOP_CHILD_FLING_THRESHOLD = 3;
    // Touch above this y-axis value can open the appBar.
    private int mCanOpenBottom;
    private boolean shouldScroll = false;
    // Determines if the appBar can be dragged open or not via direct touch on the appBar.
    private boolean mCanDrag = true;
    private boolean isPositive;

    @SuppressWarnings("unused")
    public MyAppBarBehavior() {
        init();
    }

    @SuppressWarnings("unused")
    public MyAppBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout parent, @NonNull AppBarLayout child, @NonNull View directTargetChild, View target, int nestedScrollAxes, int type) {
        return shouldScroll;
    }

    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent, @NonNull AppBarLayout child, @NonNull MotionEvent ev) {
        if (shouldScroll) {
            return super.onTouchEvent(parent, child, ev);
        } else {
            return false;
        }
    }

    public void setScrollBehavior(boolean shouldScroll) {
        this.shouldScroll = shouldScroll;
    }

    public boolean isShouldScroll() {
        return shouldScroll;
    }

    private void init() {
        setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return true;
            }
        });
    }


    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent,
                                         AppBarLayout child,
                                         MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // If appBar is closed. Only allow scrolling in defined area.
            if (child.getTop() <= -child.getTotalScrollRange()) {
                mCanDrag = event.getY() < mCanOpenBottom;
            }
        }
        return super.onInterceptTouchEvent(parent, child, event);
    }


    public void setCanOpenBottom(int bottom) {
        mCanOpenBottom = bottom;
    }
}
