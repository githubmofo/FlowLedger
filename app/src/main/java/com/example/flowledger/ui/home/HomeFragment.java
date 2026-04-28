package com.example.flowledger.ui.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowledger.R;
import com.example.flowledger.ui.home.TransactionAdapter;
import com.example.flowledger.ui.addexpense.AddExpenseBottomSheetFragment;
import com.example.flowledger.ui.largepurchases.AddLargePurchaseBottomSheetFragment;
import com.example.flowledger.ui.largepurchases.LargePurchaseAdapter;
import com.example.flowledger.ui.largepurchases.LargePurchaseViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.Locale;

public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;
    private LargePurchaseViewModel largePurchaseViewModel;
    private TransactionAdapter adapter;
    private LargePurchaseAdapter largePurchaseAdapter;
    private TextView tvMainTotal, tvBudgetInfo, tvMicroInsight;
    private LinearProgressIndicator progressBudget;
    private ChipGroup cgPeriod;
    private View viewStatusStrip;

    private double currentDailyTotal = 0.0;
    private double currentWeeklyTotal = 0.0;
    private double currentMonthlyTotal = 0.0;
    private double dailyLimit = 0.0;
    private double weeklyLimit = 0.0;
    private double monthlyLimit = 0.0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        largePurchaseViewModel = new ViewModelProvider(this).get(LargePurchaseViewModel.class);

        tvMainTotal = view.findViewById(R.id.tvMainTotal);
        tvBudgetInfo = view.findViewById(R.id.tvBudgetInfo);
        tvMicroInsight = view.findViewById(R.id.tvMicroInsight);
        viewStatusStrip = view.findViewById(R.id.viewStatusStrip);
        progressBudget = view.findViewById(R.id.progressBudget);
        cgPeriod = view.findViewById(R.id.cgPeriod);

        RecyclerView rvTransactions = view.findViewById(R.id.rvTransactions);
        adapter = new TransactionAdapter();
        rvTransactions.setAdapter(adapter);

        RecyclerView rvLargePurchases = view.findViewById(R.id.rvLargePurchases);
        largePurchaseAdapter = new LargePurchaseAdapter();
        rvLargePurchases.setAdapter(largePurchaseAdapter);
        
        largePurchaseAdapter.setOnLargePurchaseClickListener(purchase -> {
            AddLargePurchaseBottomSheetFragment editSheet = AddLargePurchaseBottomSheetFragment.newInstance(purchase.getId());
            editSheet.show(getParentFragmentManager(), "edit_large_purchase");
        });
        
        MaterialButton btnAddLargePurchase = view.findViewById(R.id.btnAddLargePurchase);
        btnAddLargePurchase.setOnClickListener(v -> {
            AddLargePurchaseBottomSheetFragment sheet = new AddLargePurchaseBottomSheetFragment();
            sheet.show(getParentFragmentManager(), "add_large_purchase");
        });

        // Defer heavy list observation to allow the layout to settle first
        view.post(() -> {
            if (!isAdded()) return;

            adapter.setOnTransactionClickListener(transaction -> {
                AddExpenseBottomSheetFragment editSheet = AddExpenseBottomSheetFragment.newInstance(transaction.transaction.getId());
                editSheet.show(getParentFragmentManager(), "edit_expense");
            });

            cgPeriod.check(R.id.chipToday);

            viewModel.getRecentTransactions().observe(getViewLifecycleOwner(), transactions -> {
                adapter.setTransactions(transactions);
                updateInsight(transactions.size());
            });

            largePurchaseViewModel.getAllLargePurchases().observe(getViewLifecycleOwner(), largePurchases -> {
                largePurchaseAdapter.setPurchases(largePurchases);
            });

            viewModel.getDailyTotal().observe(getViewLifecycleOwner(), total -> {
                currentDailyTotal = total != null ? total : 0.0;
                updateDashboard();
            });

            viewModel.getWeeklyTotal().observe(getViewLifecycleOwner(), total -> {
                currentWeeklyTotal = total != null ? total : 0.0;
                updateDashboard();
            });

            viewModel.getMonthlyTotal().observe(getViewLifecycleOwner(), total -> {
                currentMonthlyTotal = total != null ? total : 0.0;
                updateDashboard();
            });

            cgPeriod.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (!checkedIds.isEmpty()) {
                    updateDashboard();
                }
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getContext() != null && getView() != null) {
            SharedPreferences prefs = getContext().getSharedPreferences("FlowLedgerProfile", android.content.Context.MODE_PRIVATE);
            String name = prefs.getString("name", "User");
            if (name == null || name.trim().isEmpty()) {
                name = "User";
            }
            TextView tvUserName = getView().findViewById(R.id.tvUserName);
            if (tvUserName != null) {
                tvUserName.setText(name);
            }

            try {
                String dLimitStr = prefs.getString("daily_limit", "0");
                dailyLimit = dLimitStr.isEmpty() ? 0 : Double.parseDouble(dLimitStr);
                String wLimitStr = prefs.getString("weekly_limit", "0");
                weeklyLimit = wLimitStr.isEmpty() ? 0 : Double.parseDouble(wLimitStr);
                String mLimitStr = prefs.getString("monthly_limit", "0");
                monthlyLimit = mLimitStr.isEmpty() ? 0 : Double.parseDouble(mLimitStr);
            } catch (Exception e) {
                dailyLimit = 0;
                weeklyLimit = 0;
                monthlyLimit = 0;
            }
            updateDashboard();
        }
    }

    private void updateDashboard() {
        if (cgPeriod == null) return;
        
        int checkedId = cgPeriod.getCheckedChipId();
        double currentTotal;
        double limit;
        String periodName;

        if (checkedId == R.id.chipToday) {
            currentTotal = currentDailyTotal;
            limit = dailyLimit;
            periodName = "daily";
        } else if (checkedId == R.id.chipWeek) {
            currentTotal = currentWeeklyTotal;
            limit = weeklyLimit;
            periodName = "weekly";
        } else {
            currentTotal = currentMonthlyTotal;
            limit = monthlyLimit;
            periodName = "monthly";
        }

        tvMainTotal.setText(String.format(Locale.getDefault(), "₹%.2f", currentTotal));
        
        if (limit > 0) {
            int progress = (int) ((currentTotal / limit) * 100);
            progressBudget.setProgress(Math.min(progress, 100));
            tvBudgetInfo.setText(String.format(Locale.getDefault(), "₹%.0f of ₹%.0f %s limit", currentTotal, limit, periodName));
            
            // Warning logic
            if (currentTotal >= limit) {
                int red = getResources().getColor(R.color.expense_red, null);
                progressBudget.setIndicatorColor(red);
                viewStatusStrip.setBackgroundColor(red);
                tvBudgetInfo.setTextColor(red);
            } else if (currentTotal >= limit * 0.8) {
                int amber = getResources().getColor(R.color.warning_amber, null);
                progressBudget.setIndicatorColor(amber);
                viewStatusStrip.setBackgroundColor(amber);
                tvBudgetInfo.setTextColor(amber);
            } else {
                int primary = getThemeColor(com.google.android.material.R.attr.colorPrimary);
                progressBudget.setIndicatorColor(primary);
                viewStatusStrip.setBackgroundColor(primary);
                tvBudgetInfo.setTextColor(getThemeColor(com.google.android.material.R.attr.colorOnSurfaceVariant));
            }
        } else {
            progressBudget.setProgress(0);
            tvBudgetInfo.setText(String.format("No %s limit set", periodName));
            int primary = getThemeColor(com.google.android.material.R.attr.colorPrimary);
            viewStatusStrip.setBackgroundColor(primary);
        }
    }

    private int getThemeColor(int attr) {
        android.util.TypedValue typedValue = new android.util.TypedValue();
        if (getContext() != null && getContext().getTheme().resolveAttribute(attr, typedValue, true)) {
            return typedValue.data;
        }
        return 0;
    }

    private void updateInsight(int count) {
        if (count > 0) {
            tvMicroInsight.setText(String.format(Locale.getDefault(), "You have logged %d recent transactions.", count));
        } else {
            tvMicroInsight.setText("Start tracking to see insights.");
        }
    }
}
