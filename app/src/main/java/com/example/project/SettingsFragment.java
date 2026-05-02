package com.example.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsFragment extends Fragment {

    private LinearLayout layoutProfile;
    private LinearLayout layoutNotifications;
    private LinearLayout layoutLanguage;
    private LinearLayout layoutCurrency;
    private LinearLayout layoutPrivacy;
    private LinearLayout layoutAbout;
    private LinearLayout layoutLogout;

    private Switch switchNotifications;
    private TextView textViewUserName;
    private TextView textViewUserEmail;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        initializeViews(view);
        setupFirebase();
        loadUserData();
        setupClickListeners();
        loadPreferences();

        return view;
    }

    private void initializeViews(View view) {
        layoutProfile = view.findViewById(R.id.layoutProfile);
        layoutNotifications = view.findViewById(R.id.layoutNotifications);
        layoutLanguage = view.findViewById(R.id.layoutLanguage);
        layoutCurrency = view.findViewById(R.id.layoutCurrency);
        layoutPrivacy = view.findViewById(R.id.layoutPrivacy);
        layoutAbout = view.findViewById(R.id.layoutAbout);
        layoutLogout = view.findViewById(R.id.layoutLogout);

        switchNotifications = view.findViewById(R.id.switchNotifications);
        textViewUserName = view.findViewById(R.id.textViewUserName);
        textViewUserEmail = view.findViewById(R.id.textViewUserEmail);

        sp = requireContext().getSharedPreferences("SettingsPrefs", requireContext().MODE_PRIVATE);
        editor = sp.edit();
    }

    private void setupFirebase() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    private void loadUserData() {
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();

            if (name != null && !name.isEmpty()) {
                textViewUserName.setText(name);
            } else {
                String savedName = sp.getString("userName", "Guest User");
                textViewUserName.setText(savedName);
            }

            textViewUserEmail.setText(email != null ? email : "No email");
        } else {
            textViewUserName.setText("Guest User");
            textViewUserEmail.setText("Not logged in");
        }
    }

    private void setupClickListeners() {
        // Profile click
        layoutProfile.setOnClickListener(v -> {
            showEditProfileDialog();
        });

        // Notification toggle
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("notifications", isChecked);
            editor.apply();
            if (isChecked) {
                Toast.makeText(getContext(), "Notifications enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Notifications disabled", Toast.LENGTH_SHORT).show();
            }
        });

        // Notifications click (toggle switch when clicking the whole row)
        layoutNotifications.setOnClickListener(v -> {
            switchNotifications.setChecked(!switchNotifications.isChecked());
        });

        // Language click
        layoutLanguage.setOnClickListener(v -> {
            showLanguageDialog();
        });

        // Currency click
        layoutCurrency.setOnClickListener(v -> {
            showCurrencyDialog();
        });

        // Privacy click
        layoutPrivacy.setOnClickListener(v -> {
            showPrivacyPolicy();
        });

        // About click
        layoutAbout.setOnClickListener(v -> {
            showAboutDialog();
        });

        // Logout click
        layoutLogout.setOnClickListener(v -> {
            showLogoutConfirmation();
        });
    }

    private void loadPreferences() {
        boolean notifications = sp.getBoolean("notifications", true);
        switchNotifications.setChecked(notifications);
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Profile");
        builder.setMessage("Profile editing will be available in the next update!");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showLanguageDialog() {
        final String[] languages = {"English", "Spanish", "French", "German", "Chinese", "Arabic"};
        int selectedLanguage = sp.getInt("selectedLanguage", 0);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Language");
        builder.setSingleChoiceItems(languages, selectedLanguage, (dialog, which) -> {
            editor.putInt("selectedLanguage", which);
            editor.putString("language", languages[which]);
            editor.apply();
            Toast.makeText(getContext(), "Language changed to " + languages[which], Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showCurrencyDialog() {
        final String[] currencies = {"USD ($)", "EUR (€)", "GBP (£)", "PKR (₨)", "INR (₹)", "AED (د.إ)"};
        int selectedCurrency = sp.getInt("selectedCurrency", 0);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Currency");
        builder.setSingleChoiceItems(currencies, selectedCurrency, (dialog, which) -> {
            editor.putInt("selectedCurrency", which);
            editor.putString("currency", currencies[which]);
            editor.apply();
            Toast.makeText(getContext(), "Currency changed to " + currencies[which], Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showPrivacyPolicy() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Privacy Policy");
        builder.setMessage("We value your privacy. Your personal information is protected and will never be shared with third parties without your consent.\n\n" +
                "• We collect only necessary booking information\n" +
                "• Your payment details are encrypted\n" +
                "• You can delete your account anytime\n" +
                "• We don't share your data with advertisers");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("About Hotel Booking App");
        builder.setMessage("Version: 1.0.0\n\n" +
                "Find and book the perfect hotel for your stay.\n\n" +
                "Features:\n" +
                "• Search thousands of hotels\n" +
                "• Easy booking process\n" +
                "• Save your favorites\n" +
                "• Secure payments\n\n" +
                "© 2025 Hotel Booking App. All rights reserved.");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showLogoutConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setPositiveButton("Logout", (dialog, which) -> {
            performLogout();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                requireContext().getResources().getColor(android.R.color.holo_red_dark));
    }

    private void performLogout() {
        Toast.makeText(getContext(), "Logging out...", Toast.LENGTH_SHORT).show();

        // Clear SharedPreferences
        SharedPreferences loginPrefs = requireContext().getSharedPreferences("LoginPrefs", requireContext().MODE_PRIVATE);
        SharedPreferences.Editor loginEditor = loginPrefs.edit();
        loginEditor.clear();
        loginEditor.apply();

        // Clear settings preferences
        editor.clear();
        editor.apply();

        // Sign out from Firebase
        auth.signOut();

        // Navigate to Login page
        Intent intent = new Intent(getActivity(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // Finish current activity
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}