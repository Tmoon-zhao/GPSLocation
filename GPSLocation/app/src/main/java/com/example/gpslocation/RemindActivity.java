package com.example.gpslocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

public class RemindActivity extends AppCompatActivity {
    private TextView skip;
    private int TIME = 3;
    private boolean isSkip = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_remind);
        skip = (TextView) findViewById(R.id.skip);

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case -2:
                        skip.setText("跳过( "+TIME+"s )");
                        break;
                    case 1:
                        if (!isSkip) {
                            Intent intent = new Intent(RemindActivity.this, ChooseActivity.class);
                            startActivity(intent);
                            isSkip = true;
                            RemindActivity.this.finish();
                        }
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (; TIME > 0; TIME--){
                    handler.sendEmptyMessage(-2);
                    if (TIME <= 0)
                        break;
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                handler.sendEmptyMessage(1);
            }
        }).start();

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RemindActivity.this, ChooseActivity.class);
                startActivity(intent);
                isSkip = true;
                RemindActivity.this.finish();
            }
        });
    }
}

