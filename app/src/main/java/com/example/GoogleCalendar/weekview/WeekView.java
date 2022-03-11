package com.example.GoogleCalendar.weekview;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

import com.example.GoogleCalendar.MainActivity;
import com.example.GoogleCalendar.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ViewCompat;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;

import static com.example.GoogleCalendar.weekview.WeekViewUtil.isSameDay;
import static com.example.GoogleCalendar.weekview.WeekViewUtil.today;

/**
 * Created by Raquib-ul-Alam Kanak on 7/21/2014.
 * Website: http://alamkanak.github.io/
 * and modify some code by jignesh khunt for https://github.com/jignesh13/googlecalendar
 */
public class WeekView extends View {

    @Deprecated
    public static final int LENGTH_SHORT = 1;
    @Deprecated
    public static final int LENGTH_LONG = 2;
    private final Context mContext;
    int forward = 1080;
    private Paint shadowPaint;
    private boolean stop;

    private Paint mTimeTextPaint;
    private float mTimeTextWidth;
    private float mTimeTextHeight;
    private Paint mHeaderTextPaint;
    private float mHeaderTextHeight;
    private View shadow;
    private Paint jHeaderTextPaint;
    private Paint jtodayHeaderTextPaint;
    private float jHeaderTextHeight;
    private float mHeaderHeight;
    private GestureDetectorCompat mGestureDetector;
    private OverScroller mScroller;
    private PointF mCurrentOrigin = new PointF(0f, 0f);
    private Direction mCurrentScrollDirection = Direction.NONE;
    private Paint mHeaderBackgroundPaint;
    private float mWidthPerDay;
    private Paint mDayBackgroundPaint;
    private Paint mHourSeparatorPaint;
    private float mHeaderMarginBottom;
    private Paint mTodayBackgroundPaint;
    private Paint jheaderEventTextpaint;
    private int jheaderEventheight;
    private Paint mFutureBackgroundPaint;
    private Paint mPastBackgroundPaint;
    private Paint mFutureWeekendBackgroundPaint;
    private Paint mPastWeekendBackgroundPaint;
    private Paint mNowLinePaint;
    private Paint jNowbackPaint;
    private Paint mTodayHeaderTextPaint;
    private Paint mEventBackgroundPaint;
    private float mHeaderColumnWidth;
    private List<EventRect> mEventRects;
    private List<? extends WeekViewEvent> mPreviousPeriodEvents;
    private List<? extends WeekViewEvent> mCurrentPeriodEvents;
    private List<? extends WeekViewEvent> mNextPeriodEvents;
    private TextPaint mEventTextPaint;
    private Paint mHeaderColumnBackgroundPaint;
    private int mFetchedPeriod = -1; // the middle period the calendar has fetched.
    private boolean mRefreshEvents = false;
    private Direction mCurrentFlingDirection = Direction.NONE;
    private ScaleGestureDetector mScaleDetector;
    private boolean mIsZooming;
    private Calendar mFirstVisibleDay;
    private Calendar mLastVisibleDay;
    private boolean mShowFirstDayOfWeekFirst = false;
    private int mDefaultEventColor;
    private int mMinimumFlingVelocity = 0;
    private int mScaledTouchSlop = 0;
    // Attributes and their default values.
    private int mHourHeight = 50;
    private int mNewHourHeight = -1;
    private int mMinHourHeight = 0; //no minimum specified (will be dynamic, based on screen)
    private int mEffectiveMinHourHeight = mMinHourHeight; //compensates for the fact that you can't keep zooming out.
    private int mMaxHourHeight = 250;
    private int mColumnGap = 10;
    private int mFirstDayOfWeek = Calendar.MONDAY;
    private int mTextSize = 12;
    private int mHeaderColumnPadding = 10;
    private int mHeaderColumnTextColor = Color.BLACK;
    private int mNumberOfVisibleDays = 3;
    private int mHeaderRowPadding = 10;
    private int mHeaderRowBackgroundColor = Color.WHITE;
    private int mDayBackgroundColor = Color.rgb(245, 245, 245);
    private int mPastBackgroundColor = Color.rgb(227, 227, 227);
    private int mFutureBackgroundColor = Color.rgb(245, 245, 245);
    private int mPastWeekendBackgroundColor = 0;
    private int mFutureWeekendBackgroundColor = 0;
    private int mNowLineColor = Color.rgb(102, 102, 102);
    private int mNowLineThickness = 5;
    private int mHourSeparatorColor = Color.rgb(230, 230, 230);
    private int mTodayBackgroundColor = Color.rgb(239, 247, 254);
    private int mHourSeparatorHeight = 2;
    private int mTodayHeaderTextColor = Color.rgb(39, 137, 228);
    private int mEventTextSize = 12;
    private int mEventTextColor = Color.BLACK;
    private int mEventPadding = 8;
    private int mHeaderColumnBackgroundColor = Color.WHITE;
    private boolean mIsFirstDraw = true;
    private boolean mAreDimensionsInvalid = true;
    @Deprecated
    private int mDayNameLength = LENGTH_LONG;
    private int mOverlappingEventGap = 0;
    private int mEventMarginVertical = 0;
    private float mXScrollingSpeed = 1f;
    private Calendar mScrollToDay = null;
    private double mScrollToHour = -1;
    private int mEventCornerRadius = 0;
    private boolean mShowDistinctWeekendColor = false;
    private boolean mShowNowLine = false;
    private boolean mShowDistinctPastFutureColor = false;
    private boolean mHorizontalFlingEnabled = true;
    private boolean mVerticalFlingEnabled = true;
    private int mAllDayEventHeight = 100;
    private int mScrollDuration = 150;
    private float weekx;
    // Listeners.
    private EventClickListener mEventClickListener;
    private EventLongPressListener mEventLongPressListener;
    private WeekViewLoader mWeekViewLoader;
    private EmptyViewClickListener mEmptyViewClickListener;
    private EmptyViewLongPressListener mEmptyViewLongPressListener;
    private final GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {

            weekx = mCurrentOrigin.x;
            goToNearestOrigin();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            // Check if view is zoomed.

            if (mIsZooming)
                return true;
            switch (mCurrentScrollDirection) {
                case NONE: {
                    // Allow scrolling only in one direction.
                    if (Math.abs(distanceX) > Math.abs(distanceY)) {
                        if (distanceX > 0) {
                            mCurrentScrollDirection = Direction.LEFT;
                        } else {
                            mCurrentScrollDirection = Direction.RIGHT;
                        }
                    } else {
                        mCurrentScrollDirection = Direction.VERTICAL;
                    }
                    break;
                }
                case LEFT: {
                    // Change direction if there was enough change.
                    if (Math.abs(distanceX) > Math.abs(distanceY) && (distanceX < -mScaledTouchSlop)) {
                        mCurrentScrollDirection = Direction.RIGHT;
                    }
                    break;
                }
                case RIGHT: {
                    // Change direction if there was enough change.
                    if (Math.abs(distanceX) > Math.abs(distanceY) && (distanceX > mScaledTouchSlop)) {
                        mCurrentScrollDirection = Direction.LEFT;
                    }
                    break;
                }
            }

            // Calculate the new origin after scroll.
            switch (mCurrentScrollDirection) {
                case LEFT:
                case RIGHT:
                    mCurrentOrigin.x -= distanceX * mXScrollingSpeed;
                    ViewCompat.postInvalidateOnAnimation(WeekView.this);
                    break;
                case VERTICAL:

                    mCurrentOrigin.y -= distanceY;
                    ViewCompat.postInvalidateOnAnimation(WeekView.this);
                    break;
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if (mIsZooming)
                return true;

//            if ((mCurrentFlingDirection == Direction.LEFT && !mHorizontalFlingEnabled) ||
//                    (mCurrentFlingDirection == Direction.RIGHT && !mHorizontalFlingEnabled) ||
//                    (mCurrentFlingDirection == Direction.VERTICAL && !mVerticalFlingEnabled)) {
//                return true;
//            }

            mScroller.forceFinished(true);

            mCurrentFlingDirection = mCurrentScrollDirection;


            float target = 0;
            switch (mCurrentFlingDirection) {
                case LEFT:

                    target = weekx - (mWidthPerDay * getNumberOfVisibleDays());
                    ValueAnimator va = ValueAnimator.ofFloat(mCurrentOrigin.x, target);


                    va.setDuration(70);
                    va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mCurrentOrigin.x = (float) animation.getAnimatedValue();
                            invalidate();
                        }

                    });
                    va.start();

                    break;
                case RIGHT:
                    target = weekx + (mWidthPerDay * getNumberOfVisibleDays());
                    ValueAnimator va1 = ValueAnimator.ofFloat(mCurrentOrigin.x, target);
                    va1.setDuration(70);
                    va1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mCurrentOrigin.x = (float) animation.getAnimatedValue();
                            invalidate();
                        }

                    });

                    va1.start();
                    //  forward= (int) (forward+mWidthPerDay);
                    break;

                case VERTICAL:
                    mScroller.fling((int) mCurrentOrigin.x, (int) mCurrentOrigin.y, 0, (int) velocityY, Integer.MIN_VALUE, Integer.MAX_VALUE, (int) -(mHourHeight * 24 + mHeaderHeight + mHeaderRowPadding * 3 + mHeaderMarginBottom + mTimeTextHeight / 2 - getHeight()), 0);
                    ViewCompat.postInvalidateOnAnimation(WeekView.this);
                    break;
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    invalidate();
                }
            }, 100);


            return true;
        }


        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // If the tap was on an event then trigger the callback.
            if (mEventRects != null && mEventClickListener != null) {
                List<EventRect> reversedEventRects = mEventRects;
                // Collections.reverse(reversedEventRects);
                for (EventRect event : reversedEventRects) {
                    if (event.rectF != null && e.getX() > event.rectF.left && e.getX() < event.rectF.right && e.getY() > event.rectF.top && e.getY() < event.rectF.bottom) {
                        mEventClickListener.onEventClick(event.event, event.rectF);
                        playSoundEffect(SoundEffectConstants.CLICK);
                        return super.onSingleTapConfirmed(e);
                    }
                }
            }

            // If the tap was on in an empty space, then trigger the callback.
            if (mEmptyViewClickListener != null && e.getX() > mHeaderColumnWidth && e.getY() > (mHeaderHeight + mHeaderRowPadding * 3 + mHeaderMarginBottom)) {
                Calendar selectedTime = getTimeFromPoint(e.getX(), e.getY());
                if (selectedTime != null) {
                    playSoundEffect(SoundEffectConstants.CLICK);
                    mEmptyViewClickListener.onEmptyViewClicked(selectedTime);
                }
            }

            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);

            if (mEventLongPressListener != null && mEventRects != null) {
                List<EventRect> reversedEventRects = mEventRects;
                //   Collections.reverse(reversedEventRects);
                for (EventRect event : reversedEventRects) {
                    if (event.rectF != null && e.getX() > event.rectF.left && e.getX() < event.rectF.right && e.getY() > event.rectF.top && e.getY() < event.rectF.bottom) {
                        mEventLongPressListener.onEventLongPress(event.originalEvent, event.rectF);
                        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                        return;
                    }
                }
            }

            // If the tap was on in an empty space, then trigger the callback.
            if (mEmptyViewLongPressListener != null && e.getX() > mHeaderColumnWidth && e.getY() > (mHeaderHeight + mHeaderRowPadding * 3 + mHeaderMarginBottom)) {
                Calendar selectedTime = getTimeFromPoint(e.getX(), e.getY());
                if (selectedTime != null) {
                    performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    mEmptyViewLongPressListener.onEmptyViewLongPress(selectedTime);
                }
            }
        }
    };
    private DateTimeInterpreter mDateTimeInterpreter;
    private ScrollListener mScrollListener;

    public WeekView(Context context) {
        this(context, null);
    }

    public WeekView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeekView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Hold references.
        mContext = context;

        // Get the attribute values (if any).
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WeekView, 0, 0);
        try {
            mFirstDayOfWeek = a.getInteger(R.styleable.WeekView_firstDayOfWeek, mFirstDayOfWeek);
            mHourHeight = a.getDimensionPixelSize(R.styleable.WeekView_hourHeight, mHourHeight);
            mMinHourHeight = a.getDimensionPixelSize(R.styleable.WeekView_minHourHeight, mMinHourHeight);
            mEffectiveMinHourHeight = mMinHourHeight;
            mMaxHourHeight = a.getDimensionPixelSize(R.styleable.WeekView_maxHourHeight, mMaxHourHeight);
            mTextSize = a.getDimensionPixelSize(R.styleable.WeekView_textSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize, context.getResources().getDisplayMetrics()));
            mHeaderColumnPadding = a.getDimensionPixelSize(R.styleable.WeekView_headerColumnPadding, mHeaderColumnPadding);
            mColumnGap = a.getDimensionPixelSize(R.styleable.WeekView_columnGap, mColumnGap);
            mColumnGap = a.getDimensionPixelSize(R.styleable.WeekView_columnGap, mColumnGap);
            mHeaderColumnTextColor = a.getColor(R.styleable.WeekView_headerColumnTextColor, mHeaderColumnTextColor);
            mNumberOfVisibleDays = a.getInteger(R.styleable.WeekView_noOfVisibleDays, mNumberOfVisibleDays);
            mShowFirstDayOfWeekFirst = a.getBoolean(R.styleable.WeekView_showFirstDayOfWeekFirst, mShowFirstDayOfWeekFirst);
            mHeaderRowPadding = a.getDimensionPixelSize(R.styleable.WeekView_headerRowPadding, mHeaderRowPadding);
            mHeaderRowBackgroundColor = a.getColor(R.styleable.WeekView_headerRowBackgroundColor, mHeaderRowBackgroundColor);
            mDayBackgroundColor = a.getColor(R.styleable.WeekView_dayBackgroundColor, mDayBackgroundColor);
            mFutureBackgroundColor = a.getColor(R.styleable.WeekView_futureBackgroundColor, mFutureBackgroundColor);
            mPastBackgroundColor = a.getColor(R.styleable.WeekView_pastBackgroundColor, mPastBackgroundColor);
            mFutureWeekendBackgroundColor = a.getColor(R.styleable.WeekView_futureWeekendBackgroundColor, mFutureBackgroundColor); // If not set, use the same color as in the week
            mPastWeekendBackgroundColor = a.getColor(R.styleable.WeekView_pastWeekendBackgroundColor, mPastBackgroundColor);
            mNowLineColor = a.getColor(R.styleable.WeekView_nowLineColor, mNowLineColor);
            mNowLineThickness = a.getDimensionPixelSize(R.styleable.WeekView_nowLineThickness, mNowLineThickness);
            mHourSeparatorColor = a.getColor(R.styleable.WeekView_hourSeparatorColor, mHourSeparatorColor);
            mTodayBackgroundColor = a.getColor(R.styleable.WeekView_todayBackgroundColor, mTodayBackgroundColor);
            mHourSeparatorHeight = a.getDimensionPixelSize(R.styleable.WeekView_hourSeparatorHeight, mHourSeparatorHeight);
            mTodayHeaderTextColor = a.getColor(R.styleable.WeekView_todayHeaderTextColor, mTodayHeaderTextColor);
            mEventTextSize = a.getDimensionPixelSize(R.styleable.WeekView_eventTextSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mEventTextSize, context.getResources().getDisplayMetrics()));
            mEventTextColor = a.getColor(R.styleable.WeekView_eventTextColor, mEventTextColor);
            mEventPadding = a.getDimensionPixelSize(R.styleable.WeekView_eventPadding, mEventPadding);
            mHeaderColumnBackgroundColor = a.getColor(R.styleable.WeekView_headerColumnBackground, mHeaderColumnBackgroundColor);
            mDayNameLength = a.getInteger(R.styleable.WeekView_dayNameLength, mDayNameLength);
            mOverlappingEventGap = a.getDimensionPixelSize(R.styleable.WeekView_overlappingEventGap, mOverlappingEventGap);
            mEventMarginVertical = a.getDimensionPixelSize(R.styleable.WeekView_eventMarginVertical, mEventMarginVertical);
            mXScrollingSpeed = a.getFloat(R.styleable.WeekView_xScrollingSpeed, mXScrollingSpeed);
            mEventCornerRadius = a.getDimensionPixelSize(R.styleable.WeekView_eventCornerRadius, mEventCornerRadius);
            mShowDistinctPastFutureColor = a.getBoolean(R.styleable.WeekView_showDistinctPastFutureColor, mShowDistinctPastFutureColor);
            mShowDistinctWeekendColor = a.getBoolean(R.styleable.WeekView_showDistinctWeekendColor, mShowDistinctWeekendColor);
            mShowNowLine = a.getBoolean(R.styleable.WeekView_showNowLine, mShowNowLine);
            mHorizontalFlingEnabled = a.getBoolean(R.styleable.WeekView_horizontalFlingEnabled, mHorizontalFlingEnabled);
            mVerticalFlingEnabled = a.getBoolean(R.styleable.WeekView_verticalFlingEnabled, mVerticalFlingEnabled);
            mAllDayEventHeight = a.getDimensionPixelSize(R.styleable.WeekView_allDayEventHeight, mAllDayEventHeight);
            mScrollDuration = a.getInt(R.styleable.WeekView_scrollDuration, mScrollDuration);
        } finally {
            a.recycle();
        }

        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    static public Path RoundedRect(float left, float top, float right, float bottom, float rx, float ry, boolean conformToOriginalPost) {
        Path path = new Path();
        if (rx < 0) rx = 0;
        if (ry < 0) ry = 0;
        float width = right - left;
        float height = bottom - top;
        if (rx > width / 2) rx = width / 2;
        if (ry > height / 2) ry = height / 2;
        float widthMinusCorners = (width - (2 * rx));
        float heightMinusCorners = (height - (2 * ry));

        path.moveTo(right, top + ry);
        path.arcTo(right - 2 * rx, top, right, top + 2 * ry, 0, -90, false); //top-right-corner
        path.rLineTo(-widthMinusCorners, 0);
        path.arcTo(left, top, left + 2 * rx, top + 2 * ry, 270, -90, false);//top-left corner.
        path.rLineTo(0, heightMinusCorners);
        if (conformToOriginalPost) {
            path.rLineTo(0, ry);
            path.rLineTo(width, 0);
            path.rLineTo(0, -ry);
        } else {
            path.arcTo(left, bottom - 2 * ry, left + 2 * rx, bottom, 180, -90, false); //bottom-left corner
            path.rLineTo(widthMinusCorners, 0);
            path.arcTo(right - 2 * rx, bottom - 2 * ry, right, bottom, 90, -90, false); //bottom-right corner
        }

        path.rLineTo(0, -heightMinusCorners);

        path.close();//Given close, last lineto can be removed.
        return path;
    }

    private void init() {
        // Scrolling initialization.
        mGestureDetector = new GestureDetectorCompat(mContext, mGestureListener);
        mScroller = new OverScroller(mContext, new FastOutLinearInInterpolator());
        mMinimumFlingVelocity = ViewConfiguration.get(mContext).getScaledMinimumFlingVelocity();
        mScaledTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();

        shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint.setStyle(Paint.Style.FILL);
        shadowPaint.setShadowLayer(12, 0, 0, Color.GRAY);

        // Important for certain APIs
        setLayerType(LAYER_TYPE_SOFTWARE, shadowPaint);
        // Measure settings for time column.
        mTimeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimeTextPaint.setTextAlign(Paint.Align.RIGHT);
        mTimeTextPaint.setTextSize(mTextSize);
        mTimeTextPaint.setColor(mHeaderColumnTextColor);
        Rect rect = new Rect();
        mTimeTextPaint.getTextBounds("00 PM", 0, "00 PM".length(), rect);
        mTimeTextHeight = rect.height();
        mHeaderMarginBottom = mTimeTextHeight / 2;
        initTextTimeWidth();

        // Measure settings for header row.
        mHeaderTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHeaderTextPaint.setColor(mHeaderColumnTextColor);
        mHeaderTextPaint.setTextAlign(Paint.Align.CENTER);
        mHeaderTextPaint.setTextSize(mTextSize - 1);
        mHeaderTextPaint.getTextBounds("00 PM", 0, "00 PM".length(), rect);
        mHeaderTextHeight = rect.height();

        jheaderEventTextpaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        jheaderEventTextpaint.setColor(Color.WHITE);
        jheaderEventTextpaint.setTextAlign(Paint.Align.LEFT);
        jheaderEventTextpaint.setTextSize(mTextSize);
        jheaderEventTextpaint.getTextBounds("a", 0, "a".length(), rect);
        jheaderEventheight = rect.centerY();

        jtodayHeaderTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        jtodayHeaderTextPaint.setColor(Color.WHITE);
        jtodayHeaderTextPaint.setTextAlign(Paint.Align.CENTER);
        jtodayHeaderTextPaint.setTextSize(mTextSize * 1.6f);

        jHeaderTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        jHeaderTextPaint.setColor(Color.DKGRAY);
        jHeaderTextPaint.setTextAlign(Paint.Align.CENTER);
        jHeaderTextPaint.setTextSize(mTextSize * 1.6f);
        jHeaderTextPaint.getTextBounds("00", 0, "00".length(), rect);
        jHeaderTextHeight = rect.height();

        // Prepare header background paint.
        mHeaderBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHeaderBackgroundPaint.setColor(mHeaderRowBackgroundColor);

        // Prepare day background color paint.
        mDayBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDayBackgroundPaint.setColor(mDayBackgroundColor);
        mFutureBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFutureBackgroundPaint.setColor(mFutureBackgroundColor);
        mPastBackgroundPaint = new Paint();
        mPastBackgroundPaint.setColor(mPastBackgroundColor);
        mFutureWeekendBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFutureWeekendBackgroundPaint.setColor(mFutureWeekendBackgroundColor);
        mPastWeekendBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPastWeekendBackgroundPaint.setColor(mPastWeekendBackgroundColor);

        // Prepare hour separator color paint.
        mHourSeparatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHourSeparatorPaint.setStyle(Paint.Style.STROKE);
        mHourSeparatorPaint.setStrokeWidth(mHourSeparatorHeight);
        mHourSeparatorPaint.setColor(mHourSeparatorColor);

        // Prepare the "now" line color paint
        mNowLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNowLinePaint.setStrokeWidth(mNowLineThickness);
        mNowLinePaint.setColor(mNowLineColor);

        jNowbackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        jNowbackPaint.setColor(mNowLineColor);

        // Prepare today background color paint.
        mTodayBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTodayBackgroundPaint.setColor(mTodayBackgroundColor);

        // Prepare today header text color paint.
        mTodayHeaderTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTodayHeaderTextPaint.setTextAlign(Paint.Align.CENTER);
        mTodayHeaderTextPaint.setTextSize(mTextSize);
        mTodayHeaderTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTodayHeaderTextPaint.setColor(mTodayHeaderTextColor);

        // Prepare event background color.
        mEventBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mEventBackgroundPaint.setColor(Color.rgb(174, 208, 238));

        // Prepare header column background color.
        mHeaderColumnBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHeaderColumnBackgroundPaint.setColor(mHeaderColumnBackgroundColor);

        // Prepare event text size and color.
        mEventTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        mEventTextPaint.setStyle(Paint.Style.FILL);
        mEventTextPaint.setColor(mEventTextColor);
        mEventTextPaint.setTextSize(mEventTextSize);

        // Set default event color.
        mDefaultEventColor = Color.parseColor("#9fc6e7");

        mScaleDetector = new ScaleGestureDetector(mContext, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                mIsZooming = false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                mIsZooming = true;
                goToNearestOrigin();
                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                mNewHourHeight = Math.round(mHourHeight * detector.getScaleFactor());
                invalidate();
                return true;
            }
        });
    }

    // fix rotation changes
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mAreDimensionsInvalid = true;
    }

    /**
     * Initialize time column width. Calculate value with all possible hours (supposed widest text).
     */
    private void initTextTimeWidth() {
        mTimeTextWidth = 0;
        for (int i = 0; i < 24; i++) {
            // Measure time string and get max width.
            String time = getDateTimeInterpreter().interpretTime(i);
            if (time == null)
                throw new IllegalStateException("A DateTimeInterpreter must not return null time");
            mTimeTextWidth = Math.max(mTimeTextWidth, mTimeTextPaint.measureText(time));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the header row.
        drawHeaderRowAndEvents(canvas);

        // Draw the time column and all the axes/separators.
        if (mNumberOfVisibleDays != 1) drawTimeColumnAndAxes(canvas);

    }

    public void calculateHeaderHeight() {
        //Make sure the header is the right size (depends on AllDay events)
        boolean containsAllDayEvent = false;
        int noofevent = 1;
        if (mEventRects != null && mEventRects.size() > 0) {
            for (int dayNumber = 0;
                 dayNumber < mNumberOfVisibleDays;
                 dayNumber++) {
                Calendar day = (Calendar) getFirstVisibleDay().clone();
                day.add(Calendar.DATE, dayNumber);
                for (int i = 0; i < mEventRects.size(); i++) {
                    if (isSameDay(mEventRects.get(i).event.getStartTime(), day) && mEventRects.get(i).event.isAllDay()) {
                        if (mEventRects.get(i).noofevent > noofevent)
                            noofevent = mEventRects.get(i).noofevent;
                        containsAllDayEvent = true;
                        break;
                    }
                }
//                if(containsAllDayEvent){
//                    break;
//                }
            }
        }
        //((noofevent-1)*5) where is 5 padding between event
        if (mNumberOfVisibleDays == 1) {
            if (containsAllDayEvent) {
                float without = mHeaderTextHeight + jHeaderTextHeight + mHeaderMarginBottom + 70 + mHeaderRowPadding / 3.0f;
                float with = (mAllDayEventHeight * noofevent) + ((noofevent - 1) * 5) + mHeaderMarginBottom;
                mHeaderHeight = Math.max(without, with);

                shadow.setY(mHeaderHeight - 70 + mHeaderRowPadding * 3);

            } else {
                float without = mHeaderTextHeight + jHeaderTextHeight + mHeaderMarginBottom + 70 + mHeaderRowPadding / 3.0f;

                mHeaderHeight = without;
                shadow.setY(mHeaderHeight - 70 + mHeaderRowPadding * 3);
            }


        } else {
            if (containsAllDayEvent) {
                mHeaderHeight = mHeaderTextHeight + jHeaderTextHeight + ((mAllDayEventHeight * noofevent) + ((noofevent - 1) * 5) + mHeaderMarginBottom) + 70;
                shadow.setY(mHeaderHeight - 70 + mHeaderRowPadding * 3);
            } else {
                mHeaderHeight = mHeaderTextHeight + jHeaderTextHeight + mHeaderMarginBottom + 70;
                shadow.setY(mHeaderHeight - 70 + mHeaderRowPadding * 3);
            }
        }


    }

    private void canvasclipRect(Canvas mCanvas, float left, float top, float right, float bottom, Region.Op op) {
        if (op == Region.Op.REPLACE) {
            mCanvas.restore();
            mCanvas.save();
            mCanvas.clipRect(left, top, right, bottom);
            return;
        }
    }

    private void drawTimeColumnAndAxes(Canvas canvas) {
        // Draw the background color for the header column.
        canvas.drawRect(0, mHeaderHeight + mHeaderRowPadding * 3 - 70, mHeaderColumnWidth, getHeight(), mHeaderColumnBackgroundPaint);

        // Clip to paint in left column only.
        canvasclipRect(canvas, 0, mHeaderHeight + mHeaderRowPadding * 3 - 70, mHeaderColumnWidth, getHeight(), Region.Op.REPLACE);

        for (int i = 0; i < 24; i++) {
            float top = mHeaderHeight + mHeaderRowPadding * 3 + mCurrentOrigin.y + mHourHeight * i + mHeaderMarginBottom;

            // Draw the text if its y position is not outside of the visible area. The pivot point of the text is the point at the bottom-right corner.
            String time = getDateTimeInterpreter().interpretTime(i);
            if (time == null)
                throw new IllegalStateException("A DateTimeInterpreter must not return null time");
            if (top < getHeight())
                canvas.drawText(time, mTimeTextWidth + mHeaderColumnPadding, top + mTimeTextHeight, mTimeTextPaint);
        }

    }

    private void drawTimeColumnAndAxes1day(Canvas canvas, float startx) {
        // Draw the background color for the header column.
        // canvas.drawRect(0, mHeaderHeight + mHeaderRowPadding * 3-70, getWidth(), getHeight(), mHeaderColumnBackgroundPaint);

        // Clip to paint in left column only.

        canvasclipRect(canvas, 0, mHeaderHeight + mHeaderRowPadding * 3 - 70, getWidth(), getHeight(), Region.Op.REPLACE);

        for (int i = 0; i < 24; i++) {
            float top = mHeaderHeight + mHeaderRowPadding * 3 + mCurrentOrigin.y + mHourHeight * i + mHeaderMarginBottom;

            // Draw the text if its y position is not outside of the visible area. The pivot point of the text is the point at the bottom-right corner.
            String time = getDateTimeInterpreter().interpretTime(i);
            if (time == null)
                throw new IllegalStateException("A DateTimeInterpreter must not return null time");
            if (top < getHeight())
                canvas.drawText(time, startx + mTimeTextWidth + mHeaderColumnPadding, top + mTimeTextHeight, mTimeTextPaint);
        }
        canvasclipRect(canvas, 0, 0, getWidth(), getHeight(), Region.Op.REPLACE);

    }

    private void drawHeaderRowAndEvents(Canvas canvas) {
        // Calculate the available width for each day.
        mHeaderColumnWidth = mTimeTextWidth + mHeaderColumnPadding * 2;
        mWidthPerDay = getWidth() - mHeaderColumnWidth - mColumnGap * (mNumberOfVisibleDays - 1);
        if (mNumberOfVisibleDays == 1) mWidthPerDay = getWidth();//mWidthPerDay/mNumberOfVisibleDays
        else mWidthPerDay = mWidthPerDay / mNumberOfVisibleDays;

        calculateHeaderHeight(); //Make sure the header is the right size (depends on AllDay events)

        Calendar today = today();

        if (mAreDimensionsInvalid) {
            mEffectiveMinHourHeight = Math.max(mMinHourHeight, (int) ((getHeight() - mHeaderHeight - mHeaderRowPadding * 3 - mHeaderMarginBottom) / 24));

            mAreDimensionsInvalid = false;
            if (mScrollToDay != null)
                goToDate(mScrollToDay);

            mAreDimensionsInvalid = false;
            if (mScrollToHour >= 0)
                goToHour(mScrollToHour);

            mScrollToDay = null;
            mScrollToHour = -1;
            mAreDimensionsInvalid = false;
        }
        if (mIsFirstDraw) {
            mIsFirstDraw = false;

            // If the week view is being drawn for the first time, then consider the first day of the week.
            if (mNumberOfVisibleDays >= 7 && today.get(Calendar.DAY_OF_WEEK) != mFirstDayOfWeek && mShowFirstDayOfWeekFirst) {
                int difference = (today.get(Calendar.DAY_OF_WEEK) - mFirstDayOfWeek);
                mCurrentOrigin.x += (mWidthPerDay + mColumnGap) * difference;
            }
        }

        // Calculate the new height due to the zooming.
        if (mNewHourHeight > 0) {
            if (mNewHourHeight < mEffectiveMinHourHeight)
                mNewHourHeight = mEffectiveMinHourHeight;
            else if (mNewHourHeight > mMaxHourHeight)
                mNewHourHeight = mMaxHourHeight;

            mCurrentOrigin.y = (mCurrentOrigin.y / mHourHeight) * mNewHourHeight;
            mHourHeight = mNewHourHeight;
            mNewHourHeight = -1;
        }

        // If the new mCurrentOrigin.y is invalid, make it valid.
        if (mCurrentOrigin.y < getHeight() - mHourHeight * 24 - mHeaderHeight - mHeaderRowPadding * 3 - mHeaderMarginBottom - mTimeTextHeight / 2)
            mCurrentOrigin.y = getHeight() - mHourHeight * 24 - mHeaderHeight - mHeaderRowPadding * 3 - mHeaderMarginBottom - mTimeTextHeight / 2;

        // Don't put an "else if" because it will trigger a glitch when completely zoomed out and
        // scrolling vertically.
        if (mCurrentOrigin.y > 0) {
            mCurrentOrigin.y = 0;
        }

        // Consider scroll offset.
        int leftDaysWithGaps = (int) -(Math.ceil(mCurrentOrigin.x / (mWidthPerDay + mColumnGap)));
        float startFromPixel = mCurrentOrigin.x + (mWidthPerDay + mColumnGap) * leftDaysWithGaps +
                mHeaderColumnWidth;
        float startPixel = startFromPixel;

        // Prepare to iterate for each day.
        Calendar day = (Calendar) today.clone();
        day.add(Calendar.HOUR, 6);

        // Prepare to iterate for each hour to draw the hour lines.
        int lineCount = (int) ((getHeight() - mHeaderHeight - mHeaderRowPadding * 3 -
                mHeaderMarginBottom) / mHourHeight) + 1;
        lineCount = (lineCount) * (mNumberOfVisibleDays + 1);
        float[] hourLines = new float[lineCount * 4];

        // Clear the cache for event rectangles.
        if (mEventRects != null) {
            for (EventRect eventRect : mEventRects) {
                eventRect.rectF = null;
            }
        }

        // Clip to paint events only.
        float stary = 0;
        canvasclipRect(canvas, 0, stary, getWidth(), getHeight(), Region.Op.REPLACE);

        // Iterate through each day.
        Calendar oldFirstVisibleDay = mFirstVisibleDay;
        mFirstVisibleDay = (Calendar) today.clone();
        mFirstVisibleDay.add(Calendar.DATE, -(Math.round(mCurrentOrigin.x / (mWidthPerDay + mColumnGap))));
        if (!mFirstVisibleDay.equals(oldFirstVisibleDay) && mScrollListener != null) {
            mScrollListener.onFirstVisibleDayChanged(mFirstVisibleDay, oldFirstVisibleDay);
        }
        for (int dayNumber = leftDaysWithGaps + 1;
             dayNumber <= leftDaysWithGaps + mNumberOfVisibleDays + 1;
             dayNumber++) {

            // Check if the day is today.
            day = (Calendar) today.clone();
            mLastVisibleDay = (Calendar) day.clone();
            day.add(Calendar.DATE, dayNumber - 1);
            mLastVisibleDay.add(Calendar.DATE, dayNumber - 2);
            boolean sameDay = isSameDay(day, today);

            // Get more events if necessary. We want to store the events 3 months beforehand. Get
            // events only when it is the first iteration of the loop.
            if (mEventRects == null || mRefreshEvents ||
                    (dayNumber == leftDaysWithGaps + 1 && mFetchedPeriod != (int) mWeekViewLoader.toWeekViewPeriodIndex(day) &&
                            Math.abs(mFetchedPeriod - mWeekViewLoader.toWeekViewPeriodIndex(day)) > 0.5)) {
                getMoreEvents(day);
                mRefreshEvents = false;
                calculateHeaderHeight();
            }

            // Draw background color for each day.
            float start = (startPixel < mHeaderColumnWidth ? mHeaderColumnWidth : startPixel);
//            if (mWidthPerDay + startPixel - start > 0){
//                if (mShowDistinctPastFutureColor){
//                    boolean isWeekend = day.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || day.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
//                    Paint pastPaint = isWeekend && mShowDistinctWeekendColor ? mPastWeekendBackgroundPaint : mPastBackgroundPaint;
//                    Paint futurePaint = isWeekend && mShowDistinctWeekendColor ? mFutureWeekendBackgroundPaint : mFutureBackgroundPaint;
//                    float startY = mHeaderHeight + mHeaderRowPadding * 3 + mTimeTextHeight/2 + mHeaderMarginBottom + mCurrentOrigin.y;
//                    startY=stary;
//                    if (sameDay){
//                        Calendar now = Calendar.getInstance();
//                        float beforeNow = (now.get(Calendar.HOUR_OF_DAY) + now.get(Calendar.MINUTE)/60.0f) * mHourHeight;
//                        canvas.drawRect(start, startY, startPixel + mWidthPerDay, startY+beforeNow, pastPaint);
//                        canvas.drawRect(start, startY+beforeNow, startPixel + mWidthPerDay, getHeight(), futurePaint);
//                    }
//                    else if (day.before(today)) {
//                        canvas.drawRect(start, startY, startPixel + mWidthPerDay, getHeight(), pastPaint);
//                    }
//                    else {
//                        canvas.drawRect(start, startY, startPixel + mWidthPerDay, getHeight(), futurePaint);
//                    }
//
//                    canvas.drawLine(start,startY,start,getHeight(),mHourSeparatorPaint);
//                }
//                else {
//                    float ca=0 ;
//                   // canvas.drawRect(start, ca, startPixel + mWidthPerDay, getHeight(), sameDay ? mTodayBackgroundPaint : mDayBackgroundPaint);
//                    canvas.drawLine(startPixel,ca,startPixel,getHeight(),mHourSeparatorPaint);
//                    canvas.drawLine(startPixel+mWidthPerDay,ca,startPixel+mWidthPerDay,getHeight(),mHourSeparatorPaint);
//
//
//                }
//            }

            canvasclipRect(canvas, 0, stary, getWidth(), getHeight(), Region.Op.REPLACE);


            // Prepare the separator lines for hours.
            int i = 0;
            for (int hourNumber = 0; hourNumber < 24; hourNumber++) {
                float top = mHeaderHeight + mHeaderRowPadding * 3 + mCurrentOrigin.y + mHourHeight * hourNumber + mTimeTextHeight / 2 + mHeaderMarginBottom;
                if (top > mHeaderHeight - 70 + mHeaderRowPadding * 3 && top < getHeight() && startPixel + mWidthPerDay - start > 0) {
                    if (mNumberOfVisibleDays != 1) {
                        if (dayNumber == leftDaysWithGaps + 1) hourLines[i * 4] = start - 22;
                        else hourLines[i * 4] = start;
                        hourLines[i * 4 + 1] = top;
                        hourLines[i * 4 + 2] = startPixel + mWidthPerDay;

                        hourLines[i * 4 + 3] = top;
                        i++;
                    } else {
                        hourLines[i * 4] = startPixel - 22;
                        hourLines[i * 4 + 1] = top;
                        hourLines[i * 4 + 2] = startPixel + mWidthPerDay - mHeaderColumnWidth;
                        hourLines[i * 4 + 3] = top;
                        i++;
                    }


                }
            }

            // Draw the lines for hours.
            canvas.drawLines(hourLines, mHourSeparatorPaint);
            if (mNumberOfVisibleDays != 1)
                canvasclipRect(canvas, mHeaderColumnWidth, mHeaderHeight + (mHeaderRowPadding * 3) - 70, getWidth(), getHeight(), Region.Op.REPLACE);


            // Draw the events.
            if (mNumberOfVisibleDays != 1) drawEvents(day, startPixel, canvas);
            else drawEvents(day, startPixel, canvas);
            // Draw the line at the current time.


            // In the next iteration, start from the next day.
            startPixel += mWidthPerDay + mColumnGap;
        }

        // Hide everything in the first cell (top left corner).
//        canvas.clipRect(0, 0, mTimeTextWidth + mHeaderColumnPadding * 2, mHeaderHeight + mHeaderRowPadding * 3, Region.Op.INTERSECT);
//        canvas.drawRect(0, 0, mTimeTextWidth + mHeaderColumnPadding * 2, mHeaderHeight + mHeaderRowPadding * 3, mHeaderBackgroundPaint);
//       float bottom = mHeaderHeight + mHeaderRowPadding * 3;
        // Clip to paint header row only.


        if (mNumberOfVisibleDays != 1)
            canvasclipRect(canvas, mHeaderColumnWidth, 0, getWidth(), getHeight(), Region.Op.REPLACE);
        // Draw the header background.
        canvas.drawRect(0, 0, getWidth(), mHeaderHeight - 70 + mHeaderRowPadding * 3, mHeaderBackgroundPaint);
        mHourSeparatorPaint.setStrokeWidth(mHourSeparatorHeight * 2);
        if (mNumberOfVisibleDays != 1)
            canvas.drawLine(mHeaderColumnWidth, mHeaderHeight + mHeaderRowPadding * 3 - 105, mHeaderColumnWidth, getHeight(), mHourSeparatorPaint);
        mHourSeparatorPaint.setStrokeWidth(mHourSeparatorHeight);


        startPixel = startFromPixel;
        float begin = startPixel;
        Calendar start = (Calendar) today.clone();

        for (int dayNumber = leftDaysWithGaps + 1; dayNumber <= leftDaysWithGaps + mNumberOfVisibleDays + 1; dayNumber++) {
            // Check if the day is today.

            day = (Calendar) today.clone();
            day.add(Calendar.DATE, dayNumber - 1);
            boolean sameDay = isSameDay(day, today);
            int daycompare = day.compareTo(today);
            if (daycompare < 0) {
                jHeaderTextPaint.setColor(Color.parseColor("#606368"));
            } else {
                jHeaderTextPaint.setColor(Color.BLACK);

            }
            if (dayNumber == leftDaysWithGaps + 1) start = day;

            // Draw the day labels.
            String dayLabel1 = getDateTimeInterpreter().interpretDate(day);
            String dayLabel = getDateTimeInterpreter().interpretday(day);
            if (dayLabel == null)
                throw new IllegalStateException("A DateTimeInterpreter must not return null date");


            float x = startPixel + mWidthPerDay / 2;
            float xx = startPixel - mHeaderColumnWidth / 2.0f;
            float y = (mHeaderTextHeight + mHeaderRowPadding * 1.76f + jHeaderTextHeight) - jHeaderTextHeight / 2.0f;

            int size = getResources().getDimensionPixelSize(R.dimen.todaysize);
            if (mNumberOfVisibleDays != 1)
                canvas.drawLine(startPixel, mHeaderHeight + mHeaderRowPadding * 3 - 105, startPixel, getHeight(), mHourSeparatorPaint);
            else {
                canvas.drawLine(startPixel, mHeaderRowPadding / 3.0f, startPixel, getHeight(), mHourSeparatorPaint);

            }
            if (mNumberOfVisibleDays == 1) {
                if (sameDay)
                    canvas.drawRoundRect(xx - size, y - size, xx + size, y + size, size, size, mTodayBackgroundPaint);

                canvas.drawText(dayLabel, startPixel - mHeaderColumnWidth / 2.0f, mHeaderTextHeight + mHeaderRowPadding / 3.0f, sameDay ? mTodayHeaderTextPaint : mHeaderTextPaint);
                canvas.drawText(dayLabel1, startPixel - mHeaderColumnWidth / 2.0f, mHeaderTextHeight + mHeaderRowPadding * 1.76f + jHeaderTextHeight, sameDay ? jtodayHeaderTextPaint : jHeaderTextPaint);
                drawAllDayEvents(day, startPixel, canvas, dayNumber, false);


            } else {
                if (sameDay)
                    canvas.drawRoundRect(x - size, y - size, x + size, y + size, size, size, mTodayBackgroundPaint);
                canvas.drawText(dayLabel, startPixel + mWidthPerDay / 2, mHeaderTextHeight + mHeaderRowPadding / 3.0f, sameDay ? mTodayHeaderTextPaint : mHeaderTextPaint);
                canvas.drawText(dayLabel1, startPixel + mWidthPerDay / 2, mHeaderTextHeight + mHeaderRowPadding * 1.76f + jHeaderTextHeight, sameDay ? jtodayHeaderTextPaint : jHeaderTextPaint);

                drawAllDayEvents(day, startPixel, canvas, dayNumber, false);


            }

            if (mShowNowLine && sameDay) {
                float startY = mHeaderHeight + mHeaderRowPadding * 3 + mTimeTextHeight / 2 + mHeaderMarginBottom + mCurrentOrigin.y;
                Calendar now = Calendar.getInstance();
                float beforeNow = (now.get(Calendar.HOUR_OF_DAY) + now.get(Calendar.MINUTE) / 60.0f) * mHourHeight;

                float startat = startPixel < mHeaderColumnWidth ? mHeaderColumnWidth : startPixel;
                float wid = mWidthPerDay;
                float per = 20 * (1.0f - (startat - startPixel) / wid);
                if (mNumberOfVisibleDays != 1) {
                    if (mNumberOfVisibleDays != 1)
                        canvasclipRect(canvas, 0, mHeaderHeight + mHeaderRowPadding * 3 - 70, getWidth(), getHeight(), Region.Op.REPLACE);
                    canvas.drawRoundRect(startat - per, startY + beforeNow - per, startat + per, startY + beforeNow + per, per, per, jNowbackPaint);
                    canvas.drawLine(startat, startY + beforeNow, startPixel + wid, startY + beforeNow, mNowLinePaint);

                    canvasclipRect(canvas, mHeaderColumnWidth, 0, getWidth(), getHeight(), Region.Op.REPLACE);

                } else {

                    canvasclipRect(canvas, 0, mHeaderHeight + mHeaderRowPadding * 3 - 70, getWidth(), getHeight(), Region.Op.REPLACE);

                    canvas.drawRoundRect(startPixel - 20, startY + beforeNow - 20, startPixel + 20, startY + beforeNow + 20, 20, 20, jNowbackPaint);
                    canvas.drawLine(startPixel, startY + beforeNow, startPixel + wid - mHeaderColumnWidth, startY + beforeNow, mNowLinePaint);
                    canvasclipRect(canvas, 0, 0, getWidth(), getHeight(), Region.Op.REPLACE);

                }

            }
            if (mNumberOfVisibleDays == 1)
                drawTimeColumnAndAxes1day(canvas, startPixel - mHeaderColumnWidth);

            startPixel += mWidthPerDay + mColumnGap;

        }

//        jheaderEventTextpaint.setColor(Color.BLACK);
//        Calendar jday = (Calendar) today.clone();
//        int df = (int) (mCurrentOrigin.x/mWidthPerDay);
//        jday.add(Calendar.DATE, -df);
//        String tit="test";
//        if (mEventRects != null && mEventRects.size() > 0) {
//
//            Log.e("size",mEventRects.size()+"");
//            for (int i = 0; i < mEventRects.size(); i++) {
//                if (isSameDay(mEventRects.get(i).event.getStartTime(), jday) && mEventRects.get(i).event.isAllDay()&&mEventRects.get(i).event.isIsmoreday()) {
//                    tit=mEventRects.get(i).event.getName();
//                    canvas.drawText(tit, mHeaderColumnWidth, mEventRects.get(i).rectF.centerY() - jheaderEventheight, jheaderEventTextpaint);
//                }
//            }
//        }


    }

    /**
     * Get the time and date where the user clicked on.
     *
     * @param x The x position of the touch event.
     * @param y The y position of the touch event.
     * @return The time and date at the clicked position.
     */
    private Calendar getTimeFromPoint(float x, float y) {
        int leftDaysWithGaps = (int) -(Math.ceil(mCurrentOrigin.x / (mWidthPerDay + mColumnGap)));
        float startPixel = mCurrentOrigin.x + (mWidthPerDay + mColumnGap) * leftDaysWithGaps +
                mHeaderColumnWidth;
        for (int dayNumber = leftDaysWithGaps + 1;
             dayNumber <= leftDaysWithGaps + mNumberOfVisibleDays + 1;
             dayNumber++) {
            float start = (startPixel < mHeaderColumnWidth ? mHeaderColumnWidth : startPixel);
            if (mWidthPerDay + startPixel - start > 0 && x > start && x < startPixel + mWidthPerDay) {
                Calendar day = today();
                day.add(Calendar.DATE, dayNumber - 1);
                float pixelsFromZero = y - mCurrentOrigin.y - mHeaderHeight
                        - mHeaderRowPadding * 3 - mTimeTextHeight / 2 - mHeaderMarginBottom;
                int hour = (int) (pixelsFromZero / mHourHeight);
                int minute = (int) (60 * (pixelsFromZero - hour * mHourHeight) / mHourHeight);
                day.add(Calendar.HOUR, hour);
                day.set(Calendar.MINUTE, minute);
                return day;
            }
            startPixel += mWidthPerDay + mColumnGap;
        }
        return null;
    }

    /**
     * Draw all the events of a particular day.
     *
     * @param date           The day.
     * @param startFromPixel The left position of the day area. The events will never go any left from this value.
     * @param canvas         The canvas to draw upon.
     */
    private void drawEvents(Calendar date, float startFromPixel, Canvas canvas) {
        if (mEventRects != null && mEventRects.size() > 0) {
            for (int i = 0; i < mEventRects.size(); i++) {
                if (isSameDay(mEventRects.get(i).event.getStartTime(), date) && !mEventRects.get(i).event.isAllDay()) {

                    // Calculate top.
                    float top = mHourHeight * 24 * mEventRects.get(i).top / 1440 + mCurrentOrigin.y + mHeaderHeight + mHeaderRowPadding * 3 + mHeaderMarginBottom + mTimeTextHeight / 2 + mEventMarginVertical;

                    // Calculate bottom.
                    float bottom = mEventRects.get(i).bottom;
                    bottom = mHourHeight * 24 * bottom / 1440 + mCurrentOrigin.y + mHeaderHeight + mHeaderRowPadding * 3 + mHeaderMarginBottom + mTimeTextHeight / 2 - mEventMarginVertical;

                    // Calculate left and right.
                    float left = startFromPixel + mEventRects.get(i).left * (mWidthPerDay - (mNumberOfVisibleDays == 1 ? mHeaderColumnWidth : 0));
                    if (left < startFromPixel)
                        left += mOverlappingEventGap;
                    float right = left + mEventRects.get(i).width * (mWidthPerDay - (mNumberOfVisibleDays == 1 ? mHeaderColumnWidth : 0));
                    if (right < startFromPixel + (mWidthPerDay - (mNumberOfVisibleDays == 1 ? mHeaderColumnWidth : 0)))
                        right -= mOverlappingEventGap;

                    // Draw the event and the event name on top of it.
                    if (left < right &&
                            left < getWidth() &&
                            top < getHeight() &&
                            right > mHeaderColumnWidth &&
                            bottom > mHeaderHeight + mHeaderRowPadding * 3 + mTimeTextHeight / 2 + mHeaderMarginBottom
                    ) {
                        mEventRects.get(i).rectF = new RectF(left, top, right, bottom);
                        mEventBackgroundPaint.setColor(mEventRects.get(i).event.getColor() == 0 ? mDefaultEventColor : mEventRects.get(i).event.getColor());
                        canvas.drawRoundRect(mEventRects.get(i).rectF, mEventCornerRadius, mEventCornerRadius, mEventBackgroundPaint);
                        drawEventTitle(mEventRects.get(i).event, mEventRects.get(i).rectF, canvas, top, left);
                    } else
                        mEventRects.get(i).rectF = null;
                }
            }
        }
    }

    /**
     * Draw all the Allday-events of a particular day.
     *
     * @param date           The day.
     * @param startFromPixel The left position of the day area. The events will never go any left from this value.
     * @param canvas         The canvas to draw upon.
     */
    private void drawAllDayEvents(Calendar date, float startFromPixel, Canvas canvas, int daynumber, boolean check) {

        if (mEventRects != null && mEventRects.size() > 0) {


            for (int i = 0; i < mEventRects.size(); i++) {

                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");


                if (isSameDay(mEventRects.get(i).event.getStartTime(), date) && mEventRects.get(i).event.isAllDay()) {
                    // Calculate top.
                    float top = 0;
                    // Calculate bottom.
                    if (mNumberOfVisibleDays == 1) {

                        top = mHeaderRowPadding / 3.0f + mEventMarginVertical + mEventRects.get(i).top;
                    } else {
                        top = (3 * mHeaderRowPadding) + mHeaderTextHeight + jHeaderTextHeight + mEventMarginVertical + mEventRects.get(i).top;

                    }
                    float bottom = top + mEventRects.get(i).bottom;

                    // Calculate left and right.

                    float left = startFromPixel;


                    if (left < startFromPixel)
                        left += mOverlappingEventGap;


                    float right = left + (mWidthPerDay - (mNumberOfVisibleDays == 1 ? mHeaderColumnWidth : 0)) - 10;
                    if (right < startFromPixel + (mWidthPerDay - (mNumberOfVisibleDays == 1 ? mHeaderColumnWidth : 0)))
                        right -= mOverlappingEventGap;

                    // Draw the event and the event name on top of it.

                    if (left < right &&
                            left < getWidth() &&
                            top < getHeight() &&
                            right > mHeaderColumnWidth &&
                            bottom > 0
                    ) {
                        mEventRects.get(i).rectF = new RectF(left, top, right, bottom);


                        boolean mycheck = getNumberOfVisibleDays() != 1 && mEventRects.get(i).event.isIsmoreday();
                        mEventBackgroundPaint.setColor(mEventRects.get(i).event.getColor() == 0 ? mDefaultEventColor : mEventRects.get(i).event.getColor());

                        if (mycheck) {

                            if (true) {

                                float startat = startFromPixel < mHeaderColumnWidth ? mHeaderColumnWidth : startFromPixel;
                                float wid = mWidthPerDay;
                                mEventRects.get(i).rectF.left = startat;
                                if (mEventRects.get(i).event.getDaytype() != 1 && startat > 200) {
                                    mEventRects.get(i).rectF.left = startat - mEventCornerRadius * 2;

                                }
                                mEventRects.get(i).rectF.right = startFromPixel + (wid);
                                RectF fd = mEventRects.get(i).rectF;
                                if (mEventRects.get(i).event.getDaytype() == mEventRects.get(i).event.getNoofday()) {
                                    fd.right = fd.right - 10;
                                }
                                canvas.drawRoundRect(fd, mEventCornerRadius, mEventCornerRadius, mEventBackgroundPaint);

                                if (startFromPixel < mHeaderColumnWidth + 5 || mEventRects.get(i).event.getDaytype() == 1) {


                                    canvas.drawText(mEventRects.get(i).event.getName(), startat, mEventRects.get(i).rectF.centerY() - jheaderEventheight, jheaderEventTextpaint);

                                }


                            }


                        } else {

                            canvas.drawRoundRect(mEventRects.get(i).rectF, mEventCornerRadius, mEventCornerRadius, mEventBackgroundPaint);
                            canvas.drawText(mEventRects.get(i).event.getName(), mEventRects.get(i).rectF.left + 12, mEventRects.get(i).rectF.centerY() - jheaderEventheight, jheaderEventTextpaint);

                        }
                    } else
                        mEventRects.get(i).rectF = null;
                }
            }
        }

    }

    /**
     * Draw the name of the event on top of the event rectangle.
     *
     * @param event        The event of which the title (and location) should be drawn.
     * @param rect         The rectangle on which the text is to be drawn.
     * @param canvas       The canvas to draw upon.
     * @param originalTop  The original top position of the rectangle. The rectangle may have some of its portion outside of the visible area.
     * @param originalLeft The original left position of the rectangle. The rectangle may have some of its portion outside of the visible area.
     */
    private void drawEventTitle(WeekViewEvent event, RectF rect, Canvas canvas, float originalTop, float originalLeft) {
        if (rect.right - rect.left - mEventPadding * 2 < 0) return;
        if (rect.bottom - rect.top - mEventPadding * 2 < 0) return;

        // Prepare the name of the event.
        SpannableStringBuilder bob = new SpannableStringBuilder();
        if (event.getName() != null) {
            bob.append(event.getName());
            bob.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, bob.length(), 0);
            bob.append(' ');
        }

        // Prepare the location of the event.
        if (event.getLocation() != null) {
            bob.append(event.getLocation());
        }

        int availableHeight = (int) (rect.bottom - originalTop - mEventPadding * 2);
        int availableWidth = (int) (rect.right - originalLeft - mEventPadding * 2);

        // Get text dimensions.
        StaticLayout textLayout = new StaticLayout(bob, mEventTextPaint, availableWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        int lineHeight = textLayout.getHeight() / textLayout.getLineCount();

        if (availableHeight >= lineHeight) {
            // Calculate available number of line counts.
            int availableLineCount = availableHeight / lineHeight;
            do {
                // Ellipsize text to fit into event rect.
                textLayout = new StaticLayout(TextUtils.ellipsize(bob, mEventTextPaint, availableLineCount * availableWidth, TextUtils.TruncateAt.END), mEventTextPaint, (int) (rect.right - originalLeft - mEventPadding * 2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

                // Reduce line count.
                availableLineCount--;

                // Repeat until text is short enough.
            } while (textLayout.getHeight() > availableHeight);

            // Draw text.
            canvas.save();
            canvas.translate(originalLeft + mEventPadding, originalTop + mEventPadding);
            textLayout.draw(canvas);
            canvas.restore();
        }
    }

    /**
     * Gets more events of one/more month(s) if necessary. This method is called when the user is
     * scrolling the week view. The week view stores the events of three months: the visible month,
     * the previous month, the next month.
     *
     * @param day The day where the user is currently is.
     */
    private void getMoreEvents(Calendar day) {

        // Get more events if the month is changed.
        if (mEventRects == null)
            mEventRects = new ArrayList<EventRect>();
        if (mWeekViewLoader == null && !isInEditMode())
            throw new IllegalStateException("You must provide a MonthChangeListener");

        // If a refresh was requested then reset some variables.
        if (mRefreshEvents) {
            mEventRects.clear();
            mPreviousPeriodEvents = null;
            mCurrentPeriodEvents = null;
            mNextPeriodEvents = null;
            mFetchedPeriod = -1;
        }

        if (mWeekViewLoader != null) {
            int periodToFetch = (int) mWeekViewLoader.toWeekViewPeriodIndex(day);
            if (!isInEditMode() && (mFetchedPeriod < 0 || mFetchedPeriod != periodToFetch || mRefreshEvents)) {
                List<? extends WeekViewEvent> previousPeriodEvents = null;
                List<? extends WeekViewEvent> currentPeriodEvents = null;
                List<? extends WeekViewEvent> nextPeriodEvents = null;

                if (mPreviousPeriodEvents != null && mCurrentPeriodEvents != null && mNextPeriodEvents != null) {
                    if (periodToFetch == mFetchedPeriod - 1) {
                        currentPeriodEvents = mPreviousPeriodEvents;
                        nextPeriodEvents = mCurrentPeriodEvents;
                    } else if (periodToFetch == mFetchedPeriod) {
                        previousPeriodEvents = mPreviousPeriodEvents;
                        currentPeriodEvents = mCurrentPeriodEvents;
                        nextPeriodEvents = mNextPeriodEvents;
                    } else if (periodToFetch == mFetchedPeriod + 1) {
                        previousPeriodEvents = mCurrentPeriodEvents;
                        currentPeriodEvents = mNextPeriodEvents;
                    }
                }
                if (currentPeriodEvents == null)
                    currentPeriodEvents = mWeekViewLoader.onLoad(periodToFetch);
                if (previousPeriodEvents == null)
                    previousPeriodEvents = mWeekViewLoader.onLoad(periodToFetch - 1);
                if (nextPeriodEvents == null)
                    nextPeriodEvents = mWeekViewLoader.onLoad(periodToFetch + 1);


                // Clear events.
                mEventRects.clear();
                sortAndCacheEvents(previousPeriodEvents);
                sortAndCacheEvents(currentPeriodEvents);
                sortAndCacheEvents(nextPeriodEvents);
                calculateHeaderHeight();

                mPreviousPeriodEvents = previousPeriodEvents;
                mCurrentPeriodEvents = currentPeriodEvents;
                mNextPeriodEvents = nextPeriodEvents;
                mFetchedPeriod = periodToFetch;
            }
        }

        // Prepare to calculate positions of each events.
        List<EventRect> tempEvents = mEventRects;
        mEventRects = new ArrayList<EventRect>();

        // Iterate through each day with events to calculate the position of the events.
        while (tempEvents.size() > 0) {
            ArrayList<EventRect> eventRects = new ArrayList<>(tempEvents.size());

            // Get first event for a day.
            EventRect eventRect1 = tempEvents.remove(0);
            eventRects.add(eventRect1);

            int i = 0;
            while (i < tempEvents.size()) {
                // Collect all other events for same day.
                EventRect eventRect2 = tempEvents.get(i);
                if (isSameDay(eventRect1.event.getStartTime(), eventRect2.event.getStartTime())) {
                    tempEvents.remove(i);
                    eventRects.add(eventRect2);
                } else {
                    i++;
                }
            }
            computePositionOfEvents(eventRects);
        }
    }

    /**
     * Cache the event for smooth scrolling functionality.
     *
     * @param event The event to cache.
     */
    private void cacheEvent(WeekViewEvent event) {
        if (event.getStartTime().compareTo(event.getEndTime()) >= 0)
            return;
        List<WeekViewEvent> splitedEvents = event.splitWeekViewEvents();
        for (WeekViewEvent splitedEvent : splitedEvents) {
            mEventRects.add(new EventRect(splitedEvent, event, null));
        }
    }

    /**
     * Sort and cache events.
     *
     * @param events The events to be sorted and cached.
     */
    private void sortAndCacheEvents(List<? extends WeekViewEvent> events) {
        sortEvents(events);
        for (WeekViewEvent event : events) {
            cacheEvent(event);
        }
    }

    /**
     * Sorts the events in ascending order.
     *
     * @param events The events to be sorted.
     */
    private void sortEvents(List<? extends WeekViewEvent> events) {
        Collections.sort(events, new Comparator<WeekViewEvent>() {
            @Override
            public int compare(WeekViewEvent event1, WeekViewEvent event2) {
                Log.e("mecheck" + event1.getName() + "," + event2.getName(), event1.getMyday() + "," + event2.getMyday());
                return event1.getMyday() > event2.getMyday() ? -1 : event1.getMyday() < event2.getMyday() ? 1 : 0;
//                    long end2 = event2.getEndTime().getTimeInMillis();

//                long start1 = event1.getStartTime().getTimeInMillis();
//                long start2 = event2.getStartTime().getTimeInMillis();
//                int comparator = start1 > start2 ? 1 : (start1 < start2 ? -1 : 0);
//                if (comparator == 0) {
//                    long end1 = event1.getEndTime().getTimeInMillis();
//                    long end2 = event2.getEndTime().getTimeInMillis();
//                    comparator = end1 > end2 ? 1 : (end1 < end2 ? -1 : 0);
//                }
//                return comparator;
            }
        });
    }

    /**
     * Calculates the left and right positions of each events. This comes handy specially if events
     * are overlapping.
     *
     * @param eventRects The events along with their wrapper class.
     */
    private void computePositionOfEvents(List<EventRect> eventRects) {
        // Make "collision groups" for all events that collide with others.
        List<List<EventRect>> collisionGroups = new ArrayList<List<EventRect>>();
        for (EventRect eventRect : eventRects) {
            boolean isPlaced = false;

            outerLoop:
            for (List<EventRect> collisionGroup : collisionGroups) {
                for (EventRect groupEvent : collisionGroup) {
                    if (isEventsCollide(groupEvent.event, eventRect.event) && groupEvent.event.isAllDay() == eventRect.event.isAllDay()) {
                        collisionGroup.add(eventRect);
                        isPlaced = true;
                        break outerLoop;
                    }
                }
            }

            if (!isPlaced) {
                List<EventRect> newGroup = new ArrayList<EventRect>();
                newGroup.add(eventRect);
                collisionGroups.add(newGroup);
            }
        }

        for (List<EventRect> collisionGroup : collisionGroups) {
            expandEventsToMaxWidth(collisionGroup);
        }
    }

    /**
     * Expands all the events to maximum possible width. The events will try to occupy maximum
     * space available horizontally.
     *
     * @param collisionGroup The group of events which overlap with each other.
     */
    private void expandEventsToMaxWidth(List<EventRect> collisionGroup) {

        // Expand the events to maximum possible width.
        List<List<EventRect>> columns = new ArrayList<List<EventRect>>();
        columns.add(new ArrayList<EventRect>());
        for (EventRect eventRect : collisionGroup) {
            boolean isPlaced = false;
            for (List<EventRect> column : columns) {
                if (column.size() == 0) {
                    column.add(eventRect);
                    isPlaced = true;
                } else if (!isEventsCollide(eventRect.event, column.get(column.size() - 1).event)) {
                    column.add(eventRect);
                    isPlaced = true;
                    break;
                }
            }
            if (!isPlaced) {
                List<EventRect> newColumn = new ArrayList<EventRect>();
                newColumn.add(eventRect);
                columns.add(newColumn);
            }
        }


        // Calculate left and right position for all the events.
        // Get the maxRowCount by looking in all columns.
        int maxRowCount = 0;
        for (List<EventRect> column : columns) {
            maxRowCount = Math.max(maxRowCount, column.size());
        }

        for (int i = 0; i < maxRowCount; i++) {
            // Set the left and right values of the event.
            float j = 0;
            for (List<EventRect> column : columns) {
                if (column.size() >= i + 1) {

                    EventRect eventRect = column.get(i);
                    eventRect.width = 1f / columns.size();
                    eventRect.left = j / columns.size();
                    if (!eventRect.event.isAllDay()) {
                        eventRect.top = eventRect.event.getStartTime().get(Calendar.HOUR_OF_DAY) * 60 + eventRect.event.getStartTime().get(Calendar.MINUTE);
                        eventRect.bottom = eventRect.event.getEndTime().get(Calendar.HOUR_OF_DAY) * 60 + eventRect.event.getEndTime().get(Calendar.MINUTE);
                    } else {
                        eventRect.top = j * mAllDayEventHeight + j * 5;
                        eventRect.bottom = mAllDayEventHeight;
                        eventRect.noofevent = columns.size();
                    }
                    mEventRects.add(eventRect);
                }
                j++;
            }
        }
    }

    /**
     * Checks if two events overlap.
     *
     * @param event1 The first event.
     * @param event2 The second event.
     * @return true if the events overlap.
     */
    private boolean isEventsCollide(WeekViewEvent event1, WeekViewEvent event2) {
        long start1 = event1.getStartTime().getTimeInMillis();
        long end1 = event1.getEndTime().getTimeInMillis();
        long start2 = event2.getStartTime().getTimeInMillis();
        long end2 = event2.getEndTime().getTimeInMillis();
        return !((start1 >= end2) || (end1 <= start2));
    }

    /**
     * Checks if time1 occurs after (or at the same time) time2.
     *
     * @param time1 The time to check.
     * @param time2 The time to check against.
     * @return true if time1 and time2 are equal or if time1 is after time2. Otherwise false.
     */
    private boolean isTimeAfterOrEquals(Calendar time1, Calendar time2) {
        return !(time1 == null || time2 == null) && time1.getTimeInMillis() >= time2.getTimeInMillis();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        mAreDimensionsInvalid = true;
    }

    public void setOnEventClickListener(EventClickListener listener) {
        this.mEventClickListener = listener;
    }

    public void setshadow(View shadow) {
        this.shadow = shadow;
    }

    /////////////////////////////////////////////////////////////////
    //
    //      Functions related to setting and getting the properties.
    //
    /////////////////////////////////////////////////////////////////

    public void setfont(Typeface typeface, int type) {
        if (type == 0) {
            mTimeTextPaint.setTypeface(typeface);
            jHeaderTextPaint.setTypeface(ResourcesCompat.getFont(mContext, R.font.latoregular));


        } else {
            mHeaderTextPaint.setTypeface(typeface);
            jheaderEventTextpaint.setTypeface(typeface);

        }
        invalidate();

    }

    public EventClickListener getEventClickListener() {
        return mEventClickListener;
    }

    public @Nullable
    MonthLoader.MonthChangeListener getMonthChangeListener() {
        if (mWeekViewLoader instanceof MonthLoader)
            return ((MonthLoader) mWeekViewLoader).getOnMonthChangeListener();
        return null;
    }

    public void setMonthChangeListener(MonthLoader.MonthChangeListener monthChangeListener) {
        this.mWeekViewLoader = new MonthLoader(monthChangeListener);
    }

    /**
     * Get event loader in the week view. Event loaders define the  interval after which the events
     * are loaded in week view. For a MonthLoader events are loaded for every month. You can define
     * your custom event loader by extending WeekViewLoader.
     *
     * @return The event loader.
     */
    public WeekViewLoader getWeekViewLoader() {
        return mWeekViewLoader;
    }

    /**
     * Set event loader in the week view. For example, a MonthLoader. Event loaders define the
     * interval after which the events are loaded in week view. For a MonthLoader events are loaded
     * for every month. You can define your custom event loader by extending WeekViewLoader.
     *
     * @param loader The event loader.
     */
    public void setWeekViewLoader(WeekViewLoader loader) {
        this.mWeekViewLoader = loader;
    }

    public EventLongPressListener getEventLongPressListener() {
        return mEventLongPressListener;
    }

    public void setEventLongPressListener(EventLongPressListener eventLongPressListener) {
        this.mEventLongPressListener = eventLongPressListener;
    }

    public EmptyViewClickListener getEmptyViewClickListener() {
        return mEmptyViewClickListener;
    }

    public void setEmptyViewClickListener(EmptyViewClickListener emptyViewClickListener) {
        this.mEmptyViewClickListener = emptyViewClickListener;
    }

    public EmptyViewLongPressListener getEmptyViewLongPressListener() {
        return mEmptyViewLongPressListener;
    }

    public void setEmptyViewLongPressListener(EmptyViewLongPressListener emptyViewLongPressListener) {
        this.mEmptyViewLongPressListener = emptyViewLongPressListener;
    }

    public ScrollListener getScrollListener() {
        return mScrollListener;
    }

    public void setScrollListener(ScrollListener scrolledListener) {
        this.mScrollListener = scrolledListener;
    }

    /**
     * Get the interpreter which provides the text to show in the header column and the header row.
     *
     * @return The date, time interpreter.
     */
    public DateTimeInterpreter getDateTimeInterpreter() {
        if (mDateTimeInterpreter == null) {
            mDateTimeInterpreter = new DateTimeInterpreter() {
                @Override
                public String interpretday(Calendar date) {
                    try {
                        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_YEAR | DateUtils.FORMAT_NUMERIC_DATE;
                        String localizedDate = DateUtils.formatDateTime(getContext(), date.getTime().getTime(), flags);
                        SimpleDateFormat sdf = mDayNameLength == LENGTH_SHORT ? new SimpleDateFormat("EEEEE", Locale.getDefault()) : new SimpleDateFormat("EEE", Locale.getDefault());
                        return String.format("%s %s", sdf.format(date.getTime()).toUpperCase(), localizedDate);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "";
                    }
                }

                @Override
                public String interpretDate(Calendar date) {
                    int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);


                    return dayOfMonth + "";
                }

                @Override
                public String interpretTime(int hour) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, 0);

                    try {
                        SimpleDateFormat sdf = DateFormat.is24HourFormat(getContext()) ? new SimpleDateFormat("HH:mm", Locale.getDefault()) : new SimpleDateFormat("hh a", Locale.getDefault());
                        return sdf.format(calendar.getTime());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "";
                    }
                }
            };
        }
        return mDateTimeInterpreter;
    }

    /**
     * Set the interpreter which provides the text to show in the header column and the header row.
     *
     * @param dateTimeInterpreter The date, time interpreter.
     */
    public void setDateTimeInterpreter(DateTimeInterpreter dateTimeInterpreter) {
        this.mDateTimeInterpreter = dateTimeInterpreter;

        // Refresh time column width.
        initTextTimeWidth();
    }

    /**
     * Get the number of visible days in a week.
     *
     * @return The number of visible days in a week.
     */
    public int getNumberOfVisibleDays() {
        return mNumberOfVisibleDays;
    }

    /**
     * Set the number of visible days in a week.
     *
     * @param numberOfVisibleDays The number of visible days in a week.
     */
    public void setNumberOfVisibleDays(int numberOfVisibleDays) {
        this.mNumberOfVisibleDays = numberOfVisibleDays;
        mCurrentOrigin.x = 0;
        mCurrentOrigin.y = 0;
        invalidate();
    }

    public int getHourHeight() {
        return mHourHeight;
    }

    public void setHourHeight(int hourHeight) {
        mNewHourHeight = hourHeight;
        invalidate();
    }

    public int getColumnGap() {
        return mColumnGap;
    }

    public void setColumnGap(int columnGap) {
        mColumnGap = columnGap;
        invalidate();
    }

    public int getFirstDayOfWeek() {
        return mFirstDayOfWeek;
    }

    /**
     * Set the first day of the week. First day of the week is used only when the week view is first
     * drawn. It does not of any effect after user starts scrolling horizontally.
     * <p>
     * <b>Note:</b> This method will only work if the week view is set to display more than 6 days at
     * once.
     * </p>
     *
     * @param firstDayOfWeek The supported values are {@link java.util.Calendar#SUNDAY},
     *                       {@link java.util.Calendar#MONDAY}, {@link java.util.Calendar#TUESDAY},
     *                       {@link java.util.Calendar#WEDNESDAY}, {@link java.util.Calendar#THURSDAY},
     *                       {@link java.util.Calendar#FRIDAY}.
     */
    public void setFirstDayOfWeek(int firstDayOfWeek) {
        mFirstDayOfWeek = firstDayOfWeek;
        invalidate();
    }

    public boolean isShowFirstDayOfWeekFirst() {
        return mShowFirstDayOfWeekFirst;
    }

    public void setShowFirstDayOfWeekFirst(boolean show) {
        mShowFirstDayOfWeekFirst = show;
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setTextSize(int textSize) {
        mTextSize = textSize;
        mTodayHeaderTextPaint.setTextSize(mTextSize);
        mHeaderTextPaint.setTextSize(mTextSize);
        mTimeTextPaint.setTextSize(mTextSize);
        invalidate();
    }

    public int getHeaderColumnPadding() {
        return mHeaderColumnPadding;
    }

    public void setHeaderColumnPadding(int headerColumnPadding) {
        mHeaderColumnPadding = headerColumnPadding;
        invalidate();
    }

    public int getHeaderColumnTextColor() {
        return mHeaderColumnTextColor;
    }

    public void setHeaderColumnTextColor(int headerColumnTextColor) {
        mHeaderColumnTextColor = headerColumnTextColor;
        mHeaderTextPaint.setColor(mHeaderColumnTextColor);
        mTimeTextPaint.setColor(mHeaderColumnTextColor);
        invalidate();
    }

    public int getHeaderRowPadding() {
        return mHeaderRowPadding;
    }

    public void setHeaderRowPadding(int headerRowPadding) {
        mHeaderRowPadding = headerRowPadding;
        invalidate();
    }

    public int getHeaderRowBackgroundColor() {
        return mHeaderRowBackgroundColor;
    }

    public void setHeaderRowBackgroundColor(int headerRowBackgroundColor) {
        mHeaderRowBackgroundColor = headerRowBackgroundColor;
        mHeaderBackgroundPaint.setColor(mHeaderRowBackgroundColor);
        invalidate();
    }

    public int getDayBackgroundColor() {
        return mDayBackgroundColor;
    }

    public void setDayBackgroundColor(int dayBackgroundColor) {
        mDayBackgroundColor = dayBackgroundColor;
        mDayBackgroundPaint.setColor(mDayBackgroundColor);
        invalidate();
    }

    public int getHourSeparatorColor() {
        return mHourSeparatorColor;
    }

    public void setHourSeparatorColor(int hourSeparatorColor) {
        mHourSeparatorColor = hourSeparatorColor;
        mHourSeparatorPaint.setColor(mHourSeparatorColor);
        invalidate();
    }

    public int getTodayBackgroundColor() {
        return mTodayBackgroundColor;
    }

    public void setTodayBackgroundColor(int todayBackgroundColor) {
        mTodayBackgroundColor = todayBackgroundColor;
        mTodayBackgroundPaint.setColor(mTodayBackgroundColor);
        invalidate();
    }

    public int getHourSeparatorHeight() {
        return mHourSeparatorHeight;
    }

    public void setHourSeparatorHeight(int hourSeparatorHeight) {
        mHourSeparatorHeight = hourSeparatorHeight;
        mHourSeparatorPaint.setStrokeWidth(mHourSeparatorHeight);
        invalidate();
    }

    public int getTodayHeaderTextColor() {
        return mTodayHeaderTextColor;
    }

    public void setTodayHeaderTextColor(int todayHeaderTextColor) {
        mTodayHeaderTextColor = todayHeaderTextColor;
        mTodayHeaderTextPaint.setColor(mTodayHeaderTextColor);
        invalidate();
    }

    public int getEventTextSize() {
        return mEventTextSize;
    }

    public void setEventTextSize(int eventTextSize) {
        mEventTextSize = eventTextSize;
        mEventTextPaint.setTextSize(mEventTextSize);
        invalidate();
    }

    public int getEventTextColor() {
        return mEventTextColor;
    }

    public void setEventTextColor(int eventTextColor) {
        mEventTextColor = eventTextColor;
        mEventTextPaint.setColor(mEventTextColor);
        invalidate();
    }

    public int getEventPadding() {
        return mEventPadding;
    }

    public void setEventPadding(int eventPadding) {
        mEventPadding = eventPadding;
        invalidate();
    }

    public int getHeaderColumnBackgroundColor() {
        return mHeaderColumnBackgroundColor;
    }

    public void setHeaderColumnBackgroundColor(int headerColumnBackgroundColor) {
        mHeaderColumnBackgroundColor = headerColumnBackgroundColor;
        mHeaderColumnBackgroundPaint.setColor(mHeaderColumnBackgroundColor);
        invalidate();
    }

    public int getDefaultEventColor() {
        return mDefaultEventColor;
    }

    public void setDefaultEventColor(int defaultEventColor) {
        mDefaultEventColor = defaultEventColor;
        invalidate();
    }

    /**
     * <b>Note:</b> Use {@link #setDateTimeInterpreter(DateTimeInterpreter)} and
     * {@link #getDateTimeInterpreter()} instead.
     *
     * @return Either long or short day name is being used.
     */
    @Deprecated
    public int getDayNameLength() {
        return mDayNameLength;
    }

    /**
     * Set the length of the day name displayed in the header row. Example of short day names is
     * 'M' for 'Monday' and example of long day names is 'Mon' for 'Monday'.
     * <p>
     * <b>Note:</b> Use {@link #setDateTimeInterpreter(DateTimeInterpreter)} instead.
     * </p>
     */
    @Deprecated
    public void setDayNameLength(int length) {
        if (length != LENGTH_LONG && length != LENGTH_SHORT) {
            throw new IllegalArgumentException("length parameter must be either LENGTH_LONG or LENGTH_SHORT");
        }
        this.mDayNameLength = length;
    }

    public int getOverlappingEventGap() {
        return mOverlappingEventGap;
    }

    /**
     * Set the gap between overlapping events.
     *
     * @param overlappingEventGap The gap between overlapping events.
     */
    public void setOverlappingEventGap(int overlappingEventGap) {
        this.mOverlappingEventGap = overlappingEventGap;
        invalidate();
    }

    public int getEventCornerRadius() {
        return mEventCornerRadius;
    }

    /**
     * Set corner radius for event rect.
     *
     * @param eventCornerRadius the radius in px.
     */
    public void setEventCornerRadius(int eventCornerRadius) {
        mEventCornerRadius = eventCornerRadius;
    }

    public int getEventMarginVertical() {
        return mEventMarginVertical;
    }

    /**
     * Set the top and bottom margin of the event. The event will release this margin from the top
     * and bottom edge. This margin is useful for differentiation consecutive events.
     *
     * @param eventMarginVertical The top and bottom margin.
     */
    public void setEventMarginVertical(int eventMarginVertical) {
        this.mEventMarginVertical = eventMarginVertical;
        invalidate();
    }

    /**
     * Returns the first visible day in the week view.
     *
     * @return The first visible day in the week view.
     */
    public Calendar getFirstVisibleDay() {
        return mFirstVisibleDay;
    }

    /**
     * Returns the last visible day in the week view.
     *
     * @return The last visible day in the week view.
     */
    public Calendar getLastVisibleDay() {
        return mLastVisibleDay;
    }

    /**
     * Get the scrolling speed factor in horizontal direction.
     *
     * @return The speed factor in horizontal direction.
     */
    public float getXScrollingSpeed() {
        return mXScrollingSpeed;
    }

    /**
     * Sets the speed for horizontal scrolling.
     *
     * @param xScrollingSpeed The new horizontal scrolling speed.
     */
    public void setXScrollingSpeed(float xScrollingSpeed) {
        this.mXScrollingSpeed = xScrollingSpeed;
    }

    /**
     * Whether weekends should have a background color different from the normal day background
     * color. The weekend background colors are defined by the attributes
     * `futureWeekendBackgroundColor` and `pastWeekendBackgroundColor`.
     *
     * @return True if weekends should have different background colors.
     */
    public boolean isShowDistinctWeekendColor() {
        return mShowDistinctWeekendColor;
    }

    /**
     * Set whether weekends should have a background color different from the normal day background
     * color. The weekend background colors are defined by the attributes
     * `futureWeekendBackgroundColor` and `pastWeekendBackgroundColor`.
     *
     * @param showDistinctWeekendColor True if weekends should have different background colors.
     */
    public void setShowDistinctWeekendColor(boolean showDistinctWeekendColor) {
        this.mShowDistinctWeekendColor = showDistinctWeekendColor;
        invalidate();
    }

    /**
     * Whether past and future days should have two different background colors. The past and
     * future day colors are defined by the attributes `futureBackgroundColor` and
     * `pastBackgroundColor`.
     *
     * @return True if past and future days should have two different background colors.
     */
    public boolean isShowDistinctPastFutureColor() {
        return mShowDistinctPastFutureColor;
    }

    /**
     * Set whether weekends should have a background color different from the normal day background
     * color. The past and future day colors are defined by the attributes `futureBackgroundColor`
     * and `pastBackgroundColor`.
     *
     * @param showDistinctPastFutureColor True if past and future should have two different
     *                                    background colors.
     */
    public void setShowDistinctPastFutureColor(boolean showDistinctPastFutureColor) {
        this.mShowDistinctPastFutureColor = showDistinctPastFutureColor;
        invalidate();
    }

    /**
     * Get whether "now" line should be displayed. "Now" line is defined by the attributes
     * `nowLineColor` and `nowLineThickness`.
     *
     * @return True if "now" line should be displayed.
     */
    public boolean isShowNowLine() {
        return mShowNowLine;
    }

    /**
     * Set whether "now" line should be displayed. "Now" line is defined by the attributes
     * `nowLineColor` and `nowLineThickness`.
     *
     * @param showNowLine True if "now" line should be displayed.
     */
    public void setShowNowLine(boolean showNowLine) {
        this.mShowNowLine = showNowLine;
        invalidate();
    }

    /**
     * Get the "now" line color.
     *
     * @return The color of the "now" line.
     */
    public int getNowLineColor() {
        return mNowLineColor;
    }

    /**
     * Set the "now" line color.
     *
     * @param nowLineColor The color of the "now" line.
     */
    public void setNowLineColor(int nowLineColor) {
        this.mNowLineColor = nowLineColor;
        invalidate();
    }

    /**
     * Get the "now" line thickness.
     *
     * @return The thickness of the "now" line.
     */
    public int getNowLineThickness() {
        return mNowLineThickness;
    }

    /**
     * Set the "now" line thickness.
     *
     * @param nowLineThickness The thickness of the "now" line.
     */
    public void setNowLineThickness(int nowLineThickness) {
        this.mNowLineThickness = nowLineThickness;
        invalidate();
    }

    /**
     * Get whether the week view should fling horizontally.
     *
     * @return True if the week view has horizontal fling enabled.
     */
    public boolean isHorizontalFlingEnabled() {
        return mHorizontalFlingEnabled;
    }

    /**
     * Set whether the week view should fling horizontally.
     *
     * @return True if it should have horizontal fling enabled.
     */
    public void setHorizontalFlingEnabled(boolean enabled) {
        mHorizontalFlingEnabled = enabled;
    }

    /**
     * Get whether the week view should fling vertically.
     *
     * @return True if the week view has vertical fling enabled.
     */
    public boolean isVerticalFlingEnabled() {
        return mVerticalFlingEnabled;
    }

    /**
     * Set whether the week view should fling vertically.
     *
     * @return True if it should have vertical fling enabled.
     */
    public void setVerticalFlingEnabled(boolean enabled) {
        mVerticalFlingEnabled = enabled;
    }

    /**
     * Get the height of AllDay-events.
     *
     * @return Height of AllDay-events.
     */
    public int getAllDayEventHeight() {
        return mAllDayEventHeight;
    }

    /**
     * Set the height of AllDay-events.
     */
    public void setAllDayEventHeight(int height) {
        mAllDayEventHeight = height;
    }

    /**
     * Get scroll duration
     *
     * @return scroll duration
     */
    public int getScrollDuration() {
        return mScrollDuration;
    }

    /**
     * Set the scroll duration
     */
    public void setScrollDuration(int scrollDuration) {
        mScrollDuration = scrollDuration;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isclosed()) return false;

        mScaleDetector.onTouchEvent(event);
        boolean val = mGestureDetector.onTouchEvent(event);

        // Check after call of mGestureDetector, so mCurrentFlingDirection and mCurrentScrollDirection are set.
        if (event.getAction() == MotionEvent.ACTION_UP && !mIsZooming && mCurrentFlingDirection == Direction.NONE) {

            if (mCurrentScrollDirection == Direction.RIGHT || mCurrentScrollDirection == Direction.LEFT) {
                if (true) {

                    float k = 0;
                    if (mCurrentScrollDirection == Direction.RIGHT && getNumberOfVisibleDays() == 1)
                        k = mHeaderColumnWidth;
                    int next = 0;

                    if (getNumberOfVisibleDays() == 7) {
                        if (mCurrentScrollDirection == Direction.LEFT) {
                            next = (int) (weekx - mWidthPerDay * 7);
                        } else {
                            next = (int) (weekx + mWidthPerDay * 7);
                        }
                    } else {
                        if (mCurrentScrollDirection == Direction.LEFT) {
                            next = (int) (weekx - mWidthPerDay);
                        } else {
                            next = (int) (weekx + mWidthPerDay);
                        }
                    }

                    int previous = (int) weekx;
//                    if (mCurrentOrigin.x<0){
//                        next*=-1;
//                        previous*=-1;
//                    }
                    if (Math.abs(Math.abs(mCurrentOrigin.x + k) - Math.abs(next)) < Math.abs(Math.abs(mCurrentOrigin.x + k) - Math.abs(previous))) {


                        mCurrentOrigin.x = next;

                        ViewCompat.postInvalidateOnAnimation(WeekView.this);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                invalidate();
                            }
                        }, 100);

                    } else {

                        mCurrentOrigin.x = previous;

                        ViewCompat.postInvalidateOnAnimation(WeekView.this);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                invalidate();
                            }
                        }, 100);

                    }

//                    float small=Math.min(Math.abs(next-mCurrentOrigin.x),Math.abs(previous-mCurrentOrigin.x));
//                    int j=1;
//                    if (mCurrentOrigin.x<0)j=-1;
//                    mCurrentOrigin.x=(small-mCurrentOrigin.x)*j;
//                    invalidate();


                }
                //goToNearestOrigin();
            }
            mCurrentScrollDirection = Direction.NONE;
        }

        return val;
    }

    private void goToNearestOrigin() {

        double leftDays = mCurrentOrigin.x / (mWidthPerDay + mColumnGap);
        if (getNumberOfVisibleDays() == 1 && mCurrentScrollDirection == Direction.RIGHT) {
            leftDays = (mCurrentOrigin.x + mHeaderColumnWidth) / (mWidthPerDay + mColumnGap);

        }

        if (mCurrentFlingDirection != Direction.NONE) {
            // snap to nearest day
            leftDays = Math.round(leftDays);
        } else if (mCurrentScrollDirection == Direction.LEFT) {
            // snap to last day
            leftDays = Math.floor(leftDays);
        } else if (mCurrentScrollDirection == Direction.RIGHT) {
            // snap to next day
            leftDays = Math.ceil(leftDays);
        } else {
            // snap to nearest day
            leftDays = Math.round(leftDays);
        }
        int nearestOrigin = (int) (mCurrentOrigin.x - leftDays * (mWidthPerDay + mColumnGap));

        if (nearestOrigin != 0) {
            // Stop current animation.
            mScroller.forceFinished(true);
            // Snap to date.
            mScroller.startScroll((int) mCurrentOrigin.x, (int) mCurrentOrigin.y, -nearestOrigin, 0, (int) (Math.abs(nearestOrigin) / mWidthPerDay * mScrollDuration));
            ViewCompat.postInvalidateOnAnimation(WeekView.this);
        }
        // Reset scrolling and fling direction.
        mCurrentScrollDirection = mCurrentFlingDirection = Direction.NONE;
    }

    /////////////////////////////////////////////////////////////////
    //
    //      Functions related to scrolling.
    //
    /////////////////////////////////////////////////////////////////

    private void goToNearestOrigin1() {

        double leftDays = mCurrentOrigin.x / (mWidthPerDay + mColumnGap);
        if (getNumberOfVisibleDays() == 1 && mCurrentScrollDirection == Direction.RIGHT) {
            leftDays = (mCurrentOrigin.x + mHeaderColumnWidth) / (mWidthPerDay + mColumnGap);

        }

        if (mCurrentFlingDirection != Direction.NONE) {
            // snap to nearest day
            leftDays = Math.round(leftDays);
        } else if (mCurrentScrollDirection == Direction.LEFT) {
            // snap to last day
            leftDays = Math.ceil(leftDays);
        } else if (mCurrentScrollDirection == Direction.RIGHT) {
            // snap to next day
            leftDays = Math.floor(leftDays);
        } else {
            // snap to nearest day
            leftDays = Math.round(leftDays);
        }
        int nearestOrigin = (int) (mCurrentOrigin.x - leftDays * (mWidthPerDay + mColumnGap));

        if (nearestOrigin != 0) {
            // Stop current animation.
            mScroller.forceFinished(true);
            // Snap to date.
            mScroller.startScroll((int) mCurrentOrigin.x, (int) mCurrentOrigin.y, -nearestOrigin, 0, (int) (Math.abs(nearestOrigin) / mWidthPerDay * mScrollDuration));
            ViewCompat.postInvalidateOnAnimation(WeekView.this);
        }
        // Reset scrolling and fling direction.
        mCurrentScrollDirection = mCurrentFlingDirection = Direction.NONE;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
//        if (mScroller.isFinished()) {
//            if (mCurrentFlingDirection != Direction.NONE) {
//                // Snap to day after fling is finished.
//                Log.e("flingcall","flh"+mScroller.getCurrVelocity());
//               goToNearestOrigin();
//            }
//        } else {
//
//            if (mCurrentFlingDirection != Direction.NONE && forceFinishScroll()) {
//                Log.e("flingcall","finish"+mScroller.getStartX()+","+mMinimumFlingVelocity);
//
//               goToNearestOrigin();
//            }
//            else if (mScroller.computeScrollOffset()) {
//                Log.e("flingcall","main"+mScroller.getStartX()+","+mMinimumFlingVelocity);
//                mCurrentOrigin.y = mScroller.getCurrY();
//                mCurrentOrigin.x = mScroller.getCurrX();
//                ViewCompat.postInvalidateOnAnimation(this);
//            }
//        }
    }

    /**
     * Check if scrolling should be stopped.
     *
     * @return true if scrolling should be stopped before reaching the end of animation.
     */
    private boolean forceFinishScroll() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // current velocity only available since api 14
            return mScroller.getCurrVelocity() <= mMinimumFlingVelocity;
        } else {
            return false;
        }
    }

    public boolean isclosed() {

        MainActivity mainActivity = (MainActivity) mContext;
        return mainActivity.isAppBarExpanded();
    }

    /**
     * Show today on the week view.
     */
    public void goToToday() {
        Calendar today = Calendar.getInstance();
        goToDate(today);
    }

    /**
     * Show a specific day on the week view.
     *
     * @param date The date to show.
     */
    public void goToDate(Calendar date) {

        if (getNumberOfVisibleDays() == 7) {
            int diff = date.get(Calendar.DAY_OF_WEEK) - getFirstDayOfWeek();
            Log.e("diff", diff + "");
            if (diff < 0) {
                date.add(Calendar.DAY_OF_MONTH, -(7 - Math.abs(diff)));
            } else {
                date.add(Calendar.DAY_OF_MONTH, -diff);
            }

        }

        mScroller.forceFinished(true);
        mCurrentScrollDirection = mCurrentFlingDirection = Direction.NONE;

        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        if (mAreDimensionsInvalid) {
            mScrollToDay = date;
            return;
        }

        mRefreshEvents = true;

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        long day = 1000L * 60L * 60L * 24L;
        long dateInMillis = date.getTimeInMillis() + date.getTimeZone().getOffset(date.getTimeInMillis());
        long todayInMillis = today.getTimeInMillis() + today.getTimeZone().getOffset(today.getTimeInMillis());
        long dateDifference = (dateInMillis / day) - (todayInMillis / day);
        mCurrentOrigin.x = -dateDifference * (mWidthPerDay + mColumnGap);
        invalidate();
    }


    /////////////////////////////////////////////////////////////////
    //
    //      Public methods.
    //
    /////////////////////////////////////////////////////////////////

    /**
     * Refreshes the view and loads the events again.
     */
    public void notifyDatasetChanged() {
        mRefreshEvents = true;
        invalidate();
    }

    /**
     * Vertically scroll to a specific hour in the week view.
     *
     * @param hour The hour to scroll to in 24-hour format. Supported values are 0-24.
     */
    public void goToHour(double hour) {
        if (mAreDimensionsInvalid) {
            mScrollToHour = hour;
            return;
        }

        int verticalOffset = 0;
        if (hour > 24)
            verticalOffset = mHourHeight * 24;
        else if (hour > 0)
            verticalOffset = (int) (mHourHeight * hour);

        if (verticalOffset > mHourHeight * 24 - getHeight() + mHeaderHeight + mHeaderRowPadding * 3 + mHeaderMarginBottom)
            verticalOffset = (int) (mHourHeight * 24 - getHeight() + mHeaderHeight + mHeaderRowPadding * 3 + mHeaderMarginBottom);

        mCurrentOrigin.y = -verticalOffset;
        invalidate();
    }

    /**
     * Get the first hour that is visible on the screen.
     *
     * @return The first hour that is visible.
     */
    public double getFirstVisibleHour() {
        return -mCurrentOrigin.y / mHourHeight;
    }

    private enum Direction {
        NONE, LEFT, RIGHT, VERTICAL
    }

    public interface EventClickListener {
        /**
         * Triggered when clicked on one existing event
         *
         * @param event:     event clicked.
         * @param eventRect: view containing the clicked event.
         */
        void onEventClick(WeekViewEvent event, RectF eventRect);
    }


    /////////////////////////////////////////////////////////////////
    //
    //      Interfaces.
    //
    /////////////////////////////////////////////////////////////////

    public interface EventLongPressListener {
        /**
         * @param event:     event clicked.
         * @param eventRect: view containing the clicked event.
         */
        void onEventLongPress(WeekViewEvent event, RectF eventRect);
    }

    public interface EmptyViewClickListener {
        /**
         * Triggered when the users clicks on a empty space of the calendar.
         *
         * @param time: {@link Calendar} object set with the date and time of the clicked position on the view.
         */
        void onEmptyViewClicked(Calendar time);
    }

    public interface EmptyViewLongPressListener {
        /**
         * @param time: {@link Calendar} object set with the date and time of the long pressed position on the view.
         */
        void onEmptyViewLongPress(Calendar time);
    }

    public interface ScrollListener {
        /**
         * Called when the first visible day has changed.
         * <p>
         * (this will also be called during the first draw of the weekview)
         *
         * @param newFirstVisibleDay The new first visible day
         * @param oldFirstVisibleDay The old first visible day (is null on the first call).
         */
        void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay);
    }

    /**
     * A class to hold reference to the events and their visual representation. An EventRect is
     * actually the rectangle that is drawn on the calendar for a given event. There may be more
     * than one rectangle for a single event (an event that expands more than one day). In that
     * case two instances of the EventRect will be used for a single event. The given event will be
     * stored in "originalEvent". But the event that corresponds to rectangle the rectangle
     * instance will be stored in "event".
     */
    private class EventRect {
        public WeekViewEvent event;
        public WeekViewEvent originalEvent;
        public RectF rectF;
        public float left;
        public float width;
        public float top;
        public float bottom;
        public int noofevent;

        /**
         * Create a new instance of event rect. An EventRect is actually the rectangle that is drawn
         * on the calendar for a given event. There may be more than one rectangle for a single
         * event (an event that expands more than one day). In that case two instances of the
         * EventRect will be used for a single event. The given event will be stored in
         * "originalEvent". But the event that corresponds to rectangle the rectangle instance will
         * be stored in "event".
         *
         * @param event         Represents the event which this instance of rectangle represents.
         * @param originalEvent The original event that was passed by the user.
         * @param rectF         The rectangle.
         */
        public EventRect(WeekViewEvent event, WeekViewEvent originalEvent, RectF rectF) {
            this.event = event;
            this.rectF = rectF;
            this.originalEvent = originalEvent;
        }
    }
}