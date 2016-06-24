package com.newly_dawn.app.wsn_android.user;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.newly_dawn.app.wsn_android.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyMessage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("我的消息");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyMessage.this.finish();
            }
        });
        build();
    }
    public void build(){
        List<Map<String,String>> listItems = new ArrayList<>();
        for(int i = 0; i < 10; ++i){
            Map<String, String> map = new HashMap<>();
            map.put("messageTitle", "高温预警");
            map.put("messageTime","6月24日");
            map.put("messageContent", "京城热力再度升级，继21日首发蓝色预警之后，今天16时10分市气象台再发布高温蓝色预警信号，预计25-26日，本市大部地区日最高气温将达35-37℃。据预计，26日或将成为京城今夏首个37℃高温日。");
            listItems.add(map);
        }

        try{
            ListView messageList = (ListView)findViewById(R.id.messageList);
            SimpleAdapter adapter = new SimpleAdapter(MyMessage.this, listItems, R.layout.message_list_item, new String[]{"messageTitle",
                    "messageTime", "messageContent"}, new int[]{R.id.messageTitle, R.id.messageTime, R.id.messageContent});
            messageList.setAdapter(adapter);
        }catch (Exception e){
            Log.i("wsn_exception_mes", String.valueOf(e));
        }

    }
}
