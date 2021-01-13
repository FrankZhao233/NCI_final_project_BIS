package com.edu.me.flea.entity;


import android.os.Parcel;
import android.os.Parcelable;

public class BannerInfo implements Parcelable {
    public String link;
    public String title;
    public String type;
    public String description;
    public String params;

    public BannerInfo(){}

    protected BannerInfo(Parcel in) {
        link = in.readString();
        title = in.readString();
        type = in.readString();
        description = in.readString();
        params = in.readString();
    }

    public static final Creator<BannerInfo> CREATOR = new Creator<BannerInfo>() {
        @Override
        public BannerInfo createFromParcel(Parcel in) {
            return new BannerInfo(in);
        }

        @Override
        public BannerInfo[] newArray(int size) {
            return new BannerInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(link);
        parcel.writeString(title);
        parcel.writeString(type);
        parcel.writeString(description);
        parcel.writeString(params);
    }
}
