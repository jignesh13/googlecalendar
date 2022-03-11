package com.example.GoogleCalendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

public class JCalendarMonthTopView extends View {
    float eachcellheight, eachcellwidth;
    long lastsec;
    int selectedcell;
    private Paint paint, mHeaderTextPaint, jDateTextPaint, jeventRectPaint, jeventtextpaint, jselectrectpaint, jtodaypaint;
    private Typeface dayfont;
    private int dayHeight, daytextsize, datemargintop, linecolor, linewidth, daytextcolor, datetextsize, datetextcolor, eventtextsize;
    private Context mContext;
    private float downx, downy;
    private String dayname[] = {"S", "M", "T", "W", "T", "F", "S"};
    private Rect selectedrect;
    private boolean isup = false;
    private Rect mHeaderTextPaintRect;
    private Rect jDateTextPaintRect;
    private int numberofrow;
    private ArrayList<DayModel> dayModels = new ArrayList<>();
    private int firstday = 4;
    private int month, year;

    public JCalendarMonthTopView(Context context) {
        this(context, null);
    }

    public JCalendarMonthTopView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JCalendarMonthTopView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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


        } finally {
            a.recycle();
        }

    }

    public void initdata(ArrayList<DayModel> dayModels, int firstday, int month, int year) {
        this.dayModels = dayModels;
        this.firstday = firstday;
        this.month = month;
        this.year = year;
        requestLayout();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getY() < dayHeight + datemargintop) {
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downx = event.getX();
            downy = event.getY();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getX() == downx && event.getY() == downy) {
                int column = (int) (event.getX() / eachcellwidth);
                int row = (int) ((event.getY() - (dayHeight + datemargintop)) / eachcellheight);
                int cell = (row * 7) + column;
                if (cell >= firstday) {
                    int pos = cell - firstday;
                    if (pos < dayModels.size()) {
                        for (DayModel dayModel : dayModels) {
                            dayModel.setSelected(false);
                        }
                        MainActivity.lastdate = new LocalDate(year, month, dayModels.get(pos).getDay());
                        MainActivity mainActivity = (MainActivity) mContext;
                        if (mainActivity.mNestedView.getVisibility() == VISIBLE)
                            EventBus.getDefault().post(new MessageEvent(new LocalDate(year, month, dayModels.get(pos).getDay())));
                        if (mainActivity.weekviewcontainer.getVisibility() == VISIBLE) {
                            Calendar todaydate = Calendar.getInstance();
                            todaydate.set(Calendar.DAY_OF_MONTH, MainActivity.lastdate.getDayOfMonth());
                            todaydate.set(Calendar.MONTH, MainActivity.lastdate.getMonthOfYear() - 1);
                            todaydate.set(Calendar.YEAR, MainActivity.lastdate.getYear());
                            mainActivity.mWeekView.goToDate(todaydate);
                        }
                        invalidate();
                    }
                }


            }
            return super.onTouchEvent(event);
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int size = dayModels.size() + firstday;
        numberofrow = size % 7 == 0 ? size / 7 : (size / 7) + 1;
        int setheight = (mContext.getResources().getDimensionPixelSize(R.dimen.itemheight) * numberofrow) + dayHeight + datemargintop;

        setMeasuredDimension(widthSize, setheight);

        mHeaderTextPaintRect = new Rect();
        jDateTextPaintRect = new Rect();
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


        jeventRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        jeventRectPaint.setStyle(Paint.Style.FILL);
        jeventRectPaint.setColor(Color.parseColor("#009688"));

        jselectrectpaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        jselectrectpaint.setStyle(Paint.Style.FILL);
        jselectrectpaint.setColor(Color.parseColor("#F0F0F0"));


        jtodaypaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        jtodaypaint.setStyle(Paint.Style.FILL);
        jtodaypaint.setColor(getResources().getColor(R.color.selectday));


//        Log.e("height",rect.toString());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int size = dayModels.size() + firstday;
        numberofrow = size % 7 == 0 ? size / 7 : (size / 7) + 1;

        eachcellheight = (getHeight() - (dayHeight + datemargintop)) / numberofrow;
        eachcellwidth = getWidth() / 7;

        if (selectedrect != null) {
            canvas.drawRect(selectedrect, jselectrectpaint);
        }
        float[] point = new float[4];

        float begining = (dayHeight + datemargintop);

// for draw line
//        for (int i = 0; i < 7; i++) {
//
//            if (i < 6) {
//                point[0] = 0;
//                point[1] = begining;
//                point[2] = getWidth();
//                point[3] = begining;
//                canvas.drawLines(point, paint);
//            }
//            point[0] = eachcellwidth + eachcellwidth * i;
//            point[1] = dayHeight / 1.5f;
//            point[2] = eachcellwidth + eachcellwidth * i;
//            point[3] = getHeight();
//            canvas.drawLines(point, paint);
//            begining = begining + eachcellheight;
//
//        }
        for (int i = 0; i < numberofrow; i++) {
            for (int j = 0; j < 7; j++) {


                if (i == 0) {

                    canvas.drawText(dayname[j], (eachcellwidth * j + eachcellwidth / 2.0f) - mHeaderTextPaintRect.right / 2.0f, dayHeight - mHeaderTextPaintRect.height(), mHeaderTextPaint);
                }
                int position = (i * 7) + j;


                if (position >= firstday) {
                    position = position - firstday;
                    if (position >= dayModels.size()) continue;
                    DayModel dayModel = dayModels.get(position);
                    boolean selected = dayModel.getDay() == MainActivity.lastdate.getDayOfMonth() && dayModel.getMonth() == MainActivity.lastdate.getMonthOfYear() && dayModel.getYear() == MainActivity.lastdate.getYear() ? true : false;

                    String ss = dayModels.get(position).getDay() + "";

                    jDateTextPaint.getTextBounds(ss, 0, ss.length(), jDateTextPaintRect);
                    if (dayModel.isToday() || selected) {//istoday
                        float centerx = ((eachcellwidth * j) + eachcellwidth / 2.0f);
                        float centery = datemargintop + dayHeight + (i * eachcellheight) + eachcellheight / 2.0f;
                        float max = getResources().getDimensionPixelSize(R.dimen.circlesize);

                        if (dayModel.isToday()) {
                            jtodaypaint.setColor(getResources().getColor(R.color.selectday));
                            jDateTextPaint.setColor(Color.WHITE);
                        } else {

                            jtodaypaint.setColor(Color.parseColor("#4D5B80E7"));
                            jDateTextPaint.setColor(Color.rgb(91, 128, 231));
                        }
                        canvas.drawRoundRect(centerx - max, centery - max, centerx + max, centery + max, max, max, jtodaypaint);
                    } else {
                        jDateTextPaint.setColor(datetextcolor);//date enable color
                    }

                    canvas.drawText(ss, ((eachcellwidth * j) + eachcellwidth / 2.0f), datemargintop + dayHeight + (i * eachcellheight) + eachcellheight / 2.0f + jDateTextPaintRect.height() / 2.0f, jDateTextPaint);

                    if (dayModel.getEventlist() && !selected) {//event day
                        jtodaypaint.setColor(getResources().getColor(R.color.event_color_04));
                        float centerx = ((eachcellwidth * j) + eachcellwidth / 2.0f);
                        float centery = datemargintop + dayHeight + (i * eachcellheight) + eachcellheight / 2.0f + jDateTextPaintRect.height() + 8;
                        canvas.drawRoundRect(centerx - 5, centery - 5, centerx + 5, centery + 5, 5, 5, jtodaypaint);

                    }

                }


            }

        }
    }
}
