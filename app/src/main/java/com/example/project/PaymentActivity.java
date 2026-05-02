package com.example.project;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    private TextView tvPayHotelName, tvPayHotelLocation, tvPayDates, tvPayGuests;
    private Button btnConfirmPay;
    private ImageView heartButton;

    private Database db;
    private SavedDatabase savedDb;
    private boolean isSaved = false;

    // Store hotel data
    private String hotelName;
    private String hotelLocation;
    private int hotelId;
    private int personId = 1; // Default or get from login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        tvPayHotelName = findViewById(R.id.tvPayHotelName);
        tvPayHotelLocation = findViewById(R.id.tvPayHotelLocation);
        tvPayDates = findViewById(R.id.tvPayDates);
        tvPayGuests = findViewById(R.id.tvPayGuests);
        btnConfirmPay = findViewById(R.id.btnConfirmPay);
        heartButton = findViewById(R.id.heartButton);

        // Initialize databases
        db = new Database(this);
        db.open();
        savedDb = new SavedDatabase(this);
        savedDb.open();

        // Get data from Intent
        hotelName = getIntent().getStringExtra("hotelName");
        hotelLocation = getIntent().getStringExtra("hotelLocation");
        hotelId = getIntent().getIntExtra("hotelId", -1);
        String checkIn = getIntent().getStringExtra("checkInDate");
        String checkOut = getIntent().getStringExtra("checkOutDate");
        int adults = getIntent().getIntExtra("adults", 1);
        int children = getIntent().getIntExtra("children", 0);

        // Display data
        tvPayHotelName.setText(hotelName);
        tvPayHotelLocation.setText(hotelLocation);
        tvPayDates.setText("Check-in: " + checkIn + "\nCheck-out: " + checkOut);

        String guestsText = "Guests: " + adults + " Adults";
        if (children > 0) guestsText += ", " + children + " Children";
        tvPayGuests.setText(guestsText);

        // Check if hotel is already saved
        if (hotelId != -1) {
            isSaved = savedDb.isHotelSaved(personId, hotelId);
            updateHeartIcon();
        }

        // Heart button click
        heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hotelId == -1) {
                    Toast.makeText(PaymentActivity.this, "Hotel not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isSaved) {
                    // Remove from saved
                    savedDb.removeSavedHotel(personId, hotelId);
                    isSaved = false;
                    Toast.makeText(PaymentActivity.this, "Removed from saved", Toast.LENGTH_SHORT).show();
                } else {
                    // Add to saved
                    savedDb.saveHotel(personId, hotelId);
                    isSaved = true;
                    Toast.makeText(PaymentActivity.this, "Hotel saved!", Toast.LENGTH_SHORT).show();
                }
                updateHeartIcon();
            }
        });

        // Handle Pay Button Click
        btnConfirmPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create booking in database
                Booking booking = new Booking(checkIn, checkOut, personId, 1); // Room ID 1 as default
                long bookingId = db.insertBooking(booking);

                if (bookingId != -1) {
                    Toast.makeText(PaymentActivity.this, "Payment Successful! Booking confirmed.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(PaymentActivity.this, "Booking failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateHeartIcon() {
        if (isSaved) {
            heartButton.setImageResource(R.drawable.ic_heart_filled);
        } else {
            heartButton.setImageResource(R.drawable.ic_heart_empty);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) db.close();
        if (savedDb != null) savedDb.close();
    }
}