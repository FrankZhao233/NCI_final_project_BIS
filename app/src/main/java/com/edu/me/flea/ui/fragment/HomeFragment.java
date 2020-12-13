package com.edu.me.flea.ui.fragment;


import android.util.Log;
import android.view.View;
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
import com.edu.me.flea.base.CommonAdapter;
import com.edu.me.flea.base.RecyclerAdapter;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.GoodsInfo;
import com.edu.me.flea.ui.adpater.HomeGoodsAdapter;
import com.edu.me.flea.vm.HomeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends BaseFragment<HomeViewModel> {

    @BindView(R.id.productLv)
    RecyclerView goodsRv;

    @BindView(R.id.addBtn)
    FloatingActionButton addBtn;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

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
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mAdapter = new HomeGoodsAdapter(new ArrayList<>());
        goodsRv.setLayoutManager(new GridLayoutManager(getActivity(),2));
        goodsRv.setAdapter(mAdapter);
        Log.d("flea","initView");
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
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    ARouter.getInstance().build(Config.Page.PUBLISH).navigation();
                }else{
                    ARouter.getInstance().build(Config.Page.LOGIN).navigation();
                }
            }
        });

        mViewModel.getGoodsLiveData().observe(this, new Observer<List<GoodsInfo>>() {
            @Override
            public void onChanged(List<GoodsInfo> goods) {
                Log.d(Config.TAG,"goods size==>"+goods.size());
                mAdapter.addAllData(goods);
                Log.d(Config.TAG,"all size==>"+mAdapter.getItems().size());
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                mAdapter.remove();
                mViewModel.refreshList();
            }
        });

        mViewModel.getLoadingState().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer state) {
                if(state == 0){
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    addBtn.setVisibility(View.VISIBLE);
                }else if(state ==1){
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        mAdapter.setAdapterListener(new RecyclerAdapter.AdapterListener<GoodsInfo>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder<GoodsInfo> holder, GoodsInfo goodsInfo) {
                Log.d(Config.TAG,"detail id ="+goodsInfo.detailId);
                ARouter.getInstance().build(Config.Page.GOODS_DETAIL)
                .withString(Constants.ExtraName.ID,goodsInfo.detailId)
                .navigation();
            }

            @Override
            public void onItemLongClick(RecyclerAdapter.ViewHolder<GoodsInfo> holder, GoodsInfo goodsInfo) {

            }
        });
    }
}