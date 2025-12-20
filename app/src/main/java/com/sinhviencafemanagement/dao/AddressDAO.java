package com.sinhviencafemanagement.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sinhviencafemanagement.database.CreateDatabase;
import com.sinhviencafemanagement.database.DatabaseManager;
import com.sinhviencafemanagement.models.Address;

import java.util.ArrayList;
import java.util.List;

public class AddressDAO {
    private final SQLiteDatabase db;

    public AddressDAO(Context context) {
        this.db = DatabaseManager.getDatabase(context);
    }

    /**
     * Adds a new address to the database using the simple structure.
     * @param address The Address object to be added.
     * @return true if insertion is successful, false otherwise.
     */
    public boolean addAddress(Address address) {
        if (address == null) return false;

        // In this version, we combine all address parts into one string.
        // We assume the Address object has a method like getFullAddressDetails()
        // or we construct it here. For simplicity, we'll use getAddress().

        ContentValues values = new ContentValues();
        values.put(CreateDatabase.COLUMN_ADDRESS_USER_ID, address.getUserId());

        // The 'address' column will store the combined address string
        values.put(CreateDatabase.COLUMN_ADDRESS_ADDRESS, address.getAddress());

        try {
            long result = db.insert(CreateDatabase.TABLE_ADDRESSES, null, values);
            return result != -1;
        } catch (Exception e) {
            Log.e("AddressDAO", "Error adding address", e);
            return false;
        }
    }

    /**
     * Retrieves all addresses for a specific user from the simple structure.
     * @param userId The ID of the user whose addresses are to be fetched.
     * @return A list of Address objects.
     */
    @SuppressLint("Range")
    public List<Address> getAddressesByUserId(int userId) {
        List<Address> addressList = new ArrayList<>();
        String selection = CreateDatabase.COLUMN_ADDRESS_USER_ID + " = ?";
        String[] selectionArgs = { String.valueOf(userId) };

        try (Cursor cursor = db.query(
                CreateDatabase.TABLE_ADDRESSES,
                null,
                selection,
                selectionArgs,
                null,
                null,
                CreateDatabase.COLUMN_ADDRESS_ID + " DESC" // Show newest addresses first
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Create an Address object using the simple constructor
                    int addressId = cursor.getInt(cursor.getColumnIndex(CreateDatabase.COLUMN_ADDRESS_ID));
                    String fullAddress = cursor.getString(cursor.getColumnIndex(CreateDatabase.COLUMN_ADDRESS_ADDRESS));

                    Address address = new Address(addressId, fullAddress, userId);
                    addressList.add(address);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("AddressDAO", "Error getting addresses by user ID", e);
        }
        return addressList;
    }

    /**
     * Retrieves a single address by its ID from the simple structure.
     * @param addressId The ID of the address.
     * @return The Address object, or null if not found.
     */
    @SuppressLint("Range")
    public Address getAddressById(int addressId) {
        Address address = null;
        String selection = CreateDatabase.COLUMN_ADDRESS_ID + " = ?";
        String[] selectionArgs = { String.valueOf(addressId) };

        try (Cursor cursor = db.query(
                CreateDatabase.TABLE_ADDRESSES,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                int userId = cursor.getInt(cursor.getColumnIndex(CreateDatabase.COLUMN_ADDRESS_USER_ID));
                String fullAddress = cursor.getString(cursor.getColumnIndex(CreateDatabase.COLUMN_ADDRESS_ADDRESS));

                address = new Address(addressId, fullAddress, userId);

            }
        } catch (Exception e) {
            Log.e("AddressDAO", "Error getting address by ID", e);
        }
        return address;
    }
}
