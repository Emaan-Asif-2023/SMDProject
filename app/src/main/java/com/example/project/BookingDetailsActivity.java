package com.example.project;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class BookingDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);

        // --- Handle the new Back button ---
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Goes to the previous screen
            }
        });

        // --- Your existing code ---
        TextView hotelName = findViewById(R.id.tvDetailHotelName);
        TextView roomInfo = findViewById(R.id.tvDetailRoomInfo);
        TextView dates = findViewById(R.id.tvDetailDates);

        String name = getIntent().getStringExtra("hotelName");
        String roomNumber = getIntent().getStringExtra("roomNumber");
        String roomType = getIntent().getStringExtra("roomType");
        String checkIn = getIntent().getStringExtra("checkIn");
        String checkOut = getIntent().getStringExtra("checkOut");

        hotelName.setText(name);
        roomInfo.setText("Room: " + roomNumber + " - " + roomType + " Type");
        dates.setText("Check-in: " + checkIn + "\nCheck-out: " + checkOut);
    }
}