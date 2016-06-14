package com.newly_dawn.app.wsn_android.information;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.newly_dawn.app.wsn_android.R;
import com.newly_dawn.app.wsn_android.objects.City;
import com.newly_dawn.app.wsn_android.objects.WeatherInfo;
import com.newly_dawn.app.wsn_android.tool.HttpRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Weather extends AppCompatActivity {
    private Spinner spinnerProvince = null;
    private Spinner spinnerSubCitys = null;
    private Spinner spinnerSubCountys = null;
    private ArrayList<City> cityCode = null;
    private Set<String> provinces = new HashSet<>();
    private Map<String, HashSet> citys = new HashMap<>();
    private Map<String, HashSet> countys = new HashMap<>();
    private Map<String, String> CODE = new HashMap<>();
    ArrayAdapter<String> provinceAdapter = null;
    ArrayAdapter<String> cityAdapter = null;
    ArrayAdapter<String> countyAdapter = null;
    final String[] arr_T_P = new String[]{};
    String[] provinceArr = null;
    final String[] arr_T_C = new String[]{};
    String[] cityArr = null;
    final String[] arr_T_CY = new String[]{};
    String[] countyArr = null;

    private String http_str = "";
    private Handler handler;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("未来天气");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Weather.this.finish();
            }
        });


        dialog = new ProgressDialog(Weather.this);
        spinnerProvince=(Spinner)findViewById(R.id.Province);
        spinnerSubCitys=(Spinner)findViewById(R.id.City);
        spinnerSubCountys=(Spinner)findViewById(R.id.County);

        readCityCode();
        initUI();

    }
    public void initUI(){
        provinceArr = (String[]) provinces.toArray(arr_T_P);
        provinceAdapter = new ArrayAdapter<String>(Weather.this, android.R.layout.browser_link_context_header, provinceArr);
        provinceAdapter.setDropDownViewResource(android.R.layout.preference_category);
        spinnerProvince.setAdapter(provinceAdapter);

        spinnerProvince.setOnItemSelectedListener(new myProvinceItemSelectedListener());
        spinnerSubCitys.setOnItemSelectedListener(new myCityItemSelectedListener());
        spinnerSubCountys.setOnItemSelectedListener(new myCountyItemSelectedListener());
    }
//    一级导航选项监听
    private class myProvinceItemSelectedListener implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            cityArr = (String[]) citys.get(provinceArr[position]).toArray(arr_T_C);
            cityAdapter = new ArrayAdapter<String>(Weather.this, android.R.layout.browser_link_context_header, cityArr);
            cityAdapter.setDropDownViewResource(android.R.layout.preference_category);
            spinnerSubCitys.setAdapter(cityAdapter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
//    二级导航选项监听
    private class myCityItemSelectedListener implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            countyArr = (String[]) countys.get(cityArr[position]).toArray(arr_T_CY);
            countyAdapter = new ArrayAdapter<String>(Weather.this, android.R.layout.browser_link_context_header, countyArr);
            countyAdapter.setDropDownViewResource(android.R.layout.preference_category);
            spinnerSubCountys.setAdapter(countyAdapter);
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
//    三级导航选项监听
    private class myCountyItemSelectedListener implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String cityCode = CODE.get(countyArr[position]);
            Log.i("CODE_TEST", cityCode);

            if (!isNetworkAvailable()) {
                alerNetErr();
                return;
            }
            final String http = "http://wthrcdn.etouch.cn/weather_mini?citykey=" + cityCode;
            dialog.setTitle("提示信息");
            dialog.setMessage("loading......");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            new MyAsyncTask().execute(http);
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
//    网络请求，异步加载
    public class MyAsyncTask extends AsyncTask<String, Void, List<WeatherInfo>> {
        List<Map<String,String>> listItems = new ArrayList<>();
        String[] dateArr = new String[]{};
        String[] lowArr = new String[]{};
        String[] highArr = new String[]{};
        String[] typeArr = new String[]{};
        @Override
        protected void onPreExecute(){
            dialog.show();
        }
        @Override
        protected List<WeatherInfo> doInBackground(String... params) {
            List<WeatherInfo> weatherInfos = new ArrayList<WeatherInfo>();
            String result = "";
            try {
                HttpRequest httpRequest = new HttpRequest(params[0]);
                httpRequest.get_connect();
                result = httpRequest.getResponseText();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i("MY_TEST", "here");
            weatherInfos = parseWeatherInfo(result);
            Log.i("MY_TEST_CHECK", weatherInfos + "");
            Log.i("MY_TEST", "ABC");
            for(int i = 0; i < weatherInfos.size(); ++i){
                WeatherInfo tmp = weatherInfos.get(i);
                Map<String, String> map = new ArrayMap<String, String>();
                map.put("date", tmp.getDate().substring(tmp.getDate().length() - 3));
                map.put("lowTemperature",tmp.getLowTemperature());
                map.put("highTemperature", tmp.getHighTemperature());
                map.put("weatherType", tmp.getType());
                listItems.add(map);
            }
            return weatherInfos;
        }
        protected void onPostExecute(List<WeatherInfo> result){
            TextView temperature = (TextView)findViewById(R.id.temperature);
            TextView todayLow = (TextView)findViewById(R.id.todayLow);
            TextView todayHigh = (TextView)findViewById(R.id.todayHigh);
            TextView todayType = (TextView)findViewById(R.id.todayType);
            Log.i("MY_TEST", "before write");
            for(int i = 0; i < result.size(); ++i){
                Log.i("MY_TEST", result.get(i).getHighTemperature() + " " + result.get(i).getLowTemperature());
            }
            WeatherInfo today = result.get(0);
            temperature.setText(today.getTemperature() + "℃");
            todayLow.setText(today.getLowTemperature());
            todayHigh.setText(today.getHighTemperature());
            todayType.setText(today.getType());
            ListView forecastListView = (ListView)findViewById(R.id.forecastList);
            SimpleAdapter adapter = new SimpleAdapter(Weather.this, listItems, R.layout.weather_forecast_list_item,
                    new String[]{"date", "lowTemperature", "highTemperature", "weatherType" }, new int[]{
                    R.id.date, R.id.lowTemperature, R.id.highTemperature, R.id.weatherType
            });
            forecastListView.setAdapter(adapter);
            Log.i("MY_TEST", "UPDATE UI");
            dialog.dismiss();
        }
    }
//    将读取到的信息转换成weatherInfo对象集合
    public static List<WeatherInfo> parseWeatherInfo(String citiesString)
    {
        List<WeatherInfo> weatherInfos = new ArrayList<WeatherInfo>();
        try
        {
            JSONObject jsonObject = new JSONObject(citiesString);
            Log.i("MY_TEST_JSON_OBJECT", "" + jsonObject);
            JSONObject data = jsonObject.getJSONObject("data");
            String temperature = data.getString("wendu");
            JSONArray forecast = data.getJSONArray("forecast");
            for(int i = 0; i < forecast.length(); i++){
                Log.i("MY_TEST_PARSE","1");
                WeatherInfo tmp_wea = new WeatherInfo();
                Log.i("MY_TEST_PARSE","2");
                JSONObject tmp = forecast.getJSONObject(i);
                Log.i("MY_TEST_PARSE", "2+1");
                tmp_wea.setPlace(data.getString("city"));
                Log.i("MY_TEST_PARSE", "3");
                tmp_wea.setDate(tmp.getString("date"));
                Log.i("MY_TEST_PARSE", "4");
                tmp_wea.setType(tmp.getString("type"));
                tmp_wea.setTemperature(data.getString("wendu"));
                Log.i("MY_TEST_PARSE", "5");
                tmp_wea.setLowTemperature(tmp.getString("low"));
                Log.i("MY_TEST_PARSE", "6");
                tmp_wea.setHighTemperature(tmp.getString("high"));
                Log.i("MY_TEST_PARSE", "7");
                tmp_wea.setFengli(tmp.getString("fengli"));
                Log.i("MY_TEST_PARSE", "8");
                tmp_wea.setFengxiang(tmp.getString("fengxiang"));

                Log.i("MY_TEST_PARSE",""+tmp_wea);
                weatherInfos.add(tmp_wea);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return weatherInfos;
    }
//    读取xml文件生成对应的城市代码
    public void readCityCode(){
        cityCode = ParseXml(getXMLFromResXml());
        getList();
    }

//    读取xml文件
    public XmlResourceParser getXMLFromResXml(){
        XmlResourceParser xmlParser = null;
        try {
            xmlParser = getResources().getXml(R.xml.citycodes);
            return xmlParser;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xmlParser;
    }
//    解析读取到的XML文件
    public ArrayList<City> ParseXml(XmlPullParser parser){
        ArrayList<City> CityArray = new ArrayList<City>();
        City CityTemp = null;
        //开始解析事件
        int eventType = 0;
        try {
            eventType = parser.getEventType();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        int full = 0;
        //处理事件，不碰到文档结束就一直处理
        while (eventType != XmlPullParser.END_DOCUMENT) {
            //因为定义了一堆静态常量，所以这里可以用switch
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    //给当前标签起个名字
                    String tagName = parser.getName();
                    if(tagName.equals("province")){
                        CityTemp = new City();
                        String Provice = (parser.getAttributeValue(1));
                        CityTemp.setProvice(Provice);
                        full = 1;
                    }else if(tagName.equals("city")){
                        String city = parser.getAttributeValue(1);
                        String provice = CityTemp.getProvice();
                        CityTemp = new City();
                        CityTemp.setProvice(provice);
                        CityTemp.setCity(city);
                        full = 2;
                    }else if(tagName.equals("county")){
                        String provice = CityTemp.getProvice();
                        String city = CityTemp.getCity();
                        CityTemp = new City();
                        CityTemp.setProvice(provice);
                        CityTemp.setCity(city);
                        CityTemp.setCounty(parser.getAttributeValue(1));
                        CityTemp.setID(Integer.parseInt(parser.getAttributeValue(2)));
                        full = 3;
                    }
                    if(full == 3){
                        CityArray.add(CityTemp);
                        full = 0;
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                case XmlPullParser.END_DOCUMENT:
                    break;
            }
            //别忘了用next方法处理下一个事件，忘了的结果就成死循环#_#
            try {
                eventType = parser.next();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return CityArray;
    }
//    将读取的结果存入list
    public void getList(){
        int len = cityCode.size();
        for(int i = 0; i < len; ++i){
            provinces.add(cityCode.get(i).getProvice());
        }
        int len_provice = provinces.size();
        int step = 0;
        for(String provice : provinces){
            HashSet<String> tmp = new HashSet<String>();
            for(step = 0; step < len; ++step){
                if(cityCode.get(step).getProvice().equals(provice)){
                    tmp.add(cityCode.get(step).getCity());
                }
            }
            citys.put(provice, tmp);
        }
        step = 0;
        for(HashSet<String> value : citys.values()){
            for(String city : value){
                HashSet<String> tmp = new HashSet<>();
                for(step = 0; step < len; ++step){
                    if(cityCode.get(step).getCity().equals(city)){
                        tmp.add(cityCode.get(step).getCounty());
                    }
                }
                Log.i("_len_", city + " " + tmp);
                countys.put(city, tmp);
            }
        }
        for(HashSet<String> value : countys.values()){
            for(String county : value){
                int code = 0;
                for(step = 0; step < len; ++step){
                    if(cityCode.get(step).getCounty().equals(county)){
                        code = cityCode.get(step).getID();
                        break;
                    }
                }
                CODE.put(county, String.valueOf(code));
            }
        }
    }

    public boolean isNetworkAvailable()
    {
        Context context = getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0)
            {
                for (int i = 0; i < networkInfo.length; i++)
                {
                    System.out.println(i + "===状态===" + networkInfo[i].getState());
                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public void alerNetErr() {

        // 对话框
        AlertDialog.Builder ab = new AlertDialog.Builder(Weather.this);
        ab.setTitle("网络错误");
        ab.setMessage("请检查手机数据连接");
        // 设置操作对象
        ab.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 取消对话框
                        dialog.cancel();
                        // 打开网络设置Activity
                        Intent it = new Intent(
                                android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(it);
                    }
                });
        ab.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 取消对话框
                        dialog.cancel();
                        // 退出程序
                        // exitApp(context);
                    }
                });
        // 显示
        ab.create().show();
    }

}
