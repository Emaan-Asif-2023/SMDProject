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

        if (hotelsList.isEmpty()) {
            displayList.add("No hotels found");
        } else {
            for (Hotel hotel : hotelsList) {
                int roomCount = db.getRoomsByHotel(hotel.getId()).size();
                displayList.add(hotel.getId() + ": " + hotel.getName() +
                        " - " + hotel.getLocation() +
                        " (" + roomCount + " rooms)");
            }
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
        int roomCount = db.getRoomsByHotel(hotel.getId()).size();
        String details = "Hotel ID: " + hotel.getId() + "\n" +
                "Name: " + hotel.getName() + "\n" +
                "Location: " + hotel.getLocation() + "\n" +
                "Description: " + hotel.getDescription() + "\n" +
                "Total Rooms: " + roomCount;

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
        EditText etDescription = view.findViewById(R.id.etHotelDescription);

        builder.setView(view)
                .setTitle("Add New Hotel")
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String location = etLocation.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();

                    if (name.isEmpty()) {
                        Toast.makeText(this, "Hotel name is required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (location.isEmpty()) {
                        Toast.makeText(this, "Location is required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (description.isEmpty()) {
                        description = "No description available";
                    }

                    Hotel hotel = new Hotel(name, location, description, R.drawable.hotel1);
                    long id = db.insertHotel(hotel);
                    if (id > 0) {
                        Toast.makeText(this, "Hotel added successfully", Toast.LENGTH_SHORT).show();
                        loadHotels();
                    } else {
                        Toast.makeText(this, "Failed to add hotel", Toast.LENGTH_SHORT).show();
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
                .setTitle("Edit Hotel #" + hotel.getId())
                .setPositiveButton("Update", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String location = etLocation.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();

                    if (name.isEmpty()) {
                        Toast.makeText(this, "Hotel name is required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (location.isEmpty()) {
                        Toast.makeText(this, "Location is required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (description.isEmpty()) {
                        description = "No description available";
                    }

                    hotel.setName(name);
                    hotel.setLocation(location);
                    hotel.setDescription(description);

                    int rows = db.updateHotel(hotel);
                    if (rows > 0) {
                        Toast.makeText(this, "Hotel updated successfully", Toast.LENGTH_SHORT).show();
                        loadHotels();
                    } else {
                        Toast.makeText(this, "Failed to update hotel", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteHotelDialog(Hotel hotel) {

        ArrayList<Room> rooms = db.getRoomsByHotel(hotel.getId());
        int roomCount = rooms.size();


        String warningMessage = "Are you sure you want to delete " + hotel.getName() + "?";
        if (roomCount > 0) {
            warningMessage += "\n\nThis hotel has " + roomCount + " room(s) that will also be deleted.";
        }
        warningMessage += "\n\nThis action cannot be undone!";

        new AlertDialog.Builder(this)
                .setTitle("Delete Hotel")
                .setMessage(warningMessage)
                .setPositiveButton("Delete", (dialog, which) -> {

                    for (Room room : rooms) {
                        deleteRoomFromDatabase(room.getId());
                    }


                    int count = db.deleteHotel(hotel.getId());
                    if (count > 0) {
                        Toast.makeText(this, "Hotel deleted successfully", Toast.LENGTH_SHORT).show();
                        loadHotels();
                    } else {
                        Toast.makeText(this, "Failed to delete hotel", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteRoomFromDatabase(int roomId) {
        db.deleteRoom(roomId);
    }

    private void showManageRoomsDialog(Hotel hotel) {
        ArrayList<Room> rooms = db.getRoomsByHotel(hotel.getId());

        if (rooms.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Rooms for " + hotel.getName())
                    .setMessage("No rooms available for this hotel.\n\nWould you like to add a room?")
                    .setPositiveButton("Add Room", (dialog, which) -> {
                        showAddRoomDialog(hotel);
                    })
                    .setNegativeButton("Close", null)
                    .show();
        } else {
            StringBuilder roomList = new StringBuilder();
            for (Room room : rooms) {
                roomList.append("Room ").append(room.getRoomNumber())
                        .append(" - ").append(room.getType())
                        .append("\n    $").append(String.format("%.2f", room.getPrice()))
                        .append(" (ID: ").append(room.getId()).append(")\n\n");
            }

            new AlertDialog.Builder(this)
                    .setTitle("Rooms for " + hotel.getName() + " (" + rooms.size() + ")")
                    .setMessage(roomList.toString())
                    .setPositiveButton("Add Room", (dialog, which) -> {
                        showAddRoomDialog(hotel);
                    })
                    .setNeutralButton("Delete Room", (dialog, which) -> {
                        showDeleteRoomDialog(hotel, rooms);
                    })
                    .setNegativeButton("Close", null)
                    .show();
        }
    }

    private void showAddRoomDialog(Hotel hotel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_room, null);

        EditText etRoomNumber = view.findViewById(R.id.etRoomNumber);
        EditText etRoomType = view.findViewById(R.id.etRoomType);
        EditText etRoomPrice = view.findViewById(R.id.etRoomPrice);

        builder.setView(view)
                .setTitle("Add Room to " + hotel.getName())
                .setPositiveButton("Add", (dialog, which) -> {
                    String roomNumber = etRoomNumber.getText().toString().trim();
                    String roomType = etRoomType.getText().toString().trim();
                    String priceStr = etRoomPrice.getText().toString().trim();

                    if (roomNumber.isEmpty() || roomType.isEmpty() || priceStr.isEmpty()) {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double price;
                    try {
                        price = Double.parseDouble(priceStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Room room = new Room(roomNumber, roomType, price, hotel.getId());
                    long id = db.insertRoom(room);
                    if (id > 0) {
                        Toast.makeText(this, "Room added successfully", Toast.LENGTH_SHORT).show();
                        loadHotels();
                    } else {
                        Toast.makeText(this, "Failed to add room", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteRoomDialog(Hotel hotel, ArrayList<Room> rooms) {
        String[] roomNumbers = new String[rooms.size()];
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            roomNumbers[i] = room.getRoomNumber() + " - " + room.getType() + " ($" + room.getPrice() + ")";
        }

        new AlertDialog.Builder(this)
                .setTitle("Select Room to Delete")
                .setItems(roomNumbers, (dialog, which) -> {
                    Room selectedRoom = rooms.get(which);
                    new AlertDialog.Builder(this)
                            .setTitle("Confirm Delete")
                            .setMessage("Delete Room " + selectedRoom.getRoomNumber() + "?")
                            .setPositiveButton("Delete", (dialog2, which2) -> {
                                deleteRoomFromDatabase(selectedRoom.getId());
                                Toast.makeText(this, "Room deleted", Toast.LENGTH_SHORT).show();
                                loadHotels();
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                })
                .setNegativeButton("Cancel", null)
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