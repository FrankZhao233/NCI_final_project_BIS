package com.edu.me.flea.ui.fragment;


import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.lifecycle.Observer;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseFragment;
import com.edu.me.flea.base.CommonAdapter;
import com.edu.me.flea.base.ViewHolder;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.ChatListInfo;
import com.edu.me.flea.entity.ChatParams;
import com.edu.me.flea.utils.HelpUtils;
import com.edu.me.flea.utils.PreferencesUtils;
import com.edu.me.flea.vm.NotificationsViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationsFragment extends BaseFragment<NotificationsViewModel> {

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.chatListLv)
    ListView chatLv;

    CommonAdapter<ChatListInfo> mAdapter;


    public static NotificationsFragment newInstance()
    {
        return new NotificationsFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_notifications;
    }

    @Override
    protected void inject() {
        super.inject();
        ButterKnife.bind(this,getView());
    }

    @Override
    protected void initView() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mAdapter = new CommonAdapter<ChatListInfo>(R.layout.item_chat_list_view,mViewModel.getData().getValue()) {
            @Override
            public void convert(ViewHolder holder, ChatListInfo item, int position) {
                RequestOptions requestOptions = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                ImageView imageView = holder.getImageView(R.id.avatarIv);
                Glide.with(getActivity())
                        .load(R.drawable.stranger)
                        .apply(requestOptions)
                        .into(imageView);
                holder.setTvText(R.id.idTv,item.nickName);
                holder.setTvText(R.id.messageTv,item.lastMessage);
                holder.setTvText(R.id.timeTv, HelpUtils.formatDate(item.timeStamp));
                if(hasMessage(item)) {
                    holder.setVisibility(R.id.redView, View.VISIBLE);
                }else{
                    holder.setVisibility(R.id.redView, View.GONE);
                }
            }
        };
        chatLv.setAdapter(mAdapter);

        chatLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ChatListInfo info = (ChatListInfo) adapterView.getAdapter().getItem(i);
                if(info != null){
                    ChatParams chatParams = new ChatParams();
                    chatParams.peerUid = info.userId;
                    chatParams.peerNickName = info.nickName;
                    chatParams.cover = "";
                    chatParams.price = "";
                    chatParams.title = "";
                    ARouter.getInstance().build(Config.Page.CHAT)
                            .withParcelable(Constants.ExtraName.CHAT_PARAM,chatParams)
                            .navigation();
                }
            }
        });
    }

    private boolean hasMessage(ChatListInfo chatListInfo)
    {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String key = HelpUtils.getRoomId(uid,chatListInfo.userId);
        return PreferencesUtils.getBoolean(getActivity(), Constants.PrefKey.HAS_MESSAGE+key,false);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {
        mViewModel.getData().observe(this, new Observer<List<ChatListInfo>>() {
            @Override
            public void onChanged(List<ChatListInfo> chatList) {
                mAdapter.refreshView(chatList);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}