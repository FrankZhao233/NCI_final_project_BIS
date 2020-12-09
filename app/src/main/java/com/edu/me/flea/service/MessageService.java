package com.edu.me.flea.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.edu.me.flea.R;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.ChatListInfo;
import com.edu.me.flea.ui.ChatActivity;
import com.edu.me.flea.utils.DBHelper;
import com.edu.me.flea.utils.PreferencesUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;


public class MessageService extends Service {
    private static boolean bNeedNotify = true;

    public MessageService() {
    }

    public static void setIfNeedNotify(boolean need)
    {
        bNeedNotify = need;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        DBHelper.getInstance().getDatabase().getReference(Constants.Reference.CHAT_LIST)
                .child(uid).addChildEventListener(mChatListValueLister);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private ChildEventListener mChatListValueLister = new ChildEventListener() {

        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            String key = snapshot.getKey();
            ChatListInfo info = snapshot.getValue(ChatListInfo.class);
            if(info != null){
                PreferencesUtils.putBoolean(getBaseContext(),Constants.PrefKey.HAS_MESSAGE+key,true);
                if(bNeedNotify) {
                    notifyMessage(info);
                }
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        DBHelper.getInstance().getDatabase().getReference(Constants.Reference.CHAT_LIST)
                .child(uid).removeEventListener(mChatListValueLister);
    }

    public void notifyMessage(ChatListInfo info)
    {
        Log.d("sss","notify message:"+info.lastMessage);

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("uid",info.userId);
        intent.putExtra("email", info.email);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String id = "chat";
            CharSequence name = "notification channel";
            String description = "notification description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.GREEN);
            mChannel.enableVibration(false);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,
                intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this,"chat") //引用加上channelid
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Message")
                .setContentText(info.lastMessage)
                .setContentIntent(pendingIntent)
                .build();
        }else{
            notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Message")
                .setContentText(info.lastMessage)
                .setContentIntent(pendingIntent)
                .build();
        }

        mNotificationManager.notify(1,notification);
    }
}
