package com.example.finalandroidmqtt.util;

import android.content.Context;
import android.hardware.Sensor;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.finalandroidmqtt.MqttApplication;
import com.example.finalandroidmqtt.pojo.ClientHolder;

import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;

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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Mqtt {
    private static volatile Mqtt instance;
    private final MutableLiveData<List<ClientHolder>> clients = new MutableLiveData<>();
    private final MutableLiveData<List<Pair<String, String>>> mutableSubscribedMessagesList = new MutableLiveData<>();

    private final MutableLiveData<Boolean> sensorsActive = new MutableLiveData<>();

    private MqttApplication.SensorHandler sensorHandler;

    private Mqtt() {
    }

    private Mqtt(MqttApplication.SensorHandler sensorHandler) {
        this.sensorHandler = sensorHandler;
    }

    public static Mqtt getInstance(MqttApplication.SensorHandler sensorHandler) {
        if (instance == null) {
            synchronized (Mqtt.class) {
                if (instance == null) {
                    instance = new Mqtt(sensorHandler);
                }
            }
        }
        return instance;
    }

    public MutableLiveData<List<ClientHolder>> getClients() {
        checkActive();
        Log.d("MQTT", "getClients returning " + clients.getValue());
        return clients;
    }

    public void addClient(MqttAndroidClient client) {
        List<ClientHolder> clientList = clients.getValue();
        if (clientList == null) {
            clientList = new ArrayList<>();
        }
        clientList.add(new ClientHolder(client.getClientId(), client));
        clients.postValue(clientList);
    }

    public void removeClientByNameFromList(String clientName) {
        Log.d("EOGHAN", "Mqtt removeClientByNameFromMap: client name " + clientName);
        // Early retrieval with null checks
        List<ClientHolder> clientList = clients.getValue();
        if (clientList == null) {
            Log.d("EOGHAN", "Mqtt removeClientByNameFromMap a list is null");
            return;
        }

        ClientHolder foundClient = getClientHolderFromListByName(clientName, clientList);


        try {
            foundClient.getClient().disconnect();
        } catch (Exception me) {
            Log.e("removeClientByName", "Error disconnecting client: " + clientName, me);

        }
        clientList.remove(foundClient);


        clients.postValue(clientList);
    }

    public ClientHolder getClientHolderFromListByName(String clientName, List<ClientHolder> clientList) {
        ClientHolder foundClient = null;
        for (ClientHolder clientHolder : clientList) {
            if (clientHolder.getName().equalsIgnoreCase(clientName)) {
                foundClient = clientHolder;
            }
        }
        if (foundClient == null) {
            Log.d("EOGHAN", "Mqtt getClientFromListByName client not found");
            return null;
        }
        return foundClient;
    }


    public void addSubscriptionToList(String clientName, String subscription) {
        Log.d("Eoghan", "MqttApplication addSubscriptionToList called with client: " + clientName + ", subscription: " + subscription);
        List<ClientHolder> clientList = clients.getValue();
        if (clientList == null) {
            clientList = new ArrayList<>();
        }

        ClientHolder foundClient = getClientHolderFromListByName(clientName, clientList);

        Set<String> clientSubs = foundClient.getSubscriptions();

        if (clientSubs == null) {
            clientSubs = new HashSet<>();
        }
        clientSubs.add(subscription);
        Log.d("MQTT", " posting client value after subscribing foundClient : " + foundClient);
        clients.postValue(clientList);
    }

    public void addSubbedMessageToList(String topic, String message) {
        List<Pair<String, String>> messageList = mutableSubscribedMessagesList.getValue();
        if (messageList == null) {
            messageList = new ArrayList<>();
        }
        Log.d("EOGHAN", "Mqtt addSubbedMssageToList: Topic: " + topic + " Message: " + message);
        if (messageList.size() > 200) {
            messageList = new ArrayList<>();
        }
        messageList.add(new Pair<>(topic, message));
        mutableSubscribedMessagesList.postValue(messageList);
    }


    public void setupBroker(Context appContext, String clientId, String brokerUri) {
        Log.d("Eoghan", "Mqtt setupBroker");
        List<ClientHolder> clientHolderList = clients.getValue();
        if (clientHolderList!=null) {
            for (ClientHolder holder : clientHolderList) {
                if (holder.getName().equals(clientId)){
                    return;
                }
            }
        }

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
                    addClient(mqttAndroidClient);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("Eoghan", "Mqtt setupBroker onFailure: ", exception);
                }
            });
        } catch (Exception e) {
            Log.e("Eoghan", "Mqtt setupBroker mqtt exception: ", e);
        }

    }

    @NonNull
    private MqttAndroidClient getMqttAndroidClient(Context appContext, String clientId, String serverUri) {
        MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(appContext, serverUri, clientId, Ack.AUTO_ACK);

        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.e("EOGHAN", "Mqtt getMqttAndroidClient connectionLost: client: " + mqttAndroidClient.getClientId(), cause);
                removeClientByNameFromList(mqttAndroidClient.getClientId());
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

        int qos = 1;
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
        } catch (Exception e) {
            Log.e("Eoghan", "MQTT exception while subscribing to topic: " + topic + " with client ID: " + client.getClientId(), e);
        }
    }


    public void publishMessagesForClients() {
        if (clients.getValue() == null) {
            Log.e("Eoghan", "Client map is null or uninitialized.");
            return;
        }
        for (int i = 0; i < clients.getValue().size(); i++) {
            String clientName = clients.getValue().get(i).getName();
            List<Pair<Sensor, String>> sensorTopics = clients.getValue().get(i).getSensorTopics();

            if (sensorTopics != null) {
                for (Pair<Sensor, String> sensorTopicPair : sensorTopics) {
                    Sensor sensor = sensorTopicPair.first;
                    String topic = sensorTopicPair.second;
                    String payload = getSensorData(sensor);
                    MqttAndroidClient client = getClientHolderFromListByName(clientName, clients.getValue()).getClient();
                    if (client != null) {
                        Log.d("Eoghan", "Publishing message: \"" + payload + "\" for client: " + clientName + " on topic: " + topic);
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

    public void publishMessage(String topic, String payload) {
        if (clients.getValue() == null) {
            return;
        }
        if (clients.getValue().isEmpty()) {
            return;
        }
        publishMessage(clients.getValue().get(0).getClient(), topic, payload, 2, false);
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
        } catch (Exception e) {
            Log.e("MQTT", "Error publishing message to topic: " + topic, e);
        }
    }

    private String getSensorData(Sensor sensor) {
        if (sensor == null) {
            return "Sensor not available";
        }

        // Check which type of sensor is passed and fetch the appropriate last known value
        if (sensor.getType() == Sensor.TYPE_PROXIMITY) {
            return sensor.getName() + ": " + sensorHandler.getLastProximityValue();
        } else if (sensor.getType() == Sensor.TYPE_LIGHT) {
            return sensor.getName() + ": " + sensorHandler.getLastLightValue();
        } else {
            return sensor.getName() + ": Sensor type not supported";
        }
    }


    public void associateClientWithEndpoint(String clientName, Sensor sensor, String publishTopic) {
        Log.d("MQTT", "associateClientWithEndpoint clientName: " + clientName + " sensor: " + sensor + " publishTopic: ");
        List<ClientHolder> clientList = clients.getValue();
        ClientHolder thisClient = getClientHolderFromListByName(clientName, Objects.requireNonNull(clientList));
        List<Pair<Sensor, String>> sensorList = thisClient.getSensorTopics();
        sensorList.add(new Pair<>(sensor, publishTopic));
        clients.postValue(clientList);
    }

    public MutableLiveData<Boolean> getSensorActive() {
        return sensorsActive;
    }

    public void checkActive() {
        if (clients.getValue() == null) {
            return;
        }
        boolean active = false;
        for (ClientHolder clientHolder : clients.getValue()) {
            if (!clientHolder.getSensorTopics().isEmpty()) {
                active = true;
                break;
            }
        }
        if (active == Boolean.TRUE.equals(sensorsActive.getValue())) {
            return;
        }
        sensorsActive.postValue(active);
    }

    public MutableLiveData<List<Pair<String, String>>> getSubbedMessagesList() {
        return mutableSubscribedMessagesList;
    }
}
