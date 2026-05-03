package com.example.project;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PaymentActivity extends AppCompatActivity {

    private TextView tvPayHotelName, tvPayHotelLocation, tvPayDates, tvPayGuests;
    private Button btnConfirmPay;
    private ImageView heartButton;

    private Database db;
    private SavedDatabase savedDb;
    private boolean isSaved = false;

    private String hotelName;
    private String hotelLocation;
    private int hotelId;
    private int personId = -1;

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

        db = new Database(this);
        db.open();
        savedDb = new SavedDatabase(this);
        savedDb.open();

        getCurrentUserId();

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
        if (hotelId != -1 && personId != -1) {
            isSaved = savedDb.isHotelSaved(personId, hotelId);
            updateHeartIcon();
        }

        heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hotelId == -1 || personId == -1) {
                    Toast.makeText(PaymentActivity.this, "Error: User or hotel not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isSaved) {
                    savedDb.removeSavedHotel(personId, hotelId);
                    isSaved = false;
                    Toast.makeText(PaymentActivity.this, "Removed from saved", Toast.LENGTH_SHORT).show();
                } else {
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
                if (personId == -1) {
                    Toast.makeText(PaymentActivity.this, "User not identified. Please login again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // created booking with actual person ID
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

    private void getCurrentUserId() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String email = firebaseUser.getEmail();
            Person person = db.login(email, "");
            if (person == null) {
                java.util.ArrayList<Person> allPersons = db.getAllPersons();
                for (Person p : allPersons) {
                    if (p.getEmail() != null && p.getEmail().equals(email)) {
                        person = p;
                        break;
                    }
                }
            }

            if (person != null) {
                personId = person.getId();
            } else {
                Person newPerson = new Person(
                        firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : email.split("@")[0],
                        email,
                        ""
                );
                newPerson.setRole("user");
                long newId = db.insertPerson(newPerson);
                personId = (int) newId;
            }
        } else {
            SharedPreferences sp = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            String savedEmail = sp.getString("email", null);
            if (savedEmail != null) {
                Person person = db.login(savedEmail, "");
                if (person != null) {
                    personId = person.getId();
                }
            }
        }

        Toast.makeText(this, "Logged in as Person ID: " + personId, Toast.LENGTH_SHORT).show();
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