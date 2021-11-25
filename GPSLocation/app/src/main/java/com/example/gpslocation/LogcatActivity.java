package com.example.gpslocation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.List;

public class LogcatActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    LogcatAdapter logcatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logcat);

        List<Local_info> local_infos = LocalApplication.getInstance().getLocal_infos();
        List<Local_info_new> local_info_news = LocalApplication.getInstance().getLocal_info_news();

        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        logcatAdapter = new LogcatAdapter(local_infos);
        logcatAdapter.setLocal_info_news(local_info_news);
        recyclerView.setAdapter(logcatAdapter);
    }
}
