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
    private PieChart pieChart;
    private TextView tvTopCategory, tvDailyAvg, tvEmptyAllocation;
    private RecyclerView rvCategoryBreakdown;
    private CategoryAllocationAdapter allocationAdapter;
    private final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private List<CategorySpending> baseSpending = new ArrayList<>();
    private List<LargePurchase> largePurchasesList = new ArrayList<>();

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
        tvTopCategory = view.findViewById(R.id.tvTopCategory);
        tvDailyAvg = view.findViewById(R.id.tvDailyAvg);
        tvEmptyAllocation = view.findViewById(R.id.tvEmptyAllocation);
        rvCategoryBreakdown = view.findViewById(R.id.rvCategoryBreakdown);

        allocationAdapter = new CategoryAllocationAdapter();
        rvCategoryBreakdown.setAdapter(allocationAdapter);

        setupPieChart();

        viewModel.getCategorySpending().observe(getViewLifecycleOwner(), spendingList -> {
            baseSpending = spendingList != null ? spendingList : new ArrayList<>();
            processInsights();
        });

        largePurchaseViewModel.getAllLargePurchases().observe(getViewLifecycleOwner(), purchases -> {
            largePurchasesList = purchases != null ? purchases : new ArrayList<>();
            processInsights();
        });
    }

    private void processInsights() {
        List<CategorySpending> mergedList = new ArrayList<>();
        
        for (CategorySpending cs : baseSpending) {
            CategorySpending copy = new CategorySpending();
            copy.categoryName = cs.categoryName;
            copy.totalAmount = cs.totalAmount;
            copy.categoryColor = cs.categoryColor;
            mergedList.add(copy);
        }

        for (LargePurchase lp : largePurchasesList) {
            boolean found = false;
            for (CategorySpending ms : mergedList) {
                if (ms.categoryName.equals("Large Purchases")) {
                    ms.totalAmount += lp.getAmount();
                    found = true;
                    break;
                }
            }
            if (!found) {
                CategorySpending newCs = new CategorySpending();
                newCs.categoryName = "Large Purchases";
                newCs.totalAmount = lp.getAmount();
                newCs.categoryColor = "#FF9800"; // default color
                mergedList.add(newCs);
            }
        }

        mergedList.sort((a, b) -> Double.compare(b.totalAmount, a.totalAmount));

        if (mergedList != null && !mergedList.isEmpty()) {
                tvEmptyAllocation.setVisibility(View.GONE);
                rvCategoryBreakdown.setVisibility(View.VISIBLE);
                // Process chart data on background thread to avoid UI jank
                backgroundExecutor.execute(() -> {
                    final ArrayList<PieEntry> entries = new ArrayList<>();
                    final ArrayList<Integer> colors = new ArrayList<>();
                    double maxAmount = 0;
                    CategorySpending topCategory = mergedList.get(0);
                    double totalAmount = 0;

                    for (CategorySpending spending : mergedList) {
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

                    // Post results back to UI thread
                    mainHandler.post(() -> {
                        if (!isAdded()) return; // Fragment might have been detached

                        tvTopCategory.setText(topName);
                        tvDailyAvg.setText(String.format("₹%.0f", dailyAvg));
                        updateChartData(entries, colors);
                        
                        allocationAdapter.setData(mergedList, totalAmountFinal);
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
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);

        pieChart.setDrawCenterText(true);
        pieChart.setCenterText("Spending");
        pieChart.setCenterTextColor(getResources().getColor(android.R.color.white, null)); // Keep white for contrast on donut
        pieChart.setCenterTextSize(20f);

        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        pieChart.getLegend().setEnabled(false);
        pieChart.setEntryLabelColor(getResources().getColor(android.R.color.white, null));
    }

    private void updateChartData(ArrayList<PieEntry> entries, ArrayList<Integer> colors) {
        PieDataSet dataSet = new PieDataSet(entries, "Category Spending");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(getResources().getColor(android.R.color.white, null));

        pieChart.setData(data);
        // Defer animation slightly so the layout is fully settled first
        pieChart.post(() -> pieChart.animateY(1000, Easing.EaseInOutQuad));
        pieChart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        backgroundExecutor.shutdown();
    }
}
