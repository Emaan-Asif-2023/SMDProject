package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private Database db;
    private TextView welcomeText;
    private Button btnManageUsers, btnManageBookings, btnManageHotels, btnLogout;

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
            auth.signOut();
            startActivity(new Intent(this, Login.class));
            finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}