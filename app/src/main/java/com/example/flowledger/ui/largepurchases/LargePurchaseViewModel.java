package com.example.flowledger.ui.largepurchases;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.flowledger.data.db.entity.LargePurchase;
import com.example.flowledger.data.repository.LargePurchaseRepository;

import java.util.List;

public class LargePurchaseViewModel extends AndroidViewModel {
    private LargePurchaseRepository repository;
    private LiveData<List<LargePurchase>> allLargePurchases;

    public LargePurchaseViewModel(@NonNull Application application) {
        super(application);
        repository = new LargePurchaseRepository(application);
        allLargePurchases = repository.getAllLargePurchases();
    }

    public LiveData<List<LargePurchase>> getAllLargePurchases() {
        return allLargePurchases;
    }

    public void saveLargePurchase(String title, double amount, int categoryId, String paymentMethod, String purchaseType, long purchaseDate, String note, double emiAmount, int emiMonths, double loanPrincipal) {
        LargePurchase purchase = new LargePurchase(title, amount, categoryId, paymentMethod, purchaseType, purchaseDate, note, emiAmount, emiMonths, loanPrincipal);
        repository.insert(purchase);
    }

    public void updateLargePurchase(int id, String title, double amount, int categoryId, String paymentMethod, String purchaseType, long purchaseDate, String note, double emiAmount, int emiMonths, double loanPrincipal) {
        LargePurchase purchase = new LargePurchase(title, amount, categoryId, paymentMethod, purchaseType, purchaseDate, note, emiAmount, emiMonths, loanPrincipal);
        purchase.setId(id);
        repository.update(purchase);
    }

    public void deleteLargePurchase(LargePurchase purchase) {
        repository.delete(purchase);
    }

    public LiveData<LargePurchase> getLargePurchaseById(int id) {
        return repository.getLargePurchaseById(id);
    }
}
