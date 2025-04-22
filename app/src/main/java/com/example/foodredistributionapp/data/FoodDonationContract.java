package com.example.foodredistributionapp.data;
import android.provider.BaseColumns;

public final class FoodDonationContract {

    // Private constructor to prevent instantiation
    private FoodDonationContract() {}

    // Users table (both donors and recipients)
    public static final class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_USER_TYPE = "user_type"; // "donor" or "recipient"
        public static final String COLUMN_CREATED_AT = "created_at";
    }

    // Food listings table (donations)
    public static final class FoodListingEntry implements BaseColumns {
        public static final String TABLE_NAME = "food_listings";
        public static final String COLUMN_DONOR_ID = "donor_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_EXPIRY_DATE = "expiry_date";
        public static final String COLUMN_PICKUP_ADDRESS = "pickup_address";
        public static final String COLUMN_STATUS = "status"; // "available", "reserved", "completed"
        public static final String COLUMN_IMAGE_PATH = "image_path";
        public static final String COLUMN_CREATED_AT = "created_at";
    }

    // Reservations table (when a recipient reserves food)
    public static final class ReservationEntry implements BaseColumns {
        public static final String TABLE_NAME = "reservations";
        public static final String COLUMN_LISTING_ID = "listing_id";
        public static final String COLUMN_RECIPIENT_ID = "recipient_id";
        public static final String COLUMN_STATUS = "status"; // "pending", "confirmed", "completed", "cancelled"
        public static final String COLUMN_PICKUP_TIME = "pickup_time";
        public static final String COLUMN_CREATED_AT = "created_at";
    }
}