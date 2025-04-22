package com.example.foodredistributionapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.foodredistributionapp.data.FoodDonationContract.UserEntry;
import com.example.foodredistributionapp.data.FoodDonationContract.FoodListingEntry;
import com.example.foodredistributionapp.data.FoodDonationContract.ReservationEntry;

public class FoodDonationDataManager {

    private SQLiteDatabase database;
    private FoodDonationDbHelper dbHelper;

    public FoodDonationDataManager(Context context) {
        dbHelper = new FoodDonationDbHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null) {
            database.close();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    // User methods
    public long insertUser(String name, String email, String phone, String address, String password, String userType) {
        ContentValues values = new ContentValues();
        values.put(UserEntry.COLUMN_NAME, name);
        values.put(UserEntry.COLUMN_EMAIL, email);
        values.put(UserEntry.COLUMN_PHONE, phone);
        values.put(UserEntry.COLUMN_ADDRESS, address);
        values.put(UserEntry.COLUMN_PASSWORD, password);
        values.put(UserEntry.COLUMN_USER_TYPE, userType);

        return database.insert(UserEntry.TABLE_NAME, null, values);
    }

    public Cursor getUserByEmail(String email) {
        String[] projection = {
                UserEntry._ID,
                UserEntry.COLUMN_NAME,
                UserEntry.COLUMN_EMAIL,
                UserEntry.COLUMN_PHONE,
                UserEntry.COLUMN_ADDRESS,
                UserEntry.COLUMN_PASSWORD,
                UserEntry.COLUMN_USER_TYPE
        };

        String selection = UserEntry.COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};

        return database.query(
                UserEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }

    // Food Listing methods
    public long insertFoodListing(long donorId, String title, String description,
                                  String quantity, String expiryDate, String pickupAddress,
                                  String imagePath) {
        ContentValues values = new ContentValues();
        values.put(FoodListingEntry.COLUMN_DONOR_ID, donorId);
        values.put(FoodListingEntry.COLUMN_TITLE, title);
        values.put(FoodListingEntry.COLUMN_DESCRIPTION, description);
        values.put(FoodListingEntry.COLUMN_QUANTITY, quantity);
        values.put(FoodListingEntry.COLUMN_EXPIRY_DATE, expiryDate);
        values.put(FoodListingEntry.COLUMN_PICKUP_ADDRESS, pickupAddress);
        values.put(FoodListingEntry.COLUMN_STATUS, "available");
        values.put(FoodListingEntry.COLUMN_IMAGE_PATH, imagePath);

        return database.insert(FoodListingEntry.TABLE_NAME, null, values);
    }

    public Cursor getAllAvailableFoodListings() {
        String[] projection = {
                FoodListingEntry._ID,
                FoodListingEntry.COLUMN_DONOR_ID,
                FoodListingEntry.COLUMN_TITLE,
                FoodListingEntry.COLUMN_DESCRIPTION,
                FoodListingEntry.COLUMN_QUANTITY,
                FoodListingEntry.COLUMN_EXPIRY_DATE,
                FoodListingEntry.COLUMN_PICKUP_ADDRESS,
                FoodListingEntry.COLUMN_STATUS,
                FoodListingEntry.COLUMN_IMAGE_PATH,
                FoodListingEntry.COLUMN_CREATED_AT
        };

        String selection = FoodListingEntry.COLUMN_STATUS + " = ?";
        String[] selectionArgs = {"available"};

        return database.query(
                FoodListingEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                FoodListingEntry.COLUMN_CREATED_AT + " DESC"
        );
    }

    public Cursor getDonorListings(long donorId) {
        String[] projection = {
                FoodListingEntry._ID,
                FoodListingEntry.COLUMN_TITLE,
                FoodListingEntry.COLUMN_DESCRIPTION,
                FoodListingEntry.COLUMN_QUANTITY,
                FoodListingEntry.COLUMN_EXPIRY_DATE,
                FoodListingEntry.COLUMN_PICKUP_ADDRESS,
                FoodListingEntry.COLUMN_STATUS,
                FoodListingEntry.COLUMN_IMAGE_PATH,
                FoodListingEntry.COLUMN_CREATED_AT
        };

        String selection = FoodListingEntry.COLUMN_DONOR_ID + " = ?";
        String[] selectionArgs = {String.valueOf(donorId)};

        return database.query(
                FoodListingEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                FoodListingEntry.COLUMN_CREATED_AT + " DESC"
        );
    }

    public int updateFoodListingStatus(long listingId, String status) {
        ContentValues values = new ContentValues();
        values.put(FoodListingEntry.COLUMN_STATUS, status);

        String selection = FoodListingEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(listingId)};

        return database.update(FoodListingEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    // Reservation methods
    public long createReservation(long listingId, long recipientId, String pickupTime) {
        ContentValues values = new ContentValues();
        values.put(ReservationEntry.COLUMN_LISTING_ID, listingId);
        values.put(ReservationEntry.COLUMN_RECIPIENT_ID, recipientId);
        values.put(ReservationEntry.COLUMN_PICKUP_TIME, pickupTime);
        values.put(ReservationEntry.COLUMN_STATUS, "pending");

        long reservationId = database.insert(ReservationEntry.TABLE_NAME, null, values);

        // Update the listing status
        if (reservationId != -1) {
            updateFoodListingStatus(listingId, "reserved");
        }

        return reservationId;
    }

    public Cursor getRecipientReservations(long recipientId) {
        String query = "SELECT r.*, f.title, f.description, f.quantity, f.expiry_date, " +
                "f.pickup_address, u.name as donor_name " +
                "FROM " + ReservationEntry.TABLE_NAME + " r " +
                "JOIN " + FoodListingEntry.TABLE_NAME + " f ON r." + ReservationEntry.COLUMN_LISTING_ID +
                " = f." + FoodListingEntry._ID + " " +
                "JOIN " + UserEntry.TABLE_NAME + " u ON f." + FoodListingEntry.COLUMN_DONOR_ID +
                " = u." + UserEntry._ID + " " +
                "WHERE r." + ReservationEntry.COLUMN_RECIPIENT_ID + " = ? " +
                "ORDER BY r." + ReservationEntry.COLUMN_CREATED_AT + " DESC";

        return database.rawQuery(query, new String[]{String.valueOf(recipientId)});
    }

    public Cursor getDonorReservations(long donorId) {
        String query = "SELECT r.*, f.title, f.description, f.quantity, f.expiry_date, " +
                "f.pickup_address, u.name as recipient_name " +
                "FROM " + ReservationEntry.TABLE_NAME + " r " +
                "JOIN " + FoodListingEntry.TABLE_NAME + " f ON r." + ReservationEntry.COLUMN_LISTING_ID +
                " = f." + FoodListingEntry._ID + " " +
                "JOIN " + UserEntry.TABLE_NAME + " u ON r." + ReservationEntry.COLUMN_RECIPIENT_ID +
                " = u." + UserEntry._ID + " " +
                "WHERE f." + FoodListingEntry.COLUMN_DONOR_ID + " = ? " +
                "ORDER BY r." + ReservationEntry.COLUMN_CREATED_AT + " DESC";

        return database.rawQuery(query, new String[]{String.valueOf(donorId)});
    }

    public int updateReservationStatus(long reservationId, String status) {
        ContentValues values = new ContentValues();
        values.put(ReservationEntry.COLUMN_STATUS, status);

        String selection = ReservationEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(reservationId)};

        int rowsUpdated = database.update(ReservationEntry.TABLE_NAME, values, selection, selectionArgs);

        // If the reservation is completed, also update the food listing
        if (rowsUpdated > 0 && "completed".equals(status)) {
            // Get the listing ID for this reservation
            Cursor cursor = database.query(
                    ReservationEntry.TABLE_NAME,
                    new String[]{ReservationEntry.COLUMN_LISTING_ID},
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                long listingId = cursor.getLong(cursor.getColumnIndex(ReservationEntry.COLUMN_LISTING_ID));
                updateFoodListingStatus(listingId, "completed");
                cursor.close();
            }
        }

        return rowsUpdated;
    }
}