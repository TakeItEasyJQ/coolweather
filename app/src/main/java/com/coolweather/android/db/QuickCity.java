package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by My Computer on 2017/5/17.
 */

public class QuickCity extends DataSupport {
    private int id;
    private String cityName;
    private String cityDegree;
    private String weatherid;

    public String getWeatherid() {
        return weatherid;
    }

    public int getId() {
        return id;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCityDegree() {
        return cityDegree;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setCityDegree(String cityDegree) {
        this.cityDegree = cityDegree;
    }

    public void setWeatherid(String weatherid) {
        this.weatherid = weatherid;
    }
}
