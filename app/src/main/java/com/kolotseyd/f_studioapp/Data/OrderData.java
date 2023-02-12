package com.kolotseyd.f_studioapp.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderData implements Parcelable {

    private String name;
    private String formatAndType;
    private String value;

    public OrderData(String name, String formatAndType, String value) {
        this.name = name;
        this.formatAndType = formatAndType;
        this.value = value;
    }

    protected OrderData(Parcel in) {
        name = in.readString();
        formatAndType = in.readString();
        value = in.readString();
    }

    public static final Creator<OrderData> CREATOR = new Creator<OrderData>() {
        @Override
        public OrderData createFromParcel(Parcel in) {
            return new OrderData(in);
        }

        @Override
        public OrderData[] newArray(int size) {
            return new OrderData[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormatAndType() {
        return formatAndType;
    }

    public void setFormatAndType(String formatAndType) {
        this.formatAndType = formatAndType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(formatAndType);
        parcel.writeString(value);
    }
}
