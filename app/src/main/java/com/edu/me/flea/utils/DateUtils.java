package com.edu.me.flea.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy h:m:s aa", Locale.ENGLISH);

    public static String formatDate(long timeStamp)
    {
        Date date = new Date();
        date.setTime(timeStamp);
        return dateFormat.format(date);
    }
}
