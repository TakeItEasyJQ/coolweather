package com.coolweather.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.db.City;
import com.coolweather.android.db.Country;
import com.coolweather.android.db.Province;
import com.coolweather.android.db.QuickCity;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by My Computer on 2017/5/4.
 */

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTRY = 2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> datalist = new ArrayList<>();

    private List<Province>  provinceList;  //省列表
    private List<City> cityList;
    private List<Country> countryList;

    private static List<QuickCity>  quickCityList;

    private Province selectedProvince;   //选中的省
    private City selectedCity;

    private static int currentLevel;          //当前选中的级别

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, datalist);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCountries();
                }else if (currentLevel==LEVEL_COUNTRY){
                    final String weatherId=countryList.get(position).getWeatherId();
                    if (getActivity() instanceof MainActivity){
                        //getActivity()得到的时碎片的实例，关键字用来判断此时碎片属于哪一个活动
                        //如果属于MainActivity则打开WeatherActivity，如果属于WeatherActivity则关掉滑动菜单、现实下拉刷全新进度并请求天气数据
                        //requestWeather()中会在请求完天气数据之后关闭下拉刷新进度条
                        Intent intent=new Intent(getActivity(),WeatherActivity.class);
                        intent.putExtra("weather_id",weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if (getActivity() instanceof WeatherActivity){
                        WeatherActivity activity=(WeatherActivity)getActivity();
                        WeatherActivity.mWeatherId=weatherId;             //当你在WeatherActivity的碎片中切换城市后需要吧此时WeatherActivity中的mWeather也更新为切换后的城市的
                                                             //weatherId，否则在WeatherActivity中的mWeather没有改变，依旧是第一次选泽的城市的weatherId
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }
                    else if (getActivity() instanceof ChooseCityActivity){
                        String weatherUrl = "https://free-api.heweather.com/v5/weather?city=" + weatherId + "&key=bc9aa668f17648b4a2ea6f9fac4083ee";
                        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Toast.makeText(getActivity(), "获取天气信息失败", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {         //我觉得只不过就是为了得到QuickCity对象号存入DB中而已
                                final String responseText=response.body().string();                           //况且RequestWeather()已将天气信息写入SP中了
                                final Weather weather= Utility.handleWeatherResponse(responseText);
                                final String weatherid=weather.basic.weatherId;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        QuickCity quickCity=Utility.handleQuickCityResponse(weather);
                                        quickCity.save();
                                        queryQuickCities();                                               //更新QuickcityFragment中的列表
                                        WeatherActivity.mWeatherId=weatherid;
                                        Intent intent=new Intent(getContext(),WeatherActivity.class);
                                        intent.putExtra("change","new");
                                        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                                        editor.putString("weather",responseText);
                                        editor.apply();
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTRY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    private void queryProvinces() {                                          //查询省级数据，优先从本地，没有再去服务器
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList=DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            datalist.clear();
            for (Province province : provinceList) {
                datalist.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
        String address = "http://guolin.tech/api/china/";
        queryFromServer(address, "province");
        }
    }

    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class); //DataSupport.where不区分大小写
        if (cityList.size() > 0) {
            datalist.clear();
            for (City city : cityList) {
                datalist.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);    //默认的选中位置
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    private void queryCountries() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countryList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(Country.class);
        if (countryList.size() > 0) {
            datalist.clear();
            for (Country country : countryList) {
                datalist.add(country.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTRY;
        }else{
            int provinceCode=selectedProvince.getProvinceCode();
            int cityCode=selectedCity.getCityCode();
            String address="http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"country");
        }
    }

    public static void queryQuickCities(){
         quickCityList=DataSupport.findAll(QuickCity.class);
        if (quickCityList.size()>0){
            QuickCityFragment.cityList.clear();
            for (QuickCity quickCity:quickCityList){
                QuickCityFragment.cityList.add(quickCity);
            }
            QuickCityFragment.adaper.notifyDataSetChanged();                                         //我的理解是notifyDataSetChanged()自动更新UI
            currentLevel=LEVEL_COUNTRY;
        }

    }

    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                } else if ("country".equals(type)) {
                    result = Utility.handleCountryResponse(responseText, selectedCity.getId());
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("country".equals(type)) {
                                queryCountries();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败，请检查网络设置", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("正在从服务器加载数据...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
