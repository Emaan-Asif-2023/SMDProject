package com.example.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import java.util.ArrayList;

public class SavedDatabase {
    static final String DATABASE_NAME = "SavedDB";
    static final int DATABASE_VERSION = 1;
    static final String TABLE_SAVED = "saved_hotels";
    static final String COL_SAVED_ID = "saved_id";
    static final String COL_PERSON_ID = "person_id";
    static final String COL_HOTEL_ID = "hotel_id";

    Context context;
    SavedDBHelper helper;

    public SavedDatabase(Context context) {
        this.context = context;
    }

    public void open() {
        helper = new SavedDBHelper(context);
    }

    public long saveHotel(int personId, int hotelId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PERSON_ID, personId);
        cv.put(COL_HOTEL_ID, hotelId);
        long id = db.insert(TABLE_SAVED, null, cv);
        db.close();
        return id;
    }

    public int removeSavedHotel(int personId, int hotelId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int count = db.delete(TABLE_SAVED,
                COL_PERSON_ID + "=? AND " + COL_HOTEL_ID + "=?",
                new String[]{String.valueOf(personId), String.valueOf(hotelId)});
        db.close();
        return count;
    }

    public boolean isHotelSaved(int personId, int hotelId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SAVED, null,
                COL_PERSON_ID + "=? AND " + COL_HOTEL_ID + "=?",
                new String[]{String.valueOf(personId), String.valueOf(hotelId)},
                null, null, null);
        boolean saved = cursor != null && cursor.moveToFirst();
        if (cursor != null) cursor.close();
        db.close();
        return saved;
    }

    public ArrayList<Hotel> getSavedHotels(int personId, Database hotelDb) {
        ArrayList<Hotel> savedHotels = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SAVED, null,
                COL_PERSON_ID + "=?",
                new String[]{String.valueOf(personId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int hotelId = cursor.getInt(cursor.getColumnIndex(COL_HOTEL_ID));
                // Get hotel details from main database
                ArrayList<Hotel> allHotels = hotelDb.getAllHotels();
                for (Hotel hotel : allHotels) {
                    if (hotel.getId() == hotelId) {
                        savedHotels.add(hotel);
                        break;
                    }
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return savedHotels;
    }

    public void close() {
        if (helper != null) {
            helper.close();
        }
    }

    private class SavedDBHelper extends SQLiteOpenHelper {
        public SavedDBHelper(@Nullable Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_SAVED + "("
                    + COL_SAVED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_PERSON_ID + " INTEGER, "
                    + COL_HOTEL_ID + " INTEGER, "
                    + "FOREIGN KEY(" + COL_PERSON_ID + ") REFERENCES persons(person_id), "
                    + "FOREIGN KEY(" + COL_HOTEL_ID + ") REFERENCES hotels(hotel_id))");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVED);
            onCreate(db);
        }
    }
}