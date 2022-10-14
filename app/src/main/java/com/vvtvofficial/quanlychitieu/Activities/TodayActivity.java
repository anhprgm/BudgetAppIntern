package com.vvtvofficial.quanlychitieu.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.internal.FastSafeIterableMap;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vvtvofficial.quanlychitieu.DataBase.Data;
import com.vvtvofficial.quanlychitieu.DataBase.DataIncome;
import com.vvtvofficial.quanlychitieu.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class TodayActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference budgetRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);
        mAuth = FirebaseAuth.getInstance();
        budgetRef = FirebaseDatabase.getInstance().getReference().child("budget").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        Data data = new Data();
        recyclerView = findViewById(R.id.recyclerViewToday);

        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    DataIncome dataIncome = snap.getValue(DataIncome.class);
                    assert dataIncome != null;
                    if (getTodayTime() == dataIncome.getData()) {
                        data.setData(dataIncome.getData());
                        data.setAmount(dataIncome.getAmount());
                        data.setId(dataIncome.getId());
                        data.setItem(dataIncome.getItem());
                        data.setMonth(dataIncome.getMonth());
                        data.setNotes(dataIncome.getNotes());
                        data.setWeek(dataIncome.getWeek());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                return;
            }
        });
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
                if (getTodayTime().equals(model.getData())) {
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


            }

            @NonNull
            @Override
            public TodayActivity.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout, parent, false);
//                return new TodayActivity(view);
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
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
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
}