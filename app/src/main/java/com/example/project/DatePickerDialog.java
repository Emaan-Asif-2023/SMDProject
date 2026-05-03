package com.example.project;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatePickerDialog extends DialogFragment {

    private CalendarView calendarView;
    private Button buttonConfirm;
    private Button buttonCancel;
    private boolean isCheckIn;
    private Date selectedDate;
    private OnDateSelectedListener listener;
    private String minDateString;

    public DatePickerDialog() {
        this.selectedDate = new Date();
    }

    public interface OnDateSelectedListener {
        void onDateSelected(String date, boolean isCheckIn);
    }

    public void setOnDateSelectedListener(boolean isCheckIn, OnDateSelectedListener listener) {
        this.isCheckIn = isCheckIn;
        this.listener = listener;
    }

    public void setMinDate(String minDate) {
        this.minDateString = minDate;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext(), android.R.style.Theme_DeviceDefault_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_date_picker);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        calendarView = dialog.findViewById(R.id.calendarView);
        buttonConfirm = dialog.findViewById(R.id.buttonConfirm);
        buttonCancel = dialog.findViewById(R.id.buttonCancel);

        long calculatedMinDate = System.currentTimeMillis() - 1000;

        if (minDateString != null && !minDateString.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date minDate = sdf.parse(minDateString);
                if (minDate != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(minDate);
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    calculatedMinDate = cal.getTimeInMillis();
                    selectedDate = cal.getTime();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        final long minDateMillis = calculatedMinDate;

        calendarView.setMinDate(minDateMillis);
        calendarView.setDate(minDateMillis);

        final CalendarView finalCalendarView = calendarView;

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);

            if (calendar.getTimeInMillis() < minDateMillis) {
                Toast.makeText(getContext(),
                        "Please select a valid date",
                        Toast.LENGTH_SHORT).show();
                finalCalendarView.setDate(minDateMillis);
                selectedDate = new Date(minDateMillis);
            } else {
                selectedDate = calendar.getTime();
            }
        });

        buttonConfirm.setOnClickListener(v -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = sdf.format(selectedDate);
            if (listener != null) {
                listener.onDateSelected(formattedDate, isCheckIn);
            }
            dismiss();
        });

        buttonCancel.setOnClickListener(v -> dismiss());

        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            Window window = dialog.getWindow();


            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            int height = WindowManager.LayoutParams.WRAP_CONTENT;


            WindowManager.LayoutParams params = window.getAttributes();
            params.width = width;
            params.height = height;
            params.gravity = Gravity.CENTER;


            params.x = 0;
            params.y = 0;

            window.setAttributes(params);
            window.setGravity(Gravity.CENTER);
        }
    }
}