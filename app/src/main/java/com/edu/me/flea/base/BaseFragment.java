package com.edu.me.flea.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.blankj.rxbus.RxBus;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class BaseFragment<VM extends BaseViewModel> extends Fragment implements IBaseView {

    private View fragmentRootView;
    protected VM mViewModel;
    protected boolean isCreated = false;
    protected boolean isVisibleToUser = false;
    protected ProgressDialog mProgressDialog ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentRootView = inflater.inflate(getLayoutId(), container, false);
        return fragmentRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViewModel();
        getLifecycle().addObserver(mViewModel);
        inject();
        initView();
        setListener();
        registerUIChangeLiveData();
        loadData();
        registerRxBus();
    }


    @Override
    public void showLoading(int msgRes) {
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(getActivity());
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

    }

    @Override
    public void closePage() {
        closePage();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            this.isVisibleToUser = true;
            lazyLoad();
        }
    }

    private void loadData() {
        if (isLazy()) {
            isCreated = true;
            lazyLoad();
        } else {
            initData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getLifecycle().removeObserver(mViewModel);
    }

    private void lazyLoad() {

        if (isCreated && isVisibleToUser) {
            initData();
            isCreated = false;
        }
    }

    protected void initViewModel()
    {
        Class modelClass;
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            modelClass = BaseViewModel.class;
        }
        mViewModel = (VM) ViewModelProviders.of(this).get(modelClass);
    }

    protected abstract @LayoutRes
    int getLayoutId();
    protected abstract void initView();
    protected abstract void initData();
    protected void inject(){}
    protected abstract void setListener();

    public <T extends View> T f(int id) {
        return fragmentRootView.findViewById(id);
    }

    protected boolean isLazy()
    {
        return true;
    }

    //注册BaseView回调
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

}
