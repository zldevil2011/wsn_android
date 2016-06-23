package com.newly_dawn.app.wsn_android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.newly_dawn.app.wsn_android.information.Weather;
import com.newly_dawn.app.wsn_android.objects.News;
import com.newly_dawn.app.wsn_android.tool.Browser;
import com.newly_dawn.app.wsn_android.tool.HttpRequest;
import com.newly_dawn.app.wsn_android.tool.TabAdapter;
import com.newly_dawn.app.wsn_android.user.Login;
import com.newly_dawn.app.wsn_android.user.Register;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private LayoutInflater mInflater;
    private List<String> mTitleList = new ArrayList<>();//页卡标题集合
    private View index, attention, mine;//页卡视图
    private List<View> mViewList = new ArrayList<>();//页卡视图集合
    private ProgressDialog dialog;
    private ListView newsListView;

    private LineChartView line_chart_view;
    private LineChartData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("首页");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initTabFragment();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                String token = bundle.getString("token");
                String targetUrl = "http://www.xiaolong.party/api/user_info/?access_token=" + token;
                Map<String, String> dataMp = new HashMap<>();
                dataMp.put("url", targetUrl);
                new UserinfoAsyncTask().execute(dataMp);
                Toast.makeText(this, "回传成功", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Log.i("wsn_debug_main_login", String.valueOf(e));
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
                Toast.makeText(MainActivity.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
            }else{
                if(result.get("code").equals("200")){
                    try {
                        JSONObject jsonObject = new JSONObject(result.get("text"));
                        TextView nav_head_username = (TextView)findViewById(R.id.nav_head_username);
                        TextView nav_head_email = (TextView)findViewById(R.id.nav_head_email);
                        JSONObject user = jsonObject.getJSONObject("user");
                        nav_head_username.setText(user.getString("nickname"));
                        nav_head_email.setText(user.getString("email"));
                        TextView personal_username = (TextView)mine.findViewById(R.id.personal_username);
                        personal_username.setText(user.getString("nickname"));
                        updatePhoto(user.getString("portrait"));

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
    public void updatePhoto(String path){
        path = "http://www.xiaolong.party" + path;
        new GetPhotoAsyncTask().execute(path);
    }
    public class GetPhotoAsyncTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected void onPreExecute(){}
        @Override
        protected Bitmap doInBackground(String ... params) {
            URL myFileUrl = null;
            Bitmap bitmap = null;
            try {
                myFileUrl = new URL(params[0]);
            } catch (Exception e) {
                Log.i("exception_xxxxUP1", String.valueOf(e));
                e.printStackTrace();
            }
            try {
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
                is.close();
            } catch (Exception e) {
                Log.i("exception_xxxxUP2", String.valueOf(e));
                e.printStackTrace();
            }
            return bitmap;
        }
        protected void onPostExecute(Bitmap bitmap){
            ImageView portrait = (ImageView)findViewById(R.id.nav_head_photo);
            ImageView personal_photo = (ImageView)mine.findViewById(R.id.personal_photo);
            portrait.setImageBitmap(bitmap);
            personal_photo.setImageBitmap(bitmap);
            LinearLayout original = (LinearLayout)mine.findViewById(R.id.personal_info_loginRegister);
            LinearLayout currentInfo = (LinearLayout)mine.findViewById(R.id.personal_info_linearlayout);
            original.setVisibility(View.GONE);
            currentInfo.setVisibility(View.VISIBLE);
        }
    }
    public void initTabFragment(){

        mTabLayout  = (TabLayout)findViewById(R.id.tabs);
        mViewPager  = (ViewPager)findViewById(R.id.vp_FindFragment_pager);

//        mInflater = LayoutInflater.from(this);
        mInflater = getLayoutInflater();
        index = mInflater.inflate(R.layout.activity_index, null);
        attention = mInflater.inflate(R.layout.activity_attention, null);
        mine = mInflater.inflate(R.layout.activity_personal, null);

        //添加页卡视图
        mViewList.add(index);
        mViewList.add(attention);
        mViewList.add(mine);

        //添加页卡标题
        mTitleList.add("新闻");
        mTitleList.add("关注");
        mTitleList.add("我的");

        mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(0)));//添加tab选项卡
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(1)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(2)));

        MyPagerAdapter mAdapter = new MyPagerAdapter(mViewList);
        mViewPager.setAdapter(mAdapter);//给ViewPager设置适配器
        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
        mTabLayout.setTabsFromPagerAdapter(mAdapter);//给Tabs设置适配器
        index_build();
        attention_build();
        mine_build();
    }
    //ViewPager适配器
    class MyPagerAdapter extends PagerAdapter {
        private List<View> mViewList;

        public MyPagerAdapter(List<View> mViewList) {
            this.mViewList = mViewList;
        }

        @Override
        public int getCount() {
            return mViewList.size();//页卡数
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;//官方推荐写法
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));//添加页卡
//            Log.i("xyz", String.valueOf(container));
//            Log.i("xyz", String.valueOf(mViewList.get(position)));
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));//删除页卡
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);//页卡标题
        }

    }
//    首页构造
    public void index_build(){
        dialog = new ProgressDialog(MainActivity.this);
        newsListView = (ListView)index.findViewById(R.id.index_list);
        try {
            SharedPreferences sharedPreferences;
            sharedPreferences = getSharedPreferences("wsnSharedPreferences", MODE_WORLD_READABLE);
            String token = sharedPreferences.getString("token", null);
            Log.i("user_info", token);
        }catch (Exception e){
            Log.i("user_info", "no token");
        }
        String http = "http://c.m.163.com/nc/article/headline/T1348647853363/0-20.html";
        dialog.setTitle("提示信息");
        dialog.setMessage("loading......");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        new NewsAsyncTask().execute(http);
    }
    public class NewsAsyncTask extends AsyncTask<String, Void, List<News>> {
        List<Map<String,String>> listItems = new ArrayList<>();
        @Override
        protected void onPreExecute(){
            dialog.show();
        }
        @Override
        protected List<News> doInBackground(String... params) {
            List<News> newsList = new ArrayList<News>();
            String result = "";
            try {
                HttpRequest httpRequest = new HttpRequest(params[0]);
                httpRequest.get_connect();
                result = httpRequest.getResponseText();
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("MY_TEST_B", String.valueOf(e));
            }
            newsList = parseNewsInfo(result);
            for(int i = 0; i < newsList.size(); ++i){
                News tmp = newsList.get(i);
                Map<String, String> map = new HashMap<>();
                map.put("title", tmp.getTitle());
                map.put("time",tmp.getPtime());
                map.put("url",tmp.getUrl());
                listItems.add(map);
            }
            return newsList;
        }
        protected void onPostExecute(List<News> result){
            SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, listItems, R.layout.news_list_item, new String[]{"title",
                    "time", "url"}, new int[]{R.id.title, R.id.time, R.id.url});
            try{
                newsListView.setAdapter(adapter);
            }catch (Exception e){
                Log.i("user_info_bug", String.valueOf(e));
            }
            newsListView.setOnItemClickListener(new NewsItemClickListener());
            dialog.dismiss();
        }
    }
    public class NewsItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HashMap<String, String> currentItem = (HashMap<String, String>) newsListView.getItemAtPosition(position);
            String newsUrl = currentItem.get("url");
            Toast.makeText(MainActivity.this, currentItem.get("url"), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
//            Intent传递参数
            intent.putExtra("url", currentItem.get("url"));
            intent.setClass(MainActivity.this, Browser.class);
            startActivity(intent);
        }
    }
    /**
     * read from html and get the JSON data
     * @param newsString
     * @return
     */
    public static List<News> parseNewsInfo(String newsString)
    {
        List<News> newsList = new ArrayList<News>();
        try
        {
            JSONObject jsonObject = new JSONObject(newsString);
            JSONArray data = jsonObject.getJSONArray("T1348647853363");
            for(int i = 1; i < data.length(); i++){
                News tmp_news = new News();
                JSONObject tmp = data.getJSONObject(i);
                tmp_news.setTitle(tmp.getString("title"));
                try {
                    tmp_news.setUrl(tmp.getString("url_3w"));
                }catch (Exception e){
                    tmp_news.setUrl("");
                }
                tmp_news.setSource("");
                tmp_news.setLmodify("");
                tmp_news.setImgSrc("");
                tmp_news.setSubtitle("");
                tmp_news.setPtime(tmp.getString("ptime"));
                newsList.add(tmp_news);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return newsList;
    }
//    我的关注
    public void attention_build(){
        line_chart_view = (LineChartView)attention.findViewById(R.id.line_chart_view);
        try {
            SharedPreferences sharedPreferences;
            sharedPreferences = getSharedPreferences("wsnSharedPreferences", MODE_WORLD_READABLE);
            String token = sharedPreferences.getString("token", null);
            Log.i("user_info", token);
            String targetUrl = getResources().getString(R.string.ServerIP) + "/api/air_info/";
            String targetUrl2 = getResources().getString(R.string.ServerIP) + "/api/forecast_info/";
            String location = "北京";
            Map<String, String> mp = new HashMap<>();
            mp.put("url", targetUrl);
            mp.put("location", location);
            mp.put("token", token);
            Map<String, String> mp2 = new HashMap<>();
            mp2.put("url", targetUrl2);
            mp2.put("location", location);
            mp2.put("token", token);
            new airAsyncTask().execute(mp);
            new forecastAsyncTask().execute(mp2);
        }catch (Exception e){
            Log.i("user_info", "no token");
            Toast.makeText(MainActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
        }
//        List<Line> lines = initLine();
//        data = initData(lines);
//
//        line_chart_view.setLineChartData(data);
//
//        Viewport viewport = initViewPort();
//        line_chart_view.setMaximumViewport(viewport);
//        line_chart_view.setCurrentViewport(viewport);
    }
    /**
            * 设置4个边距
    */
    private Viewport initViewPort() {
        Viewport viewport = new Viewport();
        viewport.top = 40;
        viewport.bottom = 20;
        viewport.left = 0;
        viewport.right = 7;

        return viewport;
    }
    /**
     * 初始化线属性
     *
     * @return
     */
    private List<Line> initLine(Map<Integer, Float> data) {
        List<Line> lineList = new ArrayList<>();
        List<PointValue> pointValueList = new ArrayList<>();
        for(Integer key : data.keySet()){
            Float val = data.get(key);
            PointValue pointValue1 = new PointValue(key,val);
            pointValueList.add(pointValue1);
        }
//        PointValue pointValue1 = new PointValue(14,30);
//        pointValueList.add(pointValue1);
//        PointValue pointValue2 = new PointValue(15,20);
//        pointValueList.add(pointValue2);
//        PointValue pointValue3 = new PointValue(16,50);
//        pointValueList.add(pointValue3);
//        PointValue pointValue4 = new PointValue(17,44);
//        pointValueList.add(pointValue4);
//        PointValue pointValue5 = new PointValue(18,30);
//        pointValueList.add(pointValue5);
//        PointValue pointValue6 = new PointValue(19,31);
//        pointValueList.add(pointValue6);
//        PointValue pointValue7 = new PointValue(20,22);
//        pointValueList.add(pointValue7);


        Line line = new Line(pointValueList);
        line.setColor(getResources().getColor(R.color.colorAccent));
        line.setShape(ValueShape.CIRCLE);
        lineList.add(line);

        return lineList;
    }

    /**
     * 初始化数据
     *
     * @return
     */
    private LineChartData initData(List<Line> lines) {

        LineChartData data = new LineChartData(lines);
        //初始化轴
        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("时间");
        //前加字符
//        axisX.setFormatter(new SimpleAxisValueFormatter().setPrependedText("aaaa".toCharArray()));
        //后加字符
//        axisX.setFormatter(new SimpleAxisValueFormatter().setAppendedText("aaaa".toCharArray()));
//        axisX.setFormatter(new SimpleAxisValueFormatter());
        axisY.setName("气温(℃)");
        //设置轴
        data.setAxisYLeft(axisY);
        data.setAxisXBottom(axisX);
        //设置负值 设置为负无穷 默认为0
//        data.setBaseValue(Float.NEGATIVE_INFINITY);

        return data;
    }
    public class airAsyncTask extends AsyncTask<Map<String,String>, Void, Map<String, String>> {
        Map<String, String> result = new HashMap<>();
        @Override
        protected void onPreExecute(){
            dialog.show();
        }
        @Override
        protected Map<String, String> doInBackground(Map<String,String>... params) {
            String url = null;
            try {
                url = params[0].get("url") + "?location=" + URLEncoder.encode("北京", "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            HttpRequest httpRequest = new HttpRequest(url);
            Map<String, String> data = new HashMap<>();
            data.put("location", params[0].get("location"));
            try {
                httpRequest.get_connect();
                String responseCode = httpRequest.getResponseCode();
                Log.i("wsn_Exception", responseCode);
                String responseText = httpRequest.getResponseText();
                Log.i("wsn_Exception", responseText);
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
                Toast.makeText(MainActivity.this, "请检查数据连接", Toast.LENGTH_SHORT).show();
            }else{
                if(result.get("code").equals("200")){
                    Toast.makeText(MainActivity.this, "获取数据成功", Toast.LENGTH_SHORT).show();
                    String valText = result.get("text");
                    try {
                        JSONObject jsonObject = new JSONObject(valText).getJSONObject("air");
                        Log.i("wsn", String.valueOf(jsonObject));
                        TextView location = (TextView)attention.findViewById(R.id.air_location);
                        TextView air_WeaTem = (TextView)attention.findViewById(R.id.air_WeaTem);
                        TextView aqi = (TextView)attention.findViewById(R.id.aqi);
                        TextView humidity = (TextView)attention.findViewById(R.id.humidity);
                        TextView cloud_speed = (TextView)attention.findViewById(R.id.cloud_speed);
                        TextView weatherType = (TextView)attention.findViewById(R.id.weatherType);
                        location.setText(jsonObject.getString("location"));
                        air_WeaTem.setText(jsonObject.getString("weather") + " " + jsonObject.getString("temperature") + "℃");
                        aqi.setText("PM2.5:" + jsonObject.getString("aqi"));
                        humidity.setText("湿度:" + jsonObject.getString("humidity") + "%");
                        cloud_speed.setText(jsonObject.getString("cloud"));
                        weatherType.setText(jsonObject.getString("weather"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i("wsn_Exception", String.valueOf(e));
                    }

                }else{
                    Toast.makeText(MainActivity.this, "认证失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    public class forecastAsyncTask extends AsyncTask<Map<String,String>, Void, Map<String, String>> {
        Map<String, String> result = new HashMap<>();
        @Override
        protected void onPreExecute(){
            dialog.show();
        }
        @Override
        protected Map<String, String> doInBackground(Map<String,String>... params) {
            String url = null;
            try {
                url = params[0].get("url") + "?location=" + URLEncoder.encode("北京", "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            HttpRequest httpRequest = new HttpRequest(url);
            Map<String, String> data = new HashMap<>();
            data.put("location", params[0].get("location"));
            try {
                httpRequest.get_connect();
                String responseCode = httpRequest.getResponseCode();
                Log.i("wsn_Exception_forecast", responseCode);
                String responseText = httpRequest.getResponseText();
                Log.i("wsn_Exception_forecast", responseText);
                result.put("code", responseCode);
                result.put("text", responseText);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("wsn_Exception_forecast", String.valueOf(e));
                result = null;
            }
            return result;
        }
        protected void onPostExecute(Map<String,String> result){
            dialog.dismiss();
            if(result == null){
                Toast.makeText(MainActivity.this, "请检查数据连接", Toast.LENGTH_SHORT).show();
            }else{
                if(result.get("code").equals("200")){
                    Toast.makeText(MainActivity.this, "获取数据成功", Toast.LENGTH_SHORT).show();
                    String valText = result.get("text");
                    try {
                        Log.i("LENT", "xxx");
                        JSONArray jsonArray = new JSONObject(valText).getJSONArray("air");
                        Log.i("LENT", valText);
                        Log.i("LENT", "yyy");
                        int arrarLen = jsonArray.length();
                        Log.i("LENT", String.valueOf(arrarLen));
                        Map<Integer, Float> forecastData = new HashMap<>();
                        for(int i = 0; i < arrarLen; ++i){
                            JSONObject jsonTmp = jsonArray.getJSONObject(i);
                            Long tmp = jsonTmp.getLong("high_temperature");
                            forecastData.put(i, Float.valueOf(tmp));
                        }
                        Log.i("LEN", String.valueOf(forecastData));
                        List<Line> lines = initLine(forecastData);
                        data = initData(lines);

                        line_chart_view.setLineChartData(data);

                        Viewport viewport = initViewPort();
                        line_chart_view.setMaximumViewport(viewport);
                        line_chart_view.setCurrentViewport(viewport);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i("wsn_Exception--", String.valueOf(e));
                    }

                }else{
                    Toast.makeText(MainActivity.this, "认证失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    //    我的
    public void mine_build(){
        Button loginBtn = (Button)mine.findViewById(R.id.loginBtn);
        Button registerBtn = (Button)mine.findViewById(R.id.registerBtn);
        loginBtn.setOnClickListener(new loginBtnOnClickListener());
        registerBtn.setOnClickListener(new registerBtnOnClickListener());
    }
    public class loginBtnOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent login_intent = new Intent(MainActivity.this, Login.class);
            startActivity(login_intent);
        }
    }
    public class registerBtnOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent register_intent = new Intent(MainActivity.this, Register.class);
            startActivity(register_intent);
        }
    }










//    主界面布局

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_weather) {
            Intent weather_intent = new Intent(MainActivity.this, Weather.class);
            startActivity(weather_intent);
        } else if (id == R.id.nav_machine) {

        } else if (id == R.id.nav_setting) {
            Intent setting_intent = new Intent(MainActivity.this, Setting.class);
            startActivity(setting_intent);
        } else if (id == R.id.nav_share) {
            savePic();

            String strDlgTitle = "分享";
            String strSubject = "WSN_android";
            String strContent = "我正在使用WSN_Android，你也加入吧！！";
//            1.分享纯文字内容
            shareMsg(strDlgTitle, strSubject, strContent, null);

//            2.分享图片和文字内容
            strDlgTitle = "对话框标题 - 分享图片";
            String imgPath=Environment.getExternalStorageDirectory() + "/pictures/blue_sky.jpg";
            Log.i("share_path", imgPath);
            Uri imageUri = Uri.fromFile(new File(imgPath));
//            shareMsg(strDlgTitle, strSubject, strContent, imgPath);

        } else if (id == R.id.nav_login) {
            Intent login_intent = new Intent(MainActivity.this, Login.class);
//            startActivity(login_intent);
            startActivityForResult(login_intent, 0);
        } else if (id == R.id.nav_help){
            Intent browser_intent = new Intent();
            browser_intent.putExtra("url", "http://www.xiaolong.party/home/");
            browser_intent.setClass(MainActivity.this, Browser.class);
            startActivity(browser_intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
//    将图片存入本地SD卡
    public void savePic(){
        String fileName = "blue_sky";
        Drawable d = getResources().getDrawable(R.drawable.blue_sky);
        BitmapDrawable bd = (BitmapDrawable) d;
        Bitmap bitmap = bd.getBitmap();
        File file = new File("/sdcard/pictures/" + fileName + ".jpg");//创建文件对象
        try {
            file.createNewFile();                                //创建一个新文件
            FileOutputStream fileOS = new FileOutputStream(file);    //创建一个文件输出流对象
            String p = file.getAbsolutePath();
            Log.i("share_path_p", p);
            //将图片内容压缩为JPEG格式输出到输出流对象中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOS);
            fileOS.flush();                                    //将缓冲区中的数据全部写出到输出流中
            fileOS.close();                                     //关闭文件输出流对象
            Log.i("share_path", "success");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("share_path", String.valueOf(e));
        }
    }

    /**
     * @param activityTitle
     * @param msgTitle
     * @param msgText
     * @param imgPath
     */
    public void shareMsg(String activityTitle, String msgTitle, String msgText,
                         String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/jpg");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 设置弹出框标题
        if (activityTitle != null && !"".equals(activityTitle)) { // 自定义标题
            startActivity(Intent.createChooser(intent, activityTitle));
        } else { // 系统默认标题
            startActivity(intent);
        }
        startActivity(Intent.createChooser(intent, activityTitle));
    }
    @Override
    // 设置回退
    // 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认退出吗？");
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 点击“确认”后的操作
                MainActivity.this.finish();
            }
        });
        builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 点击“返回”后的操作,这里不设置没有任何操作
            }
        });
//        builder.setCancelable(false);
        builder.create().show();

        return super.onKeyDown(keyCode, event);
    }
}
