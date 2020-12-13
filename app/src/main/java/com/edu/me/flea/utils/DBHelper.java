package com.edu.me.flea.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.ChatListInfo;
import com.edu.me.flea.entity.MessageInfo;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DBHelper {
    private static DBHelper sDBHelper;
    private final FirebaseDatabase database;

    private DBHelper() {
        database = FirebaseDatabase.getInstance();
    }

    public static DBHelper getInstance() {
        if (sDBHelper == null) {
            sDBHelper = new DBHelper();
        }
        return sDBHelper;
    }

    public FirebaseDatabase getDatabase()
    {
        return database;
    }

    /**
     * peer to peer way
     * send message
     * @param message the message to send
     * @param roomId room id of chat
     * */
    public void sendMessage(MessageInfo message, String roomId) {
        DatabaseReference messageRef = database.getReference(Constants.Reference.MESSAGES).child(roomId);
        messageRef.push().setValue(message);
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    public void updateUserInfo()
//    {
//        UserInfo info = new UserInfo();
//        info.id = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        Log.d("sss","user id ==>"+info.id);
//        info.uid = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
//        Log.d("sss","user uid ==>"+info.uid);
//        info.email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
//        Log.d("sss","user email ==>"+info.email);
//        database.getReference(Constants.Reference.USERS).child(info.id).setValue(info);
//    }

    public Task<Void> updateChatList(ChatListInfo chatListInfo, String from, String to)
    {
        String roomId = HelpUtils.getRoomId(from,to);
        database.getReference(Constants.Reference.CHAT_LIST)
                .child(from)
                .child(roomId)
                .setValue(chatListInfo);
        chatListInfo.userId = from;
        chatListInfo.nickName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        return database.getReference(Constants.Reference.CHAT_LIST)
                .child(to)
                .child(roomId)
                .setValue(chatListInfo);
    }

}
