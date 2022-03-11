package com.example.GoogleCalendar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.Arrays;
import java.util.HashMap;

import androidx.core.app.ActivityCompat;


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
                        "dtstart", "dtend", "eventLocation", "calendar_displayName", CalendarContract.Events.ALL_DAY, CalendarContract.Events.EVENT_COLOR, CalendarContract.Events.CALENDAR_COLOR, CalendarContract.Events.EVENT_TIMEZONE, CalendarContract.Events.DURATION}, null,
                null, null);


        cursor.moveToFirst();
        // fetching calendars name


        // fetching calendars id
        String syncacc = null;
        while (cursor.moveToNext()) {

            syncacc = cursor.getString(6);

            if (true) {
                Log.e(cursor.getString(1), cursor.getInt(7) + "");
                LocalDate localDate = getDate(Long.parseLong(cursor.getString(3)));

                if (!localDateHashMap.containsKey(localDate)) {
                    EventInfo eventInfo = new EventInfo();
                    eventInfo.id = cursor.getInt(0);
                    eventInfo.starttime = cursor.getLong(3);
                    eventInfo.endtime = cursor.getLong(4);
                    if (cursor.getString(11) != null)
                        eventInfo.endtime = eventInfo.starttime + RFC2445ToMilliseconds(cursor.getString(11));
                    eventInfo.accountname = cursor.getString(6);
                    eventInfo.timezone = cursor.getString(10);
                    eventInfo.eventtitles = new String[]{cursor.getString(1)};
                    eventInfo.isallday = cursor.getInt(7) == 1 ? true : false;
                    eventInfo.title = cursor.getString(1);
                    eventInfo.eventcolor = cursor.getInt(8) == 0 ? Color.parseColor("#009688") : cursor.getInt(8);
                    long difference = eventInfo.endtime - eventInfo.starttime;
                    if (difference > 86400000) {
                        if (cursor.getInt(7) == 0) {
                            eventInfo.endtime = eventInfo.endtime + 86400000l;
                        }

                        LocalDateTime localDate1 = new LocalDateTime(eventInfo.starttime, DateTimeZone.forID(eventInfo.timezone)).withTime(0, 0, 0, 0);
                        LocalDateTime localDate2 = new LocalDateTime(eventInfo.endtime, DateTimeZone.forID(eventInfo.timezone)).withTime(23, 59, 59, 999);

                        int day = Days.daysBetween(localDate1, localDate2).getDays();
                        eventInfo.noofdayevent = day;
                        eventInfo.isallday = true;
                    } else if (difference < 86400000) eventInfo.noofdayevent = 0;
                    else eventInfo.noofdayevent = 1;


                    localDateHashMap.put(localDate, eventInfo);


                } else {
                    EventInfo eventInfo = localDateHashMap.get(localDate);
                    EventInfo prev = eventInfo;
                    while (prev.nextnode != null) prev = prev.nextnode;
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
                        nextnode.starttime = cursor.getLong(3);
                        nextnode.endtime = cursor.getLong(4);
                        if (cursor.getString(11) != null)
                            nextnode.endtime = nextnode.starttime + RFC2445ToMilliseconds(cursor.getString(11));

                        nextnode.isallday = cursor.getInt(7) == 1 ? true : false;
                        nextnode.timezone = cursor.getString(10);
                        nextnode.title = cursor.getString(1);
                        nextnode.accountname = cursor.getString(6);
                        nextnode.eventcolor = cursor.getInt(8) == 0 ? Color.parseColor("#009688") : cursor.getInt(8);
                        long difference = nextnode.endtime - nextnode.starttime;

                        if (nextnode.endtime - nextnode.starttime > 86400000) {
                            if (cursor.getInt(7) == 0) {
                                nextnode.endtime = nextnode.endtime + 86400000l;
                            }
                            nextnode.isallday = true;
                            LocalDateTime localDate1 = new LocalDateTime(nextnode.starttime, DateTimeZone.forID(nextnode.timezone)).withTime(0, 0, 0, 0);
                            LocalDateTime localDate2 = new LocalDateTime(nextnode.endtime, DateTimeZone.forID(nextnode.timezone)).withTime(23, 59, 59, 999);


                            int day = Days.daysBetween(localDate1, localDate2).getDays();

                            nextnode.noofdayevent = day;

                        } else if (difference < 86400000) nextnode.noofdayevent = 0;
                        else nextnode.noofdayevent = 1;
                        prev.nextnode = nextnode;


                        localDateHashMap.put(localDate, eventInfo);
                    }

                }
            }


        }

        return localDateHashMap;
    }

    public static long RFC2445ToMilliseconds(String str) {


        if (str == null || str.isEmpty())
            throw new IllegalArgumentException("Null or empty RFC string");

        int sign = 1;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        int len = str.length();
        int index = 0;
        char c;

        c = str.charAt(0);

        if (c == '-') {
            sign = -1;
            index++;
        } else if (c == '+')
            index++;

        if (len < index)
            return 0;

        c = str.charAt(index);

        if (c != 'P')
            throw new IllegalArgumentException("Duration.parse(str='" + str + "') expected 'P' at index=" + index);

        index++;
        c = str.charAt(index);
        if (c == 'T')
            index++;

        int n = 0;
        for (; index < len; index++) {
            c = str.charAt(index);

            if (c >= '0' && c <= '9') {
                n *= 10;
                n += ((int) (c - '0'));
            } else if (c == 'W') {
                weeks = n;
                n = 0;
            } else if (c == 'H') {
                hours = n;
                n = 0;
            } else if (c == 'M') {
                minutes = n;
                n = 0;
            } else if (c == 'S') {
                seconds = n;
                n = 0;
            } else if (c == 'D') {
                days = n;
                n = 0;
            } else if (c == 'T') {
            } else
                throw new IllegalArgumentException("Duration.parse(str='" + str + "') unexpected char '" + c + "' at index=" + index);
        }

        long factor = 1000 * sign;
        long result = factor * ((7 * 24 * 60 * 60 * weeks)
                + (24 * 60 * 60 * days)
                + (60 * 60 * hours)
                + (60 * minutes)
                + seconds);

        return result;
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