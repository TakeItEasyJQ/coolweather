package com.coolweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.renderscript.Sampler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.coolweather.android.db.QuickCity;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.CityAdaper;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by My Computer on 2017/5/16.
 */

public class QuickCityFragment extends Fragment {
    private ListView listView;
    public static  List<QuickCity>  cityList=new ArrayList<>();
    public static CityAdaper adaper;
    private Button setting;
    private Button add;
    private QuickCity selectedquickCity;
    private QuickCity deletecity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.quick_city,container,false);
        listView=(ListView)view.findViewById(R.id.quick_listview);
        listView.setCacheColorHint(Color.TRANSPARENT);
        adaper=new CityAdaper(getContext(),R.layout.city_item,cityList);
        listView.setAdapter(adaper);
        setting=(Button)view.findViewById(R.id.settings);
        add=(Button)view.findViewById(R.id.add_city);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),ChooseCityActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),SettingsActivity.class);
                startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedquickCity=cityList.get(position);
                String weatherid=selectedquickCity.getWeatherid();
                WeatherActivity.mWeatherId=weatherid;
                String weatherUrl= "https://free-api.heweather.com/v5/weather?city=" + weatherid + "&key=bc9aa668f17648b4a2ea6f9fac4083ee";
                HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(getActivity(),"加载失败...",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseText=response.body().string();
                        Weather weather=Utility.handleWeatherResponse(responseText);
                        Utility.handleQuickCityResponse(weather);
                        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                        editor.putString("weather",responseText);
                        editor.apply();
                        Intent intent=new Intent(getActivity(),WeatherActivity.class);
                        intent.putExtra("change","new");
                        startActivity(intent);
                        getActivity().finish();
                    }
                });

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deletecity=QuickCityFragment.cityList.get(position);
                Snackbar.make(view,"确定要删除此城市？",Snackbar.LENGTH_LONG).setAction("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deletecity.delete();
                        QuickCityFragment.cityList.remove(deletecity);
                        QuickCityFragment.adaper.notifyDataSetChanged();
                        Toast.makeText(getContext(),"删除成功",Toast.LENGTH_SHORT).show();
                    }
                }).show();
                return true;
            }
        });
        return view;
    }



}
