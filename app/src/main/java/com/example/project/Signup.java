package com.example.project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Signup extends AppCompatActivity {

    TextView account, login;
    EditText name, email,password, confirmPassword;
    Button signup;


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

        account=findViewById(R.id.tvaccount);
        login=findViewById(R.id.tvlogin);
        name=findViewById(R.id.etname);
        email=findViewById(R.id.etemail);
        password=findViewById(R.id.etpass);
        confirmPassword=findViewById(R.id.etconfirmpass);
        signup=findViewById(R.id.btnsignup);
        Database db = new Database(this);
        db.open();

        signup.setOnClickListener(v -> {

            String n = name.getText().toString();
            String e = email.getText().toString();
            String p = password.getText().toString();
            String cp = confirmPassword.getText().toString();
            if(db.isEmailExists(e))
            {
                Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            if(n.isEmpty() || e.isEmpty() || p.isEmpty() || cp.isEmpty())
            {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!p.equals(cp))
            {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            Person person = new Person(n, e, p);

            long id = db.insertPerson(person);

            if(id > 0)
            {
                Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Signup Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}