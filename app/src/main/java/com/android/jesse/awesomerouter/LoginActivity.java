package com.android.jesse.awesomerouter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.android.jesse.router_annotation.Router;
import com.android.jesse.router_api.core.AwesomeRouter;

@Router(path = "/user/LoginActivity")
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void jumpMain(View view) {
        AwesomeRouter.getInstance().build("/app/MainActivity")
                .navigation(this);
    }
}
