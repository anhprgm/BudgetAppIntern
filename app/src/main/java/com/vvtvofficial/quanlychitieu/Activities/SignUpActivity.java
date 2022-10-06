package com.vvtvofficial.quanlychitieu.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.vvtvofficial.quanlychitieu.R;

public class SignUpActivity extends AppCompatActivity {
    private EditText email, password;
    private LinearLayout loginButton;
    private TextView SignUpButton;

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        binding();
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        loginButton.setOnClickListener(v -> onBackPressed());
        SignUpButton.setOnClickListener(v -> {
            String emailString = email.getText().toString().trim();
            String passwordString = password.getText().toString().trim();

            if (emailString.isEmpty()) {
                email.setError("Chưa Nhập Email");
            }
            if (TextUtils.isEmpty(passwordString)) {
                password.setError("Chưa Nhập Mật Khẩu");
            }

            else {
                progressDialog.setMessage("Đang Đăng Ký");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                mAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(SignUpActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }
    private void binding() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        SignUpButton = findViewById(R.id.SignUpButton);
    }
}