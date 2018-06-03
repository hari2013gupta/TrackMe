package com.dev.hari.trackme.modes;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by hariom.gupta on 5/17/2018.
 */
@IgnoreExtraProperties
public class User {

    public String deviceId, mobileNo, password, phleboName, userId;
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String deviceId, String mobileNo, String password, String phleboName, String userId) {
        this.deviceId = deviceId;
        this.mobileNo = mobileNo;
        this.password = password;
        this.phleboName = phleboName;
        this.userId = userId;
    }
}