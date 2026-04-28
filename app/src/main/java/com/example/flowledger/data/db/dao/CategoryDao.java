package com.example.flowledger.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.flowledger.data.db.entity.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    void insertAll(List<Category> categories);

    @Query("SELECT * FROM categories")
    LiveData<List<Category>> getAllCategories();
}
