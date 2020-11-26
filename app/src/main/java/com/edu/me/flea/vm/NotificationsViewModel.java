package com.edu.me.flea.vm;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.edu.me.flea.base.BaseViewModel;

public class NotificationsViewModel extends BaseViewModel {

    private MutableLiveData<String> mText;

    public NotificationsViewModel(Application application) {
        super(application);
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}

