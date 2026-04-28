package com.example.flowledger.data.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "rules")
public class Rule {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String keyword;
    public int categoryId;
    
    public Rule(String keyword, int categoryId) {
        this.keyword = keyword;
        this.categoryId = categoryId;
    }
}
