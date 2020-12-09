package com.edu.me.flea.ui.adpater;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.edu.me.flea.R;
import com.edu.me.flea.entity.BannerInfo;
import com.edu.me.flea.ui.holder.ImageTitleHolder;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;


public class ImageTitleAdapter extends BannerAdapter<BannerInfo, ImageTitleHolder> {

    RequestOptions options = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL);
    public ImageTitleAdapter(List<BannerInfo> mDatas) {
        super(mDatas);
    }

    @Override
    public ImageTitleHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new ImageTitleHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_image_title, parent, false));
    }

    @Override
    public void onBindView(ImageTitleHolder holder, BannerInfo data, int position, int size) {
        Glide.with(holder.itemView)
            .load(data.imageUrl)
            .thumbnail(Glide.with(holder.itemView)
            .load(R.drawable.loading))
            .apply(options)
            .into(holder.imageView);
        holder.title.setText(data.title);
    }

}
