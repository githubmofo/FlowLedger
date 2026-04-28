package com.example.flowledger;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.flowledger.data.db.AppDatabase;
import com.example.flowledger.data.seed.DatabaseSeeder;
import com.example.flowledger.ui.navigation.LiquidNavBar;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        android.content.SharedPreferences prefs = getSharedPreferences("FlowLedgerProfile", android.content.Context.MODE_PRIVATE);
        String theme = prefs.getString("theme", "Dark");
        if ("Emerald".equals(theme)) {
            setTheme(R.style.Theme_FlowLedger_Emerald);
        } else if ("Light".equals(theme)) {
            setTheme(R.style.Theme_FlowLedger_Light);
        } else {
            setTheme(R.style.Theme_FlowLedger);
        }

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Apply theme-aware gradient to the bottom fade mask
        View bottomFade = findViewById(R.id.bottomFadeMask);
        if (bottomFade != null) {
            android.util.TypedValue tv = new android.util.TypedValue();
            int bgColor = android.graphics.Color.parseColor("#070A0F"); // dark fallback
            if (getTheme().resolveAttribute(android.R.attr.colorBackground, tv, true)) {
                bgColor = tv.data;
            }
            int transparent = android.graphics.Color.argb(0,
                    android.graphics.Color.red(bgColor),
                    android.graphics.Color.green(bgColor),
                    android.graphics.Color.blue(bgColor));
            android.graphics.drawable.GradientDrawable gradient =
                    new android.graphics.drawable.GradientDrawable(
                            android.graphics.drawable.GradientDrawable.Orientation.BOTTOM_TOP,
                            new int[]{bgColor, bgColor, transparent});
            gradient.setGradientCenter(0.5f, 0.3f);
            bottomFade.setBackground(gradient);
        }

        // Pre-initialize Room database on a background thread to prevent UI freezing.
        // This ensures the DB schema validation, migration, and first query happen off the main thread.
        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppDatabase.getDatabase(getApplicationContext());
            // Once DB is ready, seed it on the same background thread
            DatabaseSeeder.seedDatabase(getApplicationContext());
        });

        // Setup Navigation
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }
        
        LiquidNavBar liquidNavBar = findViewById(R.id.liquid_nav_bar);
        
        if (navController != null) {
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int id = destination.getId();
                if (id == R.id.homeFragment) liquidNavBar.animateToTab(0, 5);
                else if (id == R.id.searchFragment) liquidNavBar.animateToTab(1, 5);
                else if (id == R.id.addExpenseFragment) liquidNavBar.animateToTab(2, 5);
                else if (id == R.id.insightsFragment) liquidNavBar.animateToTab(3, 5);
                else if (id == R.id.profileFragment) liquidNavBar.animateToTab(4, 5);
            });
            
            liquidNavBar.setOnTabSelectedListener(index -> {
                int targetId;
                switch (index) {
                    case 0: targetId = R.id.homeFragment; break;
                    case 1: targetId = R.id.searchFragment; break;
                    case 2: targetId = R.id.addExpenseFragment; break;
                    case 3: targetId = R.id.insightsFragment; break;
                    case 4: targetId = R.id.profileFragment; break;
                    default: targetId = R.id.homeFragment;
                }
                
                if (navController.getCurrentDestination() != null &&
                    navController.getCurrentDestination().getId() != targetId) {
                    navController.navigate(targetId);
                }
            });
        }
    }
}