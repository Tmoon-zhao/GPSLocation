package com.example.gpslocation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LogcatAdapter extends RecyclerView.Adapter<LogcatAdapter.LogcatHolder>{

    private List<Local_info> local_infos = new ArrayList<>();
    private List<Local_info_new> local_info_news = new ArrayList<>();

    public LogcatAdapter(List<Local_info> local_infos) {
        this.local_infos.clear();
        this.local_infos.addAll(local_infos);
    }

    public void setLocal_info_news(List<Local_info_new> local_info_news) {
        this.local_info_news.clear();
        this.local_info_news.addAll(local_info_news);
    }

    @NonNull
    @Override
    public LogcatAdapter.LogcatHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemview = layoutInflater.inflate(R.layout.cell,parent,false);
        return new LogcatAdapter.LogcatHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull final LogcatAdapter.LogcatHolder holder, int position) {
        Local_info ainfo = local_infos.get(position);
        holder.lat.setText(String.valueOf(ainfo.getLat()));
        holder.lon.setText(String.valueOf(ainfo.getLon()));
        holder.time.setText(String.valueOf(ainfo.getLocal_t()));
        holder.num.setText(String.valueOf(position+1));
        if(local_info_news.size() > position){
            Local_info_new ainfo_new = local_info_news.get(position);
            holder.lat_new.setText(String.valueOf(ainfo_new.getLat_new()));
            holder.lon_new.setText(String.valueOf(ainfo_new.getLon_new()));
            holder.correct.setText(ainfo_new.getCorrect());
        }else{
            holder.lat_new.setText("未获取");
            holder.lon_new.setText("未获取");
            holder.correct.setText("未获取");
        }
    }

    @Override
    public int getItemCount() {
        return local_infos.size();
    }

    static class LogcatHolder extends RecyclerView.ViewHolder {
        TextView lat;
        TextView lon;
        TextView time;
        TextView num;
        TextView lat_new;
        TextView lon_new;
        TextView correct;

        public LogcatHolder(@NonNull View itemView) {
            super(itemView);
            lat = (TextView)itemView.findViewById(R.id.lat);
            lon = (TextView)itemView.findViewById(R.id.lon);
            time = (TextView)itemView.findViewById(R.id.time);
            num = (TextView)itemView.findViewById(R.id.number);
            lat_new = (TextView)itemView.findViewById(R.id.lat_new);
            lon_new = (TextView)itemView.findViewById(R.id.lon_new);
            correct = (TextView)itemView.findViewById(R.id.correct);
        }
    }
}
