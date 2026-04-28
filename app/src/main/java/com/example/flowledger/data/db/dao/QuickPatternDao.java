package com.example.flowledger.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.flowledger.data.db.entity.QuickPattern;

import java.util.List;

@Dao
public interface QuickPatternDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(QuickPattern pattern);

    @Update
    void update(QuickPattern pattern);

    @Query("SELECT * FROM quick_patterns ORDER BY usageCount DESC, lastUsedAt DESC LIMIT 5")
    LiveData<List<QuickPattern>> getTopPatterns();
    
    @Query("SELECT * FROM quick_patterns WHERE amount = :amount AND categoryId = :categoryId AND paymentMode = :paymentMode LIMIT 1")
    QuickPattern findExactMatch(double amount, int categoryId, String paymentMode);
}
