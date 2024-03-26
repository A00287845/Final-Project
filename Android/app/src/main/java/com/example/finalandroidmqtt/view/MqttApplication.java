package com.example.finalandroidmqtt.view;

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

    private MutableLiveData<Map<String, MqttAndroidClient>> mutableMqttClientList;
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

    public MutableLiveData<Map<String, MqttAndroidClient>> getMutableMqttClientList() {
        return mutableMqttClientList;
    }

    public void setMutableMqttClientList(Map<String, MqttAndroidClient> clientList) {
        mutableMqttClientList.postValue(clientList);
    }

    public void addClientToList(MqttAndroidClient client) {
        Map<String, MqttAndroidClient> clientList = mutableMqttClientList.getValue();
        if (clientList == null) {
            clientList = new HashMap<>();
        }
        clientList.put(client.getClientId(), client);
        mutableMqttClientList.postValue(clientList);
    }

    public MutableLiveData<Map<String, Set<String>>> getMutableSubscriptionMap() {
        return mutableSubscriptionMap;
    }

    public void setMutableSubscriptionMap(Map<String, Set<String>> subscriptionMap) {
        mutableSubscriptionMap.postValue(subscriptionMap);
    }

    public void addSubscriptionToList(String client, String subscription) {
        Log.d("Eoghan", "MqttApplication addSubscriptionToList called with client: " + client + ", subscription: " + subscription);

        Map<String, Set<String>> subscriptionMap = mutableSubscriptionMap.getValue();
        Log.d("Eoghan", "MqttApplication Current subscriptionMap retrieved. Null? " + (subscriptionMap == null));

        if (subscriptionMap == null) {
            subscriptionMap = new HashMap<>();
            Log.d("Eoghan", "MqttApplication subscriptionMap was null, initialized new HashMap.");
        }

        Set<String> clientSubs = subscriptionMap.get(client);
        Log.d("Eoghan", "MqttApplication Client subscriptions retrieved for client '" + client + "'. Null? " + (clientSubs == null));

        if (clientSubs == null) {
            clientSubs = new HashSet<>();
            Log.d("Eoghan", "MqttApplication clientSubs was null, initialized new HashSet.");
        }

        clientSubs.add(subscription);
        Log.d("Eoghan", "MqttApplication Added subscription '" + subscription + "' to clientSubs. Total subs now: " + clientSubs.size());

        subscriptionMap.put(client, clientSubs);
        Log.d("Eoghan", "MqttApplication Updated subscriptionMap with client '" + client + "' subscriptions. Total clients now: " + subscriptionMap.size());

        mutableSubscriptionMap.postValue(subscriptionMap);
        Log.d("Eoghan", "MqttApplication Posted updated subscriptionMap to mutableSubscriptionMap.");
    }

}
