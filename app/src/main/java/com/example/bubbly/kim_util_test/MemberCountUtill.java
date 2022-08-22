package com.example.bubbly.kim_util_test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MemberCountUtill {

    public static String countnumber(long count) {

        String count_text = "";

        if(count >= 1000){
            count_text = (count/1000)+"천명명";
        }

        return count_text;

    }
}