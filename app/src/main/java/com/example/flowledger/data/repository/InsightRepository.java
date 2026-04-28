package com.example.flowledger.data.repository;

import android.app.Application;

import com.example.flowledger.data.db.AppDatabase;
import com.example.flowledger.data.db.entity.CategorySpending;

import androidx.lifecycle.LiveData;
import java.util.List;

public class InsightRepository {
    private AppDatabase db;

    public InsightRepository(Application application) {
        db = AppDatabase.getDatabase(application);
    }
    
    public LiveData<List<CategorySpending>> getCategorySpending() {
        return db.transactionDao().getCategorySpending();
    }
}
