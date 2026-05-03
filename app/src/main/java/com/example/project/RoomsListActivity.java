package com.example.project;

import android.content.Intent;
import android.os.Bundle;
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
    private TextView tvHotelNameHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms_list);

        tvHotelNameHeader = findViewById(R.id.tvHotelNameHeader);
        recyclerViewRooms = findViewById(R.id.recyclerViewRooms);
        recyclerViewRooms.setHasFixedSize(true);
        recyclerViewRooms.setLayoutManager(new LinearLayoutManager(this));

        roomList = new ArrayList<>();
        roomAdapter = new RoomAdapter(roomList);
        recyclerViewRooms.setAdapter(roomAdapter);

        Intent intent = getIntent();
        int hotelId = intent.getIntExtra("hotelId", -1);
        String hotelName = intent.getStringExtra("hotelName");

        tvHotelNameHeader.setText("at " + hotelName);

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

    private void fetchRooms(int hotelId) {
        db = new Database(this);
        db.open();
        roomList.clear();
        roomList.addAll(db.getRoomsByHotel(hotelId));
        db.close();
        roomAdapter.notifyDataSetChanged();

        if (roomList.isEmpty()) {
            Toast.makeText(this, "No rooms available right now.", Toast.LENGTH_SHORT).show();
        }
    }
}