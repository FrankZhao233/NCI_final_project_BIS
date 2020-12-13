package com.edu.me.flea.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseActivity;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.ChatListInfo;
import com.edu.me.flea.entity.GoodsDetail;
import com.edu.me.flea.entity.MessageInfo;
import com.edu.me.flea.service.MessageService;
import com.edu.me.flea.ui.adpater.ChatDetailAdapter;
import com.edu.me.flea.utils.DBHelper;
import com.edu.me.flea.utils.HelpUtils;
import com.edu.me.flea.utils.PreferencesUtils;
import com.edu.me.flea.vm.ChatViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = Config.Page.CHAT)
public class ChatActivity extends BaseActivity<ChatViewModel> {
    public final static int TYPE_RECEIVER_MSG = 0x21;
    public final static int TYPE_SENDER_MSG = 0x22;
    private int profileId = R.drawable.hdimg_4;
    @BindView(R.id.sendBtn)
    Button sendBtn;

    @BindView(R.id.msgEt)
    EditText msgEt;

    private List<MessageInfo> mChatMessages = new ArrayList<>();
    private ChatDetailAdapter mAdapter;
    private String mRoomId;
    private String mPeerUid;

    @Autowired(name = Constants.ExtraName.GOODS_DETAIL)
    GoodsDetail mGoodsDetail;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_detail;
    }

    @Override
    protected void initView() {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mPeerUid = mGoodsDetail.creatorId;
        String from = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRoomId = HelpUtils.getRoomId(from,mPeerUid);
        setListener();
        initData();
        MessageService.setIfNeedNotify(false);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String key = HelpUtils.getRoomId(uid,mPeerUid);
        PreferencesUtils.putBoolean(this, Constants.PrefKey.HAS_MESSAGE+key,false);
    }

    @Override
    public void setListener()
    {
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = msgEt.getText().toString();
                if(TextUtils.isEmpty(msg)){
                    return ;
                }
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String from = currentUser.getUid();
                MessageInfo msgInfo = new MessageInfo();
                msgInfo.msg = msg;
                msgInfo.timeStamp = System.currentTimeMillis();
                msgInfo.creatorId = from;
                msgInfo.creatorName = currentUser.getDisplayName();
                DBHelper.getInstance().sendMessage(msgInfo,mRoomId);
                msgEt.setText("");

                ChatListInfo chatListInfo = new ChatListInfo();
                chatListInfo.userId = mPeerUid;
                chatListInfo.nickName = mGoodsDetail.creatorName;
                chatListInfo.lastMessage = msg;
                chatListInfo.timeStamp = String.valueOf(System.currentTimeMillis());
                DBHelper.getInstance().updateChatList(chatListInfo,from,mPeerUid);
            }
        });
    }

    @Override
    public void initData() {
        RecyclerView rv = findViewById(R.id.messageList);
        rv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ChatDetailAdapter(this, mChatMessages);
        rv.setAdapter(mAdapter);
        DBHelper.getInstance().getDatabase().getReference(Constants.Reference.MESSAGES)
            .child(mRoomId)
            .addValueEventListener(mMessageListener);
    }

    @Override
    protected void inject() {
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
    }

    private ValueEventListener mMessageListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            GenericTypeIndicator<HashMap<String,MessageInfo>> type = new GenericTypeIndicator<HashMap<String,MessageInfo>>() {};
            HashMap<String,MessageInfo> messages = snapshot.getValue(type);
            if(messages != null) {
                mChatMessages.clear();
                String from = FirebaseAuth.getInstance().getCurrentUser().getUid();
                for (Map.Entry<String, MessageInfo> entry : messages.entrySet()) {
                    MessageInfo info = entry.getValue();
                    if (from.equals(info.creatorId)) {
                        info.msgType = TYPE_SENDER_MSG;
                        info.avatarRes = profileId;
                    } else {
                        info.msgType = TYPE_RECEIVER_MSG;
                        info.avatarRes = R.drawable.hdimg_4;
                    }
                    mChatMessages.add(info);
                }
                Collections.sort(mChatMessages,messageComparator);
                mAdapter.notifyDataSetChanged();
            }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageService.setIfNeedNotify(true);
        DBHelper.getInstance().getDatabase()
            .getReference(Constants.Reference.MESSAGES)
            .child(mRoomId)
            .removeEventListener(mMessageListener);
    }


}