package com.edu.me.flea.vm;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.alibaba.android.arouter.launcher.ARouter;
import com.edu.me.flea.base.BaseViewModel;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.ChatListInfo;
import com.edu.me.flea.utils.DBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationsViewModel extends BaseViewModel {

    private MutableLiveData<List<ChatListInfo>> mItemData  =new MutableLiveData<>();

    public NotificationsViewModel(Application application) {
        super(application);
    }

    public LiveData<List<ChatListInfo>> getData()
    {
        return mItemData;
    }


    @Override
    public void onCreate(LifecycleOwner owner) {
        super.onCreate(owner);
        loadRefresh();
    }

    public void loadRefresh()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            ARouter.getInstance().build(Config.Page.LOGIN).navigation();
        }else {
            String uid = user.getUid();
            Log.d(Config.TAG,"UID=====>"+uid);
            DBHelper.getInstance().getDatabase().getReference(Constants.Reference.CHAT_LIST).child(uid)
                    .addValueEventListener(mValueListener);
        }
    }

    private ValueEventListener mValueListener = new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Log.d(Config.TAG,"notification onDataChange==>");
            GenericTypeIndicator<HashMap<String, ChatListInfo>> type = new GenericTypeIndicator<HashMap<String,ChatListInfo>>() {};
            HashMap<String,ChatListInfo> chatLists = snapshot.getValue(type);
            List<ChatListInfo> result = new ArrayList<>();
            if(chatLists != null) {
                for(Map.Entry<String, ChatListInfo> entry:chatLists.entrySet()){
                    result.add(entry.getValue());
                }
            }
            mItemData.setValue(result);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.d(Config.TAG,"error==>"+error.getMessage());
        }
    };

    public ChatListInfo getUserInfo(int pos)
    {
        List<ChatListInfo> list = mItemData.getValue();
        if(list != null){
            return list.get(pos);
        }
        return null;
    }


    @Override
    public void onDestroy(LifecycleOwner owner) {
        super.onDestroy(owner);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(!TextUtils.isEmpty(uid)) {
            DBHelper.getInstance().getDatabase()
                    .getReference(Constants.Reference.CHAT_LIST)
                    .child(uid)
                    .removeEventListener(mValueListener);
        }
    }
}

