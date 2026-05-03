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

public class AdminHotelsActivity extends AppCompatActivity {

    private Database db;
    private ListView listViewHotels;
    private Button btnAddHotel, btnBack;
    private ArrayList<Hotel> hotelsList;
    private ArrayList<String> displayList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_hotels);

        db = new Database(this);
        db.open();

        listViewHotels = findViewById(R.id.listView);
        btnAddHotel = findViewById(R.id.btnAddHotel);
        btnBack = findViewById(R.id.btnBack);
        TextView titleText = findViewById(R.id.titleText);
        titleText.setText("Manage Hotels");

        displayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        listViewHotels.setAdapter(adapter);

        loadHotels();

        listViewHotels.setOnItemClickListener((parent, view, position, id) -> {
            showHotelOptions(position);
        });

        btnAddHotel.setOnClickListener(v -> showAddHotelDialog());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadHotels() {
        hotelsList = db.getAllHotels();
        displayList.clear();
        for (Hotel hotel : hotelsList) {
            displayList.add(hotel.getName() + " - " + hotel.getLocation());
        }
        adapter.notifyDataSetChanged();
    }

    private void showHotelOptions(int position) {
        Hotel hotel = hotelsList.get(position);
        String[] options = {"View Details", "Edit Hotel", "Delete Hotel", "Manage Rooms", "Cancel"};

        new AlertDialog.Builder(this)
                .setTitle("Hotel: " + hotel.getName())
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            showHotelDetails(hotel);
                            break;
                        case 1:
                            showEditHotelDialog(hotel);
                            break;
                        case 2:
                            showDeleteHotelDialog(hotel);
                            break;
                        case 3:
                            showManageRoomsDialog(hotel);
                            break;
                    }
                })
                .show();
    }

    private void showHotelDetails(Hotel hotel) {
        String details = "Name: " + hotel.getName() + "\n" +
                "Location: " + hotel.getLocation() + "\n" +
                "Description: " + hotel.getDescription();

        new AlertDialog.Builder(this)
                .setTitle("Hotel Details")
                .setMessage(details)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showAddHotelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_hotel, null);

        EditText etName = view.findViewById(R.id.etHotelName);
        EditText etLocation = view.findViewById(R.id.etHotelLocation);
        EditText etPrice = view.findViewById(R.id.etHotelPrice);
        EditText etDescription = view.findViewById(R.id.etHotelDescription);

        builder.setView(view)
                .setTitle("Add New Hotel")
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String location = etLocation.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();

                    if (name.isEmpty() || location.isEmpty()) {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Hotel hotel = new Hotel(name, location, description, R.drawable.hotel1);
                    long id = db.insertHotel(hotel);
                    if (id > 0) {
                        Toast.makeText(this, "Hotel added", Toast.LENGTH_SHORT).show();
                        loadHotels();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditHotelDialog(Hotel hotel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_hotel, null);

        EditText etName = view.findViewById(R.id.etHotelName);
        EditText etLocation = view.findViewById(R.id.etHotelLocation);
        EditText etDescription = view.findViewById(R.id.etHotelDescription);

        etName.setText(hotel.getName());
        etLocation.setText(hotel.getLocation());
        etDescription.setText(hotel.getDescription());

        builder.setView(view)
                .setTitle("Edit Hotel")
                .setPositiveButton("Update", (dialog, which) -> {
                    Toast.makeText(this, "Update hotel functionality coming soon", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteHotelDialog(Hotel hotel) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Hotel")
                .setMessage("Are you sure you want to delete " + hotel.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    Toast.makeText(this, "Delete hotel functionality coming soon", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showManageRoomsDialog(Hotel hotel) {
        ArrayList<Room> rooms = db.getRoomsByHotel(hotel.getId());
        StringBuilder roomList = new StringBuilder();
        for (Room room : rooms) {
            roomList.append("Room ").append(room.getRoomNumber())
                    .append(" - ").append(room.getType())
                    .append(" ($").append(room.getPrice()).append(")\n");
        }

        new AlertDialog.Builder(this)
                .setTitle("Rooms for " + hotel.getName())
                .setMessage(roomList.length() > 0 ? roomList.toString() : "No rooms available")
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}