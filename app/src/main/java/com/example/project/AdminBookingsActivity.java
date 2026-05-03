package com.example.project;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class AdminBookingsActivity extends AppCompatActivity {

    private Database db;
    private ListView listViewBookings;
    private Button btnBack;
    private ArrayList<Booking> bookingsList;
    private ArrayList<String> displayList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list);

        db = new Database(this);
        db.open();

        listViewBookings = findViewById(R.id.listView);
        btnBack = findViewById(R.id.btnBack);
        TextView titleText = findViewById(R.id.titleText);
        titleText.setText("Manage Bookings");

        displayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        listViewBookings.setAdapter(adapter);

        loadAllBookings();

        listViewBookings.setOnItemClickListener((parent, view, position, id) -> {
            showBookingOptions(position);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadAllBookings() {
        bookingsList = db.getAllBookings();
        displayList.clear();

        if (bookingsList.isEmpty()) {
            displayList.add("No bookings found");
        } else {
            for (Booking booking : bookingsList) {
                String hotelName = booking.getHotelName() != null ? booking.getHotelName() : "Unknown Hotel";
                String roomNum = booking.getRoomNumber() != null ? booking.getRoomNumber() : "N/A";
                String checkIn = booking.getCheckIn() != null ? booking.getCheckIn() : "N/A";
                String checkOut = booking.getCheckOut() != null ? booking.getCheckOut() : "N/A";
                String personName = booking.getPersonName() != null ? booking.getPersonName() : "Unknown User";

                String display = hotelName + " - Room " + roomNum +
                        " (" + checkIn + " to " + checkOut + ") - " + personName;
                displayList.add(display);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void showBookingDetails(Booking booking) {
        String hotelName = booking.getHotelName() != null ? booking.getHotelName() : "N/A";
        String roomNum = booking.getRoomNumber() != null ? booking.getRoomNumber() : "N/A";
        String roomType = booking.getRoomType() != null ? booking.getRoomType() : "N/A";
        String checkIn = booking.getCheckIn() != null ? booking.getCheckIn() : "N/A";
        String checkOut = booking.getCheckOut() != null ? booking.getCheckOut() : "N/A";
        String personName = booking.getPersonName() != null ? booking.getPersonName() : "N/A";
        String personEmail = booking.getPersonEmail() != null ? booking.getPersonEmail() : "N/A";

        String details = "Hotel: " + hotelName + "\n" +
                "Room: " + roomNum + " (" + roomType + ")\n" +
                "Check-in: " + checkIn + "\n" +
                "Check-out: " + checkOut + "\n" +
                "Guest: " + personName + "\n" +
                "Email: " + personEmail;

        new AlertDialog.Builder(this)
                .setTitle("Booking Details")
                .setMessage(details)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showBookingOptions(int position) {
        Booking booking = bookingsList.get(position);
        String[] options = {"View Details", "Delete Booking", "Cancel"};

        new AlertDialog.Builder(this)
                .setTitle("Booking Options")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            showBookingDetails(booking);
                            break;
                        case 1:
                            deleteBooking(booking);
                            break;
                    }
                })
                .show();
    }

    private void deleteBooking(Booking booking) {
        Toast.makeText(this, "Delete booking functionality coming soon", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}