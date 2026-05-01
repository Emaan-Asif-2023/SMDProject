package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SearchResultsActivity extends AppCompatActivity {

    private TextView textViewSearchDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        textViewSearchDetails = findViewById(R.id.textViewSearchDetails);

        // Get data from intent
        Intent intent = getIntent();
        String location = intent.getStringExtra("location");
        String checkInDate = intent.getStringExtra("checkInDate");
        String checkOutDate = intent.getStringExtra("checkOutDate");
        int adults = intent.getIntExtra("adults", 0);
        int children = intent.getIntExtra("children", 0);

        // Display search parameters
        String searchDetails = "Searching hotels in " + location + "\n\n" +
                "Check-in: " + checkInDate + "\n" +
                "Check-out: " + checkOutDate + "\n\n" +
                "Guests: " + adults + " Adults";
        if (children > 0) {
            searchDetails += ", " + children + " Children";
        }

        textViewSearchDetails.setText(searchDetails);
    }
}