package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class HotelListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewHotels;
    private HotelAdapter hotelAdapter;
    private ArrayList<Hotel> hotelList;
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_list);

        recyclerViewHotels = findViewById(R.id.recyclerViewHotels);
        recyclerViewHotels.setHasFixedSize(true);
        recyclerViewHotels.setLayoutManager(new LinearLayoutManager(this));

        hotelList = new ArrayList<>();
        hotelAdapter = new HotelAdapter(hotelList);
        recyclerViewHotels.setAdapter(hotelAdapter);

        String searchedLocation = getIntent().getStringExtra("location");
        if (searchedLocation != null) {
            searchedLocation = searchedLocation.trim();
        }

        if (searchedLocation != null && !searchedLocation.isEmpty()) {
            fetchHotels(searchedLocation);
        }

        // FIXED: Go to Room List Activity
        hotelAdapter.setOnItemClickListener((position, hotel) -> {
            Intent intent = new Intent(HotelListActivity.this, RoomsListActivity.class);

            intent.putExtra("hotelId", hotel.getId());
            intent.putExtra("hotelName", hotel.getName());

            // Forward Search Dates and Guests
            intent.putExtra("checkInDate", getIntent().getStringExtra("checkInDate"));
            intent.putExtra("checkOutDate", getIntent().getStringExtra("checkOutDate"));
            intent.putExtra("adults", getIntent().getIntExtra("adults", 1));
            intent.putExtra("children", getIntent().getIntExtra("children", 0));

            startActivity(intent);
        });
    }

    private void fetchHotels(String location) {
        db = new Database(this);
        db.open();
        hotelList.clear();
        hotelList.addAll(db.getHotelsByLocation(location));
        db.close();
        hotelAdapter.notifyDataSetChanged();

        if (hotelList.isEmpty()) {
            Toast.makeText(this, "No hotels found in " + location, Toast.LENGTH_SHORT).show();
        }
    }
}