package com.android.jesse.awesomerouter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.jesse.router_annotation.Router;

@Router(path = "/app/HomeActivity")
public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
}
