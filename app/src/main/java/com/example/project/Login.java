package com.example.project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Login extends AppCompatActivity {

    ImageView logo;
    TextView welcome, forgotpass,signup;
    EditText mail,password;

    Button login;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        logo=findViewById(R.id.ivLogo);
        welcome=findViewById(R.id.tvwelcome);
        forgotpass=findViewById(R.id.tvforgotpass);
        signup=findViewById(R.id.tvsignup);
        mail=findViewById(R.id.etmail);
        password=findViewById(R.id.etpassword);
        login=findViewById(R.id.btnlogin);
    }
}