package com.example.foodredistributionapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.foodredistributionapp.data.FoodDonationContract.UserEntry;
import com.example.foodredistributionapp.data.FoodDonationContract.FoodListingEntry;
import com.example.foodredistributionapp.data.FoodDonationContract.ReservationEntry;

public class FoodDonationDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "food_donation.db";
    private static final int DATABASE_VERSION = 1;

    public FoodDonationDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users table
        final String SQL_CREATE_USERS_TABLE = "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UserEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                UserEntry.COLUMN_EMAIL + " TEXT UNIQUE NOT NULL, " +
                UserEntry.COLUMN_PHONE + " TEXT NOT NULL, " +
                UserEntry.COLUMN_ADDRESS + " TEXT NOT NULL, " +
                UserEntry.COLUMN_PASSWORD + " TEXT NOT NULL, " +
                UserEntry.COLUMN_USER_TYPE + " TEXT NOT NULL, " +
                UserEntry.COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        // Create Food Listings table
        final String SQL_CREATE_FOOD_LISTINGS_TABLE = "CREATE TABLE " + FoodListingEntry.TABLE_NAME + " (" +
                FoodListingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FoodListingEntry.COLUMN_DONOR_ID + " INTEGER NOT NULL, " +
                FoodListingEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FoodListingEntry.COLUMN_DESCRIPTION + " TEXT, " +
                FoodListingEntry.COLUMN_QUANTITY + " TEXT NOT NULL, " +
                FoodListingEntry.COLUMN_EXPIRY_DATE + " TEXT, " +
                FoodListingEntry.COLUMN_PICKUP_ADDRESS + " TEXT NOT NULL, " +
                FoodListingEntry.COLUMN_STATUS + " TEXT NOT NULL DEFAULT 'available', " +
                FoodListingEntry.COLUMN_IMAGE_PATH + " TEXT, " +
                FoodListingEntry.COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (" + FoodListingEntry.COLUMN_DONOR_ID + ") REFERENCES " +
                UserEntry.TABLE_NAME + "(" + UserEntry._ID + ")" +
                ");";

        // Create Reservations table
        final String SQL_CREATE_RESERVATIONS_TABLE = "CREATE TABLE " + ReservationEntry.TABLE_NAME + " (" +
                ReservationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ReservationEntry.COLUMN_LISTING_ID + " INTEGER NOT NULL, " +
                ReservationEntry.COLUMN_RECIPIENT_ID + " INTEGER NOT NULL, " +
                ReservationEntry.COLUMN_STATUS + " TEXT NOT NULL DEFAULT 'pending', " +
                ReservationEntry.COLUMN_PICKUP_TIME + " TEXT, " +
                ReservationEntry.COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (" + ReservationEntry.COLUMN_LISTING_ID + ") REFERENCES " +
                FoodListingEntry.TABLE_NAME + "(" + FoodListingEntry._ID + "), " +
                "FOREIGN KEY (" + ReservationEntry.COLUMN_RECIPIENT_ID + ") REFERENCES " +
                UserEntry.TABLE_NAME + "(" + UserEntry._ID + ")" +
                ");";

        // Execute the SQL statements
        db.execSQL(SQL_CREATE_USERS_TABLE);
        db.execSQL(SQL_CREATE_FOOD_LISTINGS_TABLE);
        db.execSQL(SQL_CREATE_RESERVATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop all tables and recreate them
        db.execSQL("DROP TABLE IF EXISTS " + ReservationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FoodListingEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME);
        onCreate(db);
    }
}