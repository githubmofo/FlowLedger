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
import com.example.flowledger.ui.home.TransactionAdapter;
import com.example.flowledger.ui.addexpense.AddExpenseBottomSheetFragment;
import com.google.android.material.chip.ChipGroup;

public class SearchFragment extends Fragment {

    private SearchViewModel viewModel;
    private TransactionAdapter adapter;
    private View tvEmptyState;
    private EditText etSearch;
    private ChipGroup cgFilters;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        etSearch = view.findViewById(R.id.etSearch);
        cgFilters = view.findViewById(R.id.cgFilters);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        RecyclerView rvSearchResults = view.findViewById(R.id.rvSearchResults);

        adapter = new TransactionAdapter();
        rvSearchResults.setAdapter(adapter);

        adapter.setOnTransactionClickListener(transaction -> {
            AddExpenseBottomSheetFragment editSheet = AddExpenseBottomSheetFragment.newInstance(transaction.transaction.getId());
            editSheet.show(getParentFragmentManager(), "edit_expense");
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchQuery(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        cgFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                viewModel.setSearchQuery(etSearch.getText().toString());
                return;
            }
            int checkedId = checkedIds.get(0);
            String filter = "";
            if (checkedId == R.id.chipUPI) filter = "UPI";
            else if (checkedId == R.id.chipCash) filter = "Cash";
            else if (checkedId == R.id.chipCard) filter = "Card";

            if (filter.isEmpty()) {
                viewModel.setSearchQuery(etSearch.getText().toString());
            } else {
                viewModel.setSearchQuery(filter);
            }
        });

        viewModel.getSearchResults().observe(getViewLifecycleOwner(), transactions -> {
            if (transactions == null || transactions.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
                adapter.setTransactions(java.util.Collections.emptyList());
            } else {
                tvEmptyState.setVisibility(View.GONE);
                adapter.setTransactions(transactions);
            }
        });
    }
}
