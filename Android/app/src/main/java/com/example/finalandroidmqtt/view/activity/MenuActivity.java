package com.example.finalandroidmqtt.view.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalandroidmqtt.R;
import com.example.finalandroidmqtt.view.activity.datavisualiser.DataVisualiserActivity;
import com.example.finalandroidmqtt.view.activity.messagepublish.MessagePublishActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.openVisualiseActivityButt).setOnClickListener(v->{
            startActivity(new Intent(MenuActivity.this, DataVisualiserActivity.class));
        });

        findViewById(R.id.openMainActButt).setOnClickListener(v->{
            startActivity(new Intent(MenuActivity.this, MainActivity.class));
        });

        findViewById(R.id.openMessagePubAct).setOnClickListener(v->{
            startActivity(new Intent(MenuActivity.this, MessagePublishActivity.class));
        });
    }
}