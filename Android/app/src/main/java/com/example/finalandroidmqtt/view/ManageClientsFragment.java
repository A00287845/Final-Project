package com.example.finalandroidmqtt.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.finalandroidmqtt.R;

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ManageClientsFragment extends Fragment {
    private com.example.finalandroidmqtt.util.ClientAdapter adapter;

    private RecyclerView clientListViewRecyclerView;

    public ManageClientsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_clients, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d("Eoghan", "ManageClientsFragment onViewCreated: Start");
        super.onViewCreated(view, savedInstanceState);
        Log.d("Eoghan", "ManageClientsFragment super.onViewCreated called");

        MqttApplication application = (MqttApplication) requireContext().getApplicationContext();
        Log.d("Eoghan", "ManageClientsFragment MqttApplication obtained");

        setUpRecyclerView(view, application);
        Log.d("Eoghan", "ManageClientsFragment setUpRecyclerView: Completed");

        setUpObservation(application);
        Log.d("Eoghan", "ManageClientsFragment setUpObservation: Completed");


        requireActivity().findViewById(R.id.addClientOpenFragmentButton).setOnClickListener(v -> {
            AddClientFragment dialogFragment = new AddClientFragment();
            dialogFragment.show(getChildFragmentManager(), "Add clieent");

        });
    }

    private void setUpObservation(MqttApplication application) {
        Log.d("Eoghan", "ManageClientsFragment setUpObservation: Start");
        application.getMqtt().getMutableMqttClientMap().observe(getViewLifecycleOwner(), clients -> {
            Log.d("Eoghan", "ManageClientsFragment MutableMqttClientList observed");
            if (clients != null) {
                Log.d("Eoghan", "ManageClientsFragment Clients not null, count: " + clients.size());

                String clientString = "";
                for (MqttAndroidClient client : clients.values()) {
                    clientString += client.getClientId();
                }

                Log.d("Eoghan", "ManageClientsFragment Clients not null, count: " + clients.size());
                Log.d("Eoghan", "ManageClientsFragment Clients not null, clientString: " + clientString);

                Log.d("Eoghan", "ManageClientsFragment updateUI: Called");
                updateUI(clients, application);
            } else {
                Log.d("Eoghan", "ManageClientsFragment Clients is null");
            }
        });

        application.getMqtt().getMutableSubscriptionMap().observe(getViewLifecycleOwner(), subscriptions -> {
            if (subscriptions != null) {
                updateUI(application.getMqtt().getMutableMqttClientMap().getValue(), application);
            }
        });
    }

    private void addSubscription() {
//        Log.d("EOGHAN", "**********************************************************");
//
//        Log.d("Eoghan", "ManageClientsFragment addSubscription");
//
//        MqttApplication application = (MqttApplication) requireContext().getApplicationContext();
//        Map<String, MqttAndroidClient> clients = application.getMutableMqttClientList().getValue();
//        if (clients != null) {
//            MqttAndroidClient client = clients.get("Client_1");
//            if (client != null) {
//                application.getMqtt().subscribeToTopic("test/topic", client);
//            } else {
//                Log.d("Eoghan", "ManageClientsFragment addSubscription: client is null");
//            }
//        } else {
//            Log.d("Eoghan", "ManageClientsFragment addSubscription: clients is null");
//        }
    }

    private void updateUI(Map<String, MqttAndroidClient> clients, MqttApplication application) {


        if (application.getMqtt().getMutableMqttClientMap().getValue() == null) {
            Log.d("EOGHAN", "ManageClientsFragment updateUi: list is null");


            return;
        }

        if (clientListViewRecyclerView == null) {
            Log.d("EOGHAN", "ManageClientsFragment updateUi: some component is null, recycler view: " + clientListViewRecyclerView);
            return;
        }

        if (!application.getMqtt().getMutableMqttClientMap().getValue().isEmpty()) {
            clientListViewRecyclerView.setVisibility(View.VISIBLE);
        } else {
            clientListViewRecyclerView.setVisibility(View.GONE);
        }

        Log.d("Eoghan", "ManageClientsFragment updateUI: Start");
        if (adapter != null) {
            Log.d("Eoghan", "ManageClientsFragment Adapter not null, updating data with clients: " + clients);
            adapter.updateData(clients);
            Log.d("Eoghan", "ManageClientsFragment Adapter data updated");
        } else {
            Log.d("Eoghan", "ManageClientsFragment Adapter is null");
        }
    }

    private void setUpRecyclerView(View view, MqttApplication application) {
        Log.d("Eoghan", "ManageClientsFragment setUpRecyclerView: Start");
        clientListViewRecyclerView = view.findViewById(R.id.clientsRecyclerView);
        Log.d("Eoghan", "ManageClientsFragment RecyclerView found");

        clientListViewRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        Log.d("Eoghan", "ManageClientsFragment LayoutManager set");

        adapter = new com.example.finalandroidmqtt.util.ClientAdapter(application.getMqtt().getMutableMqttClientMap().getValue(), application);
        Log.d("Eoghan", "ManageClientsFragment Adapter initialized");

        clientListViewRecyclerView.setAdapter(adapter);
        Log.d("Eoghan", "ManageClientsFragment Adapter set to RecyclerView");
    }

}