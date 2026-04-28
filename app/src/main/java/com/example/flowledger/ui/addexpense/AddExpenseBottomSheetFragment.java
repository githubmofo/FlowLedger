package com.example.flowledger.ui.addexpense;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowledger.R;
import com.example.flowledger.data.db.entity.Category;
import com.example.flowledger.data.db.entity.QuickPattern;
import com.example.flowledger.data.db.entity.Transaction;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class AddExpenseBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_TRANSACTION_ID = "transaction_id";

    private AddExpenseViewModel viewModel;
    private CategoryAdapter categoryAdapter;
    private EditText etAmount, etNote;
    private TextView tvTitle;
    private ChipGroup cgQuickAdd, cgPaymentMode;
    private MaterialButton btnSave, btnDelete;
    
    private int transactionId = -1;
    private long existingTimestamp = 0;

    public static AddExpenseBottomSheetFragment newInstance(int transactionId) {
        AddExpenseBottomSheetFragment fragment = new AddExpenseBottomSheetFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TRANSACTION_ID, transactionId);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        com.google.android.material.bottomsheet.BottomSheetDialog dialog = (com.google.android.material.bottomsheet.BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            com.google.android.material.bottomsheet.BottomSheetDialog d = (com.google.android.material.bottomsheet.BottomSheetDialog) dialogInterface;
            android.widget.FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                com.google.android.material.bottomsheet.BottomSheetBehavior.from(bottomSheet).setState(com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        android.view.Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_add_expense, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            transactionId = getArguments().getInt(ARG_TRANSACTION_ID, -1);
        }

        viewModel = new ViewModelProvider(this).get(AddExpenseViewModel.class);
        
        tvTitle = view.findViewById(R.id.tvTitle);
        etAmount = view.findViewById(R.id.etAmount);
        etNote = view.findViewById(R.id.etNote);
        cgQuickAdd = view.findViewById(R.id.cgQuickAdd);
        cgPaymentMode = view.findViewById(R.id.cgPaymentMode);
        btnSave = view.findViewById(R.id.btnSave);
        btnDelete = view.findViewById(R.id.btnDelete);
        RecyclerView rvCategories = view.findViewById(R.id.rvCategories);

        categoryAdapter = new CategoryAdapter();
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);

        cgPaymentMode.check(R.id.chipUPI);

        viewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            categoryAdapter.setCategories(categories);
            // If editing, wait for categories to be loaded before selecting the current one
            if (transactionId != -1) {
                loadTransactionData();
            }
        });

        viewModel.getTopPatterns().observe(getViewLifecycleOwner(), this::populateQuickAddChips);

        btnSave.setOnClickListener(v -> saveTransaction());
        btnDelete.setOnClickListener(v -> deleteTransaction());

        if (transactionId != -1) {
            tvTitle.setText("Edit Transaction");
            btnSave.setText("Update Transaction");
            btnDelete.setVisibility(View.VISIBLE);
        }
    }

    private void loadTransactionData() {
        viewModel.getTransaction(transactionId).observe(getViewLifecycleOwner(), transaction -> {
            if (transaction != null) {
                etAmount.setText(String.valueOf(transaction.getAmount()));
                etNote.setText(transaction.getNote());
                existingTimestamp = transaction.getTimestamp();
                categoryAdapter.setSelectedCategoryId(transaction.getCategoryId());
                
                if ("UPI".equals(transaction.getPaymentMode())) cgPaymentMode.check(R.id.chipUPI);
                else if ("Cash".equals(transaction.getPaymentMode())) cgPaymentMode.check(R.id.chipCash);
                else if ("Card".equals(transaction.getPaymentMode())) cgPaymentMode.check(R.id.chipCard);
            }
        });
    }

    private void populateQuickAddChips(List<QuickPattern> patterns) {
        cgQuickAdd.removeAllViews();
        int[] quickAmounts = {10, 20, 50, 100};
        for (int amount : quickAmounts) {
            Chip chip = new Chip(getContext());
            chip.setText("₹" + amount);
            chip.setOnClickListener(v -> etAmount.setText(String.valueOf(amount)));
            cgQuickAdd.addView(chip);
        }

        if (patterns == null || patterns.isEmpty()) return;
        for (QuickPattern pattern : patterns) {
            Chip chip = new Chip(getContext());
            chip.setText(String.format("₹%.0f - %s", pattern.getAmount(), pattern.getNote()));
            chip.setOnClickListener(v -> {
                etAmount.setText(String.valueOf(pattern.getAmount()));
                etNote.setText(pattern.getNote());
                categoryAdapter.setSelectedCategoryId(pattern.getCategoryId());
                if ("UPI".equals(pattern.getPaymentMode())) cgPaymentMode.check(R.id.chipUPI);
                else if ("Cash".equals(pattern.getPaymentMode())) cgPaymentMode.check(R.id.chipCash);
                else if ("Card".equals(pattern.getPaymentMode())) cgPaymentMode.check(R.id.chipCard);
            });
            cgQuickAdd.addView(chip);
        }
    }

    private void saveTransaction() {
        String amountStr = etAmount.getText().toString();
        if (amountStr.isEmpty()) {
            Toast.makeText(getContext(), "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        String note = etNote.getText().toString();
        Category selectedCategory = categoryAdapter.getSelectedCategory();

        if (selectedCategory == null) {
            Toast.makeText(getContext(), "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        String paymentMode = "UPI";
        int checkedId = cgPaymentMode.getCheckedChipId();
        if (checkedId == R.id.chipCash) paymentMode = "Cash";
        else if (checkedId == R.id.chipCard) paymentMode = "Card";

        if (transactionId == -1) {
            viewModel.saveTransaction(amount, note, selectedCategory.getId(), "EXPENSE", paymentMode);
            Toast.makeText(getContext(), "Transaction saved!", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.updateTransaction(transactionId, amount, note, selectedCategory.getId(), "EXPENSE", paymentMode, existingTimestamp);
            Toast.makeText(getContext(), "Transaction updated!", Toast.LENGTH_SHORT).show();
        }
        dismiss();
    }

    private void deleteTransaction() {
        viewModel.getTransaction(transactionId).observe(getViewLifecycleOwner(), transaction -> {
            if (transaction != null) {
                viewModel.deleteTransaction(transaction);
                Toast.makeText(getContext(), "Transaction deleted", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
}
