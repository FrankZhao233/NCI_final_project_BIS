package com.edu.me.flea.entity;

public class MessageInfo {
    public String msg;
    public long timeStamp;
    public int msgType;
    public int avatarRes;
    public String creatorId;
    public String creatorName;

    public MessageInfo(){

    }

    public MessageInfo(String msg, long timeStamp, int avatarRes , int msgType) {
        this.msg = msg;
        this.timeStamp = timeStamp;
        this.avatarRes = avatarRes;
        this.msgType = msgType;
    }
}
