package com.example.GoogleCalendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.joda.time.LocalDate;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

public class YearView extends View {
    private String dayname[] = { "S","M", "T", "W", "T", "F", "S"};
    private Context mContext;
    private Paint daypaint, mHeaderTextPaint,datepaint;
    private Rect mHeaderTextPaintRect,mdayrect,mdaterect;
   private String monthname="Jan";
   private int startofweek, month, noofday,year;
    public YearView(Context context) {
        super(context);
    }

    public YearView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YearView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public YearView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {

        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
    }
    public void updateYearView(int year){

        this.year=year;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeaderTextPaintRect = new Rect();
        mHeaderTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHeaderTextPaint.setColor(Color.BLACK);
        mHeaderTextPaint.setTypeface(ResourcesCompat.getFont(mContext, R.font.googlesansmed));
        mHeaderTextPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.smalltextsize));


        mdayrect = new Rect();
        daypaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        daypaint.setColor(Color.GRAY);
        daypaint.setTypeface(ResourcesCompat.getFont(mContext, R.font.googlesans_regular));
        daypaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.daytextsize));
        daypaint.getTextBounds("S", 0, "S".length(), mdayrect);

        mdaterect = new Rect();
        datepaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        datepaint.setColor(Color.BLACK);
        datepaint.setTypeface(ResourcesCompat.getFont(mContext, R.font.googlesans_regular));
        datepaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.daytextsize));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float eachmonthwidth=((getWidth()-30)/3.0f);
        float eachmonthheight=(getHeight()-30)/4.0f;
        for(int m=0;m<4;m++){
            for(int n=0;n<3;n++){

                LocalDate localDate=new LocalDate(year,(m*3)+(n+1),1);
                startofweek = localDate.dayOfMonth().withMinimumValue().dayOfWeek().get();
                if (startofweek == 7) startofweek = 0;
                month=(m*3)+n;
                monthname=localDate.toString("MMMM");
                noofday=localDate.dayOfMonth().getMaximumValue();
                mHeaderTextPaint.getTextBounds(monthname, 0, monthname.length(), mHeaderTextPaintRect);

                float eachcellsize = (eachmonthwidth-30)/7.0f;
                //30 because of margin left each month
                canvas.drawText(monthname,30+(eachmonthwidth*n)+(eachcellsize/2.0f)-(mdayrect.width()/2.0f),30+(eachmonthheight*m)+mHeaderTextPaintRect.height(),mHeaderTextPaint);
                //20 is topwidth
                for (int i=0;i<7;i++){
                    canvas.drawText(dayname[i],30+(eachmonthwidth*n)+(eachcellsize*i)+(eachcellsize/2.0f)-(mdayrect.width()/2.0f),30+(eachmonthheight*m)+mHeaderTextPaintRect.height()+mdayrect.height()+20,daypaint);
                }
                //   20 is datestartheight
                float endofdayheight=30+(eachmonthheight*m)+mHeaderTextPaintRect.height()+mdayrect.height()+20+20;
                float remainingheight=(eachmonthheight*(m+1))-endofdayheight;
                float eachcellheight=(remainingheight-30)/6.0f;
                int startday=1;
                for(int i=0;i<6;i++){
                    for(int j=0;j<7;j++){
                        int dateindex=((i*7)+j);
                        if(dateindex<startofweek||startday>noofday)continue;
                        String text=startday+"";
                        Log.e("Text",text);
                        datepaint.getTextBounds(text, 0, text.length(), mdaterect);
                        canvas.drawText(text,30+(eachmonthwidth*n)+(eachcellsize*j)+(eachcellsize/2.0f)-(mdaterect.width()/2.0f),endofdayheight+(eachcellheight*i)+(eachcellheight/2.0f)+(mdaterect.height()/2.0f),datepaint);
                        startday++;
                    }
                }
            }
        }
//        float eachcellsize = eachmonthwidth/7.0f;
//        canvas.drawText(monthname,(eachcellsize/2.0f)-(mdayrect.width()/2.0f),mHeaderTextPaintRect.height(),mHeaderTextPaint);
//       //20 is topwidth
//        for (int i=0;i<7;i++){
//            canvas.drawText(dayname[i],(eachcellsize*i)+(eachcellsize/2.0f)-(mdayrect.width()/2.0f),mHeaderTextPaintRect.height()+mdayrect.height()+20,daypaint);
//           }
//        //20 is datestartheight
//        float endofdayheight=mHeaderTextPaintRect.height()+mdayrect.height()+20+20;
//        float remainingheight=getHeight()-endofdayheight;
//        float eachcellheight=remainingheight/6.0f;
//        int startday=1;
//        for(int i=0;i<6;i++){
//            for(int j=0;j<7;j++){
//                int dateindex=((i*7)+j);
//                if(dateindex<startofweek||startday>noofday)continue;
//                String text=startday+"";
//                Log.e("Text",text);
//                datepaint.getTextBounds(text, 0, text.length(), mdaterect);
//                canvas.drawText(text,(eachcellsize*j)+(eachcellsize/2.0f)-(mdaterect.width()/2.0f),endofdayheight+(eachcellheight*i)+(eachcellheight/2.0f)+(mdaterect.height()/2.0f),datepaint);
//                startday++;
//            }
//        }

    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        float eachmonthsize=getWidth()/3.0f;
//        for(int m=0;m<4;m++){
//            for(int n=0;n<3;n++){
//
//            }
//        }
//        float eachcellsize = getWidth()/7.0f;
//        canvas.drawText(monthname,(eachcellsize/2.0f)-(mdayrect.width()/2.0f),mHeaderTextPaintRect.height(),mHeaderTextPaint);
//        //20 is topwidth
//        for (int i=0;i<7;i++){
//            canvas.drawText(dayname[i],(eachcellsize*i)+(eachcellsize/2.0f)-(mdayrect.width()/2.0f),mHeaderTextPaintRect.height()+mdayrect.height()+20,daypaint);
//        }
//        //20 is datestartheight
//        float endofdayheight=mHeaderTextPaintRect.height()+mdayrect.height()+20+20;
//        float remainingheight=getHeight()-endofdayheight;
//        float eachcellheight=remainingheight/6.0f;
//        int startday=1;
//        for(int i=0;i<6;i++){
//            for(int j=0;j<7;j++){
//                int dateindex=((i*7)+j);
//                if(dateindex<startofweek||startday>noofday)continue;
//                String text=startday+"";
//                Log.e("Text",text);
//                datepaint.getTextBounds(text, 0, text.length(), mdaterect);
//                canvas.drawText(text,(eachcellsize*j)+(eachcellsize/2.0f)-(mdaterect.width()/2.0f),endofdayheight+(eachcellheight*i)+(eachcellheight/2.0f)+(mdaterect.height()/2.0f),datepaint);
//                startday++;
//            }
//        }
//
//    }

}
