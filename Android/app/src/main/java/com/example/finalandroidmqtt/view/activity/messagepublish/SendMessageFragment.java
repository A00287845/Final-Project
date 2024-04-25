package com.example.finalandroidmqtt.view.activity.messagepublish;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.finalandroidmqtt.MqttApplication;
import com.example.finalandroidmqtt.R;
import com.example.finalandroidmqtt.pojo.ClientHolder;

import java.util.Objects;

import info.mqtt.android.service.MqttAndroidClient;


public class SendMessageFragment extends Fragment {

    private final String SEND_MESSAGE_CLIENT_NAME = "android_message_client_name";
    private final String ANDROID_MESSAGE_TOPIC = "a00287845/device/android/input/text";
    boolean setup = false;
    private MqttApplication application;

    public SendMessageFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_send_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        application = (MqttApplication) requireActivity().getApplication();

        EditText topicEt, messageEt;
        topicEt = view.findViewById(R.id.sendMessageEtTopic);
        messageEt = view.findViewById(R.id.sendMessageEt);
        beginObserving();

        application.getMqtt().setupBroker(application.getApplicationContext(), SEND_MESSAGE_CLIENT_NAME, "ssl://930094acb7da4acfbf5761b3ac2c7c90.s1.eu.hivemq.cloud:8883");


        view.findViewById(R.id.sendMessageButt).setOnClickListener(v -> {
            String topicString = topicEt.getText().toString();
            String messageString = messageEt.getText().toString();
            if (messageString.isEmpty()) {
                Toast.makeText(requireActivity(), "Fill in message field", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!setup) {
                Toast.makeText(requireActivity(), "Waiting for client to initialize", Toast.LENGTH_SHORT).show();
                return;
            }
            if (topicString.isEmpty()) {
                topicString = ANDROID_MESSAGE_TOPIC;
            }
            application.getMqtt().publishMessage(topicString, messageString);
        });
    }

    private void beginObserving() {
        application.getMqtt().getClients().observe(getViewLifecycleOwner(), clients -> {
            if (clients != null) {
                ClientHolder holder = application.getMqtt().getClientHolderFromListByName(SEND_MESSAGE_CLIENT_NAME, Objects.requireNonNull(application.getMqtt().getClients().getValue()));
                if (holder == null) {
                    return;
                }
                setup = true;

            }
        });

    }
}