package com.example.finalandroidmqtt.util;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.finalandroidmqtt.MqttApplication;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Mqtt {

    private static volatile Mqtt instance;
    private MqttApplication app;

    private Mqtt(MqttApplication app) {
        this.app = app;
    }

    public static Mqtt getInstance(MqttApplication app) {
        if (instance == null) {
            synchronized (Mqtt.class) {
                if (instance == null) {
                    instance = new Mqtt(app);
                }
            }
        }
        return instance;
    }


    public void setupBroker(Context appContext, String cliendId, String brokerUri) {
        MqttAndroidClient mqttAndroidClient = getMqttAndroidClient(appContext, cliendId, brokerUri);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);

        try {
            IMqttToken token = mqttAndroidClient.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("Mqtt setupBroker", "onSuccess: Adding " + mqttAndroidClient.getClientId() + " to connected clients list.");
                    app.addClientToList(mqttAndroidClient);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("Mqtt setupBroker", "onFailure: ", exception);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    @NonNull
    private static MqttAndroidClient getMqttAndroidClient(Context appContext, String clientId, String serverUri) {
        MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(appContext, serverUri, clientId);

        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.e("Mqtt getMqttAndroidClient", "connectionLost: client: " + mqttAndroidClient.getClientId(), cause);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                Log.d("Mqtt getMqttAndroidClient", "messageArrived: topic: " + topic + " message " + message);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d("Mqtt getMqttAndroidClient", "deliveryComplete: token: " + token);
            }
        });
        return mqttAndroidClient;
    }

}
