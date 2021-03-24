package com.edu.me.flea.vm;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.edu.me.flea.base.BaseViewModel;
import com.edu.me.flea.base.SingleLiveEvent;
import com.edu.me.flea.config.Config;
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
    private DocumentSnapshot lastVisible;

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
        loadFirstPage();
    }

    //加载第一页数据
    public void loadFirstPage()
    {
        Log.d(Config.TAG,"loadDataPage first page");
        Query query = FirebaseFirestore.getInstance()
                .collection(Constants.Collection.SNAPSHOT)
                .orderBy("createTime")
                .limit(getPageSize());
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.size() >= 1) {
                    lastVisible = queryDocumentSnapshots.getDocuments()
                            .get(queryDocumentSnapshots.size() - 1);
                }
                List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                List<GoodsInfo> goods = new ArrayList<>();
                for(DocumentSnapshot documentSnapshot:documentSnapshots){
                    GoodsInfo goodsInfo = documentSnapshot.toObject(GoodsInfo.class);
                    goods.add(goodsInfo);
                    Log.d(Config.TAG,"id==>"+goodsInfo.id);
                }
                Log.d(Config.TAG,"first page==>"+goods.size());
                mGoods.setValue(goods);
                mLoadingState.setValue(0);
            }
        });
    }

    //加载更多页面
    public void loadMore()
    {
        Log.d(Config.TAG,"start load more");
        Query query = FirebaseFirestore.getInstance()
            .collection(Constants.Collection.SNAPSHOT)
            .orderBy("createTime")
            .startAfter(lastVisible)
            .limit(getPageSize());
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                List<GoodsInfo> goods = new ArrayList<>();
                for(DocumentSnapshot documentSnapshot:documentSnapshots){
                    GoodsInfo goodsInfo = documentSnapshot.toObject(GoodsInfo.class);
                    goods.add(goodsInfo);
                    Log.d(Config.TAG,"id==>"+goodsInfo.id);
                }
                Log.d(Config.TAG,"loadMore size==>"+goods.size());
                mGoods.setValue(goods);
                mLoadingState.setValue(1);
            }
        });
    }


    public int getPageSize()
    {
        return 10;
    }

    public void refreshList()
    {
        loadFirstPage();
    }

}