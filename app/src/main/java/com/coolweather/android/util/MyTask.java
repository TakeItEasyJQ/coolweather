package com.coolweather.android.util;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.coolweather.android.ChooseAreaFragment;
import com.coolweather.android.WeatherActivity;
import com.coolweather.android.db.QuickCity;
import com.coolweather.android.gson.Weather;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by My Computer on 2017/5/17.
 */

public class MyTask extends AsyncTask {
    private String weatherUrl;
    public MyTask(String weatherId) {
        super();
        weatherUrl = "https://free-api.heweather.com/v5/weather?city=" + weatherId + "&key=bc9aa668f17648b4a2ea6f9fac4083ee";
    }

    @Override
    protected Object doInBackground(Object[] params) {
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                Weather weather=Utility.handleWeatherResponse(responseText);
                String weatherid=weather.basic.weatherId;
                QuickCity quickCity=Utility.handleQuickCityResponse(weather);
                quickCity.save();
                ChooseAreaFragment.queryQuickCities();
                WeatherActivity.mWeatherId=weatherid;

            }
        });

//                        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
//                            @Override
//                            public void onFailure(Call call, IOException e) {
//                                Toast.makeText(getActivity(), "获取天气信息失败", Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void onResponse(Call call, Response response) throws IOException {
//                                final String responseText=response.body().string();
//                                final Weather weather= Utility.handleWeatherResponse(responseText);
//                                final String weatherid=weather.basic.weatherId;
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        QuickCity quickCity=Utility.handleQuickCityResponse(weather);
//                                        quickCity.save();
//                                        queryQuickCities();
//                                        WeatherActivity.mWeatherId=weatherid;
//                                        Intent intent=new Intent(getContext(),WeatherActivity.class);
//                                        intent.putExtra("change","new");
//                                        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
//                                        editor.putString("weather",responseText);
//                                        editor.apply();
//                                        startActivity(intent);
//                                        getActivity().finish();
//                                    }
//                                });
//                            }
//                        });
        return null;
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

}
