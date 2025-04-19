package com.example.foodredistributionapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipientDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_dashboard);

        TextView welcomeText = findViewById(R.id.textViewWelcome);
        welcomeText.setText("Welcome, Recipient!");
    }
}