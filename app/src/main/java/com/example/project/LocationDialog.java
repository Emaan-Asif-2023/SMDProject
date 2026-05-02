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

    // Method to set the listener
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
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_location);

        // Set dialog window to full width
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
        }

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