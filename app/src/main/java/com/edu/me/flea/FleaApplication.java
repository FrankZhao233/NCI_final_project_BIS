package com.edu.me.flea;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.alibaba.android.arouter.launcher.ARouter;

/**
 *
 * application of app
 *
 * */
public class FleaApplication extends MultiDexApplication {

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }


    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            //开启InstantRun之后，一定要在ARouter.init之前调用openDebug
            ARouter.openDebug();
            ARouter.openLog();
        }
        ARouter.init(this);

    }
}
