package com.example.flowledger.data.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.flowledger.data.db.dao.CategoryDao;
import com.example.flowledger.data.db.dao.LargePurchaseDao;
import com.example.flowledger.data.db.dao.RuleDao;
import com.example.flowledger.data.db.dao.TransactionDao;
import com.example.flowledger.data.db.dao.QuickPatternDao;
import com.example.flowledger.data.db.entity.Category;
import com.example.flowledger.data.db.entity.LargePurchase;
import com.example.flowledger.data.db.entity.QuickPattern;
import com.example.flowledger.data.db.entity.Rule;
import com.example.flowledger.data.db.entity.Transaction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Transaction.class, Category.class, Rule.class, QuickPattern.class, LargePurchase.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    // Shared executor for all database background work
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract TransactionDao transactionDao();
    public abstract CategoryDao categoryDao();
    public abstract RuleDao ruleDao();
    public abstract QuickPatternDao quickPatternDao();
    public abstract LargePurchaseDao largePurchaseDao();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE transactions ADD COLUMN paymentMode TEXT");
            database.execSQL("ALTER TABLE transactions ADD COLUMN recurringType TEXT");
            database.execSQL("ALTER TABLE transactions ADD COLUMN sourceType TEXT");
            database.execSQL("CREATE TABLE IF NOT EXISTS `quick_patterns` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `amount` REAL NOT NULL, `categoryId` INTEGER NOT NULL, `paymentMode` TEXT, `note` TEXT, `usageCount` INTEGER NOT NULL, `lastUsedAt` INTEGER NOT NULL)");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `large_purchases` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT, `amount` REAL NOT NULL, `categoryId` INTEGER NOT NULL, `paymentMethod` TEXT, `purchaseType` TEXT, `purchaseDate` INTEGER NOT NULL, `note` TEXT, `emiAmount` REAL NOT NULL, `emiMonths` INTEGER NOT NULL, `loanPrincipal` REAL NOT NULL)");
        }
    };

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "flowledger_database")
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
