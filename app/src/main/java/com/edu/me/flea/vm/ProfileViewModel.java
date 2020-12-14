package com.edu.me.flea.vm;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.edu.me.flea.base.BaseViewModel;
import com.edu.me.flea.base.SingleLiveEvent;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;

public class ProfileViewModel extends BaseViewModel {
    private SingleLiveEvent<String> mUploadAvatar = new SingleLiveEvent<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);
    }

    public SingleLiveEvent<String> getUploadAvatar(){
        return mUploadAvatar;
    }

    public void uploadAvatar(Context context, String path) {

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
            }
        });
    }
}
