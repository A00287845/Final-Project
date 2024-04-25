package com.example.finalandroidmqtt.view.activity.clientsandsubs.fragments;

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

import com.example.finalandroidmqtt.MqttApplication;
import com.example.finalandroidmqtt.R;
import com.example.finalandroidmqtt.pojo.ClientHolder;
import com.example.finalandroidmqtt.view.activity.clientsandsubs.fragments.AddClientFragment;

import java.util.List;


public class ManageClientsFragment extends Fragment {
    private com.example.finalandroidmqtt.util.ClientAdapter adapter;

    private RecyclerView clientListViewRecyclerView;
    private MqttApplication application;

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

        application = (MqttApplication) requireContext().getApplicationContext();

        setUpRecyclerView(view);

        setUpObservation();

        requireActivity().findViewById(R.id.addClientOpenFragmentButton).setOnClickListener(v -> {
            AddClientFragment dialogFragment = new AddClientFragment();
            dialogFragment.show(getChildFragmentManager(), "Add clieent");
        });
    }

    private void setUpObservation() {
        Log.d("Eoghan", "ManageClientsFragment setUpObservation: Start");
        application.getMqtt().getClients().observe(getViewLifecycleOwner(), clients -> {
            Log.d("Eoghan", "ManageClientsFragment MutableMqttClientList observed");
            if (clients != null) {
                updateUI(clients);
            } else {
                Log.d("Eoghan", "ManageClientsFragment Clients is null");
            }
        });
    }

    private void updateUI(List<ClientHolder> clientList) {

        if (application.getMqtt().getClients().getValue() == null) {
            Log.d("EOGHAN", "ManageClientsFragment updateUi: list is null");
            return;
        }

        if (clientListViewRecyclerView == null) {
            Log.d("EOGHAN", "ManageClientsFragment updateUi: some component is null, recycler view: " + clientListViewRecyclerView);
            return;
        }

        if (!application.getMqtt().getClients().getValue().isEmpty()) {
            clientListViewRecyclerView.setVisibility(View.VISIBLE);
        } else {
            clientListViewRecyclerView.setVisibility(View.GONE);
        }

        Log.d("Eoghan", "ManageClientsFragment updateUI: Start");
        if (adapter != null) {
            Log.d("Eoghan", "ManageClientsFragment Adapter not null, updating data with clients: " + clientList);

            adapter.updateData(clientList);
            Log.d("Eoghan", "ManageClientsFragment Adapter data updated");
        } else {
            Log.d("Eoghan", "ManageClientsFragment Adapter is null");
        }
    }

    private void setUpRecyclerView(View view) {
        clientListViewRecyclerView = view.findViewById(R.id.clientsRecyclerView);
        clientListViewRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        adapter = new com.example.finalandroidmqtt.util.ClientAdapter(application.getMqtt().getClients().getValue(), application);
        clientListViewRecyclerView.setAdapter(adapter);
    }
}