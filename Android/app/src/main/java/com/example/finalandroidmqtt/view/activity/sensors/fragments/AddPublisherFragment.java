package com.example.finalandroidmqtt.view.activity.sensors.fragments;

import android.app.Dialog;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import com.example.finalandroidmqtt.pojo.ClientHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddPublisherFragment extends DialogFragment {
    private static final String ARG_SELECTED_CLIENT_ID = "selectedClientId";
    MqttApplication application;
    private View dialogView;
    private Spinner sensorSpinner;
    private EditText subscriptionEditText;
    ArrayAdapter<String> sensorAdapter;

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

        dialogView = getLayoutInflater().inflate(R.layout.fragment_add_publisher, null);

        sensorSpinner = dialogView.findViewById(R.id.sensorSpinner);
        subscriptionEditText = dialogView.findViewById(R.id.subscriptionEditText);

        sensorAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, getSensorNames());
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
        final String[] topic = new String[1];
        topic[0] = "no topic";
        sensorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String sensorName = (String) sensorAdapter.getItem(position);
                setHintsAndTopic(topic, sensorName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(view -> {
            String selectedSensor = sensorSpinner.getSelectedItem().toString();
            String finalTopic = subscriptionEditText.getText().toString().trim();

            if (selectedSensor.isEmpty()) {
                Toast.makeText(requireActivity(), "Please select a sensor and a subscription topic.", Toast.LENGTH_SHORT).show();
                return;
            }


            linkSensorToTopic(selectedSensor, topic[0]);
            Toast.makeText(requireActivity(), "Linked " + selectedSensor + " with " + finalTopic, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

    private void setHintsAndTopic(String[] topic, String sensorName) {
        if (sensorName.contains("P") || sensorName.contains("p")) {
            topic[0] = "a00287845/device/android/sensors/proximity";
            subscriptionEditText.setHint(topic[0]);
        } else if (sensorName.contains("L") || sensorName.contains("l")) {
            topic[0] = "a00287845/device/android/sensors/light";
            subscriptionEditText.setHint(topic[0]);
        }
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
        ClientHolder clientHolder = application.getMqtt().getClientHolderFromListByName(clientId, Objects.requireNonNull(application.getMqtt().getClients().getValue()));


        return new ArrayList<>(clientHolder.getSubscriptions());
    }

    private void linkSensorToTopic(String sensorName, String topic) {
        Sensor toLink = null;
        for (Sensor sensor : application.getSensorHandler().getSensorList()) {
            if (sensor.getName().equalsIgnoreCase(sensorName)) {
                toLink = sensor;
            }
        }
        if (toLink == null) {
            return;
        }
        application.getInstance().getMqtt().associateClientWithEndpoint((String) requireArguments().get(ARG_SELECTED_CLIENT_ID), toLink, topic);
    }
}
