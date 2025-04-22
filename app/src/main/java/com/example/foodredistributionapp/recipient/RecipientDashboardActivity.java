package com.example.foodredistributionapp.recipient;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodredistributionapp.R;

public class RecipientDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_dashboard);

        TextView welcomeText = findViewById(R.id.textViewWelcome);
        welcomeText.setText("Welcome, Recipient!");
    }
}