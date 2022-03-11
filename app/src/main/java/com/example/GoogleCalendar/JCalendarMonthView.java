package com.example.GoogleCalendar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;

public class JCalendarMonthView extends View {
    float eachcellheight, eachcellwidth;
    long lastsec;
    int selectedcell;
    private int currentscrollstate;
    private Paint paint, mHeaderTextPaint, jDateTextPaint, jeventRectPaint, jeventtextpaint, jselectrectpaint, jtodaypaint;
    private Typeface dayfont;
    private int dayHeight, daytextsize, datemargintop, linecolor, linewidth, daytextcolor, datetextsize, datetextcolor, eventtextsize;
    private Context mContext;
    private float downx, downy;
    private String dayname[] = {"S", "M", "T", "W", "T", "F", "S"};
    private Rect selectedrect;
    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {


            Log.e("scrollstate", state + "");

            if (state == 2) {

                selectedrect = null;
                selectedcell = -1;
                downx = -1;
                downy = -1;
                invalidate();
            }
            currentscrollstate = state;

        }
    };
    private boolean isup = false;
    private ArrayList<DayModel> dayModels;
    private int mDefaultEventColor = Color.parseColor("#9fc6e7");
    private Rect mHeaderTextPaintRect;
    private Rect jDateTextPaintRect, jeventtextpaintRect;
    private int currentdaynameindex;
    private GestureDetector mDetector;

    public JCalendarMonthView(Context context) {
        this(context, null);
    }

    public JCalendarMonthView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public JCalendarMonthView(final Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // Hold references.
        mContext = context;
        if (attrs == null) {
            return;
        }
        // Get the attribute values (if any).
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.JCalendarMonthView, 0, 0);
        try {
            dayHeight = a.getDimensionPixelSize(R.styleable.JCalendarMonthView_dayHeight, 200);
            daytextsize = a.getDimensionPixelSize(R.styleable.JCalendarMonthView_daytextsize, 12);
            datetextsize = a.getDimensionPixelSize(R.styleable.JCalendarMonthView_datetextsize, 14);
            eventtextsize = a.getDimensionPixelSize(R.styleable.JCalendarMonthView_eventtextsize, 11);

            daytextcolor = a.getColor(R.styleable.JCalendarMonthView_daytextcolor, Color.GRAY);
            datetextcolor = a.getColor(R.styleable.JCalendarMonthView_datetextcolor, Color.GRAY);


            datemargintop = a.getDimensionPixelSize(R.styleable.JCalendarMonthView_datemargintop, 25);
            linecolor = a.getColor(R.styleable.JCalendarMonthView_linecolor, Color.GRAY);
            linewidth = a.getDimensionPixelSize(R.styleable.JCalendarMonthView_linewidth, 2);

            mDetector = new GestureDetector(context, new MyGestureListener());


        } finally {
            a.recycle();
        }

    }

    public void setDayModels(ArrayList<DayModel> dayModels, int currentdaynameindex) {
        this.dayModels = dayModels;
        this.currentdaynameindex = currentdaynameindex;
        invalidate();
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        if (event.getAction()==MotionEvent.ACTION_UP)return true;
//
//        if (event.getAction()==MotionEvent.ACTION_MOVE)return false;
//        return super.dispatchTouchEvent(event);
//    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        final int xtouch = (int) motionEvent.getX();
        final int ytouch = (int) motionEvent.getY();
        if (ytouch < dayHeight) return true;


        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

            isup = false;
            downx = xtouch;
            downy = ytouch;
            lastsec = System.currentTimeMillis();
            return true;

        } else if (currentscrollstate == 0 && motionEvent.getAction() == MotionEvent.ACTION_MOVE) {


            if (xtouch == downx && ytouch == downy && System.currentTimeMillis() - lastsec >= 80) {
                int column = (int) (xtouch / eachcellwidth);
                int row = (int) ((ytouch - dayHeight) / eachcellheight);
                int cell = (row * 7) + column;
                if (selectedcell != cell) {
                    selectedcell = cell;
                    int reachxend = (int) (eachcellwidth * (column + 1));
                    int reachxstart = (int) (eachcellwidth * (column));
                    int reachyend = (int) (eachcellheight * (row + 1) + dayHeight);
                    int reachystart = (int) (eachcellheight * (row) + dayHeight);

                    final int left = (int) (xtouch - reachxstart);
                    final int right = (int) (reachxend - xtouch);
                    final int top = (int) (ytouch - reachystart);
                    final int bottom = (int) (reachyend - ytouch);
                    ValueAnimator widthAnimator = ValueAnimator.ofInt(0, 100);
                    widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {

                            int progress = (int) animation.getAnimatedValue();
                            int start = xtouch - ((left * progress) / 100);
                            int endside = xtouch + ((right * progress) / 100);
                            int topside = ytouch - ((top * progress) / 100);
                            int bottomside = ytouch + ((bottom * progress) / 100);
                            selectedrect = new Rect(start, topside, endside, bottomside);

                            invalidate();
                        }
                    });
                    widthAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (isup) {
                                selectedrect = null;
                                selectedcell = -1;
                                downx = -1;
                                downy = -1;
                                invalidate();
                            }
                        }
                    });
                    widthAnimator.setDuration(220);
                    widthAnimator.start();
                }

            } else {
                selectedrect = null;
                selectedcell = -1;
                invalidate();
            }
            return super.onTouchEvent(motionEvent);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {


            if (xtouch == downx && ytouch == downy) {
                int column = (int) (xtouch / eachcellwidth);
                int row = (int) ((ytouch - dayHeight) / eachcellheight);
                int cell = (row * 7) + column;

                selectedcell = cell;
                int reachxend = (int) (eachcellwidth * (column + 1));
                int reachxstart = (int) (eachcellwidth * (column));
                int reachyend = (int) (eachcellheight * (row + 1) + dayHeight);
                int reachystart = (int) (eachcellheight * (row) + dayHeight);


                final int left = (int) (xtouch - reachxstart);
                final int right = (int) (reachxend - xtouch);
                final int top = (int) (ytouch - reachystart);
                final int bottom = (int) (reachyend - ytouch);
                ValueAnimator widthAnimator = ValueAnimator.ofInt(0, 100);
                widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {

                        int progress = (int) animation.getAnimatedValue();
                        int start = xtouch - ((left * progress) / 100);
                        int endside = xtouch + ((right * progress) / 100);
                        int topside = ytouch - ((top * progress) / 100);
                        int bottomside = ytouch + ((bottom * progress) / 100);
                        selectedrect = new Rect(start, topside, endside, bottomside);

                        invalidate();
                        if (progress == 100) {
                            MainActivity mainActivity = (MainActivity) mContext;
                            if (mainActivity != null && selectedcell != -1) {
                                DayModel dayModel = dayModels.get(selectedcell);
                                mainActivity.selectdateFromMonthPager(dayModel.getYear(), dayModel.getMonth(), dayModel.getDay());
                            }
                            selectedrect = null;
                            selectedcell = -1;
                            downx = -1;
                            downy = -1;
                            invalidate();
                        }
                    }
                });

                widthAnimator.setDuration(150);
                widthAnimator.start();


            } else {
                selectedrect = null;
                selectedcell = -1;
                downx = -1;
                downy = -1;
                invalidate();
            }

            isup = true;
            return super.onTouchEvent(motionEvent);
        } else {
            selectedrect = null;
            selectedcell = -1;
            downx = -1;
            downy = -1;
            invalidate();

        }

        return super.onTouchEvent(motionEvent);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        selectedrect = null;
        selectedcell = -1;
        downx = -1;
        downy = -1;

        mHeaderTextPaintRect = new Rect();
        jDateTextPaintRect = new Rect();
        jeventtextpaintRect = new Rect();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(linewidth);
        paint.setColor(linecolor);

        mHeaderTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextAlign(Paint.Align.CENTER);
        mHeaderTextPaint.setColor(daytextcolor);
        mHeaderTextPaint.setTypeface(ResourcesCompat.getFont(mContext, R.font.googlesansmed));
        mHeaderTextPaint.setTextSize(daytextsize);
        mHeaderTextPaint.getTextBounds("S", 0, "S".length(), mHeaderTextPaintRect);


        jDateTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        jDateTextPaint.setTextAlign(Paint.Align.CENTER);
        jDateTextPaint.setColor(datetextcolor);
        jDateTextPaint.setTypeface(ResourcesCompat.getFont(mContext, R.font.latoregular));
        jDateTextPaint.setTextSize(datetextsize);


        jeventtextpaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        jeventtextpaint.setTextAlign(Paint.Align.LEFT);
        jeventtextpaint.setColor(Color.WHITE);
        jeventtextpaint.setTypeface(ResourcesCompat.getFont(mContext, R.font.googlesansmed));
        jeventtextpaint.setTextSize(eventtextsize);
        jeventtextpaint.getTextBounds("a", 0, "a".length(), jeventtextpaintRect);

        jeventRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        jeventRectPaint.setStyle(Paint.Style.FILL);
        jeventRectPaint.setColor(Color.parseColor("#009688"));

        jselectrectpaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        jselectrectpaint.setStyle(Paint.Style.FILL);
        jselectrectpaint.setColor(Color.parseColor("#F0F0F0"));


        jtodaypaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        jtodaypaint.setStyle(Paint.Style.FILL);
        jtodaypaint.setColor(getResources().getColor(R.color.selectday));


        Log.e("height", "Test");
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        Log.e("event", "drag");
        return super.onDragEvent(event);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        eachcellheight = (getHeight() - dayHeight) / 6;
        eachcellwidth = getWidth() / 7;
        if (selectedrect != null) {
            canvas.drawRect(selectedrect, jselectrectpaint);
        }
        float[] point = new float[4];

        float begining = dayHeight;


        for (int i = 0; i < 7; i++) {

            if (i < 6) {
                point[0] = 0;
                point[1] = begining;
                point[2] = getWidth();
                point[3] = begining;
                canvas.drawLines(point, paint);
            }
            point[0] = eachcellwidth + eachcellwidth * i;
            point[1] = dayHeight / 1.5f;
            point[2] = eachcellwidth + eachcellwidth * i;
            point[3] = getHeight();
            canvas.drawLines(point, paint);
            begining = begining + eachcellheight;

        }
        int[] topspace = new int[42];
        for (int i = 0; i < 7 && dayModels != null && dayModels.size() == 42; i++) {
            for (int j = 0; j < 7 && i < 6; j++) {

                if (i == 0) {
                    if (j == currentdaynameindex)
                        mHeaderTextPaint.setColor(getResources().getColor(R.color.selectday));//todaycolor
                    else mHeaderTextPaint.setColor(daytextcolor);
                    canvas.drawText(dayname[j], (eachcellwidth * j + eachcellwidth / 2.0f) - mHeaderTextPaintRect.right / 2.0f, 5 + mHeaderTextPaintRect.height(), mHeaderTextPaint);
                }

                DayModel mydayModel = dayModels.get((i * 7) + j);

                String ss = mydayModel.getDay() + "";
                jDateTextPaint.getTextBounds(ss, 0, ss.length(), jDateTextPaintRect);
                if (mydayModel.isToday()) {//istoday
                    float centerx = ((eachcellwidth * j) + eachcellwidth / 2.0f);
                    float centery = datemargintop + dayHeight + (i * eachcellheight) + jDateTextPaintRect.height() / 2.0f;
                    float max = Math.max(jDateTextPaintRect.width(), jDateTextPaintRect.height());
                    jDateTextPaint.setColor(Color.WHITE);
                    canvas.drawRoundRect(centerx - max, centery - max, centerx + max, centery + max, max, max, jtodaypaint);
                } else {
                    if (!mydayModel.isenable())
                        jDateTextPaint.setColor(daytextcolor);//date disable color
                    else jDateTextPaint.setColor(datetextcolor);//date enable color
                }

                canvas.drawText(ss, ((eachcellwidth * j) + eachcellwidth / 2.0f), datemargintop + dayHeight + (i * eachcellheight) + jDateTextPaintRect.height(), jDateTextPaint);
                EventInfo eventInfo = mydayModel.getEventInfo();
                float constant = (2 * datemargintop) + dayHeight + (i * eachcellheight) + jDateTextPaintRect.height();
                int k = topspace[(i * 7) + j];
                int noofevent = 0;
                while (eventInfo != null) {
                    Log.e("jcalendar", mydayModel.toString() + "," + eventInfo.noofdayevent);
                    int row = i;
                    int col = j;
                    int jnoofday = eventInfo.noofdayevent;
                    if (jnoofday == 0) jnoofday = 1;
                    if (jnoofday > 1) {
                        boolean b = true;
                        int myrow = row + 1;
                        if ((row * 7) + col + jnoofday >= (myrow * 7)) {
                            while (b && myrow < 6) {
                                if ((row * 7) + col + jnoofday < ((myrow + 1) * 7)) {
                                    int diff = (row * 7) + j + jnoofday - (myrow) * 7;
                                    RectF rect1 = new RectF();
                                    rect1.left = (eachcellwidth * 0) - linewidth;
                                    rect1.right = (eachcellwidth * diff);
                                    rect1.top = dayHeight + (myrow * eachcellheight);//(2 * datemargintop + dayHeight + (i * eachcellheight) + rect.height());
                                    rect1.bottom = dayHeight + ((myrow + 1) * eachcellheight);//(2 * datemargintop + dayHeight + (i * eachcellheight) + rect.height() + 50);
                                    canvas.save();
                                    canvas.clipRect(rect1);
                                    RectF colorrect = new RectF();
                                    colorrect.left = rect1.left + 8;//0th column left padding
                                    colorrect.right = rect1.right - 12;
                                    float myconstant = (2 * datemargintop) + dayHeight + (myrow * eachcellheight) + jDateTextPaintRect.height();
                                    int newk = topspace[(myrow * 7) + 0];

                                    colorrect.top = myconstant + (42 * newk) + (3 * newk);
                                    colorrect.bottom = colorrect.top + 42;
                                    int color = eventInfo.eventcolor == 0 ? mDefaultEventColor : eventInfo.eventcolor;
                                    jeventRectPaint.setColor(color);
                                    canvas.drawRoundRect(colorrect, 6, 6, jeventRectPaint);
                                    canvas.drawText(eventInfo.title, colorrect.left + 5, colorrect.centerY() + (jeventtextpaintRect.height() / 2.0f), jeventtextpaint);
                                    canvas.restore();

                                    b = false;


                                } else {
                                    RectF rect1 = new RectF();
                                    rect1.left = (eachcellwidth * 0) - linewidth;
                                    rect1.right = (eachcellwidth * (0 + 7));
                                    rect1.top = dayHeight + (myrow * eachcellheight);//(2 * datemargintop + dayHeight + (i * eachcellheight) + rect.height());
                                    rect1.bottom = dayHeight + ((myrow + 1) * eachcellheight);//(2 * datemargintop + dayHeight + (i * eachcellheight) + rect.height() + 50);
                                    canvas.save();
                                    canvas.clipRect(rect1);
                                    RectF colorrect = new RectF();
                                    colorrect.left = rect1.left + 8;//0th column left padding
                                    colorrect.right = rect1.right - 12;
                                    float myconstant = (2 * datemargintop) + dayHeight + (myrow * eachcellheight) + jDateTextPaintRect.height();
                                    int newk = topspace[(myrow * 7) + 0];

                                    colorrect.top = myconstant + (42 * newk) + (3 * newk);
                                    colorrect.bottom = colorrect.top + 42;
                                    int color = eventInfo.eventcolor == 0 ? mDefaultEventColor : eventInfo.eventcolor;
                                    jeventRectPaint.setColor(color);
                                    canvas.drawRoundRect(colorrect, 6, 6, jeventRectPaint);
                                    canvas.drawText(eventInfo.title, colorrect.left + 5, colorrect.centerY() + (jeventtextpaintRect.height() / 2.0f), jeventtextpaint);
                                    canvas.restore();

                                }
                                myrow++;
                            }
                        }

                        int begin = (i * 7) + j;

                        for (int ia = 1; ia < jnoofday; ia++) {
                            if (begin + ia > 41) continue;
                            topspace[begin + ia] = k + 1;

                        }


                    }


                    RectF rect1 = new RectF();
                    rect1.left = (eachcellwidth * col) - linewidth;
                    int calculateday = col + jnoofday > 7 ? 7 - col : jnoofday;
                    rect1.right = (eachcellwidth * (col + calculateday));
                    Log.e("right", rect1.right + "," + col + jnoofday);
                    rect1.top = dayHeight + (row * eachcellheight);//(2 * datemargintop + dayHeight + (i * eachcellheight) + rect.height());
                    rect1.bottom = dayHeight + ((row + 1) * eachcellheight);//(2 * datemargintop + dayHeight + (i * eachcellheight) + rect.height() + 50);
                    canvas.save();
                    canvas.clipRect(rect1);
                    RectF colorrect = new RectF();
                    if (j > 0) colorrect.left = rect1.left;
                    else colorrect.left = rect1.left + 8;//0th column left padding
                    colorrect.right = rect1.right - 12;
                    colorrect.top = constant + (42 * k) + (3 * k);
                    colorrect.bottom = colorrect.top + 42;
                    int color = eventInfo.eventcolor == 0 ? mDefaultEventColor : eventInfo.eventcolor;
                    jeventRectPaint.setColor(color);
                    if (noofevent > 2) {

                        jeventtextpaint.setColor(Color.BLACK);
                        canvas.drawText("•••", colorrect.left + 5, colorrect.centerY() + (jeventtextpaintRect.height() / 2.0f), jeventtextpaint);

                    } else {
                        Log.e("noofevent", noofevent + "");
                        jeventtextpaint.setColor(Color.WHITE);
                        canvas.drawRoundRect(colorrect, 6, 6, jeventRectPaint);

                        canvas.drawText(eventInfo.title, colorrect.left + 5, colorrect.centerY() + (jeventtextpaintRect.height() / 2.0f), jeventtextpaint);

                    }
                    canvas.restore();
                    k++;
                    noofevent++;
                    eventInfo = eventInfo.nextnode;
                }
            }

        }
    }

    // In the SimpleOnGestureListener subclass you should override
    // onDown and any other gesture that you want to detect.
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d("TAG", "onDown: ");

            // don't return false here or else none of the other
            // gestures will work
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i("TAG", "onSingleTapConfirmed: ");
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.i("TAG", "onLongPress: ");
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i("TAG", "onDoubleTap: ");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            Log.i("TAG", "onScroll: ");
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d("TAG", "onFling: ");
            return true;
        }
    }
}
