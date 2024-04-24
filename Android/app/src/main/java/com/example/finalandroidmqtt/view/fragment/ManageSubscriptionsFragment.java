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

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ManageSubscriptionsFragment extends Fragment {

    private Spinner dropDownItemsSpinner;
    private Button addSubscriptionButton, addSensorButton;
    private ArrayAdapter<String> dropDownAdapter;

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
        MqttApplication application = (MqttApplication) requireContext().getApplicationContext();
        setUpObservation(application);
        handleDropdown(application);
        addSubscriptionButton = requireActivity().findViewById(R.id.addSubscriptionOpenFragmentButton);
        addSensorButton = requireActivity().findViewById(R.id.addTopicOpenFragmentButton);

        addSubscriptionButton.setOnClickListener(v -> {
            AddSubscriptionFragment dialogFragment = AddSubscriptionFragment.newInstance(currentSelectedClient);
            dialogFragment.show(getChildFragmentManager(), "Add subscription");
        });

        addSensorButton.setOnClickListener( v-> {
            AddPublisherFragment dialogFragment = AddPublisherFragment.newInstance(currentSelectedClient);
            dialogFragment.show(getChildFragmentManager(), "addSensorTopic");
        });
    }



    private void setUpObservation(MqttApplication application) {
        application.getMqtt().getMutableMqttClientMap().observe(getViewLifecycleOwner(), clients -> {
            if (clients != null) {
                updateUi(application);
            } else {
                Log.d("Eoghan", "ManageClientsFragment Clients is null");
            }
        });

        application.getMqtt().getMutableSubscriptionMap().observe(getViewLifecycleOwner(), subscriptions -> {
            if (subscriptions != null) {
                updateUi(application);
            }
        });
    }

    private void updateUi(MqttApplication application){

        if (addSubscriptionButton == null) {
            return;
        }

        if (!application.getMqtt().getMutableMqttClientMap().getValue().isEmpty()) {
            addSubscriptionButton.setVisibility(View.VISIBLE);
            addSensorButton.setVisibility(View.VISIBLE);
            dropDownItemsSpinner.setVisibility(View.VISIBLE);
            handleDropdown(application);
        } else {
            addSubscriptionButton.setVisibility(View.GONE);
            dropDownItemsSpinner.setVisibility(View.GONE);
        }
    }

    private void handleDropdown(MqttApplication application) {
        Log.d("EOGHAN", "ManageClientsFragment handleDropdown:");

        List<String> dropdownItems = new ArrayList<>();


        if (dropDownItemsSpinner == null) { // initialize case
            Log.d("EOGHAN", "ManageClientsFragment handleDropdown: Initialize case");
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

            Map<String, MqttAndroidClient> clientMap = application.getMqtt().getMutableMqttClientMap().getValue();
            if (clientMap == null) {
                Log.d("EOGHAN", "ManageClientsFragment handleDropdown: clientMap is null");
                return;
            }

            if (clientMap.isEmpty()) {
                Log.d("EOGHAN", "ManageClientsFragment handleDropdown: clientMap is empty");

                return;
            }


            for (MqttAndroidClient client : clientMap.values()) {
                dropdownItems.add(client.getClientId());
            }

            Log.d("EOGHAN", "ManageClientsFragment handleDropdown: refresh case");

            dropDownAdapter.clear();
            dropDownAdapter.addAll(dropdownItems);
        }


    }
}