package com.example.finalandroidmqtt;

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

import com.example.finalandroidmqtt.util.ClientAdapter;

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.util.Set;


public class ManageClientsFragment extends Fragment {
    private ClientAdapter adapter;
    private RecyclerView clientRecyclerView;


    public ManageClientsFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
    }

    private void setUpObservation(MqttApplication application) {
        Log.d("Eoghan", "ManageClientsFragment setUpObservation: Start");
        application.getMutableMqttClientList().observe(getViewLifecycleOwner(), clients -> {
            Log.d("Eoghan", "ManageClientsFragment MutableMqttClientList observed");
            if (clients != null) {
                Log.d("Eoghan", "ManageClientsFragment Clients not null, count: " + clients.size());

                String clientString = "";
                for(MqttAndroidClient client: clients){
                    clientString += client.getClientId();
                }

                Log.d("Eoghan", "ManageClientsFragment Clients not null, count: " + clients.size());
                Log.d("Eoghan", "ManageClientsFragment Clients not null, clientString: " + clientString);

                Log.d("Eoghan", "ManageClientsFragment updateUI: Called");
                updateUI(clients);
            } else {
                Log.d("Eoghan", "ManageClientsFragment Clients is null");
            }
        });
    }

    private void updateUI(Set<MqttAndroidClient> clients) {
        Log.d("Eoghan", "ManageClientsFragment updateUI: Start");
        if (adapter != null) {
            Log.d("Eoghan", "ManageClientsFragment Adapter not null, updating data with clients: " + clients);
            adapter.updateData(clients);
            Log.d("Eoghan", "ManageClientsFragment Adapter data updated");
        } else {
            Log.d("Eoghan", "ManageClientsFragment Adapter is null");
        }
    }

    private void setUpRecyclerView(View view, MqttApplication application){
        Log.d("Eoghan", "ManageClientsFragment setUpRecyclerView: Start");
        clientRecyclerView = view.findViewById(R.id.clientsRecyclerView);
        Log.d("Eoghan", "ManageClientsFragment RecyclerView found");

        clientRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        Log.d("Eoghan", "ManageClientsFragment LayoutManager set");

        adapter = new ClientAdapter(application.getMutableMqttClientList().getValue());
        Log.d("Eoghan", "ManageClientsFragment Adapter initialized");

        clientRecyclerView.setAdapter(adapter);
        Log.d("Eoghan", "ManageClientsFragment Adapter set to RecyclerView");
    }
}