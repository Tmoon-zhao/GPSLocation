package com.example.gpslocation;

public class Local_info_new {
    private double lat_new;
    private double lon_new;
    private String correct;

    public Local_info_new(double lat_new, double lon_new, String correct) {
        this.lat_new = lat_new;
        this.lon_new = lon_new;
        this.correct = correct;
    }

    public double getLat_new() {
        return lat_new;
    }

    public void setLat_new(double lat_new) {
        this.lat_new = lat_new;
    }

    public double getLon_new() {
        return lon_new;
    }

    public void setLon_new(double lon_new) {
        this.lon_new = lon_new;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }
}
