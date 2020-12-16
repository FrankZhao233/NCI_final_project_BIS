package com.edu.me.flea.vm;

import android.app.Activity;
import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseViewModel;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.utils.ToastUtils;
import com.edu.me.flea.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import top.zibin.luban.Luban;
import top.zibin.luban.OnRenameListener;

import static com.edu.me.flea.config.Config.TAG;

public class GoodsAuctionViewModel extends BaseViewModel {

    private StorageReference mStorageRef;
    int uploadCount = 0 ;

    public GoodsAuctionViewModel(@NonNull Application application) {
        super(application);
        mStorageRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(Config.FIREBASE_STORAGE_URL);
    }


    /**
     * publish goods step:
     * 1. upload images of goods
     * 2. create goods detail record
     * 3. create snapshot for goods detail record
     * */
    public void publish(final Activity activity, final String title, final String content,
                        List<String> photos, final String price, final String tags,final long dueTime)
    {
        showLoading(R.string.publish_now);
        Disposable d = Flowable.just(photos)
                .observeOn(Schedulers.io())
                .map(new Function<List<String>, List<File>>() {
                    @Override
                    public List<File> apply(@NonNull List<String> list) throws Exception {
                        Log.d(TAG,"compress photos=>");
                        List<File> results = Luban.with(activity)
                                .load(list).ignoreBy(100)
                                .setTargetDir(Utils.getTargetDir(activity))
                                .setRenameListener(new OnRenameListener() {
                                    @Override
                                    public String rename(String filePath) {
                                        String fileName = UUID.randomUUID().toString().replace("-","");
                                        return fileName + ".jpeg";
                                    }
                                }).get();
                        Log.d(TAG,"compress result=>"+results);
                        return results;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<File>>() {
                    @Override
                    public void accept(List<File> files) throws Exception {
                        postInner(activity,title,content,files,price,tags,dueTime);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        closeLoading();
                    }
                });
        addDisposable(d);
    }

    private void postInner(final Activity activity, String title,String content, List<File> files,
                           String price, String tags,final long dueTime)
    {
        uploadCount = 0 ;
        for(File file:files){
            Log.d(TAG,"cache file==>"+file.getAbsolutePath()+",name="+file.getName());
            String name = file.getName();
            Uri uri = Uri.fromFile(file);
            final StorageReference photoRef = mStorageRef.child("goods/"+name);
            photoRef.putFile(uri)
                .addOnSuccessListener(activity, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG,"upload success");
                        ++uploadCount;
                        if(uploadCount >= files.size()){
                            createGoodsDetail(title,content,files,price,tags,dueTime);
                        }
                    }
                }).addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"upload failed e==>"+e);
                        closeLoading();
                    }
                });
        }
    }

    /**
     * insert goods detail to database
     * **/
    private void createGoodsDetail(String title,String content, List<File> files, String price,
                                   String tags,final long dueTime)
    {
        FirebaseUser user =  FirebaseAuth.getInstance().getCurrentUser();
        final Map<String, Object> detail = new HashMap<>();
        String detailId = UUID.randomUUID().toString();
        detail.put("id", detailId);
        detail.put("title", title);
        detail.put("content", content);
        detail.put("creatorName",user.getDisplayName());
        detail.put("creatorId",user.getUid());
        detail.put("price",price);
        detail.put("tags",tags);
        detail.put("createTime",System.currentTimeMillis());
        detail.put("hotDegree",0);
        detail.put("checkCount",0);
        detail.put("dueTime",dueTime);
        List<String> imgs = new ArrayList<>();
        for(File file:files){
            imgs.add(file.getName());
        }
        detail.put("images",imgs);
        String cover = imgs.get(0);
        Log.d(TAG,"create goods detail now");
        FirebaseFirestore.getInstance().collection(Constants.Collection.GOODS).document(detailId)
            .set(detail)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "create good detail successfully written!");
                    createSnapShot(cover,detail);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "create good detail Error writing document", e);
                    closeLoading();
                }
            });
    }

    /**
     * create snapshot for the goods detail
     * */
    private void createSnapShot(String cover,Map<String, Object> detail)
    {
        final Map<String, Object> snapshot = new HashMap<>();
        String id = UUID.randomUUID().toString();
        snapshot.put("id", id);
        snapshot.put("detailId", detail.get("id"));
        snapshot.put("title", detail.get("title"));
        snapshot.put("creatorName",detail.get("creatorName"));
        snapshot.put("creatorId",detail.get("creatorId"));
        snapshot.put("price",detail.get("price"));
        snapshot.put("tags",detail.get("tags"));
        snapshot.put("cover",cover);
        snapshot.put("createTime",detail.get("createTime"));
        snapshot.put("dueTime",detail.get("dueTime"));
        snapshot.put("hotDegree",0);
        snapshot.put("checkCount",0);
        Log.d(TAG,"create goods snapshot now");
        Task<Void> task = FirebaseFirestore.getInstance().collection(Constants.Collection.SNAPSHOT).document(id)
            .set(snapshot)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "create snapshot successfully!");
                    ToastUtils.showShort("publish successful");
                    postEvent(Constants.Event.PUBLISH_OVER);
                    closeLoading();
                    closePage();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "create snapshot Error writing document", e);
                    closeLoading();
                }
            });
    }
}
