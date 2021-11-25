package com.example.gpslocation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseActivity extends AppCompatActivity {
    Button auto;
    Button man;
    Button collect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        auto = (Button)this.findViewById(R.id.auto);
        man = (Button)this.findViewById(R.id.man);
        collect = (Button)this.findViewById(R.id.collect);

        auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseActivity.this, StartActivity.class);
                intent.putExtra("model","auto");
                startActivity(intent);
            }
        });

        man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseActivity.this, ManActivity.class);
                startActivity(intent);
            }
        });

        collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseActivity.this, StartActivity.class);
                intent.putExtra("model","collect");
                startActivity(intent);
            }
        });
    }
}
