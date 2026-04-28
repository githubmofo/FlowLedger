package com.example.flowledger.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.flowledger.data.db.entity.Rule;

import java.util.List;

@Dao
public interface RuleDao {
    @Insert
    void insertAll(List<Rule> rules);

    @Query("SELECT * FROM rules WHERE :note LIKE '%' || keyword || '%' LIMIT 1")
    Rule findMatchingRule(String note);
    
    @Query("SELECT COUNT(*) FROM rules")
    int getRuleCount();
}
