package com.example.GoogleCalendar;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.DateUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.regex.Pattern;

public class ReadCalendar {
    static Cursor cursor;

    public static void readCalendar(Context context) {

        ContentResolver contentResolver = context.getContentResolver();

        // Fetch a list of all calendars synced with the device, their display names and whether the

        cursor = contentResolver.query(Uri.parse("content://com.android.calendar/calendars"),
                (new String[]{"_id", "displayName", "selected"}), null, null, null);

        HashSet<String> calendarIds = new HashSet<String>();

        try {
            System.out.println("Count=" + cursor.getCount());
            if (cursor.getCount() > 0) {
                System.out.println("the control is just inside of the cursor.count loop");
                while (cursor.moveToNext()) {

                    String _id = cursor.getString(0);
                    String displayName = cursor.getString(1);
                    Boolean selected = !cursor.getString(2).equals("0");

                    System.out.println("Id: " + _id + " Display Name: " + displayName + " Selected: " + selected);
                    calendarIds.add(_id);
                }
            }
        } catch (AssertionError ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        // For each calendar, display all the events from the previous week to the end of next week.        
        for (String id : calendarIds) {
            Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
            //Uri.Builder builder = Uri.parse("content://com.android.calendar/calendars").buildUpon();
            long now = new Date().getTime();

            ContentUris.appendId(builder, now - DateUtils.DAY_IN_MILLIS * 10000);
            ContentUris.appendId(builder, now + DateUtils.DAY_IN_MILLIS * 10000);

            Cursor eventCursor = contentResolver.query(builder.build(),
                    new String[]{"title", "begin", "end", "allDay"}, "Calendars._id=" + 1,
                    null, "startDay ASC, startMinute ASC");

            System.out.println("eventCursor count=" + eventCursor.getCount());
            if (eventCursor.getCount() > 0) {

                if (eventCursor.moveToFirst()) {
                    do {
                        Object mbeg_date, beg_date, beg_time, end_date, end_time;

                        final String title = eventCursor.getString(0);
                        final Date begin = new Date(eventCursor.getLong(1));
                        final Date end = new Date(eventCursor.getLong(2));
                        final Boolean allDay = !eventCursor.getString(3).equals("0");

                        /*  System.out.println("Title: " + title + " Begin: " + begin + " End: " + end +
                                    " All Day: " + allDay);
                        */
                        System.out.println("Title:" + title);
                        System.out.println("Begin:" + begin);
                        System.out.println("End:" + end);
                        System.out.println("All Day:" + allDay);

                        /* the calendar control metting-begin events Respose  sub-string (starts....hare) */

                        Pattern p = Pattern.compile(" ");
                        String[] items = p.split(begin.toString());
                        String scalendar_metting_beginday, scalendar_metting_beginmonth, scalendar_metting_beginyear, scalendar_metting_begindate, scalendar_metting_begintime, scalendar_metting_begingmt;

                        scalendar_metting_beginday = items[0];
                        scalendar_metting_beginmonth = items[1];
                        scalendar_metting_begindate = items[2];
                        scalendar_metting_begintime = items[3];
                        scalendar_metting_begingmt = items[4];
                        scalendar_metting_beginyear = items[5];


                        String calendar_metting_beginday = scalendar_metting_beginday;
                        String calendar_metting_beginmonth = scalendar_metting_beginmonth.toString().trim();

                        int calendar_metting_begindate = Integer.parseInt(scalendar_metting_begindate.trim());

                        String calendar_metting_begintime = scalendar_metting_begintime.toString().trim();
                        String calendar_metting_begingmt = scalendar_metting_begingmt;
                        int calendar_metting_beginyear = Integer.parseInt(scalendar_metting_beginyear.trim());


                        System.out.println("calendar_metting_beginday=" + calendar_metting_beginday);

                        System.out.println("calendar_metting_beginmonth =" + calendar_metting_beginmonth);

                        System.out.println("calendar_metting_begindate =" + calendar_metting_begindate);

                        System.out.println("calendar_metting_begintime=" + calendar_metting_begintime);

                        System.out.println("calendar_metting_begingmt =" + calendar_metting_begingmt);

                        System.out.println("calendar_metting_beginyear =" + calendar_metting_beginyear);

                        /* the calendar control metting-begin events Respose  sub-string (starts....ends) */

                        /* the calendar control metting-end events Respose  sub-string (starts....hare) */

                        Pattern p1 = Pattern.compile(" ");
                        String[] enditems = p.split(end.toString());
                        String scalendar_metting_endday, scalendar_metting_endmonth, scalendar_metting_endyear, scalendar_metting_enddate, scalendar_metting_endtime, scalendar_metting_endgmt;

                        scalendar_metting_endday = enditems[0];
                        scalendar_metting_endmonth = enditems[1];
                        scalendar_metting_enddate = enditems[2];
                        scalendar_metting_endtime = enditems[3];
                        scalendar_metting_endgmt = enditems[4];
                        scalendar_metting_endyear = enditems[5];


                        String calendar_metting_endday = scalendar_metting_endday;
                        String calendar_metting_endmonth = scalendar_metting_endmonth.toString().trim();

                        int calendar_metting_enddate = Integer.parseInt(scalendar_metting_enddate.trim());

                        String calendar_metting_endtime = scalendar_metting_endtime.toString().trim();
                        String calendar_metting_endgmt = scalendar_metting_endgmt;
                        int calendar_metting_endyear = Integer.parseInt(scalendar_metting_endyear.trim());


                        System.out.println("calendar_metting_beginday=" + calendar_metting_endday);

                        System.out.println("calendar_metting_beginmonth =" + calendar_metting_endmonth);

                        System.out.println("calendar_metting_begindate =" + calendar_metting_enddate);

                        System.out.println("calendar_metting_begintime=" + calendar_metting_endtime);

                        System.out.println("calendar_metting_begingmt =" + calendar_metting_endgmt);

                        System.out.println("calendar_metting_beginyear =" + calendar_metting_endyear);

                        /* the calendar control metting-end events Respose  sub-string (starts....ends) */

                        System.out.println("only date begin of events=" + begin.getDate());
                        System.out.println("only begin time of events=" + begin.getHours() + ":" + begin.getMinutes() + ":" + begin.getSeconds());


                        System.out.println("only date begin of events=" + end.getDate());
                        System.out.println("only begin time of events=" + end.getHours() + ":" + end.getMinutes() + ":" + end.getSeconds());

                        beg_date = begin.getDate();
                        mbeg_date = begin.getDate() + "/" + calendar_metting_beginmonth + "/" + calendar_metting_beginyear;
                        beg_time = begin.getHours();

                        System.out.println("the vaule of mbeg_date=" + mbeg_date.toString().trim());
                        end_date = end.getDate();
                        end_time = end.getHours();


                    }
                    while (eventCursor.moveToNext());
                }
            }
            break;
        }
    }
}