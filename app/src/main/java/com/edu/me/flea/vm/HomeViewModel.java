package com.edu.me.flea.vm;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.edu.me.flea.base.BaseViewModel;
import com.edu.me.flea.base.SingleLiveEvent;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.GoodsInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends BaseViewModel {

    private MutableLiveData<List<GoodsInfo>> mGoods;
    private SingleLiveEvent<Integer> mLoadingState = new SingleLiveEvent<>();

    public HomeViewModel(Application application) {
        super(application);
        mGoods = new MutableLiveData<>();
    }

    public LiveData<List<GoodsInfo>> getGoodsLiveData()
    {
        return mGoods;
    }
    public LiveData<Integer> getLoadingState(){
        return mLoadingState;
    }

    @Override
    public void onCreate(LifecycleOwner owner) {
        super.onCreate(owner);
        loadDataPage(0,true);
        Log.d("flea","oncreate");
    }

    public void loadDataPage(int start,boolean first)
    {
        Query query = FirebaseFirestore.getInstance()
            .collection(Constants.Collection.SNAPSHOT)
            .orderBy("createTime")
            .startAt(start)
            .limit(getPageSize());
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                List<GoodsInfo> goods = new ArrayList<>();
                for(DocumentSnapshot documentSnapshot:documentSnapshots){
                    GoodsInfo goodsInfo = documentSnapshot.toObject(GoodsInfo.class);
                    goods.add(goodsInfo);
                }
                mGoods.setValue(goods);
                if(first){
                    mLoadingState.setValue(0);
                }else{
                    mLoadingState.setValue(1);
                }
            }
        });
    }

    public void loadMore(int start)
    {
        loadDataPage(start,false);
    }

    public int getPageSize()
    {
        return 10;
    }

    public void refreshList()
    {
        loadDataPage(0,false);
    }

}