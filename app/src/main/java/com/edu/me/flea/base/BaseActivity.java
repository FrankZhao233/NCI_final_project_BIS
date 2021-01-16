package com.edu.me.flea.base;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import com.blankj.rxbus.RxBus;
import com.edu.me.flea.FleaApplication;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.utils.PreferencesUtils;
import com.edu.me.flea.utils.StatusBarUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Locale;

public abstract class BaseActivity<VM extends BaseViewModel> extends AppCompatActivity implements IBaseView {

    protected VM mViewModel;
    protected  ProgressDialog mProgressDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initViewModel();
        mViewModel.setPageIntent(getIntent());
        getLifecycle().addObserver(mViewModel);
        inject();
        setListener();
        initView();
        registerUIChangeLiveData();
        initData();
        registerRxBus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void postEvent(int event)
    {
        RxBus.getDefault().post(new RxEvent(event,null));
    }

    public void postEvent(int what,Object event)
    {
        RxBus.getDefault().post(new RxEvent(what,event));
    }

    public void postEventWithTag(String tag,int what,Object event)
    {
        RxBus.getDefault().post(new RxEvent(what,event),tag);
    }

    public void setStatusBar(boolean isDark) {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        StatusBarUtil.setTranslucentStatus(this);
        StatusBarUtil.setStatusBarDarkTheme(this, isDark);
    }

    public void removeToolbarElevation()
    {
        ActionBar bar = getSupportActionBar();
        if(bar != null){
            bar.setElevation(0);
        }
    }

    private void registerRxBus() {
        RxBus.getDefault().subscribe(this, new RxBus.Callback<RxEvent>() {

            @Override
            public void onEvent(RxEvent rxEvent) {
                onBusEvent(rxEvent.what,rxEvent.event);
            }
        });

        if(!TextUtils.isEmpty(regRxBusTag())){
            RxBus.getDefault().subscribe(this, regRxBusTag(), new RxBus.Callback<RxEvent>() {
                @Override
                public void onEvent(RxEvent rxEvent) {
                    onBusEvent(rxEvent.what,rxEvent.event);
                }
            });
        }
    }

    protected String regRxBusTag(){
        return "";
    }

    protected void onBusEvent(int what, Object event) {
        switch(what){
            case Constants.Event.LANGUAGE_CHANGED:
                changeAppLanguage();
                recreate();
                break;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        String language = PreferencesUtils.getString(FleaApplication.getApp(), Constants.PrefKey.LANGUAGE,"en");
        super.attachBaseContext(attachBaseContext(newBase, language));
    }

    public Context attachBaseContext(Context context, String language) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        } else {
            return context;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private Context updateResources(Context context, String language) {
        Resources resources = context.getResources();
        Locale locale = getLocaleByLanguage(language);
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLocales(new LocaleList(locale));
        return context.createConfigurationContext(configuration);
    }

    public void changeAppLanguage() {
        String sta = PreferencesUtils.getString(FleaApplication.getApp(), Constants.PrefKey.LANGUAGE,"en");
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = getLocaleByLanguage(sta);
        res.updateConfiguration(conf, dm);
    }

    private Locale getLocaleByLanguage(String sta)
    {
        Locale myLocale=null;
        if(sta.equals("zh")){
            myLocale = new Locale(sta,Locale.CHINESE.getCountry());
        }else  if(sta.equals("en")||sta.equals("en_US")){
            myLocale = new Locale( "en",Locale.ENGLISH.getCountry());
        }
        return myLocale;
    }

    @Override
    public void showLoading(int msgRes) {
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(msgRes));
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(true);
        }
        if(!mProgressDialog.isShowing()){
            mProgressDialog.show();
        }
    }

    @Override
    public void closeLoading() {
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getLifecycle().removeObserver(mViewModel);
    }

    protected void initViewModel() {
        Class modelClass;
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            modelClass = BaseViewModel.class;
        }
        mViewModel = (VM) createViewModel(this, modelClass);
    }

    protected abstract @LayoutRes
    int getLayoutId();
    protected abstract void initView();
    protected abstract void initData();
    protected abstract void inject();

    protected abstract void setListener();

    protected void setContentView() {}

    protected boolean showToolbar()
    {
        return true;
    }

    public <T extends ViewModel> T createViewModel(FragmentActivity activity, Class<T> cls) {
        return ViewModelProviders.of(activity).get(cls);
    }

    public <T extends View> T f(int id) {
        return findViewById(id);
    }

    protected void registerUIChangeLiveData() {
        mViewModel.getBaseViewLiveData().getShowDialogEvent().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer titleRes) {
                showLoading(titleRes);
            }
        });

        mViewModel.getBaseViewLiveData().getDismissDialogEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                closeLoading();
            }
        });

        mViewModel.getBaseViewLiveData().getFinishEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                closePage();
            }
        });

    }

    @Override
    public void closePage() {
        finish();
    }
}
