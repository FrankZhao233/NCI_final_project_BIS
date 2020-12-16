package com.edu.me.flea.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseActivity;
import com.edu.me.flea.vm.WelfareDetailViewModel;

import butterknife.ButterKnife;

public class WelfareDetailActivity extends BaseActivity<WelfareDetailViewModel> {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_welfare_detail;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void inject() {
        ButterKnife.bind(this);
    }

    @Override
    protected void setListener() {

    }
}