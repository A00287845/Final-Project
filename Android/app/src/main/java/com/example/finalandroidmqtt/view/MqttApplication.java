package com.example.finalandroidmqtt.view;

import android.app.Application;
import android.util.Log;

import com.example.finalandroidmqtt.util.Mqtt;

public class MqttApplication extends Application {
    private Mqtt mqttInstance;

    @Override
    public void onCreate() {
        Log.d("MqttApplication", "onCreate: ");
        super.onCreate();
        mqttInstance = Mqtt.getInstance();
    }


    public Mqtt getMqtt() {
        if (mqttInstance == null) {
            mqttInstance = Mqtt.getInstance();
        }
        return mqttInstance;
    }
}
