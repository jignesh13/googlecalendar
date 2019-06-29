package com.example.GoogleCalendar;

import android.support.annotation.NonNull;

import org.joda.time.LocalDate;

public class EventModel implements Comparable<EventModel> {
    private String eventname;
    private LocalDate localDate;

    public EventModel(String eventname, LocalDate localDate, int type) {
        this.eventname = eventname;
        this.localDate = localDate;
        this.type = type;
    }

    private int type;

    public int getType() {
        return type;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public String getEventname() {
        return eventname;
    }


    public void setEventname(String eventname) {
        this.eventname = eventname;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    @Override
    public int compareTo(@NonNull EventModel eventModel) {
        return localDate.compareTo(eventModel.localDate);
    }
}
