package com.edu.me.flea.ui.fragment;


import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseFragment;
import com.edu.me.flea.base.CommonAdapter;
import com.edu.me.flea.base.ViewHolder;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.ChatListInfo;
import com.edu.me.flea.utils.HelpUtils;
import com.edu.me.flea.utils.PreferencesUtils;
import com.edu.me.flea.vm.NotificationsViewModel;
import com.google.firebase.auth.FirebaseAuth;

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
                        .load(R.drawable.hdimg_4)
                        .apply(requestOptions)
                        .into(imageView);
                holder.setTvText(R.id.idTv,item.email);
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
    }

    private boolean hasMessage(ChatListInfo chatListInfo)
    {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String key = HelpUtils.getRoomId(uid,chatListInfo.userId);
        return PreferencesUtils.getBoolean(getActivity(), Constants.PrefKey.HAS_MESSAGE+key,false);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {

    }
}