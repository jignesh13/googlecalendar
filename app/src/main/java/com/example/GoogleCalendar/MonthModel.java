package com.example.GoogleCalendar;

import java.util.ArrayList;

public class MonthModel {
    private String monthnamestr;
    private int month;
    private int year;
    private int noofday;
    private int noofweek;
    private ArrayList<DayModel> dayModelArrayList;
    private int firstday;

    public int getFirstday() {
        return firstday;
    }

    public void setFirstday(int firstday) {
        this.firstday = firstday;
    }

    public ArrayList<DayModel> getDayModelArrayList() {
        return dayModelArrayList;
    }

    public void setDayModelArrayList(ArrayList<DayModel> dayModelArrayList) {
        this.dayModelArrayList = dayModelArrayList;
    }

    public String getMonthnamestr() {
        return monthnamestr;
    }

    public void setMonthnamestr(String monthnamestr) {
        this.monthnamestr = monthnamestr;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getNoofday() {
        return noofday;
    }

    public void setNoofday(int noofday) {
        this.noofday = noofday;
    }

    public int getNoofweek() {
        return noofweek;
    }

    public void setNoofweek(int noofweek) {
        this.noofweek = noofweek;
    }
}
