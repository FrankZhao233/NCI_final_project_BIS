package com.edu.me.flea.ui.fragment;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseFragment;
import com.edu.me.flea.base.RecyclerAdapter;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.GoodsInfo;
import com.edu.me.flea.ui.adpater.HomeGoodsAdapter;
import com.edu.me.flea.vm.HomeViewModel;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gu.swiperefresh.SwipeRefreshPlush;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends BaseFragment<HomeViewModel> {

    @BindView(R.id.productLv) RecyclerView goodsRv;
    @BindView(R.id.floatMenu) FloatingActionMenu floatMenu;
    @BindView(R.id.publishFab) FloatingActionButton publishFab;
    @BindView(R.id.auctionFab) FloatingActionButton auctionFab;
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshPlush swipeRefreshLayout;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.maskView)View maskView;

    HomeGoodsAdapter mAdapter;

    public static HomeFragment newInstance()
    {
        return new HomeFragment();
    }

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
        swipeRefreshLayout.setRefreshColorResources(R.color.colorPrimary);
        mAdapter = new HomeGoodsAdapter(new ArrayList<GoodsInfo>());
        goodsRv.setLayoutManager(new GridLayoutManager(getActivity(),2));
        goodsRv.setAdapter(mAdapter);
        Log.d("flea","initView");

        View noMoreView = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_no_more,
                null, false);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        noMoreView.setPadding(10,10,10,10);
        swipeRefreshLayout.setNoMoreView(noMoreView, layoutParams);
    }

    @Override
    protected void initData()
    {

    }

    @Override
    protected void setListener() {
        publishFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    ARouter.getInstance().build(Config.Page.PUBLISH).navigation();
                }else{
                    ARouter.getInstance().build(Config.Page.LOGIN).navigation();
                }
                floatMenu.close(false);
            }
        });

        auctionFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    ARouter.getInstance().build(Config.Page.GOODS_AUCTION).navigation();
                } else {
                    ARouter.getInstance().build(Config.Page.LOGIN).navigation();
                }
                floatMenu.close(false);
            }
        });

        mViewModel.getGoodsLiveData().observe(this, new Observer<List<GoodsInfo>>() {
            @Override
            public void onChanged(List<GoodsInfo> goods) {
                Log.d(Config.TAG,"goods size==>"+goods.size());
                if (goods.size() < mViewModel.getPageSize()) {
                    swipeRefreshLayout.showNoMore(true);
                }
                mAdapter.addAllData(goods);
                Log.d(Config.TAG,"all size==>"+mAdapter.getItems().size());
            }
        });

        swipeRefreshLayout.setOnScrollListener(new SwipeRefreshPlush.OnRefreshListener() {
            @Override
            public void onPullDownToRefresh() {
                mAdapter.remove();
                mViewModel.refreshList();
            }

            @Override
            public void onPullUpToRefresh() {
                mViewModel.loadMore(mAdapter.getItems().size());
            }
        });

        mViewModel.getLoadingState().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer state) {
                if (state == 0) {
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    floatMenu.setVisibility(View.VISIBLE);
                } else if(state ==1) {
                    swipeRefreshLayout.setRefresh(false);
                }
            }
        });

        mAdapter.setAdapterListener(new RecyclerAdapter.AdapterListener<GoodsInfo>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder<GoodsInfo> holder, GoodsInfo goodsInfo) {
                Log.d(Config.TAG,"detail id ="+goodsInfo.detailId);
                ARouter.getInstance().build(Config.Page.GOODS_DETAIL)
                .withParcelable(Constants.ExtraName.SNAPSHOT,goodsInfo)
                .navigation();
            }

            @Override
            public void onItemLongClick(RecyclerAdapter.ViewHolder<GoodsInfo> holder, GoodsInfo goodsInfo) {

            }
        });

        floatMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if(opened){
                    maskView.setVisibility(View.VISIBLE);
                }else{
                    maskView.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onBusEvent(int what, Object event) {
        super.onBusEvent(what, event);
        if(what == Constants.Event.PUBLISH_OVER){
            mAdapter.remove();
            mViewModel.refreshList();
        }
    }
}