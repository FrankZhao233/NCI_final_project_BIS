package com.edu.me.flea.vm;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseViewModel;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.GoodsDetail;
import com.edu.me.flea.entity.GoodsInfo;
import com.edu.me.flea.utils.ToastUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MyGoodsListViewModel extends BaseViewModel {

    private MutableLiveData<List<GoodsInfo>> mGoods = new MutableLiveData<>();

    public MyGoodsListViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<GoodsInfo>> getGoods(){
        return mGoods;
    }

    public void queryGoods()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore.getInstance()
            .collection(Constants.Collection.SNAPSHOT)
            .whereEqualTo("creatorId",user.getUid())
            .get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                    ArrayList<GoodsInfo> goods = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot:documentSnapshots) {
                        GoodsInfo goodsInfo = documentSnapshot.toObject(GoodsInfo.class);
                        goods.add(goodsInfo);
                    }
                    mGoods.setValue(goods);
                }
            });
    }

    public void deleteSnapshot(int pos)
    {
        List<GoodsInfo> list = mGoods.getValue();
        if(list != null && pos>=0 && pos<list.size()) {
            GoodsInfo info = list.get(pos);
            Log.d(Config.TAG,"delete snapshot id=="+info.id+",detailId=="+info.detailId);
            showLoading(R.string.waiting_now);
            FirebaseFirestore.getInstance()
                .collection(Constants.Collection.SNAPSHOT)
                .document(info.id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(Config.TAG,"delete snapshot success,query detail now");
                        queryGoodsDetail(info);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    closeLoading();
                }
            });
        }
    }

    public void queryGoodsDetail(GoodsInfo goods)
    {
        FirebaseFirestore.getInstance()
                .collection(Constants.Collection.GOODS)
                .document(goods.detailId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        GoodsDetail detail = documentSnapshot.toObject(GoodsDetail.class);
                        deleteGoods(goods,detail);
                        Log.d(Config.TAG,"query detail success,delete detail now=="+detail.id);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ToastUtils.showShort("query failed");
            }
        });
    }

    private void deleteGoods(GoodsInfo info,GoodsDetail detail)
    {
        FirebaseFirestore.getInstance()
            .collection(Constants.Collection.GOODS)
            .document(info.detailId)
            .delete()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    deleteImages(info,detail);
                    Log.d(Config.TAG,"delete detail success,delete images");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    closeLoading();
                }
            });
    }

    private void deleteImages(GoodsInfo info,GoodsDetail detail)
    {
        List<String> images = detail.images;
        if(images != null && images.size()>0) {
            for(String image:images) {
                Log.d(Config.TAG,"delete iamge==>"+image);
                String location = String.format(Config.GOODS_FULL_REF_PATH_FMT, image);
                StorageReference gsReference = FirebaseStorage.getInstance().getReferenceFromUrl(location);
                gsReference.delete();
            }
        }
        List<GoodsInfo> list = mGoods.getValue();
        if (list != null) {
            list.remove(info);
            mGoods.setValue(list);
        }
        Log.d(Config.TAG, "delete task success");
        closeLoading();
    }
}
