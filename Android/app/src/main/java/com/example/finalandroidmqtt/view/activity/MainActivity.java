package com.example.finalandroidmqtt.view.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.finalandroidmqtt.MqttApplication;
import com.example.finalandroidmqtt.R;
import com.example.finalandroidmqtt.util.RepeatedTaskLooper;
import com.example.finalandroidmqtt.view.fragment.ManageClientsFragment;
import com.example.finalandroidmqtt.view.fragment.ManageSubscriptionsFragment;
import com.example.finalandroidmqtt.view.fragment.SendMessageFragment;

public class MainActivity extends AppCompatActivity {
    private RepeatedTaskLooper looper;
    private MqttApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        application = (MqttApplication) getApplication();


        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        findViewById(R.id.clientFragmentButton).setOnClickListener(v -> addFragment(new ManageClientsFragment()));
        findViewById(R.id.subsFragmentButton).setOnClickListener(v -> addFragment(new ManageSubscriptionsFragment()));
        findViewById(R.id.messageFragmentButton).setOnClickListener(v-> addFragment(new SendMessageFragment()));

        addFragment(new ManageClientsFragment());
        setUpObservation();
    }

    private void setUpObservation() {
        application.getMqtt().getSensorActive().observe(this, active -> {
            Log.d("Eoghan", "MainActivity sensorActive observed");
            if(!application.getMqtt().getClients().getValue().isEmpty()){
                findViewById(R.id.messageFragmentButton).setVisibility(View.VISIBLE);
            }
            if (active != null) {
                if (!active) {
                    if (looper != null) {
                        looper.stop();
                    }
                    return;
                }

                looper = new RepeatedTaskLooper(this);
                looper.start();
            }
        });
    }


    private void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.contentArea, fragment);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (looper != null) {
            looper.stop();
        }
    }
}