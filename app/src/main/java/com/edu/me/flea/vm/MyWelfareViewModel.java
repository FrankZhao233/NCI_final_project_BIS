package com.edu.me.flea.vm;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.edu.me.flea.base.BaseViewModel;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.ContributionInfo;
import com.edu.me.flea.entity.GoodsInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyWelfareViewModel extends BaseViewModel {

    private MutableLiveData<List<ContributionInfo>> mContributions = new MutableLiveData<>();

    public MyWelfareViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<ContributionInfo>> getContributions(){
        return mContributions;
    }

    public void queryContributions()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore.getInstance()
            .collection(Constants.Collection.CONTRIBUTION)
            .whereEqualTo("uid",user.getUid())
            .get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                    ArrayList<ContributionInfo> contributions = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot:documentSnapshots) {
                        ContributionInfo contribution = documentSnapshot.toObject(ContributionInfo.class);
                        if(contribution != null) {
                            contribution.id = documentSnapshot.getId();
                            Log.d("flea","contribution id===>"+contribution.id);
                            contributions.add(contribution);
                        }
                    }
                    mContributions.setValue(contributions);
                }
            });
    }
}
