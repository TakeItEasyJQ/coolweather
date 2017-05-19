package com.coolweather.android.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.coolweather.android.R;
import com.coolweather.android.db.QuickCity;

import java.util.List;

/**
 * Created by My Computer on 2017/5/16.
 */

public class CityAdaper extends ArrayAdapter<QuickCity>{
    private int resourceId;
    public CityAdaper(Context context, int resource, List<QuickCity> objects) {
        super(context, resource, objects);
        resourceId=resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QuickCity quickCity=getItem(position);
        View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView cityName=(TextView)view.findViewById(R.id.selected_city);
        TextView cityDegree=(TextView)view.findViewById(R.id.seleced_degree);
        cityName.setText(quickCity.getCityName());
        cityDegree.setText(quickCity.getCityDegree());
        return view;
    }
}
