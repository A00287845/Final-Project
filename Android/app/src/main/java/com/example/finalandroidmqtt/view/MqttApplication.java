package com.example.finalandroidmqtt.view;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.finalandroidmqtt.util.Mqtt;

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
