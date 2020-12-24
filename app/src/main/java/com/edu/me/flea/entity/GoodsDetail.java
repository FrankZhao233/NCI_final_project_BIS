package com.edu.me.flea.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class GoodsDetail implements Parcelable {
    public String id;
    public String title;
    public String content;
    public String creatorId;
    public String creatorName;
    public String creatorAvatar;
    public ArrayList<String> images;
    public String tags;
    public String price;
    public long createTime;
    public int hotDegree;
    public int checkCount;
    public long dueTime;

    public GoodsDetail(){}

    protected GoodsDetail(Parcel in) {
        id = in.readString();
        title = in.readString();
        content = in.readString();
        creatorId = in.readString();
        creatorName = in.readString();
        creatorAvatar = in.readString();
        images = in.createStringArrayList();
        tags = in.readString();
        price = in.readString();
        createTime = in.readLong();
        hotDegree = in.readInt();
        checkCount = in.readInt();
        dueTime = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(creatorId);
        dest.writeString(creatorName);
        dest.writeString(creatorAvatar);
        dest.writeStringList(images);
        dest.writeString(tags);
        dest.writeString(price);
        dest.writeLong(createTime);
        dest.writeInt(hotDegree);
        dest.writeInt(checkCount);
        dest.writeLong(dueTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GoodsDetail> CREATOR = new Creator<GoodsDetail>() {
        @Override
        public GoodsDetail createFromParcel(Parcel in) {
            return new GoodsDetail(in);
        }

        @Override
        public GoodsDetail[] newArray(int size) {
            return new GoodsDetail[size];
        }
    };
}
