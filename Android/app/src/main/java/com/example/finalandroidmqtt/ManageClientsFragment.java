package com.example.finalandroidmqtt;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finalandroidmqtt.util.ClientAdapter;

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.util.Set;


public class ManageClientsFragment extends Fragment {
    private ClientAdapter adapter;
    private RecyclerView clientRecyclerView;


    public ManageClientsFragment() {
        // Required empty public constructor
    }


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
        super.onViewCreated(view, savedInstanceState);
        MqttApplication application = (MqttApplication) requireContext().getApplicationContext();
        setUpRecyclerView(view, application);
        setUpObservation(application);
    }

    private void setUpObservation(MqttApplication application) {
        application.getMutableMqttClientList().observe(getViewLifecycleOwner(), clients -> {
            if (clients != null) {
                updateUI(clients);
            }
        });
    }

    private void updateUI(Set<MqttAndroidClient> clients) {
        if (adapter != null) {
            adapter.updateData(clients);
        }
    }

    private void setUpRecyclerView(View view, MqttApplication application){
        clientRecyclerView = view.findViewById(R.id.clientsRecyclerView);
        clientRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        adapter = new ClientAdapter(application.getMutableMqttClientList().getValue());
        clientRecyclerView.setAdapter(adapter);
    }
}