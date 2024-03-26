package com.example.finalandroidmqtt.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


import android.app.Dialog;
import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;

import com.example.finalandroidmqtt.R;

public class AddSubscriptionFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_sub, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Customize the dialog here (e.g., title, buttons)
        builder.setTitle("My Floating Window");
        builder.setView(R.layout.fragment_add_sub);
        return builder.create();
    }

    // You can customize the style and dimensions of the dialog in onCreateDialog if needed
}
