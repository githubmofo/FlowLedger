package com.example.flowledger.data.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "large_purchases")
public class LargePurchase {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private double amount;
    private int categoryId;
    private String paymentMethod; // UPI, Cash, Card, Transfer
    private String purchaseType; // ONE_TIME, EMI, LOAN
    private long purchaseDate;
    private String note;
    
    // EMI & Loan specific fields
    private double emiAmount;
    private int emiMonths;
    private double loanPrincipal;

    public LargePurchase(String title, double amount, int categoryId, String paymentMethod, String purchaseType, long purchaseDate, String note, double emiAmount, int emiMonths, double loanPrincipal) {
        this.title = title;
        this.amount = amount;
        this.categoryId = categoryId;
        this.paymentMethod = paymentMethod;
        this.purchaseType = purchaseType;
        this.purchaseDate = purchaseDate;
        this.note = note;
        this.emiAmount = emiAmount;
        this.emiMonths = emiMonths;
        this.loanPrincipal = loanPrincipal;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getPurchaseType() { return purchaseType; }
    public void setPurchaseType(String purchaseType) { this.purchaseType = purchaseType; }
    
    public long getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(long purchaseDate) { this.purchaseDate = purchaseDate; }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    
    public double getEmiAmount() { return emiAmount; }
    public void setEmiAmount(double emiAmount) { this.emiAmount = emiAmount; }
    
    public int getEmiMonths() { return emiMonths; }
    public void setEmiMonths(int emiMonths) { this.emiMonths = emiMonths; }
    
    public double getLoanPrincipal() { return loanPrincipal; }
    public void setLoanPrincipal(double loanPrincipal) { this.loanPrincipal = loanPrincipal; }

    public int getRemainingEmiMonths() {
        if (emiMonths <= 0) return 0;
        long diff = System.currentTimeMillis() - purchaseDate;
        long monthMillis = 30L * 24L * 60L * 60L * 1000L;
        int monthsPassed = (int) (diff / monthMillis);
        int remaining = emiMonths - monthsPassed;
        return Math.max(0, remaining);
    }

    public double getRemainingLoanPrincipal() {
        if (emiAmount <= 0) return loanPrincipal > 0 ? loanPrincipal : amount;
        double basePrincipal = loanPrincipal > 0 ? loanPrincipal : amount;
        long diff = System.currentTimeMillis() - purchaseDate;
        long monthMillis = 30L * 24L * 60L * 60L * 1000L;
        int monthsPassed = (int) (diff / monthMillis);
        double deducted = Math.min(monthsPassed, emiMonths) * emiAmount;
        double remaining = basePrincipal - deducted;
        return Math.max(0, remaining);
    }
}
