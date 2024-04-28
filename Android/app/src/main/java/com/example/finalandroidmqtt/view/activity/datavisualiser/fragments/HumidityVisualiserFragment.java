package com.example.finalandroidmqtt.view.activity.datavisualiser.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finalandroidmqtt.MqttApplication;
import com.example.finalandroidmqtt.R;
import com.example.finalandroidmqtt.pojo.ClientHolder;
import com.github.anastr.speedviewlib.SpeedView;

import org.json.JSONObject;

import java.util.Objects;

import info.mqtt.android.service.MqttAndroidClient;


public class HumidityVisualiserFragment extends Fragment {

    public static final String SENSEHAT_ENVIRONMENT_TOPIC = "a00287845/device/rpi/sensors/environment";
    public static final String ENVIRONMENT_CLIENT_NAME = "android_environ_client";
    private SpeedView humidityGauge;
    private MqttApplication application;


    public HumidityVisualiserFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_humidity_visualiser, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        application = (MqttApplication) requireActivity().getApplication();
        humidityGauge = view.findViewById(R.id.humidityGauge);

        setupHumidityGauge();
        beginObserving();
        setupMqtt();
    }


    private void setupHumidityGauge() {
        humidityGauge.setMaxSpeed(100);
        humidityGauge.setMinSpeed(0);
        humidityGauge.setUnit("%");
    }

    private void setupMqtt() {
        application = (MqttApplication) requireActivity().getApplication();
        application.getMqtt().setupBroker(application.getApplicationContext(), ENVIRONMENT_CLIENT_NAME, "ssl://930094acb7da4acfbf5761b3ac2c7c90.s1.eu.hivemq.cloud:8883");
    }

    private void beginObserving() {
        application.getMqtt().getClients().observe(getViewLifecycleOwner(), clients -> {
            if (clients != null) {
                ClientHolder holder = application.getMqtt().getClientHolderFromListByName(ENVIRONMENT_CLIENT_NAME, Objects.requireNonNull(application.getMqtt().getClients().getValue()));
                if(holder == null){
                    return;
                }
                Log.d("HELP", "" + holder.getSubscriptions());
                if (holder.getSubscriptions().contains(SENSEHAT_ENVIRONMENT_TOPIC)) {
                    return;
                }
                MqttAndroidClient gyroscopeMqttClient = application.getMqtt().getClientHolderFromListByName(ENVIRONMENT_CLIENT_NAME, Objects.requireNonNull(application.getMqtt().getClients().getValue())).getClient();
                application.getMqtt().subscribeToTopic(SENSEHAT_ENVIRONMENT_TOPIC, gyroscopeMqttClient);
            }
        });

        application.getMqtt().getSubbedMessagesList().observe(getViewLifecycleOwner(), messages -> {
            if (messages != null) {
                if (messages.get(messages.size() - 1).first.equals(SENSEHAT_ENVIRONMENT_TOPIC)) {
                    updateHumidityGauge(messages.get(messages.size() - 1).second);
                }
            } else {
                Log.d("Eoghan", "Environment messages is null");
            }
        });
    }

    private void updateHumidityGauge(String input) {
        String jsonPart = input.substring(input.indexOf('{'));

        try {
            JSONObject jsonObject = new JSONObject(jsonPart);
            double humidity = jsonObject.getDouble("humidity");
            humidityGauge.speedTo((float) humidity, 50);

        } catch (Exception e) {
            Log.e("JSON Parse Error", "Error parsing input string for humidity", e);
        }

    }
}