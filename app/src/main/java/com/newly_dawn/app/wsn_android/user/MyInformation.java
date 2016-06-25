package com.newly_dawn.app.wsn_android.user;

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
import android.widget.TextView;
import android.widget.Toast;

import com.newly_dawn.app.wsn_android.R;
import com.newly_dawn.app.wsn_android.tool.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyInformation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("我的信息");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyInformation.this.finish();
            }
        });
        structureFunction();
    }
    public void structureFunction(){
        try {
            SharedPreferences sharedPreferences;
            sharedPreferences = getSharedPreferences("wsnSharedPreferences", MODE_WORLD_READABLE);
            String token = sharedPreferences.getString("token", null);
            Log.i("user_info_detail", token);
            String targetUrl = "http://www.xiaolong.party/api/user_info/?access_token=" + token;
            Map<String, String> dataMp = new HashMap<>();
            dataMp.put("url", targetUrl);
            new UserinfoAsyncTask().execute(dataMp);
        }catch (Exception e){
            Log.i("user_info_detail", "no token");
        }
    }
    public class UserinfoAsyncTask extends AsyncTask<Map<String,String>, Void, Map<String, String>> {
        Map<String, String> result = new HashMap<>();
        @Override
        protected void onPreExecute(){        }
        @Override
        protected Map<String, String> doInBackground(Map<String,String>... params) {
            String url = params[0].get("url");
            HttpRequest httpRequest = new HttpRequest(url);
            try {
                httpRequest.get_connect();
                String responseCode = httpRequest.getResponseCode();
                String responseText = httpRequest.getResponseText();
                result.put("code", responseCode);
                result.put("text", responseText);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("wsn_Exception_user_info", String.valueOf(e));
                result = null;
            }
            return result;
        }
        protected void onPostExecute(Map<String,String> result){
            if(result == null){
                Toast.makeText(MyInformation.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
            }else{
                if(result.get("code").equals("200")){
                    try {
                        JSONObject jsonObject = new JSONObject(result.get("text"));
                        JSONObject user = jsonObject.getJSONObject("user");
                        TextView information_username = (TextView)findViewById(R.id.information_username);
                        TextView information_nickname = (TextView)findViewById(R.id.information_nickname);
                        TextView information_sex = (TextView)findViewById(R.id.information_sex);
                        TextView information_location = (TextView)findViewById(R.id.information_location);
                        TextView information_birthday = (TextView)findViewById(R.id.information_birthday);
                        information_username.setText(user.getString("nickname"));
                        information_nickname.setText(user.getString("nickname"));
                        information_sex.setText(user.getString("sex"));
                        information_location.setText(user.getString("location"));
                        information_birthday.setText(user.getString("register_date"));
//                        updatePhoto(user.getString("portrait"));

                    } catch (JSONException e) {
                        Log.i("getImg", String.valueOf(e));
                        e.printStackTrace();
                    }
                    Log.i("user_info_text", result.get("text"));
                }else{
                    Log.i("user_info_code", result.get("code"));
                }
            }
        }
    }
}
