package com.edu.me.flea.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseActivity;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.service.MessageService;
import com.edu.me.flea.ui.adpater.FragmentAdapter;
import com.edu.me.flea.vm.MainViewModel;
import com.edu.me.flea.widget.NoScrollViewPager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = Config.Page.MAIN)
public class MainActivity extends BaseActivity<MainViewModel> {

    @BindView(R.id.viewPager)
    NoScrollViewPager viewPager;

    @BindView(R.id.navView)
    BottomNavigationView navView;

    private FragmentAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mAdapter = new FragmentAdapter(this, getSupportFragmentManager());
        viewPager.setPagerEnabled(false);
        viewPager.setAdapter(mAdapter);
        viewPager.setOffscreenPageLimit(4);

        setStatusBar(true);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            startService(new Intent(this, MessageService.class));
        }
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
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_home:
                        viewPager.setCurrentItem(0);
                        setStatusBar(true);
                        break;
                    case R.id.navigation_welfare:
                        viewPager.setCurrentItem(1);
                        setStatusBar(false);
                        break;
                    case R.id.navigation_notifications:
                        viewPager.setCurrentItem(2);
                        setStatusBar(true);
                        break;
                    case R.id.navigation_profile:
                        viewPager.setCurrentItem(3);
                        setStatusBar(false);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onBusEvent(int what, Object event) {
        super.onBusEvent(what, event);
        switch (what) {
            case Constants.Event.LOGIN_DONE:
                startService(new Intent(this, MessageService.class));
                break;
        }
    }
}