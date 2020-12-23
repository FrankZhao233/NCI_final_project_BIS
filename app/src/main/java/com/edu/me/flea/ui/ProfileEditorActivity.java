package com.edu.me.flea.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseActivity;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.vm.ProfileEditorViewModel;

@Route(path = Config.Page.PROFILE_EDITOR)
public class ProfileEditorActivity extends BaseActivity<ProfileEditorViewModel> {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_profile_editor;
    }

    @Override
    protected void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void inject() {

    }

    @Override
    protected void setListener() {

    }
}