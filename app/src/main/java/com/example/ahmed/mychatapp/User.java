package com.example.ahmed.mychatapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ahmed on 8/20/17.
 */

public class User implements Parcelable {

    private String uid;
    private String name;
    private String email;
    private String photoUrl;

    public User(){};

    public User(String uid, String name, String email, String photoUrl) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    public User(String uid, String name, String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
    }


    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public String toString() {
        return "uid " + uid
                + "name " + name
                + "email " + email;
    }

    protected User(Parcel in) {
        uid = in.readString();
        name = in.readString();
        email = in.readString();
        photoUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(photoUrl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}