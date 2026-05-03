package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RoomsListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRooms;
    private RoomAdapter roomAdapter;
    private ArrayList<Room> roomList;
    private Database db;
    private SavedDatabase savedDb;
    private TextView tvHotelNameHeader;
    private ImageView ivHeart;

    private int hotelId;
    private String hotelName;
    private int personId = 1;
    private boolean isSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms_list);

        tvHotelNameHeader = findViewById(R.id.tvHotelNameHeader);
        ivHeart = findViewById(R.id.ivHeart);
        recyclerViewRooms = findViewById(R.id.recyclerViewRooms);
        recyclerViewRooms.setHasFixedSize(true);
        recyclerViewRooms.setLayoutManager(new LinearLayoutManager(this));

        roomList = new ArrayList<>();
        roomAdapter = new RoomAdapter(roomList);
        recyclerViewRooms.setAdapter(roomAdapter);

        db = new Database(this);
        db.open();
        savedDb = new SavedDatabase(this);
        savedDb.open();

        Intent intent = getIntent();
        hotelId = intent.getIntExtra("hotelId", -1);
        hotelName = intent.getStringExtra("hotelName");

        tvHotelNameHeader.setText("at " + hotelName);

        checkIfSaved();

        ivHeart.setOnClickListener(v -> {
            toggleSaveHotel();
        });

        if (hotelId != -1) {
            fetchRooms(hotelId);
        }

        roomAdapter.setOnItemClickListener((position, room) -> {
            Intent payIntent = new Intent(RoomsListActivity.this, PaymentActivity.class);

            payIntent.putExtra("hotelName", hotelName);
            payIntent.putExtra("roomNumber", room.getRoomNumber());
            payIntent.putExtra("roomType", room.getType());
            payIntent.putExtra("roomPrice", room.getPrice());
            payIntent.putExtra("roomId", room.getId());

            payIntent.putExtra("checkInDate", intent.getStringExtra("checkInDate"));
            payIntent.putExtra("checkOutDate", intent.getStringExtra("checkOutDate"));
            payIntent.putExtra("adults", intent.getIntExtra("adults", 1));
            payIntent.putExtra("children", intent.getIntExtra("children", 0));

            startActivity(payIntent);
        });
    }

    private void checkIfSaved() {
        isSaved = savedDb.isHotelSaved(personId, hotelId);
        updateHeartIcon();
    }

    private void toggleSaveHotel() {
        if (isSaved) {

            savedDb.removeSavedHotel(personId, hotelId);
            isSaved = false;
            Toast.makeText(this, "Removed from saved", Toast.LENGTH_SHORT).show();
        } else {

            savedDb.saveHotel(personId, hotelId);
            isSaved = true;
            Toast.makeText(this, "Hotel saved!", Toast.LENGTH_SHORT).show();
        }
        updateHeartIcon();
    }

    private void updateHeartIcon() {
        if (isSaved) {
            ivHeart.setImageResource(R.drawable.ic_heart_filled);
        } else {
            ivHeart.setImageResource(R.drawable.ic_heart_empty);
        }
    }

    private void fetchRooms(int hotelId) {
        roomList.clear();
        roomList.addAll(db.getRoomsByHotel(hotelId));
        roomAdapter.notifyDataSetChanged();

        if (roomList.isEmpty()) {
            Toast.makeText(this, "No rooms available right now.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) db.close();
        if (savedDb != null) savedDb.close();
    }
}