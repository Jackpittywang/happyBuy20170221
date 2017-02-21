package dotc.android.happybuy.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {


    public static String time2sss(long time) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss.sss");
        Date currentTime = new Date();
        currentTime.setTime(time);
        String stime = sDateFormat.format(currentTime);
        return stime;
    }

    public static String time2ss(long time) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentTime = new Date();
        currentTime.setTime(time);
        String stime = sDateFormat.format(currentTime);
        return stime;
    }

    public static String time2date(long time) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentTime = new Date();
        currentTime.setTime(time);
        String stime = sDateFormat.format(currentTime);
        return stime;
    }

    public static String formateCountDownTime(long millisUntilFinished) {
        long minute = (millisUntilFinished % (60 * 60 * 1000)) / (60 * 1000);
        long second = (millisUntilFinished % (60 * 1000)) / 1000;
        long millSecond = (millisUntilFinished % 1000);
        StringBuilder stringBuilder = new StringBuilder();
        if (minute < 10) {
            stringBuilder.append(0);
        }
        stringBuilder.append(minute);
        stringBuilder.append(":");

        if (second < 10) {
            stringBuilder.append(0);
        }
        stringBuilder.append(second);
        stringBuilder.append(":");

        if (millSecond < 100) {
            stringBuilder.append(0);
        }
        if (millSecond < 10) {
            stringBuilder.append(0);
        }
        stringBuilder.append(millSecond);
        return stringBuilder.toString();
    }

    public static long time2interval(long startTime, long endTime) {
        long diff = endTime - startTime;
        if (diff >= 0) {
            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
            return days;
        } else return -1;

    }

    /**
     * 根据日期返回不同的时间格式
     */

    public static String getTimeAccordCurrentDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(time);
        Calendar current = Calendar.getInstance();

        String sTime = format.format(date);
        Calendar today = Calendar.getInstance(); // 今天
        today.set(Calendar.YEAR, current.get(Calendar.YEAR));
        today.set(Calendar.MONTH, current.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH));
        today.set( Calendar.HOUR_OF_DAY, 0);
        today.set( Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        current.setTime(date);

        if (current.after(today)) {
            return sTime.split(" ")[1];
        } else {
            return sTime.split(" ")[0];
        }
    }

    public static boolean checkTimeIsToday(long time){
        Date date = new Date(time);
        Calendar current = Calendar.getInstance();

        Calendar today = Calendar.getInstance(); // 今天
        today.set(Calendar.YEAR, current.get(Calendar.YEAR));
        today.set(Calendar.MONTH, current.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH));
        today.set( Calendar.HOUR_OF_DAY, 0);
        today.set( Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        current.setTime(date);
        if(current.after(today)){
            return true;
        }
        return false;
    }

}
