package com.coolweather.android;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ChooseBackgroundActivity extends AppCompatActivity implements View.OnClickListener{
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.background_layout);
        button1=(Button)findViewById(R.id.bg_1);
        button2=(Button)findViewById(R.id.bg_2);
        button3=(Button)findViewById(R.id.bg_3);
        button4=(Button)findViewById(R.id.bg_4);
        button5=(Button)findViewById(R.id.bg_5);
        button6=(Button)findViewById(R.id.bg_6);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);

    }
    public void onClick(View v) {
        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(this).edit();
        int background;
        switch (v.getId()){
            case R.id.bg_1:
                SettingsActivity.USEBINGPIC=SettingsActivity.DISBING;
                editor= PreferenceManager.getDefaultSharedPreferences(this).edit();
                background=R.drawable.img_bg1;
                editor.putBoolean("mode",false);
                editor.putInt("background",background);
                editor.apply();
                Toast.makeText(getApplicationContext(),"请刷新天气完成修改",Toast.LENGTH_SHORT).show();
                this.finish();
                break;
            case R.id.bg_2:
                SettingsActivity.USEBINGPIC=SettingsActivity.DISBING;
                editor= PreferenceManager.getDefaultSharedPreferences(this).edit();
                background=R.drawable.img_bg2;
                editor.putBoolean("mode",false);
                editor.putInt("background",background);
                editor.apply();
                Toast.makeText(getApplicationContext(),"请刷新天气完成修改",Toast.LENGTH_SHORT).show();
                this.finish();
                break;
            case R.id.bg_3:
                SettingsActivity.USEBINGPIC=SettingsActivity.DISBING;
                editor= PreferenceManager.getDefaultSharedPreferences(this).edit();
                background=R.drawable.img_bg3;
                editor.putBoolean("mode",false);
                editor.putInt("background",background);
                editor.apply();
                Toast.makeText(getApplicationContext(),"请刷新天气完成修改",Toast.LENGTH_SHORT).show();
                this.finish();
                break;
            case R.id.bg_4:
                SettingsActivity.USEBINGPIC=SettingsActivity.DISBING;
                editor= PreferenceManager.getDefaultSharedPreferences(this).edit();
                background=R.drawable.img_bg4;
                editor.putBoolean("mode",false);
                editor.putInt("background",background);
                editor.apply();
                Toast.makeText(getApplicationContext(),"请刷新天气完成修改",Toast.LENGTH_SHORT).show();
                this.finish();
                break;
            case R.id.bg_5:
                SettingsActivity.USEBINGPIC=SettingsActivity.DISBING;
                editor= PreferenceManager.getDefaultSharedPreferences(this).edit();
                background=R.drawable.img_bg5;
                editor.putBoolean("mode",false);
                editor.putInt("background",background);
                editor.apply();
                Toast.makeText(getApplicationContext(),"请刷新天气完成修改",Toast.LENGTH_SHORT).show();
                this.finish();
                break;
            case R.id.bg_6:
                SettingsActivity.USEBINGPIC=SettingsActivity.DISBING;
                editor= PreferenceManager.getDefaultSharedPreferences(this).edit();
                background=R.drawable.img_bg6;
                editor.putBoolean("mode",false);
                editor.putInt("background",background);
                editor.apply();
                Toast.makeText(getApplicationContext(),"请刷新天气完成修改",Toast.LENGTH_SHORT).show();
                this.finish();
                break;
            default:
                break;
        }
    }
}
