package com.example.project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SavedHotelsAdapter extends RecyclerView.Adapter<SavedHotelsAdapter.ViewHolder> {

    private List<Hotel> hotels;
    private OnHotelClickListener listener;

    public interface OnHotelClickListener {
        void onHotelClick(Hotel hotel);
    }

    public SavedHotelsAdapter(List<Hotel> hotels, OnHotelClickListener listener) {
        this.hotels = hotels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_saved_hotel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hotel hotel = hotels.get(position);
        holder.hotelName.setText(hotel.getName());
        holder.hotelLocation.setText(hotel.getLocation());

        // Set hotel image if available
        try {
            holder.hotelImage.setImageResource(hotel.getImageResId());
        } catch (Exception e) {
            holder.hotelImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.itemView.setOnClickListener(v -> listener.onHotelClick(hotel));
    }

    @Override
    public int getItemCount() {
        return hotels.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView hotelImage;
        TextView hotelName, hotelLocation;

        ViewHolder(View itemView) {
            super(itemView);
            hotelImage = itemView.findViewById(R.id.imageHotel);
            hotelName = itemView.findViewById(R.id.textHotelName);
            hotelLocation = itemView.findViewById(R.id.textHotelLocation);
        }
    }
}