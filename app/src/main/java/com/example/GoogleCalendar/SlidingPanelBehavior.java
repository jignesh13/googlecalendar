//package com.example.expandedtopviewtestupdate;
//
//import android.content.Context;
//import android.support.annotation.NonNull;
//import android.support.design.widget.AppBarLayout;
//import android.support.design.widget.CoordinatorLayout;
//import android.support.v4.view.NestedScrollingChild2;
//import android.support.v4.view.ViewCompat;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//import android.view.View;
//
///**
// * <p>{@code SlidingPanelBehavior} provides all the functionality of
// * {@code AppBarLayout.ScrollingViewBehavior}
// * but allows a view that implements the {@code NestedScrollingChild2} interface, such as
// * {@code RecyclerView}, to slide up and down as a panel. This sliding panel effect is similar
// * to the functionality of the day/week/month/agenda view of the Google Calendar app when the
// * month toggle is clicked in the toolbar and a concise calendar month slides down from the top
// * and the day/week/month/agenda view slides down below it.
// * </p>
// * <h2>Set Up</h2>
// * <h3>Layout Structure</h3>
// * Below is the general structure for the {@code CoordinatorLayout}. The key components here
// * are the scroll flags of the {@code CollapsingToolbarLayout}, the view that will toggle the
// * appbar from closed to expanded and the presence of the {@code NestedScrollingChild2} view which
// * is a {@code RecyclerView} here.
// * <p>
// * <pre>{@code
// *     <android.support.design.widget.CoordinatorLayout
// *          <android.support.design.widget.AppBarLayout
// *            <android.support.design.widget.CollapsingToolbarLayout
// *              app:layout_scrollFlags="scroll|exitUntilCollapsed|enterAlways|snap"
// *              ... >
// *              <ImageView
// *                android:layout_height="?dp"
// *                android:layout_marginTop="?attr/actionBarSize"
// *                ... />
// *              <android.support.v7.widget.Toolbar
// *                app:layout_collapseMode="pin"
// *                ... >
// *
// *     <!-- View goes here that toggles the sliding panel expanded/collapsed state -->
// *
// *         <android.support.v7.widget.RecyclerView
// *           app:layout_behavior=".SlidingPanelBehavior"
// *           ... />
// * }</pre>
// * <p>
// * <h3>Responsibilities of the App</h3>
// * The above layout will display most of the desired behavior. There are a few remaining
// * issues to be addressed by the app:
// * <br/><br/>
// * <ol>
// * <li>
// * Disabled dragging of the appbar open and closed via direct touch.
// * See <a href="https://developer.android.com/reference/android/support/design/widget/AppBarLayout.Behavior.DragCallback.html" target="_blank">AppBarLayout.Behavior.DragCallback</a>.
// * <br/><br/>
// * </li>
// * <li>
// * Toggle the appbar expanded/collapsed. When toggled expanded, enable nested scrolling on the
// * {@code NestedScrollingChild2}. When toggled closed, disable nested scrolling on the
// * {@code NestedScrollingChild2}.
// * </li>
// * </ol>
// */
//
//@SuppressWarnings("unused")
//public class SlidingPanelBehavior extends AppBarLayout.ScrollingViewBehavior {
//    private AppBarLayout mAppBar;
//
//    public SlidingPanelBehavior() {
//        super();
//    }
//
//    public SlidingPanelBehavior(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    @Override
//    public boolean layoutDependsOn(final CoordinatorLayout parent, View child, View dependency) {
//        if (mAppBar == null && dependency instanceof AppBarLayout) {
//            // Capture our appbar for later use.
//            mAppBar = (AppBarLayout) dependency;
//        }
//        return dependency instanceof AppBarLayout;
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(CoordinatorLayout parent, View child, MotionEvent event) {
//        int action = event.getAction();
//
//        if (event.getAction() != MotionEvent.ACTION_DOWN) { // Only want "down" events
//            return false;
//        }
//        if (getAppBarLayoutOffset(mAppBar) == -mAppBar.getTotalScrollRange()) {
//            // When appbar is collapsed, don't let it open through nested scrolling.
//            setNestedScrollingEnabledWithTest((NestedScrollingChild2) child, false);
//        } else {
//            // Appbar is partially to fully expanded. Set nested scrolling enabled to activate
//            // the methods within this behavior.
//            setNestedScrollingEnabledWithTest((NestedScrollingChild2) child, true);
//        }
//        return false;
//    }
//
//    @Override
//    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child,
//                                       @NonNull View directTargetChild, @NonNull View target,
//                                       int axes, int type) {
//        //noinspection RedundantCast
//        return ((NestedScrollingChild2) child).isNestedScrollingEnabled();
//    }
//
//    @Override
//    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child,
//                                  @NonNull View target, int dx, int dy, @NonNull int[] consumed,
//                                  int type) {
//        // How many pixels we must scroll to fully expand the appbar. This value is <= 0.
//        final int appBarOffset = getAppBarLayoutOffset(mAppBar);
//
//        // Check to see if this scroll will expand the appbar 100% or collapse it fully.
//        if (dy <= appBarOffset) {
//            // Scroll by the amount that will fully expand the appbar and dispose of the rest (dy).
//            super.onNestedPreScroll(coordinatorLayout, mAppBar, target, dx,
//                                    appBarOffset, consumed, type);
//            consumed[1] += dy;
//        } else if (dy >= (mAppBar.getTotalScrollRange() + appBarOffset)) {
//            // This scroll will collapse the appbar. Collapse it and dispose of the rest.
//            super.onNestedPreScroll(coordinatorLayout, mAppBar, target, dx,
//                                    mAppBar.getTotalScrollRange() + appBarOffset,
//                                    consumed, type);
//            consumed[1] += dy;
//        } else {
//            // This scroll will leave the appbar partially open. Just do normal stuff.
//            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
//        }
//    }
//
//    /**
//     * {@code onNestedPreFling()} is overriden to address a nested scrolling defect that was
//     * introduced in API 26. This method prevent the appbar from misbehaving when scrolled/flung.
//     * <p>
//     * Refer to <a href="https://issuetracker.google.com/issues/65448468"  target="_blank">"Bug in design support library"</a>
//     */
//
//    @Override
//    public boolean onNestedPreFling(@NonNull CoordinatorLayout coordinatorLayout,
//                                    @NonNull View child, @NonNull View target,
//                                    float velocityX, float velocityY) {
//        //noinspection RedundantCast
//        if (((NestedScrollingChild2) child).isNestedScrollingEnabled()) {
//            // Just stop the nested fling and let the appbar settle into place.
//            ((NestedScrollingChild2) child).stopNestedScroll(ViewCompat.TYPE_NON_TOUCH);
//            return true;
//        }
//        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
//    }
//
//    private static int getAppBarLayoutOffset(AppBarLayout appBar) {
//        final CoordinatorLayout.Behavior behavior =
//            ((CoordinatorLayout.LayoutParams) appBar.getLayoutParams()).getBehavior();
//        if (behavior instanceof AppBarLayout.Behavior) {
//            return ((AppBarLayout.Behavior) behavior).getTopAndBottomOffset();
//        }
//        return 0;
//    }
//
//    // Something goes amiss when the flag it set to its current value, so only call
//    // setNestedScrollingEnabled() if it will result in a change.
//    private void setNestedScrollingEnabledWithTest(NestedScrollingChild2 child, boolean enabled) {
//        if (child.isNestedScrollingEnabled() != enabled) {
//            child.setNestedScrollingEnabled(enabled);
//        }
//    }
//
//    @SuppressWarnings("unused")
//    private static final String TAG = "SlidingPanelBehavior";
//}
