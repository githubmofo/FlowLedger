package com.example.flowledger.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowledger.R;
import com.example.flowledger.data.db.entity.LargePurchase;
import com.example.flowledger.ui.home.TransactionAdapter;
import com.example.flowledger.ui.addexpense.AddExpenseBottomSheetFragment;
import com.example.flowledger.ui.largepurchases.AddLargePurchaseBottomSheetFragment;
import com.example.flowledger.ui.largepurchases.LargePurchaseAdapter;
import com.example.flowledger.ui.largepurchases.LargePurchaseViewModel;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private SearchViewModel viewModel;
    private LargePurchaseViewModel largePurchaseViewModel;
    private TransactionAdapter adapter;
    private LargePurchaseAdapter largePurchaseAdapter;
    private View tvEmptyState;
    private TextView tvLargePurchasesHeader;
    private RecyclerView rvSearchLargePurchases;
    private EditText etSearch;
    private ChipGroup cgFilters;
    private List<LargePurchase> allLargePurchases = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        largePurchaseViewModel = new ViewModelProvider(this).get(LargePurchaseViewModel.class);

        etSearch = view.findViewById(R.id.etSearch);
        cgFilters = view.findViewById(R.id.cgFilters);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        tvLargePurchasesHeader = view.findViewById(R.id.tvLargePurchasesHeader);
        rvSearchLargePurchases = view.findViewById(R.id.rvSearchLargePurchases);
        RecyclerView rvSearchResults = view.findViewById(R.id.rvSearchResults);

        adapter = new TransactionAdapter();
        rvSearchResults.setAdapter(adapter);

        largePurchaseAdapter = new LargePurchaseAdapter();
        rvSearchLargePurchases.setAdapter(largePurchaseAdapter);

        largePurchaseAdapter.setOnLargePurchaseClickListener(purchase -> {
            AddLargePurchaseBottomSheetFragment editSheet = AddLargePurchaseBottomSheetFragment.newInstance(purchase.getId());
            editSheet.show(getParentFragmentManager(), "edit_large_purchase");
        });

        adapter.setOnTransactionClickListener(transaction -> {
            AddExpenseBottomSheetFragment editSheet = AddExpenseBottomSheetFragment.newInstance(transaction.transaction.getId());
            editSheet.show(getParentFragmentManager(), "edit_expense");
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                viewModel.setSearchQuery(query);
                filterLargePurchases(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        cgFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                viewModel.setSearchQuery(etSearch.getText().toString());
                filterLargePurchases(etSearch.getText().toString());
                return;
            }
            int checkedId = checkedIds.get(0);
            
            if (checkedId == R.id.chipLarge) {
                // Large purchases mode
                tvLargePurchasesHeader.setVisibility(View.VISIBLE);
                rvSearchLargePurchases.setVisibility(View.VISIBLE);
                largePurchaseAdapter.setPurchases(allLargePurchases);
                viewModel.setSearchQuery("NON_EXISTENT_QUERY_FOR_EMPTY"); // Hack to empty normal transactions
                return;
            }

            String filter = "";
            if (checkedId == R.id.chipUPI) filter = "UPI";
            else if (checkedId == R.id.chipCash) filter = "Cash";
            else if (checkedId == R.id.chipCard) filter = "Card";

            if (filter.isEmpty()) {
                viewModel.setSearchQuery(etSearch.getText().toString());
            } else {
                viewModel.setSearchQuery(filter);
            }
            filterLargePurchases(etSearch.getText().toString());
        });

        viewModel.getSearchResults().observe(getViewLifecycleOwner(), transactions -> {
            boolean hasTransactions = transactions != null && !transactions.isEmpty();
            boolean hasLargePurchases = rvSearchLargePurchases.getVisibility() == View.VISIBLE;

            if (!hasTransactions && !hasLargePurchases) {
                tvEmptyState.setVisibility(View.VISIBLE);
                adapter.setTransactions(java.util.Collections.emptyList());
            } else {
                tvEmptyState.setVisibility(View.GONE);
                adapter.setTransactions(transactions);
            }
        });

        largePurchaseViewModel.getAllLargePurchases().observe(getViewLifecycleOwner(), purchases -> {
            allLargePurchases = purchases != null ? purchases : new ArrayList<>();
            filterLargePurchases(etSearch.getText().toString());
        });
    }

    private void filterLargePurchases(String query) {
        if (query == null || query.trim().isEmpty()) {
            tvLargePurchasesHeader.setVisibility(View.GONE);
            rvSearchLargePurchases.setVisibility(View.GONE);
            largePurchaseAdapter.setPurchases(new ArrayList<>());
            return;
        }
        
        List<LargePurchase> filtered = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        for (LargePurchase p : allLargePurchases) {
            if (p.getTitle().toLowerCase().contains(lowerQuery)) {
                filtered.add(p);
            }
        }
        
        if (!filtered.isEmpty()) {
            tvLargePurchasesHeader.setVisibility(View.VISIBLE);
            rvSearchLargePurchases.setVisibility(View.VISIBLE);
            largePurchaseAdapter.setPurchases(filtered);
            tvEmptyState.setVisibility(View.GONE);
        } else {
            tvLargePurchasesHeader.setVisibility(View.GONE);
            rvSearchLargePurchases.setVisibility(View.GONE);
            largePurchaseAdapter.setPurchases(new ArrayList<>());
        }
    }
}
