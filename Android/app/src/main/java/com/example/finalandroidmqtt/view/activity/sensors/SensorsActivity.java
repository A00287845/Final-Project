package com.example.finalandroidmqtt.view.activity.sensors;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalandroidmqtt.MqttApplication;
import com.example.finalandroidmqtt.R;
import com.example.finalandroidmqtt.util.RepeatedTaskLooper;

public class SensorsActivity extends AppCompatActivity {
    private MqttApplication application;
    private RepeatedTaskLooper looper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (MqttApplication) getApplication();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sensors);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setUpObservation();

    }

    private void setUpObservation() {
        application.getMqtt().getSensorActive().observe(this, active -> {
            Log.d("Eoghan", "MainActivity sensorActive observed");
            if (active != null) {
                if (!active) {
                    if (looper != null) {
                        looper.stop();
                    }
                    return;
                }
                looper = new RepeatedTaskLooper(this);
                looper.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (looper != null) {
            looper.stop();
        }
    }
}