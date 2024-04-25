package com.example.finalandroidmqtt.pojo;

import android.hardware.Sensor;
import android.util.Pair;

//import org.eclipse.paho.android.service.MqttAndroidClient;
import info.mqtt.android.service.MqttAndroidClient;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClientHolder {
    private String name;
    private MqttAndroidClient client;
    private Set<String> subscriptions = new HashSet<>();
    List<Pair<Sensor, String>> sensorTopics = new ArrayList<>();



    public ClientHolder(){}
    public ClientHolder(String clientName, MqttAndroidClient client){
        this.name = clientName;
        this.client = client;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MqttAndroidClient getClient() {
        return client;
    }

    public void setClient(MqttAndroidClient client) {
        this.client = client;
    }

    public Set<String> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<String> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List<Pair<Sensor, String>> getSensorTopics() {
        return sensorTopics;
    }

    public void setSensorTopics(List<Pair<Sensor, String>> sensorTopics) {
        this.sensorTopics = sensorTopics;
    }

    public String getSensorTopicsForTv(){
        StringBuilder sb = new StringBuilder();
        for(Pair<Sensor, String> pair: sensorTopics){
            sb.append("sensorName: ");
            sb.append(pair.first.getName());
            sb.append(" topic ");
            sb.append(pair.second);
            sb.append("\n\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "ClientHolder{" +
                "name='" + name + '\'' +
                ", client=" + client +
                ", subscriptions=" + subscriptions +
                ", sensorTopics=" + sensorTopics +
                '}';
    }
}
