package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by My Computer on 2017/5/8.
 */

public class AQI {
    public AQICity city;
    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
