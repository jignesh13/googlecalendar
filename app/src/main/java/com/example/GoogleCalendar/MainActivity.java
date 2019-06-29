package com.example.GoogleCalendar;

import android.Manifest;
import android.content.pm.PackageManager;
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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
    private boolean mAppBarIdle = true;
    private int mAppBarMaxOffset = 0;
    MyLinearSmoothScroller smoothScroller;
    private AppBarLayout mAppBar;
    private boolean mIsExpanded = false;
    private ImageView mArrowImageView;
    private TextView monthname;
    private LinearLayout mSmallLayout;
    private LinearLayout expandCollapse;
    private boolean isappbarclosed = true;
    private int month;
    private LocalDate lastdate;
    private int expandedfirst;
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
                           linearLayoutManager.scrollToPositionWithOffset(val,0);
                           EventBus.getDefault().post(new MonthChange(localDate));
                           month=localDate.getDayOfMonth();
                           lastdate=localDate;
                       }
                   },100);
               }
               else {
                   expandedfirst=val;
                   linearLayoutManager.scrollToPositionWithOffset(val,0);
                   EventBus.getDefault().post(new MonthChange(localDate));
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
        smoothScroller = new MyLinearSmoothScroller(this) {
            @Override protected int getVerticalSnapPreference() {
                return MyLinearSmoothScroller.SNAP_TO_START;
            }
        };

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
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
           public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
               if (isappbarclosed){
                   int pos=llm.findFirstVisibleItemPosition();

                   View view=llm.findViewByPosition(pos);
                   int currentmonth=dateAdapter.geteventallList().get(pos).getLocalDate().getMonthOfYear();
                   if (currentmonth!=month&&Math.abs(view.getTop())>100){
                       offset=0;
                       mydy=dy;
                       EventBus.getDefault().post(new MonthChange(dateAdapter.geteventallList().get(pos).getLocalDate()));
                       month=currentmonth;
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
                            isappbarclosed=false;
                            calendarView.setCurrentmonth(lastdate);
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

        lastdate=event.getMessage();
       if (!isAppBarExpanded()){
           LocalDate localDate=new LocalDate();
           String year=event.getMessage().getYear()==localDate.getYear()?"":event.getMessage().getYear()+"";
           monthname.setText(event.getMessage().toString("MMMM")+" "+year);

       }

    }
    @Subscribe//use for scrolling
    public void onEvent(MessageEvent event) {
        LinearLayoutManager linearLayoutManager= (LinearLayoutManager) mNestedView.getLayoutManager();
        if (indextrack.containsKey(event.getMessage())){
                expandedfirst=indextrack.get(event.getMessage());
               linearLayoutManager.scrollToPositionWithOffset(indextrack.get(event.getMessage()),0);
          }
        else {
            Integer ind=indextrack.get(event.getMessage().dayOfWeek().withMinimumValue());
            expandedfirst=ind;
            linearLayoutManager.scrollToPositionWithOffset(ind,0);

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
                  linearLayoutManager.scrollToPositionWithOffset(expandedfirst,0);
                  EventBus.getDefault().post(new MonthChange(localDate));
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
           return eventalllist.get(position).getType();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType==0){
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_item, parent, false);
                return new ItemViewHolder(view);
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
            if (viewtype==0){
                ItemViewHolder holder= (ItemViewHolder) viewHolder;
                holder.eventtextview.setText(eventalllist.get(position).getEventname());
            }
            else if (viewtype==1){

                EndViewHolder holder= (EndViewHolder) viewHolder;
                holder.eventimageview.setImageResource(monthresource[eventalllist.get(position).getLocalDate().getMonthOfYear()-1]);
                holder.monthname.setText(eventalllist.get(position).getLocalDate().toString("MMMM YYYY"));
            }
            else if (viewtype==2){

            }
            else {
                RangeViewHolder holder= (RangeViewHolder) viewHolder;
                holder.rangetextview.setText(eventalllist.get(position).getEventname().replaceAll("tojigs",""));
            }

        }

        @Override
        public long getHeaderId(int position) {


            if (eventalllist.get(position).getEventname().equals("start"))return position;
            else if (eventalllist.get(position).getEventname().contains("tojigs"))return position;
            LocalDate localDate=eventalllist.get(position).getLocalDate();
            String uniquestr=""+localDate.getDayOfMonth()+localDate.getMonthOfYear()+localDate.getYear();
            return Long.parseLong(uniquestr);

        }

        @Override
        public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent,int position) {
            if (getItemViewType(position)==2){
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.todayheader, parent, false);
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
            int viewtype=getItemViewType(position);
            if (viewtype==0||viewtype==2){
                TextView vartextView=holder.itemView.findViewById(R.id.textView9);
                TextView datetextView=holder.itemView.findViewById(R.id.textView10);
                vartextView.setText(var[eventalllist.get(position).getLocalDate().getDayOfWeek()-1]);
                datetextView.setText(eventalllist.get(position).getLocalDate().getDayOfMonth()+"");
                holder.itemView.setTag(position);
            }
            else {
                TextView vartextView=holder.itemView.findViewById(R.id.textView9);
                TextView datetextView=holder.itemView.findViewById(R.id.textView10);
                vartextView.setText("");
                datetextView.setText("");

            }

        }

        @Override
        public int getItemCount() {
            return eventalllist.size();
        }
        class ItemViewHolder extends RecyclerView.ViewHolder{

            TextView eventtextview;
            public ItemViewHolder(View itemView) {
                super(itemView);
                eventtextview=itemView.findViewById(R.id.view_item_textview);
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
