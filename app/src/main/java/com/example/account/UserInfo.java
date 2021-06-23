package com.example.account;

import android.app.Application;

import com.example.account.pojo.entity.User;

public class UserInfo extends Application {
    User user = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
