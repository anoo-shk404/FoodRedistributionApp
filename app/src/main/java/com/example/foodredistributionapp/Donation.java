package com.example.foodredistributionapp;

public class Donation {
    private String title;
    private String description;
    private String date;
    private String status;

    public Donation(String title, String description, String date, String status) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }
}