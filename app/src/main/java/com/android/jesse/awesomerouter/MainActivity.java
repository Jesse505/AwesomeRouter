package com.android.jesse.awesomerouter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.jesse.awesomerouter.provider.IUserInfoProvider;
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
        AwesomeRouter.getInstance().build("/user/LoginActivity")
                .navigation(this);
    }

    public void getUserName(View view) {
        if ((IUserInfoProvider)AwesomeRouter.getInstance().build("/user/info").navigation(this) == null) {
            Toast.makeText(this,"hahha", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,
                    ((IUserInfoProvider)AwesomeRouter.getInstance().build("/user/info").navigation(this))
                            .getUserName(), Toast.LENGTH_SHORT).show();
        }
    }
}
