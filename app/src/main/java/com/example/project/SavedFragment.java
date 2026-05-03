package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class SavedFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView textViewEmpty;
    private Database db;
    private SavedDatabase savedDb;
    private int personId = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewSaved);
        textViewEmpty = view.findViewById(R.id.textViewEmpty);

        db = new Database(getContext());
        db.open();
        savedDb = new SavedDatabase(getContext());
        savedDb.open();

        loadSavedHotels();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSavedHotels();
    }

    private void loadSavedHotels() {
        ArrayList<Hotel> savedHotels = savedDb.getSavedHotels(personId, db);

        if (savedHotels.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);

            SavedHotelsAdapter adapter = new SavedHotelsAdapter(savedHotels, hotel -> {
                navigateToSearchWithHotel(hotel);
            });

            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            recyclerView.setAdapter(adapter);
        }
    }

    private void navigateToSearchWithHotel(Hotel hotel) {
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle(hotel.getName())
                .setMessage("To view rooms and book this hotel, you need to select dates and guests first.\n\nWhat would you like to do?")
                .setPositiveButton("Go to Search", (dialog, which) -> {
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.putExtra("navigateTo", "search");
                    intent.putExtra("prefillLocation", hotel.getLocation());
                    intent.putExtra("prefillHotelName", hotel.getName());
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (db != null) db.close();
        if (savedDb != null) savedDb.close();
    }
}