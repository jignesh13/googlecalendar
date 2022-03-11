package com.example.GoogleCalendar.weekview;

import android.util.Log;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.GoogleCalendar.weekview.WeekViewUtil.isSameDay;

/**
 * Created by Raquib-ul-Alam Kanak on 7/21/2014.
 * Website: http://april-shower.com
 * and modify some code by jignesh khunt for https://github.com/jignesh13/googlecalendar
 */
public class WeekViewEvent {
    private long mId;
    private Calendar mStartTime, actualstart;
    private Calendar mEndTime, actualend;
    private String mName;
    private String mLocation;
    private String accountname;
    private int mColor;
    private boolean mAllDay;
    private int daytype;
    private boolean ismoreday;
    private long noofday;
    private boolean alreadyset;
    private int myday;

    public WeekViewEvent() {

    }

    /**
     * Initializes the event for week view.
     *
     * @param id          The id of the event.
     * @param name        Name of the event.
     * @param startYear   Year when the event starts.
     * @param startMonth  Month when the event starts.
     * @param startDay    Day when the event starts.
     * @param startHour   Hour (in 24-hour format) when the event starts.
     * @param startMinute Minute when the event starts.
     * @param endYear     Year when the event ends.
     * @param endMonth    Month when the event ends.
     * @param endDay      Day when the event ends.
     * @param endHour     Hour (in 24-hour format) when the event ends.
     * @param endMinute   Minute when the event ends.
     */
    public WeekViewEvent(long id, String name, int startYear, int startMonth, int startDay, int startHour, int startMinute, int endYear, int endMonth, int endDay, int endHour, int endMinute) {
        this.mId = id;

        this.mStartTime = Calendar.getInstance();
        this.mStartTime.set(Calendar.YEAR, startYear);
        this.mStartTime.set(Calendar.MONTH, startMonth - 1);
        this.mStartTime.set(Calendar.DAY_OF_MONTH, startDay);
        this.mStartTime.set(Calendar.HOUR_OF_DAY, startHour);
        this.mStartTime.set(Calendar.MINUTE, startMinute);

        this.mEndTime = Calendar.getInstance();
        this.mEndTime.set(Calendar.YEAR, endYear);
        this.mEndTime.set(Calendar.MONTH, endMonth - 1);
        this.mEndTime.set(Calendar.DAY_OF_MONTH, endDay);
        this.mEndTime.set(Calendar.HOUR_OF_DAY, endHour);
        this.mEndTime.set(Calendar.MINUTE, endMinute);

        this.mName = name;
    }

    /**
     * Initializes the event for week view.
     *
     * @param id        The id of the event.
     * @param name      Name of the event.
     * @param location  The location of the event.
     * @param startTime The time when the event starts.
     * @param endTime   The time when the event ends.
     * @param allDay    Is the event an all day event.
     */
    public WeekViewEvent(long id, String name, String location, Calendar startTime, Calendar endTime, boolean allDay, String accountname) {
        this.mId = id;
        this.mName = name;
        this.mLocation = location;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.mAllDay = allDay;
        this.accountname = accountname;
    }

    /**
     * Initializes the event for week view.
     *
     * @param id        The id of the event.
     * @param name      Name of the event.
     * @param location  The location of the event.
     * @param startTime The time when the event starts.
     * @param endTime   The time when the event ends.
     */
    public WeekViewEvent(long id, String name, String location, Calendar startTime, Calendar endTime, String accountname) {
        this(id, name, location, startTime, endTime, false, accountname);
    }

    /**
     * Initializes the event for week view.
     *
     * @param id        The id of the event.
     * @param name      Name of the event.
     * @param startTime The time when the event starts.
     * @param endTime   The time when the event ends.
     */
    public WeekViewEvent(long id, String name, Calendar startTime, Calendar endTime, String accountname) {
        this(id, name, null, startTime, endTime, accountname);
    }

    public int getMyday() {
        return myday;
    }

    public void setMyday(int myday) {
        this.myday = myday;
    }

    public boolean isAlreadyset() {
        return alreadyset;
    }

    public void setAlreadyset(boolean alreadyset) {
        this.alreadyset = alreadyset;
    }

    public Calendar getActualend() {
        return actualend;
    }

    public void setActualend(Calendar actualend) {
        this.actualend = actualend;
    }

    public Calendar getActualstart() {
        return actualstart;
    }

    public void setActualstart(Calendar actualstart) {
        this.actualstart = actualstart;
    }

    public String getAccountname() {
        return accountname;
    }

    public void setAccountname(String accountname) {
        this.accountname = accountname;
    }

    public long getNoofday() {
        return noofday;
    }

    public void setNoofday(long noofday) {
        this.noofday = noofday;
    }

    public boolean isIsmoreday() {
        return ismoreday;
    }

    public void setIsmoreday(boolean ismoreday) {
        this.ismoreday = ismoreday;
    }

    public int getDaytype() {
        return daytype;
    }

    public void setDaytype(int daytype) {
        this.daytype = daytype;
    }

    public Calendar getStartTime() {
        return mStartTime;
    }

    public void setStartTime(Calendar startTime) {
        this.mStartTime = startTime;
    }

    public Calendar getEndTime() {
        return mEndTime;
    }

    public void setEndTime(Calendar endTime) {
        this.mEndTime = endTime;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        this.mLocation = location;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        this.mColor = color;
    }

    public boolean isAllDay() {
        return mAllDay;
    }

    public void setAllDay(boolean allDay) {
        this.mAllDay = allDay;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WeekViewEvent that = (WeekViewEvent) o;

        return mId == that.mId;

    }

    @Override
    public int hashCode() {
        return (int) (mId ^ (mId >>> 32));
    }

    public List<WeekViewEvent> splitWeekViewEvents() {
        //This function splits the WeekViewEvent in WeekViewEvents by day
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        // The first millisecond of the next day is still the same day. (no need to split events for this).
        Calendar endTime = (Calendar) this.getEndTime().clone();
        endTime.add(Calendar.MILLISECOND, -1);

        if (!isSameDay(this.getStartTime(), endTime)) {
            Log.e("jmore" + this.getName(), new LocalDate(getStartTime()) + "," + new LocalDate(this.getEndTime()));

            long remainingDays = Math.round((float) (endTime.getTimeInMillis() - getStartTime().getTimeInMillis()) / (24 * 60 * 60 * 1000));
            endTime = (Calendar) this.getStartTime().clone();
            endTime.set(Calendar.HOUR_OF_DAY, 23);
            endTime.set(Calendar.MINUTE, 59);
            int k = 1;
            WeekViewEvent event1 = new WeekViewEvent(this.getId(), this.getName(), this.getLocation(), this.getStartTime(), endTime, this.isAllDay(), this.accountname);
            event1.setIsmoreday(true);
            event1.setDaytype(k);
            event1.setActualstart(this.getStartTime());
            event1.setActualend(this.getEndTime());
            event1.setNoofday(remainingDays);
            event1.setColor(this.getColor());
            events.add(event1);

            // Add other days.
            Calendar otherDay = (Calendar) this.getStartTime().clone();
            otherDay.add(Calendar.DATE, 1);
            Log.e("jtestbefore", this.getName() + new LocalDate(otherDay.getTimeInMillis()));
            while (!isSameDay(otherDay, this.getEndTime())) {
                Calendar overDay = (Calendar) otherDay.clone();
                overDay.set(Calendar.HOUR_OF_DAY, 0);
                overDay.set(Calendar.MINUTE, 0);
                Calendar endOfOverDay = (Calendar) overDay.clone();
                endOfOverDay.set(Calendar.HOUR_OF_DAY, 23);
                endOfOverDay.set(Calendar.MINUTE, 59);
                WeekViewEvent eventMore = new WeekViewEvent(this.getId(), this.getName(), null, overDay, endOfOverDay, this.isAllDay(), this.accountname);

                eventMore.setColor(this.getColor());
                eventMore.setIsmoreday(true);
                eventMore.setActualstart(this.getStartTime());
                eventMore.setActualend(this.getEndTime());
                k++;
                eventMore.setDaytype(k);
                eventMore.setNoofday(remainingDays);
                events.add(eventMore);

                // Add next day.
                otherDay.add(Calendar.DATE, 1);
            }
//
            // Add last day.
//            Calendar startTime = (Calendar) this.getEndTime().clone();
//            startTime.set(Calendar.HOUR_OF_DAY, 0);
//            startTime.set(Calendar.MINUTE, 0);
//
//            WeekViewEvent event2 = new WeekViewEvent(this.getId(), this.getName(), this.getLocation(), startTime, this.getEndTime(), this.isAllDay());
//            event2.setColor(this.getColor());
//            event2.setIsmoreday(true);
//            event2.setNoofday(remainingDays);
//            k++;
//            event2.setDaytype(k);
//            events.add(event2);
        } else {
            events.add(this);
        }

        return events;
    }
}
