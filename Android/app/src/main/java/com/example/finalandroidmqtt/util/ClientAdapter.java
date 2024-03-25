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

import java.util.ArrayList;
import java.util.HashSet;
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
        Log.d("Eoghan", "ClientAdapter onBindViewHolder called with position: " + position);

        List<MqttAndroidClient> clientList = new ArrayList<>(clientSet);
        Log.d("Eoghan", "ClientAdapter clientList created with size: " + clientList.size());

        MqttAndroidClient client = clientList.get(position);
        Log.d("Eoghan", "ClientAdapter MqttClient obtained: " + client.getClientId());

        MqttApplication application = (MqttApplication) holder.itemView.getContext().getApplicationContext();
        Log.d("Eoghan", "ClientAdapter MqttApplication obtained");

        Map<String, Set<String>> subMap = application.getMutableSubscriptionMap().getValue();
        Log.d("Eoghan", "ClientAdapter Subscription Map obtained with size: " + (subMap != null ? subMap.size() : "null"));

        String topic = null;
        if (subMap != null) {
            Log.d("Eoghan", "ClientAdapter Subscription Map is not null");
            if (!subMap.isEmpty()) {
                Log.d("Eoghan", "ClientAdapter Subscription Map is not empty");
                topic = Objects.requireNonNull(subMap.get(client.getClientId())).toString();
                Log.d("Eoghan", "ClientAdapter Topic obtained: " + topic);
            } else {
                Log.d("Eoghan", "ClientAdapter Subscription Map is empty");
            }
        } else {
            Log.d("Eoghan", "ClientAdapter Subscription Map is null");
        }

        String clientDetails = holder.itemView.getContext().getResources().getString(R.string.client_details, client.getClientId(), client.getServerURI(), topic);
        Log.d("Eoghan", "ClientAdapter Client Details String prepared: " + clientDetails);

        holder.clientTv.setText(clientDetails);
        Log.d("Eoghan", "ClientAdapter Client details set to TextView with clientDetails: " + clientDetails);
    }


    @Override
    public int getItemCount() {
        Log.d("Eoghan", "ClientAdapter getItemCount called.");
        if (clientSet != null) {
            Log.d("Eoghan", "ClientAdapter clientSet is not null. Size: " + clientSet.size());
            return clientSet.size();
        } else {
            Log.d("Eoghan", "ClientAdapter clientSet is null. Returning 0.");
            return 0;
        }
    }


    public void updateData(Set<MqttAndroidClient> newClients) {
        Log.d("Eoghan", "ClientAdapter updateData called with newClients size: " + (newClients != null ? newClients.size() : "null"));
        if (clientSet == null) {
            Log.d("Eoghan", "ClientAdapter updateData clientSet is null, creating new set");

            clientSet = new HashSet<>();
        }
        Log.d("Eoghan", "ClientAdapter updateData Clearing and adding all newClients.");
        clientSet.clear();
        clientSet.addAll(newClients);
        notifyDataSetChanged();
        Log.d("Eoghan", "ClientAdapter updateData notifyDataSetChanged called after updating clientSet.");

    }

    public static class ClientViewHolder extends RecyclerView.ViewHolder {
        TextView clientTv;

        public ClientViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("Eoghan", "ClientAdapter ClientViewHolder constructor called.");
            clientTv = itemView.findViewById(R.id.clientContent);
            Log.d("Eoghan", "ClientAdapter clientTv TextView found in itemView.");
        }
    }

}