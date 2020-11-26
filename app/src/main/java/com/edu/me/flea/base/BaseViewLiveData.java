package com.edu.me.flea.base;


import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

public class BaseViewLiveData extends SingleLiveEvent implements IBaseView {

    private SingleLiveEvent<Integer> showDialogEvent;
    private SingleLiveEvent<Void> dismissDialogEvent;
    private SingleLiveEvent<Void> finishEvent;
    private SingleLiveEvent<String> setMessageEvent;

    public SingleLiveEvent<Integer> getShowDialogEvent() {
        if (showDialogEvent == null) {
            showDialogEvent = new SingleLiveEvent();
        }
        return showDialogEvent;
    }

    public SingleLiveEvent<Void> getDismissDialogEvent() {
        if(dismissDialogEvent == null){
            dismissDialogEvent = new SingleLiveEvent();
        }
        return dismissDialogEvent;
    }

    public SingleLiveEvent<Void> getFinishEvent() {
        if(finishEvent == null){
            finishEvent = new SingleLiveEvent<>();
        }
        return finishEvent;
    }

    public SingleLiveEvent<String> getSetMessageEvent() {
        if(setMessageEvent == null){
            setMessageEvent = new SingleLiveEvent();
        }
        return setMessageEvent;
    }

    @Override
    public void observe(LifecycleOwner owner, Observer observer) {
        super.observe(owner, observer);
    }

    @Override
    public void showLoading(int msgRes) {
        getShowDialogEvent().setValue(msgRes);
    }

    @Override
    public void closeLoading() {
        getDismissDialogEvent().postValue(null);
    }

    @Override
    public void closePage() {
        getFinishEvent().postValue(null);
    }
}