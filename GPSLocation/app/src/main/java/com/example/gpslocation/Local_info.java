package com.example.gpslocation;

public class Local_info{
    private double lat;
    private double lon;
    private Long local_t;

    public Local_info(double lat, double lon, Long local_t) {
        this.lat = lat;
        this.lon = lon;
        this.local_t = local_t;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public Long getLocal_t() {
        return local_t;
    }

    public void setLocal_t(Long local_t) {
        this.local_t = local_t;
    }
}
