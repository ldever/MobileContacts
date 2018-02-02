package com.example.jiashunz.mobilecontacts;

/**
 * Created by zhoujiashun on 1/31/18.
 */

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {
    public String phoneNumber;
    public String contactName;
    public Bitmap contactPhoto;

    public Contact(String phoneNumber, String contactName, Bitmap contactPhoto) {
        this.phoneNumber = phoneNumber;
        this.contactName = contactName;
        this.contactPhoto = contactPhoto;
    }

    protected Contact(Parcel in) {
        this.phoneNumber = in.readString();
        this.contactName = in.readString();
        this.contactPhoto = (Bitmap)in.readParcelable(getClass().getClassLoader());
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.phoneNumber);
        dest.writeString(this.contactName);
        dest.writeParcelable(this.contactPhoto, flags);
    }
}
