package com.edu.me.flea.base;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;

import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.AutoDisposeConverter;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

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

    protected <T> AutoDisposeConverter<T> bindLifecycle() {
        if (null == lifecycleOwner)
            throw new NullPointerException("lifecycleOwner == null");
        return bindLifecycle(lifecycleOwner);
    }

    public static <T> AutoDisposeConverter<T> bindLifecycle(LifecycleOwner lifecycleOwner) {
        return AutoDispose.autoDisposable(
                AndroidLifecycleScopeProvider.from(lifecycleOwner)
        );
    }

    @Override
    public void onDestroy(LifecycleOwner owner) {
//        L.d("event","onDestroy="+owner);
    }

    @Override
    public void onStart() {
//        L.d("event","onStart");
    }

    @Override
    public void onStop() {
//        L.d("event","onStop");
    }

    @Override
    public void onResume() {
//        L.d("event","onResume");
    }

    @Override
    public void onPause() {
//        L.d("event","onPause");
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
