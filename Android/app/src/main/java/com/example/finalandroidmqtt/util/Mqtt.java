package com.example.finalandroidmqtt.util;

import android.content.Context;
import android.hardware.Sensor;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.finalandroidmqtt.pojo.ClientHolder;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Mqtt {
    private static volatile Mqtt instance;
    private final MutableLiveData<List<ClientHolder>> clients = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Set<String>>> mutableSubscriptionMap;
    private final MutableLiveData<List<String>> mutableSubscribedMessagesList;
    private final MutableLiveData<Map<String, List<Pair<Sensor, String>>>> sensorTopics = new MutableLiveData<>();
    private final Map<String, List<Pair<Sensor, String>>> clientSensors = new HashMap<>();


    private Mqtt() {
        mutableSubscriptionMap = new MutableLiveData<>();
        mutableSubscribedMessagesList = new MutableLiveData<>();
    }

    public static Mqtt getInstance() {
        if (instance == null) {
            synchronized (Mqtt.class) {
                if (instance == null) {
                    instance = new Mqtt();
                }
            }
        }
        return instance;
    }


    public MutableLiveData<List<ClientHolder>> getClients() {
        return clients;
    }



    public void addClient(MqttAndroidClient client) {
        List<ClientHolder> clientList = clients.getValue();
        if (clientList == null) {
            clientList = new ArrayList<>();
        }
        clientList.add(new ClientHolder()client.getClientId(), client);
        mutableMqttClientMap.postValue(clientList);
    }

    public void removeClientByNameFromMap(String clientName) {
        Log.d("EOGHAN", "Mqtt removeClientByNameFromMap: client name " + clientName);
        // Early retrieval with null checks
        Map<String, MqttAndroidClient> clientMap = mutableMqttClientMap.getValue();
        Map<String, Set<String>> subscriptionMap = mutableSubscriptionMap.getValue();
        if (clientMap == null) {
            Log.d("EOGHAN", "Mqtt removeClientByNameFromMap a map is null");

            return; // No action needed if either map is null
        }

        // Attempt to remove the client and disconnect if present
        MqttAndroidClient client = clientMap.remove(clientName);
        if (client != null) {
            try {
                Log.d("EOGHAN", "Mqtt removeClientByNameFromMap disconnecting client");

                client.disconnect(); // Disconnect the client if it was found
            } catch (MqttException e) {
                // Consider logging the exception instead of throwing a runtime exception
                Log.e("removeClientByName", "Error disconnecting client: " + clientName, e);
                // Optionally, handle the error more gracefully or rethrow as a checked exception
            }
        }
        mutableMqttClientMap.postValue(clientMap);

        if (subscriptionMap == null) {
            return;
        }

        // Remove the subscription associated with the clientName if present
        subscriptionMap.remove(clientName);

        // Post the updated maps back to their respective LiveData or observable fields
        mutableSubscriptionMap.postValue(subscriptionMap);
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

    public void addSubbedMessageToList(String topic, String message) {
        List<String> messageList = mutableSubscribedMessagesList.getValue();
        if (messageList == null) {
            messageList = new ArrayList<>();
        }
        Log.d("EOGHAN", "Mqtt addSubbedMssageToList: Topic: " + topic + " Message: " + message);

        messageList.add("Topic: " + topic + " Message: " + message);
        mutableSubscribedMessagesList.postValue(messageList);
    }


    public void setupBroker(Context appContext, String clientId, String brokerUri) {
        Log.d("Eoghan", "Mqtt setupBroker");

        MqttAndroidClient mqttAndroidClient = getMqttAndroidClient(appContext, clientId, brokerUri);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        options.setUserName("A00287845");
        options.setPassword(new String(Base64.getDecoder().decode("TmlwcGxlczEq"), StandardCharsets.UTF_8).toCharArray());

        try {
            IMqttToken token = mqttAndroidClient.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("Eoghan", "Mqtt setupBroker onSuccess: Adding " + mqttAndroidClient.getClientId() + " to connected clients list.");
                    addClientToMap(mqttAndroidClient);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("Eoghan", "Mqtt setupBroker onFailure: ", exception);
                }
            });
        } catch (MqttException e) {
            Log.e("Eoghan", "Mqtt setupBroker mqtt exception: ", e);
        }

    }

    @NonNull
    private MqttAndroidClient getMqttAndroidClient(Context appContext, String clientId, String serverUri) {
        MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(appContext, serverUri, clientId);

        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.e("EOGHAN", "Mqtt getMqttAndroidClient connectionLost: client: " + mqttAndroidClient.getClientId(), cause);
                removeClientByNameFromMap(mqttAndroidClient.getClientId());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                Log.d("EOGHAN", "Mqtt getMqttAndroidClient messageArrived: topic: " + topic + " message " + message);
                addSubbedMessageToList(topic, message.getId() + " : " + message);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d("", "Mqtt getMqttAndroidClient deliveryComplete: token: " + token);
            }
        });
        return mqttAndroidClient;
    }

    public void subscribeToTopic(String topic, MqttAndroidClient client) {
        Log.d("Eoghan", "Attempting to subscribe to topic: " + topic + " with client ID: " + client.getClientId());

        int qos = 1; // at least once delivery
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("Eoghan", "Successfully subscribed to topic: " + topic + " with client ID: " + client.getClientId());
                    addSubscriptionToList(client.getClientId(), topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("Eoghan", "Failed to subscribe to topic: " + topic + " with client ID: " + client.getClientId(), exception);
                }
            });
        } catch (MqttException e) {
            Log.e("Eoghan", "MQTT exception while subscribing to topic: " + topic + " with client ID: " + client.getClientId(), e);
        }
    }



    public void publishMessagesForClients() {
        if (mutableMqttClientMap == null || mutableMqttClientMap.getValue() == null) {
            Log.e("Eoghan", "Client map is null or uninitialized.");
            return;
        }

        for (String clientName : mutableMqttClientMap.getValue().keySet()) {
            List<Pair<Sensor, String>> sensorList = clientSensors.get(clientName);
            if (sensorList != null) {
                for (Pair<Sensor, String> sensorTopicPair : sensorList) {
                    Sensor sensor = sensorTopicPair.first;
                    String topic = sensorTopicPair.second;
                    String payload = getSensorData(sensor);
                    MqttAndroidClient client = mutableMqttClientMap.getValue().get(clientName);
                    if (client != null) {
                        Log.d("Eoghan", "Publishing message for client: " + clientName + " on topic: " + topic);
                        publishMessage(client, topic, payload, 1, false);
                    } else {
                        Log.e("Eoghan", "MQTT client for " + clientName + " is null.");
                    }
                }
            } else {
                Log.d("Eoghan", "No sensors found for client: " + clientName);
            }
        }
    }


    private void publishMessage(MqttAndroidClient client, String topic, String payload, int qos, boolean retained) {
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(payload.getBytes());
            message.setQos(qos);
            message.setRetained(retained);

            client.publish(topic, message, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MQTT", "Message published successfully to topic: " + topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("MQTT", "Failed to publish message to topic: " + topic, exception);
                }
            });
        } catch (MqttException e) {
            Log.e("MQTT", "Error publishing message to topic: " + topic, e);
        }
    }

    private String getSensorData(Sensor sensor) {
        // Return a string representation of sensor data.
//        return sensor.getData(); // Assume getData returns the current data as a String.

        return sensor.getName() + " TEST";
    }


    public void associateClientWithEndpoint(String clientName, Sensor sensor, String publishTopic) {
        // Retrieve the list of sensor-topic pairs for the client
        List<Pair<Sensor, String>> sensorList = clientSensors.get(clientName);

        // If no list exists, create one and put it in the map
        if (sensorList == null) {
            sensorList = new ArrayList<>();
            clientSensors.put(clientName, sensorList);
        }

        // Add the new sensor and topic pair to the list
        sensorList.add(new Pair<>(sensor, publishTopic));
        Log.d("MQTT", " ADDING TO MUTABLE LIST " + clientSensors);
        sensorTopics.postValue(clientSensors);
    }

    public MutableLiveData<Map<String, List<Pair<Sensor, String>>>> getSensorTopics() {
        return sensorTopics;
    }
}
