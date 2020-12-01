package com.edu.me.flea.ui.fragment;


import android.widget.TextView;

import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseFragment;
import com.edu.me.flea.vm.HomeViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends BaseFragment<HomeViewModel> {

    @BindView(R.id.text_home)
    TextView textView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
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

    }
}