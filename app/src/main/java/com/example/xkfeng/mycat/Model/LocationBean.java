package com.example.xkfeng.mycat.Model;

public class LocationBean {
    private String locationData ;
    private String weather ;


    public LocationBean(String locationData ,String weather){
        this.locationData = locationData ;
        this.weather = weather ;
    }

    public String getLocationData() {
        return locationData;
    }

    public void setLocationData(String locationData) {
        this.locationData = locationData;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }
}
