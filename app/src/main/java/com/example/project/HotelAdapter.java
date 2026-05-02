package com.example.project;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelViewHolder> {

    private ArrayList<Hotel> hotelList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position, Hotel hotel);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public HotelAdapter(ArrayList<Hotel> hotelList) {
        this.hotelList = hotelList;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hotel, parent, false);
        return new HotelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        Hotel hotel = hotelList.get(position);

        holder.tvName.setText(hotel.getName());
        holder.tvLocation.setText(hotel.getLocation());
        holder.tvDesc.setText(hotel.getDescription());

        // Load image safely (uses 0 if no image is assigned)
        if (hotel.getImageResId() != 0) {
            holder.imgHotel.setImageResource(hotel.getImageResId());
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position, hotel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hotelList.size();
    }

    public static class HotelViewHolder extends RecyclerView.ViewHolder {
        ImageView imgHotel;
        TextView tvName, tvLocation, tvDesc;

        public HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            imgHotel = itemView.findViewById(R.id.imgHotel);
            tvName = itemView.findViewById(R.id.tvHotelName);
            tvLocation = itemView.findViewById(R.id.tvHotelLocation);
            tvDesc = itemView.findViewById(R.id.tvHotelDesc);
        }
    }
}