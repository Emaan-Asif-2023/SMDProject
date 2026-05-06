package com.example.project;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {

    private TextView tvPayHotelName, tvPayRoomInfo, tvPayDates, tvPayGuests;
    private TextView tvLabelRoomPrice, tvValueRoomPrice, tvValueDays, tvValueAdults, tvValueChildren, tvTotalPrice;
    private Button btnConfirmPay;
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        tvPayHotelName = findViewById(R.id.tvPayHotelName);
        tvPayRoomInfo = findViewById(R.id.tvPayRoomInfo);
        tvPayDates = findViewById(R.id.tvPayDates);
        tvPayGuests = findViewById(R.id.tvPayGuests);
        tvValueRoomPrice = findViewById(R.id.tvValueRoomPrice);
        tvValueDays = findViewById(R.id.tvValueDays);
        tvValueAdults = findViewById(R.id.tvValueAdults);
        tvValueChildren = findViewById(R.id.tvValueChildren);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnConfirmPay = findViewById(R.id.btnConfirmPay);

        db = new Database(this);
        db.open();

        String hotelName = getIntent().getStringExtra("hotelName");
        String roomNumber = getIntent().getStringExtra("roomNumber");
        String roomType = getIntent().getStringExtra("roomType");
        double roomPrice = getIntent().getDoubleExtra("roomPrice", 0.0);
        int roomId = getIntent().getIntExtra("roomId", -1);

        String checkIn = getIntent().getStringExtra("checkInDate");
        String checkOut = getIntent().getStringExtra("checkOutDate");
        int adults = getIntent().getIntExtra("adults", 1);
        int children = getIntent().getIntExtra("children", 0);

        tvPayHotelName.setText(hotelName);
        tvPayRoomInfo.setText("Room " + roomNumber + " - " + roomType);
        tvPayDates.setText("Check-in: " + checkIn + "\nCheck-out: " + checkOut);

        String guestsText = adults + " Adults";
        if (children > 0) guestsText += ", " + children + " Children";
        tvPayGuests.setText("Guests: " + guestsText);

        int numOfDays = calculateDays(checkIn, checkOut);

        double adultCostPerDay = adults * roomPrice;
        double childCostPerDay = children * (roomPrice * 0.5);
        double totalCostPerDay = adultCostPerDay + childCostPerDay;
        double grandTotal = totalCostPerDay * numOfDays;

        tvValueRoomPrice.setText("$" + String.format("%.2f", roomPrice));
        tvValueDays.setText(String.valueOf(numOfDays));
        tvValueAdults.setText(String.valueOf(adults));
        tvValueChildren.setText(String.valueOf(children));
        tvTotalPrice.setText("$" + String.format("%.2f", grandTotal));

        btnConfirmPay.setOnClickListener(v -> {
            // Get the actual logged-in user's ID
            int currentUserId = getCurrentUserId();

            if (roomId != -1 && currentUserId != -1) {
                Booking newBooking = new Booking(checkIn, checkOut, currentUserId, roomId);
                long id = db.insertBooking(newBooking);

                if (id > 0) {
                    Toast.makeText(this, "Payment Success! Booking ID: " + id, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(this, "Booking failed in database.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Error: User not found", Toast.LENGTH_SHORT).show();
            }
            db.close();
        });
    }

    private int getCurrentUserId() {
        // First try Firebase
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String email = firebaseUser.getEmail();
            Person person = db.login(email, "");
            if (person != null) {
                return person.getId();
            }
        }

        SharedPreferences sp = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        return sp.getInt("personId", -1);
    }

    private int calculateDays(String checkIn, String checkOut) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date dateIn = sdf.parse(checkIn);
            Date dateOut = sdf.parse(checkOut);

            if (dateIn != null && dateOut != null) {
                long diffInMillis = dateOut.getTime() - dateIn.getTime();
                int days = (int) (diffInMillis / (1000 * 60 * 60 * 24));
                return Math.max(days, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 1;
    }
}