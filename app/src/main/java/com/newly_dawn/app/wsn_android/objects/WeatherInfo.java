package com.newly_dawn.app.wsn_android.objects;

/**
 * Created by dell on 2016/6/14.
 */
public class WeatherInfo {
    String place;
    String date;
    String temperature;
    String highTemperature;
    String lowTemperature;
    String type;
    String fengli;
    String fengxiang;

    public String getPlace() {
        return place;
    }

    public String getDate() {
        return date;
    }
    public String getTemperature() {
        return temperature;
    }
    public String getHighTemperature() {
        return highTemperature;
    }

    public String getLowTemperature() {
        return lowTemperature;
    }

    public String getType() {
        return type;
    }

    public String getFengli() {
        return fengli;
    }

    public String getFengxiang() {
        return fengxiang;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setHighTemperature(String highTemperature) {
        this.highTemperature = highTemperature;
    }

    public void setLowTemperature(String lowTemperature) {
        this.lowTemperature = lowTemperature;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFengli(String fengli) {
        this.fengli = fengli;
    }

    public void setFengxiang(String fengxiang) {
        this.fengxiang = fengxiang;
    }
}
