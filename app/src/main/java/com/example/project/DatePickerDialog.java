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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
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

    // Empty constructor required for DialogFragment
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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_date_picker);

        // Set dialog window to full width with margins
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);

            // Add margin to the dialog window
            WindowManager.LayoutParams params = window.getAttributes();
            params.horizontalMargin = 20; // 20dp margin on each side
            params.verticalMargin = 10;
            window.setAttributes(params);
        }

        calendarView = dialog.findViewById(R.id.calendarView);
        buttonConfirm = dialog.findViewById(R.id.buttonConfirm);
        buttonCancel = dialog.findViewById(R.id.buttonCancel);

        // Set minimum date to today
        calendarView.setMinDate(System.currentTimeMillis() - 1000);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            selectedDate = calendar.getTime();
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
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                // Set dialog width to match parent with margins
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                params.gravity = Gravity.CENTER;
                window.setAttributes(params);
            }
        }
    }
}