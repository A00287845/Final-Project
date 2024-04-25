package com.example.finalandroidmqtt.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalandroidmqtt.MqttApplication;

public class RepeatedTaskLooper {
    private Handler handler;
    private boolean running;
    private Runnable taskRunnable;
    private int interval = 5000;  // Interval to run the task (500ms)

    public RepeatedTaskLooper(AppCompatActivity activity) {
        handler = new Handler(Looper.getMainLooper());
        taskRunnable = new Runnable() {
            @Override
            public void run() {
                // Your task here
                performTask(activity);

                // Schedule the task to run again after the interval
                handler.postDelayed(this, interval);
            }
        };
    }

    public void setInterval(int interval) {
        Log.d("RepeatedTaskLooper", " Setting loop speed to " + interval);
        this.interval = interval;
    }

    private void performTask(AppCompatActivity activity) {
        // The task to be executed every 500 milliseconds
        System.out.println("Task executed");
        MqttApplication application = (MqttApplication) activity.getApplication();
        application.getMqtt().publishMessagesForClients();
    }

    public boolean isRunning() {
        return running;
    }

    public void start() {

        running = true;
        handler.post(taskRunnable);
    }

    public void stop() {
        running = false;

        handler.removeCallbacks(taskRunnable);
    }
}
