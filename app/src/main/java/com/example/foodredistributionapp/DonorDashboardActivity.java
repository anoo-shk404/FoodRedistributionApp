package com.example.foodredistributionapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class DonorDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private RecyclerView recyclerViewDonations;
    private DonationAdapter donationAdapter;
    private List<Donation> donationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_dashboard);

        startService(new Intent(this, DonationNotificationService.class));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Donor Dashboard");


        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        recyclerViewDonations = findViewById(R.id.recycler_view_donations);
        recyclerViewDonations.setLayoutManager(new LinearLayoutManager(this));


        donationList = new ArrayList<>();
        loadSampleDonations();


        donationAdapter = new DonationAdapter(donationList);
        recyclerViewDonations.setAdapter(donationAdapter);


        FloatingActionButton fab = findViewById(R.id.fab_add_donation);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open donation form activity
                Intent intent = new Intent(DonorDashboardActivity.this, AddDonationActivity.class);
                startActivity(intent);
            }
        });

        // Set up dashboard stats
        updateDashboardStats();
    }

    private void loadSampleDonations() {

        donationList.add(new Donation("Curd Rice", "50 packets", "2025-03-22", "Pending"));
        donationList.add(new Donation("Idlis", "12 packets", "2025-03-20", "Picked Up"));
        donationList.add(new Donation("Bread", "10 packets of bread", "2025-03-18", "Delivered"));
        donationList.add(new Donation("Milk", "5 litres of milk", "2025-03-15", "Expired"));
    }

    private void updateDashboardStats() {

        int pending = 0;
        int completed = 0;
        int expired = 0;

        for (Donation donation : donationList) {
            if (donation.getStatus().equals("Pending")) {
                pending++;
            } else if (donation.getStatus().equals("Picked Up") || donation.getStatus().equals("Delivered")) {
                completed++;
            } else if (donation.getStatus().equals("Expired")) {
                expired++;
            }
        }


        findViewById(R.id.card_pending).setOnClickListener(v -> filterDonations("Pending"));
        findViewById(R.id.card_completed).setOnClickListener(v -> filterDonations("Completed"));
        findViewById(R.id.card_expired).setOnClickListener(v -> filterDonations("Expired"));
    }

    private void filterDonations(String status) {
        Toast.makeText(this, "Showing " + status + " donations", Toast.LENGTH_SHORT).show();
        // TODO: Implement actual filtering
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Toast.makeText(this, "Profile selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_donations) {
            // Already on donations page
        } else if (id == R.id.nav_schedule) {
            Toast.makeText(this, "Schedule selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_notifications) {
            Toast.makeText(this, "Notifications selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_settings) {
            Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            // Handle logout
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}