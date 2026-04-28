package com.example.flowledger.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.flowledger.data.db.entity.LargePurchase;

import java.util.List;

@Dao
public interface LargePurchaseDao {
    @Insert
    void insert(LargePurchase purchase);

    @Update
    void update(LargePurchase purchase);

    @Delete
    void delete(LargePurchase purchase);

    @Query("SELECT * FROM large_purchases ORDER BY purchaseDate DESC")
    LiveData<List<LargePurchase>> getAllLargePurchases();

    @Query("SELECT * FROM large_purchases WHERE id = :id LIMIT 1")
    LiveData<LargePurchase> getLargePurchaseById(int id);
    
    @Query("SELECT SUM(amount) FROM large_purchases")
    LiveData<Double> getTotalLargePurchasesAmount();
}
