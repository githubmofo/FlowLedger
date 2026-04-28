package com.example.flowledger.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.flowledger.data.db.AppDatabase;
import com.example.flowledger.data.db.dao.TransactionDao;
import com.example.flowledger.data.db.dao.QuickPatternDao;
import com.example.flowledger.data.db.entity.QuickPattern;
import com.example.flowledger.data.db.entity.Transaction;
import com.example.flowledger.data.db.entity.TransactionWithCategory;

import java.util.List;

public class TransactionRepository {
    private final TransactionDao transactionDao;
    private final QuickPatternDao quickPatternDao;
    private final LiveData<List<TransactionWithCategory>> allTransactions;
    private final LiveData<List<TransactionWithCategory>> recentTransactions;
    private final LiveData<List<QuickPattern>> topPatterns;

    public TransactionRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        transactionDao = db.transactionDao();
        quickPatternDao = db.quickPatternDao();
        allTransactions = transactionDao.getAllTransactionsWithCategory();
        recentTransactions = transactionDao.getRecentTransactionsWithCategory();
        topPatterns = quickPatternDao.getTopPatterns();
    }

    public LiveData<List<TransactionWithCategory>> getAllTransactions() {
        return allTransactions;
    }

    public LiveData<List<TransactionWithCategory>> getRecentTransactions() {
        return recentTransactions;
    }

    public LiveData<Double> getDailyTotal(long startOfDay) {
        return transactionDao.getDailyTotal(startOfDay);
    }

    public LiveData<Double> getWeeklyTotal(long startOfWeek) {
        return transactionDao.getWeeklyTotal(startOfWeek);
    }

    public LiveData<Double> getMonthlyTotal(long startOfMonth) {
        return transactionDao.getMonthlyTotal(startOfMonth);
    }

    public LiveData<List<TransactionWithCategory>> searchTransactions(String query) {
        return transactionDao.searchTransactions(query);
    }

    public LiveData<List<QuickPattern>> getTopPatterns() {
        return topPatterns;
    }

    public void updatePattern(double amount, int categoryId, String paymentMode, String note) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            QuickPattern existing = quickPatternDao.findExactMatch(amount, categoryId, paymentMode);
            if (existing != null) {
                existing.setUsageCount(existing.getUsageCount() + 1);
                existing.setLastUsedAt(System.currentTimeMillis());
                quickPatternDao.update(existing);
            } else {
                QuickPattern newPattern = new QuickPattern();
                newPattern.setAmount(amount);
                newPattern.setCategoryId(categoryId);
                newPattern.setPaymentMode(paymentMode);
                newPattern.setNote(note);
                newPattern.setUsageCount(1);
                newPattern.setLastUsedAt(System.currentTimeMillis());
                quickPatternDao.insert(newPattern);
            }
        });
    }

    public void insert(Transaction transaction) {
        AppDatabase.databaseWriteExecutor.execute(() -> transactionDao.insert(transaction));
    }

    public void update(Transaction transaction) {
        AppDatabase.databaseWriteExecutor.execute(() -> transactionDao.update(transaction));
    }

    public void delete(Transaction transaction) {
        AppDatabase.databaseWriteExecutor.execute(() -> transactionDao.delete(transaction));
    }

    public LiveData<Transaction> getTransactionById(int id) {
        return transactionDao.getTransactionById(id);
    }

    public void insertAll(List<Transaction> transactions) {
        AppDatabase.databaseWriteExecutor.execute(() -> transactionDao.insertAll(transactions));
    }

    public void deleteAll() {
        AppDatabase.databaseWriteExecutor.execute(() -> transactionDao.deleteAll());
    }
}
