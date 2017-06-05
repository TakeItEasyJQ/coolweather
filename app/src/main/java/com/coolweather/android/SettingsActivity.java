package com.coolweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;
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
    private Button changebg;
    public static final boolean AUTOSERVICE=true;   //  开启自动更新
    public static final boolean DISSERVICE=false;   //  关闭自动更新
    public static final boolean BINGPIC=true;       //  使用每日一图
    public static final boolean DISBING=false;      //  取消每日一图

    public static boolean SERVICE;            //默认为false
    public static boolean USEBINGPIC;

    public static int hour;
    public Intent intent;


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
        changebg=(Button)findViewById(R.id.change_background);
        if (prefs.getBoolean("MODE",false)) {
            if (prefs.getBoolean("unshow",false)){
                toggleButton.setChecked(true);
                timelayout.setVisibility(View.GONE);
            }else {
                toggleButton.setChecked(true);
                timelayout.setVisibility(View.VISIBLE);
            }

        } else{
            toggleButton.setChecked(false);
            timelayout.setVisibility(View.GONE);
        }

        if (pref.getBoolean("mode",false)){
            toggleButton2.setChecked(true);
            SettingsActivity.USEBINGPIC=SettingsActivity.BINGPIC;
        }else {
            toggleButton2.setChecked(false);
            SettingsActivity.USEBINGPIC=SettingsActivity.DISBING;
        }
        toggleButton.setOnClickListener(this);
        toggleButton2.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button4.setOnClickListener(this);
        button8.setOnClickListener(this);
        changebg.setOnClickListener(this);
        intent=new Intent(this,AutoUpdateServier.class);
    }
    @Override
    public void onClick(View v) {                          //AutoUpdateService
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
                    SettingsActivity.USEBINGPIC=SettingsActivity.BINGPIC;
                    Toast.makeText(SettingsActivity.this,"每日一图已开启",Toast.LENGTH_SHORT).show();
                }else {
                    SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit();
                    editor.putBoolean("mode",false);
                    editor.apply();
                    SettingsActivity.USEBINGPIC=SettingsActivity.DISBING;
                    Toast.makeText(SettingsActivity.this,"每日一图已关闭",Toast.LENGTH_SHORT).show();
                }
                 break;
            case R.id.button_one:
                stopService(intent);
                SettingsActivity.hour=1;
                handleService();
                timelayout.setVisibility(View.GONE);
                SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit();
                editor.putBoolean("unshow",true);
                editor.apply();
                Toast.makeText(this,"成功设置后台刷新频率为1小时",Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_two:
                stopService(intent);
                SettingsActivity.hour=2;
                handleService();
                timelayout.setVisibility(View.GONE);
                SharedPreferences.Editor editor1= PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit();
                editor1.putBoolean("unshow",true);
                editor1.apply();
                Toast.makeText(this,"成功设置后台刷新频率为2小时",Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_four:
                stopService(intent);
                SettingsActivity.hour=4;
                handleService();
                timelayout.setVisibility(View.GONE);
                SharedPreferences.Editor editor2= PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit();
                editor2.putBoolean("unshow",true);
                editor2.apply();
                Toast.makeText(this,"成功设置后台刷新频率为4小时",Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_eight:
                stopService(intent);
                SettingsActivity.hour=8;
                handleService();
                timelayout.setVisibility(View.GONE);
                SharedPreferences.Editor editor3= PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit();
                editor3.putBoolean("unshow",true);
                editor3.apply();
                Toast.makeText(this,"成功设置后台刷新频率为8小时",Toast.LENGTH_SHORT).show();
                break;
            case R.id.change_background:
                Intent intent=new Intent(SettingsActivity.this,ChooseBackgroundActivity.class);
                startActivity(intent);
                this.finish();
            default:
                break;

        }

    }

    private  void handleService(){
        if (SettingsActivity.SERVICE) {
            intent.putExtra("time", SettingsActivity.hour);
            startService(intent);
        } else if (SettingsActivity.SERVICE == SettingsActivity.DISSERVICE) {
            stopService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("MODE",false)){
            if (prefs.getBoolean("unshow",false)){
                toggleButton.setChecked(true);
                timelayout.setVisibility(View.GONE);;
            }else {
                toggleButton.setChecked(true);
                timelayout.setVisibility(View.VISIBLE);;
            }
        }else{
            toggleButton.setChecked(false);
            timelayout.setVisibility(View.GONE);
        }
        if (SettingsActivity.USEBINGPIC){
            toggleButton2.setChecked(true);
            SettingsActivity.USEBINGPIC=SettingsActivity.BINGPIC;
        }else {
            toggleButton2.setChecked(false);
            SettingsActivity.USEBINGPIC=SettingsActivity.DISBING;
        }
    }
}
