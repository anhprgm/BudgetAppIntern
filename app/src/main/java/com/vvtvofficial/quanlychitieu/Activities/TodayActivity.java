package com.vvtvofficial.quanlychitieu.Activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vvtvofficial.quanlychitieu.DataBase.Data;
import com.vvtvofficial.quanlychitieu.R;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class TodayActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference budgetRef;
    private TextView todayBudgetTotal;
    private LinearLayout todayPickerBtn;
    private ProgressDialog loader;
    private String DateString = getTodayTime();
    private String postKey = "";
    final static String DATE_FORMAT = "dd-MM-yyyy";
    private String item = "";
    private int amount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        budgetRef = FirebaseDatabase.getInstance().getReference().child("budget").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        recyclerView = findViewById(R.id.recyclerViewToday);
        loader = new ProgressDialog(this);
        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalBudget = 0;
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Data dataSnap = snap.getValue(Data.class);
                    assert dataSnap != null;
                    if (dataSnap.getData().equals(getTodayTime())) {
                        totalBudget += dataSnap.getAmount();
                    }
                }
                String text = "Chi Tiêu: " + dotMoney(totalBudget) + "đ";
                todayBudgetTotal.setText(text);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding();
        todayPickerBtn.setOnClickListener(v -> pickDate());

    }
    private void binding() {
        todayBudgetTotal = findViewById(R.id.totalBudgetAmountTextView);
        todayPickerBtn = findViewById(R.id.todayPicker);
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(budgetRef, Data.class)
                .build();
        FirebaseRecyclerAdapter<Data, TodayActivity.MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, TodayActivity.MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TodayActivity.MyViewHolder holder, int position, @NonNull Data model) {
                if (DateString.equals(model.getData())) {
                    holder.setDate("Thời gian: " + model.getData());
                    if (Objects.equals(model.getItem(), "Thu Nhập")) {
                        holder.setItemAmountGreen("Thu Nhập: " + dotMoney(model.getAmount()) + "đ");
                        holder.setItemName(model.getItem());
                    } else {
                        holder.setItemAmount("Tiêu: " + dotMoney(model.getAmount()) + "đ");
                        holder.setItemName("Tiêu Cho: " + model.getItem());
                    }
                    if (model.getNotes() == null) {
                        holder.notes.setVisibility(View.GONE);
                    } else {
                        holder.notes.setVisibility(View.VISIBLE);
                        holder.setNotes("Note: " + model.getNotes());
                    }

                    switch (model.getItem()) {
                        case "Di Chuyển":
                            holder.imageView.setImageResource(R.drawable.ic_transport);
                            break;
                        case "Ăn Uống":
                            holder.imageView.setImageResource(R.drawable.ic_eat);
                            break;
                        case "Nhà Cửa":
                            holder.imageView.setImageResource(R.drawable.ic_home);
                            break;
                        case "Giải trí":
                            holder.imageView.setImageResource(R.drawable.ic_entertainment);
                            break;
                        case "Học Tập":
                            holder.imageView.setImageResource(R.drawable.ic_study);
                            break;
                        case "Từ Thiện":
                            holder.imageView.setImageResource(R.drawable.ic_charity);
                            break;
                        case "Sức Khỏe":
                            holder.imageView.setImageResource(R.drawable.ic_health);
                            break;
                        case "Cá Nhân":
                            holder.imageView.setImageResource(R.drawable.ic_clothes);
                            break;
                        case "Thu Nhập":
                            holder.imageView.setImageResource(R.drawable.ic_income);
                            break;
                        case "Khác":
                            holder.imageView.setImageResource(R.drawable.ic_other);
                            break;
                    }
                    holder.itemView.setVisibility(View.VISIBLE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                } else {
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));

                }
                holder.view.setOnLongClickListener(v -> {
                    postKey = getRef(position).getKey();
                    item = model.getItem();
                    amount = model.getAmount();
                    updateData();
                    return false;
                });
            }

            @NonNull
            @Override
            public TodayActivity.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout, parent, false);
                return new MyViewHolder(view);

            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View view;
        public ImageView imageView;
        public TextView notes;
        public CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            cardView = itemView.findViewById(R.id.retrieve);
            imageView = itemView.findViewById(R.id.imageView);
            notes = itemView.findViewById(R.id.note);
        }


        public void setItemName(String itemName) {
            TextView item = view.findViewById(R.id.item);
            item.setText(itemName);
        }

        public void setItemAmount(String itemAmount) {
            TextView amount = view.findViewById(R.id.amount);
            amount.setText(itemAmount);
        }
        public void setItemAmountGreen(String itemAmount) {
            TextView amount = view.findViewById(R.id.amount);
            amount.setText(itemAmount);
            amount.setTextColor(Color.rgb(0, 121, 107));
        }
        public void setDate(String itemDate) {
            TextView date = view.findViewById(R.id.date);
            date.setText(itemDate);
        }

        public void setNotes(String notes) {
            TextView note = view.findViewById(R.id.note);
            note.setText(notes);
        }
    }
    private String getTodayTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }
    private void updateData() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View mview = inflater.inflate(R.layout.update_layout, null);

        myDialog.setView(mview);
        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final TextView mItem = mview.findViewById(R.id.itemName);
        final EditText mAmount = mview.findViewById(R.id.amount);
        final EditText mNotes = mview.findViewById(R.id.note);

        mNotes.setVisibility(View.GONE);

        mItem.setText(item);

        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());

        TextView delBtn = mview.findViewById(R.id.btnDelete);
        TextView updBtn = mview.findViewById(R.id.btnUpdate);

        updBtn.setOnClickListener(v -> {
            amount = Integer.parseInt(mAmount.getText().toString());

            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            String date = dateFormat.format(cal.getTime());

            MutableDateTime epoch = new MutableDateTime();
            epoch.setDate(0);
            DateTime now = new DateTime();
            Months month = Months.monthsBetween(epoch, now);
            Weeks weeks = Weeks.weeksBetween(epoch, now);
            Data data = new Data(item, date, postKey, null, amount, month.getMonths(), weeks.getWeeks());
            assert postKey != null;
            budgetRef.child(postKey).setValue(data).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(TodayActivity.this, "Cập Nhật Thành Công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TodayActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
                }

            });
            dialog.dismiss();
        });
        delBtn.setOnClickListener(v -> {
            budgetRef.child(postKey).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(TodayActivity.this, "Xóa Thành Công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TodayActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
                }

            });
            dialog.dismiss();
        });

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

    private void pickDate() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.gett_date_layout, null);

        myDialog.setView(view);
        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final EditText day = view.findViewById(R.id.day);
        final EditText month = view.findViewById(R.id.month);
        final EditText year = view.findViewById(R.id.year);

        final TextView cancel = view.findViewById(R.id.cancel);
        final TextView accept = view.findViewById(R.id.accept);

        accept.setOnClickListener(v -> {
            String text_day = day.getText().toString().trim();
            String text_month = month.getText().toString().trim();
            String text_year = year.getText().toString().trim();

            if (TextUtils.isEmpty(text_day)) {
                day.setError("Nhap Ngay");
            }
            if (TextUtils.isEmpty(text_month)) {
                month.setError("Nhap Thang");
            }
            if (TextUtils.isEmpty(text_year)) {
                year.setError("Nhap Nam");
            }
            if (!isDateValid(generateDate(text_day, text_month, text_year))) {
                Toast.makeText(this, "Nhap Sai Ngay", Toast.LENGTH_SHORT).show();
            } else {
                DateString = generateDate(text_day, text_month, text_year);
                Toast.makeText(this, DateString, Toast.LENGTH_SHORT).show();
                onStart();
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    public String generateDate(String text_day, String text_month, String text_year) {
        StringBuilder DateText = new StringBuilder();
        if (text_day.length() == 1){
            DateText.append(0);
        }
        DateText.append(text_day);
        DateText.append('-');
        if (text_month.length() == 1) {
            DateText.append(0);
        }
        DateText.append(text_month);
        DateText.append('-');
        if (text_year.length() == 4) {
            DateText.append(text_year);
        }
        return DateText.toString();
    }
    public static boolean isDateValid(String date) {
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
