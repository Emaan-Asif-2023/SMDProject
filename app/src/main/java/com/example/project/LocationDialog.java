package com.example.project;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class LocationDialog extends DialogFragment {

    private EditText editTextLocation;
    private OnLocationSelectedListener listener;

    // Constructor with no arguments
    public LocationDialog() {
        // Required empty constructor
    }

    public void setOnLocationSelectedListener(OnLocationSelectedListener listener) {
        this.listener = listener;
    }

    public interface OnLocationSelectedListener {
        void onLocationSelected(String location, double latitude, double longitude);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_location);

        editTextLocation = dialog.findViewById(R.id.editTextLocation);
        Button buttonConfirm = dialog.findViewById(R.id.buttonConfirm);
        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

        buttonConfirm.setOnClickListener(v -> {
            String location = editTextLocation.getText().toString().trim();
            if (location.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a location", Toast.LENGTH_SHORT).show();
                return;
            }
            // For now, using dummy coordinates
            if (listener != null) {
                listener.onLocationSelected(location, 0.0, 0.0);
            }
            dismiss();
        });

        buttonCancel.setOnClickListener(v -> dismiss());

        return dialog;
    }
}