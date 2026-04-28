package com.example.flowledger.ui.addexpense;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.flowledger.data.db.entity.Category;
import com.example.flowledger.data.db.entity.QuickPattern;
import com.example.flowledger.data.db.entity.Transaction;
import com.example.flowledger.data.repository.CategoryRepository;
import com.example.flowledger.data.repository.TransactionRepository;

import java.util.List;

public class AddExpenseViewModel extends AndroidViewModel {
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final LiveData<List<Category>> allCategories;
    private final LiveData<List<QuickPattern>> topPatterns;

    public AddExpenseViewModel(@NonNull Application application) {
        super(application);
        transactionRepository = new TransactionRepository(application);
        categoryRepository = new CategoryRepository(application);
        allCategories = categoryRepository.getAllCategories();
        topPatterns = transactionRepository.getTopPatterns();
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public LiveData<List<QuickPattern>> getTopPatterns() {
        return topPatterns;
    }

    public LiveData<Transaction> getTransaction(int id) {
        return transactionRepository.getTransactionById(id);
    }

    public void saveTransaction(double amount, String note, int categoryId, String type, String paymentMode) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setNote(note);
        transaction.setCategoryId(categoryId);
        transaction.setType(type);
        transaction.setPaymentMode(paymentMode);
        transaction.setSourceType("Manual");
        transaction.setTimestamp(System.currentTimeMillis());
        
        transactionRepository.insert(transaction);
        transactionRepository.updatePattern(amount, categoryId, paymentMode, note);
    }

    public void updateTransaction(int id, double amount, String note, int categoryId, String type, String paymentMode, long timestamp) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setAmount(amount);
        transaction.setNote(note);
        transaction.setCategoryId(categoryId);
        transaction.setType(type);
        transaction.setPaymentMode(paymentMode);
        transaction.setSourceType("Manual");
        transaction.setTimestamp(timestamp);
        
        transactionRepository.update(transaction);
    }

    public void deleteTransaction(Transaction transaction) {
        transactionRepository.delete(transaction);
    }
}
