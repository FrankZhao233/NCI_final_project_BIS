package com.edu.me.flea.ui.fragment;

import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.lifecycle.Observer;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseFragment;
import com.edu.me.flea.base.CommonAdapter;
import com.edu.me.flea.base.ViewHolder;
import com.edu.me.flea.entity.BannerInfo;
import com.edu.me.flea.entity.WelfareInfo;
import com.edu.me.flea.ui.adpater.ImageTitleAdapter;
import com.edu.me.flea.vm.WelfareViewModel;
import com.youth.banner.Banner;
import com.youth.banner.config.BannerConfig;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.util.BannerUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelfareFragment extends BaseFragment<WelfareViewModel> {
    @BindView(R.id.banner) Banner banner;
    @BindView(R.id.swipeRefresh) SwipeRefreshLayout refresh;
    @BindView(R.id.welfareLv) ListView welfareLv;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    private CommonAdapter<WelfareInfo> mAdapter;

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
        refresh.setEnabled(true);
        refresh.setColorSchemeResources(R.color.colorPrimary);
        banner.setAdapter(new ImageTitleAdapter(BannerInfo.getTestData()))
            .addBannerLifecycleObserver(this)
            .setIndicator(new CircleIndicator(getActivity()))
            .setIndicatorGravity(IndicatorConfig.Direction.CENTER)
            .setIndicatorMargins(new IndicatorConfig.Margins(0, 0,
                    BannerConfig.INDICATOR_MARGIN, (int) BannerUtils.dp2px(12)));
        mAdapter = new CommonAdapter<WelfareInfo>(R.layout.item_welfare,mViewModel.getWelfareList().getValue()) {
            @Override
            public void convert(ViewHolder holder, WelfareInfo item, int position) {

            }
        };
        welfareLv.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        mViewModel.queryWelfare();
    }

    @Override
    protected void setListener() {
        mViewModel.getWelfareList().observe(this, new Observer<List<WelfareInfo>>() {
            @Override
            public void onChanged(List<WelfareInfo> welfareInfos) {
                mAdapter.refreshView(welfareInfos);
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}