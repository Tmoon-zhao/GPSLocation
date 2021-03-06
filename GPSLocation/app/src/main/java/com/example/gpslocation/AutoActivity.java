package com.example.gpslocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.OutputStream;
import java.io.PrintStream;

import java.net.Socket;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class AutoActivity extends AppCompatActivity{
    private static final String TAG = "unSafe";
    public LocationManager locationManager;
    private MapView mapView;  //??????????????????
    private BaiduMap baiduMap;
    private boolean isFirstLocate = true;
    Button start;
    Button finish;
    Button next;
    Button lastline;
    Button logcat;
    Button upload;
    boolean isDraw = false;
    boolean Drawfirst = true;
    List<LatLng> newpoints = new ArrayList<>();
    List<String> corrests = new ArrayList<>();
    List<LatLng> points = new ArrayList<>();  //??????????????????????????????
    List<Long> times = new ArrayList<>();
    List<Float> speed = new ArrayList<>();
    List<Double> altitude = new ArrayList<>();
    List<Float> accuracy = new ArrayList<>();
    List<Integer> sate_nums = new ArrayList<>();
    List<Integer> sate_nums_p = new ArrayList<>();
    List<List<Float>> sate_azimuths = new ArrayList<>();
    List<List<Float>> sate_elevations = new ArrayList<>();
    List<List<Integer>> sate_prns = new ArrayList<>();
    List<List<Float>> sate_snrs = new ArrayList<>();
    List<List<Boolean>> sate_hasalmanacs = new ArrayList<>();
    List<List<Boolean>> sate_hasephemeriss = new ArrayList<>();
    List<List<Boolean>> sate_useinfixs = new ArrayList<>();
    BitmapDescriptor Starticon;  //?????????????????????
    BitmapDescriptor Finishicon;  //??????????????????
    Polyline mPolyline;  //??????????????????
    private LinesDatabase linesDatabase;
    private LineDao lineDao;
    private LiveData<List<LocalLine>> allLinesLive;
    private List<LocalLine> lines = new ArrayList<>();
    int i=-1;
    LatLng last_l;
    boolean canDraw = false;
    private ResolveInfo homeInfo;
    boolean[] check_info1 = new boolean[]{true,true,true};
    boolean[] check_info2 = new boolean[]{true,true,true,true,true,false,false,true};
    LocalViewModel viewModel;
    String upload_filename1 = "";
    String upload_filename2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int loca_frequency = bundle.getInt("frequency");

        SDKInitializer.initialize(getApplicationContext());  //????????????????????????????????????
        setContentView(R.layout.activity_auto);

        PackageManager pm = getPackageManager();
        homeInfo = pm.resolveActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME),0);

        mapView = (MapView) findViewById(R.id.bmapView);  //?????????????????????
        baiduMap = mapView.getMap();  //??????????????????
        baiduMap.setMyLocationEnabled(true);  //??????????????????
        start = (Button)this.findViewById(R.id.start);  //????????????????????????
        finish = (Button)this.findViewById(R.id.finish);  //????????????????????????
        next = (Button)this.findViewById(R.id.next);
        lastline = (Button)this.findViewById(R.id.last);
        upload = (Button)this.findViewById(R.id.upload);
        logcat = (Button)this.findViewById(R.id.logcat2);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        View mark_view1 = LayoutInflater.from(this).inflate(R.layout.start,mapView,false);
        Starticon = BitmapDescriptorFactory.fromView(mark_view1);  //?????????????????????
        View mark_view2 = LayoutInflater.from(this).inflate(R.layout.end,mapView,false);
        Finishicon = BitmapDescriptorFactory.fromView(mark_view2);  //??????????????????

        linesDatabase = LinesDatabase.getDatabase(this);
        lineDao = linesDatabase.getLineDao();
        new DeleteAllAsyncTask(lineDao).execute();
        allLinesLive = lineDao.getAllLines();
        allLinesLive.observe(this, new Observer<List<LocalLine>>() {
            @Override
            public void onChanged(List<LocalLine> localLines) {
                lines = localLines;
                if(localLines!=null){
                    i=0;
                }
            }
        });

        viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(LocalViewModel.class);
        viewModel.getNew_p().observe(this, new Observer<LatLng>() {
            @Override
            public void onChanged(LatLng latLng) {
                newpoints.add(latLng);
                Local_info_new info_new = new Local_info_new(latLng.latitude,latLng.longitude,viewModel.getCorrect());
                LocalApplication.getInstance().setLocal_info_news(info_new);
                corrests.add(viewModel.getCorrect());
                if(newpoints.size() == 1){
                    DrawStart(newpoints);
                }
            }
        });

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        openGPSSettings();
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);  //??????????????????GPS????????????????????????app
        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(AutoActivity.this, Manifest.  //?????????????????????????????????
                permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(AutoActivity.this, Manifest.
                permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(AutoActivity.this, Manifest.
                permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);  //?????????????????????
            ActivityCompat.requestPermissions(AutoActivity.this,permissions,1);
        }else{
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            updateLaL(location);  //??????????????????
            LocationListener locationListener = new LocationListener() {  //???????????????
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    updateLaL(location);
                }  //?????????????????????????????????

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }
            };
            locationManager.addGpsStatusListener(statusListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, loca_frequency *1000,1,locationListener);  //???loca_frequency???????????????????????????
        }

        String[] GPS_info2 = new String[]{"????????????","?????????","??????","PNR","?????????","??????????????????","??????????????????","????????????????????????GPS????????????"};
        AlertDialog.Builder builder2 = new AlertDialog.Builder(AutoActivity.this);
        builder2.setTitle("????????????????????????????????????");
        builder2.setMultiChoiceItems(GPS_info2, check_info2, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                check_info2[which] = isChecked;
            }
        });
        builder2.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isDraw = true;  //????????????
                mapView.getMap().clear();

                LocalApplication.getInstance().clear_info();
            }
        });
        builder2.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog2 = builder2.create();

        String[] GPS_info1 = new String[]{"??????","??????","??????"};
        AlertDialog.Builder builder1 = new AlertDialog.Builder(AutoActivity.this);
        builder1.setTitle("????????????????????????????????????");
        builder1.setMultiChoiceItems(GPS_info1, check_info1, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                check_info1[which] = isChecked;
            }
        });
        builder1.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog2.show();
            }
        });
        builder1.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog1 = builder1.create();

        //????????????????????????
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.show();
            }
        });

        //????????????????????????
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(points.size() != corrests.size()){
                    Toast.makeText(AutoActivity.this,"?????????????????????",Toast.LENGTH_SHORT).show();
                }else{
                    isDraw = false;  //????????????
                    Toast.makeText(AutoActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    if (points != null) {
                        if (points.size() < 2) {
                            Toast.makeText(AutoActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        DrawFinish(points);  //????????????????????????
                        DrawFinish(newpoints);

                        LocalLine newline = new LocalLine(points, times);

                        newline.setSpeed(speed);
                        newline.setAltitude(altitude);
                        newline.setAccuracy(accuracy);
                        newline.setSate_num(sate_nums);
                        newline.setSate_azimuth(sate_azimuths);
                        newline.setSate_elevation(sate_elevations);
                        newline.setSate_prn(sate_prns);
                        newline.setSate_snr(sate_snrs);
                        newline.setSate_hasalmanac(sate_hasalmanacs);
                        newline.setSate_hasephemeris(sate_hasephemeriss);
                        newline.setSate_useinfix(sate_useinfixs);
                        newline.setSate_num_p(sate_nums_p);

                        newline.setLine_new(newpoints);
                        newline.setIscorrect(corrests);

                        new InsertAsyncTask(lineDao).execute(newline);

                        points.clear();  //???????????????
                        times.clear();
                        speed.clear();
                        altitude.clear();
                        accuracy.clear();
                        sate_nums.clear();
                        sate_azimuths.clear();
                        sate_elevations.clear();
                        sate_prns.clear();
                        sate_snrs.clear();
                        sate_hasalmanacs.clear();
                        sate_hasephemeriss.clear();
                        sate_useinfixs.clear();
                        sate_nums_p.clear();
                        Drawfirst = true;

                        newpoints.clear();
                        corrests.clear();
                    }
                }
            }
        });

        lastline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDraw && lines != null) {  //?????????????????????????????????
                    if (lines.size() > 1) {  //?????????????????????1
                        if (i >= 0 && i < lines.size() - 1) {  //??????????????????????????????
                            mapView.getMap().clear();  //????????????????????????
                            i += 1;  //???????????????
                            LocalLine mline = lines.get(i);
                            List<LatLng> mp = mline.getLine();
                            DrawStart(mp);
                            DrawLine(mp);
                            DrawFinish(mp);

                            List<LatLng> mp_new = mline.getLine_new();
                            DrawStart(mp_new);
                            DrawLine(mp_new);
                            DrawFinish(mp_new);

                            LocalApplication.getInstance().clear_info();
                            List<Long> ltime = mline.getLine_time();
                            List<String> lcorrect = mline.getIscorrect();
                            for(int pn = 0;pn < mp.size();pn++){
                                Local_info info = new Local_info(mp.get(pn).latitude,mp.get(pn).longitude,ltime.get(pn));
                                LocalApplication.getInstance().setLocal_infos(info);
                                Local_info_new info_new = new Local_info_new(mp_new.get(pn).latitude,mp_new.get(pn).longitude,lcorrect.get(pn));
                                LocalApplication.getInstance().setLocal_info_news(info_new);
                            }
                        } else if (i == lines.size() - 1) {
                            Toast.makeText(AutoActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AutoActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDraw && lines != null) {
                    if (lines.size() > 1) {
                        if (i > 0) {
                            mapView.getMap().clear();
                            i -= 1;
                            LocalLine mline = lines.get(i);
                            List<LatLng> mp = mline.getLine();
                            DrawStart(mp);
                            DrawLine(mp);
                            DrawFinish(mp);

                            List<LatLng> mp_new = mline.getLine_new();
                            DrawStart(mp_new);
                            DrawLine(mp_new);
                            DrawFinish(mp_new);

                            LocalApplication.getInstance().clear_info();
                            List<Long> ltime = mline.getLine_time();
                            List<String> lcorrect = mline.getIscorrect();
                            for(int pn = 0;pn < mp.size();pn++){
                                Local_info info = new Local_info(mp.get(pn).latitude,mp.get(pn).longitude,ltime.get(pn));
                                LocalApplication.getInstance().setLocal_infos(info);
                                Local_info_new info_new = new Local_info_new(mp_new.get(pn).latitude,mp_new.get(pn).longitude,lcorrect.get(pn));
                                LocalApplication.getInstance().setLocal_info_news(info_new);
                            }
                        } else if (i == 0) {
                            Toast.makeText(AutoActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AutoActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        View file_view = getLayoutInflater().inflate(R.layout.file_name,null);
        final EditText local_file = (EditText)file_view.findViewById(R.id.local_edit);
        final EditText info_file = (EditText)file_view.findViewById(R.id.info_edit);
        AlertDialog.Builder builder_file = new AlertDialog.Builder(AutoActivity.this);
        builder_file.setTitle("???????????????");
        builder_file.setView(file_view);
        builder_file.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                upload_filename1 = local_file.getText().toString();
                upload_filename2 = info_file.getText().toString();
                if(upload_filename1.equals("") || upload_filename2.equals("")){
                    Toast.makeText(AutoActivity.this,"????????????????????????",Toast.LENGTH_SHORT).show();
                }else{
                    upload_lines();
                }
            }
        });
        builder_file.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog_file = builder_file.create();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmmss");
                Date date = new Date(System.currentTimeMillis());
                String curtime = formatter.format(date);

                String start_local = curtime+"_GPS_routes";
                local_file.setText(start_local);
                String start_info = curtime+"_GPS_info";
                info_file.setText(start_info);
                dialog_file.show();
            }
        });

        logcat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(AutoActivity.this,LogcatActivity.class);
                startActivity(intent);
            }
        });
    }

    static class InsertAsyncTask extends AsyncTask<LocalLine,Void,Void>{
        private LineDao lineDao;

        public InsertAsyncTask(LineDao lineDao){
            this.lineDao = lineDao;
        }

        @Override
        protected Void doInBackground(LocalLine... localLines) {
            lineDao.InsertLines(localLines);
            return null;
        }

    }

    static class DeleteAllAsyncTask extends AsyncTask<LocalLine,Void,Void>{
        private LineDao lineDao;

        public DeleteAllAsyncTask(LineDao lineDao){
            this.lineDao = lineDao;
        }

        @Override
        protected Void doInBackground(LocalLine... localLines) {
            lineDao.DeleteAllLines();
            return null;
        }

    }

    private void openGPSSettings(){  //??????GPS????????????
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){  //??????GPS??????
            Toast.makeText(this,"GPS????????????",Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this,"?????????GPS!",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_SEARCH_SETTINGS); //??????intent??????????????????GPS????????????
        startActivityForResult(intent,0);
    }

    @Override  //????????????
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0){
                    for(int result : grantResults){
                        if(result != PackageManager.PERMISSION_GRANTED){
                            finish();
                            return;
                        }
                    }
                }else{
                    Toast.makeText(AutoActivity.this,"??????????????????",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    private final GpsStatus.Listener statusListener = new GpsStatus.Listener(){  //?????????????????????
        @Override
        public void onGpsStatusChanged(int event) {

        }
    };

    private void navigateTo(Location location){  //app????????????
        CoordinateConverter converter = new CoordinateConverter();  //???GPS??????????????????????????????
        converter.from(CoordinateConverter.CoordType.GPS);
        LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
        converter.coord(ll);
        LatLng BDll = converter.convert();
        Long local_time = location.getTime();
        if(isFirstLocate){  //????????????
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(BDll);
            baiduMap.animateMapStatus(update);  //?????????????????????????????????
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
            last_l = ll;
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();  //????????????????????????
        locationBuilder.latitude(BDll.latitude);
        locationBuilder.longitude(BDll.longitude);
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
        if(isDraw){  //????????????????????????
            Local_info info = new Local_info(BDll.latitude,BDll.longitude,local_time);
            LocalApplication.getInstance().setLocal_infos(info);

            getnewlatlng(BDll,local_time);

            if(Drawfirst){  //??????????????????????????????
                if(!canDraw){
                    if((last_l.latitude - ll.latitude < 3) && (last_l.longitude - ll.longitude < 3)){
                        canDraw = true;
                    }
                    else {
                        last_l = ll;
                        Toast.makeText(AutoActivity.this, "GPS???????????????", Toast.LENGTH_SHORT).show();
                        isDraw = false;
                        return;
                    }
                }
                Toast.makeText(AutoActivity.this,"?????????",Toast.LENGTH_SHORT).show();
                points.add(BDll);  //?????????????????????????????????
                times.add(local_time);
                get_GPS_info(location);
                get_sate_info();
                DrawStart(points);  //??????????????????
                Drawfirst = false;
                return;  //?????????????????????????????????????????????
            }
            if(points.size() >= 1){  //??????????????????????????????????????????????????????????????????????????????
                points.add(BDll);  //???????????????????????????
                times.add(local_time);
                get_GPS_info(location);
                get_sate_info();
                mapView.getMap().clear();  //???????????????????????????????????????????????????

                DrawStart(points); //??????????????????????????????

                DrawLine(points);

                if(newpoints.size() > 1){
                    DrawStart(newpoints);
                    DrawnewLine(newpoints);
                }
            }
        }
    }

    void getnewlatlng(LatLng oldll, Long oldt) {
        String latitude = String.valueOf(oldll.latitude);
        String longitude = String.valueOf(oldll.longitude);
        String time = String.valueOf(oldt);
        viewModel.Getnewlocal(latitude,longitude,time,AutoActivity.this);
    }

    void DrawStart(List<LatLng> p){
        MarkerOptions oStart = new MarkerOptions();
        oStart.position(p.get(0));
        oStart.icon(Starticon);
        oStart.zIndex(1);
        baiduMap.addOverlay(oStart);
    }

    void DrawFinish(List<LatLng> p){
        MarkerOptions oFinish = new MarkerOptions();  //????????????????????????
        oFinish.position(p.get(p.size()-1));
        oFinish.icon(Finishicon);
        baiduMap.addOverlay(oFinish);
        oFinish.zIndex(2);  //??????????????????
    }

    void DrawLine(List<LatLng> p){
        OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAAFF0000).points(p);  //????????????
        mPolyline = (Polyline) baiduMap.addOverlay(ooPolyline);
        mPolyline.setZIndex(3);
    }

    void DrawnewLine(List<LatLng> p){
        OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAA0000FF).points(p);  //????????????
        mPolyline = (Polyline) baiduMap.addOverlay(ooPolyline);
        mPolyline.setZIndex(4);
    }

    void get_GPS_info(Location location){
        if(check_info1[0]){
            Float l_speed = location.getSpeed();
            speed.add(l_speed);
        }
        if(check_info1[1]){
            Double l_altitude = location.getAltitude();
            altitude.add(l_altitude);
        }
        if(check_info1[2]){
            Float l_accuracy = location.getAccuracy();
            accuracy.add(l_accuracy);
        }
    }

    void get_sate_info(){
        if(ContextCompat.checkSelfPermission(AutoActivity.this, Manifest.
                permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(AutoActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else{
            GpsStatus status = locationManager.getGpsStatus(null);
            int maxSatellites = status.getMaxSatellites();
            Iterator<GpsSatellite> it = status.getSatellites().iterator();
            Integer count = 0;
            List<Float> sate_azimuth = new ArrayList<>();
            List<Float> sate_elevation = new ArrayList<>();
            List<Integer> sate_prn = new ArrayList<>();
            List<Float> sate_snr = new ArrayList<>();
            List<Boolean> sate_hasalmanac = new ArrayList<>();
            List<Boolean> sate_hasephemeris = new ArrayList<>();
            List<Boolean> sate_useinfix = new ArrayList<>();

            while(it.hasNext() && count <= maxSatellites){
                GpsSatellite s = it.next();
                if(s.getSnr() > 0){
                    count++;
                    if(check_info2[1]){
                        Float azimuth = s.getAzimuth();
                        sate_azimuth.add(azimuth);
                    }
                    if(check_info2[2]){
                        Float elevation = s.getElevation();
                        sate_elevation.add(elevation);
                    }
                    if(check_info2[3]){
                        Integer prn = s.getPrn();
                        sate_prn.add(prn);
                    }
                    if(check_info2[4]){
                        Float snr = s.getSnr();
                        sate_snr.add(snr);
                    }
                    if(check_info2[5]){
                        Boolean hasalmanac = s.hasAlmanac();
                        sate_hasalmanac.add(hasalmanac);
                    }
                    if(check_info2[6]){
                        Boolean hasephemeris = s.hasEphemeris();
                        sate_hasephemeris.add(hasephemeris);
                    }
                    if(check_info2[7]){
                        Boolean useinfix = s.usedInFix();
                        sate_useinfix.add(useinfix);
                    }
                }
            }
            if(check_info2[0]){
                sate_nums.add(count);
            }
            sate_nums_p.add(count);
            sate_azimuths.add(sate_azimuth);
            sate_elevations.add(sate_elevation);
            sate_prns.add(sate_prn);
            sate_snrs.add(sate_snr);
            sate_hasalmanacs.add(sate_hasalmanac);
            sate_hasephemeriss.add(sate_hasephemeris);
            sate_useinfixs.add(sate_useinfix);
        }
    }

    private void updateLaL(Location loc){  //????????????
        if(loc != null)
        {
            navigateTo(loc);
        }
        else
        {
            Toast.makeText(AutoActivity.this,"???????????????",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        LocationClientOption option = new LocationClientOption();
        switch (item.getItemId()){
            case R.id.High_Accuracy_mode:
                if(option.getLocationMode() != LocationClientOption.LocationMode.Hight_Accuracy){
                    option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
                }
                Toast.makeText(AutoActivity.this,"?????????????????????????????????",Toast.LENGTH_SHORT).show();
                break;
            case R.id.GPS_mode:
                if(option.getLocationMode() != LocationClientOption.LocationMode.Device_Sensors){
                    option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
                }
                Toast.makeText(AutoActivity.this,"????????????GPS????????????",Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }

    void upload_lines(){
        boolean success = true;

        int n = 0;
        for(LocalLine l:lines){  //????????????????????????
            List<LatLng> ll = l.getLine();
            List<Long> ltime = l.getLine_time();
            List<LatLng> ll_new = l.getLine_new();
            List<String> lcorrect = l.getIscorrect();

            n++;

            Socket s = null;
            try{
                s = new Socket("121.37.8.35",80);  //????????????????????????

                String mfilename = upload_filename1+n+".csv";  //?????????

                try{  //????????????????????????????????????
                    PrintStream ps = new PrintStream(s.getOutputStream());
                    String info1 = "POST /" + mfilename + " HTTP/1.1\n";
                    String info2 = "Host: 192.168.0.133\n";
                    String info3 = "\r\n";
                    ps.write(info1.getBytes());
                    ps.write(info2.getBytes());
                    ps.write(info3.getBytes());
                    ps.flush();
                }catch (IOException e){
                    System.out.println("?????????????????????");
                    success = false;
                }

                try {  //??????2s????????????????????????????????????
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    success = false;
                }

                //????????????
                StringBuffer buffer = new StringBuffer();
                buffer.append("lat,lon,timestamp,lat_correct,lon_correct,correct\r\n");  //?????????
                for(int lln = 0;lln < ll.size();lln++){  //???????????????????????????????????????
                    buffer.append(ll.get(lln).latitude+","+ll.get(lln).longitude+","+ltime.get(lln)+","+
                            ll_new.get(lln).latitude+","+ll_new.get(lln).longitude+","+lcorrect.get(lln)+"\r\n");
                }

                try {
                    OutputStream os = s.getOutputStream();
                    String data = buffer.toString();
                    os.write(data.getBytes());  //??????
                    System.out.println("??????" + mfilename + "?????????");
                    os.flush();
                    os.close();
                    s.close();
                }catch (IOException e){
                    System.out.println("???????????????????????????");
                    e.printStackTrace();
                    success = false;
                }
            }catch (IOException e){
                System.out.println("?????????????????????");
                success = false;
            }

            //????????????
            List<Float> speed = l.getSpeed();
            List<Double> altitude = l.getAltitude();
            List<Float> accuracy = l.getAccuracy();
            List<Integer> sate_num = l.getSate_num();
            List<Integer> sate_num_p = l.getSate_num_p();
            List<List<Float>> sate_azimuth = l.getSate_azimuth();
            List<List<Float>> sate_elevation = l.getSate_elevation();
            List<List<Integer>> sate_prn = l.getSate_prn();
            List<List<Float>> sate_snr = l.getSate_snr();
            List<List<Boolean>> sate_hasalmanac = l.getSate_hasalmanac();
            List<List<Boolean>> sate_hasephemeris = l.getSate_hasephemeris();
            List<List<Boolean>> sate_useinfix = l.getSate_useinfix();

            try{
                JSONObject ALL_info = new JSONObject();
                for(int lln = 0;lln < ll.size();lln++){
                    JSONObject info = new JSONObject();
                    if(speed.size() > 0){
                        info.put("speed",speed.get(lln));
                    }
                    if(altitude.size() > 0){
                        info.put("altitude",altitude.get(lln));
                    }
                    if(accuracy.size() > 0){
                        info.put("accuracy",accuracy.get(lln));
                    }
                    if(sate_num.size() > 0){
                        info.put("satellite_num",sate_num.get(lln));
                    }
                    JSONArray array = new JSONArray();
                    for(int i = 0;i < sate_num_p.get(lln);i++){
                        JSONObject arr = new JSONObject();
                        arr.put("satellite_id",i+1);
                        if(sate_azimuth.get(0).size() > 0){
                            arr.put("azimuth",sate_azimuth.get(lln).get(i));
                        }
                        if(sate_elevation.get(0).size() > 0){
                            arr.put("elevation",sate_elevation.get(lln).get(i));
                        }
                        if(sate_prn.get(0).size() > 0){
                            arr.put("prn",sate_prn.get(lln).get(i));
                        }
                        if(sate_snr.get(0).size() > 0){
                            arr.put("snr",sate_snr.get(lln).get(i));
                        }
                        if(sate_hasalmanac.get(0).size() > 0){
                            arr.put("hasalmanac",sate_hasalmanac.get(lln).get(i));
                        }
                        if(sate_hasephemeris.get(0).size() > 0){
                            arr.put("hasephemeris",sate_hasephemeris.get(lln).get(i));
                        }
                        if(sate_useinfix.get(0).size() > 0){
                            arr.put("useinfix",sate_useinfix.get(lln).get(i));
                        }
                        array.put(arr);
                    }
                    ALL_info.put("GPS_info"+lln,info);
                    ALL_info.put("satellite_info"+lln,array);
                }

                Socket s2 = null;
                try{
                    s2 = new Socket("121.37.8.35",80);  //????????????????????????

                    String mfilename2 = upload_filename2+n+".json";  //?????????

                    try{  //????????????????????????????????????
                        PrintStream ps = new PrintStream(s2.getOutputStream());
                        String info1 = "POST /" + mfilename2 + " HTTP/1.1\n";
                        String info2 = "Host: 192.168.0.133\n";
                        String info3 = "\r\n";
                        ps.write(info1.getBytes());
                        ps.write(info2.getBytes());
                        ps.write(info3.getBytes());
                        ps.flush();
                    }catch (IOException e){
                        System.out.println("?????????????????????");
                        success = false;
                    }

                    try {  //??????2s????????????????????????????????????
                        Thread.sleep(2000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                        success = false;
                    }

                    try {
                        OutputStream os = s2.getOutputStream();
                        String data = ALL_info.toString();
                        byte b[] = {(byte)0xEF, (byte)0xBB, (byte)0xBF};  //????????????utf-8?????????????????????????????????
                        os.write(b);
                        os.write(data.getBytes());  //??????
                        System.out.println("??????" + mfilename2 + "?????????");
                        os.flush();
                        os.close();
                        s2.close();
                    }catch (IOException e){
                        System.out.println("???????????????????????????");
                        e.printStackTrace();
                        success = false;
                    }
                }catch (IOException e){
                    System.out.println("?????????????????????");
                    success = false;
                }
            }catch (JSONException e){
                e.printStackTrace();
                success = false;
            }
        }
        if(success){
            Toast.makeText(AutoActivity.this,"??????????????????",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(AutoActivity.this,"??????????????????",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() { //??????????????????
        super.onDestroy();
        mapView.getMap().clear();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
        mapView = null;
        Starticon.recycle();
        Finishicon.recycle();
    }
}
