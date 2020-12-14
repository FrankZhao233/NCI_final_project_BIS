package com.edu.me.flea.ui.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.edu.me.flea.R;
import com.edu.me.flea.base.RecyclerAdapter;
import com.edu.me.flea.entity.MessageInfo;

public class RightMessageHolder extends RecyclerAdapter.ViewHolder<MessageInfo>{

    ImageView avatarIv;
    TextView messageTv;

    public RightMessageHolder(View itemView) {
        super(itemView);
        avatarIv = itemView.findViewById(R.id.senderAvatarIv);
        messageTv = itemView.findViewById(R.id.senderMessageTv);
    }

    @Override
    protected void onBind(MessageInfo messageInfo) {
        messageTv.setText(messageInfo.msg);
    }
}
