package com.vvtvofficial.quanlychitieu.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vvtvofficial.quanlychitieu.R;

import java.util.Objects;
import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 0;
    private EditText email;
    private TextInputEditText password;
    private TextView loginButton, forgotPassword, dontMe, helloUser;
    private ImageView fingerButton;
    private LinearLayout SignUpButton;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private boolean check = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        SignUpButton = findViewById(R.id.SignUpButton);
        forgotPassword = findViewById(R.id.forgotPassword);
        dontMe = findViewById(R.id.dontMe);
        helloUser = findViewById(R.id.helloUser);
        fingerButton = findViewById(R.id.fingerprintButton);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        showPass.setOnClickListener(v -> {
////            password.setTransformationMethod();
//        });

        progressDialog = new ProgressDialog(this);
        if (currentUser != null) {
            helloUser.setText("XIN CHÀO " + currentUser.getDisplayName());
            fingerButton.setVisibility(View.VISIBLE);
            check = true;
            email.setVisibility(View.GONE);
            DontMe();
        }
        SignUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ModifyUserActivity.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(v -> {
            String emailString;
            if (!check) {
                emailString = email.getText().toString().trim();
            }
            else {
                assert currentUser != null;
                emailString = currentUser.getEmail();
            }
            String passwordString = Objects.requireNonNull(password.getText()).toString().trim();
            assert emailString != null;
            if (emailString.isEmpty()) {
                email.setError("Chưa Nhập Email");
            }
            if (TextUtils.isEmpty(passwordString)) {
                password.setError("Chưa Nhập Mật Khẩu");
            }

            else {
                progressDialog.setMessage("Đang Đăng Nhập");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                mAuth.signInWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        progressDialog.dismiss();
                    } else {
                        forgotPassword.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this, "Sai Mật Khẩu Hoặc Tài Khoản", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });

            BiometricManager biometricManager = BiometricManager.from(this);
            switch (biometricManager.canAuthenticate()) {

                // this means we can use biometric sensor
                case BiometricManager.BIOMETRIC_SUCCESS:
                    fingerBioAuth();
                    break;

                // this means that the device doesn't have fingerprint sensor
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                    Toast.makeText(LoginActivity.this, "This device doesn't have a fingerprint sensor", Toast.LENGTH_SHORT).show();
                    fingerButton.setVisibility(View.GONE);
                    break;

                // this means that biometric sensor is not available
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    Toast.makeText(LoginActivity.this, "The biometric sensor is currently unavailable", Toast.LENGTH_SHORT).show();
                    fingerButton.setVisibility(View.GONE);
                    break;

                // this means that the device doesn't contain your fingerprint
                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                    Toast.makeText(LoginActivity.this, "Your device doesn't have fingerprint saved,please check your security settings", Toast.LENGTH_SHORT).show();
                    fingerButton.setVisibility(View.GONE);
                    break;
                // don't known error
                case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                    Toast.makeText(LoginActivity.this, "unknown error ", Toast.LENGTH_SHORT).show();
                    fingerButton.setVisibility(View.GONE);
                    break;

            }

        }

    private void DontMe() {
        dontMe.setOnClickListener(v -> {
            auth.signOut();
            fingerButton.setVisibility(View.GONE);
            email.setVisibility(View.VISIBLE);
        });
    }

    private void fingerBioAuth() {
        Executor executor = ContextCompat.getMainExecutor(this);
        // this will give us result of AUTHENTICATION
        final BiometricPrompt biometricPrompt = new BiometricPrompt(LoginActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            // THIS METHOD IS CALLED WHEN AUTHENTICATION IS SUCCESS
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("Đăng Nhập Bằng Vân Tay")
                .setDescription("Use your fingerprint to login ").setNegativeButtonText("Cancel").build();
        fingerButton.setOnClickListener(v -> {
            biometricPrompt.authenticate(promptInfo);

        });
    }
}