package com.mainnet.bubbly.kim_util_test;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Kim_DateUtil_Cre {

    public static String creTime(String from) throws ParseException {

        // get_Post_Cre 받아서
        // 아래 포맷에 맞게 해석
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date to = transFormat.parse(from);


        // 해석한 Date 형식을
        // 지정한 형식으로 다시 변경
        SimpleDateFormat transFormat2 = new SimpleDateFormat("yyyy년 MM월 dd일ㆍHH:mm a", Locale.KOREA);
        String to2 = transFormat2.format(to);

        Log.d("디버그태그", "날짜 변환 : "+to2);

        return to2;

    }

}
