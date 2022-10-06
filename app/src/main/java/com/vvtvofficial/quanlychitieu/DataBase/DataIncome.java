package com.vvtvofficial.quanlychitieu.DataBase;

import org.joda.time.Months;

public class DataIncome {
    String notes, id, date;
    int income;
    int month;
    public DataIncome(){

    }
    public DataIncome(String notes, String id, String date, int income, int month) {
        this.notes = notes;
        this.id = id;
        this.date = date;
        this.income = income;
        this.month = month;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }
}
