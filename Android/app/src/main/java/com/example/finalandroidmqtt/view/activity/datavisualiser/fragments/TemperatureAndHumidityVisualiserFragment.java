package com.example.finalandroidmqtt.view.activity.datavisualiser.fragments;

import static com.example.finalandroidmqtt.view.activity.datavisualiser.fragments.HumidityVisualiserFragment.ENVIRONMENT_CLIENT_NAME;
import static com.example.finalandroidmqtt.view.activity.datavisualiser.fragments.HumidityVisualiserFragment.SENSEHAT_ENVIRONMENT_TOPIC;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finalandroidmqtt.MqttApplication;
import com.example.finalandroidmqtt.R;
import com.example.finalandroidmqtt.pojo.ClientHolder;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import info.mqtt.android.service.MqttAndroidClient;


public class TemperatureAndHumidityVisualiserFragment extends Fragment {
    private MqttApplication application;
    private LineChart chartTemperature;
    private LineChart chartHumidity;
    private int timeIndexTemperature = 0;
    private int timeIndexHumidity = 0;


    public TemperatureAndHumidityVisualiserFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_temperature_and_humidity_visualiser, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        application = (MqttApplication) requireActivity().getApplication();

        chartTemperature = view.findViewById(R.id.chartTemperature);
        chartHumidity = view.findViewById(R.id.chartHumidity);

        setupTemperatureChart(chartTemperature, "Temperature (°C)");
        setupHumidityChart(chartHumidity, "Humidity (%)");
        beginObserving();
        setupMqtt();
    }

    private void setupTemperatureChart(LineChart chart, String label) {
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setPinchZoom(true);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // If each increment represents a fixed time interval, you can calculate it here
                // Example: every index is 10 seconds apart
                int totalSeconds = (int) value * 10;
                int minutes = totalSeconds / 60;
                int seconds = totalSeconds % 60;
                return String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds);
            }
        });

        // Set up the Y-axis for Temperature
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMaximum(40);  // Max temperature
        leftAxis.setAxisMinimum(-10); // Min temperature
        leftAxis.setDrawGridLines(true);

        // Disable the right Y-axis
        chart.getAxisRight().setEnabled(false);


        LineDataSet dataSet = new LineDataSet(null, label);
        dataSet.setLineWidth(2.5f);
        dataSet.setColor(Color.rgb(240, 99, 99));
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.2f);

        LineData data = new LineData(dataSet);
        chart.setData(data);
    }

    private void setupHumidityChart(LineChart chart, String label) {
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setPinchZoom(true);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // If each increment represents a fixed time interval, you can calculate it here
                // Example: every index is 10 seconds apart
                int totalSeconds = (int) value * 10;
                int minutes = totalSeconds / 60;
                int seconds = totalSeconds % 60;
                return String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds);
            }
        });

        // Set up the Y-axis for Humidity
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMaximum(100); // Max humidity
        leftAxis.setAxisMinimum(0);   // Min humidity
        leftAxis.setDrawGridLines(true);

        // Disable the right Y-axis
        chart.getAxisRight().setEnabled(false);


        LineDataSet dataSet = new LineDataSet(null, label);
        dataSet.setLineWidth(2.5f);
        dataSet.setColor(Color.rgb(240, 99, 99));
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.2f);

        LineData data = new LineData(dataSet);
        chart.setData(data);
    }

    private void addEntry(LineChart chart, float value, int timeIndex) {
        LineData data = chart.getData();
        if (data != null) {
            LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
            if (set == null) {
                set = new LineDataSet(new ArrayList<Entry>(), "Dynamic Data");
                data.addDataSet(set);
            }
            set.addEntry(new Entry(timeIndex, value)); // Use the time index
            data.notifyDataChanged();
            chart.notifyDataSetChanged();
            chart.invalidate(); // Refresh the chart

            Log.d("AddEntry", "Added entry: Time Index = " + timeIndex + ", Value = " + value);
        }
    }




    public void addEntryToTemperature(float temperature) {
        addEntry(chartTemperature, temperature, timeIndexTemperature++);
    }

    public void addEntryToHumidity(float humidity) {
        addEntry(chartHumidity, humidity, timeIndexHumidity++);
    }



    private void setupMqtt() {
        application.getMqtt().setupBroker(application.getApplicationContext(), ENVIRONMENT_CLIENT_NAME, "ssl://930094acb7da4acfbf5761b3ac2c7c90.s1.eu.hivemq.cloud:8883");
    }

    private void beginObserving() {
        application.getMqtt().getClients().observe(getViewLifecycleOwner(), clients -> {
            if (clients != null) {
                ClientHolder holder = application.getMqtt().getClientHolderFromListByName(ENVIRONMENT_CLIENT_NAME, Objects.requireNonNull(application.getMqtt().getClients().getValue()));
                if(holder == null){
                    return;
                }
                Log.d("HELP", "" + holder.getSubscriptions());
                if (holder.getSubscriptions().contains(SENSEHAT_ENVIRONMENT_TOPIC)) {
                    return;
                }
                MqttAndroidClient gyroscopeMqttClient = application.getMqtt().getClientHolderFromListByName(ENVIRONMENT_CLIENT_NAME, Objects.requireNonNull(application.getMqtt().getClients().getValue())).getClient();
                application.getMqtt().subscribeToTopic(SENSEHAT_ENVIRONMENT_TOPIC, gyroscopeMqttClient);
            }
        });

        application.getMqtt().getSubbedMessagesList().observe(getViewLifecycleOwner(), messages -> {
            if (messages != null) {
                if (messages.get(messages.size() - 1).first.equals(SENSEHAT_ENVIRONMENT_TOPIC)) {
                    updateCharts(messages.get(messages.size() - 1).second);
                }
            } else {
                Log.d("Eoghan", "Environment messages is null");
            }
        });
    }

    private void updateCharts(String input) {
        String jsonPart = input.substring(input.indexOf('{'));

        try {
            JSONObject jsonObject = new JSONObject(jsonPart);
            double humidity = jsonObject.getDouble("humidity");
            double temperature = jsonObject.getDouble("temperature");
            addEntryToTemperature((float) temperature );
            addEntryToHumidity((float) humidity );

        } catch (Exception e) {
            Log.e("JSON Parse Error", "Error parsing input string for humidity", e);
        }

    }
}