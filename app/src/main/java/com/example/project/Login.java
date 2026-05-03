package com.example.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    ImageView logo;
    TextView welcome, forgotpass, signup;
    EditText mail, password;
    Button login;
    CheckBox remember;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    Database db;

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        logo = findViewById(R.id.ivLogo);
        welcome = findViewById(R.id.tvwelcome);
        forgotpass = findViewById(R.id.tvforgotpass);
        signup = findViewById(R.id.tvsignup);
        mail = findViewById(R.id.etmail);
        password = findViewById(R.id.etpassword);
        login = findViewById(R.id.btnlogin);
        remember = findViewById(R.id.cbRemember);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        db = new Database(this);
        db.open();
        ensureTestDataExists();
        fixAdminUser();
        db.close();

        if (user != null) {
            String email = user.getEmail();
            String name = user.getDisplayName() != null ? user.getDisplayName() : "User";


            syncFirebaseUserToLocalDb(user);

            checkAdminAndRedirect(email);
            return;
        }

        sp = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        editor = sp.edit();

        signup.setOnClickListener(v -> {
            Intent i = new Intent(Login.this, Signup.class);
            startActivity(i);
        });

        boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            String savedEmail = sp.getString("email", "");
            mail.setText(savedEmail);
            remember.setChecked(true);
        }

        login.setOnClickListener(v -> {
            String e = mail.getText().toString().trim();
            String p = password.getText().toString().trim();

            if (TextUtils.isEmpty(e)) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (TextUtils.isEmpty(p)) {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
                return;
            }
            else if(p.length() < 8) {
                Toast.makeText(this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(e, p)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();

                            if (remember.isChecked()) {
                                editor.putBoolean("isLoggedIn", true);
                                editor.putString("email", e);
                                editor.apply();
                            } else {
                                editor.clear();
                                editor.apply();
                            }


                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            syncFirebaseUserToLocalDb(firebaseUser);

                            checkAdminAndRedirect(e);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Login.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        forgotpass.setOnClickListener(v -> {
            Intent i = new Intent(Login.this, ForgetPasswordActivity.class);
            startActivity(i);
        });
    }

    private void syncFirebaseUserToLocalDb(FirebaseUser firebaseUser) {
        if (firebaseUser == null) return;

        Database syncDb = new Database(Login.this);
        syncDb.open();

        String email = firebaseUser.getEmail();
        String name = firebaseUser.getDisplayName();

        if (name == null || name.isEmpty()) {
            name = email.split("@")[0];
        }

        if (!syncDb.isEmailExists(email)) {

            Person person = new Person(name, email, "");
            person.setRole("user");

            if (email.equals("admin@test.com")) {
                person.setRole("admin");
            }

            syncDb.insertPerson(person);
        }

        syncDb.close();
    }

    private void ensureTestDataExists() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean dataInserted = prefs.getBoolean("testDataInserted", false);

        if (!dataInserted) {
            if (db.isTableEmpty() || db.getAllHotels().isEmpty()) {
                db.insertTestData();
                Toast.makeText(this, "Loading hotel data...", Toast.LENGTH_SHORT).show();
            }
            prefs.edit().putBoolean("testDataInserted", true).apply();
        } else {
            if (db.getAllHotels().isEmpty()) {
                db.insertTestData();
                Toast.makeText(this, "Reloading hotel data...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fixAdminUser() {
        Person adminPerson = db.login("admin@test.com", "admin12345");
        if (adminPerson != null) {
            if (!"admin".equals(adminPerson.getRole())) {
                adminPerson.setRole("admin");
                db.update(adminPerson);
            }
        } else {
            Person admin = new Person("Admin", "admin@test.com", "admin12345");
            admin.setRole("admin");
            db.insertPerson(admin);
        }
    }

    private void checkAdminAndRedirect(String email) {
        Database checkDb = new Database(Login.this);
        checkDb.open();

        boolean isAdmin = checkDb.isAdmin(email);

        if (isAdmin) {
            startActivity(new Intent(Login.this, AdminDashboardActivity.class));
        } else {
            startActivity(new Intent(Login.this, HomeActivity.class));
        }

        checkDb.close();
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