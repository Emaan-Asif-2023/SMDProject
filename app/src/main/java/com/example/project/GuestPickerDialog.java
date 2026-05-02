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
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class GuestPickerDialog extends DialogFragment {

    private TextView textViewAdultsCount;
    private TextView textViewChildrenCount;
    private int adults = 1;
    private int children = 0;
    private OnGuestsSelectedListener listener;
    private Button buttonAdultMinus, buttonAdultPlus;
    private Button buttonChildMinus, buttonChildPlus;

    public interface OnGuestsSelectedListener {
        void onGuestsSelected(int adults, int children);
    }

    public GuestPickerDialog() {
        // Required empty constructor
    }

    public void setOnGuestsSelectedListener(OnGuestsSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_guest_picker);

        // Set dialog window to full width
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
        }

        textViewAdultsCount = dialog.findViewById(R.id.textViewAdultsCount);
        textViewChildrenCount = dialog.findViewById(R.id.textViewChildrenCount);
        Button buttonConfirm = dialog.findViewById(R.id.buttonConfirm);
        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

        // Adults controls
        dialog.findViewById(R.id.buttonAdultMinus).setOnClickListener(v -> {
            if (adults > 1) {
                adults--;
                updateCounts();
            }
        });

        dialog.findViewById(R.id.buttonAdultPlus).setOnClickListener(v -> {
            if (adults < 10) {
                adults++;
                updateCounts();
            }
        });

        // Children controls
        dialog.findViewById(R.id.buttonChildMinus).setOnClickListener(v -> {
            if (children > 0) {
                children--;
                updateCounts();
            }
        });

        dialog.findViewById(R.id.buttonChildPlus).setOnClickListener(v -> {
            if (children < 5) {
                children++;
                updateCounts();
            }
        });

        buttonConfirm.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGuestsSelected(adults, children);
            }
            dismiss();
        });

        buttonCancel.setOnClickListener(v -> dismiss());

        updateCounts();
        return dialog;
    }

    private void updateCounts() {
        textViewAdultsCount.setText(String.valueOf(adults));
        textViewChildrenCount.setText(String.valueOf(children));
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                // Set dialog width to 90% of screen width
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                params.gravity = Gravity.CENTER;
                window.setAttributes(params);
            }
        }
    }
}