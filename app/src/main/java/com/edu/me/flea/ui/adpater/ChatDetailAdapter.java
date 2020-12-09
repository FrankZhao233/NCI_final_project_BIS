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
import com.edu.me.flea.entity.MessageInfo;
import com.edu.me.flea.ui.ChatActivity;

import java.util.List;


public class ChatDetailAdapter extends RecyclerView.Adapter<ChatDetailAdapter.MsgViewHolder> {

    private List<MessageInfo> listData;
    private Context context;

    public ChatDetailAdapter(Context context, List<MessageInfo> listData) {
        this.context = context;
        this.listData = listData;
    }

    @Override
    public MsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_msg_list, parent, false);
        return new MsgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MsgViewHolder holder, int position) {
        MessageInfo currentMsgData = listData.get(position);
        MessageInfo preMsgData = null;
        if (position >= 1)
            preMsgData = listData.get(position - 1);
        switch (currentMsgData.msgType) {
            case ChatActivity.TYPE_RECEIVER_MSG:
                initTimeStamp(holder, currentMsgData, preMsgData);
                holder.senderLayout.setVisibility(View.GONE);
                holder.receiverLayout.setVisibility(View.VISIBLE);
                holder.receiveMsg.setText(currentMsgData.msg);
                holder.receiver_profile.setImageResource(currentMsgData.avatarRes);
                break;

            case ChatActivity.TYPE_SENDER_MSG:
                initTimeStamp(holder, currentMsgData, preMsgData);
                holder.senderLayout.setVisibility(View.VISIBLE);
                holder.receiverLayout.setVisibility(View.GONE);
                holder.sendMsg.setText(currentMsgData.msg);
                holder.send_profile.setImageResource(currentMsgData.avatarRes);
                break;
        }
    }

    private void initTimeStamp(MsgViewHolder holder, MessageInfo currentMsgData, MessageInfo preMsgData) {
        holder.timeStamp.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class MsgViewHolder extends RecyclerView.ViewHolder {

        ImageView receiver_profile, send_profile;
        TextView timeStamp, receiveMsg, sendMsg;
        RelativeLayout senderLayout;
        LinearLayout receiverLayout;

        public MsgViewHolder(View itemView) {
            super(itemView);
            receiver_profile =  itemView.findViewById(R.id.item_wechat_msg_iv_receiver_profile);
            send_profile =  itemView.findViewById(R.id.item_wechat_msg_iv_sender_profile);
            timeStamp =  itemView.findViewById(R.id.item_wechat_msg_iv_time_stamp);
            receiveMsg =  itemView.findViewById(R.id.item_wechat_msg_tv_receiver_msg);
            sendMsg =  itemView.findViewById(R.id.item_wechat_msg_tv_sender_msg);
            senderLayout =  itemView.findViewById(R.id.item_wechat_msg_layout_sender);
            receiverLayout =  itemView.findViewById(R.id.item_wechat_msg_layout_receiver);
        }
    }
}
