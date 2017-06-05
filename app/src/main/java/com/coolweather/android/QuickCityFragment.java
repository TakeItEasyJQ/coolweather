package com.coolweather.android;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.coolweather.android.util.CityAdaper;
import java.util.ArrayList;
import java.util.List;

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
    private static final String TAG = "QuickCityFragment";
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
                WeatherActivity activity=(WeatherActivity)getActivity();
                activity.drawerLayout.closeDrawers();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {        //原来下面的添加城市时请求两次天气
                selectedquickCity=cityList.get(position);
                String weatherid=selectedquickCity.getWeatherid();
                WeatherActivity.mWeatherId=weatherid;
                Intent intent=new Intent(getActivity(),WeatherActivity.class);
                        intent.putExtra("change","new");
                        startActivity(intent);
                        getActivity().finish();
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
