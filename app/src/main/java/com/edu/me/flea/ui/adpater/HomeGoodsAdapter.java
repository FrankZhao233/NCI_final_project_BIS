package com.edu.me.flea.ui.adpater;

import android.view.View;

import com.edu.me.flea.R;
import com.edu.me.flea.base.RecyclerAdapter;
import com.edu.me.flea.entity.GoodsInfo;
import com.edu.me.flea.ui.holder.HomeGoodsHolder;

import java.util.List;

public class HomeGoodsAdapter extends RecyclerAdapter<GoodsInfo> {

    public HomeGoodsAdapter(List<GoodsInfo> goods)
    {
        super(goods,null);
    }

    @Override
    public ViewHolder<GoodsInfo> onCreateViewHolder(View root, int viewType) {
        return new HomeGoodsHolder(root);
    }

    @Override
    public int getItemLayout(GoodsInfo goodsInfo, int position) {
        return R.layout.item_home_goods;
    }
}
