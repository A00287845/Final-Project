package com.example.finalandroidmqtt;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Eoghan", "onCreate: Entry");

        super.onCreate(savedInstanceState);
        Log.d("Eoghan", "onCreate: Called super.onCreate");

        EdgeToEdge.enable(this);
        Log.d("Eoghan", "onCreate: Enabled EdgeToEdge");

        setContentView(R.layout.activity_main);
        Log.d("Eoghan", "onCreate: Set content view to activity_main");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            Log.d("Eoghan", "onApplyWindowInsets: Applied window insets padding. Left: " + systemBars.left + ", Top: " + systemBars.top + ", Right: " + systemBars.right + ", Bottom: " + systemBars.bottom);
            return insets;
        });
        Log.d("Eoghan", "onCreate: Set OnApplyWindowInsetsListener");

        MqttApplication application = (MqttApplication) getApplication();
        Log.d("Eoghan", "onCreate: MqttApplication obtained");

        application.getMqtt().setupBroker(this, "Client_1", "tcp://broker.hivemq.com:1883");
        Log.d("Eoghan", "onCreate: Setup MQTT Broker with Client_1 at tcp://broker.hivemq.com:1883");

        addFragment(new ManageClientsFragment());
        Log.d("Eoghan", "onCreate: Added ManageClientsFragment");
    }


    private void addFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.contentArea, fragment);
        transaction.commit();
    }
}