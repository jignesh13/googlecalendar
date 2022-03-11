package com.example.GoogleCalendar;

public class EventInfo {
    public String[] eventtitles;
    public boolean isallday;
    public int id;
    public String accountname;
    public int noofdayevent;
    public long starttime;
    public long endtime;
    public EventInfo nextnode;
    public String title;
    public String timezone;
    public int eventcolor;
    public boolean isalreadyset;

    public EventInfo(String[] eventtitles) {
        this.eventtitles = eventtitles;
    }

    public EventInfo() {
    }

    public EventInfo(EventInfo eventInfo) {
        this.eventtitles = eventInfo.eventtitles;
        this.isallday = eventInfo.isallday;
        this.id = eventInfo.id;
        this.accountname = eventInfo.accountname;
        this.noofdayevent = eventInfo.noofdayevent;
        this.starttime = eventInfo.starttime;
        this.endtime = eventInfo.endtime;
        this.title = eventInfo.title;
        this.timezone = eventInfo.timezone;
        this.eventcolor = eventInfo.eventcolor;
        this.isalreadyset = eventInfo.isalreadyset;
    }

}
