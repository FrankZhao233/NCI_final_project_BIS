package com.edu.me.flea.base;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;


import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseViewModel extends AndroidViewModel implements IBaseViewModel,IBaseView{

    private CompositeDisposable mCompositeDisposable;
    private LifecycleOwner lifecycleOwner;
    private BaseViewLiveData mBaseView;
    private Intent mPageIntent;

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    public void addDisposable(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void onCreate(LifecycleOwner owner) {
        this.lifecycleOwner = owner;
    }

    @Override
    public void onDestroy(LifecycleOwner owner) {
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d("event","onCleared");
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.clear();
        }
    }


    public BaseViewLiveData getBaseViewLiveData() {
        if (mBaseView == null) {
            mBaseView = new BaseViewLiveData();
        }
        return mBaseView;
    }

    @Override
    public void showLoading(int msgRes) {
        getBaseViewLiveData().showLoading(msgRes);
    }

    @Override
    public void closeLoading() {
        getBaseViewLiveData().closeLoading();
    }

    @Override
    public void closePage() {
        getBaseViewLiveData().closePage();
    }

    public Intent getPageIntent()
    {
        return mPageIntent;
    }

    public void setPageIntent(Intent intent)
    {
        this.mPageIntent = intent;
    }


}
