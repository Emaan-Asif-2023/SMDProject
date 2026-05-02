package com.example.project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.ViewHolder> {

    private List<Booking> bookings;
    private OnBookingClickListener listener;

    public interface OnBookingClickListener {
        void onBookingClick(Booking booking);
    }

    public BookingsAdapter(ArrayList<Booking> bookings, OnBookingClickListener listener) {
        this.bookings = bookings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        holder.hotelName.setText(booking.getHotelName());
        holder.roomInfo.setText("Room: " + booking.getRoomNumber() + " - " + booking.getRoomType());
        holder.dates.setText("Check-in: " + booking.getCheckIn() + "\nCheck-out: " + booking.getCheckOut());

        holder.itemView.setOnClickListener(v -> listener.onBookingClick(booking));
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView hotelName, roomInfo, dates;

        ViewHolder(View itemView) {
            super(itemView);
            hotelName = itemView.findViewById(R.id.textHotelName);
            roomInfo = itemView.findViewById(R.id.textRoomInfo);
            dates = itemView.findViewById(R.id.textDates);
        }
    }
}