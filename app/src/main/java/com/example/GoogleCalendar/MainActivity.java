package com.example.GoogleCalendar;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjiazhe.scrollparallaximageview.ScrollParallaxImageView;
import com.gjiazhe.scrollparallaximageview.parallaxstyle.VerticalMovingStyle;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.joda.time.LocalDate;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements MyRecyclerView.AppBarTracking {

    private MyRecyclerView mNestedView;
    private int mAppBarOffset = 0;
    private boolean mAppBarIdle = true;
    private int mAppBarMaxOffset = 0;
    private View shadow;
    private AppBarLayout mAppBar;
    private boolean mIsExpanded = false;
private View redlay;
    long lasttime;
    private ImageView mArrowImageView;
    private TextView monthname;
    private Toolbar toolbar;

    private int lastchangeindex=-1;
    private boolean isappbarclosed = true;
    private int month;
    public static LocalDate lastdate= LocalDate.now();
    private int expandedfirst;
    public static int topspace=0;
    private View roundrect;
    private TextView eventnametextview,eventrangetextview,holidaytextview;
    private ImageView calendaricon;
    private View eventview,fullview;
    private GooglecalenderView calendarView;
    private ArrayList<EventModel> eventalllist = new ArrayList();
    private HashMap<LocalDate, Integer> indextrack = new HashMap<>();
    private ImageButton closebtn;
    private HashMap<LocalDate, Integer> dupindextrack = new HashMap<>();
    private String[] var = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN",};
    private int[] monthresource = {
            R.drawable.bkg_01_jan,
            R.drawable.bkg_02_feb,
            R.drawable.bkg_03_mar,
            R.drawable.bkg_04_apr,
            R.drawable.bkg_05_may,
            R.drawable.bkg_06_jun,
            R.drawable.bkg_07_jul,
            R.drawable.bkg_08_aug,
            R.drawable.bkg_09_sep,
            R.drawable.bkg_10_oct,
            R.drawable.bkg_11_nov,
            R.drawable.bkg_12_dec
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.action_favorite){
            final LocalDate localDate=LocalDate.now();

           final LinearLayoutManager linearLayoutManager= (LinearLayoutManager) mNestedView.getLayoutManager();
           mNestedView.stopScroll();
            if (indextrack.containsKey(new LocalDate(localDate.getYear(),localDate.getMonthOfYear(),localDate.getDayOfMonth()))){

                final Integer val=indextrack.get(new LocalDate(localDate.getYear(),localDate.getMonthOfYear(),localDate.getDayOfMonth()));

               if (isAppBarExpanded()){
                   calendarView.setCurrentmonth(new LocalDate());
                   expandedfirst=val;
                   topspace=20;
                   linearLayoutManager.scrollToPositionWithOffset(val,20);
                   EventBus.getDefault().post(new MonthChange(localDate,0));
                   month=localDate.getDayOfMonth();
                   lastdate=localDate;
               }
               else {
                   calendarView.setCurrentmonth(new LocalDate());
                   expandedfirst=val;
                   topspace=20;
                   linearLayoutManager.scrollToPositionWithOffset(val,20);
                   EventBus.getDefault().post(new MonthChange(localDate,0));
                   month=localDate.getDayOfMonth();
                   lastdate=localDate;

               }


            }

        }
        return super.onOptionsItemSelected(item);

    }
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    public int getnavigationHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }
    public static void setTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        transparentStatusBar(activity);
        setRootView(activity);
    }
    private static void setRootView(Activity activity) {
        ViewGroup parent = (ViewGroup) activity.findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(false);
                ((ViewGroup) childView).setClipToPadding(true);
            }
        }
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void transparentStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            activity.getWindow().setNavigationBarColor(Color.parseColor("#f1f3f5"));
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private void setMargins (View view, int left, int top, int right, int bottom,int width,int height) {

        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            view.setLayoutParams(new CoordinatorLayout.LayoutParams(width,height));
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }
    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
    public void closebtnClick(){
        closebtn.setVisibility(View.GONE);
        eventnametextview.setVisibility(View.GONE);
        roundrect.setVisibility(View.GONE);
        eventrangetextview.setVisibility(View.GONE);
        calendaricon.setVisibility(View.GONE);
        holidaytextview.setVisibility(View.GONE);
        ValueAnimator animwidth = ValueAnimator.ofInt(getDevicewidth(), eventview.getWidth());
        animwidth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
                layoutParams.width = val;
                redlay.setLayoutParams(layoutParams);
            }
        });
        animwidth.setDuration(300);

        ValueAnimator animheight = ValueAnimator.ofInt(getDeviceHeight(), 0);
        animheight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
                layoutParams.height = val;
                redlay.setLayoutParams(layoutParams);
                if (redlay.getTranslationZ()!=0&&valueAnimator.getAnimatedFraction()>0.7){
                    redlay.setBackgroundResource(R.drawable.white_touch);
                    redlay.setTranslationZ(0);
                    shadow.setVisibility(View.GONE);
                }
            }
        });
        animheight.setDuration(300);

        ValueAnimator animx = ValueAnimator.ofFloat(0, eventview.getLeft());
        animx.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                Float val = (Float) valueAnimator.getAnimatedValue();

                redlay.setTranslationX(val);
            }
        });
        animx.setDuration(300);

        ValueAnimator animy = ValueAnimator.ofFloat(0,fullview.getTop()+toolbar.getHeight());

        animy.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float val = (Float) valueAnimator.getAnimatedValue();
                redlay.setTranslationY(val);
            }
        });
        animy.setDuration(300);
        animwidth.start();
        animheight.start();
        animy.start();
        animx.start();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
      redlay=findViewById(R.id.redlay);
              redlay.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        shadow=findViewById(R.id.shadow);
        closebtn=findViewById(R.id.closebtn);
        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vh) {
              closebtnClick();
            }
        });
        roundrect=findViewById(R.id.roundrect);
        eventnametextview=findViewById(R.id.textView12);
        eventrangetextview=findViewById(R.id.textView13);
        calendaricon=findViewById(R.id.imageView2);
        holidaytextview=findViewById(R.id.textView14);
        calendarView=findViewById(R.id.calander);
        calendarView.setPadding(0,getStatusBarHeight(),0,0);
        mNestedView = findViewById(R.id.nestedView);
      //  setMargins(mNestedView,0,0,0,getnavigationHeight());
        mNestedView.setAppBarTracking(this);


        final LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mNestedView.setLayoutManager(linearLayoutManager);
        DateAdapter dateAdapter=new DateAdapter();
        mNestedView.setAdapter(dateAdapter);

        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(dateAdapter);
        mNestedView.addItemDecoration(headersDecor);
        EventBus.getDefault().register(this);


         monthname=findViewById(R.id.monthname);
        calendarView.setMonthChangeListner(new MonthChangeListner() {
            @Override
            public void onmonthChange(MonthModel monthModel) {

                LocalDate localDate=new LocalDate();
                String year=monthModel.getYear()==localDate.getYear()?"":monthModel.getYear()+"";
                monthname.setText(monthModel.getMonthnamestr()+" "+year);
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_CALENDAR},200);
            }
        }
        else {
            LocalDate mintime=new LocalDate().minusYears(10);
            LocalDate maxtime=new LocalDate().plusYears(10);
            HashMap<LocalDate,String[]> eventlist=Utility.readCalendarEvent(this,mintime,maxtime);
            calendarView.init(eventlist,mintime,maxtime);
            calendarView.setCurrentmonth(new LocalDate());
            calendarView.adjustheight();

        }
        toolbar = findViewById(R.id.toolbar);
        toolbar.setPadding(0,getStatusBarHeight(),0,0);
//        expandCollapse = findViewById(R.id.expandCollapseButton);
       mArrowImageView = findViewById(R.id.arrowImageView);

       mNestedView.addOnScrollListener(new RecyclerView.OnScrollListener() {
           LinearLayoutManager llm = (LinearLayoutManager) mNestedView.getLayoutManager();
           DateAdapter dateAdapter= (DateAdapter) mNestedView.getAdapter();
           int mydy;
           private int offset = 0;
           @Override
           public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
               super.onScrollStateChanged(recyclerView, newState);

               if (isappbarclosed&&newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                  calendarView.setCurrentmonth(dateAdapter.geteventallList().get(expandedfirst).getLocalDate());
               }
           }
           @Override
           public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
               if (isappbarclosed){

                   int pos=llm.findFirstVisibleItemPosition();
                   View view=llm.findViewByPosition(pos);

                   int currentmonth=dateAdapter.geteventallList().get(pos).getLocalDate().getMonthOfYear();

                   if (dateAdapter.geteventallList().get(pos).getType()==1){


                       if (dy>0&&Math.abs(view.getTop())>100){
                           if (month!=currentmonth)EventBus.getDefault().post(new MonthChange(dateAdapter.geteventallList().get(pos).getLocalDate(),dy));
                           month=currentmonth;
                           lastdate=dateAdapter.geteventallList().get(pos).getLocalDate();
                           expandedfirst=pos;
                       }
                       else if (dy<0&&Math.abs(view.getTop())<100&&pos-1>0)
                       {


                           pos--;
                           currentmonth=dateAdapter.geteventallList().get(pos).getLocalDate().getMonthOfYear();


                           if (month!=currentmonth)EventBus.getDefault().post(new MonthChange(dateAdapter.geteventallList().get(pos).getLocalDate(),dy));
                           month=currentmonth;
                           lastdate=dateAdapter.geteventallList().get(pos).getLocalDate().dayOfMonth().withMaximumValue();
                           expandedfirst=pos;
                       }
//                       if (dy>=0){
//                           if (Math.abs(view.getTop())>100){
//                               offset=0;
//                               mydy=dy;
//                              // calendarView.setCurrentmonth(dateAdapter.geteventallList().get(pos).getLocalDate());
//                               if (month!=currentmonth)EventBus.getDefault().post(new MonthChange(dateAdapter.geteventallList().get(pos).getLocalDate()));
//                               month=currentmonth;
//
//                           }
//                           else {
//                               if (pos-1>0)firstitem=pos-1;
//                               lastdate=lastdate.minusDays(1);
//                           }
//                       }
//                       else if (dy<0){
//                            Log.e("viewtop",view.getTop()+"");
//                           if (Math.abs(view.getTop())<10){
//                               offset=0;
//                               mydy=dy;
//                              // calendarView.setCurrentmonth(dateAdapter.geteventallList().get(pos).getLocalDate());
//                               if (month!=currentmonth)EventBus.getDefault().post(new MonthChange(dateAdapter.geteventallList().get(pos).getLocalDate()));
//                               month=currentmonth;
//                           }
//                           else {
//                               if (pos+1<dateAdapter.getItemCount())firstitem=pos+1;
//
//                               lastdate=lastdate.plusDays(1);
//                           }
//                       }




                   }
                   else {
                       lastdate=dateAdapter.geteventallList().get(pos).getLocalDate();
                       expandedfirst=pos;
                   }

               }

               super.onScrolled(recyclerView, dx, dy);
           }
       });


        mAppBar = findViewById(R.id.app_bar);


        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {

            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        }



        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (mAppBarOffset!=verticalOffset){
                    mAppBarOffset = verticalOffset;
                    mAppBarMaxOffset = -mAppBar.getTotalScrollRange();
                    //calendarView.setTranslationY(mAppBarOffset);
                    //calendarView.setLayoutParams(new CollapsingToolbarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,500));
                    int totalScrollRange = appBarLayout.getTotalScrollRange();
                    float progress = (float) (-verticalOffset) / (float) totalScrollRange;
                    mArrowImageView.setRotation(progress * 180);
                    mIsExpanded = verticalOffset == 0;
                    mAppBarIdle = mAppBarOffset >= 0 || mAppBarOffset <= mAppBarMaxOffset;
                    float alpha = (float) -verticalOffset / totalScrollRange;

                    if (mAppBarOffset==-appBarLayout.getTotalScrollRange()) {
                        isappbarclosed=true;
                        setExpandAndCollapseEnabled(false);
                    }
                    else {
                        setExpandAndCollapseEnabled(true);
                    }

                    if (mAppBarOffset==0){
                        if (isappbarclosed){
                            mNestedView.stopScroll();
                            isappbarclosed=false;
                            expandedfirst=linearLayoutManager.findFirstVisibleItemPosition();
                            topspace=linearLayoutManager.findViewByPosition(linearLayoutManager.findFirstVisibleItemPosition()).getTop();
                           // linearLayoutManager.scrollToPositionWithOffset(expandedfirst,0);
                            //calendarView.setCurrentmonth(lastdate);
                            calendarView.adjustheight();
                            calendarView.update();

                        }
                    }

                }


            }
        });

        findViewById(R.id.backsupport).setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
                mIsExpanded = !mIsExpanded;
                mNestedView.stopScroll();

                mAppBar.setExpanded(mIsExpanded, true);


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==200&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            LocalDate mintime=new LocalDate().minusYears(10);
            LocalDate maxtime=new LocalDate().plusYears(10);
            HashMap<LocalDate,String[]> eventlist=Utility.readCalendarEvent(this,mintime,maxtime);
            calendarView.init(eventlist,mintime.minusYears(10),maxtime.plusYears(10));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    lastdate=new LocalDate();
                    calendarView.setCurrentmonth(new LocalDate());
                    calendarView.adjustheight();
//                    LinearLayoutManager linearLayoutManager= (LinearLayoutManager) mNestedView.getLayoutManager();
//                    if (indextrack.containsKey(new LocalDate())){
//                        smoothScroller.setTargetPosition(indextrack.get(new LocalDate()));
//                        linearLayoutManager.scrollToPositionWithOffset(indextrack.get(new LocalDate()),0);
//                    }
//                    else {
//                        for (int i=0;i<eventalllist.size();i++){
//                            if (eventalllist.get(i).getLocalDate().getMonthOfYear()==new LocalDate().getMonthOfYear()&&eventalllist.get(i).getLocalDate().getYear()==new LocalDate().getYear()){
//                                linearLayoutManager.scrollToPositionWithOffset(i,0);
//                                break;
//                            }
//                        }
//                    }
                }
            },10);
        }
    }

    @Subscribe
    public void onEvent(MonthChange event) {


       if (!isAppBarExpanded()){

           LocalDate localDate=new LocalDate();
           String year=event.getMessage().getYear()==localDate.getYear()?"":event.getMessage().getYear()+"";
           monthname.setText(event.getMessage().toString("MMMM")+" "+year);

           long diff=System.currentTimeMillis()-lasttime;
           boolean check=diff>600;
           if (check&&event.mdy>0){
               monthname.setTranslationY(35);
               mArrowImageView.setTranslationY(35);
               lasttime=System.currentTimeMillis();
               monthname.animate().translationY(0).setDuration(200).start();
               mArrowImageView.animate().translationY(0).setDuration(200).start();

           }
           else if (check&&event.mdy<0){

               monthname.setTranslationY(-35);
               mArrowImageView.setTranslationY(-35);
               lasttime=System.currentTimeMillis();
               monthname.animate().translationY(0).setDuration(200).start();
               mArrowImageView.animate().translationY(0).setDuration(200).start();
           }



       }

    }
    @Subscribe//use for scrolling
    public void onEvent(MessageEvent event) {


        int previous=lastchangeindex;
        if (previous!=-1){
            int totalremove=0;
            for (int k=1;k<=3;k++){

                if (eventalllist.get(previous).getEventname().equals("dupli")||eventalllist.get(previous).getEventname().equals("click")){
                    totalremove++;
                    EventModel eventModel =eventalllist.remove(previous);
                }
        }
           indextrack.clear();
            indextrack.putAll(dupindextrack);
            mNestedView.getAdapter().notifyDataSetChanged();

        }

        LinearLayoutManager linearLayoutManager= (LinearLayoutManager) mNestedView.getLayoutManager();
        if (indextrack.containsKey(event.getMessage())){
             int index=indextrack.get(event.getMessage());
             int type=eventalllist.get(index).getType();
             if (type==0||type==2){

                 lastdate=event.getMessage();
                 expandedfirst=index;
                 topspace=20;
                 linearLayoutManager.scrollToPositionWithOffset(expandedfirst,20);
                 lastchangeindex=-1;

             }
             else {


                 lastdate=event.getMessage();



                 Integer ind=indextrack.get(event.getMessage());
                 ind++;
                 for (int i=ind;i<eventalllist.size();i++){


                     if (event.getMessage().isBefore(eventalllist.get(i).getLocalDate())){
                         ind=i;
                         break;
                     }
                 }
                 lastchangeindex=ind;
                 int typeselect=eventalllist.get(ind+1).getType()==200?200:100;
                 if (!eventalllist.get(ind-1).getEventname().startsWith("dup")){

                     eventalllist.add(ind,new EventModel("dupli",event.getMessage(),typeselect));
                     ind++;
                 }
                 expandedfirst=ind;
                 eventalllist.add(ind,new EventModel("click",event.getMessage(),1000));
                 ind++;
                 if (!eventalllist.get(ind).getEventname().startsWith("dup")){

                     eventalllist.add(ind,new EventModel("dupli",event.getMessage(),typeselect));
                 }
                 mNestedView.getAdapter().notifyDataSetChanged();

                 topspace=20;
                 linearLayoutManager.scrollToPositionWithOffset(expandedfirst,20);

                 for (int i=lastchangeindex;i<eventalllist.size();i++){
                     if (!eventalllist.get(i).getEventname().startsWith("dup"))indextrack.put(eventalllist.get(i).getLocalDate(),i);
                 }


             }

          }
        else {
         Integer ind=indextrack.get(event.getMessage().dayOfWeek().withMinimumValue().minusDays(1));
            ind++;
            for (int i=ind;i<eventalllist.size();i++){

                if (event.getMessage().isBefore(eventalllist.get(i).getLocalDate())){
                    ind=i;
                    break;
                }
            }
            lastchangeindex=ind;
            int typeselect=eventalllist.get(ind+1).getType()==200?200:100;
           if (!eventalllist.get(ind-1).getEventname().startsWith("dup")){

               eventalllist.add(ind,new EventModel("dupli",event.getMessage(),typeselect));
               ind++;
           }
            expandedfirst=ind;

            eventalllist.add(ind,new EventModel("click",event.getMessage(),1000));
            ind++;
          if (!eventalllist.get(ind).getEventname().startsWith("dup")){

              eventalllist.add(ind,new EventModel("dupli",event.getMessage(),typeselect));
          }

            mNestedView.getAdapter().notifyDataSetChanged();
            topspace=20;
            linearLayoutManager.scrollToPositionWithOffset(expandedfirst,20);

            for (int i=lastchangeindex;i<eventalllist.size();i++){
                if (!eventalllist.get(i).getEventname().startsWith("dup"))indextrack.put(eventalllist.get(i).getLocalDate(),i);
            }

        }

    }
   private int getDeviceHeight(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        return height+getStatusBarHeight();
    }
    private int getDevicewidth(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        return width;
    }
    @Override
    public void onBackPressed() {
        if (closebtn.getVisibility()==View.VISIBLE){
            closebtnClick();
            return;
        }
        super.onBackPressed();
        finish();
    }

    @Subscribe//use for add
    public void onEvent(AddEvent event) {

        eventalllist=event.getArrayList();

        indextrack=event.getIndextracker();
        for (Map.Entry<LocalDate,Integer> entry:indextrack.entrySet()){
            dupindextrack.put(entry.getKey(),entry.getValue());
        }
      if (mNestedView.isAttachedToWindow()) {

          mNestedView.getAdapter().notifyDataSetChanged();
      }
      new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
              LocalDate localDate=new LocalDate();
              LinearLayoutManager linearLayoutManager= (LinearLayoutManager) mNestedView.getLayoutManager();
              if (indextrack.containsKey(LocalDate.now())){

                  Integer val=indextrack.get(LocalDate.now());
                  expandedfirst=val;
                  topspace=20;
                  linearLayoutManager.scrollToPositionWithOffset(expandedfirst,20);
                  EventBus.getDefault().post(new MonthChange(localDate,0));
                  month=localDate.getDayOfMonth();
                  lastdate=localDate;

              }
          }
      },100);



    }
    private void setExpandAndCollapseEnabled(boolean enabled) {

        if (mNestedView.isNestedScrollingEnabled() != enabled) {
            ViewCompat.setNestedScrollingEnabled(mNestedView,enabled);

        }
    }

    @Override
    public boolean isAppBarClosed() {
        return isappbarclosed;
    }

    @Override
    public int appbaroffset() {
        return expandedfirst;
    }

    public class DateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

        LocalDate today=LocalDate.now();
        public ArrayList<EventModel> geteventallList(){
            return eventalllist;
        }
        @Override
        public int getItemViewType(int position) {
            if (position>1&&eventalllist.get(position).getType()==0&&getHeaderId(position)==getHeaderId(position-1))return 5;
            if (position>1&&eventalllist.get(position).getType()==3&&eventalllist.get(position-1).getType()==1)return 7;
            if (position+1<eventalllist.size()&&eventalllist.get(position).getType()==3&&(eventalllist.get(position+1).getType()==1||eventalllist.get(position+1).getType()==0))return 6;
            return eventalllist.get(position).getType();
        }
        public int getHeaderItemViewType(int position) {
            return eventalllist.get(position).getType();
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType==0){

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_item, parent, false);


                return new ItemViewHolder(view);
            }
            else if (viewType==5){
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.viewitemlessspace, parent, false);
                return new ItemViewHolder(view);
            }
            else if (viewType==100){
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.extraspace, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            }
            else if (viewType==200){
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.liitlespace, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            }
            else if (viewType==1){
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.viewlast, parent, false);
                return new EndViewHolder(view);
            }
            else if (viewType==2){
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.noplanlay, parent, false);
                return new NoplanViewHolder(view);
            }
            else if (viewType==1000){
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.noplanlittlespace, parent, false);
                return new NoplanViewHolder(view);
            }

            else if (viewType==6){
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rangelayextrabottomspace, parent, false);
                return new RangeViewHolder(view);
            }
            else if (viewType==7){
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rangelayextratopspace, parent, false);
                return new RangeViewHolder(view);
            }
            else{
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rangelay, parent, false);
                return new RangeViewHolder(view);
            }

        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            int viewtype=getItemViewType(position);
            if (viewtype==0||viewtype==5){

                ItemViewHolder holder= (ItemViewHolder) viewHolder;
                holder.eventtextview.setText(eventalllist.get(position).getEventname());
                if (position+1<eventalllist.size()&&eventalllist.get(position).getLocalDate().equals(today)&&(!eventalllist.get(position+1).getLocalDate().equals(today)||eventalllist.get(position+1).getType()==100||eventalllist.get(position+1).getType()==200)){
                    holder.circle.setVisibility(View.VISIBLE);
                    holder.line.setVisibility(View.VISIBLE);

                }
                else {
                    holder.circle.setVisibility(View.GONE);
                    holder.line.setVisibility(View.GONE);
                }
            }
            else if (viewtype==1){

                EndViewHolder holder= (EndViewHolder) viewHolder;
                holder.eventimageview.setImageResource(monthresource[eventalllist.get(position).getLocalDate().getMonthOfYear()-1]);
                holder.monthname.setText(eventalllist.get(position).getLocalDate().toString("MMMM YYYY"));
            }
            else if (viewtype==2||viewtype==100||viewtype==200||viewtype==1000){

            }
            else {
                RangeViewHolder holder= (RangeViewHolder) viewHolder;
                holder.rangetextview.setText(eventalllist.get(position).getEventname().replaceAll("tojigs",""));
            }

        }

        @Override
        public long getHeaderId(int position) {


            if (eventalllist.get(position).getType()==1)return position;
            else if (eventalllist.get(position).getType()==3)return position;
           else if (eventalllist.get(position).getType()==100)return position;
            else if (eventalllist.get(position).getType()==200)return position;
            LocalDate localDate=eventalllist.get(position).getLocalDate();
            String uniquestr=""+localDate.getDayOfMonth()+localDate.getMonthOfYear()+localDate.getYear();
            return Long.parseLong(uniquestr);

        }

        @Override
        public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent,int position) {
            int viewtype=getHeaderItemViewType(position);
            if (viewtype==2){
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.todayheader, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            }
            else if (viewtype==0&&eventalllist.get(position).getLocalDate().equals(today)){
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.todayheader, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            }
            else if (viewtype==1||viewtype==3||viewtype==100||viewtype==200){
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.empty, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            }
            else {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.headerview, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            }

        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
            int viewtype=getHeaderItemViewType(position);
            if (viewtype==0||viewtype==2||viewtype==1000){
                TextView vartextView=holder.itemView.findViewById(R.id.textView9);
                TextView datetextView=holder.itemView.findViewById(R.id.textView10);
                vartextView.setText(var[eventalllist.get(position).getLocalDate().getDayOfWeek()-1]);
                datetextView.setText(eventalllist.get(position).getLocalDate().getDayOfMonth()+"");
                holder.itemView.setTag(position);
            }
            else{


            }

        }

        @Override
        public int getItemCount() {
            return eventalllist.size();
        }
        class ItemViewHolder extends RecyclerView.ViewHolder{
            String daysList[] = {"", "Monday", "Tuesday", "Wednesday",
                    "Thursday", "Friday", "Saturday","Sunday"};

            TextView eventtextview;
            View circle,line;
            public ItemViewHolder(View itemView) {
                super(itemView);
                eventtextview=itemView.findViewById(R.id.view_item_textview);
                eventtextview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (isAppBarExpanded()){
                            mIsExpanded = !mIsExpanded;
                            mNestedView.stopScroll();

                            mAppBar.setExpanded(mIsExpanded, true);
                            return;
                        }
                        eventnametextview.setText(eventalllist.get(getAdapterPosition()).getEventname());
                        LocalDate localDate=eventalllist.get(getAdapterPosition()).getLocalDate();
                        LocalDate todaydate=LocalDate.now();
                        LocalDate nextday=localDate.plusDays(1);
                        if (localDate.getYear()==todaydate.getYear()){
                            String rangetext=daysList[localDate.getDayOfWeek()]+", "+localDate.toString("d MMM")+" - "+daysList[nextday.getDayOfWeek()]+", "+nextday.toString("d MMM");
                            eventrangetextview.setText(rangetext);
                        }
                        else {
                            String rangetext=daysList[localDate.getDayOfWeek()]+", "+localDate.toString("d MMM, YYYY")+" - "+daysList[nextday.getDayOfWeek()]+", "+nextday.toString("d MMM, YYYY");
                            eventrangetextview.setText(rangetext);
                        }
                        closebtn.setVisibility(View.GONE);
                        eventnametextview.setVisibility(View.GONE);
                        roundrect.setVisibility(View.GONE);
                        eventrangetextview.setVisibility(View.GONE);
                        calendaricon.setVisibility(View.GONE);
                        holidaytextview.setVisibility(View.GONE);
                        final View view= mNestedView.getLayoutManager().findViewByPosition(getAdapterPosition());
                        ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
                        layoutParams.height = v.getHeight();
                        layoutParams.width=v.getWidth();
                        redlay.setLayoutParams(layoutParams);
                        redlay.setTranslationX(v.getLeft());
                        redlay.setTranslationY(view.getTop()+toolbar.getHeight());
                        redlay.setBackgroundResource(R.drawable.white_touch);
                        redlay.setTranslationZ(0);

                        ValueAnimator animwidth = ValueAnimator.ofInt(redlay.getWidth(), getDevicewidth());
                        animwidth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                int val = (Integer) valueAnimator.getAnimatedValue();
                                ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
                                layoutParams.width = val;
                                redlay.setLayoutParams(layoutParams);
                            }
                        });
                        animwidth.setDuration(300);

                        ValueAnimator animheight = ValueAnimator.ofInt(redlay.getHeight(), getDeviceHeight());
                        animheight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                int val = (Integer) valueAnimator.getAnimatedValue();
                                ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
                                layoutParams.height = val;
                                redlay.setLayoutParams(layoutParams);
                                if (redlay.getTranslationZ()==0&&valueAnimator.getAnimatedFraction()>0.15){
                                    redlay.setBackgroundColor(Color.WHITE);
                                    shadow.setVisibility(View.VISIBLE);
                                    redlay.setTranslationZ(getResources().getDimensionPixelSize(R.dimen.tendp));
                                }
                            }
                        });
                        animheight.setDuration(300);

                        ValueAnimator animx = ValueAnimator.ofFloat(redlay.getTranslationX(), 0);
                        animx.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                Float val = (Float) valueAnimator.getAnimatedValue();
                               redlay.setTranslationX(val);
                            }
                        });
                        animx.setDuration(300);

                        ValueAnimator animy = ValueAnimator.ofFloat(redlay.getTranslationY(), 0);
                        animy.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                Float val = (Float) valueAnimator.getAnimatedValue();
                                redlay.setTranslationY(val);
                            }
                        });
                        animy.setDuration(300);

                        animheight.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        closebtn.setVisibility(View.VISIBLE);
                                        eventnametextview.setVisibility(View.VISIBLE);
                                        roundrect.setVisibility(View.VISIBLE);
                                        eventrangetextview.setVisibility(View.VISIBLE);
                                        calendaricon.setVisibility(View.VISIBLE);
                                        holidaytextview.setVisibility(View.VISIBLE);
                                    }
                                },150);

                            }
                        });
                        animwidth.start();
                        animheight.start();
                        animy.start();
                        animx.start();
                        eventview=v;
                        fullview=view;

                    }
                });
                circle=itemView.findViewById(R.id.circle);
                line=itemView.findViewById(R.id.line);
            }
        }
        class EndViewHolder extends RecyclerView.ViewHolder{

            ScrollParallaxImageView eventimageview;
            TextView monthname;
            public EndViewHolder(View itemView) {
                super(itemView);
                eventimageview=itemView.findViewById(R.id.imageView);
                eventimageview.setParallaxStyles(new VerticalMovingStyle());
                monthname=itemView.findViewById(R.id.textView11);
            }
        }
        class NoplanViewHolder extends RecyclerView.ViewHolder{

            TextView noplantextview;
            public NoplanViewHolder(View itemView) {
                super(itemView);
                noplantextview=itemView.findViewById(R.id.view_noplan_textview);
            }
        }
        class RangeViewHolder extends RecyclerView.ViewHolder{

            TextView rangetextview;
            public RangeViewHolder(View itemView) {
                super(itemView);
                rangetextview=itemView.findViewById(R.id.view_range_textview);
            }
        }
    }
    @Override
    public boolean isAppBarExpanded() {

        return mAppBarOffset == 0;
    }


    @Override
    public boolean isAppBarIdle() {
        return mAppBarIdle;
    }

    private static final String TAG = "MainActivity";
}
