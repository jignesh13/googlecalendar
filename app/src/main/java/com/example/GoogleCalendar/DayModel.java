package com.example.GoogleCalendar;

public class DayModel {
    private int month;
    private int day;
    private int year;
    private boolean today;
    private boolean selected;
    private boolean eventlist;

    public boolean getEventlist() {
        return eventlist;
    }

    public void setEventlist(boolean event) {
        this.eventlist = event;
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
    public boolean isIsenable() {
        return isenable;
    }

    public void setIsenable(boolean isenable) {
        this.isenable = isenable;
    }

    private boolean isenable;

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

}
