package com.edu.me.flea.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.edu.me.flea.base.BaseViewModel;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.GoodsInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
}
