package com.example.finalandroidmqtt;

import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.finalandroidmqtt.util.Mqtt;

import java.util.Arrays;
import java.util.List;

public class MqttApplication extends Application {
    private Mqtt mqttInstance;
    private SensorManager sensorManager;
    private MqttApplication.SensorHandler sensorHandler;
    private MqttApplication instance;

    @Override
    public void onCreate() {
        Log.d("MqttApplication", "onCreate: ");
        super.onCreate();
        instance = this;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorHandler = new MqttApplication.SensorHandler();
        sensorHandler.initializeSensors();
        mqttInstance = Mqtt.getInstance(sensorHandler);
    }


    public Mqtt getMqtt() {
        if (mqttInstance == null) {
            mqttInstance = Mqtt.getInstance(sensorHandler);
        }
        return mqttInstance;
    }

    public class SensorHandler implements SensorEventListener {
        private Sensor proximitySensor;
        private Sensor lightSensor;
        private float lastProximityValue = 0.0f;
        private float lastLightValue = 0.0f;
        private List<Sensor> sensorList;

        public void initializeSensors() {
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
//            startProximitySensor();
//            startLightSensor();
            sensorList = Arrays.asList(proximitySensor, lightSensor);
        }

        public void startProximitySensor() {
            if (proximitySensor != null) {
                sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w("SensorApplication", "No Proximity Sensor found");
            }
        }

        public void stopProximitySensor() {
            if (proximitySensor != null) {
                sensorManager.unregisterListener(this, proximitySensor);
            }
        }

        public void startLightSensor() {
            if (lightSensor != null) {
                sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w("SensorApplication", "No Light Sensor found");
            }
        }

        public void stopLightSensor() {
            if (lightSensor != null) {
                sensorManager.unregisterListener(this, lightSensor);
            }
        }

        public float getLastProximityValue() {
            return lastProximityValue;
        }

        public float getLastLightValue() {
            return lastLightValue;
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                lastProximityValue = event.values[0];
                Log.i("SensorApplication", "Proximity Sensor Reading: " + lastProximityValue);
            } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                lastLightValue = event.values[0];
                Log.i("SensorApplication", "Light Sensor Reading: " + lastLightValue);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // This can be implemented if needed.
        }

        public List<Sensor> getSensorList() {
            return sensorList;
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (sensorHandler != null) {
            sensorHandler.stopProximitySensor();
            sensorHandler.stopLightSensor();
        }
    }

    public SensorHandler getSensorHandler() {
        return sensorHandler;
    }

    public MqttApplication getInstance() {
        return instance;
    }
}
