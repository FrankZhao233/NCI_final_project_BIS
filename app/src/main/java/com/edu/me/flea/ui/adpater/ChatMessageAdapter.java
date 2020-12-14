package com.edu.me.flea.ui.adpater;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.edu.me.flea.R;
import com.edu.me.flea.base.RecyclerAdapter;
import com.edu.me.flea.entity.MessageInfo;
import com.edu.me.flea.ui.ChatActivity;
import com.edu.me.flea.ui.holder.LeftMessageHolder;
import com.edu.me.flea.ui.holder.RightMessageHolder;

import java.util.List;


public class ChatMessageAdapter extends RecyclerAdapter<MessageInfo> {

    public ChatMessageAdapter() {
        super(null);
    }

    @Override
    public ViewHolder<MessageInfo> onCreateViewHolder(View root, int viewType) {
        if(R.layout.item_left_message_view == viewType){
            return new LeftMessageHolder(root);
        }else{
            return new RightMessageHolder(root);
        }
    }

    @Override
    public int getItemLayout(MessageInfo messageInfo, int position) {
        if(messageInfo.msgType == ChatActivity.TYPE_RECEIVER_MSG){
            return R.layout.item_left_message_view;
        }else{
            return R.layout.item_right_message_view;
        }
    }
}
