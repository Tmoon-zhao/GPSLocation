package com.example.gpslocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ManActivity extends AppCompatActivity {
    public LocationManager locationManager;
    private MapView mapView;  //用于地图显示
    private BaiduMap baiduMap;
    private Button local;
    private Button logcat;
    boolean isFirstLocate = true;
    LatLng last;
    Long time;
    BitmapDescriptor point_icon;
    BitmapDescriptor point_icon2;
    LocalViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SDKInitializer.initialize(getApplicationContext());  //地图显示有关的初始化操作
        setContentView(R.layout.activity_man);

        local = (Button)this.findViewById(R.id.locat);
        logcat = (Button)this.findViewById(R.id.logcat);

        mapView = (MapView) findViewById(R.id.bmapView2);  //获取并显示地图
        baiduMap = mapView.getMap();  //地图总控制器
        baiduMap.setMyLocationEnabled(true);  //开启定位图层

        View mark_view1 = LayoutInflater.from(this).inflate(R.layout.mark,mapView,false);
        point_icon = BitmapDescriptorFactory.fromView(mark_view1);
        View mark_view2 = LayoutInflater.from(this).inflate(R.layout.mark_new,mapView,false);
        point_icon2 = BitmapDescriptorFactory.fromView(mark_view2);

        viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(LocalViewModel.class);
        viewModel.getNew_p().observe(this, new Observer<LatLng>() {
            @Override
            public void onChanged(LatLng latLng) {
                DrawnewMark(latLng);
                Local_info_new info_new = new Local_info_new(latLng.latitude,latLng.longitude,viewModel.getCorrect());
                LocalApplication.getInstance().setLocal_info_news(info_new);
            }
        });

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        openGPSSettings();
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);  //强制只能使用GPS定位，否则会退出app
        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(ManActivity.this, Manifest.  //查询权限是否都得到许可
                permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(ManActivity.this, Manifest.
                permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(ManActivity.this, Manifest.
                permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);  //不是则申请权限
            ActivityCompat.requestPermissions(ManActivity.this,permissions,1);
        }else{
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            updateLaL(location);  //首次定位操作
            LocationListener locationListener = new LocationListener() {  //定位监听器
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    updateLaL(location);
                }  //位置发生改变，更新定位

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,1,locationListener);  //每loca_frequency秒回调一次定位状态
        }

        local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawMark(last);
                Local_info info = new Local_info(last.latitude,last.longitude,time);
                LocalApplication.getInstance().setLocal_infos(info);
                getnewlatlng(new LatLng(info.getLat(),info.getLon()),info.getLocal_t());
            }
        });

        logcat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ManActivity.this,LogcatActivity.class);
                startActivity(intent);
            }
        });
    }

    private void openGPSSettings(){  //判断GPS是否打开
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){  //检测GPS状态
            Toast.makeText(this,"GPS模块正常",Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this,"请开启GPS!",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_SEARCH_SETTINGS); //利用intent对象返回开启GPS导航设置
        startActivityForResult(intent,0);
    }

    private void updateLaL(Location loc){  //定位更新
        if(loc != null)
        {
            navigateTo(loc);
        }
        else
        {
            Toast.makeText(ManActivity.this,"无位置信息",Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateTo(Location location){  //app主要函数
        CoordinateConverter converter = new CoordinateConverter();  //将GPS定位坐标改为百度坐标
        converter.from(CoordinateConverter.CoordType.GPS);
        LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
        converter.coord(ll);
        LatLng BDll = converter.convert();
        Long local_time = location.getTime();
        if(isFirstLocate){  //首次定位
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(BDll);
            baiduMap.animateMapStatus(update);  //将地图移动到定位的位置
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();  //将“我”显示出来
        locationBuilder.latitude(BDll.latitude);
        locationBuilder.longitude(BDll.longitude);
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);

        last = BDll;
        time = local_time;
    }

    void getnewlatlng(LatLng oldll, Long oldt) {
        String latitude = String.valueOf(oldll.latitude);
        String longitude = String.valueOf(oldll.longitude);
        String time = String.valueOf(oldt);
        viewModel.Getnewlocal(latitude,longitude,time,ManActivity.this);
    }

    void DrawMark(LatLng p){
        MarkerOptions oPoint = new MarkerOptions();
        oPoint.position(p);
        oPoint.icon(point_icon);
        oPoint.zIndex(1);
        baiduMap.addOverlay(oPoint);
    }

    void DrawnewMark(LatLng p){
        MarkerOptions oPoint = new MarkerOptions();
        oPoint.position(p);
        oPoint.icon(point_icon2);
        oPoint.zIndex(1);
        baiduMap.addOverlay(oPoint);
    }

    @Override
    protected void onDestroy() { //退出释放资源
        super.onDestroy();
        mapView.getMap().clear();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
        mapView = null;
        LocalApplication.getInstance().getLocal_infos().clear();
        LocalApplication.getInstance().getLocal_info_news().clear();
    }
}
