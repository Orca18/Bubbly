package com.example.bubbly.kim_util_test;

import android.util.Log;

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
        gap = (long) (gap / 1000); //
        long day = gap / 86400;
        long hour = gap / 3600;
        gap = gap % 3600;
        long min = gap / 60;
        long sec = gap % 60;

        if (hour > 24) {
            ret = day + "d";
            Log.d("시간", "time day"+ret);
            
//            if (day > 30){
            //// 몇달전
//            if () {
                //// 몇년전
//            }
//            }
            
            
        } else if (hour > 0) {
            ret = hour + "h";
        } else if (min > 0) {
            ret = min + "m";
        } else if (sec > 0) {
            ret = sec + "s";
        } else {
            ret = new SimpleDateFormat("HH").format(date);
        }
        Log.d("dateutil","dateutil: "+ret);
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
            ret = day + "d";
            Log.d("시간", "time day"+ret);
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