package com.example.finalandroidmqtt.view.activity.datavisualiser.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.finalandroidmqtt.MqttApplication;
import com.example.finalandroidmqtt.R;
import com.example.finalandroidmqtt.pojo.ClientHolder;

import info.mqtt.android.service.MqttAndroidClient;
import org.json.JSONObject;

import java.util.Objects;


public class GyroscopeVisualiserFragment extends Fragment {

    private final String GYROSCOPE_CLIENT_NAME = "android_gyro_client";
    private final String GYROSCOPE_TOPIC = "a00287845/device/rpi/sensors/gyroscope";
    MqttApplication application;
    private TextView pitchTv, rollTv, yawTv;
    public GyroscopeVisualiserFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gyroscope_visualiser, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pitchTv = view.findViewById(R.id.gyroPitchTv);
        rollTv = view.findViewById(R.id.gyroRollTv);
        yawTv = view.findViewById(R.id.gyroYawTv);

        setupMqtt();
        beginObserving();
    }

    private void setupMqtt() {
        application = (MqttApplication) requireActivity().getApplication();
        application.getMqtt().setupBroker(application.getApplicationContext(), GYROSCOPE_CLIENT_NAME, "ssl://930094acb7da4acfbf5761b3ac2c7c90.s1.eu.hivemq.cloud:8883");
    }

    private void beginObserving() {
        application.getMqtt().getClients().observe(getViewLifecycleOwner(), clients -> {
            if(clients != null){
                ClientHolder holder =  application.getMqtt().getClientHolderFromListByName(GYROSCOPE_CLIENT_NAME, Objects.requireNonNull(application.getMqtt().getClients().getValue()));
                if(holder == null){
                    return;
                }
                if(holder.getSubscriptions().contains(GYROSCOPE_TOPIC)){
                    return;
                }
                MqttAndroidClient gyroscopeMqttClient = application.getMqtt().getClientHolderFromListByName(GYROSCOPE_CLIENT_NAME, Objects.requireNonNull(application.getMqtt().getClients().getValue())).getClient();
                application.getMqtt().subscribeToTopic(GYROSCOPE_TOPIC, gyroscopeMqttClient);
            }
        });

        application.getMqtt().getSubbedMessagesList().observe(getViewLifecycleOwner(), messages -> {
            if (messages != null) {
                if(messages.get(messages.size()-1).first.equals(GYROSCOPE_TOPIC)){
                    displayResponse(messages.get(messages.size()-1).second);
                }
            } else {
                Log.d("Eoghan", "Gyroscope messages is null");
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void displayResponse(String input){

        Log.d("help", input);
        String jsonPart = input.substring(input.indexOf('{'));

        try {
            JSONObject jsonObject = new JSONObject(jsonPart);

            double roll = jsonObject.getDouble("roll");
            double pitch = jsonObject.getDouble("pitch");
            double yaw = jsonObject.getDouble("yaw");

            rollTv.setText(Double.toString(roll));
            pitchTv.setText(Double.toString(pitch));
            yawTv.setText(Double.toString(yaw));
        } catch (Exception e) {
            Log.e("JSON Parse Error", "Error parsing input string: " + jsonPart, e);
        }
    }
}