package com.edu.me.flea.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseActivity;
import com.edu.me.flea.base.CommonAdapter;
import com.edu.me.flea.base.ViewHolder;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.entity.ContributionInfo;
import com.edu.me.flea.entity.GoodsInfo;
import com.edu.me.flea.utils.DateUtils;
import com.edu.me.flea.utils.ImageLoader;
import com.edu.me.flea.vm.MyWelfareViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = Config.Page.MY_WELFARE)
public class MyWelfareActivity extends BaseActivity<MyWelfareViewModel> {

    @BindView(R.id.welfareLv)
    ListView welfareLv;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private CommonAdapter<ContributionInfo> mAdapter ;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_warefare;
    }

    @Override
    protected void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mAdapter = new CommonAdapter<ContributionInfo>(R.layout.item_my_welfare,mViewModel.getContributions().getValue()) {
            @Override
            public void convert(ViewHolder holder, ContributionInfo item, int position) {
                holder.setTvText(R.id.titleTv,item.welfareTitle);
                holder.setTvText(R.id.valueTv,"contribution:"+item.value);
                holder.setTvText(R.id.timeTv, DateUtils.formatDate(item.createTime));
                ImageView coverIv = holder.getImageView(R.id.coverIv);
                ImageLoader.loadNetImage(coverIv,item.welfareCover);
            }
        };
        welfareLv.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initData() {
        mViewModel.queryContributions();
    }

    @Override
    protected void inject() {
        ButterKnife.bind(this);
    }

    @Override
    protected void setListener() {
        mViewModel.getContributions().observe(this, new Observer<List<ContributionInfo>>() {
            @Override
            public void onChanged(List<ContributionInfo> goods) {
                mAdapter.refreshView(goods);
                progressBar.setVisibility(View.GONE);
                welfareLv.setVisibility(View.VISIBLE);
            }
        });
    }
}