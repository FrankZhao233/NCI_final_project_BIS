package com.edu.me.flea.vm;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.edu.me.flea.base.BaseViewModel;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.GoodsInfo;
import com.edu.me.flea.entity.WelfareInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class WelfareViewModel extends BaseViewModel {

    private MutableLiveData<List<WelfareInfo>> mWelfare = new MutableLiveData<>();

    public WelfareViewModel(Application application) {
        super(application);
    }


    public LiveData<List<WelfareInfo>> getWelfareList(){
        return mWelfare;
    }

    public void queryWelfare()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore.getInstance()
            .collection(Constants.Collection.WELFARE)
            .get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                    ArrayList<WelfareInfo> welfareInfos = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot:documentSnapshots) {
                        WelfareInfo welfareInfo = documentSnapshot.toObject(WelfareInfo.class);
                        welfareInfos.add(welfareInfo);
                    }
                    mWelfare.setValue(welfareInfos);
                }
            });
    }

}