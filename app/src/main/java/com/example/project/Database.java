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
    static final int DATABASE_VERSION = 5;

    static final String TABLE_PERSON = "persons";
    static final String COLUMN_ID = "person_id";
    static final String COLUMN_NAME = "person_name";
    static final String COLUMN_EMAIL = "person_email";
    static final String COLUMN_PASSWORD = "person_password";
    static final String COLUMN_ROLE = "person_role";

    static final String TABLE_HOTEL = "hotels";
    static final String COL_HOTEL_ID = "hotel_id";
    static final String COL_HOTEL_NAME = "hotel_name";
    static final String COL_HOTEL_LOCATION = "hotel_location";
    static final String COL_HOTEL_DESC = "hotel_description";
    static final String COL_HOTEL_IMAGE = "hotel_image_res";

    static final String TABLE_ROOM = "rooms";
    static final String COL_ROOM_ID = "room_id";
    static final String COL_ROOM_NUMBER = "room_number";
    static final String COL_ROOM_TYPE = "room_type";
    static final String COL_ROOM_PRICE = "room_price";
    static final String COL_ROOM_HOTEL_ID = "hotel_id";

    static final String TABLE_BOOKING = "bookings";
    static final String COL_BOOKING_ID = "booking_id";
    static final String COL_BOOKING_CHECK_IN = "check_in_date";
    static final String COL_BOOKING_CHECK_OUT = "check_out_date";
    static final String COL_BOOKING_PERSON_ID = "person_id";
    static final String COL_BOOKING_ROOM_ID = "room_id";

    Context context;
    DBHelper helper;

    public Database(Context context) {
        this.context = context;
    }

    public void open() {
        helper = new DBHelper(context);
    }

    // ==========================================
    // PERSON METHODS
    // ==========================================
    public long insertPerson(Person person) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, person.getName());
        cv.put(COLUMN_EMAIL, person.getEmail());
        cv.put(COLUMN_PASSWORD, person.getPassword());
        cv.put(COLUMN_ROLE, person.getRole() != null ? person.getRole() : "user");
        long id = db.insert(TABLE_PERSON, null, cv);
        db.close();
        return id;
    }

    public Person getPersonById(int id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PERSON, null, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        Person person = null;
        if (cursor != null && cursor.moveToFirst()) {
            person = new Person();
            person.setId(id);
            int indexName = cursor.getColumnIndex(COLUMN_NAME);
            int indexEmail = cursor.getColumnIndex(COLUMN_EMAIL);
            int indexPass = cursor.getColumnIndex(COLUMN_PASSWORD);
            int indexRole = cursor.getColumnIndex(COLUMN_ROLE);
            if (indexName >= 0) person.setName(cursor.getString(indexName));
            if (indexEmail >= 0) person.setEmail(cursor.getString(indexEmail));
            if (indexPass >= 0) person.setPassword(cursor.getString(indexPass));
            if (indexRole >= 0) person.setRole(cursor.getString(indexRole));
            cursor.close();
        }
        db.close();
        return person;
    }

    public Person login(String email, String password) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PERSON, null, COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?", new String[]{email, password}, null, null, null);
        Person person = null;
        if (cursor != null && cursor.moveToFirst()) {
            person = new Person();
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
            int roleIndex = cursor.getColumnIndex(COLUMN_ROLE);
            if (idIndex >= 0) person.setId(cursor.getInt(idIndex));
            if (nameIndex >= 0) person.setName(cursor.getString(nameIndex));
            if (roleIndex >= 0) person.setRole(cursor.getString(roleIndex));
            else person.setRole("user");
            person.setEmail(email);
            person.setPassword(password);
            cursor.close();
        }
        db.close();
        return person;
    }

    public ArrayList<Person> getAllPersons() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PERSON, null, null, null, null, null, null);
        ArrayList<Person> personArrayList = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Person person = new Person();
                int indexId = cursor.getColumnIndex(COLUMN_ID);
                int indexName = cursor.getColumnIndex(COLUMN_NAME);
                int indexEmail = cursor.getColumnIndex(COLUMN_EMAIL);
                int indexPass = cursor.getColumnIndex(COLUMN_PASSWORD);
                int indexRole = cursor.getColumnIndex(COLUMN_ROLE);
                if (indexId >= 0) person.setId(cursor.getInt(indexId));
                if (indexName >= 0) person.setName(cursor.getString(indexName));
                if (indexEmail >= 0) person.setEmail(cursor.getString(indexEmail));
                if (indexPass >= 0) person.setPassword(cursor.getString(indexPass));
                if (indexRole >= 0) person.setRole(cursor.getString(indexRole));
                else person.setRole("user");
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
        cv.put(COLUMN_ROLE, person.getRole());
        int rows = db.update(TABLE_PERSON, cv, COLUMN_ID + "=?", new String[]{String.valueOf(person.getId())});
        db.close();
        return rows;
    }

    public int deletePerson(int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int count = db.delete(TABLE_PERSON, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return count;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PERSON, null, COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
        boolean exists = (cursor != null && cursor.moveToFirst());
        if (cursor != null) cursor.close();
        db.close();
        return exists;
    }

    public boolean isTableEmpty() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PERSON, null, null, null, null, null, null);
        boolean empty = (cursor == null || cursor.getCount() == 0);
        if (cursor != null) cursor.close();
        db.close();
        return empty;
    }

    public boolean isAdmin(String email) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PERSON, null, COLUMN_EMAIL + "=? AND " + COLUMN_ROLE + "=?", new String[]{email, "admin"}, null, null, null);
        boolean isAdmin = (cursor != null && cursor.moveToFirst());
        if (cursor != null) cursor.close();
        db.close();
        return isAdmin;
    }

    public boolean updateUserRole(int userId, String role) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ROLE, role);
        int rows = db.update(TABLE_PERSON, cv, COLUMN_ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
        return rows > 0;
    }

    // ==========================================
    // HOTEL METHODS
    // ==========================================
    public long insertHotel(Hotel hotel) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_HOTEL_NAME, hotel.getName());
        cv.put(COL_HOTEL_LOCATION, hotel.getLocation());
        cv.put(COL_HOTEL_DESC, hotel.getDescription());

        try {
            cv.put(COL_HOTEL_IMAGE, hotel.getImageResId());
        } catch (Exception e) {
            cv.put(COL_HOTEL_IMAGE, 0);
        }

        long id = db.insert(TABLE_HOTEL, null, cv);
        db.close();
        return id;
    }

    public ArrayList<Hotel> getAllHotels() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HOTEL, null, null, null, null, null, COL_HOTEL_NAME + " ASC");
        ArrayList<Hotel> list = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Hotel h = new Hotel();
                int idxId = cursor.getColumnIndex(COL_HOTEL_ID);
                int idxName = cursor.getColumnIndex(COL_HOTEL_NAME);
                int idxLoc = cursor.getColumnIndex(COL_HOTEL_LOCATION);
                int idxDesc = cursor.getColumnIndex(COL_HOTEL_DESC);
                int idxImg = cursor.getColumnIndex(COL_HOTEL_IMAGE);
                if (idxId >= 0) h.setId(cursor.getInt(idxId));
                if (idxName >= 0) h.setName(cursor.getString(idxName));
                if (idxLoc >= 0) h.setLocation(cursor.getString(idxLoc));
                if (idxDesc >= 0) h.setDescription(cursor.getString(idxDesc));
                if (idxImg >= 0) h.setImageResId(cursor.getInt(idxImg));
                list.add(h);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return list;
    }

    public ArrayList<Hotel> getHotelsByLocation(String location) {
        ArrayList<Hotel> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HOTEL, null, COL_HOTEL_LOCATION + " LIKE ?", new String[]{"%" + location + "%"}, null, null, COL_HOTEL_NAME + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Hotel h = new Hotel();
                int idxId = cursor.getColumnIndex(COL_HOTEL_ID);
                int idxName = cursor.getColumnIndex(COL_HOTEL_NAME);
                int idxLoc = cursor.getColumnIndex(COL_HOTEL_LOCATION);
                int idxDesc = cursor.getColumnIndex(COL_HOTEL_DESC);
                int idxImg = cursor.getColumnIndex(COL_HOTEL_IMAGE);
                if (idxId >= 0) h.setId(cursor.getInt(idxId));
                if (idxName >= 0) h.setName(cursor.getString(idxName));
                if (idxLoc >= 0) h.setLocation(cursor.getString(idxLoc));
                if (idxDesc >= 0) h.setDescription(cursor.getString(idxDesc));
                if (idxImg >= 0) h.setImageResId(cursor.getInt(idxImg));
                list.add(h);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return list;
    }

    public int deleteHotel(int hotelId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int count = db.delete(TABLE_HOTEL, COL_HOTEL_ID + "=?", new String[]{String.valueOf(hotelId)});
        db.close();
        return count;
    }

    public int updateHotel(Hotel hotel) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_HOTEL_NAME, hotel.getName());
        cv.put(COL_HOTEL_LOCATION, hotel.getLocation());
        cv.put(COL_HOTEL_DESC, hotel.getDescription());
        int rows = db.update(TABLE_HOTEL, cv, COL_HOTEL_ID + "=?", new String[]{String.valueOf(hotel.getId())});
        db.close();
        return rows;
    }

    // ==========================================
    // ROOM METHODS
    // ==========================================
    public long insertRoom(Room room) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_ROOM_NUMBER, room.getRoomNumber());
        cv.put(COL_ROOM_TYPE, room.getType());
        cv.put(COL_ROOM_PRICE, room.getPrice());
        cv.put(COL_ROOM_HOTEL_ID, room.getHotelId());
        long id = db.insert(TABLE_ROOM, null, cv);
        db.close();
        return id;
    }

    public ArrayList<Room> getRoomsByHotel(int hotelId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ROOM, null, COL_ROOM_HOTEL_ID + "=?", new String[]{String.valueOf(hotelId)}, null, null, COL_ROOM_NUMBER + " ASC");
        ArrayList<Room> list = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Room r = new Room();
                int idxId = cursor.getColumnIndex(COL_ROOM_ID);
                int idxNum = cursor.getColumnIndex(COL_ROOM_NUMBER);
                int idxType = cursor.getColumnIndex(COL_ROOM_TYPE);
                int idxPrice = cursor.getColumnIndex(COL_ROOM_PRICE);
                int idxHotelId = cursor.getColumnIndex(COL_ROOM_HOTEL_ID);
                if (idxId >= 0) r.setId(cursor.getInt(idxId));
                if (idxNum >= 0) r.setRoomNumber(cursor.getString(idxNum));
                if (idxType >= 0) r.setType(cursor.getString(idxType));
                if (idxPrice >= 0) r.setPrice(cursor.getDouble(idxPrice));
                if (idxHotelId >= 0) r.setHotelId(cursor.getInt(idxHotelId));
                list.add(r);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return list;
    }

    // ==========================================
    // BOOKING METHODS
    // ==========================================
    public long insertBooking(Booking booking) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_BOOKING_CHECK_IN, booking.getCheckIn());
        cv.put(COL_BOOKING_CHECK_OUT, booking.getCheckOut());
        cv.put(COL_BOOKING_PERSON_ID, booking.getPersonId());
        cv.put(COL_BOOKING_ROOM_ID, booking.getRoomId());
        long id = db.insert(TABLE_BOOKING, null, cv);
        db.close();
        return id;
    }

    public ArrayList<Booking> getBookingsByUser(int personId) {
        ArrayList<Booking> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String query = "SELECT b.*, r.room_number, r.room_type, h.hotel_name, p.person_name " +
                "FROM " + TABLE_BOOKING + " b " +
                "INNER JOIN " + TABLE_ROOM + " r ON b." + COL_BOOKING_ROOM_ID + " = r." + COL_ROOM_ID + " " +
                "INNER JOIN " + TABLE_HOTEL + " h ON r." + COL_ROOM_HOTEL_ID + " = h." + COL_HOTEL_ID + " " +
                "INNER JOIN " + TABLE_PERSON + " p ON b." + COL_BOOKING_PERSON_ID + " = p." + COLUMN_ID + " " +
                "WHERE b." + COL_BOOKING_PERSON_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(personId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Booking b = new Booking();

                int idxId = cursor.getColumnIndex(COL_BOOKING_ID);
                int idxIn = cursor.getColumnIndex(COL_BOOKING_CHECK_IN);
                int idxOut = cursor.getColumnIndex(COL_BOOKING_CHECK_OUT);
                int idxPid = cursor.getColumnIndex(COL_BOOKING_PERSON_ID);
                int idxRid = cursor.getColumnIndex(COL_BOOKING_ROOM_ID);
                int idxHname = cursor.getColumnIndex("hotel_name");
                int idxRnum = cursor.getColumnIndex("room_number");
                int idxRtype = cursor.getColumnIndex("room_type");
                int idxPname = cursor.getColumnIndex("person_name");

                if (idxId >= 0) b.setId(cursor.getInt(idxId));
                if (idxIn >= 0) b.setCheckIn(cursor.getString(idxIn));
                if (idxOut >= 0) b.setCheckOut(cursor.getString(idxOut));
                if (idxPid >= 0) b.setPersonId(cursor.getInt(idxPid));
                if (idxRid >= 0) b.setRoomId(cursor.getInt(idxRid));
                if (idxHname >= 0) b.setHotelName(cursor.getString(idxHname));
                if (idxRnum >= 0) b.setRoomNumber(cursor.getString(idxRnum));
                if (idxRtype >= 0) b.setRoomType(cursor.getString(idxRtype));
                if (idxPname >= 0) b.setPersonName(cursor.getString(idxPname));

                list.add(b);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return list;
    }

    public ArrayList<Booking> getAllBookings() {
        ArrayList<Booking> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        // Use explicit column names and aliases to avoid confusion
        String query = "SELECT " +
                "b." + COL_BOOKING_ID + " as booking_id, " +
                "b." + COL_BOOKING_CHECK_IN + " as check_in, " +
                "b." + COL_BOOKING_CHECK_OUT + " as check_out, " +
                "b." + COL_BOOKING_PERSON_ID + " as person_id, " +
                "b." + COL_BOOKING_ROOM_ID + " as room_id, " +
                "r." + COL_ROOM_NUMBER + " as room_number, " +
                "r." + COL_ROOM_TYPE + " as room_type, " +
                "h." + COL_HOTEL_NAME + " as hotel_name, " +
                "p." + COLUMN_NAME + " as person_name, " +
                "p." + COLUMN_EMAIL + " as person_email " +
                "FROM " + TABLE_BOOKING + " b " +
                "INNER JOIN " + TABLE_ROOM + " r ON b." + COL_BOOKING_ROOM_ID + " = r." + COL_ROOM_ID + " " +
                "INNER JOIN " + TABLE_HOTEL + " h ON r." + COL_ROOM_HOTEL_ID + " = h." + COL_HOTEL_ID + " " +
                "INNER JOIN " + TABLE_PERSON + " p ON b." + COL_BOOKING_PERSON_ID + " = p." + COLUMN_ID + " " +
                "ORDER BY b." + COL_BOOKING_ID + " DESC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Booking b = new Booking();

                // Use column names from the query aliases
                b.setId(getIntFromCursor(cursor, "booking_id"));
                b.setCheckIn(getStringFromCursor(cursor, "check_in"));
                b.setCheckOut(getStringFromCursor(cursor, "check_out"));
                b.setPersonId(getIntFromCursor(cursor, "person_id"));
                b.setRoomId(getIntFromCursor(cursor, "room_id"));
                b.setHotelName(getStringFromCursor(cursor, "hotel_name"));
                b.setRoomNumber(getStringFromCursor(cursor, "room_number"));
                b.setRoomType(getStringFromCursor(cursor, "room_type"));
                b.setPersonName(getStringFromCursor(cursor, "person_name"));
                b.setPersonEmail(getStringFromCursor(cursor, "person_email"));

                list.add(b);

                android.util.Log.d("BOOKING_DEBUG",
                        "Booking ID: " + b.getId() +
                                ", Guest: " + b.getPersonName() +
                                ", Hotel: " + b.getHotelName());

            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return list;
    }

    private String getStringFromCursor(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if (index >= 0 && !cursor.isNull(index)) {
            return cursor.getString(index);
        }
        return "N/A";
    }

    private int getIntFromCursor(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if (index >= 0 && !cursor.isNull(index)) {
            return cursor.getInt(index);
        }
        return 0;
    }

    public int deleteBooking(int bookingId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int count = db.delete(TABLE_BOOKING, COL_BOOKING_ID + "=?", new String[]{String.valueOf(bookingId)});
        db.close();
        return count;
    }

    public void close() {
        if (helper != null) {
            helper.close();
        }
    }
    public void insertTestData() {
        Person admin = new Person("Admin", "admin@test.com", "admin12345");
        admin.setRole("admin");
        insertPerson(admin);

        insertPerson(new Person("John Doe", "john@test.com", "12345678"));
        insertPerson(new Person("Jane Smith", "jane@test.com", "password123"));

        insertHotel(new Hotel("Grand Plaza", "New York", "A luxury 5-star experience in the city center.", android.R.drawable.ic_menu_gallery));
        insertHotel(new Hotel("The Central Inn", "New York", "Affordable comfort near Times Square.", android.R.drawable.ic_menu_camera));

        insertHotel(new Hotel("Sunset Resort", "Los Angeles", "Beautiful beachfront property with ocean views.", android.R.drawable.ic_menu_mapmode));
        insertHotel(new Hotel("LAX Airport Hotel", "Los Angeles", "Convenient stay right next to the airport.", android.R.drawable.ic_menu_gallery));

        insertHotel(new Hotel("Ocean Breeze Resort", "Miami", "Stunning Art Deco hotel on South Beach.", android.R.drawable.ic_menu_camera));
        insertHotel(new Hotel("Downtown Miami Hub", "Miami", "Modern rooms in the heart of the financial district.", android.R.drawable.ic_menu_mapmode));

        insertHotel(new Hotel("The Windy Tower", "Chicago", "Luxury suites overlooking Lake Michigan.", android.R.drawable.ic_menu_gallery));
        insertHotel(new Hotel("Bean City Lodge", "Chicago", "Cozy boutique hotel near Millennium Park.", android.R.drawable.ic_menu_camera));

        insertHotel(new Hotel("Mountain View Inn", "Denver", "Cozy rooms located near the Rocky Mountains.", android.R.drawable.ic_menu_mapmode));
        insertHotel(new Hotel("Mile High Suites", "Denver", "Spacious suites perfect for families.", android.R.drawable.ic_menu_gallery));


        insertRoom(new Room("101", "Single", 120.00, 1));
        insertRoom(new Room("102", "Double", 180.00, 1));
        insertRoom(new Room("103", "Suite", 350.00, 1));

        insertRoom(new Room("201", "Single", 89.99, 2));
        insertRoom(new Room("202", "Double", 129.99, 2));
        insertRoom(new Room("203", "Double", 129.99, 2));

        insertRoom(new Room("101", "Ocean View Single", 150.00, 3));
        insertRoom(new Room("102", "Ocean View Double", 220.00, 3));
        insertRoom(new Room("103", "Presidential Suite", 500.00, 3));

        insertRoom(new Room("101", "Standard", 75.00, 4));
        insertRoom(new Room("102", "Standard", 75.00, 4));
        insertRoom(new Room("103", "Deluxe", 110.00, 4));

        insertRoom(new Room("501", "King Room", 200.00, 5));
        insertRoom(new Room("502", "Twin Room", 200.00, 5));
        insertRoom(new Room("503", "Penthouse", 800.00, 5));


        insertRoom(new Room("301", "Single", 95.00, 6));
        insertRoom(new Room("302", "Double", 140.00, 6));


        insertRoom(new Room("801", "City View Double", 190.00, 7));
        insertRoom(new Room("802", "Lake View Suite", 450.00, 7));
        insertRoom(new Room("803", "Lake View Suite", 450.00, 7));


        insertRoom(new Room("101", "Queen Room", 130.00, 8));
        insertRoom(new Room("102", "King Room", 160.00, 8));


        insertRoom(new Room("101", "Single", 99.99, 9));
        insertRoom(new Room("102", "Double", 149.99, 9));


        insertRoom(new Room("401", "Family Room", 180.00, 10));
        insertRoom(new Room("402", "Executive Suite", 300.00, 10));
        insertRoom(new Room("403", "Family Room", 180.00, 10));
        insertRoom(new Room("404", "Executive Suite", 300.00, 10));



        insertBooking(new Booking("2023-12-01", "2023-12-05", 2, 1)); // Jane Smith in Grand Plaza
        insertBooking(new Booking("2023-11-20", "2023-11-22", 1, 5)); // John Doe in Ocean Breeze
    }

    private class DBHelper extends SQLiteOpenHelper {
        public DBHelper(@Nullable Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_PERSON + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME + " TEXT, " + COLUMN_EMAIL + " TEXT, " + COLUMN_PASSWORD + " TEXT, " + COLUMN_ROLE + " TEXT DEFAULT 'user')");
            sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_HOTEL + "(" + COL_HOTEL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_HOTEL_NAME + " TEXT, " + COL_HOTEL_LOCATION + " TEXT, " + COL_HOTEL_DESC + " TEXT, " + COL_HOTEL_IMAGE + " INTEGER)");
            sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_ROOM + "(" + COL_ROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_ROOM_NUMBER + " TEXT, " + COL_ROOM_TYPE + " TEXT, " + COL_ROOM_PRICE + " REAL, " + COL_ROOM_HOTEL_ID + " INTEGER, FOREIGN KEY(" + COL_ROOM_HOTEL_ID + ") REFERENCES " + TABLE_HOTEL + "(" + COL_HOTEL_ID + "))");
            sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_BOOKING + "(" + COL_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_BOOKING_CHECK_IN + " TEXT, " + COL_BOOKING_CHECK_OUT + " TEXT, " + COL_BOOKING_PERSON_ID + " INTEGER, " + COL_BOOKING_ROOM_ID + " INTEGER, FOREIGN KEY(" + COL_BOOKING_PERSON_ID + ") REFERENCES " + TABLE_PERSON + "(" + COLUMN_ID + "), FOREIGN KEY(" + COL_BOOKING_ROOM_ID + ") REFERENCES " + TABLE_ROOM + "(" + COL_ROOM_ID + "))");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            if (oldVersion < 5) {
                try {
                    sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_PERSON + " ADD COLUMN " + COLUMN_ROLE + " TEXT DEFAULT 'user'");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ContentValues cv = new ContentValues();
                cv.put(COLUMN_ROLE, "admin");
                sqLiteDatabase.update(TABLE_PERSON, cv, COLUMN_EMAIL + "=?", new String[]{"admin@test.com"});
            }
        }
    }
}