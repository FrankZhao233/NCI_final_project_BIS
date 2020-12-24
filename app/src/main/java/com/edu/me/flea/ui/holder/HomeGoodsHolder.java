package com.edu.me.flea.ui.holder;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.edu.me.flea.R;
import com.edu.me.flea.base.RecyclerAdapter;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.entity.GoodsInfo;
import com.edu.me.flea.module.GlideApp;
import com.edu.me.flea.utils.DateUtils;
import com.edu.me.flea.utils.ImageLoader;
import com.edu.me.flea.utils.Utils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.haozhang.lib.SlantedTextView;

public class HomeGoodsHolder extends RecyclerAdapter.ViewHolder<GoodsInfo> {

    ImageView coverIv;
    TextView priceTv;
    ImageView avatarIv;
    TextView nameTv;
    TextView titleTv;
    TextView hotTv;
    TextView dueTimeTv;
    SlantedTextView slantedTv;

    public HomeGoodsHolder(View itemView) {
        super(itemView);
        coverIv = itemView.findViewById(R.id.coverIv);
        avatarIv = itemView.findViewById(R.id.avatarIv);
        titleTv = itemView.findViewById(R.id.titleTv);
        nameTv = itemView.findViewById(R.id.nameTv);
        priceTv = itemView.findViewById(R.id.priceTv);
        hotTv = itemView.findViewById(R.id.hotTv);
        dueTimeTv = itemView.findViewById(R.id.dueTimeTv);
        slantedTv = itemView.findViewById(R.id.slantedTv);
    }

    @Override
    protected void onBind(GoodsInfo goodsInfo) {
        nameTv.setText(goodsInfo.creatorName);
        priceTv.setText("€"+goodsInfo.price);
        titleTv.setText(goodsInfo.title);
        hotTv.setText(goodsInfo.hotDegree+" want");
        if(goodsInfo.dueTime>0){
            dueTimeTv.setVisibility(View.VISIBLE);
            slantedTv.setVisibility(View.VISIBLE);
            dueTimeTv.setText("due time: "+DateUtils.formatDate(goodsInfo.dueTime));
        }else{
            dueTimeTv.setVisibility(View.GONE);
            slantedTv.setVisibility(View.GONE);
        }
        ImageLoader.loadCover(coverIv,goodsInfo.cover);
        ImageLoader.loadAvatar(avatarIv,goodsInfo.creatorId);
    }
}
