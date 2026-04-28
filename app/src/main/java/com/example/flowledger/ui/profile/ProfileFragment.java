package com.example.flowledger.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.flowledger.R;
import com.example.flowledger.ui.largepurchases.LargePurchaseViewModel;
import com.example.flowledger.data.db.entity.LargePurchase;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;
    private LargePurchaseViewModel largePurchaseViewModel;
    private TextInputEditText etName, etDailyLimit, etWeeklyLimit, etMonthlyLimit;
    private TextView tvAvatarPlaceholder, tvTotalEmi, tvTotalLoan;
    private MaterialButtonToggleGroup toggleTheme;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        largePurchaseViewModel = new ViewModelProvider(this).get(LargePurchaseViewModel.class);

        etName = view.findViewById(R.id.etName);
        etDailyLimit = view.findViewById(R.id.etDailyLimit);
        etWeeklyLimit = view.findViewById(R.id.etWeeklyLimit);
        etMonthlyLimit = view.findViewById(R.id.etMonthlyLimit);
        tvAvatarPlaceholder = view.findViewById(R.id.tvAvatarPlaceholder);
        tvTotalEmi = view.findViewById(R.id.tvTotalEmi);
        tvTotalLoan = view.findViewById(R.id.tvTotalLoan);
        toggleTheme = view.findViewById(R.id.toggleTheme);
        View btnSaveProfile = view.findViewById(R.id.btnSaveProfile);

        loadData();

        btnSaveProfile.setOnClickListener(v -> {
            saveData();
        });

        largePurchaseViewModel.getAllLargePurchases().observe(getViewLifecycleOwner(), purchases -> {
            double totalEmi = 0;
            double totalLoan = 0;
            if (purchases != null) {
                for (LargePurchase p : purchases) {
                    if ("EMI".equals(p.getPurchaseType()) || "LOAN".equals(p.getPurchaseType())) {
                        totalEmi += p.getEmiAmount();
                        if ("LOAN".equals(p.getPurchaseType())) {
                            totalLoan += p.getLoanPrincipal();
                        } else {
                            totalLoan += p.getAmount(); // for normal EMI
                        }
                    }
                }
            }
            tvTotalEmi.setText(String.format(Locale.getDefault(), "₹%,.0f", totalEmi));
            tvTotalLoan.setText(String.format(Locale.getDefault(), "₹%,.0f", totalLoan));
        });
    }

    private void loadData() {
        String name = viewModel.getName();
        etName.setText(name);
        etDailyLimit.setText(viewModel.getDailyLimit());
        etWeeklyLimit.setText(viewModel.getWeeklyLimit());
        etMonthlyLimit.setText(viewModel.getMonthlyLimit());
        updateAvatar(name);

        String theme = viewModel.getTheme();
        if ("Emerald".equals(theme)) {
            toggleTheme.check(R.id.btnThemeEmerald);
        } else if ("Light".equals(theme)) {
            toggleTheme.check(R.id.btnThemeLight);
        } else {
            toggleTheme.check(R.id.btnThemeDark);
        }
    }

    private void saveData() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String daily = etDailyLimit.getText() != null ? etDailyLimit.getText().toString().trim() : "";
        String weekly = etWeeklyLimit.getText() != null ? etWeeklyLimit.getText().toString().trim() : "";
        String monthly = etMonthlyLimit.getText() != null ? etMonthlyLimit.getText().toString().trim() : "";
        
        String theme = "Dark";
        int checkedId = toggleTheme.getCheckedButtonId();
        if (checkedId == R.id.btnThemeEmerald) theme = "Emerald";
        else if (checkedId == R.id.btnThemeLight) theme = "Light";

        boolean themeChanged = !theme.equals(viewModel.getTheme());

        viewModel.saveProfile(name, daily, weekly, monthly, theme);
        updateAvatar(name);
        
        Toast.makeText(getContext(), "Profile Saved!", Toast.LENGTH_SHORT).show();

        if (themeChanged && getActivity() != null) {
            // Restart activity to apply new theme immediately
            getActivity().recreate();
        }
    }

    private void updateAvatar(String name) {
        if (!name.isEmpty()) {
            tvAvatarPlaceholder.setText(name.substring(0, 1).toUpperCase());
        } else {
            tvAvatarPlaceholder.setText("U");
        }
    }
}
