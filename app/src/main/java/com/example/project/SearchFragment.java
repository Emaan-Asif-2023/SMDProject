package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class SearchFragment extends Fragment {

    private LinearLayout layoutLocation;
    private LinearLayout layoutCheckIn;
    private LinearLayout layoutCheckOut;
    private LinearLayout layoutGuests;
    private Button buttonSearch;

    private TextView textViewLocation;
    private TextView textViewCheckInDate;
    private TextView textViewCheckOutDate;
    private TextView textViewGuests;

    private String selectedLocation = "";
    private String checkInDate = "";
    private String checkOutDate = "";
    private int adults = 1;
    private int children = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        initializeViews(view);
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {
        layoutLocation = view.findViewById(R.id.layoutLocation);
        layoutCheckIn = view.findViewById(R.id.layoutCheckIn);
        layoutCheckOut = view.findViewById(R.id.layoutCheckOut);
        layoutGuests = view.findViewById(R.id.layoutGuests);
        buttonSearch = view.findViewById(R.id.buttonSearch);

        textViewLocation = view.findViewById(R.id.textViewLocation);
        textViewCheckInDate = view.findViewById(R.id.textViewCheckInDate);
        textViewCheckOutDate = view.findViewById(R.id.textViewCheckOutDate);
        textViewGuests = view.findViewById(R.id.textViewGuests);
    }

    private void setupClickListeners() {
        // Location click
        layoutLocation.setOnClickListener(v -> {
            LocationDialog locationDialog = new LocationDialog();
            locationDialog.setOnLocationSelectedListener((location, latitude, longitude) -> {
                selectedLocation = location;
                textViewLocation.setText(location);
                textViewLocation.setTextColor(getResources().getColor(android.R.color.black));
            });
            locationDialog.show(getChildFragmentManager(), "LocationDialog");
        });

        // Check-in date click - FIXED
        layoutCheckIn.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog();
            datePicker.setOnDateSelectedListener(true, (date, isCheckIn) -> {
                checkInDate = date;
                textViewCheckInDate.setText(formatDateForDisplay(date));
                textViewCheckInDate.setTextColor(getResources().getColor(android.R.color.black));
            });
            datePicker.show(getChildFragmentManager(), "CheckInDatePicker");
        });

        // Check-out date click - FIXED
        layoutCheckOut.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog();
            datePicker.setOnDateSelectedListener(false, (date, isCheckIn) -> {
                checkOutDate = date;
                textViewCheckOutDate.setText(formatDateForDisplay(date));
                textViewCheckOutDate.setTextColor(getResources().getColor(android.R.color.black));
            });
            datePicker.show(getChildFragmentManager(), "CheckOutDatePicker");
        });

        // Guests click - FIXED
        layoutGuests.setOnClickListener(v -> {
            GuestPickerDialog guestPicker = new GuestPickerDialog();
            guestPicker.setOnGuestsSelectedListener((adultsCount, childrenCount) -> {
                adults = adultsCount;
                children = childrenCount;
                updateGuestsDisplay();
                textViewGuests.setTextColor(getResources().getColor(android.R.color.black));
            });
            guestPicker.show(getChildFragmentManager(), "GuestPicker");
        });

        // Search button click
        buttonSearch.setOnClickListener(v -> {
            if (validateForm()) {
                Intent intent = new Intent(getActivity(), HotelListActivity.class);
                intent.putExtra("location", selectedLocation);
                intent.putExtra("checkInDate", checkInDate);
                intent.putExtra("checkOutDate", checkOutDate);
                intent.putExtra("adults", adults);
                intent.putExtra("children", children);
                startActivity(intent);
            }
        });
    }

    private void updateGuestsDisplay() {
        StringBuilder guestsText = new StringBuilder();
        guestsText.append(adults).append(" Adult");
        if (adults > 1) guestsText.append("s");

        if (children > 0) {
            guestsText.append(", ").append(children).append(" Child");
            if (children > 1) guestsText.append("ren");
        }

        textViewGuests.setText(guestsText.toString());
    }

    private String formatDateForDisplay(String date) {
        try {
            String[] parts = date.split("-");
            String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            int monthIndex = Integer.parseInt(parts[1]) - 1;
            return parts[2] + " " + months[monthIndex] + " " + parts[0];
        } catch (Exception e) {
            return date;
        }
    }

    private boolean validateForm() {
        if (selectedLocation.isEmpty()) {
            Toast.makeText(getContext(), "Please select a location", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (checkInDate.isEmpty()) {
            Toast.makeText(getContext(), "Please select check-in date", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (checkOutDate.isEmpty()) {
            Toast.makeText(getContext(), "Please select check-out date", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (adults == 0) {
            Toast.makeText(getContext(), "Please select at least 1 adult", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}