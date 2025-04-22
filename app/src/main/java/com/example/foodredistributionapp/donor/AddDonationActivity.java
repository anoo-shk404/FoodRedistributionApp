package com.example.foodredistributionapp.donor;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.foodredistributionapp.R;
import com.example.foodredistributionapp.data.FoodDonationDataManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddDonationActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextExpiryDate;
    private EditText editTextQuantity;
    private EditText editTextPickupAddress;
    private Spinner spinnerFoodType;
    private Button buttonSubmit;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private FoodDonationDataManager dataManager;
    private long donorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_donation);

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
        getSupportActionBar().setTitle("Add New Food Donation");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        editTextExpiryDate = findViewById(R.id.edit_text_expiry_date);
        editTextQuantity = findViewById(R.id.edit_text_quantity);
        editTextPickupAddress = findViewById(R.id.edit_text_pickup_address);
        spinnerFoodType = findViewById(R.id.spinner_food_type);
        buttonSubmit = findViewById(R.id.button_submit);

        // Setup food type spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.food_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFoodType.setAdapter(adapter);

        // Initialize date picker
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        editTextExpiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        // Handle form submission
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFoodListing();
            }
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    editTextExpiryDate.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void submitFoodListing() {
        // Validate form
        if (editTextTitle.getText().toString().trim().isEmpty()) {
            editTextTitle.setError("Title is required");
            return;
        }

        if (editTextExpiryDate.getText().toString().trim().isEmpty()) {
            editTextExpiryDate.setError("Expiry date is required");
            return;
        }

        if (editTextQuantity.getText().toString().trim().isEmpty()) {
            editTextQuantity.setError("Quantity is required");
            return;
        }

        if (editTextPickupAddress.getText().toString().trim().isEmpty()) {
            editTextPickupAddress.setError("Pickup address is required");
            return;
        }

        // Get values from the form
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String expiryDate = editTextExpiryDate.getText().toString().trim();
        String quantity = editTextQuantity.getText().toString().trim();
        String pickupAddress = editTextPickupAddress.getText().toString().trim();
        String foodType = spinnerFoodType.getSelectedItem().toString();

        // Create description that includes food type if selected
        String fullDescription = description;
        if (!foodType.isEmpty() && !foodType.equalsIgnoreCase("Select Food Type")) {
            fullDescription = "Type: " + foodType + "\n" + description;
        }

        // Insert food listing into database
        long listingId = dataManager.insertFoodListing(
                donorId,
                title,
                fullDescription,
                quantity,
                expiryDate,
                pickupAddress,
                "" // No image path for now
        );

        if (listingId != -1) {
            Toast.makeText(this, "Food donation added successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving donation", Toast.LENGTH_SHORT).show();
        }
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