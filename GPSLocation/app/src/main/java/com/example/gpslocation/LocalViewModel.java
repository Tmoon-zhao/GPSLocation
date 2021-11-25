package com.example.gpslocation;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.baidu.mapapi.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class LocalViewModel extends AndroidViewModel {
    private MutableLiveData<LatLng> new_p = new MutableLiveData<>();
    private Context context;
    private String correct;

    public LocalViewModel(@NonNull Application application) {
        super(application);
    }

    void Getnewlocal(String latitude, String longitude, String time, Context context){
        this.context = context;
        new GetnewlocalAsyncTask(latitude, longitude, time).execute();
    }

    public MutableLiveData<LatLng> getNew_p() {
        return new_p;
    }

    public String getCorrect() {
        return correct;
    }

    public class GetnewlocalAsyncTask extends AsyncTask<Void, Void, Boolean>{
        private String latitude;
        private String longitude;
        private String time;
        private double lat;
        private double lon;

        public GetnewlocalAsyncTask(String latitude, String longitude, String time) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.time = time;
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            Socket s = null;
            try {
                s = new Socket("3584e564w1.qicp.vip", 40870);  //与服务器建立连接
                try {  //向服务器发送文件相关信息
                    PrintStream ps = new PrintStream(s.getOutputStream());
                    String info1 = "GET /correct?lat=" + latitude + "&lon=" + longitude + "&time=" + time+ " HTTP/1.1\n";
                    String info2 = "Host: 172.30.207.89\n";
                    String info3 = "\r\n";
                    ps.write(info1.getBytes());
                    ps.write(info2.getBytes());
                    ps.write(info3.getBytes());
                    ps.flush();
                } catch (IOException e) {
                    System.out.println("服务器连接中断");
                    Toast.makeText(context,"服务器连接中断",Toast.LENGTH_SHORT).show();
                    return false;
                }

                try{
                    DataInputStream in = new DataInputStream(s.getInputStream());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] by = new byte[1024];
                    int n;
                    while((n = in.read(by)) != -1){
                        baos.write(by, 0, n);
                        baos.flush();
                    }
                    String JsonStr = new String(baos.toByteArray(),"GB2312");
                    String[] js = JsonStr.split("\r\n");
                    JSONObject new_local = new JSONObject(js[3]);
                    lat = new_local.getDouble("lat");
                    lon = new_local.getDouble("lon");
                    correct = new_local.getString("correct");
                    in.close();
                    baos.close();
                    s.close();
                }catch (IOException e){
                    System.out.println("客户端输出文件出错");
                    Toast.makeText(context,"客户端输出文件出错",Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            } catch (IOException e) {
                System.out.println("未连接到服务器");
                Toast.makeText(context,"未连接到服务器",Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result)
            {
                LatLng newll = new LatLng(lat, lon);
                new_p.setValue(newll);
            }
        }
    }
}
