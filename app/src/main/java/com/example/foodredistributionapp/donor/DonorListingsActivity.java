package com.example.foodredistributionapp.donor;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodredistributionapp.R;
import com.example.foodredistributionapp.data.FoodDonationContract.FoodListingEntry;
import com.example.foodredistributionapp.data.FoodDonationDataManager;
import com.example.foodredistributionapp.model.Donation;
import com.example.foodredistributionapp.adapter.DonationAdapter;

import java.util.ArrayList;
import java.util.List;

public class DonorListingsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewListings;
    private DonationAdapter donationAdapter;
    private List<Donation> donationList;
    private FoodDonationDataManager dataManager;
    private long donorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_listings);

        // Initialize database manager
        dataManager = new FoodDonationDataManager(this);
        dataManager.open();

        // Get donor ID from SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        donorId = prefs.getLong("user_id", -1);

        if (donorId == -1) {
            Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Food Listings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set up recycler view
        recyclerViewListings = findViewById(R.id.recycler_view_listings);
        recyclerViewListings.setLayoutManager(new LinearLayoutManager(this));

        donationList = new ArrayList<>();
        donationAdapter = new DonationAdapter(donationList, donation -> {
            // Handle click on donation item - show details/edit options
            Toast.makeText(this, "Selected: " + donation.getTitle(), Toast.LENGTH_SHORT).show();
        });

        recyclerViewListings.setAdapter(donationAdapter);

        // Load all donations
        loadDonations();
    }

    private void loadDonations() {
        donationList.clear();

        Cursor cursor = dataManager.getDonorListings(donorId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(FoodListingEntry._ID));
                String title = cursor.getString(cursor.getColumnIndex(FoodListingEntry.COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndex(FoodListingEntry.COLUMN_DESCRIPTION));
                String quantity = cursor.getString(cursor.getColumnIndex(FoodListingEntry.COLUMN_QUANTITY));
                String expiryDate = cursor.getString(cursor.getColumnIndex(FoodListingEntry.COLUMN_EXPIRY_DATE));
                String status = cursor.getString(cursor.getColumnIndex(FoodListingEntry.COLUMN_STATUS));

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
                donationList.add(donation);

            } while (cursor.moveToNext());

            cursor.close();
        }

        donationAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataManager != null) {
            dataManager.close();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}