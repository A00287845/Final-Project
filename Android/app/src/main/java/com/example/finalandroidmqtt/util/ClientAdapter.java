package com.example.finalandroidmqtt.util;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalandroidmqtt.view.MqttApplication;
import com.example.finalandroidmqtt.R;

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientViewHolder> {
    private final MqttApplication application;
    private Map<String, MqttAndroidClient> clientMap;
    private List<MqttAndroidClient> clientList;




    public ClientAdapter(Map<String, MqttAndroidClient> clientList, MqttApplication application) {
        this.clientMap = clientList;
        this.application = application;
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

        clientList = new ArrayList<>(clientMap.values());
        Log.d("Eoghan", "ClientAdapter onBindViewHolder clientList created with size: " + clientList.size());

        MqttAndroidClient client = clientList.get(position);
        Log.d("Eoghan", "ClientAdapter onBindViewHolder MqttClient obtained: " + client.getClientId());

        MqttApplication application = (MqttApplication) holder.itemView.getContext().getApplicationContext();
        Log.d("Eoghan", "ClientAdapter onBindViewHolder MqttApplication obtained");

        Map<String, Set<String>> subMap = application.getMqtt().getMutableSubscriptionMap().getValue();
        Log.d("Eoghan", "ClientAdapter onBindViewHolder Subscription Map obtained with size: " + (subMap != null ? subMap.size() : "null"));

        // Early return if subMap is null or empty
        if (subMap == null) {
            Log.d("Eoghan", "ClientAdapter onBindViewHolder Subscription Map is null");
            setClientDetails(holder, client, null);
            return;
        }
        Log.d("Eoghan", "ClientAdapter onBindViewHolder Subscription Map is not null");

        if (subMap.isEmpty()) {
            Log.d("Eoghan", "ClientAdapter onBindViewHolder Subscription Map is empty");
            setClientDetails(holder, client, null);
            return;
        }
        Log.d("Eoghan", "ClientAdapter onBindViewHolder Subscription Map is not empty");

        // Proceed with non-null and non-empty subMap
        String topic = Objects.requireNonNull(subMap.get(client.getClientId())).toString();
        Log.d("Eoghan", "ClientAdapter onBindViewHolder Topic obtained: " + topic);

        setClientDetails(holder, client, topic);
    }

    private void setClientDetails(ClientViewHolder holder, MqttAndroidClient client, String topic) {
        String clientDetails = holder.itemView.getContext().getResources().getString(R.string.client_details, client.getClientId(), client.getServerURI(), topic);
        Log.d("Eoghan", "ClientAdapter onBindViewHolder Client Details String prepared: " + clientDetails);

        holder.clientIdTv.setText(client.getClientId());
        holder.clientUriTv.setText(client.getServerURI());
        holder.clientSubscriptionsTv.setText(topic);
        Log.d("Eoghan", "ClientAdapter onBindViewHolder Client details set to TextView with clientDetails: " + clientDetails);
    }



    @Override
    public int getItemCount() {
        Log.d("Eoghan", "ClientAdapter getItemCount called.");
        if (clientMap != null) {
            Log.d("Eoghan", "ClientAdapter getItemCount clientSet is not null. Size: " + clientMap.size());
            return clientMap.size();
        } else {
            Log.d("Eoghan", "ClientAdapter getItemCount clientSet is null. Returning 0.");
            return 0;
        }
    }


    public void updateData(Map<String, MqttAndroidClient> newClients) {
        Log.d("Eoghan", "ClientAdapter updateData called with newClients size: " + (newClients != null ? newClients.size() : "null"));
        clientMap =  new HashMap<>();

        Log.d("Eoghan", "ClientAdapter updateData Clearing and adding all newClients.");

        clientMap = newClients;
        notifyDataSetChanged();
        Log.d("Eoghan", "ClientAdapter updateData notifyDataSetChanged called after updating clientSet.");

    }

    public class ClientViewHolder extends RecyclerView.ViewHolder {
        TextView clientIdTv, clientUriTv, clientSubscriptionsTv;

        public ClientViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("Eoghan", "ClientAdapter ClientViewHolder constructor called.");
            clientIdTv = itemView.findViewById(R.id.clientIdTv);
            clientUriTv = itemView.findViewById(R.id.clientUrlTv);
            clientSubscriptionsTv = itemView.findViewById(R.id.clientSubsTv);
            Log.d("Eoghan", "ClientAdapter ClientViewHolder constructor clientTv TextView found in itemView.");

            itemView.findViewById(R.id.deleteClientButton).setOnClickListener(v -> {
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    if (application.getMqtt().getMutableMqttClientMap().getValue() == null) {
                        return;
                    }
                    if (application.getMqtt().getMutableMqttClientMap().getValue().isEmpty()) {
                        return;
                    }
                    Log.d("Eoghan", "ClientAdapter ClientViewHolder deleteOnClick called. for ID: " + clientList.get(position).getClientId());
                    try {
                        application.getMqtt().removeClientByNameFromMap(clientList.get(position).getClientId());
                    } catch(Exception e){
                        Log.d("Eoghan", "ClientAdapter ClientViewHolder deleteOnClick failed " + e.getMessage());

                    }
                }
            });
        }
    }

}