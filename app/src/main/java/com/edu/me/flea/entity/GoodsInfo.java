package com.edu.me.flea.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class GoodsInfo implements Parcelable {
    public String id;
    public String detailId;
    public String title;
    public String creatorId;
    public String creatorName;
    public String creatorAvatar;
    public String cover;
    public String price;
    public long createTime;
    public int hotDegree;
    public int checkCount;

    public GoodsInfo(){}

    protected GoodsInfo(Parcel in) {
        id = in.readString();
        detailId = in.readString();
        title = in.readString();
        creatorId = in.readString();
        creatorName = in.readString();
        creatorAvatar = in.readString();
        cover = in.readString();
        price = in.readString();
        createTime = in.readLong();
        hotDegree = in.readInt();
        checkCount = in.readInt();
    }

    public static final Creator<GoodsInfo> CREATOR = new Creator<GoodsInfo>() {
        @Override
        public GoodsInfo createFromParcel(Parcel in) {
            return new GoodsInfo(in);
        }

        @Override
        public GoodsInfo[] newArray(int size) {
            return new GoodsInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(detailId);
        parcel.writeString(title);
        parcel.writeString(creatorId);
        parcel.writeString(creatorName);
        parcel.writeString(creatorAvatar);
        parcel.writeString(cover);
        parcel.writeString(price);
        parcel.writeLong(createTime);
        parcel.writeInt(hotDegree);
        parcel.writeInt(checkCount);
    }
}
