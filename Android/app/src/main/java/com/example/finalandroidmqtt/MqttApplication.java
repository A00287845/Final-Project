package com.example.finalandroidmqtt;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.finalandroidmqtt.util.Mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;

import java.util.ArrayList;

public class MqttApplication extends Application {
    private Mqtt mqttInstance;

    private MutableLiveData<ArrayList<MqttClient>> mutableMqttClientList;

    @Override
    public void onCreate() {
        Log.d("MqttApplication", "onCreate: ");
        super.onCreate();
        mqttInstance = Mqtt.getInstance();
        mutableMqttClientList = new MutableLiveData<>();
    }

    public Mqtt getMqtt() {
        if (mqttInstance == null) {
            mqttInstance = Mqtt.getInstance();
        }
        return mqttInstance;
    }

    public MutableLiveData<ArrayList<MqttClient>> getMutableMqttClientList() {
        return mutableMqttClientList;
    }

    public void setMutableMqttClientList(ArrayList<MqttClient> clientList){
        mutableMqttClientList.setValue(clientList);
    }

    public void addClientToList(MqttClient client){
        ArrayList<MqttClient> clientList = mutableMqttClientList.getValue();
        if (clientList == null){
            clientList = new ArrayList<>();
        }
        clientList.add(client);
        mutableMqttClientList.setValue(clientList);
    }
}
