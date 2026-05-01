package com.example.project;

import android.content.Intent; // ✅ ADDED THIS IMPORT
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
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
        EdgeToEdge.enable(this);
        setContentView(R.layout.signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
                            Toast.makeText(Signup.this, "User created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Signup.this, HomeActivity.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Signup.this, "Signup failed", Toast.LENGTH_SHORT).show();
                        }
                    });


            Person person = new Person(n, e, p);
            long id = db.insertPerson(person);

            if (id > 0) {
                Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show();

                // Optional: Automatically go back to Login after successful signup
                // finish();
            } else {
                Toast.makeText(this, "Signup Failed", Toast.LENGTH_SHORT).show();
            }
        });

        login.setOnClickListener(v -> {
            Intent i = new Intent(Signup.this, Login.class);
            startActivity(i);
            finish();
        });
    }
}