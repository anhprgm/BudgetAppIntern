package com.vvtvofficial.quanlychitieu.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.vvtvofficial.quanlychitieu.R;

public class UserActivity extends AppCompatActivity {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private LinearLayout resetEmailButton, resetPassWordButton, deleteAccButton, needAHelp, signOut, editName;
    private TextView userName;
    private ImageView userImage;
    private String email = "";
    private String nameDisplay = "";
    private ProgressDialog loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user);
        ImageView returnKey = findViewById(R.id.returnKey);
        loader = new ProgressDialog(this);

        binding();
        returnKey.setOnClickListener(v -> onBackPressed());

        if (user != null) {
            // Name, email address, and profile photo Url
            email = user.getEmail();
            nameDisplay = user.getDisplayName();
            userName.setText(nameDisplay);
        }
        // changeUrNameDisplay
        editName.setOnClickListener(v -> ChangeName());
        // changeEmail
        resetEmailButton.setOnClickListener(v -> AcceptOrDeny());
        // change Ur Password
        resetPassWordButton.setOnClickListener(v -> ChangePassword());
        //sign Out
        signOut.setOnClickListener(v -> signOut());
    }

    private void binding() {
        resetEmailButton = findViewById(R.id.resetEmailButton);
        resetPassWordButton = findViewById(R.id.resetPassWordButton);
        editName = findViewById(R.id.changeNameDisplay);
        signOut = findViewById(R.id.SignOut);
        needAHelp = findViewById(R.id.needAHelp);
        userName = findViewById(R.id.nameUser);
        userImage = findViewById(R.id.userImage);
        deleteAccButton = findViewById(R.id.deleteAccButton);
    }

    private void AcceptOrDeny() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.accept_or_deny, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final EditText email = myView.findViewById(R.id.email);
        final EditText password = myView.findViewById(R.id.password);
        final TextView cancel = myView.findViewById(R.id.cancel);
        final TextView save = myView.findViewById(R.id.save);
        final EditText reEmail = myView.findViewById(R.id.reString);

        reEmail.setHint("Nhập Lại Email");

        save.setOnClickListener(v -> {
            String emailString = email.getText().toString();
            String passwordString = password.getText().toString();
            String reEmailString = reEmail.getText().toString();
            if (TextUtils.isEmpty(emailString)) {
                email.setError("Không được để trống");
            }
            if (TextUtils.isEmpty(passwordString)) {
                password.setError("Không được để trống");
            }
            if (TextUtils.isEmpty(reEmailString)) {
                reEmail.setError("Không được để trống");
            }
            else {
                AuthCredential credential = EmailAuthProvider
                        .getCredential(emailString, passwordString);

                user.reauthenticate(credential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                user.updateEmail(reEmailString)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                auth.signOut();
                                                Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                Log.d("AAA", "User email address updated.");
                                            }
                                        });
                            }
                        });
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
    private void ChangeName() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.accept_or_deny, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final EditText email = myView.findViewById(R.id.email);
        final EditText password = myView.findViewById(R.id.password);
        final TextView cancel = myView.findViewById(R.id.cancel);
        final TextView save = myView.findViewById(R.id.save);
        final EditText reName = myView.findViewById(R.id.reString);

        email.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        reName.setHint("Nhập Tên Mới");

        save.setOnClickListener(v -> {
            String newName = reName.getText().toString().trim();

            if (TextUtils.isEmpty(newName)) {
                reName.setError("Không Được Bỏ trống");
            } else {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(newName)
                        .build();
                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("AAA", "User profile updated.");
                            }
                        });
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void ChangePassword() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.accept_or_deny, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final EditText email = myView.findViewById(R.id.email);
        final EditText password = myView.findViewById(R.id.password);
        final TextView cancel = myView.findViewById(R.id.cancel);
        final TextView save = myView.findViewById(R.id.save);
        final EditText rePass = myView.findViewById(R.id.reString);

        rePass.setHint("Nhập Mật Khẩu Mới");

        save.setOnClickListener(v -> {
            String emailString = email.getText().toString();
            String passwordString = password.getText().toString();
            String rePassString = rePass.getText().toString();
            if (TextUtils.isEmpty(emailString)) {
                email.setError("Không được để trống");
            }
            if (TextUtils.isEmpty(passwordString)) {
                password.setError("Không được để trống");
            }
            if (TextUtils.isEmpty(rePassString)) {
                rePass.setError("Không được để trống");
            }
            else {

                loader.setMessage("Đang xử lý");
                loader.setCanceledOnTouchOutside(false);
                loader.show();

                AuthCredential credential = EmailAuthProvider
                        .getCredential(emailString, passwordString);

                user.reauthenticate(credential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                user.updatePassword(rePassString)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                auth.signOut();
                                                Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                Log.d("AAA", "User pass address updated.");
                                            }
                                        });
                            }
                            loader.dismiss();
                        });
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void signOut() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.accept_or_deny, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final EditText email = myView.findViewById(R.id.email);
        final EditText password = myView.findViewById(R.id.password);
        final TextView cancel = myView.findViewById(R.id.cancel);
        final TextView save = myView.findViewById(R.id.save);
        final EditText rePass = myView.findViewById(R.id.reString);
        email.setVisibility(View.GONE);
        rePass.setVisibility(View.GONE);
        password.setVisibility(View.GONE);

        save.setText("Đăng Xuất");
        save.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finishAffinity();
            dialog.dismiss();
        });
        cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();

    }
}