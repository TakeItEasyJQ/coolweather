package com.coolweather.android;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.coolweather.android.db.QuickCity;
import com.coolweather.android.gson.Alarms;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import java.io.IOException;
import java.util.zip.Inflater;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherlayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private TextView dressText;
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefresh;
    public static String mWeatherId;
    public DrawerLayout drawerLayout;
    private Button navButton;
    private String bingPic;

    private  TextView direction;
    private TextView force;
    private  TextView hum;

    private LinearLayout alarmslayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        weatherlayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.spor_text);
        dressText=(TextView)findViewById(R.id.dress_text);
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        direction=(TextView)findViewById(R.id.wind_dir);
        force=(TextView)findViewById(R.id.wind_sc);
        hum=(TextView)findViewById(R.id.wind_hum);
        alarmslayout=(LinearLayout)findViewById(R.id.alarms_layout);
        navButton = (Button) findViewById(R.id.nav_button);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
                ChooseAreaFragment.queryQuickCities();
            }
        });
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if ("new".equals(getIntent().getStringExtra("change"))){
            requestWeather(mWeatherId);
        }else {
            String weatherString = prefs.getString("weather", null); //第二个为默认值即当找不到键对应值时返回此致
            if (weatherString != null) {
                Weather weather = Utility.handleWeatherResponse(weatherString);
                mWeatherId = weather.basic.weatherId;
                requestWeather(mWeatherId);

            } else {                                                  //第一次会执行此句
                SettingsActivity.USEBINGPIC=SettingsActivity.BINGPIC;
                SettingsActivity.SERVICE=SettingsActivity.AUTOSERVICE;
                SharedPreferences.Editor editor=prefs.edit();
                editor.putBoolean("mode",true);
                editor.apply();
                mWeatherId = getIntent().getStringExtra("weather_id");         //此前在这里出了个错   错误写为Sring mWeather=... 此时为新的对象并不是外面的没Weather了
                weatherlayout.setVisibility(View.INVISIBLE);          //此时mWeather已赋值为weatherId
                String weatherURL = "https://free-api.heweather.com/v5/weather?city=" + mWeatherId + "&key=bc9aa668f17648b4a2ea6f9fac4083ee";
                HttpUtil.sendOkHttpRequest(weatherURL, new Callback() {                                    //第一次启动时将所选城市的信息存入QuickCity的DB中就可以啦
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseText=response.body().string();
                        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                        editor.putString("weather",responseText);
                        editor.apply();
                        final Weather weather=Utility.handleWeatherResponse(responseText);
                         runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showWeatherInfo(weather);
                            }
                        });
                        QuickCity city=Utility.handleQuickCityResponse(weather);
                        city.save();
                    }
                });
            }
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });
        boolean a=prefs.getBoolean("mode",false);
        if (a){
            String bingPic = prefs.getString("bing_pic", null);
            if (bingPic != null) {
                Glide.with(this).load(bingPic).into(bingPicImg);
            } else {
                loadBingPic();
            }
        }else {
            int background=prefs.getInt("background",0);
            if (background!=0){
                Glide.with(this).load(background).into(bingPicImg);
            }else {
                loadBingPic();
            }
        }
        }


    public  void requestWeather(final String weatherId) {
        String weatherUrl = "https://free-api.heweather.com/v5/weather?city=" +weatherId+ "&key=bc9aa668f17648b4a2ea6f9fac4083ee";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
//                final Alarms alarms=Utility.handleAlarmsResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null &&  "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
//                            if (alarms!=null){
//                                showAlarmsInfo(alarms);
//                                alarmslayout.setVisibility(View.VISIBLE);
//                            }else{
//                                alarmslayout.setVisibility(View.GONE);
//                            }

                        }else if (weather!=null&&"no more requests".equals(weather.status)){
                            Toast.makeText(WeatherActivity.this,"对不起，暂时用于学习交流的免费接口每日请求数量已达上限，抱歉！",Toast.LENGTH_SHORT).show();
                        }else if (weather!=null&&"unknown city".equals(weather.status)){
                            Toast.makeText(WeatherActivity.this,"天气接口出现错误，可能维护中，请稍后再试",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(WeatherActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
        loadBingPic();
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败，请检查网络设置", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                        loadBingPic();
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityname;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;

        String dir=weather.now.wind.direction;
        String sc=weather.now.wind.force;
        String hum=weather.now.hum+"%";


        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();    //清空未来天气中所有的信息
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            int i=Integer.valueOf(weather.aqi.city.aqi);
            if (0<=i&&i<=50){
                aqiText.setText("优");
            }else if (51<=i&&i<=100){
                aqiText.setText("良");
            }else if (101<=i&&i<=200){
                aqiText.setText("轻度");
            }else if (201<=i&&i<=300){
                aqiText.setText("中度");
            }else if (i>300){
                aqiText.setText("重度");
            }
            pm25Text.setText(weather.aqi.city.pm25);
        }
        if (weather.now.wind!=null&&weather.now.hum!=null){
            direction.setText(dir);
            force.setText(sc);
            this.hum.setText(hum);
        }
        String comfort = "舒适度:" + weather.suggestion.comfort.info;
        String carWash = "洗车指数:" + weather.suggestion.carWash.info;
        String sport = "运动指数:" + weather.suggestion.sport.info;
        String dress ="穿衣搭配:"+weather.suggestion.clothes.wear;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        dressText.setText(dress);
        weatherlayout.setVisibility(View.VISIBLE);
    }
//    public void showAlarmsInfo(Alarms alarms){
//        alarmslayout.removeAllViews();
//        String title=alarms.title;
//        String txt=alarms.txt;
//        TextView alarmsTitle=(TextView)findViewById(R.id.alarms_title);
//        TextView alarmsTxt=(TextView)findViewById(R.id.alarms_txt);
//        alarmsTitle.setText(title);
//        alarmsTxt.setText(txt);
//    }

    private  void loadBingPic() {
        SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
        boolean UseBing=pref.getBoolean("mode",false);
        if (UseBing){//如果允许使用每日一图的话  在MainActivity中默认设置了true
            String requestBingPic = "http://guolin.tech/api/bing_pic";
            HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    bingPic = response.body().string();
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                    editor.putString("bing_pic", bingPic);
                    editor.apply();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                        }
                    });
                }
            });
        }else {
            //加载本地的图片URL
            SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
            final int bgPic=prefs.getInt("background",0);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Glide.with(WeatherActivity.this).load(bgPic).into(bingPicImg);
                }
            });
        }
    }
}
