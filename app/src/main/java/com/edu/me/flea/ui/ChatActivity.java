package com.edu.me.flea.ui;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.lifecycle.Observer;
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
import com.edu.me.flea.entity.ChatParams;
import com.edu.me.flea.entity.MessageInfo;
import com.edu.me.flea.service.MessageService;
import com.edu.me.flea.ui.adpater.ChatMessageAdapter;
import com.edu.me.flea.utils.DBHelper;
import com.edu.me.flea.utils.HelpUtils;
import com.edu.me.flea.utils.PreferencesUtils;
import com.edu.me.flea.vm.ChatViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = Config.Page.CHAT)
public class ChatActivity extends BaseActivity<ChatViewModel> {

    public final static int TYPE_RECEIVER_MSG = 0x21;
    public final static int TYPE_SENDER_MSG = 0x22;

    @BindView(R.id.sendBtn)
    Button sendBtn;

    @BindView(R.id.msgEt)
    EditText msgEt;

    @BindView(R.id.messageList)
    RecyclerView chatRv;

    private ChatMessageAdapter mAdapter;
    private String mRoomId;
    private String mPeerUid;

    @Autowired(name = Constants.ExtraName.CHAT_PARAM)
    ChatParams mChatParams;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_detail;
    }

    @Override
    protected void initView() {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mPeerUid = mChatParams.peerUid;
        String from = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRoomId = HelpUtils.getRoomId(from,mPeerUid);

        MessageService.setIfNeedNotify(false);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String key = HelpUtils.getRoomId(uid,mPeerUid);
        PreferencesUtils.putBoolean(this, Constants.PrefKey.HAS_MESSAGE+key,false);

        chatRv.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        mAdapter = new ChatMessageAdapter();
        chatRv.setAdapter(mAdapter);

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
                chatListInfo.nickName = mChatParams.peerNickName;
                chatListInfo.lastMessage = msg;
                chatListInfo.timeStamp = String.valueOf(System.currentTimeMillis());
                DBHelper.getInstance().updateChatList(chatListInfo,from,mPeerUid);
            }
        });


        mViewModel.getAddMessage().observe(this, new Observer<MessageInfo>() {
            @Override
            public void onChanged(MessageInfo messageInfo) {
                mAdapter.add(messageInfo);
            }
        });
    }

    @Override
    public void initData() {
        mViewModel.loadMessage(mRoomId);
    }

    @Override
    protected void inject() {
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
    }


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
    }


}