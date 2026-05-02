package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Signup extends AppCompatActivity {

    TextView account, login;
    EditText name, email, password, confirmPassword;
    Button signup;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        account = findViewById(R.id.tvaccount);
        login = findViewById(R.id.tvlogin);
        name = findViewById(R.id.etname);
        email = findViewById(R.id.etemail);
        password = findViewById(R.id.etpass);
        confirmPassword = findViewById(R.id.etconfirmpass);
        signup = findViewById(R.id.btnsignup);
        auth = FirebaseAuth.getInstance();

        Database db = new Database(this);
        db.open();

        signup.setOnClickListener(v -> {

            String n = name.getText().toString().trim();
            String e = email.getText().toString().trim();
            String p = password.getText().toString().trim();
            String cp = confirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(e)) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (TextUtils.isEmpty(n)) {
                Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (TextUtils.isEmpty(p)) {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (TextUtils.isEmpty(cp)) {
                Toast.makeText(this, "Enter password again", Toast.LENGTH_SHORT).show();
                return;
            }
            else if(p.length() < 8) {
                Toast.makeText(this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.isEmailExists(e)) {
                Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!p.equals(cp)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.createUserWithEmailAndPassword(e,p)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Person person = new Person(n, e, p);
                            long id = db.insertPerson(person);

                            Toast.makeText(Signup.this, "Account created!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Signup.this, HomeActivity.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Signup.this, "Signup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        login.setOnClickListener(v -> {
            Intent i = new Intent(Signup.this, Login.class);
            startActivity(i);
            finish();
        });
    }
}