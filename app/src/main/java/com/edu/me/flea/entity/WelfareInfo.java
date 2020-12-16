package com.edu.me.flea.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class WelfareInfo implements Parcelable {
    public String id;
    public String title;
    public String cover;
    public String description;
    public long destination;
    public long current;
    public long hotDegree;
    public int progress;
    public Timestamp createTime;

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
        createTime = in.readParcelable(Timestamp.class.getClassLoader());
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(cover);
        dest.writeString(description);
        dest.writeLong(destination);
        dest.writeLong(current);
        dest.writeLong(hotDegree);
        dest.writeInt(progress);
        dest.writeParcelable(createTime, flags);
    }
}
