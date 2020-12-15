package com.edu.me.flea.vm;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alibaba.android.arouter.launcher.ARouter;
import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseViewModel;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.ChatParams;
import com.edu.me.flea.entity.GoodsDetail;
import com.edu.me.flea.entity.GoodsInfo;
import com.edu.me.flea.utils.ToastUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class GoodsDetailViewModel extends BaseViewModel {

    private MutableLiveData<GoodsDetail> mGoodsDetail = new MutableLiveData<>();

    public GoodsDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<GoodsDetail> getGoodsDetail(){
        return mGoodsDetail;
    }


    public void queryGoodsDetail(GoodsInfo goods)
    {
        FirebaseFirestore.getInstance()
            .collection(Constants.Collection.GOODS)
            .document(goods.detailId).get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    GoodsDetail goodsInfo = documentSnapshot.toObject(GoodsDetail.class);
                    mGoodsDetail.setValue(goodsInfo);
                    addCheckCount(goods);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ToastUtils.showShort("query failed");
                }
            });
    }

    public void addCheckCount(GoodsInfo goodsInfo)
    {
        GoodsDetail goodsDetail = mGoodsDetail.getValue();
        if(goodsDetail != null){
            Map<String,Object> updates = new HashMap<>();
            updates.put("checkCount", goodsDetail.checkCount+1);

            FirebaseFirestore.getInstance()
                    .collection(Constants.Collection.SNAPSHOT)
                    .document(goodsInfo.id).update(updates);

            FirebaseFirestore.getInstance()
                .collection(Constants.Collection.GOODS)
                .document(goodsInfo.detailId).update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mGoodsDetail.getValue().checkCount+=1;
                }
            });
        }
    }

    public void want(GoodsInfo goodsInfo)
    {
        showLoading(R.string.waiting_now);
        GoodsDetail goodsDetail = mGoodsDetail.getValue();
        if(goodsDetail != null){
            Map<String,Object> updates = new HashMap<>();
            updates.put("hotDegree", goodsDetail.hotDegree+1);

            FirebaseFirestore.getInstance()
                .collection(Constants.Collection.SNAPSHOT)
                .document(goodsInfo.id).update(updates);

            FirebaseFirestore.getInstance()
                .collection(Constants.Collection.GOODS)
                .document(goodsInfo.detailId)
                .update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        closeLoading();
                        mGoodsDetail.getValue().hotDegree+=1;
                        goChat();
                    }
                });
        }
    }

    private void goChat()
    {
        GoodsDetail detail = mGoodsDetail.getValue();
        if(detail != null) {
            ChatParams chatParams = new ChatParams();
            chatParams.peerUid = detail.creatorId;
            chatParams.peerNickName = detail.creatorName;
            chatParams.cover = detail.images.get(0);
            chatParams.price = detail.price;
            chatParams.title = detail.title;
            chatParams.detailId = detail.id;
            ARouter.getInstance().build(Config.Page.CHAT)
                    .withParcelable(Constants.ExtraName.CHAT_PARAM, chatParams)
                    .navigation();
        }
    }
}
