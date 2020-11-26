package com.edu.me.flea.vm;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.edu.me.flea.base.BaseViewModel;

public class HomeViewModel extends BaseViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel(Application application) {
        super(application);
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}