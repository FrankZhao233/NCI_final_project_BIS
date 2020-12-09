package com.edu.me.flea.ui.fragment;


import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseFragment;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.vm.HomeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends BaseFragment<HomeViewModel> {

    @BindView(R.id.productLv)
    ListView productLv;

    @BindView(R.id.addBtn)
    FloatingActionButton addBtn;

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
    protected void initView()
    {

    }

    @Override
    protected void initData()
    {

    }

    @Override
    protected void setListener() {
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ARouter.getInstance().build(Config.Page.PUBLISH).navigation();
            }
        });
    }
}