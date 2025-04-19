package com.example.foodredistributionapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DonationAdapter extends RecyclerView.Adapter<DonationAdapter.DonationViewHolder> {

    private List<Donation> donationList;

    public DonationAdapter(List<Donation> donationList) {
        this.donationList = donationList;
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
        holder.textViewDescription.setText(donation.getDescription());
        holder.textViewDate.setText("Date: " + donation.getDate());
        holder.textViewStatus.setText(donation.getStatus());


        switch (donation.getStatus()) {
            case "Pending":
                holder.textViewStatus.setTextColor(Color.parseColor("#FFA500")); // Orange
                break;
            case "Picked Up":
            case "Delivered":
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
        public TextView textViewDescription;
        public TextView textViewDate;
        public TextView textViewStatus;

        public DonationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view_donation);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            textViewStatus = itemView.findViewById(R.id.text_view_status);
        }
    }
}