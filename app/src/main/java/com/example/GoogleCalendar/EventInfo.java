package com.example.GoogleCalendar;

public class EventInfo {
    public String[] eventtitles;
    public boolean isallday;
    public int id;
    public String accountname;
    public long starttime;
    public long endtime;
    public EventInfo nextnode;
    public String title;
    public String timezone;
    public int eventcolor;
    public EventInfo(String[] eventtitles){
        this.eventtitles=eventtitles;
    }
    public EventInfo(){
    }
}
