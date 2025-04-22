package com.example.foodredistributionapp.donor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodredistributionapp.MainActivity;
import com.example.foodredistributionapp.R;
import com.example.foodredistributionapp.data.FoodDonationContract.FoodListingEntry;
import com.example.foodredistributionapp.data.FoodDonationDataManager;
import com.example.foodredistributionapp.model.Donation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.example.foodredistributionapp.adapter.DonationAdapter;

import java.util.ArrayList;
import java.util.List;

public class DonorDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private RecyclerView recyclerViewDonations;
    private DonationAdapter donationAdapter;
    private List<Donation> donationList;
    private FoodDonationDataManager dataManager;
    private long donorId;
    private TextView textViewPendingCount, textViewReservedCount, textViewCompletedCount, textViewExpiredCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_dashboard);

        // Initialize database manager
        dataManager = new FoodDonationDataManager(this);
        dataManager.open();

        // Get donor ID from SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        donorId = prefs.getLong("user_id", -1);

        if (donorId == -1) {
            Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Donor Dashboard");

        // Set up navigation drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Find dashboard stat views
        textViewPendingCount = findViewById(R.id.text_view_available_count);
        textViewReservedCount = findViewById(R.id.text_view_reserved_count);
        textViewCompletedCount = findViewById(R.id.text_view_completed_count);
        textViewExpiredCount = findViewById(R.id.text_view_expired_count);

        // Set up recycler view
        recyclerViewDonations = findViewById(R.id.recycler_view_donations);
        recyclerViewDonations.setLayoutManager(new LinearLayoutManager(this));

        donationList = new ArrayList<>();
        donationAdapter = new DonationAdapter(donationList, donation -> {
            // Handle donation item click - show details or edit
            Toast.makeText(DonorDashboardActivity.this,
                    "Selected: " + donation.getTitle(), Toast.LENGTH_SHORT).show();
            // Could open details activity here
        });
        recyclerViewDonations.setAdapter(donationAdapter);

        // Load donations from database
        loadDonations();

        // Set up floating action button for adding new donations
        FloatingActionButton fab = findViewById(R.id.fab_add_donation);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(DonorDashboardActivity.this, AddDonationActivity.class);
            startActivity(intent);
        });

        // Set up dashboard stats click handlers
        findViewById(R.id.card_available).setOnClickListener(v -> filterDonations("available"));
        findViewById(R.id.card_reserved).setOnClickListener(v -> filterDonations("reserved"));
        findViewById(R.id.card_completed).setOnClickListener(v -> filterDonations("completed"));
        findViewById(R.id.card_expired).setOnClickListener(v -> filterDonations("expired"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload donations when returning to this activity
        loadDonations();
    }

    private void loadDonations() {
        donationList.clear();

        // Get cursor for donor's food listings
        Cursor cursor = dataManager.getDonorListings(donorId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    // Safely get column indices
                    int idIndex = cursor.getColumnIndex(FoodListingEntry._ID);
                    int titleIndex = cursor.getColumnIndex(FoodListingEntry.COLUMN_TITLE);
                    int descriptionIndex = cursor.getColumnIndex(FoodListingEntry.COLUMN_DESCRIPTION);
                    int quantityIndex = cursor.getColumnIndex(FoodListingEntry.COLUMN_QUANTITY);
                    int expiryDateIndex = cursor.getColumnIndex(FoodListingEntry.COLUMN_EXPIRY_DATE);
                    int pickupAddressIndex = cursor.getColumnIndex(FoodListingEntry.COLUMN_PICKUP_ADDRESS);
                    int statusIndex = cursor.getColumnIndex(FoodListingEntry.COLUMN_STATUS);
                    int imagePathIndex = cursor.getColumnIndex(FoodListingEntry.COLUMN_IMAGE_PATH);

                    // Check if all required columns are present
                    if (idIndex < 0 || titleIndex < 0 || statusIndex < 0) {
                        Toast.makeText(this, "Database error: Missing required columns", Toast.LENGTH_SHORT).show();
                        continue;
                    }

                    int id = cursor.getInt(idIndex);
                    String title = cursor.getString(titleIndex);
                    String description = descriptionIndex >= 0 ? cursor.getString(descriptionIndex) : "";
                    String quantity = quantityIndex >= 0 ? cursor.getString(quantityIndex) : "";
                    String expiryDate = expiryDateIndex >= 0 ? cursor.getString(expiryDateIndex) : "";
                    String pickupAddress = pickupAddressIndex >= 0 ? cursor.getString(pickupAddressIndex) : "";
                    String status = cursor.getString(statusIndex);
                    String imagePath = imagePathIndex >= 0 ? cursor.getString(imagePathIndex) : "";

                    // Map database status to UI status
                    String uiStatus;
                    switch (status) {
                        case "available":
                            uiStatus = "Pending";
                            break;
                        case "reserved":
                            uiStatus = "Reserved";
                            break;
                        case "completed":
                            uiStatus = "Completed";
                            break;
                        case "expired":
                            uiStatus = "Expired";
                            break;
                        default:
                            uiStatus = "Pending";
                    }

                    // Create Donation object and add to list
                    Donation donation = new Donation(title, quantity, expiryDate, uiStatus);
                    donation.setId(id);
                    donation.setDescription(description);
                    donation.setPickupAddress(pickupAddress);
                    donation.setImagePath(imagePath);
                    donationList.add(donation);
                } catch (Exception e) {
                    // Log or handle the exception more gracefully
                    Toast.makeText(this, "Error processing donation: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } while (cursor.moveToNext());

            cursor.close();
        }

        // Update adapter
        donationAdapter.notifyDataSetChanged();

        // Update dashboard stats
        updateDashboardStats();
    }

    private void updateDashboardStats() {
        int available = 0;
        int reserved = 0;
        int completed = 0;
        int expired = 0;

        for (Donation donation : donationList) {
            if (donation.getStatus().equals("Pending")) {
                available++;
            } else if (donation.getStatus().equals("Reserved")) {
                reserved++;
            } else if (donation.getStatus().equals("Completed")) {
                completed++;
            } else if (donation.getStatus().equals("Expired")) {
                expired++;
            }
        }

        // Update the text views
        textViewPendingCount.setText(String.valueOf(available));
        textViewReservedCount.setText(String.valueOf(reserved));
        textViewCompletedCount.setText(String.valueOf(completed));
        textViewExpiredCount.setText(String.valueOf(expired));
    }

    private void filterDonations(String dbStatus) {
        donationList.clear();
        Cursor cursor = dataManager.getDonorListings(donorId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    // Get the status column index and check if it exists
                    int statusIndex = cursor.getColumnIndex(FoodListingEntry.COLUMN_STATUS);
                    if (statusIndex < 0) {
                        Toast.makeText(this, "Database error: Missing status column", Toast.LENGTH_SHORT).show();
                        continue;
                    }

                    String status = cursor.getString(statusIndex);

                    // Only include items matching the filter
                    if (status.equals(dbStatus)) {
                        // Safely get column indices
                        int idIndex = cursor.getColumnIndex(FoodListingEntry._ID);
                        int titleIndex = cursor.getColumnIndex(FoodListingEntry.COLUMN_TITLE);
                        int descriptionIndex = cursor.getColumnIndex(FoodListingEntry.COLUMN_DESCRIPTION);
                        int quantityIndex = cursor.getColumnIndex(FoodListingEntry.COLUMN_QUANTITY);
                        int expiryDateIndex = cursor.getColumnIndex(FoodListingEntry.COLUMN_EXPIRY_DATE);
                        int pickupAddressIndex = cursor.getColumnIndex(FoodListingEntry.COLUMN_PICKUP_ADDRESS);
                        int imagePathIndex = cursor.getColumnIndex(FoodListingEntry.COLUMN_IMAGE_PATH);

                        // Check if required columns are present
                        if (idIndex < 0 || titleIndex < 0) {
                            Toast.makeText(this, "Database error: Missing required columns", Toast.LENGTH_SHORT).show();
                            continue;
                        }

                        int id = cursor.getInt(idIndex);
                        String title = cursor.getString(titleIndex);
                        String description = descriptionIndex >= 0 ? cursor.getString(descriptionIndex) : "";
                        String quantity = quantityIndex >= 0 ? cursor.getString(quantityIndex) : "";
                        String expiryDate = expiryDateIndex >= 0 ? cursor.getString(expiryDateIndex) : "";
                        String pickupAddress = pickupAddressIndex >= 0 ? cursor.getString(pickupAddressIndex) : "";
                        String imagePath = imagePathIndex >= 0 ? cursor.getString(imagePathIndex) : "";

                        // Map database status to UI status
                        String uiStatus;
                        switch (status) {
                            case "available":
                                uiStatus = "Pending";
                                break;
                            case "reserved":
                                uiStatus = "Reserved";
                                break;
                            case "completed":
                                uiStatus = "Completed";
                                break;
                            case "expired":
                                uiStatus = "Expired";
                                break;
                            default:
                                uiStatus = "Pending";
                        }

                        Donation donation = new Donation(title, quantity, expiryDate, uiStatus);
                        donation.setId(id);
                        donation.setDescription(description);
                        donation.setPickupAddress(pickupAddress);
                        donation.setImagePath(imagePath);
                        donationList.add(donation);
                    }
                } catch (Exception e) {
                    // Log or handle the exception more gracefully
                    Toast.makeText(this, "Error processing donation: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } while (cursor.moveToNext());

            cursor.close();
        }

        donationAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            // Already on dashboard
        } else if (id == R.id.nav_listings) {
            Intent intent = new Intent(this, DonorListingsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_profile) {
            //Intent intent = new Intent(this, DonorProfileActivity.class);
            //startActivity(intent);
        } else if (id == R.id.nav_logout) {
            // Handle logout
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit().remove("user_id").apply();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataManager != null) {
            dataManager.close();
        }
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