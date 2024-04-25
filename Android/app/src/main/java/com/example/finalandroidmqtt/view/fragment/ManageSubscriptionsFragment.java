package com.example.finalandroidmqtt.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.finalandroidmqtt.MqttApplication;
import com.example.finalandroidmqtt.R;
import com.example.finalandroidmqtt.pojo.ClientHolder;

import info.mqtt.android.service.MqttAndroidClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ManageSubscriptionsFragment extends Fragment {

    private Spinner dropDownItemsSpinner;
    private Button addSubscriptionButton, addSensorButton;
    private ArrayAdapter<String> dropDownAdapter;
    private MqttApplication application;
    private String currentSelectedClient;

    public ManageSubscriptionsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_subscriptions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        application = (MqttApplication) requireContext().getApplicationContext();
        setUpObservation();
        handleDropdown();
        addSubscriptionButton = requireActivity().findViewById(R.id.addSubscriptionOpenFragmentButton);
        addSensorButton = requireActivity().findViewById(R.id.addTopicOpenFragmentButton);

        addSubscriptionButton.setOnClickListener(v -> {
            AddSubscriptionFragment dialogFragment = AddSubscriptionFragment.newInstance(currentSelectedClient);
            dialogFragment.show(getChildFragmentManager(), "Add subscription");
        });

        addSensorButton.setOnClickListener(v -> {
            AddPublisherFragment dialogFragment = AddPublisherFragment.newInstance(currentSelectedClient);
            dialogFragment.show(getChildFragmentManager(), "addSensorTopic");
        });
    }


    private void setUpObservation() {
        application.getMqtt().getClients().observe(getViewLifecycleOwner(), clients -> {
            if (clients != null) {
                updateUi(clients);
            } else {
                Log.d("Eoghan", "ManageClientsFragment Clients is null");
            }
        });

    }

    private void updateUi(List<ClientHolder> clientHolderList) {

        if (addSubscriptionButton == null) {
            return;
        }

        if (!clientHolderList.isEmpty()) {
            addSubscriptionButton.setVisibility(View.VISIBLE);
            addSensorButton.setVisibility(View.VISIBLE);
            dropDownItemsSpinner.setVisibility(View.VISIBLE);
            handleDropdown();
        } else {
            addSubscriptionButton.setVisibility(View.GONE);
            dropDownItemsSpinner.setVisibility(View.GONE);
        }
    }

    private void handleDropdown() {
        Log.d("EOGHAN", "ManageSubscriptionsFragment handleDropdown:");

        List<String> dropdownItems = new ArrayList<>();

        if (dropDownItemsSpinner == null) { // initialize case
            Log.d("EOGHAN", "ManageSubscriptionsFragment handleDropdown: Initialize case");
            dropDownItemsSpinner = requireActivity().findViewById(R.id.clientDropDownSpinner);

            dropDownAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, dropdownItems);

            dropDownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dropDownItemsSpinner.setAdapter(dropDownAdapter);

            final boolean[] setup = {false};
            dropDownItemsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    currentSelectedClient = dropdownItems.get(position);
                    if (setup[0]) {
                        Toast.makeText(requireActivity(), dropdownItems.get(position), Toast.LENGTH_SHORT).show();
                    }
                    setup[0] = true;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Optionally implement this method if you want to react to nothing being selected
                }
            });

        } else { //refresh case

            // Retrieving the list of clients
            List<ClientHolder> clientList = application.getMqtt().getClients().getValue();

            // Check if the client list is null or empty and handle accordingly
            if (clientList == null) {
                Log.d("EOGHAN", "ManageSubscriptionsFragment handleDropdown: clientList is null");
                return;
            }
            if (clientList.isEmpty()) {
                Log.d("EOGHAN", "ManageSubscriptionsFragment handleDropdown: clientList is empty");
                return;
            }

            // Populate the dropdown with names from the client holders
            for (ClientHolder holder : clientList) {
                if (holder != null) {
                    String name = holder.getName(); // Using getName() to get the name for the dropdown
                    if (name != null && !name.isEmpty()) {
                        dropdownItems.add(name);
                    } else {
                        Log.d("EOGHAN", "ManageSubscriptions handleDropdown: found empty or null name");
                    }
                } else {
                    Log.d("EOGHAN", "ManageSubscriptionsFragment handleDropdown: null holder encountered");
                }
            }

            Log.d("EOGHAN", "ManageSubscriptionsFragment handleDropdown: refresh case");

// Update the dropdown adapter
            dropDownAdapter.clear();
            dropDownAdapter.addAll(dropdownItems);

        }


    }
}