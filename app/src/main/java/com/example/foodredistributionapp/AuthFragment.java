package com.example.foodredistributionapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.Context;


import com.example.foodredistributionapp.data.FoodDonationContract.UserEntry;
import com.example.foodredistributionapp.data.FoodDonationDataManager;
import com.example.foodredistributionapp.donor.DonorDashboardActivity;
import com.example.foodredistributionapp.recipient.RecipientDashboardActivity;

public class AuthFragment extends Fragment {

    private String userType;
    private ViewFlipper viewFlipper;
    private FoodDonationDataManager dataManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auth, container, false);

        userType = getArguments() != null ? getArguments().getString("USER_TYPE") : "";

        // Initialize the data manager
        dataManager = new FoodDonationDataManager(getContext());
        dataManager.open();

        TextView titleTextView = view.findViewById(R.id.textViewAuthTitle);
        titleTextView.setText(userType.substring(0, 1).toUpperCase() + userType.substring(1) + " Authentication");

        viewFlipper = view.findViewById(R.id.viewFlipperAuth);

        Button loginButton = view.findViewById(R.id.buttonLogin);
        TextView switchToRegisterText = view.findViewById(R.id.textViewSwitchToRegister);

        Button registerButton = view.findViewById(R.id.buttonRegister);
        TextView switchToLoginText = view.findViewById(R.id.textViewSwitchToLogin);

        loginButton.setOnClickListener(v -> handleLogin());
        registerButton.setOnClickListener(v -> handleRegistration());

        switchToRegisterText.setOnClickListener(v -> viewFlipper.setDisplayedChild(1)); // Show registration view
        switchToLoginText.setOnClickListener(v -> viewFlipper.setDisplayedChild(0)); // Show login view

        return view;
    }

    private void handleLogin() {
        EditText emailField = getView().findViewById(R.id.editTextLoginEmail);
        EditText passwordField = getView().findViewById(R.id.editTextLoginPassword);

        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check login credentials in the database
        Cursor cursor = dataManager.getUserByEmail(email);

        if (cursor != null && cursor.moveToFirst()) {
            // User exists, check password
            int passwordColumnIndex = cursor.getColumnIndex(UserEntry.COLUMN_PASSWORD);
            int userTypeColumnIndex = cursor.getColumnIndex(UserEntry.COLUMN_USER_TYPE);
            int idColumnIndex = cursor.getColumnIndex(UserEntry._ID);

            String storedPassword = cursor.getString(passwordColumnIndex);
            String storedUserType = cursor.getString(userTypeColumnIndex);
            long userId = cursor.getLong(idColumnIndex);

            cursor.close();

            if (password.equals(storedPassword)) {
                // Check if the user type matches the current flow
                if (userType.equals(storedUserType)) {
                    navigateToMainApp(userId);
                } else {
                    Toast.makeText(getContext(), "Invalid user type. Please login as " + storedUserType, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Invalid password", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (cursor != null) {
                cursor.close();
            }
            Toast.makeText(getContext(), "Email not registered", Toast.LENGTH_SHORT).show();

            // For testing purposes, allow login with test credentials
            if (email.equals("test@mail.com") && password.equals("123")) {
                Toast.makeText(getContext(), "Using test account", Toast.LENGTH_SHORT).show();
                navigateToMainApp(-1); // Using -1 as a placeholder for test user ID
            }
        }
    }

    private void handleRegistration() {
        EditText nameField = getView().findViewById(R.id.editTextRegisterName);
        EditText emailField = getView().findViewById(R.id.editTextRegisterEmail);
        EditText phoneField = getView().findViewById(R.id.editTextRegisterPhone);
        EditText addressField = getView().findViewById(R.id.editTextRegisterAddress);
        EditText passwordField = getView().findViewById(R.id.editTextRegisterPassword);
        EditText confirmPasswordField = getView().findViewById(R.id.editTextRegisterConfirmPassword);

        String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String phone = phoneField.getText().toString().trim();
        String address = addressField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String confirmPassword = confirmPasswordField.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) ||
                TextUtils.isEmpty(address) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Passwords don't match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if email already exists
        Cursor cursor = dataManager.getUserByEmail(email);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            Toast.makeText(getContext(), "Email already registered", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cursor != null) {
            cursor.close();
        }

        // Register the user in the database
        long userId = dataManager.insertUser(name, email, phone, address, password, userType);

        if (userId != -1) {
            Toast.makeText(getContext(), "Registration successful", Toast.LENGTH_SHORT).show();

            // Switch to login view
            viewFlipper.setDisplayedChild(0);

            // Pre-fill email field
            EditText loginEmailField = getView().findViewById(R.id.editTextLoginEmail);
            loginEmailField.setText(email);
        } else {
            Toast.makeText(getContext(), "Registration failed", Toast.LENGTH_SHORT).show();
        }
    }

    // After successful login, in navigateToMainApp method
    private void navigateToMainApp(long userId) {
        // Save user session data using PreferenceManager for consistency
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("user_id", userId);
        editor.putString("user_type", userType);
        editor.putBoolean("is_logged_in", true);
        editor.apply();

        // Then navigate as before (keep this part unchanged)
        Intent intent;
        if ("donor".equals(userType)) {
            intent = new Intent(getActivity(), DonorDashboardActivity.class);
        } else {
            intent = new Intent(getActivity(), RecipientDashboardActivity.class);
        }

        intent.putExtra("USER_TYPE", userType);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dataManager != null) {
            dataManager.close();
        }
    }
}