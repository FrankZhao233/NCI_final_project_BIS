package com.edu.me.flea.vm;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseViewModel;
import com.edu.me.flea.base.SingleLiveEvent;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.ContributionInfo;
import com.edu.me.flea.entity.UserInfo;
import com.edu.me.flea.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;

public class ProfileEditorViewModel extends BaseViewModel {
    private SingleLiveEvent<Void> mOperateOver = new SingleLiveEvent();
    private MutableLiveData<UserInfo> mUser = new MutableLiveData();
    private SingleLiveEvent<String> mUploadAvatar = new SingleLiveEvent<>();


    public ProfileEditorViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<UserInfo> getUser(){
        return mUser;
    }

    public LiveData<Void> getOperation(){
        return mOperateOver;
    }

    public SingleLiveEvent<String> getUploadAvatar(){
        return mUploadAvatar;
    }

    public void queryUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore.getInstance()
            .collection(Constants.Collection.USER)
            .document(user.getUid())
            .get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    UserInfo userInfo = documentSnapshot.toObject(UserInfo.class);
                    mUser.setValue(userInfo);
                }
            });
    }

    public void saveUser(String gender,String signature,String address,String hobby)
    {
        showLoading(R.string.waiting_now);
        Map<String,Object> updates = new HashMap<>();
        updates.put("gender",gender);
        updates.put("signature",signature);
        updates.put("address",address);
        updates.put("hobby",hobby);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore.getInstance()
            .collection(Constants.Collection.USER)
            .document(user.getUid())
            .update(updates)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mOperateOver.call();
                }
            }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                closeLoading();
            }
        });
    }


    public void uploadAvatar(Context context, String path) {
        showLoading(R.string.upload_avatar_now);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Luban.with(context)
                .load(new File(path))
                .ignoreBy(30)
                .setTargetDir(Utils.getTargetDir(context))
                .setRenameListener(new OnRenameListener() {
                    @Override
                    public String rename(String filePath) {
                        return user.getUid() + ".jpeg";
                    }
                }).setCompressListener(new OnCompressListener() {
            @Override
            public void onStart() {
                Log.d(Config.TAG, "start compress image , thread=" +
                        Thread.currentThread().getName());
            }

            @Override
            public void onSuccess(File file) {
                Log.d(Config.TAG, "compress image over, thread=" +
                        Thread.currentThread().getName());
                uploadFile(file);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(Config.TAG, "compress image exception ==>" + e.getMessage());
                closeLoading();
            }
        }).launch();
    }

    private void uploadFile(File file)
    {
        StorageReference mStorageRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(Config.FIREBASE_STORAGE_URL);
        Log.d(Config.TAG,"cache file==>"+file.getAbsolutePath()+",name="+file.getName());
        String name = file.getName();
        Uri uri = Uri.fromFile(file);
        final StorageReference photoRef = mStorageRef.child(Config.StorageSpace.AVATAR+name);
        photoRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(Config.TAG,"upload success");
                mUploadAvatar.call();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(Config.TAG,"upload failed e==>"+e);
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Log.d(Config.TAG,"upload complete==>");
                closeLoading();
            }
        });
    }

}
