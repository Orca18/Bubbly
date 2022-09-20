package com.mainnet.bubbly.kim_util_test;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Kim_DateUtil {

    public static String beforeTime(Date date) {

        Calendar c = Calendar.getInstance();

        long now = c.getTimeInMillis();
        Log.d("dateutil", "now: " + now);
        long dateM = date.getTime();
        Log.d("dateutil", "dateM: " + dateM);

        long gap = now - dateM;

        String ret = "";

//        초       분   시
//        1000    60  60
        gap = (long) (gap / 1000);
        Log.d("dateutil", "gap: " + gap);
        long day = gap / 86400;
        Log.d("dateutil", "day: " + day);
        long hour = gap / 3600;
        Log.d("dateutil", "hour: " + hour);
        gap = gap % 3600;
        Log.d("dateutil", "gap: " + gap);
        long min = gap / 60;
        Log.d("dateutil", "min: " + min);
        long sec = gap % 60;
        Log.d("dateutil", "sec: " + sec);

        if (hour > 24) {
            ret = day + "일 전";
            Log.d("시간", "time day" + ret);

            if (day >= 7) {
                ret = day / 7 + "주 전";
            }

        } else if (hour > 0) {
            ret = hour + "시간 전";
        } else if (min > 0) {
            ret = min + "분 전";
        } else if (sec > 0) {
            ret = sec + "초 전";
        } else {
            ret = new SimpleDateFormat("HH").format(date);
        }
        Log.d("시간", "주 전" + ret+"/day: "+day);
        Log.d("시간", "주 전2" + ret+"/day: "+day%7);

        Log.d("dateutil", "dateutil: " + ret);
        return ret;

    }


    public static String beforeTime_reply(Date date) {

        Calendar c = Calendar.getInstance();

        long now = c.getTimeInMillis();
        long dateM = date.getTime();

        long gap = now - dateM;

        String ret = "";

//        초       분   시
//        1000    60  60
        gap = (long) (gap / 1000);
        long day = gap / 86400;
        long hour = gap / 3600;
        gap = gap % 3600;
        long min = gap / 60;
        long sec = gap % 60;

        if (hour > 24) {
            ret = day + "일 전";
        } else if (hour > 0) {
            ret = hour + "시간 전";
        } else if (min > 0) {
            ret = min + "분 전";
        } else if (sec > 0) {
            ret = sec + "초 전";
        } else {
            ret = new SimpleDateFormat("HH").format(date);
        }
        return ret;

    }
}