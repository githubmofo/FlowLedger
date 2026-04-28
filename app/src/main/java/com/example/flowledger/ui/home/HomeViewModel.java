package com.example.flowledger.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.flowledger.data.db.entity.Transaction;
import com.example.flowledger.data.db.entity.TransactionWithCategory;
import com.example.flowledger.data.repository.TransactionRepository;

import java.util.Calendar;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private final TransactionRepository repository;
    private final LiveData<List<TransactionWithCategory>> recentTransactions;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = new TransactionRepository(application);
        recentTransactions = repository.getRecentTransactions();
    }

    public LiveData<List<TransactionWithCategory>> getRecentTransactions() {
        return recentTransactions;
    }

    public LiveData<Double> getDailyTotal() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return repository.getDailyTotal(cal.getTimeInMillis());
    }

    public LiveData<Double> getWeeklyTotal() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return repository.getWeeklyTotal(cal.getTimeInMillis());
    }

    public LiveData<Double> getMonthlyTotal() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return repository.getMonthlyTotal(cal.getTimeInMillis());
    }
}
