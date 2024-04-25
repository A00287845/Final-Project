package com.example.finalandroidmqtt.view.activity.sensors;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalandroidmqtt.MqttApplication;
import com.example.finalandroidmqtt.R;
import com.example.finalandroidmqtt.pojo.ClientHolder;
import com.example.finalandroidmqtt.util.RepeatedTaskLooper;
import com.example.finalandroidmqtt.view.activity.sensors.fragments.AddPublisherFragment;

import java.util.Arrays;
import java.util.Objects;

public class SensorsActivity extends AppCompatActivity {
    private MqttApplication application;
    private RepeatedTaskLooper looper;

    private boolean setup = false;

    private final String SENSOR_CLIENT_NAME = "android_sensor_client";
    Button addSensorButton;

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
        application.getMqtt().setupBroker(application.getApplicationContext(), SENSOR_CLIENT_NAME, "ssl://930094acb7da4acfbf5761b3ac2c7c90.s1.eu.hivemq.cloud:8883");

        addSensorButton = findViewById(R.id.addSensorTopicButt);

        addSensorButton.setOnClickListener(v -> {
            if (!setup) {
                Toast.makeText(this, "Waiting for client to initialize", Toast.LENGTH_SHORT).show();
                return;
            }
            AddPublisherFragment dialogFragment = AddPublisherFragment.newInstance(SENSOR_CLIENT_NAME);
            dialogFragment.show(getSupportFragmentManager(), "addSensorTopic");
        });
        SwitchCompat sensorSwitch = findViewById(R.id.sensorSwitch);
        sensorSwitch.setOnClickListener(v -> {
            Log.d("SensorcActivity", "sensor switch clicked");
            if (application.getSensorHandler() != null) {
                if (sensorSwitch.isChecked()) {
                    Log.d("SensorcActivity", "sensor switch activated");

                    application.getSensorHandler().startLightSensor();
                    application.getSensorHandler().startProximitySensor();
                } else {
                    Log.d("SensorcActivity", "sensor switch deactivated");

                    application.getSensorHandler().stopLightSensor();
                    application.getSensorHandler().stopProximitySensor();
                }
            }
        });
        Spinner sensorSpeedSpinner = findViewById(R.id.pollSpeedSpinner);
        ArrayAdapter<Integer> sensorSpeedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Arrays.asList(500, 1000, 2000, 5000, 10000));
        sensorSpeedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sensorSpeedSpinner.setAdapter(sensorSpeedAdapter);

        sensorSpeedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int pollSpeed = sensorSpeedAdapter.getItem(position);
                if (looper == null) {
                    return;
                }
                if (looper.isRunning()) {
                    if (sensorSwitch.isChecked()) {
                        looper.setInterval(pollSpeed);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

        application.getMqtt().getClients().observe(this, clients -> {
            if (clients != null) {
                ClientHolder holder = application.getMqtt().getClientHolderFromListByName(SENSOR_CLIENT_NAME, Objects.requireNonNull(application.getMqtt().getClients().getValue()));
                if (holder == null) {
                    return;
                }
                setup = true;
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