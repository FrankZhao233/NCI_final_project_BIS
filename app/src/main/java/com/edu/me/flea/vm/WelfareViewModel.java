package com.edu.me.flea.vm;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.edu.me.flea.base.BaseViewModel;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.BannerInfo;
import com.edu.me.flea.entity.WelfareInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class WelfareViewModel extends BaseViewModel {

    private MutableLiveData<List<WelfareInfo>> mWelfare = new MutableLiveData<>();
    private MutableLiveData<List<BannerInfo>> mBanners = new MutableLiveData<>();

    public WelfareViewModel(Application application) {
        super(application);
    }

    public LiveData<List<WelfareInfo>> getWelfareList(){
        return mWelfare;
    }

    public LiveData<List<BannerInfo>> getBanner(){
        return mBanners;
    }

    @Override
    public void onCreate(LifecycleOwner owner) {
        super.onCreate(owner);
        queryData();
    }

    public void queryWelfare()
    {
        FirebaseFirestore.getInstance().collection(Constants.Collection.WELFARE)
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    List<DocumentSnapshot> documentSnapshots = value.getDocuments();
                    ArrayList<WelfareInfo> welfareInfos = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot:documentSnapshots) {
                        WelfareInfo welfareInfo = documentSnapshot.toObject(WelfareInfo.class);
                        welfareInfos.add(welfareInfo);
                    }
                    mWelfare.setValue(welfareInfos);
                }
            });
    }

    public void loadRefresh()
    {
        queryData();
    }

    public void queryData()
    {
        FirebaseFirestore.getInstance().collection(Constants.Collection.BANNER)
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    queryWelfare();
                    List<DocumentSnapshot> documentSnapshots = value.getDocuments();
                    ArrayList<BannerInfo> bannerInfos = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot:documentSnapshots) {
                        BannerInfo bannerInfo = documentSnapshot.toObject(BannerInfo.class);
                        bannerInfos.add(bannerInfo);
                    }
                    mBanners.setValue(bannerInfos);
                }
            });
    }

}