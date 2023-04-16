package com.aksapps.collageappadmin.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aksapps.collageappadmin.R;

public class LoginActivity extends AppCompatActivity {
    private EditText userEmail, userPassword;
    private TextView tvShow;
    private RelativeLayout loginInBtn;

    private String email = "", password = "";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmail = findViewById(R.id.user_email);
        userPassword = findViewById(R.id.user_password);
        tvShow = findViewById(R.id.txt_show);
        loginInBtn = findViewById(R.id.login_btn);

        sharedPreferences = this.getSharedPreferences("Login", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (sharedPreferences.getString("isLogin", "false").equals("Yes")) {
            openDash();
        }

        tvShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userPassword.getInputType() == 144) {
                    userPassword.setInputType(129);
                    tvShow.setText("Show");
                } else {
                    userPassword.setInputType(144);
                    tvShow.setText("Hide");
                }
                userPassword.setSelection(userPassword.getText().length());
            }
        });

        loginInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void validateData() {
        email = userEmail.getText().toString();
        password = userPassword.getText().toString();

        if (email.isEmpty()) {
            userEmail.setError("Email is Required.");
            userEmail.requestFocus();
        } else if (password.isEmpty()) {
            userPassword.setError("Password is Required.");
            userPassword.requestFocus();
        } else if (email.equals("admin@gmail.com") && password.equals("12345")) {
            editor.putString("isLogin", "Yes");
            editor.commit();
            openDash();
        } else {
            Toast.makeText(this, "Please Check Email and Password again!", Toast.LENGTH_LONG).show();
        }
    }

    private void openDash() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}