package com.example.qian.test;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by john on 2016/2/24.
 */
public class AdapterBoxOffice extends RecyclerView.Adapter<AdapterBoxOffice.ViewHolderBoxOffice> {    //adapte fragment with the layoutfile and the data

    private ArrayList<Weather> listW=new ArrayList<>();
    private LayoutInflater layoutInflater;
    public AdapterBoxOffice(Context context){
        layoutInflater=LayoutInflater.from(context);
    }

    public void setWeatherList(ArrayList<Weather> list){
        this.listW=list;
        notifyItemRangeChanged(0,listW.size());
    }

    static class ViewHolderBoxOffice extends RecyclerView.ViewHolder{
        private TextView mCityName;
        private TextView mWeather;
        private TextView mTemperature;
        private TextView mMax;
        private TextView mMin;
        private TextView mPressure;


        public ViewHolderBoxOffice(View itemView) {

            super(itemView);
            mCityName= (TextView) itemView.findViewById(R.id.wLocation);
            mWeather= (TextView) itemView.findViewById(R.id.wWeather);
            mTemperature= (TextView) itemView.findViewById(R.id.wTemperature);
            mMax= (TextView) itemView.findViewById(R.id.wMax);
            mMin= (TextView) itemView.findViewById(R.id.wMin);
            mPressure= (TextView) itemView.findViewById(R.id.wPressure);
        }
    }

    @Override
    public ViewHolderBoxOffice onCreateViewHolder(ViewGroup parent, int viewType) {   //inflate the layout file
        View view=layoutInflater.inflate(R.layout.custom_weather_box_office,parent,false);
        ViewHolderBoxOffice viewHolder=new ViewHolderBoxOffice(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolderBoxOffice holder, int position) {
        Weather currentWeather=listW.get(position);
        holder.mCityName.setText(currentWeather.getCity_name());
        holder.mWeather.setText(currentWeather.getWeather());
        holder.mTemperature.setText(currentWeather.getTemperature());
        holder.mPressure.setText(currentWeather.getPressure());
        holder.mMax.setText(currentWeather.getTemp_max());
        holder.mMin.setText(currentWeather.getTemp_min());
    }



    @Override
    public int getItemCount() {
        return listW.size();
    }
}
