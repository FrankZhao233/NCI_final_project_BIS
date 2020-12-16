package com.edu.me.flea.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class WelfareInfo implements Parcelable {
    public String id;
    public String title;
    public String cover;
    public String description;
    public long destination;
    public long current;
    public long hotDegree;
    public int progress;
    public String createTime;

    public WelfareInfo(){}

    protected WelfareInfo(Parcel in) {
        id = in.readString();
        title = in.readString();
        cover = in.readString();
        description = in.readString();
        destination = in.readLong();
        current = in.readLong();
        hotDegree = in.readLong();
        progress = in.readInt();
        createTime = in.readString();
    }

    public static final Creator<WelfareInfo> CREATOR = new Creator<WelfareInfo>() {
        @Override
        public WelfareInfo createFromParcel(Parcel in) {
            return new WelfareInfo(in);
        }

        @Override
        public WelfareInfo[] newArray(int size) {
            return new WelfareInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(cover);
        parcel.writeString(description);
        parcel.writeLong(destination);
        parcel.writeLong(current);
        parcel.writeLong(hotDegree);
        parcel.writeInt(progress);
        parcel.writeString(createTime);
    }
}
