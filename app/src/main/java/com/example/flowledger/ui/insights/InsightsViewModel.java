package com.example.flowledger.ui.insights;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.flowledger.data.db.entity.CategorySpending;
import com.example.flowledger.data.repository.InsightRepository;

import java.util.List;

public class InsightsViewModel extends AndroidViewModel {
    private final InsightRepository repository;
    private final LiveData<List<CategorySpending>> categorySpending;

    public InsightsViewModel(@NonNull Application application) {
        super(application);
        repository = new InsightRepository(application);
        categorySpending = repository.getCategorySpending();
    }

    public LiveData<List<CategorySpending>> getCategorySpending() {
        return categorySpending;
    }
}
