package com.example.GoogleCalendar;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;

public class AddEvent {
    public ArrayList<EventModel> arrayList;
    public HashMap<LocalDate, Integer> indextracker;
    public ArrayList<MonthModel> monthModels;

    public AddEvent(ArrayList<EventModel> arrayList, HashMap<LocalDate, Integer> indextracker, ArrayList<MonthModel> monthModels) {
        this.arrayList = arrayList;
        this.indextracker = indextracker;
        this.monthModels = monthModels;
    }

    public ArrayList<MonthModel> getMonthModels() {
        return monthModels;
    }

    public ArrayList<EventModel> getArrayList() {
        return arrayList;
    }

    public HashMap<LocalDate, Integer> getIndextracker() {
        return indextracker;
    }
}
