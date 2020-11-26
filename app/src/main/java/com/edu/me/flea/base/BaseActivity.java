package com.edu.me.flea.base;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class BaseActivity<VM extends BaseViewModel> extends AppCompatActivity implements IBaseView {

    protected VM mViewModel;

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
