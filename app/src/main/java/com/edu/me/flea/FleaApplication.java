package com.edu.me.flea;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.alibaba.android.arouter.launcher.ARouter;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.utils.PreferencesUtils;
import com.edu.me.flea.utils.Utils;

import java.util.Locale;

public class FleaApplication extends MultiDexApplication {

    public static FleaApplication sInst;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    public static FleaApplication getApp()
    {
        return sInst;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInst = this ;
        if (BuildConfig.DEBUG) {
            ARouter.openDebug();
            ARouter.openLog();
        }
        ARouter.init(this);
        Utils.init(this);

        String sta = PreferencesUtils.getString(this, Constants.PrefKey.LANGUAGE,"en");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            changeAppLanguage(this, sta);
        }
    }


    @SuppressWarnings("deprecation")
    public static void changeAppLanguage(Context context, String newLanguage) {
        if (TextUtils.isEmpty(newLanguage)) {
            return;
        }
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = getLocaleByLanguage(newLanguage);
        configuration.setLocale(locale);
        // updateConfiguration
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, dm);
    }

    private static Locale getLocaleByLanguage(String sta)
    {
        Locale myLocale=null;
        if(sta.equals("zh")){
            myLocale = new Locale(sta,Locale.CHINESE.getCountry());
        }else  if(sta.equals("en")||sta.equals("en_US")){
            myLocale = new Locale( "en",Locale.ENGLISH.getCountry());
        }else  if(sta.equals("de")){
            myLocale = new Locale( "de",Locale.GERMANY.getCountry());
        }else  if(sta.equals("fr")){
            myLocale = new Locale( "fr",Locale.FRANCE.getCountry());
        }
        return myLocale;
    }
}
