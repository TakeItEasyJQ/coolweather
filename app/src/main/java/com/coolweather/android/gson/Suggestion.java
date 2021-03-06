package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by My Computer on 2017/5/8.
 */

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;
    @SerializedName("cw")
    public CarWash carWash;
    public Sport sport;
    @SerializedName("drsg")
    public Clothes clothes;
    public class Comfort{
        @SerializedName("txt")
        public String info;
    }
    public class CarWash{
        @SerializedName("txt")
        public String info;
    }
    public class Sport{
        @SerializedName("txt")
        public String info;
    }
    public class Clothes{
        @SerializedName("txt")
        public String wear;
    }
}
