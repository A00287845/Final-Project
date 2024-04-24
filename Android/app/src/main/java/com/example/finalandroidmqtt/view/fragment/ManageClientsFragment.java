package com.example.finalandroidmqtt.view.fragment;

import android.hardware.Sensor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finalandroidmqtt.MqttApplication;
import com.example.finalandroidmqtt.R;

import org.eclipse.paho.android.service.MqttAndroidClient;

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

        MqttApplication application = (MqttApplication) requireContext().getApplicationContext();

        setUpRecyclerView(view, application);

        setUpObservation(application);


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
                updateUI(clients, application, application.getMqtt().getSensorTopics().getValue());
            } else {
                Log.d("Eoghan", "ManageClientsFragment Clients is null");
            }
        });

        application.getMqtt().getMutableSubscriptionMap().observe(getViewLifecycleOwner(), subscriptions -> {
            if (subscriptions != null) {
                updateUI(application.getMqtt().getMutableMqttClientMap().getValue(), application, application.getMqtt().getSensorTopics().getValue());
            }
        });

        application.getMqtt().getSensorTopics().observe(getViewLifecycleOwner(), topics -> {
            if (topics != null) {
                updateUI(application.getMqtt().getMutableMqttClientMap().getValue(), application, topics);
            }
        });
    }

    private void updateUI(Map<String, MqttAndroidClient> clients, MqttApplication application, Map<String, List<Pair<Sensor, String>>> clientSensors) {

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

            adapter.updateData(clients, clientSensors);
            Log.d("Eoghan", "ManageClientsFragment Adapter data updated");
        } else {
            Log.d("Eoghan", "ManageClientsFragment Adapter is null");
        }
    }

    private void setUpRecyclerView(View view, MqttApplication application) {
        clientListViewRecyclerView = view.findViewById(R.id.clientsRecyclerView);
        clientListViewRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        adapter = new com.example.finalandroidmqtt.util.ClientAdapter(application.getMqtt().getMutableMqttClientMap().getValue(), application);
        clientListViewRecyclerView.setAdapter(adapter);
    }
}