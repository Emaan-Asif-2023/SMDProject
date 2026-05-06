package com.example.project;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import android.widget.EditText;
import android.widget.ImageView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;

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

            if (name == null || name.isEmpty()) {
                Database db = new Database(requireContext());
                db.open();

                ArrayList<Person> persons = db.getAllPersons();
                for (Person p : persons) {
                    if (p.getEmail() != null && p.getEmail().equals(email)) {
                        name = p.getName();
                        break;
                    }
                }
                db.close();

                if (name != null && !name.isEmpty()) {
                    editor.putString("userName", name);
                    editor.apply();
                }
            }

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
        layoutProfile.setOnClickListener(v -> {
            showEditProfileDialog();
        });

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
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null);

        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);
        EditText etCurrentPassword = dialogView.findViewById(R.id.etCurrentPassword);
        EditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);
        TextView buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        TextView buttonSave = dialogView.findViewById(R.id.buttonSave);
        ImageView ivClose = dialogView.findViewById(R.id.ivClose);

        String currentName = textViewUserName.getText().toString();
        String currentEmail = textViewUserEmail.getText().toString();

        if (!currentName.equals("Guest User")) {
            etName.setText(currentName);
        }
        etEmail.setText(currentEmail);
        etEmail.setEnabled(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        buttonCancel.setOnClickListener(v -> dialog.dismiss());
        ivClose.setOnClickListener(v -> dialog.dismiss());

        buttonSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String currentPass = etCurrentPassword.getText().toString().trim();
            String newPass = etNewPassword.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!currentPass.isEmpty() || !newPass.isEmpty()) {
                if (currentPass.isEmpty()) {
                    Toast.makeText(getContext(), "Enter current password to change", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newPass.isEmpty()) {
                    Toast.makeText(getContext(), "Enter new password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newPass.length() < 8) {
                    Toast.makeText(getContext(), "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                updatePassword(currentPass, newPass, dialog);
            }

            updateProfileName(name, dialog);
        });

        dialog.show();
    }

    private void updateProfileName(String name, AlertDialog dialog) {
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            updateNameInLocalDb(name);

                            editor.putString("userName", name);
                            editor.apply();

                            textViewUserName.setText(name);

                            Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            updateNameInLocalDb(name);
            editor.putString("userName", name);
            editor.apply();
            textViewUserName.setText(name);
            Toast.makeText(getContext(), "Profile updated locally", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    }

    private void updateNameInLocalDb(String name) {
        Database db = new Database(requireContext());
        db.open();

        String email = user != null ? user.getEmail() : textViewUserEmail.getText().toString();
        Person person = db.login(email, ""); // Get current user

        if (person != null) {
            person.setName(name);
            db.update(person);
        }

        db.close();
    }

    private void updatePassword(String currentPassword, String newPassword, AlertDialog dialog) {
        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(passwordTask -> {
                                        if (passwordTask.isSuccessful()) {
                                            // Update password in local database
                                            updatePasswordInLocalDb(newPassword);
                                            Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getContext(), "Failed to update password: " +
                                                    passwordTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(getContext(), "Current password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updatePasswordInLocalDb(String newPassword) {
        Database db = new Database(requireContext());
        db.open();

        String email = user.getEmail();
        Person person = db.login(email, "");

        if (person != null) {
            person.setPassword(newPassword);
            db.update(person);
        }

        db.close();
    }

    private void showCurrencyDialog() {
        final String[] currencies = {"USD ($)", "EUR (€)", "GBP (£)", "PKR (₨)", "INR (₹)", "AED (د.إ)"};
        int selectedCurrency = sp.getInt("selectedCurrency", 0);

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_currency, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        TextView title = dialogView.findViewById(R.id.dialogTitle);
        title.setText("Select Currency");

        // Create radio group items dynamically
        LinearLayout radioGroup = dialogView.findViewById(R.id.radioGroup);

        for (int i = 0; i < currencies.length; i++) {
            View radioItem = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_radio_item, null);
            TextView radioText = radioItem.findViewById(R.id.radioText);
            View radioCircle = radioItem.findViewById(R.id.radioCircle);

            radioText.setText(currencies[i]);

            final int index = i;
            if (selectedCurrency == i) {
                radioCircle.setBackgroundResource(R.drawable.radio_selected);
            }

            radioItem.setOnClickListener(v -> {
                editor.putInt("selectedCurrency", index);
                editor.putString("currency", currencies[index]);
                editor.apply();
                Toast.makeText(getContext(), "Currency changed to " + currencies[index], Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });

            radioGroup.addView(radioItem);
        }

        TextView buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showPrivacyPolicy() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_privacy, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView buttonOk = dialogView.findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showAboutDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_about, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView buttonOk = dialogView.findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showLogoutConfirmation() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_logout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        TextView buttonLogout = dialogView.findViewById(R.id.buttonLogout);
        TextView buttonCancel = dialogView.findViewById(R.id.buttonCancel);

        buttonLogout.setOnClickListener(v -> {
            dialog.dismiss();
            performLogout();
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
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

    private void showLanguageDialog() {
        final String[] languages = {"English", "Spanish", "French", "German", "Chinese", "Arabic"};
        final String[] languageCodes = {"en", "es", "fr", "de", "zh", "ar"};

        // Find out which language is currently selected
        String currentCode = sp.getString("language_code", "en");
        int selectedLanguage = 0;
        for (int i = 0; i < languageCodes.length; i++) {
            if (languageCodes[i].equalsIgnoreCase(currentCode)) {
                selectedLanguage = i;
                break;
            }
        }

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_language, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        TextView title = dialogView.findViewById(R.id.dialogTitle);
        title.setText("Select Language");

        // Create radio group items dynamically
        LinearLayout radioGroup = dialogView.findViewById(R.id.radioGroup);

        for (int i = 0; i < languages.length; i++) {
            View radioItem = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_radio_item, null);
            TextView radioText = radioItem.findViewById(R.id.radioText);
            View radioCircle = radioItem.findViewById(R.id.radioCircle);

            radioText.setText(languages[i]);

            final int index = i;
            if (selectedLanguage == i) {
                radioCircle.setBackgroundResource(R.drawable.radio_selected);
            }

            radioItem.setOnClickListener(v -> {
                LocaleHelper.setLocale(requireContext(), languageCodes[index]);
                if (getActivity() != null) {
                    getActivity().recreate();
                }
                dialog.dismiss();
            });

            radioGroup.addView(radioItem);
        }

        TextView buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}