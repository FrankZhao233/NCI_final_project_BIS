package com.edu.me.flea.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseActivity;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.vm.PublishViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = Config.Page.PUBLISH)
public class PublishActivity extends BaseActivity<PublishViewModel> {

    @BindView(R.id.contentEt)
    EditText contentEt;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_publish;
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