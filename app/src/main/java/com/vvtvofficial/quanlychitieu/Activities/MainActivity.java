package com.vvtvofficial.quanlychitieu.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vvtvofficial.quanlychitieu.DataBase.Data;
import com.vvtvofficial.quanlychitieu.DataBase.DataIncome;
import com.vvtvofficial.quanlychitieu.R;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private CardView budgetCardView, todayCardView;
    private ImageView user_setting, fab;
    private SwitchMaterial expandMenu;
    private DatabaseReference incomeRef, budgetRef;
    private TextView nameUser, emailUser, moneyBalance;
    private TextView budgetTv, todayTv, weekTv, monthTV, savingTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding();
//        Intent intentx =new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
//        startActivity(intentx);
        expandMenu.setVisibility(View.VISIBLE);
        budgetRef = FirebaseDatabase.getInstance().getReference().child("budget").child(Objects.requireNonNull(auth.getCurrentUser()).getUid());
        incomeRef = FirebaseDatabase.getInstance().getReference().child("incomeMoney").child(Objects.requireNonNull(auth.getCurrentUser()).getUid());
        budgetCardView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BudgetActivity.class);
            startActivity(intent);
        });
        user_setting.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
            startActivity(intent);
        });

        if (user != null) {
            // Name, email address, and profile photo Url
            emailUser.setText(user.getEmail());
            nameUser.setText("Xin Chào \n" + user.getDisplayName());
        }

        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount = 0;
                int totalBudget = 0;
                int totalWeekAmount = 0;
                int totalMonthAmount = 0;
                int todayAmount = 0;

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Data data = snap.getValue(Data.class);
                    assert data != null;
                    if (data.getItem().equals("Thu Nhập")) {
                        totalAmount += data.getAmount();
                    } else {
                        totalBudget += data.getAmount();
                        if (data.getWeek() == getTime(1)) {
                            totalWeekAmount += data.getAmount();
                        }
                        if (data.getMonth() == getTime(0)) {
                            totalMonthAmount += data.getAmount();
                        }
                        if (data.getData().equals(getTodayTime())) {
                            todayAmount += data.getAmount();
                        }
                    }
                }
                String sTotal =  dotMoney(totalAmount - totalBudget) + " VNĐ";
                moneyBalance.setText(sTotal);
                budgetTv.setText((totalBudget / 1000) + "K");
                weekTv.setText((totalWeekAmount / 1000) + "K");
                monthTV.setText((totalMonthAmount / 1000) + "K");
                todayTv.setText((todayAmount / 1000) + "K");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                return;
            }
        });
        savingTV = findViewById(R.id.savingsTv);
        savingTV.setOnClickListener(v -> {
            Intent intent = new Intent(this, TestActivity.class);
            startActivity(intent);
        });

        fab.setOnClickListener(v -> addItem());
        todayCardView.setOnClickListener(v -> {
            Intent intent = new Intent(this, TodayActivity.class);
            startActivity(intent);
        });
    }
    private void binding() {
        budgetCardView = findViewById(R.id.budgetCardView);
        user_setting = findViewById(R.id.user_setting);
        nameUser = findViewById(R.id.nameUser);
        emailUser = findViewById(R.id.email);
        moneyBalance = findViewById(R.id.money_balance);
        fab = findViewById(R.id.addIncome);
        expandMenu = findViewById(R.id.expanded_menu);

        budgetTv = findViewById(R.id.budgetTv);
        monthTV = findViewById(R.id.monthTv);
        todayTv = findViewById(R.id.todayTv);
        weekTv = findViewById(R.id.weekTv);
        savingTV = findViewById(R.id.savingsTv);

        todayCardView = findViewById(R.id.todayCardView);
    }

    private void addItem() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_layout, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final Spinner itemSpinner = myView.findViewById(R.id.itemsSpinner);
        final EditText amount = myView.findViewById(R.id.amount);
        final TextView cancel = myView.findViewById(R.id.cancel);
        final TextView save = myView.findViewById(R.id.save);
        final EditText note = myView.findViewById(R.id.note);
        final TextView title = myView.findViewById(R.id.title);

        itemSpinner.setVisibility(View.GONE);
        note.setVisibility(View.VISIBLE);
        title.setText("Thêm Thu Nhập");
        save.setOnClickListener(v -> {
            String incomeAmount = amount.getText().toString().trim();
            String notes = note.getText().toString().trim();
            if (TextUtils.isEmpty(notes)) {
                notes = null;
            }
            if (TextUtils.isEmpty(incomeAmount)) {
                amount.setError("Nhập Khác 0");
            } else {
                String id = incomeRef.push().getKey();
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal = Calendar.getInstance();
                String date = dateFormat.format(cal.getTime());

                MutableDateTime epoch = new MutableDateTime();
                epoch.setDate(0);
                DateTime now = new DateTime();
                Months month = Months.monthsBetween(epoch, now);
                Weeks weeks = Weeks.weeksBetween(epoch, now);
                Data data = new Data("Thu Nhập", date, id, notes, Integer.parseInt(incomeAmount), month.getMonths(), weeks.getWeeks());
                assert id != null;
                budgetRef.child(id).setValue(data).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Thêm Thành Công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    public String dotMoney(int x) {
        StringBuilder xDot = new StringBuilder();
        int count  = 0;
        while (x > 0) {
            xDot.append(x % 10);
            x /= 10;
            count++;
            if (count % 3 == 0) xDot.append(',');
        }
        String vDot = xDot.reverse().toString();
        vDot = vDot.startsWith(",") ? vDot.substring(1) : vDot;
        return vDot;
    }

    private void expandMenu() {

    }

    private String getTodayTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    private int getTime(int x) {

        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months month = Months.monthsBetween(epoch, now);
        Weeks weeks = Weeks.weeksBetween(epoch, now);
        if (x == 0) return month.getMonths();
        return weeks.getWeeks();
    }
}
