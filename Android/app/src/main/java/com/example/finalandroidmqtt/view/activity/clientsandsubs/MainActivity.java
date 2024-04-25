package com.example.finalandroidmqtt.view.activity.clientsandsubs;

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
import com.example.finalandroidmqtt.view.activity.clientsandsubs.fragments.ManageClientsFragment;
import com.example.finalandroidmqtt.view.activity.clientsandsubs.fragments.ManageSubscriptionsFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);


        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        findViewById(R.id.clientFragmentButton).setOnClickListener(v -> addFragment(new ManageClientsFragment()));
        findViewById(R.id.subsFragmentButton).setOnClickListener(v -> addFragment(new ManageSubscriptionsFragment()));

        addFragment(new ManageClientsFragment());
    }




    private void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.contentArea, fragment);
        transaction.commit();
    }


}