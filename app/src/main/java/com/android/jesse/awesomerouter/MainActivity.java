package com.android.jesse.awesomerouter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.jesse.router_annotation.Router;
import com.android.jesse.router_api.core.AwesomeRouter;

@Router(path = "/app/MainActivity")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void jumpLogin(View view) {
        AwesomeRouter.getInstance().build("/account/LoginActivity")
                .navigation(this);
    }
}
