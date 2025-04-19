package com.example.foodredistributionapp;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class AppInfoDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireActivity())
                .setTitle("About the App")
                .setMessage("This app helps in food redistribution.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .create();
    }
}
