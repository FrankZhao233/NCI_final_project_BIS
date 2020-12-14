package com.edu.me.flea.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatParams implements Parcelable {
    public String peerUid;
    public String peerNickName;
    public String detailId;
    public String price;
    public String title;
    public String cover;

    public ChatParams(){}

    protected ChatParams(Parcel in) {
        peerUid = in.readString();
        peerNickName = in.readString();
        detailId = in.readString();
        price = in.readString();
        title = in.readString();
        cover = in.readString();
    }

    public static final Creator<ChatParams> CREATOR = new Creator<ChatParams>() {
        @Override
        public ChatParams createFromParcel(Parcel in) {
            return new ChatParams(in);
        }

        @Override
        public ChatParams[] newArray(int size) {
            return new ChatParams[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(peerUid);
        parcel.writeString(peerNickName);
        parcel.writeString(detailId);
        parcel.writeString(price);
        parcel.writeString(title);
        parcel.writeString(cover);
    }
}
