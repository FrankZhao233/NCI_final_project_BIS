package com.edu.me.flea.utils;

import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
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


}

