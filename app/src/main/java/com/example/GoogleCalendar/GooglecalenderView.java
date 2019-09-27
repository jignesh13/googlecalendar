package com.example.GoogleCalendar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Months;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GooglecalenderView extends LinearLayout {
    private Context context;
    private ViewPager viewPager;
    private MonthChangeListner monthChangeListner;
    private int currentmonth=0;
    private LocalDate mindate,maxdate;
    private HashMap<LocalDate,String[]> eventuser=new HashMap<>();

    public void setMonthChangeListner(MonthChangeListner monthChangeListner) {
        this.monthChangeListner = monthChangeListner;
    }

    public GooglecalenderView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.viewpagerlay, this);
        this.context=context;


    }

    public GooglecalenderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.viewpagerlay, this);
        this.context=context;



    }

    public GooglecalenderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.viewpagerlay, this);
        this.context=context;



    }

    public GooglecalenderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LayoutInflater.from(context).inflate(R.layout.viewpagerlay, this);
        this.context=context;


    }
    public void setCurrentmonth(LocalDate currentmonth){
        if (currentmonth==null)return;

        LocalDate mindateobj=mindate.toDateTimeAtStartOfDay().dayOfMonth().withMinimumValue().toLocalDate();
        LocalDate current=currentmonth.dayOfMonth().withMaximumValue();
        int months= Months.monthsBetween(mindateobj,current).getMonths();
        if (viewPager.getCurrentItem()!=months){
            viewPager.setCurrentItem(months,false);
            viewPager.getAdapter().notifyDataSetChanged();
        }


    }
   public void init(HashMap<LocalDate,String[]> eventhashmap,LocalDate mindate,LocalDate maxdate){
        eventuser=eventhashmap;
        viewPager=findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        this.mindate=mindate;
        this.maxdate=maxdate;
        DateTime mindateobj=mindate.toDateTimeAtStartOfDay();
       DateTime maxdateobj=maxdate.toDateTimeAtStartOfDay();
       int months= Months.monthsBetween(mindateobj,maxdateobj).getMonths();

       final ArrayList<MonthModel> arrayList=new ArrayList<>();
       HashMap<LocalDate,String[]> eventhash=new HashMap<>();

      for (int i=0;i<=months;i++){

          int firstday =mindateobj.dayOfMonth().withMinimumValue().dayOfWeek().get();
          if (firstday==7)firstday=0;
         int lastday=mindateobj.dayOfMonth().withMaximumValue().dayOfWeek().get();
         MonthModel month=new MonthModel();
         month.setMonthnamestr(mindateobj.toString("MMMM"));
         month.setMonth(mindateobj.getMonthOfYear());
         month.setNoofday(mindateobj.dayOfMonth().getMaximumValue());
         month.setYear(mindateobj.getYear());
          month.setFirstday(firstday);
          int currentyear=new LocalDate().getYear();
          ArrayList<DayModel> dayModelArrayList=new ArrayList<>();
          DateTime startday=mindateobj.dayOfMonth().withMinimumValue().withTimeAtStartOfDay();
          LocalDate minweek=startday.dayOfWeek().withMinimumValue().toLocalDate().minusDays(1);
          while (minweek.compareTo(startday.dayOfMonth().withMaximumValue().toLocalDate())<0){
              if (minweek.getMonthOfYear()==minweek.plusDays(6).getMonthOfYear()){
                  String lastpattern=minweek.getYear()==currentyear?"d MMM":"d MMM YYYY";

                  String s[]={"tojigs"+minweek.toString("d").toUpperCase()+" - "+minweek.plusDays(6).toString(lastpattern).toUpperCase()};

                 if (!eventhash.containsKey(minweek))eventhash.put(minweek,s);

                  minweek=minweek.plusWeeks(1);

              }
              else {

                  String lastpattern=minweek.getYear()==currentyear?"d MMM":"d MMM YYYY";
                  String s[]={"tojigs"+minweek.toString("d MMM").toUpperCase()+" - "+minweek.plusDays(6).toString(lastpattern).toUpperCase()};
                  if (!eventhash.containsKey(minweek))eventhash.put(minweek,s);

                  minweek=minweek.plusWeeks(1);
              }


          }

          for(int j=1;j<=month.getNoofday();j++){

              DayModel dayModel=new DayModel();
              dayModel.setDay(startday.getDayOfMonth());
              dayModel.setMonth(startday.getMonthOfYear());
              dayModel.setYear(startday.getYear());
              if (eventuser.containsKey(startday.toLocalDate())){
                  if (eventhash.containsKey(startday.toLocalDate())){
                      List<String> list=Arrays.asList(eventhash.get(startday.toLocalDate()));
                      list=new ArrayList<>(list);
                      for(String s:eventuser.get(startday.toLocalDate())){
                          list.add(s);
                      }
                      String[] mStringArray = new String[list.size()];
                      String[] s=list.toArray(mStringArray);
                      eventhash.put(startday.toLocalDate(),s);

                  }
                 else eventhash.put(startday.toLocalDate(),eventuser.get(startday.toLocalDate()));
                  dayModel.setEventlist(true);

              }

              if (startday.toLocalDate().equals(new LocalDate())){
                  dayModel.setToday(true);
                  currentmonth=i;
              }
              else {
                  dayModel.setToday(false);
              }
              dayModelArrayList.add(dayModel);

              if (j==1){
                  String s[]={"start"};
//                  if (eventhash.containsKey(startday.dayOfWeek().withMinimumValue().toLocalDate())&&eventhash.get(startday.dayOfWeek().withMinimumValue().toLocalDate())[0].contains("tojigs")){
//                     Log.e("remove",startday.dayOfWeek().withMinimumValue().toLocalDate()+"->"+Arrays.asList(eventhash.get(startday.dayOfWeek().withMinimumValue().toLocalDate())));
//                    eventhash.remove(startday.dayOfWeek().withMinimumValue().toLocalDate());
//                  }
                  if (eventhash.containsKey(startday.toLocalDate())){
                      List<String> list=Arrays.asList(eventhash.get(startday.toLocalDate()));
                      list=new ArrayList<>(list);
                      list.add(0,"start");
                      String[] mStringArray = new String[list.size()];
                      s=list.toArray(mStringArray);


                  }
                  eventhash.put(startday.toLocalDate(),s);
              }
//              if (j==month.getNoofday()&&i!=months){
//                  Log.e("endcount",startday.toLocalDate().toString());
//                  Log.e("end",eventhash.containsKey(startday.toLocalDate())+"");
//                  String s[]={"end"};
//                  eventhash.put(startday.toLocalDate(),s);
//              }
              startday=startday.plusDays(1);

          }
          month.setDayModelArrayList(dayModelArrayList);
          arrayList.add(month);
          mindateobj=mindateobj.plusMonths(1);

      }

        final FragmentManager fragmentManager=((AppCompatActivity)context).getSupportFragmentManager();
       final MyPagerAdapter myPagerAdapter=new MyPagerAdapter(fragmentManager,arrayList);

       viewPager.setAdapter(myPagerAdapter);
       viewPager.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
           @Override
           public void onViewAttachedToWindow(View view) {
               viewPager.setCurrentItem(currentmonth);

           }

           @Override
           public void onViewDetachedFromWindow(View view) {

           }
       });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                MainActivity mainActivity= (MainActivity) context;
                if (!mainActivity.isAppBarClosed()){
                    adjustheight();
                    EventBus.getDefault().post(new MessageEvent(new LocalDate(myPagerAdapter.monthModels.get(position).getYear(),myPagerAdapter.monthModels.get(position).getMonth(),1)));
                    myPagerAdapter.getFirstFragments().get(position).updategrid();
                    if (monthChangeListner!=null)monthChangeListner.onmonthChange(myPagerAdapter.monthModels.get(position));


                }
//                if (myPagerAdapter.getFirstFragments().get(position).isVisible()){
//                    myPagerAdapter.getFirstFragments().get(position).updategrid(arrayList.get(position).getDayModelArrayList());
//                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        LocalDate todaydate=LocalDate.now();
        if (!eventhash.containsKey(todaydate)){

            eventhash.put(todaydate,new String[]{"todaydate"});
        }
        else {

            List<String> list=Arrays.asList(eventhash.get(todaydate));
            list=new ArrayList<>(list);

            boolean b=true;

            list.add("todaydate");

            String[] mStringArray = new String[list.size()];
            eventhash.put(todaydate,list.toArray(mStringArray));
        }
       Map<LocalDate,String[]> treeMap = new TreeMap<LocalDate,String[]>(eventhash);
       HashMap<LocalDate,Integer> indextrack=new HashMap<>();
       int i=0;
       ArrayList<EventModel> eventModelslist=new ArrayList<>();
        for (HashMap.Entry<LocalDate,String[]> localDateStringEntry:treeMap.entrySet()){
           for(String s:localDateStringEntry.getValue()){
               if (s==null)continue;
               int type=0;
               if (s.startsWith("todaydate"))type=2;
               else if (s.equals("start"))type=1;
               else if (s.contains("jigs"))type=3;
               if (type==2&&eventModelslist.get(eventModelslist.size()-1).getType()==0&&eventModelslist.get(eventModelslist.size()-1).getLocalDate().equals(localDateStringEntry.getKey())){

               }
               else {
                   if (type==0&&eventModelslist.size()>0&&eventModelslist.get(eventModelslist.size()-1).getType()==0&&!eventModelslist.get(eventModelslist.size()-1).getLocalDate().equals(localDateStringEntry.getKey())){

                       eventModelslist.add(new EventModel("dup",localDateStringEntry.getKey(),100));
                      // if (!indextrack.containsKey(localDateStringEntry.getKey()))indextrack.put(localDateStringEntry.getKey(),i);
                       i++;
                   }
                   else if ((type==3)&&eventModelslist.size()>0&&eventModelslist.get(eventModelslist.size()-1).getType()==0){
                       eventModelslist.add(new EventModel("dup",eventModelslist.get(eventModelslist.size()-1).getLocalDate(),100));
                    //   if (!indextrack.containsKey(localDateStringEntry.getKey()))indextrack.put(localDateStringEntry.getKey(),i);
                       i++;
                   }

                   else if ((type==1)&&eventModelslist.size()>0&&eventModelslist.get(eventModelslist.size()-1).getType()==0){
                       eventModelslist.add(new EventModel("dup",eventModelslist.get(eventModelslist.size()-1).getLocalDate(),200));
                      // if (!indextrack.containsKey(localDateStringEntry.getKey()))indextrack.put(localDateStringEntry.getKey(),i);
                       i++;
                   }
                   else if (type==0&&eventModelslist.size()>0&&(eventModelslist.get(eventModelslist.size()-1).getType()==1)){
                       eventModelslist.add(new EventModel("dup",localDateStringEntry.getKey(),200));
                       //if (!indextrack.containsKey(localDateStringEntry.getKey()))indextrack.put(localDateStringEntry.getKey(),i);
                       i++;
                   }

                   else if (type==2&&eventModelslist.size()>0&&eventModelslist.get(eventModelslist.size()-1).getType()==0){
                       eventModelslist.add(new EventModel("dup",eventModelslist.get(eventModelslist.size()-1).getLocalDate(),100));
                     //  if (!indextrack.containsKey(localDateStringEntry.getKey()))indextrack.put(localDateStringEntry.getKey(),i);
                       i++;
                   }

                   eventModelslist.add(new EventModel(s,localDateStringEntry.getKey(),type));
                   indextrack.put(localDateStringEntry.getKey(),i);
                   i++;
               }


//               if (type==2){
//                   if (eventModelslist.get(eventModelslist.size()-1).getType()!=0){
//                       eventModelslist.add(new EventModel(s,localDateStringEntry.getKey(),type));
//                       if (!indextrack.containsKey(localDateStringEntry.getKey()))indextrack.put(localDateStringEntry.getKey(),i);
//                       i++;
//                   }
//               }
//               else {
//                   eventModelslist.add(new EventModel(s,localDateStringEntry.getKey(),type));
//                   if (!indextrack.containsKey(localDateStringEntry.getKey()))indextrack.put(localDateStringEntry.getKey(),i);
//                   i++;
//               }


           }
        }
       EventBus.getDefault().post(new AddEvent(eventModelslist,indextrack));
    }
    public void update(){
        final MyPagerAdapter myPagerAdapter= (MyPagerAdapter) viewPager.getAdapter();
        if (myPagerAdapter!=null) {
            final int position = viewPager.getCurrentItem();
            myPagerAdapter.getFirstFragments().get(position).updategrid();
        }
    }
    public void adjustheight(){
       final MyPagerAdapter myPagerAdapter= (MyPagerAdapter) viewPager.getAdapter();
        if (myPagerAdapter!=null){
           final int position=viewPager.getCurrentItem();
            int size=myPagerAdapter.monthModels.get(position).getDayModelArrayList().size()+myPagerAdapter.monthModels.get(position).getFirstday();
            int numbercolumn=size%7==0?size/7:(size/7)+1;
            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = 65+(context.getResources().getDimensionPixelSize(R.dimen.itemheight)*numbercolumn)+context.getResources().getDimensionPixelSize(R.dimen.tendp)+getStatusBarHeight();
            setLayoutParams(params);


        }

    }
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    public  class MyPagerAdapter extends FragmentStatePagerAdapter {
        private ArrayList<MonthModel> monthModels;
        private ArrayList<FirstFragment> firstFragments=new ArrayList<>();

        public ArrayList<MonthModel> getMonthModels() {
            return monthModels;
        }

        public ArrayList<FirstFragment> getFirstFragments() {
            return firstFragments;
        }

        public MyPagerAdapter(FragmentManager fragmentManager, ArrayList<MonthModel> monthModels) {

            super(fragmentManager);
            this.monthModels=monthModels;
            for (int i=0;i<monthModels.size();i++){
                firstFragments.add(FirstFragment.newInstance(monthModels.get(i).getMonth(),monthModels.get(i).getYear(),monthModels.get(i).getFirstday(),monthModels.get(i).getDayModelArrayList()));
            }
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return monthModels.size();
        }


        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {

            return firstFragments.get(position);

        }

        // Returns the page title for the top indicator




}
}
