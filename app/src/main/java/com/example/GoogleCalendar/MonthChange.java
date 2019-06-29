package com.example.GoogleCalendar;

import org.joda.time.LocalDate;

public class MonthChange {
    public LocalDate mMessage;

    public MonthChange(LocalDate message) {
        mMessage = message;
    }

    public LocalDate getMessage() {
        return mMessage;
    }
}
