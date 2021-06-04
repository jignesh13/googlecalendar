package com.example.GoogleCalendar;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.util.Log;

import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.LocalDate;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import androidx.core.app.ActivityCompat;



public class Utility {


    public static HashMap<LocalDate, EventInfo> localDateHashMap = new HashMap<>();

    public static HashMap<LocalDate, EventInfo> readCalendarEvent(Context context, LocalDate mintime, LocalDate maxtime) {


        int f=1;
        String selection = "(( " + CalendarContract.Events.SYNC_EVENTS + " = " + f +" ) AND ( " + CalendarContract.Events.DTSTART + " >= " + mintime.toDateTimeAtStartOfDay().getMillis() + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + maxtime.toDateTimeAtStartOfDay().getMillis() + " ))";
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Cursor cursor = context.getContentResolver().query(
                CalendarContract.Events.CONTENT_URI,
                new String[]{"_id", "title", "description",
                        "dtstart", "dtend", "eventLocation", "account_name",CalendarContract.Events.ALL_DAY,CalendarContract.Events.DISPLAY_COLOR, CalendarContract.Events.DISPLAY_COLOR,CalendarContract.Events.EVENT_TIMEZONE}, selection,
                null, null);


        cursor.moveToFirst();
        // fetching calendars name


        // fetching calendars id
        String syncacc = null;
        while (cursor.moveToNext()) {


            syncacc = cursor.getString(6);




            if (true) {
                LocalDate localDate = getDate(Long.parseLong(cursor.getString(3)));
                Log.e("Acc",cursor.getString(1)+","+Integer.toHexString(cursor.getInt(8))+","+cursor.getString(9)+","+localDate);
                if (!localDateHashMap.containsKey(localDate)) {
                    EventInfo eventInfo=new EventInfo();
                    eventInfo.id=cursor.getInt(0);
                    eventInfo.starttime=cursor.getLong(3);
                    eventInfo.endtime=cursor.getLong(4);
                    eventInfo.isallday=cursor.getInt(7)==1?true:false;
                    eventInfo.eventtitles = new String[]{cursor.getString(1)};
                    eventInfo.title=cursor.getString(1);
                    eventInfo.timezone=cursor.getString(10);
                    eventInfo.eventcolor=cursor.getInt(8);
                    localDateHashMap.put(localDate, eventInfo);



                } else {
                    EventInfo eventInfo= localDateHashMap.get(localDate);
                    EventInfo prev=eventInfo;
                    String[] s =eventInfo.eventtitles;

                    boolean isneed = true;
                    for (int i = 0; i < s.length; i++) {
                        if (s[i].equals(cursor.getString(1))) {

                            isneed = false;
                            break;
                        }
                        if (i+1<s.length)prev=prev.nextnode;

                    }

                    if (isneed) {

                        String ss[] = Arrays.copyOf(s, s.length + 1);
                        ss[ss.length - 1] = cursor.getString(1);
                        Log.e("location", cursor.getString(5) + "f");
                        eventInfo.eventtitles=ss;

                        EventInfo nextnode=new EventInfo();
                        nextnode.id=cursor.getInt(0);
                        nextnode.starttime=Long.parseLong(cursor.getString(3));
                        nextnode.endtime=Long.parseLong(cursor.getString(4));
                        nextnode.isallday=cursor.getInt(7)==1?true:false;
                        nextnode.title=cursor.getString(1);
                        nextnode.timezone=cursor.getString(10);
                        nextnode.eventcolor=cursor.getInt(8);
                        prev.nextnode=nextnode;


                        localDateHashMap.put(localDate, eventInfo);
                    }

                }
            }


        }

        return localDateHashMap;
    }

    public static LocalDate getDate(long milliSeconds) {
        Instant instantFromEpochMilli
                = Instant.ofEpochMilli(milliSeconds);
        return instantFromEpochMilli.toDateTime(DateTimeZone.getDefault()).toLocalDate();

    }
}