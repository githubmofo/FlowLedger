package com.example.flowledger.data.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "quick_patterns")
public class QuickPattern {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private double amount;
    private int categoryId;
    private String paymentMode;
    private String note;
    private int usageCount;
    private long lastUsedAt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public int getUsageCount() { return usageCount; }
    public void setUsageCount(int usageCount) { this.usageCount = usageCount; }

    public long getLastUsedAt() { return lastUsedAt; }
    public void setLastUsedAt(long lastUsedAt) { this.lastUsedAt = lastUsedAt; }
}
