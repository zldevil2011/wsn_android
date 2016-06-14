package com.newly_dawn.app.wsn_android.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.newly_dawn.app.wsn_android.R;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("登录");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login.this.finish();
            }
        });
        initlistener();
    }
    public void initlistener(){
        TextView register_tip = (TextView)findViewById(R.id.registerTextView);
        TextView forgetPassword_tip = (TextView)findViewById(R.id.forgetPasswordTextView);
        register_tip.setOnClickListener(new registerOnClickListener());
        forgetPassword_tip.setOnClickListener(new forgetPasswordOnClickListener());
    }
    public class registerOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent register_intent = new Intent(Login.this, Register.class);
            startActivity(register_intent);
        }
    }
    public class forgetPasswordOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Toast.makeText(Login.this, "Try to remember", Toast.LENGTH_SHORT).show();
        }
    }
}
