package com.example.project;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
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

    public interface OnGuestsSelectedListener {
        void onGuestsSelected(int adults, int children);
    }

    public GuestPickerDialog(OnGuestsSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_guest_picker);

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
            listener.onGuestsSelected(adults, children);
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
}