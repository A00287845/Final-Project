package com.example.finalandroidmqtt.util;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalandroidmqtt.MqttApplication;
import com.example.finalandroidmqtt.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientViewHolder> {
    private Set<MqttAndroidClient> clientSet;


    public ClientAdapter(Set<MqttAndroidClient> clientList) {
        this.clientSet = clientList;
    }

    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mqtt_client_item, parent, false);
        return new ClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder holder, int position) {
        Log.d("Eoghan", "onBindViewHolder called with position: " + position);

        List<MqttAndroidClient> clientList = new ArrayList<>(clientSet);
        Log.d("Eoghan", "clientList created with size: " + clientList.size());

        MqttAndroidClient client = clientList.get(position);
        Log.d("Eoghan", "MqttClient obtained: " + client.getClientId());

        MqttApplication application = (MqttApplication) holder.itemView.getContext().getApplicationContext();
        Log.d("Eoghan", "MqttApplication obtained");

        Map<String, Set<String>> subMap = application.getMutableSubscriptionMap().getValue();
        Log.d("Eoghan", "Subscription Map obtained with size: " + (subMap != null ? subMap.size() : "null"));

        String topic = null;
        if (subMap != null) {
            Log.d("Eoghan", "Subscription Map is not null");
            if (!subMap.isEmpty()) {
                Log.d("Eoghan", "Subscription Map is not empty");
                topic = Objects.requireNonNull(subMap.get(client.getClientId())).toString();
                Log.d("Eoghan", "Topic obtained: " + topic);
            } else {
                Log.d("Eoghan", "Subscription Map is empty");
            }
        } else {
            Log.d("Eoghan", "Subscription Map is null");
        }

        String clientDetails = holder.itemView.getContext().getResources().getString(R.string.client_details, client.getClientId(), client.getServerURI(), topic);
        Log.d("Eoghan", "Client Details String prepared: " + clientDetails);

        holder.clientTv.setText(clientDetails);
        Log.d("Eoghan", "Client details set to TextView");
    }


    @Override
    public int getItemCount() {
        if (clientSet != null) {
            return clientSet.size();
        } else {
            return 0;
        }
    }

    public void updateData(Set<MqttAndroidClient> newClients) {
        clientSet.clear();
        clientSet.addAll(newClients);
        notifyDataSetChanged();
    }

    public static class ClientViewHolder extends RecyclerView.ViewHolder {
        TextView clientTv;

        public ClientViewHolder(@NonNull View itemView) {
            super(itemView);
            clientTv = itemView.findViewById(R.id.clientContent);
        }

    }
}