package com.example.finalandroidmqtt;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.finalandroidmqtt.util.Mqtt;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttClient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MqttApplication extends Application {
    private Mqtt mqttInstance;

    private MutableLiveData<Set<MqttAndroidClient>> mutableMqttClientList;
    private MutableLiveData<Map<String, Set<String>>> mutableSubscriptionMap;

    @Override
    public void onCreate() {
        Log.d("MqttApplication", "onCreate: ");
        super.onCreate();
        mqttInstance = Mqtt.getInstance(this);
        mutableMqttClientList = new MutableLiveData<>();
        mutableSubscriptionMap = new MutableLiveData<>();
    }


    public Mqtt getMqtt() {
        if (mqttInstance == null) {
            mqttInstance = Mqtt.getInstance(this);
        }
        return mqttInstance;
    }

    public MutableLiveData<Set<MqttAndroidClient>> getMutableMqttClientList() {
        return mutableMqttClientList;
    }

    public void setMutableMqttClientList(Set<MqttAndroidClient> clientList) {
        mutableMqttClientList.postValue(clientList);
    }

    public void addClientToList(MqttAndroidClient client) {
        Set<MqttAndroidClient> clientList = mutableMqttClientList.getValue();
        if (clientList == null) {
            clientList = new HashSet<>();
        }
        clientList.add(client);
        mutableMqttClientList.postValue(clientList);
    }

    public MutableLiveData<Map<String, Set<String>>> getMutableSubscriptionMap() {
        return mutableSubscriptionMap;
    }

    public void setMutableSubscriptionMap(Map<String, Set<String>> subscriptionMap) {
        mutableSubscriptionMap.postValue(subscriptionMap);
    }

    public void addSubscriptionToList(String client, String subscription) {
        Map<String, Set<String>> subscriptionMap = mutableSubscriptionMap.getValue();
        if (subscriptionMap == null) {
            subscriptionMap = new HashMap<>();
        }
        Set<String> clientSubs = subscriptionMap.get("client");
        if (clientSubs == null) {
            clientSubs = new HashSet<>();
        }
        clientSubs.add(subscription);
        subscriptionMap.put(client, clientSubs);
        mutableSubscriptionMap.postValue(subscriptionMap);
    }
}
