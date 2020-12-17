package com.edu.me.flea.vm;

import android.app.Application;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseViewModel;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.ChatListInfo;
import com.edu.me.flea.entity.MessageInfo;
import com.edu.me.flea.utils.DBHelper;
import com.edu.me.flea.utils.PreferencesUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.edu.me.flea.ui.ChatActivity.TYPE_RECEIVER_MSG;
import static com.edu.me.flea.ui.ChatActivity.TYPE_SENDER_MSG;

public class ChatViewModel extends BaseViewModel {
    private MutableLiveData<MessageInfo> mAddMessage = new MutableLiveData<>();

    private String mRoomId;

    public ChatViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadMessage(String roomId)
    {
        mRoomId = roomId;

        DBHelper.getInstance().getDatabase().getReference(Constants.Reference.MESSAGES)
                .child(roomId).addChildEventListener(mChatValueLister);
    }

    public LiveData<MessageInfo> getAddMessage()
    {
        return mAddMessage;
    }

    private ChildEventListener mChatValueLister = new ChildEventListener() {

        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            MessageInfo info = snapshot.getValue(MessageInfo.class);
            String from = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if (from.equals(info.creatorId)) {
                info.msgType = TYPE_SENDER_MSG;
            } else {
                info.msgType = TYPE_RECEIVER_MSG;
            }
            info.avatarRes = R.drawable.stranger;
            mAddMessage.setValue(info);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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

    private Comparator<MessageInfo> messageComparator = new Comparator<MessageInfo>() {
        @Override
        public int compare(MessageInfo o1, MessageInfo o2) {
            if(o1.timeStamp > o2.timeStamp){
                return 1;
            }else if(o1.timeStamp < o2.timeStamp){
                return -1;
            }
            return 0;
        }
    };

    @Override
    public void onDestroy(LifecycleOwner owner) {
        super.onDestroy(owner);

        DBHelper.getInstance().getDatabase()
                .getReference(Constants.Reference.MESSAGES)
                .child(mRoomId).removeEventListener(mChatValueLister);
    }
}
