package com.example.finalandroidmqtt.util;

import android.os.Handler;
import android.os.Looper;

import com.example.finalandroidmqtt.MqttApplication;
import com.example.finalandroidmqtt.view.activity.MainActivity;

public class RepeatedTaskLooper {
    private Handler handler;
    private Runnable taskRunnable;
    private final int interval = 5000;  // Interval to run the task (500ms)

    public RepeatedTaskLooper(MainActivity activity) {
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

    private void performTask(MainActivity activity) {
        // The task to be executed every 500 milliseconds

        System.out.println("Task executed");
        MqttApplication application = (MqttApplication) activity.getApplication();
        application.getMqtt().publishMessagesForClients();
    }

    public void start() {
        handler.post(taskRunnable);
    }

    public void stop() {
        handler.removeCallbacks(taskRunnable);
    }
}