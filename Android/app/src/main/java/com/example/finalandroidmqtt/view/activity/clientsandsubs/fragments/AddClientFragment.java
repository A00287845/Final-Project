package com.example.finalandroidmqtt.view.activity.clientsandsubs.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.finalandroidmqtt.MqttApplication;
import com.example.finalandroidmqtt.R;

import java.util.Objects;

public class AddClientFragment extends DialogFragment {
    private MqttApplication application;
    private View dialogView;

    public AddClientFragment(){}



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        application = (MqttApplication) requireActivity().getApplication();

        dialogView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_add_client, null);

        builder.setTitle("Add a new client");
        builder.setView(dialogView); // Set the view using the reference

        builder.setPositiveButton("OK", null); // Set the listener later in onStart
        builder.setNegativeButton("Cancel", (dialog, which) -> dismiss());

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(view -> {
                EditText clientIdEditText = dialogView.findViewById(R.id.clientIdEntryEt);
                EditText clientUriEditText = dialogView.findViewById(R.id.newSubTopicEt);

                String clientId = clientIdEditText.getText().toString();
                String clientBrokerUri = clientUriEditText.getText().toString();
                if (!clientId.isEmpty()) {
                    // Your validation passed, handle the data
                    if(clientBrokerUri.isEmpty()){
//                        application.getMqtt().setupBroker(application.getApplicationContext(), clientId, "tcp://broker.hivemq.com:1883");
                        application.getMqtt().setupBroker(application.getApplicationContext(), clientId, "ssl://930094acb7da4acfbf5761b3ac2c7c90.s1.eu.hivemq.cloud:8883");

                    }else {
                        application.getMqtt().setupBroker(application.getApplicationContext(), clientId, clientBrokerUri);
                    }
                    dialog.dismiss();
                } else {
                    Toast.makeText(requireActivity(), "Need to fill in ID and Uri", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
