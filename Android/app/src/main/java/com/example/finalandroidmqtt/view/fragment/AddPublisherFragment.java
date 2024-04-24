package com.example.finalandroidmqtt.view.fragment;

import android.app.Dialog;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.finalandroidmqtt.MqttApplication;
import com.example.finalandroidmqtt.R;

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class AddPublisherFragment extends DialogFragment {
    private static final String ARG_SELECTED_CLIENT_ID = "selectedClientId";
    MqttApplication application;
    private View dialogView;
    private Spinner sensorSpinner;
    private EditText subscriptionEditText;

    public static AddPublisherFragment newInstance(String clientId) {
        AddPublisherFragment fragment = new AddPublisherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SELECTED_CLIENT_ID, clientId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        application = (MqttApplication) requireActivity().getApplication();

        dialogView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_add_publisher, null);

        sensorSpinner = dialogView.findViewById(R.id.sensorSpinner);
        subscriptionEditText = dialogView.findViewById(R.id.subscriptionEditText);

        // Load sensor names
        ArrayAdapter<String> sensorAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, getSensorNames());
        sensorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sensorSpinner.setAdapter(sensorAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Link Sensor to Subscription");
        builder.setPositiveButton("Link", null); // Set the listener later in onStart
        builder.setNegativeButton("Cancel", (dialog, which) -> dismiss());
        builder.setView(dialogView);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog == null) {
            return;
        }

        Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(view -> {
            String selectedSensor = sensorSpinner.getSelectedItem().toString();
            String topic = subscriptionEditText.getText().toString().trim();

            if (selectedSensor.isEmpty() || topic.isEmpty()) {
                Toast.makeText(requireActivity(), "Please enter both a sensor and a subscription topic.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Assuming you have a method to handle linking the sensor to the topic
            linkSensorToTopic(selectedSensor, topic);
            Toast.makeText(requireActivity(), "Linked " + selectedSensor + " with " + topic, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

    private List<String> getSensorNames() {
        List<String> names = new ArrayList<>();
        for (Sensor sensor : application.getSensorHandler().getSensorList()) {
            names.add(sensor.getName());
        }
        return names;
    }

    private List<String> getSubscriptionsForClient(String clientId) {
        List<String> subscriptions = new ArrayList<>();
        Map<String, Set<String>> subscriptionMap = application.getMqtt().getMutableSubscriptionMap().getValue();
        if (subscriptionMap != null && subscriptionMap.containsKey(clientId)) {
            subscriptions.addAll(Objects.requireNonNull(subscriptionMap.get(clientId)));
        }
        return subscriptions;
    }

    private void linkSensorToTopic(String sensorName, String topic) {
        Sensor toLink = null;
        for(Sensor sensor: application.getSensorHandler().getSensorList()){
            if (sensor.getName().equalsIgnoreCase(sensorName)){
                toLink = sensor;
            }
        }
        if(toLink == null){
            return;
        }
        application.getInstance().getMqtt().associateClientWithEndpoint((String) requireArguments().get(ARG_SELECTED_CLIENT_ID), toLink, topic);
    }
}
