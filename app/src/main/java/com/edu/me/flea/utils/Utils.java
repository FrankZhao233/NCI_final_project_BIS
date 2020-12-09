package com.edu.me.flea.utils;

import android.content.Context;
import androidx.annotation.StringRes;

public class Utils {

    private static Context context;

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void init(Context context) {
        Utils.context = context.getApplicationContext();
    }

    public static Context getContext() {
        if (context != null) return context;
        throw new NullPointerException("u should init first");
    }


    public static String getString(@StringRes int id) {
        return context.getResources().getString(id);
    }


    private static long sLastClickTime;

    public static boolean isFastDoubleClick(){
        long time = System.currentTimeMillis();
        long timeD = time - sLastClickTime;
        if ( 0 < timeD && timeD < 800){
            return true;
        }
        sLastClickTime = time;
        return false;
    }


    public static boolean isFastDoubleClick(int timePeriod){
        long time = System.currentTimeMillis();
        long timeD = time - sLastClickTime;
        if ( 0 < timeD && timeD < timePeriod){
            return true;
        }
        sLastClickTime = time;
        return false;
    }



}