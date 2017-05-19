package com.coolweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.coolweather.android.gson.Weather;
import com.coolweather.android.service.AutoUpdateServier;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "SettingsActivity";
    private ToggleButton toggleButton;
    private ToggleButton toggleButton2;
    private LinearLayout timelayout;
    private Button button1;
    private Button button2;
    private Button button4;
    private Button button8;
    public static final boolean AUTOSERVICE=true;   //  开启自动更新
    public static final boolean DISSERVICE=false;   //  关闭自动更新
    public static final boolean BINGPIC=true;       //  使用每日一图
    public static final boolean DISBING=false;      //  取消每日一图

    public static boolean SERVICE;            //默认为false
    public static boolean USEBINGPIC;

    public static int hour;
    public AutoUpdateServier servier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        timelayout=(LinearLayout)findViewById(R.id.time_layout);
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
        toggleButton=(ToggleButton)findViewById(R.id.toggle_button);
        toggleButton2=(ToggleButton)findViewById(R.id.toggle_button2);
        button1=(Button)findViewById(R.id.button_one);
        button2=(Button)findViewById(R.id.button_two);
        button4=(Button)findViewById(R.id.button_four);
        button8=(Button)findViewById(R.id.button_eight);

        if (prefs.getBoolean("MODE",false)){
            toggleButton.setChecked(true);
            timelayout.setVisibility(View.VISIBLE);
        }else{
            toggleButton.setChecked(false);
            timelayout.setVisibility(View.GONE);
        }
        if (pref.getBoolean("mode",false)){
            toggleButton2.setChecked(true);
        }else {
            toggleButton2.setChecked(false);
        }
        toggleButton.setOnClickListener(this);
        toggleButton2.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button4.setOnClickListener(this);
        button8.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toggle_button:
                if (toggleButton.isChecked()){
                    SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit();
                    editor.putBoolean("MODE",true);
                    editor.apply();
                    SettingsActivity.SERVICE=SettingsActivity.AUTOSERVICE;
                    SettingsActivity.hour=8;
                    Toast.makeText(this,"后台刷新已开启，默认每8小时",Toast.LENGTH_SHORT);
                    handleService();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timelayout.setVisibility(View.VISIBLE);
                        }
                    });
                }else {
                    SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit();
                    editor.putBoolean("MODE",false);
                    editor.apply();
                    SettingsActivity.SERVICE=SettingsActivity.DISSERVICE;
                    handleService();
                    Toast.makeText(this,"后台刷新已关闭",Toast.LENGTH_SHORT);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timelayout.setVisibility(View.GONE);
                        }
                    });
                }
                break;
            case  R.id.toggle_button2:
                if (toggleButton2.isChecked()){
                    SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit();
                    editor.putBoolean("mode",true);
                    editor.apply();
                }else {
                    SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit();
                    editor.putBoolean("mode",false);
                    editor.apply();
                }
                 break;
            case R.id.button_one:
                SettingsActivity.hour=1;
                handleService();
                Toast.makeText(this,"成功设置后台刷新频率为1小时",Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_two:
                SettingsActivity.hour=2;
                handleService();
                Toast.makeText(this,"成功设置后台刷新频率为2小时",Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_four:
                SettingsActivity.hour=4;
                handleService();
                Toast.makeText(this,"成功设置后台刷新频率为4小时",Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_eight:
                SettingsActivity.hour=8;
                handleService();
                Toast.makeText(this,"成功设置后台刷新频率为8小时",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

    }
    private void handleService(){
        Intent intent = new Intent(this, AutoUpdateServier.class);
        if (SettingsActivity.SERVICE) {
            intent.putExtra("time", hour);
            if (servier != null) {
                stopService(intent);
            }
            startService(intent);
        } else if (SettingsActivity.SERVICE == false) {
            stopService(intent);
        }
    }
}
