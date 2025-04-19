package com.example.foodredistributionapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class UserTypeSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type_selection);


        CardView donorCard = findViewById(R.id.cardViewDonor);
        CardView recipientCard = findViewById(R.id.cardViewRecipient);
        TextView learnMoreTextView = findViewById(R.id.textViewLearnMore);
        Button continueButton = findViewById(R.id.buttonContinue);

        continueButton.setEnabled(false);

        final String[] selectedUserType = {null};

        donorCard.setOnClickListener(view -> {
            donorCard.setCardBackgroundColor(getResources().getColor(R.color.light_green, null));
            recipientCard.setCardBackgroundColor(getResources().getColor(R.color.white, null));
            selectedUserType[0] = "donor";
            continueButton.setEnabled(true);
        });

        recipientCard.setOnClickListener(view -> {
            recipientCard.setCardBackgroundColor(getResources().getColor(R.color.light_green, null));
            donorCard.setCardBackgroundColor(getResources().getColor(R.color.white, null));
            selectedUserType[0] = "recipient";
            continueButton.setEnabled(true);
        });

        learnMoreTextView.setOnClickListener(view -> {
            showAppInfoDialog();
        });

        continueButton.setOnClickListener(view -> {
            if (selectedUserType[0] != null) {
                loadAuthFragment(selectedUserType[0]);
            }
        });
    }

    private void loadAuthFragment(String userType) {
        AuthFragment authFragment = new AuthFragment();
        Bundle args = new Bundle();
        args.putString("USER_TYPE", userType);
        authFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, authFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showAppInfoDialog() {
        AppInfoDialog infoDialog = new AppInfoDialog();
        infoDialog.show(getSupportFragmentManager(), "AppInfoDialog");
    }
}