package com.edu.me.flea.vm;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.edu.me.flea.base.BaseViewModel;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.GoodsDetail;
import com.edu.me.flea.entity.GoodsInfo;
import com.edu.me.flea.utils.ToastUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class GoodsDetailViewModel extends BaseViewModel {

    private MutableLiveData<GoodsDetail> mGoodsDetail = new MutableLiveData<>();

    public GoodsDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<GoodsDetail> getGoodsDetail(){
        return mGoodsDetail;
    }

    public void queryGoodsDetail(String id)
    {
        Log.d(Config.TAG,"query detail id ="+id);
        FirebaseFirestore.getInstance()
            .collection(Constants.Collection.GOODS)
            .document(id).get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    GoodsDetail goodsInfo = documentSnapshot.toObject(GoodsDetail.class);
                    mGoodsDetail.setValue(goodsInfo);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ToastUtils.showShort("query failed");
                }
            });
    }
}
