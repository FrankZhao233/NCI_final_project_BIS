package com.edu.me.flea.ui.fragment;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseFragment;
import com.edu.me.flea.entity.BannerInfo;
import com.edu.me.flea.ui.adpater.ImageTitleAdapter;
import com.edu.me.flea.vm.WelfareViewModel;
import com.youth.banner.Banner;
import com.youth.banner.config.BannerConfig;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.util.BannerUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelfareFragment extends BaseFragment<WelfareViewModel> {

    @BindView(R.id.banner)
    Banner banner;

    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout refresh;

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
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {

    }
}