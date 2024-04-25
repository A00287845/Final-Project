package com.example.finalandroidmqtt.view.activity.datavisualiser;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.finalandroidmqtt.R;
import com.example.finalandroidmqtt.view.activity.MenuActivity;
import com.example.finalandroidmqtt.view.activity.datavisualiser.fragments.GyroscopeVisualiserFragment;
import com.example.finalandroidmqtt.view.activity.datavisualiser.fragments.HumidityVisualiserFragment;
import com.example.finalandroidmqtt.view.activity.datavisualiser.fragments.TemperatureAndHumidityVisualiserFragment;
import com.example.finalandroidmqtt.view.fragment.ManageClientsFragment;

public class DataVisualiserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_data_visualiser);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        findViewById(R.id.gyroscopeFragmentButt).setOnClickListener(v -> addFragment(new GyroscopeVisualiserFragment()));
        findViewById(R.id.humidityFragmentButt).setOnClickListener(v -> addFragment(new HumidityVisualiserFragment()));
        findViewById(R.id.temyAndHumidFragmentButt).setOnClickListener(v-> addFragment(new TemperatureAndHumidityVisualiserFragment()));

        addFragment(new GyroscopeVisualiserFragment());

    }


    private void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.visualiser_content_area, fragment);
        transaction.commit();
    }
}