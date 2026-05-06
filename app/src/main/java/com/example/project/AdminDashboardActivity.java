package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private Database db;
    private TextView welcomeText;
    private LinearLayout btnManageUsers, btnManageBookings, btnManageHotels, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        auth = FirebaseAuth.getInstance();
        db = new Database(this);
        db.open();

        welcomeText = findViewById(R.id.welcomeText);
        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnManageBookings = findViewById(R.id.btnManageBookings);
        btnManageHotels = findViewById(R.id.btnManageHotels);
        btnLogout = findViewById(R.id.btnLogout);

        verifyAdminAccess();

        btnManageUsers.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminUsersActivity.class));
        });

        btnManageBookings.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminBookingsActivity.class));
        });

        btnManageHotels.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminHotelsActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            showLogoutConfirmation();
        });
    }

    private void verifyAdminAccess() {
        String userEmail = auth.getCurrentUser().getEmail();
        if (!isAdminEmail(userEmail)) {
            Toast.makeText(this, "Access Denied!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }

    private boolean isAdminEmail(String email) {
        String[] adminEmails = {"admin@test.com", "admin@gmail.com"};
        for (String adminEmail : adminEmails) {
            if (adminEmail.equals(email)) {
                return true;
            }
        }
        return false;
    }

    private void showLogoutConfirmation() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_logout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
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
        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();

        // Sign out from Firebase
        auth.signOut();

        // Navigate to Login page
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // Finish current activity
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}