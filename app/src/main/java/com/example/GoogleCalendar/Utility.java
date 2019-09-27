package com.example.GoogleCalendar;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class Utility {


    public static HashMap<LocalDate, String[]> localDateHashMap = new HashMap<>();

    public static HashMap<LocalDate, String[]> readCalendarEvent(Context context, LocalDate mintime, LocalDate maxtime) {


        String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + mintime.toDateTimeAtStartOfDay().getMillis() + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + maxtime.toDateTimeAtStartOfDay().getMillis() + " ))";
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

        Cursor cursor = context.getContentResolver()
                .query(
                        CalendarContract.Events.CONTENT_URI,
                        new String[]{"_id", "title", "description",
                                "dtstart", "dtend", "eventLocation", "account_name"}, selection,
                        null, null);

        cursor.moveToFirst();
        // fetching calendars name
        String CNames[] = new String[cursor.getCount()];

        // fetching calendars id
      String syncacc=null;
        while (cursor.moveToNext()){

            if (syncacc==null)syncacc=cursor.getString(6);
            if (cursor.getString(6).equals(syncacc)){
                LocalDate localDate=getDate(Long.parseLong(cursor.getString(3)));
                if (!localDateHashMap.containsKey(localDate)){
                    Log.e("location",cursor.getString(5)+"f");
                    localDateHashMap.put(localDate,new String[]{cursor.getString(1)});
                }
                else {
                    String[] s=localDateHashMap.get(localDate);
                    boolean isneed=true;
                    for (int i=0;i<s.length;i++){
                        if (s[i].equals(cursor.getString(1))){

                            isneed=false;
                            break;
                        }
                    }
                    if (isneed){
                        String ss[]= Arrays.copyOf(s,s.length+1);
                        ss[ss.length-1]=cursor.getString(1);
                        Log.e("location",cursor.getString(5)+"f");
                        localDateHashMap.put(localDate,ss);
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