package com.example.foodredistributionapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

//public class SplashScreenActivity extends AppCompatActivity {
public class MainActivity extends AppCompatActivity {


    private static final int SPLASH_TIMEOUT = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ImageView logoImageView = findViewById(R.id.imageViewLogo);
        TextView appNameTextView = findViewById(R.id.textViewAppName);


        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);


        logoImageView.startAnimation(fadeIn);
        appNameTextView.startAnimation(slideUp);


        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, UserTypeSelectionActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_TIMEOUT);
    }
}