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
import com.edu.me.flea.utils.Utils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class HomeGoodsHolder extends RecyclerAdapter.ViewHolder<GoodsInfo> {

    ImageView coverIv;
    TextView priceTv;
    ImageView avatarIv;
    TextView nameTv;
    TextView titleTv;
    TextView hotTv;

    RequestOptions requestOptions = new RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(R.drawable.ic_empty_image)
        .error(R.drawable.ic_empty_image)
        .centerCrop();

    public HomeGoodsHolder(View itemView) {
        super(itemView);
        coverIv = itemView.findViewById(R.id.coverIv);
        avatarIv = itemView.findViewById(R.id.avatarIv);
        titleTv = itemView.findViewById(R.id.titleTv);
        nameTv = itemView.findViewById(R.id.nameTv);
        priceTv = itemView.findViewById(R.id.priceTv);
        hotTv = itemView.findViewById(R.id.hotTv);
    }

    @Override
    protected void onBind(GoodsInfo goodsInfo) {
        nameTv.setText(goodsInfo.creatorName);
        priceTv.setText("$"+goodsInfo.price);
        titleTv.setText(goodsInfo.title);
        if(!TextUtils.isEmpty(goodsInfo.cover)) {
            String location = String.format(Config.GOODS_FULL_REF_PATH_FMT, goodsInfo.cover);
            StorageReference gsReference = FirebaseStorage.getInstance().getReferenceFromUrl(location);
            GlideApp.with(Utils.getContext())
                    .load(gsReference)
                    .apply(requestOptions)
                    .into(coverIv);
        }
    }
}
