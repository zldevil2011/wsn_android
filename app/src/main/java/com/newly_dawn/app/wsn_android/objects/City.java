package com.newly_dawn.app.wsn_android.objects;

/**
 * Created by dell on 2016/6/14.
 */
public class City {
    String provice;
    String city;
    String county;
    int ID;
    public void setProvice(String provice){
        this.provice = provice;
    }
    public void setCity(String city){
        this.city = city;
    }
    public void setCounty(String county){
        this.county = county;
    }
    public void setID(int ID){
        this.ID = ID;
    }
    public String getProvice(){
        return this.provice;
    }
    public String getCity(){
        return this.city;
    }
    public String getCounty(){
        return this.county;
    }
    public int getID(){
        return this.ID;
    }
}
