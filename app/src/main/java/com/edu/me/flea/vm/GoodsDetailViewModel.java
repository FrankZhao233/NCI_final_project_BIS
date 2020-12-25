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
import com.edu.me.flea.base.SingleLiveEvent;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.AuctionInfo;
import com.edu.me.flea.entity.ChatParams;
import com.edu.me.flea.entity.ContributionInfo;
import com.edu.me.flea.entity.GoodsDetail;
import com.edu.me.flea.entity.GoodsInfo;
import com.edu.me.flea.utils.ToastUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoodsDetailViewModel extends BaseViewModel {

    private SingleLiveEvent<Void> mAuctionEvent = new SingleLiveEvent<>();
    private MutableLiveData<GoodsDetail> mGoodsDetail = new MutableLiveData<>();
    private MutableLiveData<List<AuctionInfo>> mAuctions = new MutableLiveData<>();

    public GoodsDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<GoodsDetail> getGoodsDetail(){
        return mGoodsDetail;
    }

    public LiveData<Void> getAuctionEvent(){
        return mAuctionEvent;
    }

    public LiveData<List<AuctionInfo>> getAuctions()
    {
        return mAuctions;
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

    public void queryAuctionInfo()
    {
        FirebaseFirestore.getInstance()
            .collection(Constants.Collection.AUCTION)
            .orderBy("price")
            .limit(10)
            .get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                    ArrayList<AuctionInfo> auctions = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot:documentSnapshots) {
                        AuctionInfo auction = documentSnapshot.toObject(AuctionInfo.class);
                        if(auction != null) {
                            auctions.add(auction);
                        }
                    }
                    mAuctions.setValue(auctions);
                }
            })
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

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

    public void doAuction(String price,String message)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Map<String,Object> values = new HashMap<>();
        values.put("goodsId",mGoodsDetail.getValue().id);
        values.put("uid",user.getUid());
        values.put("nickName",user.getDisplayName());
        values.put("price",Float.parseFloat(price));
        values.put("message",message);
        values.put("createTime",System.currentTimeMillis());
        showLoading(R.string.waiting_now);
        FirebaseFirestore.getInstance()
            .collection(Constants.Collection.AUCTION)
            .add(values)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    mAuctionEvent.call();
                }
            }).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    closeLoading();
                }
            });
    }
}
