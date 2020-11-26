package com.edu.me.flea.ui.fragment;


import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseFragment;
import com.edu.me.flea.vm.NotificationsViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationsFragment extends BaseFragment<NotificationsViewModel> {

    @BindView(R.id.text_notifications)
    TextView textView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_notifications;
    }

    @Override
    protected void inject() {
        super.inject();
        ButterKnife.bind(this,getView());
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {
        mViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
    }
}