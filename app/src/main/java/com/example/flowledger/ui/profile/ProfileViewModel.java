package com.example.flowledger.ui.profile;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class ProfileViewModel extends AndroidViewModel {

    private final SharedPreferences prefs;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        prefs = application.getSharedPreferences("FlowLedgerProfile", Context.MODE_PRIVATE);
    }

    public String getName() { return prefs.getString("name", ""); }
    public String getDailyLimit() { return prefs.getString("daily_limit", ""); }
    public String getWeeklyLimit() { return prefs.getString("weekly_limit", ""); }
    public String getMonthlyLimit() { return prefs.getString("monthly_limit", ""); }
    public String getTheme() { return prefs.getString("theme", "Dark"); }

    public void saveProfile(String name, String daily, String weekly, String monthly, String theme) {
        prefs.edit()
            .putString("name", name)
            .putString("daily_limit", daily)
            .putString("weekly_limit", weekly)
            .putString("monthly_limit", monthly)
            .putString("theme", theme)
            .apply();
    }
}
