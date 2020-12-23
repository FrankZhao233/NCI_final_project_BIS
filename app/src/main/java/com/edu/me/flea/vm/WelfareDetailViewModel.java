package com.edu.me.flea.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseViewModel;
import com.edu.me.flea.base.SingleLiveEvent;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.WelfareInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WelfareDetailViewModel extends BaseViewModel {

    public SingleLiveEvent<Void> donateOver = new SingleLiveEvent<>();

    public WelfareDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void checkDonate(WelfareInfo welfareInfo)
    {
        showLoading(R.string.waiting_now);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore.getInstance().collection(Constants.Collection.CONTRIBUTION)
            .whereEqualTo("uid",user.getUid())
            .whereEqualTo("welfareId",welfareInfo.id)
            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                    if(!documentSnapshots.isEmpty()){
                        donateOver.call();
                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    closeLoading();
                }
            });
    }


    public void donate(String value, String message, WelfareInfo welfare)
    {
        showLoading(R.string.waiting_now);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Map<String,Object> info = new HashMap<>();
        info.put("uid",user.getUid());
        info.put("welfareId",welfare.id);
        info.put("welfareTitle",welfare.title);
        info.put("nickName",user.getDisplayName());
        info.put("value",value);
        info.put("message",message);
        info.put("createTime",message);
        FirebaseFirestore.getInstance()
            .collection(Constants.Collection.CONTRIBUTION)
            .add(info)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    welfare.current = welfare.current + Integer.parseInt(value);
                    welfare.hotDegree = welfare.hotDegree + 1;
                    welfare.progress = (int)(welfare.current*100/welfare.destination);
                    updateWelfare(welfare);
                }
            }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                closeLoading();
            }
        });
    }

    public void updateWelfare(WelfareInfo info)
    {
        Map<String,Object> updates = new HashMap<>();
        updates.put("hotDegree", info.hotDegree);
        updates.put("current", info.current);
        updates.put("progress", info.progress);

        FirebaseFirestore.getInstance()
            .collection(Constants.Collection.WELFARE)
            .document(info.id)
            .update(updates)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    donateOver.call();
                }
            }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                closeLoading();
            }
        });
    }
}
