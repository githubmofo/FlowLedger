package com.example.flowledger.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.flowledger.data.db.entity.Transaction;
import com.example.flowledger.data.db.entity.TransactionWithCategory;
import com.example.flowledger.data.db.entity.CategorySpending;

import java.util.List;

@Dao
public interface TransactionDao {
    @Insert
    void insert(Transaction transaction);

    @Insert
    void insertAll(List<Transaction> transactions);

    @androidx.room.Update
    void update(Transaction transaction);

    @androidx.room.Delete
    void delete(Transaction transaction);

    @Query("SELECT * FROM transactions WHERE id = :id")
    LiveData<Transaction> getTransactionById(int id);

    @Query("DELETE FROM transactions")
    void deleteAll();

    @androidx.room.Transaction
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    LiveData<List<TransactionWithCategory>> getAllTransactionsWithCategory();
    
    @androidx.room.Transaction
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC LIMIT 10")
    LiveData<List<TransactionWithCategory>> getRecentTransactionsWithCategory();
    
    @Query("SELECT SUM(amount) FROM transactions WHERE timestamp >= :startOfDay")
    LiveData<Double> getDailyTotal(long startOfDay);
    
    @Query("SELECT SUM(amount) FROM transactions WHERE timestamp >= :startOfWeek")
    LiveData<Double> getWeeklyTotal(long startOfWeek);

    @Query("SELECT SUM(amount) FROM transactions WHERE timestamp >= :startOfMonth")
    LiveData<Double> getMonthlyTotal(long startOfMonth);

    @androidx.room.Transaction
    @Query("SELECT * FROM transactions WHERE note LIKE '%' || :query || '%' OR paymentMode LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    LiveData<List<TransactionWithCategory>> searchTransactions(String query);

    @androidx.room.Transaction
    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId ORDER BY timestamp DESC")
    LiveData<List<TransactionWithCategory>> getTransactionsByCategory(int categoryId);

    @Query("SELECT c.name as categoryName, c.color as categoryColor, SUM(t.amount) as totalAmount " +
           "FROM transactions t INNER JOIN categories c ON t.categoryId = c.id " +
           "WHERE t.type = 'EXPENSE' GROUP BY c.name")
    LiveData<List<CategorySpending>> getCategorySpending();
}
