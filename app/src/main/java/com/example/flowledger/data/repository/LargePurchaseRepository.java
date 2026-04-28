package com.example.flowledger.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.flowledger.data.db.AppDatabase;
import com.example.flowledger.data.db.dao.LargePurchaseDao;
import com.example.flowledger.data.db.entity.LargePurchase;

import java.util.List;

public class LargePurchaseRepository {
    private LargePurchaseDao largePurchaseDao;
    private LiveData<List<LargePurchase>> allLargePurchases;

    public LargePurchaseRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        largePurchaseDao = db.largePurchaseDao();
        allLargePurchases = largePurchaseDao.getAllLargePurchases();
    }

    public LiveData<List<LargePurchase>> getAllLargePurchases() {
        return allLargePurchases;
    }

    public LiveData<LargePurchase> getLargePurchaseById(int id) {
        return largePurchaseDao.getLargePurchaseById(id);
    }
    
    public LiveData<Double> getTotalLargePurchasesAmount() {
        return largePurchaseDao.getTotalLargePurchasesAmount();
    }

    public void insert(LargePurchase purchase) {
        AppDatabase.databaseWriteExecutor.execute(() -> largePurchaseDao.insert(purchase));
    }

    public void update(LargePurchase purchase) {
        AppDatabase.databaseWriteExecutor.execute(() -> largePurchaseDao.update(purchase));
    }

    public void delete(LargePurchase purchase) {
        AppDatabase.databaseWriteExecutor.execute(() -> largePurchaseDao.delete(purchase));
    }
}
