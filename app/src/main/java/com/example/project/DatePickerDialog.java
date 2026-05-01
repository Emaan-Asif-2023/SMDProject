package com.example.project;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatePickerDialog extends DialogFragment {

    private CalendarView calendarView;
    private Button buttonConfirm;
    private Button buttonCancel;
    private boolean isCheckIn;
    private Date selectedDate;
    private OnDateSelectedListener listener;

    public interface OnDateSelectedListener {
        void onDateSelected(String date, boolean isCheckIn);
    }

    public DatePickerDialog(boolean isCheckIn, OnDateSelectedListener listener) {
        this.isCheckIn = isCheckIn;
        this.listener = listener;
        this.selectedDate = new Date();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_date_picker);

        calendarView = dialog.findViewById(R.id.calendarView);
        buttonConfirm = dialog.findViewById(R.id.buttonConfirm);
        buttonCancel = dialog.findViewById(R.id.buttonCancel);

        calendarView.setMinDate(System.currentTimeMillis() - 1000);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            selectedDate = calendar.getTime();
        });

        buttonConfirm.setOnClickListener(v -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = sdf.format(selectedDate);
            listener.onDateSelected(formattedDate, isCheckIn);
            dismiss();
        });

        buttonCancel.setOnClickListener(v -> dismiss());

        return dialog;
    }
}