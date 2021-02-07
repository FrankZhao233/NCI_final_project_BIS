package com.edu.me.flea.ui.fragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.lifecycle.Observer;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseFragment;
import com.edu.me.flea.base.CommonAdapter;
import com.edu.me.flea.base.ViewHolder;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.BannerInfo;
import com.edu.me.flea.entity.WelfareInfo;
import com.edu.me.flea.ui.adpater.ImageTitleAdapter;
import com.edu.me.flea.utils.DateUtils;
import com.edu.me.flea.utils.ImageLoader;
import com.edu.me.flea.vm.WelfareViewModel;
import com.youth.banner.Banner;
import com.youth.banner.config.BannerConfig;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.util.BannerUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelfareFragment extends BaseFragment<WelfareViewModel> {
    @BindView(R.id.banner) Banner banner;
    @BindView(R.id.swipeRefresh) SwipeRefreshLayout refreshLayout;
    @BindView(R.id.welfareLv) ListView welfareLv;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    private CommonAdapter<WelfareInfo> mAdapter;
    private ImageTitleAdapter mBannerAdapter;

    public static WelfareFragment newInstance()
    {
        return new WelfareFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_welfare;
    }

    @Override
    protected void inject() {
        super.inject();
        ButterKnife.bind(this,getView());
    }

    @Override
    protected void initView() {
        refreshLayout.setEnabled(true);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mBannerAdapter =  new ImageTitleAdapter(new ArrayList<>());
        banner.setAdapter(mBannerAdapter)
            .addBannerLifecycleObserver(this)
            .setIndicator(new CircleIndicator(getActivity()))
            .setIndicatorGravity(IndicatorConfig.Direction.CENTER)
            .setIndicatorMargins(new IndicatorConfig.Margins(0, 0,
                    BannerConfig.INDICATOR_MARGIN, (int) BannerUtils.dp2px(12)));

        mAdapter = new CommonAdapter<WelfareInfo>(R.layout.item_welfare,mViewModel.getWelfareList().getValue()) {
            @Override
            public void convert(ViewHolder holder, WelfareInfo item, int position) {
                holder.setTvText(R.id.titleTv,item.title);
                holder.setTvText(R.id.descriptionTv,item.description);
                holder.setTvText(R.id.timeTv, DateUtils.formatOnlyDate(item.createTime.getSeconds()*1000));
                holder.setTvText(R.id.countTv,getContribution(item.hotDegree) +" contributors");
                ProgressBar bar = holder.getView(R.id.progressBar);
                bar.setProgress(item.progress);
                ImageLoader.loadNetImage(holder.getImageView(R.id.coverIv),item.cover);
            }
        };
        welfareLv.setAdapter(mAdapter);

    }

    public String getContribution(long count)
    {
        if(count>=1000){
            return String.format("%d.%dK"+(count/1000),(count%1000)/100);
        }else{
            return String.valueOf(count);
        }
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void setListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mViewModel.loadRefresh();
            }
        });

        mViewModel.getWelfareList().observe(this, new Observer<List<WelfareInfo>>() {
            @Override
            public void onChanged(List<WelfareInfo> welfareInfos) {
                mAdapter.refreshView(welfareInfos);
                progressBar.setVisibility(View.GONE);
                refreshLayout.setVisibility(View.VISIBLE);
                refreshLayout.setRefreshing(false);
            }
        });

        welfareLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                WelfareInfo info = mViewModel.getWelfareList().getValue().get(i);
                ARouter.getInstance()
                    .build(Config.Page.WELFARE_DETAIL)
                    .withParcelable(Constants.ExtraName.WELFARE_DETAIL,info)
                    .navigation();
            }
        });

        mViewModel.getBanner().observe(this, new Observer<List<BannerInfo>>() {
            @Override
            public void onChanged(List<BannerInfo> bannerInfos) {
                mBannerAdapter.setDatas(bannerInfos);
            }
        });
    }
}