package com.example.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;

public class BookingsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView textViewEmpty;
    private Database db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookings, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewBookings);
        textViewEmpty = view.findViewById(R.id.textViewEmpty);

        db = new Database(getContext());
        db.open();

        loadBookings();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBookings();
    }

    private void loadBookings() {
        int personId = getCurrentUserId();

        if (personId == -1) {
            recyclerView.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
            textViewEmpty.setText("Please login to view bookings");
            return;
        }

        ArrayList<Booking> bookings = db.getBookingsByUser(personId);

        if (bookings.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
            textViewEmpty.setText("No bookings yet");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);

            BookingsAdapter adapter = new BookingsAdapter(bookings, booking -> {
                Intent intent = new Intent(getActivity(), BookingDetailsActivity.class);
                intent.putExtra("bookingId", booking.getId());
                intent.putExtra("hotelName", booking.getHotelName());
                intent.putExtra("roomNumber", booking.getRoomNumber());
                intent.putExtra("roomType", booking.getRoomType());
                intent.putExtra("checkIn", booking.getCheckIn());
                intent.putExtra("checkOut", booking.getCheckOut());
                startActivity(intent);
            });

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        }
    }

    private int getCurrentUserId() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null && db != null) {
            String email = firebaseUser.getEmail();
            Person person = db.login(email, "");
            if (person != null) {
                return person.getId();
            }
        }

        SharedPreferences sp = requireContext().getSharedPreferences("LoginPrefs", requireContext().MODE_PRIVATE);
        return sp.getInt("personId", -1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (db != null) db.close();
    }
}