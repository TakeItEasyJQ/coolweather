package com.coolweather.android.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import com.coolweather.android.R;
import com.coolweather.android.SettingsActivity;
import com.coolweather.android.WeatherActivity;
import com.coolweather.android.gson.Alarms;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateServier extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        int hour=intent.getIntExtra("time",8);
        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour=hour*60*60*1000;
        long triggerAtTime= SystemClock.elapsedRealtime()+anHour;
        Intent i=new Intent(this,AutoUpdateServier.class);
        i.putExtra("time",8);
        PendingIntent pi=PendingIntent.getActivity(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return START_REDELIVER_INTENT;
    }
    private void updateWeather(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=prefs.getString("weather",null);
        if (weatherString!=null){
            Weather weather= Utility.handleWeatherResponse(weatherString);
            String weatherId=weather.basic.weatherId;
            String weatherUrl="https://free-api.heweather.com/v5/weather?city="+weatherId+"&key=bc9aa668f17648b4a2ea6f9fac4083ee";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText=response.body().string();
                    Weather weather=Utility.handleWeatherResponse(responseText);
//                    Alarms alarms=Utility.handleAlarmsResponse(responseText);
                    if (weather!=null&&"ok".equals(weather.status)){
                        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateServier.this).edit();
                        editor.putString("weather",responseText);
                        editor.apply();
//                        if (alarms!=null){
//                            String title=alarms.title;
//                            Intent intent=new Intent(AutoUpdateServier.this, WeatherActivity.class);
//                            PendingIntent pi=PendingIntent.getActivity(AutoUpdateServier.this,0,intent,0);
//                            NotificationManager manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//                            Notification notification=new NotificationCompat.Builder(AutoUpdateServier.this)
//                                                            .setContentTitle(title).setContentIntent(pi)
//                                                            .setSmallIcon(R.mipmap.img_launcher)
//                                                            .build();
//                            manager.notify(1,notification);
//                        }
                    }
                }
            });
        }
    }
    private void updateBingPic(){
        if (SettingsActivity.BINGPIC==SettingsActivity.USEBINGPIC){
            String requestBingPic="http://guolin.tech/api/bing_pic";
            HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText=response.body().string();
                    SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateServier.this).edit();
                    editor.putString("bing_pic",responseText);
                    editor.apply();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean("unshow",false);
    }
}
