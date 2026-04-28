package com.example.flowledger.data.seed;

import android.content.Context;

import com.example.flowledger.data.db.AppDatabase;
import com.example.flowledger.data.db.entity.Category;
import com.example.flowledger.data.db.entity.Rule;

import java.util.ArrayList;
import java.util.List;

public class DatabaseSeeder {

    /**
     * Seeds the database with initial categories and rules.
     * This method is safe to call from any thread — it runs the actual
     * DB work on the shared database executor to prevent main thread blocking.
     */
    public static void seedDatabase(Context context) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(context);
            if (db.ruleDao().getRuleCount() == 0) {
                List<Category> categories = new ArrayList<>();
                categories.add(new Category("Food", "#FF5252", "🍔"));
                categories.add(new Category("Transport", "#448AFF", "🚗"));
                categories.add(new Category("Shopping", "#FF4081", "🛍️"));
                categories.add(new Category("Entertainment", "#7C4DFF", "🍿"));
                categories.add(new Category("Bills", "#FF5252", "📄"));
                categories.add(new Category("Health", "#69F0AE", "💊"));
                categories.add(new Category("Education", "#FFD740", "🎓"));
                categories.add(new Category("Groceries", "#B2FF59", "🛒"));
                categories.add(new Category("Subscriptions", "#7C4DFF", "💳"));
                categories.add(new Category("Travel", "#40C4FF", "✈️"));
                categories.add(new Category("Rent", "#A1887F", "🏠"));
                categories.add(new Category("Gifts", "#FFAB40", "🎁"));
                categories.add(new Category("Personal", "#E040FB", "✨"));
                categories.add(new Category("Savings", "#18FFFF", "🏦"));
                categories.add(new Category("Other", "#BDBDBD", "📦"));
                
                db.categoryDao().insertAll(categories);
                
                List<Rule> rules = new ArrayList<>();
                rules.add(new Rule("swiggy", 1)); // Food
                rules.add(new Rule("zomato", 1));
                rules.add(new Rule("uber", 2)); // Transport
                rules.add(new Rule("ola", 2));
                rules.add(new Rule("amazon", 3)); // Shopping
                rules.add(new Rule("netflix", 9)); // Subscriptions
                rules.add(new Rule("spotify", 9));
                rules.add(new Rule("jio", 5)); // Bills
                rules.add(new Rule("airtel", 5));
                rules.add(new Rule("blinkit", 8)); // Groceries
                rules.add(new Rule("zepto", 8));
                rules.add(new Rule("apollo", 6)); // Health
                rules.add(new Rule("makemytrip", 10)); // Travel
                
                db.ruleDao().insertAll(rules);
            }
        });
    }
}
