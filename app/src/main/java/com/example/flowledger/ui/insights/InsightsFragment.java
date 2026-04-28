package com.example.flowledger.ui.insights;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.graphics.Color;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowledger.R;
import com.example.flowledger.data.db.entity.CategorySpending;
import com.example.flowledger.data.db.entity.LargePurchase;
import com.example.flowledger.ui.largepurchases.LargePurchaseViewModel;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InsightsFragment extends Fragment {

    private InsightsViewModel viewModel;
    private LargePurchaseViewModel largePurchaseViewModel;
    private PieChart pieChart, lpPieChart;
    private View layoutLpChart;
    private TextView tvTopCategory, tvDailyAvg, tvEmptyAllocation;
    private RecyclerView rvCategoryBreakdown;
    private CategoryAllocationAdapter allocationAdapter;
    private final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_insights, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(InsightsViewModel.class);
        largePurchaseViewModel = new ViewModelProvider(this).get(LargePurchaseViewModel.class);
        pieChart = view.findViewById(R.id.pieChart);
        lpPieChart = view.findViewById(R.id.lpPieChart);
        layoutLpChart = view.findViewById(R.id.layoutLpChart);
        tvTopCategory = view.findViewById(R.id.tvTopCategory);
        tvDailyAvg = view.findViewById(R.id.tvDailyAvg);
        tvEmptyAllocation = view.findViewById(R.id.tvEmptyAllocation);
        rvCategoryBreakdown = view.findViewById(R.id.rvCategoryBreakdown);

        allocationAdapter = new CategoryAllocationAdapter();
        rvCategoryBreakdown.setAdapter(allocationAdapter);

        setupPieChart(pieChart, "Spending");
        setupPieChart(lpPieChart, "Large Purchases");

        viewModel.getCategorySpending().observe(getViewLifecycleOwner(), spendingList -> {
            if (spendingList != null && !spendingList.isEmpty()) {
                tvEmptyAllocation.setVisibility(View.GONE);
                rvCategoryBreakdown.setVisibility(View.VISIBLE);
                
                backgroundExecutor.execute(() -> {
                    final ArrayList<PieEntry> entries = new ArrayList<>();
                    final ArrayList<Integer> colors = new ArrayList<>();
                    double maxAmount = 0;
                    CategorySpending topCategory = spendingList.get(0);
                    double totalAmount = 0;

                    for (CategorySpending spending : spendingList) {
                        entries.add(new PieEntry((float) spending.totalAmount, spending.categoryName));
                        try {
                            colors.add(Color.parseColor(spending.categoryColor));
                        } catch (Exception e) {
                            colors.add(Color.GRAY);
                        }
                        totalAmount += spending.totalAmount;
                        if (spending.totalAmount > maxAmount) {
                            maxAmount = spending.totalAmount;
                            topCategory = spending;
                        }
                    }

                    final String topName = topCategory.categoryName;
                    final double dailyAvg = totalAmount / 30.0;
                    final double totalAmountFinal = totalAmount;

                    mainHandler.post(() -> {
                        if (!isAdded()) return;

                        tvTopCategory.setText(topName);
                        tvDailyAvg.setText(String.format("₹%.0f", dailyAvg));
                        updateChartData(pieChart, entries, colors);
                        
                        allocationAdapter.setData(spendingList, totalAmountFinal);
                    });
                });
            } else {
                tvEmptyAllocation.setVisibility(View.VISIBLE);
                rvCategoryBreakdown.setVisibility(View.GONE);
                tvTopCategory.setText("N/A");
                tvDailyAvg.setText("₹0");
                allocationAdapter.setData(new ArrayList<>(), 0);
                pieChart.clear();
            }
        });

        largePurchaseViewModel.getAllLargePurchases().observe(getViewLifecycleOwner(), purchases -> {
            if (purchases != null && !purchases.isEmpty()) {
                layoutLpChart.setVisibility(View.VISIBLE);
                
                backgroundExecutor.execute(() -> {
                    double emiTotal = 0, loanTotal = 0, oneTimeTotal = 0;
                    for (LargePurchase lp : purchases) {
                        if ("EMI".equals(lp.getPurchaseType())) emiTotal += lp.getAmount();
                        else if ("LOAN".equals(lp.getPurchaseType())) loanTotal += lp.getAmount();
                        else oneTimeTotal += lp.getAmount();
                    }
                    
                    final ArrayList<PieEntry> entries = new ArrayList<>();
                    final ArrayList<Integer> colors = new ArrayList<>();
                    
                    if (emiTotal > 0) {
                        entries.add(new PieEntry((float) emiTotal, "EMI"));
                        colors.add(Color.parseColor("#4CAF50")); // Green
                    }
                    if (loanTotal > 0) {
                        entries.add(new PieEntry((float) loanTotal, "Loans"));
                        colors.add(Color.parseColor("#F44336")); // Red
                    }
                    if (oneTimeTotal > 0) {
                        entries.add(new PieEntry((float) oneTimeTotal, "One-Time"));
                        colors.add(Color.parseColor("#2196F3")); // Blue
                    }

                    mainHandler.post(() -> {
                        if (!isAdded()) return;
                        updateChartData(lpPieChart, entries, colors);
                    });
                });
            } else {
                layoutLpChart.setVisibility(View.GONE);
                lpPieChart.clear();
            }
        });
    }

    private void setupPieChart(PieChart chart, String centerText) {
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.TRANSPARENT);
        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);

        chart.setDrawCenterText(true);
        chart.setCenterText(centerText);
        chart.setCenterTextColor(getResources().getColor(android.R.color.white, null));
        chart.setCenterTextSize(20f);

        chart.setRotationAngle(0);
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        chart.getLegend().setEnabled(false);
        chart.setEntryLabelColor(getResources().getColor(android.R.color.white, null));
    }

    private void updateChartData(PieChart chart, ArrayList<PieEntry> entries, ArrayList<Integer> colors) {
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(getResources().getColor(android.R.color.white, null));

        chart.setData(data);
        chart.post(() -> chart.animateY(1000, Easing.EaseInOutQuad));
        chart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        backgroundExecutor.shutdown();
    }
}
