package com.example.GoogleCalendar;

public class DayModel {
    private int month;
    private int day;
    private int year;
    private boolean today;
    private String[] events;
    private int noofdayevent;
    private EventInfo eventInfo;
    private boolean selected;
    private boolean eventlist;
    private boolean isenable;

    public EventInfo getEventInfo() {
        return eventInfo;
    }

    public void setEventInfo(EventInfo eventInfo) {
        this.eventInfo = eventInfo;
    }

    public int getNoofdayevent() {
        return noofdayevent;
    }

    public void setNoofdayevent(int noofdayevent) {
        this.noofdayevent = noofdayevent;
    }

    public boolean getEventlist() {
        return eventlist;
    }

    public void setEventlist(boolean event) {
        this.eventlist = event;
    }

    public String[] getEvents() {
        return events;
    }

    public void setEvents(String[] events) {
        this.events = events;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isToday() {
        return today;
    }

    public void setToday(boolean today) {
        this.today = today;
    }

    public boolean isenable() {
        return isenable;
    }

    public void setIsenable(boolean isenable) {
        this.isenable = isenable;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return day + "/" + month + "/" + year;
    }
}
