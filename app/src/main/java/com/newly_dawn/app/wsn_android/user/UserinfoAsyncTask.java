package com.newly_dawn.app.wsn_android.user;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.newly_dawn.app.wsn_android.tool.HttpRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dell on 2016/6/22.
 */
public class UserinfoAsyncTask extends AsyncTask<Map<String,String>, Void, Map<String, String>> {
    public String responseCode;
    public String responseText;
    public Map<String, String> result = new HashMap<>();
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
            responseCode = "404";
        }else{
            if(result.get("code").equals("200")){
                responseText = result.get("text");
            }else{
                responseText = "error";
            }
        }
        Log.i("wsn_Exception_usertest", responseCode);
    }
    public String getResponseCode(){
        return this.responseCode;
    }
    public String getResponseText(){
        return this.responseText;
    }
}
