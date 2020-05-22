package com.android.jesse.awesomerouter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.jesse.router_annotation.Router;

@Router(path = "/account/LoginActivity")
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}
