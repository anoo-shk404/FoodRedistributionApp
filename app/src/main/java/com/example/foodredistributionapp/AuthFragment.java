package com.example.foodredistributionapp;

import android.content.Intent;
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

public class AuthFragment extends Fragment {

    private String userType;
    private ViewFlipper viewFlipper;


    private static final String TEST_EMAIL = "test@mail.com";
    private static final String TEST_PASSWORD = "123";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auth, container, false);

        userType = getArguments() != null ? getArguments().getString("USER_TYPE") : "";


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

        if (email.equals(TEST_EMAIL) && password.equals(TEST_PASSWORD)) {
            navigateToMainApp();
        } else {
            Toast.makeText(getContext(), "Invalid email or password", Toast.LENGTH_SHORT).show(); // ❌ Show error
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


        Toast.makeText(getContext(), "Registration successful", Toast.LENGTH_SHORT).show();


        viewFlipper.setDisplayedChild(0);


        EditText loginEmailField = getView().findViewById(R.id.editTextLoginEmail);
        loginEmailField.setText(email);
    }

    private void navigateToMainApp() {

        Intent intent;
        if ("donor".equals(userType)) {
            intent = new Intent(getActivity(), DonorDashboardActivity.class);
        } else {
            intent = new Intent(getActivity(), RecipientDashboardActivity.class);
        }

        intent.putExtra("USER_TYPE", userType);
        startActivity(intent);
        getActivity().finish();
    }
}