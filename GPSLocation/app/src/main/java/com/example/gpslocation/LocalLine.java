package com.example.gpslocation;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

@Entity
public class LocalLine {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @TypeConverters(LatlngTypeConverter.class)
    private List<LatLng> line = new ArrayList<>();

    @TypeConverters(LongTypeConverter.class)
    private List<Long> line_time = new ArrayList<>();

    @TypeConverters(LatlngTypeConverter.class)
    private List<LatLng> line_new = new ArrayList<>();

    @TypeConverters(StringTypeConverter.class)
    private List<String> iscorrect = new ArrayList<>();

    @TypeConverters(FloatTypeConverter.class)
    private List<Float> speed = new ArrayList<>();

    @TypeConverters(DoubleTypeConverter.class)
    private List<Double> altitude = new ArrayList<>();

    @TypeConverters(FloatTypeConverter.class)
    private List<Float> accuracy = new ArrayList<>();

    @TypeConverters(IntegerTypeConverter.class)
    private List<Integer> sate_num = new ArrayList<>();

    @TypeConverters(IntegerTypeConverter.class)
    private List<Integer> sate_num_p = new ArrayList<>();

    @TypeConverters(ListFloatConverter.class)
    private List<List<Float>> sate_azimuth = new ArrayList<>();

    @TypeConverters(ListFloatConverter.class)
    private List<List<Float>> sate_elevation = new ArrayList<>();

    @TypeConverters(ListIntegerConverter.class)
    private List<List<Integer>> sate_prn = new ArrayList<>();

    @TypeConverters(ListFloatConverter.class)
    private List<List<Float>> sate_snr = new ArrayList<>();

    @TypeConverters(ListBooleanConverter.class)
    private List<List<Boolean>> sate_hasalmanac = new ArrayList<>();

    @TypeConverters(ListBooleanConverter.class)
    private List<List<Boolean>> sate_hasephemeris = new ArrayList<>();

    @TypeConverters(ListBooleanConverter.class)
    private List<List<Boolean>> sate_useinfix = new ArrayList<>();

    public LocalLine(List<LatLng> line, List<Long> line_time) {
        this.line.addAll(line);
        this.line_time.addAll(line_time);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<LatLng> getLine() {
        return line;
    }

    public void setLine(List<LatLng> line) {
        this.line = line;
    }

    public List<Long> getLine_time() {
        return line_time;
    }

    public void setLine_time(List<Long> line_time) {
        this.line_time = line_time;
    }

    public List<Float> getSpeed() {
        return speed;
    }

    public void setSpeed(List<Float> speed) {
        this.speed.addAll(speed);
    }

    public List<Double> getAltitude() {
        return altitude;
    }

    public void setAltitude(List<Double> altitude) {
        this.altitude.addAll(altitude);
    }

    public List<Float> getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(List<Float> accuracy) {
        this.accuracy.addAll(accuracy);
    }

    public List<Integer> getSate_num() {
        return sate_num;
    }

    public void setSate_num(List<Integer> sate_num) {
        this.sate_num.addAll(sate_num);
    }

    public List<List<Float>> getSate_azimuth() {
        return sate_azimuth;
    }

    public void setSate_azimuth(List<List<Float>> sate_azimuth) {
        this.sate_azimuth.addAll(sate_azimuth);
    }

    public List<List<Float>> getSate_elevation() {
        return sate_elevation;
    }

    public void setSate_elevation(List<List<Float>> sate_elevation) {
        this.sate_elevation.addAll(sate_elevation);
    }

    public List<List<Integer>> getSate_prn() {
        return sate_prn;
    }

    public void setSate_prn(List<List<Integer>> sate_prn) {
        this.sate_prn.addAll(sate_prn);
    }

    public List<List<Float>> getSate_snr() {
        return sate_snr;
    }

    public void setSate_snr(List<List<Float>> sate_snr) {
        this.sate_snr.addAll(sate_snr);
    }

    public List<List<Boolean>> getSate_hasalmanac() {
        return sate_hasalmanac;
    }

    public void setSate_hasalmanac(List<List<Boolean>> sate_hasalmanac) {
        this.sate_hasalmanac.addAll(sate_hasalmanac);
    }

    public List<List<Boolean>> getSate_hasephemeris() {
        return sate_hasephemeris;
    }

    public void setSate_hasephemeris(List<List<Boolean>> sate_hasephemeris) {
        this.sate_hasephemeris.addAll(sate_hasephemeris);
    }

    public List<List<Boolean>> getSate_useinfix() {
        return sate_useinfix;
    }

    public void setSate_useinfix(List<List<Boolean>> sate_useinfix) {
        this.sate_useinfix.addAll(sate_useinfix);
    }

    public List<Integer> getSate_num_p() {
        return sate_num_p;
    }

    public void setSate_num_p(List<Integer> sate_num_p) {
        this.sate_num_p.addAll(sate_num_p);
    }

    public List<LatLng> getLine_new() {
        return line_new;
    }

    public void setLine_new(List<LatLng> line_new) {
        this.line_new.addAll(line_new);
    }

    public List<String> getIscorrect() {
        return iscorrect;
    }

    public void setIscorrect(List<String> iscorrect) {
        this.iscorrect.addAll(iscorrect);
    }
}
