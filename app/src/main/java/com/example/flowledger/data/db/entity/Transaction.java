package com.example.flowledger.data.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private double amount;
    private int categoryId;
    private String type; // EXPENSE or INCOME
    private String note;
    private long timestamp;
    
    private String paymentMode;
    private String recurringType;
    private String sourceType;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public String getRecurringType() { return recurringType; }
    public void setRecurringType(String recurringType) { this.recurringType = recurringType; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
}
