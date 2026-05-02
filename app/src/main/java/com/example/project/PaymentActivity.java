package com.example.project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    private TextView tvPayHotelName, tvPayHotelLocation, tvPayDates, tvPayGuests;
    private Button btnConfirmPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        tvPayHotelName = findViewById(R.id.tvPayHotelName);
        tvPayHotelLocation = findViewById(R.id.tvPayHotelLocation);
        tvPayDates = findViewById(R.id.tvPayDates);
        tvPayGuests = findViewById(R.id.tvPayGuests);
        btnConfirmPay = findViewById(R.id.btnConfirmPay);

        // 1. Get data from Intent
        String hotelName = getIntent().getStringExtra("hotelName");
        String hotelLocation = getIntent().getStringExtra("hotelLocation");
        String checkIn = getIntent().getStringExtra("checkInDate");
        String checkOut = getIntent().getStringExtra("checkOutDate");
        int adults = getIntent().getIntExtra("adults", 1);
        int children = getIntent().getIntExtra("children", 0);

        // 2. Display data
        tvPayHotelName.setText(hotelName);
        tvPayHotelLocation.setText(hotelLocation);
        tvPayDates.setText("Check-in: " + checkIn + "\nCheck-out: " + checkOut);

        String guestsText = "Guests: " + adults + " Adults";
        if (children > 0) guestsText += ", " + children + " Children";
        tvPayGuests.setText(guestsText);

        // 3. Handle Pay Button Click
        btnConfirmPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PaymentActivity.this, "Payment Success!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}