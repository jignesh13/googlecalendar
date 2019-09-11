package com.example.GoogleCalendar;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements MyRecyclerView.AppBarTracking {

    private MyRecyclerView mNestedView;
    private int mAppBarOffset = 0;
    public static int imonth,iyear,iday;
    private boolean mAppBarIdle = true;
    private int mAppBarMaxOffset = 0;
    MyLinearSmoothScroller smoothScroller;
    private AppBarLayout mAppBar;
    private boolean mIsExpanded = false;
    private boolean isanimationend=true;
    long lasttime;
    private ImageView mArrowImageView;
    private TextView monthname;
    private int lastdy;
    private LinearLayout mSmallLayout;
    private LinearLayout expandCollapse;
    private boolean isappbarclosed = true;
    private int month;
    public static LocalDate lastdate= LocalDate.now();
    private int expandedfirst;
    public static int topspace=0;
    private GooglecalenderView calendarView;
    private ArrayList<EventModel> eventalllist = new ArrayList();
    private HashMap<LocalDate, Integer> indextrack = new HashMap<>();
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
                   new Handler().postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           expandedfirst=val;
                           topspace=0;
                           linearLayoutManager.scrollToPositionWithOffset(val,0);
                           EventBus.getDefault().post(new MonthChange(localDate,0));
                           month=localDate.getDayOfMonth();
                           lastdate=localDate;
                       }
                   },100);
               }
               else {
                   calendarView.setCurrentmonth(new LocalDate());
                   expandedfirst=val;
                   topspace=0;
                   linearLayoutManager.scrollToPositionWithOffset(val,0);
                   EventBus.getDefault().post(new MonthChange(localDate,0));
                   month=localDate.getDayOfMonth();
                   lastdate=localDate;

               }


            }

        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calendarView=findViewById(R.id.calander);
        mNestedView = findViewById(R.id.nestedView);
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
                Log.e("change","monthmodel");
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
        Toolbar toolbar = findViewById(R.id.toolbar);
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
                   Log.e("dy",dy+"");
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
                   lastdy=pos;
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
                Log.e("offset",verticalOffset+"");
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

        LinearLayoutManager linearLayoutManager= (LinearLayoutManager) mNestedView.getLayoutManager();
        if (indextrack.containsKey(event.getMessage())){

            int index=indextrack.get(event.getMessage());
                lastdate=event.getMessage();
                expandedfirst=eventalllist.get(index).getType()==100||eventalllist.get(index).getType()==200?index+1:index;
                topspace=0;
               linearLayoutManager.scrollToPositionWithOffset(expandedfirst,0);
          }
        else {
            //lastdate=event.getMessage().dayOfWeek().withMinimumValue();
           if (lastdate.getMonthOfYear()==event.getMessage().dayOfWeek().withMinimumValue().minusDays(1).getMonthOfYear()){
               Integer ind=indextrack.get(event.getMessage().dayOfWeek().withMinimumValue().minusDays(1));
               if (eventalllist.get(ind).getType()==100||eventalllist.get(ind).getType()==200)ind++;
               expandedfirst=ind;
               topspace=0;
               linearLayoutManager.scrollToPositionWithOffset(expandedfirst,0);
            }


        }

    }

    @Subscribe//use for add
    public void onEvent(AddEvent event) {

        eventalllist=event.getArrayList();
        indextrack=event.getIndextracker();
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
                  topspace=0;
                  linearLayoutManager.scrollToPositionWithOffset(expandedfirst,0);
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
                if (position+1<eventalllist.size()&&eventalllist.get(position).getLocalDate().equals(today)&&(!eventalllist.get(position+1).getLocalDate().equals(today)||eventalllist.get(position+1).getType()==100)||eventalllist.get(position+1).getType()==200){
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
            else if (viewtype==2||viewtype==100||viewtype==200){

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
            if (viewtype==0||viewtype==2){
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

            TextView eventtextview;
            View circle,line;
            public ItemViewHolder(View itemView) {
                super(itemView);
                eventtextview=itemView.findViewById(R.id.view_item_textview);
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
