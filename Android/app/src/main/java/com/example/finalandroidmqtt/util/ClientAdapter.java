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
import com.example.finalandroidmqtt.pojo.ClientHolder;

import info.mqtt.android.service.MqttAndroidClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientViewHolder> {
    private final MqttApplication application;
    private List<ClientHolder> clientList;

    public ClientAdapter(List<ClientHolder> clientList, MqttApplication application) {
        this.clientList = clientList;
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

        MqttAndroidClient client = clientList.get(position).getClient();

        String topic = "";
        if(clientList.get(position).getSubscriptions() != null){
            topic = clientList.get(position).getSubscriptions().toString();
        }
        Log.d("ClientAdapter", " topic: " + topic);
        Log.d("ClientAdapter", " activeClientHolder: " + clientList.get(position));
        ClientHolder clientHolder = clientList.get(position);
        setClientDetails(holder, client, topic, clientHolder);
    }

    private void setClientDetails(ClientViewHolder holder, MqttAndroidClient client, String topic, ClientHolder clientHolder) {
        String clientDetails = holder.itemView.getContext().getResources().getString(R.string.client_details, client.getClientId(), client.getServerURI(), topic);

        holder.clientIdTv.setText(client.getClientId());
        holder.clientUriTv.setText(client.getServerURI());
        holder.clientSubscriptionsTv.setText(topic);

//        List<Pair<Sensor, String>> clientSensors = clientHolder.getSensorTopics();

        String sensorTopicsStr = clientHolder.getSensorTopicsForTv();

        if (sensorTopicsStr != null && !sensorTopicsStr.isEmpty()) {
            holder.clientTopicsTv.setVisibility(View.VISIBLE);
            holder.clientTopicsTv.setText(sensorTopicsStr);
            Log.d("Client adapter", "Sensor list provided");
        } else {
            Log.d(" CliendAdapter", "No sensor data available for client ID: " + client.getClientId());
            holder.clientTopicsTv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (clientList != null) {
            return clientList.size();
        } else {
            return 0;
        }
    }

    public void updateData(List<ClientHolder> clientList) {
        this.clientList = new ArrayList<>(clientList);
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
                    if (application.getMqtt().getClients().getValue() == null) {
                        return;
                    }
                    if (application.getMqtt().getClients().getValue().isEmpty()) {
                        return;
                    }
                    try {
                        application.getMqtt().removeClientByNameFromList(clientList.get(position).getClient().getClientId());
                    } catch(Exception e){

                    }
                }
            });
        }
    }
}