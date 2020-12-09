package com.edu.me.flea.utils;

import android.content.Context;
import android.content.res.Resources;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class HelpUtils {

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getStatusBarHeight(Context context) {
        Resources res = context.getResources();
        int resId = res.getIdentifier("status_bar_height", "dimen", "android");
        return res.getDimensionPixelSize(resId);
    }

    public static String formatDate(String dateTime)
    {
        long time = Long.parseLong(dateTime);
        Date date = new Date();
        date.setTime(time);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy",
                Locale.ENGLISH);
        return sdf.format(date);
    }


    public static long getCurrentMillisTime() {
        return System.currentTimeMillis();
    }

    public static String getRoomId(String from,String to)
    {
        String roomId = "";
        if(from.compareTo(to) > 0){
            roomId = from + "-"+ to;
        }else{
            roomId = to +"-"+from;
        }
        return roomId;
    }
}
