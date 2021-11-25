package com.example.gpslocation;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

public class LocalApplication extends Application {
    private List<Local_info> local_infos;
    private List<Local_info_new> local_info_news;
    private static LocalApplication instance;

    @Override
    public void onCreate() {
        instance = this;
        local_infos = new ArrayList<>();
        local_info_news = new ArrayList<>();
        super.onCreate();
    }

    public List<Local_info> getLocal_infos() {
        return local_infos;
    }

    public void setLocal_infos(Local_info local_info) {
        this.local_infos.add(local_info);
    }

    public List<Local_info_new> getLocal_info_news() {
        return local_info_news;
    }

    public void setLocal_info_news(Local_info_new local_info_new) {
        this.local_info_news.add(local_info_new);
    }

    public static LocalApplication getInstance() {
        return instance;
    }

    public void clear_info(){
        local_infos.clear();
        local_info_news.clear();
    }
}
