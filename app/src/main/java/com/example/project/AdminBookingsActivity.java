package com.example.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
            if (bookingsList != null && position < bookingsList.size()) {
                showBookingOptions(position);
            }
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

                String display = "ID:" + booking.getId() + " | " + hotelName +
                        "\nRoom " + roomNum + " | " + checkIn + " → " + checkOut +
                        "\nGuest: " + personName;
                displayList.add(display);
            }
        }
        adapter.notifyDataSetChanged();

        if (bookingsList.isEmpty()) {
            Toast.makeText(this, "No bookings found", Toast.LENGTH_SHORT).show();
        }
    }

    private void showBookingOptions(int position) {
        Booking booking = bookingsList.get(position);
        String[] options = {"View Details", "Edit Booking", "Delete Booking", "Cancel"};

        new AlertDialog.Builder(this)
                .setTitle("Booking #" + booking.getId())
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            showBookingDetails(booking);
                            break;
                        case 1:
                            showEditBookingDialog(booking);
                            break;
                        case 2:
                            showDeleteBookingDialog(booking);
                            break;
                    }
                })
                .show();
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
                "Room: " + roomNum + " (" + roomType + ")\n\n" +
                "Check-in: " + checkIn + "\n" +
                "Check-out: " + checkOut + "\n\n" +
                "Guest: " + personName + "\n" +
                "Email: " + personEmail + "\n\n" +
                "Booking ID: " + booking.getId();

        new AlertDialog.Builder(this)
                .setTitle("Booking Details")
                .setMessage(details)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showEditBookingDialog(Booking booking) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_booking, null);

        EditText etCheckIn = view.findViewById(R.id.etCheckIn);
        EditText etCheckOut = view.findViewById(R.id.etCheckOut);
        EditText etRoomNumber = view.findViewById(R.id.etRoomNumber);
        EditText etHotelName = view.findViewById(R.id.etHotelName);

        etCheckIn.setText(booking.getCheckIn());
        etCheckOut.setText(booking.getCheckOut());
        etRoomNumber.setText(booking.getRoomNumber());
        etHotelName.setText(booking.getHotelName());

        builder.setView(view)
                .setTitle("Edit Booking #" + booking.getId())
                .setPositiveButton("Update", (dialog, which) -> {
                    String checkIn = etCheckIn.getText().toString().trim();
                    String checkOut = etCheckOut.getText().toString().trim();

                    if (checkIn.isEmpty() || checkOut.isEmpty()) {
                        Toast.makeText(this, "Please fill all date fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Validate dates
                    if (checkOut.compareTo(checkIn) <= 0) {
                        Toast.makeText(this, "Check-out must be after check-in", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Update booking dates
                    booking.setCheckIn(checkIn);
                    booking.setCheckOut(checkOut);

                    boolean success = updateBookingDates(booking);
                    if (success) {
                        Toast.makeText(this, "Booking updated successfully", Toast.LENGTH_SHORT).show();
                        loadAllBookings();
                    } else {
                        Toast.makeText(this, "Failed to update booking", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private boolean updateBookingDates(Booking booking) {
        int rows = db.updateBooking(booking);
        return rows > 0;
    }

    private void showDeleteBookingDialog(Booking booking) {
        String hotelName = booking.getHotelName() != null ? booking.getHotelName() : "Unknown Hotel";
        String personName = booking.getPersonName() != null ? booking.getPersonName() : "Unknown User";

        new AlertDialog.Builder(this)
                .setTitle("Delete Booking #" + booking.getId())
                .setMessage("Are you sure you want to delete this booking?\n\n" +
                        "Hotel: " + hotelName + "\n" +
                        "Guest: " + personName + "\n" +
                        "Check-in: " + booking.getCheckIn() + "\n\n" +
                        "This action cannot be undone!")
                .setPositiveButton("Delete", (dialog, which) -> {
                    performDeleteBooking(booking);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performDeleteBooking(Booking booking) {
        int count = db.deleteBooking(booking.getId());
        if (count > 0) {
            Toast.makeText(this, "Booking #" + booking.getId() + " deleted successfully", Toast.LENGTH_SHORT).show();
            loadAllBookings();
        } else {
            Toast.makeText(this, "Failed to delete booking", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}