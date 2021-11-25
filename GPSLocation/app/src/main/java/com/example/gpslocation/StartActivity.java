package com.example.gpslocation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {
    SeekBar seekBar;
    Button button;
    TextView textView;
    int seek_number = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Intent intent_get = getIntent();
        String model = intent_get.getStringExtra("model");

        seekBar = (SeekBar)findViewById(R.id.seekBar);
        button = (Button)findViewById(R.id.seek_but);
        textView = (TextView)findViewById(R.id.seek_num);
        textView.setText("5");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setText(String.valueOf(progress));
                seek_number = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(seek_number >= 1){
                    Intent intent;
                    if(model.equals("collect")){
                        intent = new Intent(StartActivity.this,MainActivity.class);
                    }else{
                        intent = new Intent(StartActivity.this,AutoActivity.class);
                    }
                    Bundle bundle = new Bundle();
                    bundle.putInt("frequency",seek_number);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(StartActivity.this,"采集频率不能为0！",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
