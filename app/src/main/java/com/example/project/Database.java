package com.example.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;


public class Database {

    static final String DATABASE_NAME = "HotelDB";
    static final int DATABASE_VERSION = 1;
    static final String TABLE_PERSON = "persons";
    static final String COLUMN_ID = "person_id";
    static final String COLUMN_NAME = "person_name";
    static final String COLUMN_EMAIL = "person_email";
    static final String COLUMN_PASSWORD = "person_password";

    Context context;
    DBHelper helper;


    public Database(Context context) {
        this.context = context;
    }

    public void open() {
        helper = new DBHelper(context);
    }

    public long insertPerson(Person person) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, person.getName());
        cv.put(COLUMN_EMAIL, person.getEmail());
        cv.put(COLUMN_PASSWORD, person.getPassword());
        long id = db.insert(TABLE_PERSON, null, cv);
        db.close();
        return id;
    }

    public void insertTestData() {
        insertPerson(new Person("John Doe", "john@test.com", "123456"));
        insertPerson(new Person("Jane Smith", "jane@test.com", "password"));
        insertPerson(new Person("Admin", "admin@test.com", "admin123"));
    }

    public Person getPersonById(int id) {
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PERSON,
                null,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);

        Person person = null;

        if (cursor != null && cursor.moveToFirst()) {
            int index_name = cursor.getColumnIndex(COLUMN_NAME);
            int index_email = cursor.getColumnIndex(COLUMN_EMAIL);
            int index_password = cursor.getColumnIndex(COLUMN_PASSWORD);

            person = new Person();
            person.setId(id);
            person.setName(cursor.getString(index_name));
            person.setEmail(cursor.getString(index_email));
            person.setPassword(cursor.getString(index_password));
            cursor.close();
        }
        db.close();
        return person;
    }

    public ArrayList<Person> getAllPersons() {
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PERSON,
                null, null, null, null, null, null);

        ArrayList<Person> personArrayList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int index_name = cursor.getColumnIndex(COLUMN_NAME);
                int index_id = cursor.getColumnIndex(COLUMN_ID);
                int index_email = cursor.getColumnIndex(COLUMN_EMAIL);
                int index_password = cursor.getColumnIndex(COLUMN_PASSWORD);

                Person person = new Person();
                person.setId(cursor.getInt(index_id));
                person.setName(cursor.getString(index_name));
                person.setEmail(cursor.getString(index_email));
                person.setPassword(cursor.getString(index_password));
                personArrayList.add(person);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        return personArrayList;
    }

    public int update(Person person) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, person.getName());
        cv.put(COLUMN_EMAIL, person.getEmail());
        cv.put(COLUMN_PASSWORD, person.getPassword());
        int id = db.update(TABLE_PERSON,
                cv,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(person.getId())});
        db.close();
        return id;
    }

    public int deletePerson(int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int count = db.delete(TABLE_PERSON, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
        return count;
    }

    public void close() {
        helper.close();
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PERSON,
                null,
                COLUMN_EMAIL + "=?",
                new String[]{email},
                null, null, null);

        boolean exists = (cursor != null && cursor.moveToFirst());

        if (cursor != null) cursor.close();
        db.close();

        return exists;
    }

    public Person login(String email, String password) {
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PERSON,
                null,
                COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{email, password},
                null, null, null);

        Person person = null;

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int nameIndex = cursor.getColumnIndex(COLUMN_NAME);

            person = new Person();
            person.setId(cursor.getInt(idIndex));
            person.setName(cursor.getString(nameIndex));
            person.setEmail(email);
            person.setPassword(password);

            cursor.close();
        }

        db.close();
        return person;
    }

    // ✅ NEW: Check if table is empty (for test data)
    public boolean isTableEmpty() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PERSON, null, null, null, null, null, null);
        boolean empty = (cursor == null || cursor.getCount() == 0);
        if (cursor != null) cursor.close();
        db.close();
        return empty;
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(@Nullable Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            String createTable = "CREATE TABLE " + TABLE_PERSON + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_NAME + " TEXT, "
                    + COLUMN_EMAIL + " TEXT, "
                    + COLUMN_PASSWORD + " TEXT)";
            sqLiteDatabase.execSQL(createTable);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PERSON);
            onCreate(sqLiteDatabase);
        }
    }
}