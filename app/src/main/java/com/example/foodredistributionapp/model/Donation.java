package com.example.foodredistributionapp.model;

public class Donation {
    private int id;
    private String title;
    private String description;
    private String quantity;
    private String expiryDate;
    private String status;
    private String pickupAddress;
    private String imagePath;

    // Constructor for list display
    public Donation(String title, String quantity, String expiryDate, String status) {
        this.title = title;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.status = status;
    }

    // Full constructor
    public Donation(int id, String title, String description, String quantity,
                    String expiryDate, String status, String pickupAddress, String imagePath) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.status = status;
        this.pickupAddress = pickupAddress;
        this.imagePath = imagePath;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getStatus() {
        return status;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public String getImagePath() {
        return imagePath;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}