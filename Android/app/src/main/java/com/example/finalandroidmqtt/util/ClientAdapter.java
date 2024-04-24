package com.example.finalandroidmqtt.util;

import android.hardware.Sensor;
import android.util.Log;
import android.util.Pair;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientViewHolder> {
    private final MqttApplication application;
    private Map<String, MqttAndroidClient> clientMap;
    private List<MqttAndroidClient> clientList;
    Map<String, List<Pair<Sensor, String>>> clientSensors;

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

        clientList = new ArrayList<>(clientMap.values());

        MqttAndroidClient client = clientList.get(position);

        MqttApplication application = (MqttApplication) holder.itemView.getContext().getApplicationContext();

        Map<String, Set<String>> subMap = application.getMqtt().getMutableSubscriptionMap().getValue();

        // Early return if subMap is null or empty
        if (subMap == null) {
            setClientDetails(holder, client, null);
            return;
        }

        if (subMap.isEmpty()) {
            setClientDetails(holder, client, null);
            return;
        }

        // Proceed with non-null and non-empty subMap
        String topic = Objects.requireNonNull(subMap.get(client.getClientId())).toString();

        setClientDetails(holder, client, topic);
    }

    private void setClientDetails(ClientViewHolder holder, MqttAndroidClient client, String topic) {
        String clientDetails = holder.itemView.getContext().getResources().getString(R.string.client_details, client.getClientId(), client.getServerURI(), topic);

        holder.clientIdTv.setText(client.getClientId());
        holder.clientUriTv.setText(client.getServerURI());
        holder.clientSubscriptionsTv.setText(topic);

        if (clientSensors != null) {
            if (clientSensors.containsKey(client.getClientId())) {
                holder.clientTopicsTv.setVisibility(View.VISIBLE);
                StringBuilder pairs = new StringBuilder();
                List<Pair<Sensor, String>> sensorList = clientSensors.get(client.getClientId());
                if (sensorList != null) {  // Ensure the list is not null before iterating
                    Log.d("Client adapter", " Sensor list size " + sensorList.size());
                    for (Pair<Sensor, String> thing : sensorList) {
                        pairs.append(thing.first.getName());
                        pairs.append("->");
                        pairs.append(thing.second);
                        pairs.append("         ||||||||||||        ");
                    }
                    holder.clientTopicsTv.setText(pairs);
                } else {
                    // Optionally handle the case where sensorList is null
                    System.out.println("No sensor data available for client ID: " + client.getClientId());
                }
            } else {
                System.out.println("No entry found for client ID: " + client.getClientId());
            }
        } else {
            System.out.println("clientSensors is null");
        }

    }

    @Override
    public int getItemCount() {
        if (clientMap != null) {
            return clientMap.size();
        } else {
            return 0;
        }
    }

    public void updateData(Map<String, MqttAndroidClient> newClients, Map<String, List<Pair<Sensor, String>>> clientSensors) {
        clientMap = newClients;
        this.clientSensors = clientSensors;
        notifyDataSetChanged();
    }

    public class ClientViewHolder extends RecyclerView.ViewHolder {
        TextView clientIdTv, clientUriTv, clientSubscriptionsTv, clientTopicsTv;

        public ClientViewHolder(@NonNull View itemView) {
            super(itemView);
            clientIdTv = itemView.findViewById(R.id.clientIdTv);
            clientUriTv = itemView.findViewById(R.id.clientUrlTv);
            clientSubscriptionsTv = itemView.findViewById(R.id.clientSubsTv);
            clientTopicsTv = itemView.findViewById(R.id.clientPublisherTv);

            itemView.findViewById(R.id.deleteClientButton).setOnClickListener(v -> {
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    if (application.getMqtt().getMutableMqttClientMap().getValue() == null) {
                        return;
                    }
                    if (application.getMqtt().getMutableMqttClientMap().getValue().isEmpty()) {
                        return;
                    }
                    try {
                        application.getMqtt().removeClientByNameFromMap(clientList.get(position).getClientId());
                    } catch(Exception e){

                    }
                }
            });
        }
    }
}