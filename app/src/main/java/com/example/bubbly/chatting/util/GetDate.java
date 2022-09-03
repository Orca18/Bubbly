package com.example.bubbly.chatting.util;

import android.content.Intent;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class GetDate {
    // 오늘날짜 출력 yyyymmdd
    public static String getTodayDate(){
        String pattern = "yyyyMMdd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.KOREA);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        String date = simpleDateFormat.format(new Date());
        return date;
    }

    // 오늘날짜 출력 yyyymmdd
    public static String getTodayDateWithSlash(){
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.KOREA);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        String date = simpleDateFormat.format(new Date());
        return date;
    }

    // 오늘날짜 출력 yyyy-MM-dd HH:mm
    public static String getTodayDateWithTime(){
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.KOREA);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        String date = simpleDateFormat.format(new Date());
        return date;
    }

    // 특정날짜 출력 yyyy년 MM월 dd일 (요일)
    public static String getDateWithYMDAndWeekDay(){
        String dateOfYYYYMMDD = getTodayDate();
        String year = dateOfYYYYMMDD.substring(0,4);
        String month = dateOfYYYYMMDD.substring(4,6);
        String day = dateOfYYYYMMDD.substring(6,8);

        // 요일
        String weekDay = getDayOfWeek(dateOfYYYYMMDD);

        return year + "년 " + month + "월 " + day + "일" + "(" + weekDay + ")";
    }

    // 특정날짜 출력 yyyy.MM.dd
    public static String getDateWithYMDAndDot(String dateOfYYYYMMDD){
        String year = dateOfYYYYMMDD.substring(0,4);
        String month = dateOfYYYYMMDD.substring(4,6);
        String day = dateOfYYYYMMDD.substring(6,8);

        return year + "." + month + "." + day;
    }

    // YYYY년 MM월 dd일 오전/오후 hh:mm
    public static String getDateWithYMdhmWithPmAm(){
        String todayYYMMdd = getDateWithYMDAndWeekDay();

        String timeAmPm = getAmPmTime();

        return todayYYMMdd + " " + timeAmPm;
    }

    // 특정요일 구하기
    public static String getDayOfWeek(String dateOfYYYYMMDD) {
        String pattern = "yyyyMMdd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        int dayOfWeekInt = 9999;
        String dayOfWeek = "";

        try {
            Date date = simpleDateFormat.parse(dateOfYYYYMMDD);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            dayOfWeekInt = calendar.get(Calendar.DAY_OF_WEEK);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        switch (dayOfWeekInt){
            case 1:
                dayOfWeek = "일";
                break;
            case 2:
                dayOfWeek = "월";
                break;
            case 3:
                dayOfWeek = "화";
                break;
            case 4:
                dayOfWeek = "수";
                break;
            case 5:
                dayOfWeek = "목";
                break;
            case 6:
                dayOfWeek = "금";
                break;
            case 7:
                dayOfWeek = "토";
                break;
        }
        return dayOfWeek;
    }

    public static String getAmPmTime(String hhmm){
        String[] timeArr = hhmm.split(":");
        String time = null;

        String hourStr = timeArr[0];
        String minStr = timeArr[1];


        int hour = Integer.parseInt(hourStr);
        int minute = Integer.parseInt(minStr);

        if(minute < 10){
            minStr = "0"+minute;
        } else{
            minStr = ""+minute;
        }

        if(hour == 0){
            time = "오전 " + 12 + ":" + minStr;
        } else if(hour < 12){
            time = "오전 " + hour + ":" + minStr;
        } else if(hour == 12){
            time = "오후 " + hour + ":" + minStr;
        } else{
            time = "오후 " + (hour - 12) + ":" + minStr;
        }
        return time;
    }

    /**
     * 오전, 오후 시간 구하기
     * */
    public static String getAmPmTime(){
        String time;
        String timehhmm = null;
        try {
            timehhmm = getCurrentTime(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return getAmPmTime(timehhmm);
    }

    // 현재시간 구하기
    public static String getCurrentTime(boolean isLaterTime) throws Exception {
        SimpleDateFormat format2 = new SimpleDateFormat( "HH:mm",Locale.KOREA);
        format2.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        Date today = new Date();
        String time = format2.format(today);

        if(isLaterTime){
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);
            cal.add(Calendar.HOUR, 1);

            time = format2.format(cal.getTime());
        }

        return time;
    }

    // 두 날짜의 일 수 차이 구하기
    public static long getDayDifference(String date1, String date2){
        long calDateDays = 0;

        try{ // String Type을 Date Type으로 캐스팅하면서 생기는 예외로 인해 여기서 예외처리 해주지 않으면 컴파일러에서 에러가 발생해서 컴파일을 할 수 없다.
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            // date1, date2 두 날짜를 parse()를 통해 Date형으로 변환.
            Date FirstDate = format.parse(date1);
            Date SecondDate = format.parse(date2);

            Log.d("FirstDate:", FirstDate.toString());
            Log.d("SecondDate:", SecondDate.toString());

            // Date로 변환된 두 날짜를 계산한 뒤 그 리턴값으로 long type 변수를 초기화 하고 있다.
            // 연산결과 -950400000. long type 으로 return 된다.
            long calDate = SecondDate.getTime() - FirstDate.getTime();

            Log.d("calDate:", "" + calDate);


            // Date.getTime() 은 해당날짜를 기준으로1970년 00:00:00 부터 몇 초가 흘렀는지를 반환해준다.
            // 이제 24*60*60*1000(각 시간값에 따른 차이점) 을 나눠주면 일수가 나온다.
            calDateDays = calDate / ( 24*60*60*1000);

            System.out.println("두 날짜의 날짜 차이: "+ calDateDays);

        }
        catch(ParseException e)
        {
            // 예외 처리
        }
        return calDateDays;
    }

    // String(datetime형태) to date
    public static Date makeDateFromDatetimeOfString(String datetime){
        System.out.println("datetime:" + datetime);
        datetime += ":00";

        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = null;

        try {
            time = transFormat.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println(time);

        return time;
    }

    // 서버시간과의 차이 보정한 메시지 수신시간 리턴
    public static String computeTimeDifferToServer(String datetime, long timeDiffer){
        Date currentTimeOfDate = makeDateFromDatetimeOfString(datetime);
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        int timeDifferSec = (int)(timeDiffer/1000);

        System.out.println("currentTimeOfDate: " + currentTimeOfDate);
        System.out.println("timeDifferSec: " + timeDifferSec);

        Calendar cal = Calendar.getInstance();
        cal.setTime(currentTimeOfDate);
        cal.add(Calendar.SECOND, timeDifferSec);

        System.out.println("메시지 수신시간:" + datetime);

        System.out.println("보정시간; "+ sdFormat.format(cal.getTime()));

        return sdFormat.format(cal.getTime());
    }
}
