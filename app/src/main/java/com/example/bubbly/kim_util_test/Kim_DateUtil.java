package com.example.bubbly.kim_util_test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Kim_DateUtil {

    public static String beforeTime(Date date) {

        Calendar c = Calendar.getInstance();

        long now = c.getTimeInMillis();
        long dateM = date.getTime();

        long gap = now - dateM;

        String ret = "";

//        초       분   시
//        1000    60  60
        gap = (long) (gap / 1000);
        long hour = gap / 3600;
        gap = gap % 3600;
        long min = gap / 60;
        long sec = gap % 60;

        if (hour > 24) {
            ret = new SimpleDateFormat("HH").format(date);
        } else if (hour > 0) {
            ret = hour + "h";
        } else if (min > 0) {
            ret = min + "m";
        } else if (sec > 0) {
            ret = sec + "s";
        } else {
            ret = new SimpleDateFormat("HH").format(date);
        }
        return ret;

    }
}