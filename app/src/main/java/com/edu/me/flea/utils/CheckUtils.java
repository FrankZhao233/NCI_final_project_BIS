package com.edu.me.flea.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.util.Calendar;


public class CheckUtils {

    private CheckUtils(){}

    public static boolean isLetterDigit(String str) {
        if(str == null)
            return false;
        String regex1=".*[a-zA-Z]+.*";
        String regex2=".*[0-9]+.*";
        return str.matches(regex1) && str.matches(regex2);
    }

    public static boolean isValidEmail(String str)
    {
        if(str == null)
            return false;
        String regex = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
        return str.matches(regex);
    }
}
