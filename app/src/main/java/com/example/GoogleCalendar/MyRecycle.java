package com.example.GoogleCalendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyRecycle extends RecyclerView {
    private LinearLayoutManager linearLayoutManager;
    private AppBarTracking appBarTracking;

    public MyRecycle(Context context) {
        super(context);
    }

    public MyRecycle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecycle(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        linearLayoutManager = (LinearLayoutManager) layout;
    }

    public void setAppbartrackListner(AppBarTracking appbarListner) {
        appBarTracking = appbarListner;
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);

    }

    @Override
    public boolean fling(int velocityX, int velocityY) {

        return super.fling(velocityX, velocityY);

    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    interface AppBarTracking {
        boolean isAppBarIdle();

        boolean isAppBarExpanded();
    }
}
