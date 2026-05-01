package com.example.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    ImageView logo;
    TextView welcome, forgotpass, signup;
    EditText mail, password;
    Button login;
    CheckBox remember;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    Database db;

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

        sp = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        editor = sp.edit();

        db = new Database(this);
        db.open();

        if (db.isTableEmpty()) {
            db.insertTestData();
            Toast.makeText(this, "Test data inserted!", Toast.LENGTH_SHORT).show();
        }

        signup.setOnClickListener(v -> {
            Intent i = new Intent(Login.this, Signup.class);
            startActivity(i);
        });

        boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            String savedEmail = sp.getString("email", "");
            mail.setText(savedEmail);
            remember.setChecked(true);
            Toast.makeText(this, "Welcome back " + savedEmail, Toast.LENGTH_SHORT).show();
        }

        login.setOnClickListener(v -> {
            String e = mail.getText().toString().trim();
            String p = password.getText().toString().trim();

            if (e.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Enter email & password", Toast.LENGTH_SHORT).show();
                return;
            }

            Person person = db.login(e, p);

            if (person != null) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

                if (remember.isChecked()) {
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("email", e);
                    editor.apply();
                } else {
                    editor.clear();
                    editor.apply();
                }

                // Intent i = new Intent(Login.this, Home.class);
                // startActivity(i);
                // finish();
            } else {
                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        });
        forgotpass.setOnClickListener( v->
        {
            Intent i = new Intent(Login.this, ForgetPassword.class);
        });
    }
}