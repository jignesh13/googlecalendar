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
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TimeZone;

import androidx.core.app.ActivityCompat;


public class Utility {


    public static HashMap<LocalDate, EventInfo> localDateHashMap = new HashMap<>();

    public static String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static HashMap<LocalDate, EventInfo> readCalendarEvent(Context context, LocalDate mintime, LocalDate maxtime) throws JSONException {


//        Cursor cursor = context.getContentResolver().query(
//                CalendarContract.Events.CONTENT_URI,
//                new String[]{"_id", "title", "description",
//                        "dtstart", "dtend", "eventLocation", "calendar_displayName", CalendarContract.Events.ALL_DAY, CalendarContract.Events.EVENT_COLOR, CalendarContract.Events.CALENDAR_COLOR, CalendarContract.Events.EVENT_TIMEZONE, CalendarContract.Events.DURATION}, null,
//                null, null);
//
//
//        cursor.moveToFirst();
        // fetching calendars name

        String data = loadJSONFromAsset(context);
        JSONObject jsonObject = new JSONObject(data);
        JSONArray jsonArray = jsonObject.getJSONArray("data");

        // fetching calendars id
        String syncacc = null;
        int count=0;
        while (count<jsonArray.length()) {

            JSONObject object = jsonArray.getJSONObject(count);


            if (true) {

                LocalDate startlocalDate = LocalDate.parse(object.getString("booking_date"), DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss"));
                LocalDate endlocalDate = LocalDate.parse(object.getString("booking_slot"), DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss"));

                if (!localDateHashMap.containsKey(startlocalDate)) {
                    EventInfo eventInfo = new EventInfo();
                    eventInfo.id = object.getInt("booking_id");
                    eventInfo.starttime = startlocalDate.toDate().getTime();
                    eventInfo.timezone = TimeZone.getDefault().getID();
                    eventInfo.endtime = endlocalDate.toDate().getTime();


                    eventInfo.eventtitles = new String[]{object.getString("service_name")};
                    eventInfo.isallday = true;
                    eventInfo.eventcolor = Color.parseColor("#009688");

                    eventInfo.title = object.getString("service_name");
                    long difference = eventInfo.endtime - eventInfo.starttime;
                    if (difference > 86400000) {
                            eventInfo.endtime = eventInfo.endtime + 86400000l;

                        LocalDateTime localDate1 = new LocalDateTime(eventInfo.starttime, DateTimeZone.forID(eventInfo.timezone)).withTime(0, 0, 0, 0);
                        LocalDateTime localDate2 = new LocalDateTime(eventInfo.endtime, DateTimeZone.forID(eventInfo.timezone)).withTime(23, 59, 59, 999);

                        int day = Days.daysBetween(localDate1, localDate2).getDays();
                        eventInfo.noofdayevent = day;
                        eventInfo.isallday = true;
                    } else if (difference < 86400000) eventInfo.noofdayevent = 0;
                    else eventInfo.noofdayevent = 1;
                    localDateHashMap.put(startlocalDate, eventInfo);


                } else {
                    EventInfo eventInfo = localDateHashMap.get(startlocalDate);
                    EventInfo prev = eventInfo;
                    while (prev.nextnode != null) prev = prev.nextnode;
                    String[] s = eventInfo.eventtitles;

                    boolean isneed = true;
                    for (int i = 0; i < s.length; i++) {
                        if (s[i].equals(object.getString("service_name"))) {

                            isneed = false;
                            break;
                        }
//                        if (i + 1 < s.length) prev = prev.nextnode;

                    }

                    if (isneed) {

                        String ss[] = Arrays.copyOf(s, s.length + 1);
                        ss[ss.length - 1] = object.getString("service_name");
                        eventInfo.eventtitles = ss;

                        EventInfo nextnode = new EventInfo();
                        nextnode.id = object.getInt("booking_id");
                        nextnode.starttime = startlocalDate.toDate().getTime();
                        nextnode.endtime = endlocalDate.toDate().getTime();
                        nextnode.isallday = true;
                        nextnode.timezone = TimeZone.getDefault().getID();
                        nextnode.title = object.getString("service_name");
//                        nextnode.accountname = cursor.getString(6);
                        nextnode.eventcolor = Color.parseColor("#009688");
                       long difference = nextnode.endtime - nextnode.starttime;

                        if (nextnode.endtime - nextnode.starttime > 86400000) {
                            eventInfo.endtime = eventInfo.endtime + 86400000l;

                            nextnode.isallday = true;
                            LocalDateTime localDate1 = new LocalDateTime(nextnode.starttime, DateTimeZone.forID(nextnode.timezone)).withTime(0, 0, 0, 0);
                            LocalDateTime localDate2 = new LocalDateTime(nextnode.endtime, DateTimeZone.forID(nextnode.timezone)).withTime(23, 59, 59, 999);


                            int day = Days.daysBetween(localDate1, localDate2).getDays();

                            nextnode.noofdayevent = day;

                        } else if (difference < 86400000) nextnode.noofdayevent = 0;
                        else nextnode.noofdayevent = 1;
                        prev.nextnode = nextnode;


                        localDateHashMap.put(startlocalDate, eventInfo);
                    }

                }
            }

            count++;

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