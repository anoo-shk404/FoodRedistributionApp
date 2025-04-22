package com.example.foodredistributionapp.adapter;  // Change the package to match

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodredistributionapp.R;
import com.example.foodredistributionapp.model.Donation;  // Make sure this import is correct

import java.util.List;
public class DonationAdapter extends RecyclerView.Adapter<DonationAdapter.DonationViewHolder> {
    private List<Donation> donationList;
    private OnDonationClickListener listener;

    // Interface for click listener
    public interface OnDonationClickListener {
        void onDonationClick(Donation donation);
    }

    // Constructor with click listener
    public DonationAdapter(List<Donation> donationList, OnDonationClickListener listener) {
        this.donationList = donationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DonationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_donation, parent, false);
        return new DonationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonationViewHolder holder, int position) {
        Donation donation = donationList.get(position);

        holder.textViewTitle.setText(donation.getTitle());
        // Use quantity for the second line instead of description
        holder.textViewQuantity.setText(donation.getQuantity());
        holder.textViewDate.setText("Expires: " + donation.getExpiryDate());
        holder.textViewStatus.setText(donation.getStatus());

        // Set click listener
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDonationClick(donation);
            }
        });

        // Color status text based on status
        switch (donation.getStatus()) {
            case "Pending":
                holder.textViewStatus.setTextColor(Color.parseColor("#FFA500")); // Orange
                break;
            case "Reserved":
                holder.textViewStatus.setTextColor(Color.parseColor("#3F51B5")); // Indigo
                break;
            case "Completed":
                holder.textViewStatus.setTextColor(Color.parseColor("#008000")); // Green
                break;
            case "Expired":
                holder.textViewStatus.setTextColor(Color.parseColor("#FF0000")); // Red
                break;
            default:
                holder.textViewStatus.setTextColor(Color.parseColor("#000000")); // Black
        }
    }

    @Override
    public int getItemCount() {
        return donationList.size();
    }

    public static class DonationViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView textViewTitle;
        public TextView textViewQuantity;  // Renamed from textViewDescription to match model
        public TextView textViewDate;
        public TextView textViewStatus;

        public DonationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view_donation);
            textViewTitle = itemView.findViewById(R.id.text_view_donation_title);
            textViewQuantity = itemView.findViewById(R.id.text_view_donation_quantity);
            textViewDate = itemView.findViewById(R.id.text_view_donation_date);
            textViewStatus = itemView.findViewById(R.id.text_view_donation_status);
        }
    }
}