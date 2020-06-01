package com.android.jesse.awesomerouter;

import android.content.Context;

import com.android.jesse.awesomerouter.provider.IUserInfoProvider;
import com.android.jesse.router_annotation.Router;

@Router(path = "/user/info")
public class UserInfoProvider implements IUserInfoProvider {
    @Override
    public void init(Context context) {

    }


    @Override
    public String getUserName() {
        return "Jesse";
    }
}
