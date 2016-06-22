package com.newly_dawn.app.wsn_android.user;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.newly_dawn.app.wsn_android.MainActivity;
import com.newly_dawn.app.wsn_android.R;
import com.newly_dawn.app.wsn_android.objects.News;
import com.newly_dawn.app.wsn_android.tool.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Login extends AppCompatActivity {
    private TextView register_tip;
    private TextView forgetPassword_tip;
    private Button loginButton;
    private EditText username;
    private EditText password;
    private ProgressDialog dialog;
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
        register_tip = (TextView)findViewById(R.id.registerTextView);
        forgetPassword_tip = (TextView)findViewById(R.id.forgetPasswordTextView);
        loginButton = (Button)findViewById(R.id.loginBtn);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        register_tip.setOnClickListener(new registerOnClickListener());
        forgetPassword_tip.setOnClickListener(new forgetPasswordOnClickListener());
        loginButton.setOnClickListener(new loginBtnOnClickListener());
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
    public class loginBtnOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            dialog = new ProgressDialog(Login.this);
            dialog.setTitle("提示信息");
            dialog.setMessage("正在登录......");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            String ServerIP = getResources().getString(R.string.ServerIP);
            String targetUrl = ServerIP + "/api/login/";
            String usernameVal = username.getText().toString();
            String passwordVal = password.getText().toString();
            Map<String, String> mp = new HashMap<>();
            mp.put("url", targetUrl);
            mp.put("username", usernameVal);
            mp.put("password", passwordVal);
            new LoginAsyncTask().execute(mp);
        }
    }
    public class LoginAsyncTask extends AsyncTask<Map<String,String>, Void, Map<String, String>> {
        Map<String, String> result = new HashMap<>();
        @Override
        protected void onPreExecute(){
            dialog.show();
        }
        @Override
        protected Map<String, String> doInBackground(Map<String,String>... params) {
            String url = params[0].get("url");
            HttpRequest httpRequest = new HttpRequest(url);
            Map<String, String> data = new HashMap<>();
            data.put("username", params[0].get("username"));
            data.put("password", params[0].get("password"));
            try {
                httpRequest.post_connect(data);
                String responseCode = httpRequest.getResponseCode();
                String responseText = httpRequest.getResponseText();
                JSONObject jsonObject = new JSONObject(responseText);
                result.put("code", responseCode);
                result.put("text", responseText);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("wsn_Exception", String.valueOf(e));
                result = null;
            }
            return result;
        }
        protected void onPostExecute(Map<String,String> result){
            dialog.dismiss();
            if(result == null){
                Toast.makeText(Login.this, "请检查数据连接", Toast.LENGTH_SHORT).show();
            }else{
                if(result.get("code").equals("200")){
                    Toast.makeText(Login.this, "登陆成功", Toast.LENGTH_SHORT).show();
                    String valText = result.get("text");
                    try {
                        JSONObject jsonObject = new JSONObject(valText);
                        String token = jsonObject.getString("access_token");
                        SharedPreferences sharedPreferences;
                        SharedPreferences.Editor editor;
                        sharedPreferences = getSharedPreferences("wsnSharedPreferences", MODE_WORLD_READABLE);
                        editor = sharedPreferences.edit();
                        editor.putString("token", token);
                        editor.apply();
                        Intent intent = new Intent();
                        intent.putExtra("result", "ok");// 把返回数据存入Intent
                        intent.putExtra("token", token);;//添加要返回给页面1的数据
                        intent.putExtra("username", "devil");//添加要返回给页面1的数据
                        intent.putExtra("email", "xx@xx.com");//添加要返回给页面1的数据
                        Login.this.setResult(Activity.RESULT_OK, intent);//返回页面1
                        Login.this.finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("wsn_Exception_ll", String.valueOf(e));
                    }

                }else{
                    Toast.makeText(Login.this, "用户名或密码不正确", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
