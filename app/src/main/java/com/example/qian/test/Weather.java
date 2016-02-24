package com.example.qian.test;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by john on 2016/2/23.
 */
public class Weather {
    private String city_name;
    private String weather;
    private String temperature;

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getTemp_min() {
        return temp_min;
    }

    public void setTemp_min(String temp_min) {
        this.temp_min = temp_min;
    }

    public String getTemp_max() {
        return temp_max;
    }

    public void setTemp_max(String temp_max) {
        this.temp_max = temp_max;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    private String temp_min;
    private String temp_max;
    private String pressure;
    public static void _alert(Context context, String s) {
        Toast.makeText(context, s + "", Toast.LENGTH_SHORT).show();
    }
    public Weather(){

    }
    public Weather(String city_name,
            String weather,
            String temperature,
            String temp_min,
            String temp_max,
            String pressure){
        this.city_name=city_name;
        this.weather=weather;
        this.temperature=temperature;
        this.temp_min=temp_min;
        this.temp_max=temp_max;
        this.pressure=pressure;
    }

    @Override
    public String toString() {
        return "city:"+city_name+",weather:"+weather+",temperature:"+temperature+",min:"+temp_min+",max:"+temp_max+",pressure:"+pressure;
    }
}
