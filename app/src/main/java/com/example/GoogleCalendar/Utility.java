package com.example.GoogleCalendar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.LocalDate;

import java.util.Arrays;

import java.util.HashMap;
import java.util.List;

import androidx.core.app.ActivityCompat;
import me.everything.providers.android.calendar.Calendar;
import me.everything.providers.android.calendar.CalendarProvider;
import me.everything.providers.android.calendar.Event;
import me.everything.providers.android.calendar.Instance;
import me.everything.providers.core.Data;


public class Utility {


    public static HashMap<LocalDate, EventInfo> localDateHashMap = new HashMap<>();

    public static HashMap<LocalDate, EventInfo> readCalendarEvent(Context context, LocalDate mintime, LocalDate maxtime) {

//        CalendarProvider calendarProvider = new CalendarProvider(context);
//
//        List<Calendar> calendars = calendarProvider.getCalendars().getList();
//        for(int i=0;i<calendars.size();i++){
//            List<Event> calendars1 = calendarProvider.getEvents(calendars.get(i).id).getList();
//            for (Event event:calendars1) {
//                Log.e("name"+calendars.get(i).id,event.title+","+event.eventColor+","+calendars.get(i).calendarColor+","+event.calendarColor);
//            }
//
//        }
        int f = 1;
        String selection = "(( " + CalendarContract.Events.SYNC_EVENTS + " = " + f + " ) AND ( " + CalendarContract.Events.DTSTART + " >= " + mintime.toDateTimeAtStartOfDay().getMillis() + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + maxtime.toDateTimeAtStartOfDay().getMillis() + " ))";
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
//        int ff=0;
//      Cursor cursor1=  context.getContentResolver().query(CalendarContract.Colors.CONTENT_URI,new String[]{"color","color_index","color_type","account_name"},null,null);
//        cursor1.moveToFirst();
//        while (cursor1.moveToNext()) {
//        Log.e("str"+ff,cursor1.getString(0)+","+cursor1.getString(1)+","+cursor1.getString(2)+","+cursor1.getString(3));
//ff++;
//        }
        Cursor cursor = context.getContentResolver().query(
                CalendarContract.Events.CONTENT_URI,
                new String[]{"_id", "title", "description",
                        "dtstart", "dtend", "eventLocation", "account_name", CalendarContract.Events.ALL_DAY, CalendarContract.Events.EVENT_COLOR, CalendarContract.Events.CALENDAR_COLOR, CalendarContract.Events.EVENT_TIMEZONE}, null,
                null, null);


        cursor.moveToFirst();
        // fetching calendars name


        // fetching calendars id
        String syncacc = null;
        while (cursor.moveToNext()) {

            syncacc = cursor.getString(6);

            if (true) {
                LocalDate localDate = getDate(Long.parseLong(cursor.getString(3)));
                Log.e("Acc", cursor.getString(1) + "," + syncacc + "," + cursor.getInt(8) + "," + localDate);
                if (!localDateHashMap.containsKey(localDate)) {
                    EventInfo eventInfo = new EventInfo();
                    eventInfo.id = cursor.getInt(0);
                    eventInfo.starttime = cursor.getLong(3);
                    eventInfo.endtime = cursor.getLong(4);
                    eventInfo.isallday = cursor.getInt(7) == 1 ? true : false;
                    eventInfo.eventtitles = new String[]{cursor.getString(1)};
                    eventInfo.title = cursor.getString(1);
                    eventInfo.timezone = cursor.getString(10);
                    eventInfo.eventcolor = cursor.getInt(8)==0? Color.parseColor("#009688"):cursor.getInt(8);

                    localDateHashMap.put(localDate, eventInfo);


                } else {
                    EventInfo eventInfo = localDateHashMap.get(localDate);
                    EventInfo prev = eventInfo;
                    while (prev.nextnode!=null)prev=prev.nextnode;
                    String[] s = eventInfo.eventtitles;

                    boolean isneed = true;
                    for (int i = 0; i < s.length; i++) {
                        if (s[i].equals(cursor.getString(1))) {

                            isneed = false;
                            break;
                        }
//                        if (i + 1 < s.length) prev = prev.nextnode;

                    }

                    if (isneed) {

                        String ss[] = Arrays.copyOf(s, s.length + 1);
                        ss[ss.length - 1] = cursor.getString(1);
                        eventInfo.eventtitles = ss;

                        EventInfo nextnode = new EventInfo();
                        nextnode.id = cursor.getInt(0);
                        nextnode.starttime = Long.parseLong(cursor.getString(3));
                        nextnode.endtime = Long.parseLong(cursor.getString(4));
                        nextnode.isallday = cursor.getInt(7) == 1 ? true : false;
                        nextnode.title = cursor.getString(1);
                        nextnode.timezone = cursor.getString(10);
                        nextnode.eventcolor = cursor.getInt(8)==0? Color.parseColor("#009688"):cursor.getInt(8);
                        prev.nextnode = nextnode;


                        localDateHashMap.put(localDate, eventInfo);
                    }

                }
            }


        }

        return localDateHashMap;
    }

    public static void getDataFromCalendarTable(Context context) {
        Cursor cur = null;
        ContentResolver cr = context.getContentResolver();

        String[] mProjection =
                {
                        CalendarContract.Calendars.ALLOWED_ATTENDEE_TYPES,
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                        CalendarContract.Calendars.CALENDAR_LOCATION,
                        CalendarContract.Calendars.CALENDAR_TIME_ZONE,
                        CalendarContract.Calendars._ID
                };

        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[]{"jigneshkhunt13@gmail.com", "jigneshkhunt13@gmail.com",
                "jigneshkhunt13@gmail.com"};


        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        cur = cr.query(uri, mProjection, null, null, null);

        while (cur.moveToNext()) {
            String displayName = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME));
            String accountName = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME));


        }

    }

    public static LocalDate getDate(long milliSeconds) {
        Instant instantFromEpochMilli
                = Instant.ofEpochMilli(milliSeconds);
        return instantFromEpochMilli.toDateTime(DateTimeZone.getDefault()).toLocalDate();

    }
}