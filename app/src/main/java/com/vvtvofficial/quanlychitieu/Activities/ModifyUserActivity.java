package com.vvtvofficial.quanlychitieu.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vvtvofficial.quanlychitieu.R;

public class ModifyUserActivity extends AppCompatActivity {
    private TextView textViewForgotPass;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth mAuth;
    private LinearLayout resetEmailButton, resetPassWordButton, deleteAccButton, needAHelp, signOut, editName;
    private ImageView userImage, save, returnKey;
    private String email = "";
    private String nameDisplay = "";
    private EditText emailInput, passwordInput, reString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user);
        binding();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            email = user.getEmail();
            nameDisplay = user.getDisplayName();
        }
        authIsU();
        returnVoid(returnKey);
    }

    private void binding() {
        returnKey = findViewById(R.id.returnKey);
        mAuth = FirebaseAuth.getInstance();
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        reString = findViewById(R.id.reString);
        save = findViewById(R.id.save);
        textViewForgotPass = findViewById(R.id.textViewForgotPass);

    }

    private void returnVoid(ImageView x) {
        x.setOnClickListener(v -> onBackPressed());
    }

    private void authIsU() {
            passwordInput.setVisibility(View.GONE);
            textViewForgotPass.setText("Nhập Email Tài Khoản Cần Lấy Mật Khẩu");
            save.setOnClickListener(v -> {
                String emailString = emailInput.getText().toString().trim();
                if (TextUtils.isEmpty(emailString)) {
                    emailInput.setError("Nhập Email");
                } else {
                    mAuth.sendPasswordResetEmail(emailString).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(this, LoginActivity.class);
                            startActivity(intent);
                            Toast.makeText(this, "Đã Gửi Mật Khẩu Qua Email", Toast.LENGTH_SHORT).show();
                        } else {

                        }
                    });

                }
            });
    }
}