package com.example.xkfeng.mycat.Model;

public class NearLocationModel {

    private double latitude ;
    private double longitude ;
    private int scale ;
    private String address ;
    private String name ;


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getScale() {
        return scale;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }
}
