package com.nathanaelg.jasmine.date;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class TimeStamp {
    public static String getCurrentTimeStamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd, yyyy hh:mm a");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        return simpleDateFormat.format(Calendar.getInstance().getTime());
    }
}
