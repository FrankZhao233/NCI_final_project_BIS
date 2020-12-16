package com.edu.me.flea.utils;

import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.edu.me.flea.R;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.module.GlideApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageLoader {

    public static void loadAvatar(ImageView imageView, String id)
    {
        if(!TextUtils.isEmpty(id)){
            RequestOptions avatarRequestOptions = new RequestOptions()
                .bitmapTransform(new CircleCrop())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.image_default_head)
                .error(R.drawable.image_default_head);
            String location = String.format(Config.AVATAR_FULL_REF_PATH_FMT, id+".jpeg");
            StorageReference gsReference = FirebaseStorage.getInstance().getReferenceFromUrl(location);
            GlideApp.with(Utils.getContext())
                .load(gsReference)
                .apply(avatarRequestOptions)
                .thumbnail(0.5f)
                .into(imageView);
        }
    }


    public static void loadCover(ImageView imageView,String cover){
        if(!TextUtils.isEmpty(cover)) {
            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_empty_image)
                    .error(R.drawable.ic_empty_image)
                    .centerCrop();
            String location = String.format(Config.GOODS_FULL_REF_PATH_FMT, cover);
            StorageReference gsReference = FirebaseStorage.getInstance().getReferenceFromUrl(location);
            GlideApp.with(Utils.getContext())
                    .load(gsReference)
                    .apply(requestOptions)
                    .into(imageView);
        }
    }

    public static void loadNetImage(ImageView imageView,String cover){
        if(!TextUtils.isEmpty(cover)) {
            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_empty_image)
                    .error(R.drawable.ic_empty_image)
                    .centerCrop();

            Glide.with(Utils.getContext())
                .load(cover)
                .apply(requestOptions)
                .transform(new RoundedCorners(6))
                .into(imageView);
        }
    }

}

