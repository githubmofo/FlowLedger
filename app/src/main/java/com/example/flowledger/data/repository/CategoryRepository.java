package com.example.flowledger.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.flowledger.data.db.AppDatabase;
import com.example.flowledger.data.db.dao.CategoryDao;
import com.example.flowledger.data.db.entity.Category;

import java.util.List;

public class CategoryRepository {
    private CategoryDao categoryDao;
    private LiveData<List<Category>> allCategories;

    public CategoryRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        categoryDao = db.categoryDao();
        allCategories = categoryDao.getAllCategories();
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }
}
