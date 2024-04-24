package com.example.finalandroidmqtt.view.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalandroidmqtt.MqttApplication;
import com.example.finalandroidmqtt.R;

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.util.Objects;

public class AddSubscriptionFragment extends DialogFragment {
    private MqttApplication application;
    private static final String ARG_SELECTED_CLIENT_ID = "selectedClientId";
    private String selectedClientId;
    private View dialogView;

    public static AddSubscriptionFragment newInstance(String selectedClientId) {
        AddSubscriptionFragment fragment = new AddSubscriptionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SELECTED_CLIENT_ID, selectedClientId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedClientId = getArguments().getString(ARG_SELECTED_CLIENT_ID);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        application = (MqttApplication) requireActivity().getApplication();
        dialogView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_add_sub, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        // Customize the dialog here (e.g., title, buttons)
        builder.setTitle("My Floating Window");
        builder.setPositiveButton("Add Subscription", null); // Set the listener later in onStart
        builder.setNegativeButton("Cancel", (dialog, which) -> dismiss());
        builder.setView(dialogView);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        TextView clientNameHolder = dialogView.findViewById(R.id.selectedClientName);
        clientNameHolder.setText(selectedClientId);

        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog == null) {
            return;
        }

        Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);

        positiveButton.setOnClickListener(view -> {
            Log.d("EOGHAN", "AddSubscriptionFragment onStart: Positive button clicked");
            EditText newSubTopicEditText = dialogView.findViewById(R.id.newSubTopicEt);
            String newSubTopic = newSubTopicEditText.getText().toString();

            if (selectedClientId == null) {
                Toast.makeText(requireActivity(), "Need to fill in ID and Uri", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newSubTopic.isEmpty()) {
                Toast.makeText(requireActivity(), "Need to fill in topic", Toast.LENGTH_SHORT).show();
                return;
            }

            if (application.getMqtt().getMutableMqttClientMap().getValue() == null) {
                Toast.makeText(requireActivity(), "Mutable client map is null", Toast.LENGTH_SHORT).show();
                return;
            }

            if (application.getMqtt().getMutableMqttClientMap().getValue().get(selectedClientId) == null) {
                Toast.makeText(requireActivity(), "Value is null", Toast.LENGTH_SHORT).show();

                return;
            }

            MqttAndroidClient selectedClient = application.getMqtt().getMutableMqttClientMap().getValue().get(selectedClientId);

            assert selectedClient != null;
            application.getMqtt().subscribeToTopic(newSubTopic, selectedClient);
            dialog.dismiss();
        });
    }


    // You can customize the style and dimensions of the dialog in onCreateDialog if needed
}
