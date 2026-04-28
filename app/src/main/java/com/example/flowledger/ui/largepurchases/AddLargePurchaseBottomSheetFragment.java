package com.example.flowledger.ui.largepurchases;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.flowledger.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddLargePurchaseBottomSheetFragment extends BottomSheetDialogFragment {

    private LargePurchaseViewModel viewModel;
    private EditText etTitleInput, etAmount, etEmiAmount, etEmiMonths;
    private TextView tvDate;
    private ChipGroup cgPurchaseType;
    private LinearLayout layoutEmiFields;
    private MaterialButton btnSave;
    
    private long purchaseTimestamp;
    private int purchaseId = -1;

    public static AddLargePurchaseBottomSheetFragment newInstance(int id) {
        AddLargePurchaseBottomSheetFragment fragment = new AddLargePurchaseBottomSheetFragment();
        Bundle args = new Bundle();
        args.putInt("purchase_id", id);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog d = (BottomSheetDialog) dialogInterface;
            View bottomSheetInternal = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheetInternal != null) {
                BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_add_large_purchase, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(LargePurchaseViewModel.class);
        
        etTitleInput = view.findViewById(R.id.etTitleInput);
        etAmount = view.findViewById(R.id.etAmount);
        etEmiAmount = view.findViewById(R.id.etEmiAmount);
        etEmiMonths = view.findViewById(R.id.etEmiMonths);
        tvDate = view.findViewById(R.id.tvDate);
        cgPurchaseType = view.findViewById(R.id.cgPurchaseType);
        layoutEmiFields = view.findViewById(R.id.layoutEmiFields);
        btnSave = view.findViewById(R.id.btnSave);
        
        if (getArguments() != null) {
            purchaseId = getArguments().getInt("purchase_id", -1);
        }

        purchaseTimestamp = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault());
        tvDate.setText(sdf.format(new Date(purchaseTimestamp)));

        if (purchaseId != -1) {
            viewModel.getLargePurchaseById(purchaseId).observe(getViewLifecycleOwner(), purchase -> {
                if (purchase != null) {
                    etTitleInput.setText(purchase.getTitle());
                    etAmount.setText(String.valueOf(purchase.getAmount()));
                    purchaseTimestamp = purchase.getPurchaseDate();
                    tvDate.setText(sdf.format(new Date(purchaseTimestamp)));
                    
                    if (purchase.getPurchaseType().equals("EMI")) {
                        cgPurchaseType.check(R.id.chipEmi);
                        layoutEmiFields.setVisibility(View.VISIBLE);
                        etEmiAmount.setText(String.valueOf(purchase.getEmiAmount()));
                        etEmiMonths.setText(String.valueOf(purchase.getEmiMonths()));
                    } else if (purchase.getPurchaseType().equals("LOAN")) {
                        cgPurchaseType.check(R.id.chipLoan);
                        layoutEmiFields.setVisibility(View.VISIBLE);
                        etEmiAmount.setText(String.valueOf(purchase.getEmiAmount()));
                        etEmiMonths.setText(String.valueOf(purchase.getEmiMonths()));
                    } else {
                        cgPurchaseType.check(R.id.chipOneTime);
                        layoutEmiFields.setVisibility(View.GONE);
                    }
                    
                    btnSave.setText("Update");
                }
            });
        } else {
            cgPurchaseType.check(R.id.chipOneTime);
        }
        cgPurchaseType.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int checkedId = checkedIds.get(0);
                if (checkedId == R.id.chipEmi || checkedId == R.id.chipLoan) {
                    layoutEmiFields.setVisibility(View.VISIBLE);
                } else {
                    layoutEmiFields.setVisibility(View.GONE);
                }
            }
        });

        btnSave.setOnClickListener(v -> savePurchase());
    }

    private void savePurchase() {
        String title = etTitleInput.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        
        if (title.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(getContext(), "Please enter title and amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        String purchaseType = "ONE_TIME";
        
        double emiAmount = 0.0;
        int emiMonths = 0;
        double loanPrincipal = 0.0;

        int selectedTypeId = cgPurchaseType.getCheckedChipId();
        if (selectedTypeId == R.id.chipEmi) {
            purchaseType = "EMI";
        } else if (selectedTypeId == R.id.chipLoan) {
            purchaseType = "LOAN";
            loanPrincipal = amount; 
        }

        if (purchaseType.equals("EMI") || purchaseType.equals("LOAN")) {
            String emiAmtStr = etEmiAmount.getText().toString().trim();
            String emiMthStr = etEmiMonths.getText().toString().trim();
            
            if (emiAmtStr.isEmpty() || emiMthStr.isEmpty()) {
                Toast.makeText(getContext(), "Please enter EMI details", Toast.LENGTH_SHORT).show();
                return;
            }
            emiAmount = Double.parseDouble(emiAmtStr);
            emiMonths = Integer.parseInt(emiMthStr);
        }

        // Hardcoding Category ID and Payment Method for V1 simplicity
        int categoryId = 1; 
        String paymentMethod = "Card";

        if (purchaseId == -1) {
            viewModel.saveLargePurchase(title, amount, categoryId, paymentMethod, purchaseType, purchaseTimestamp, "", emiAmount, emiMonths, loanPrincipal);
            Toast.makeText(getContext(), "Large Purchase Saved", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.updateLargePurchase(purchaseId, title, amount, categoryId, paymentMethod, purchaseType, purchaseTimestamp, "", emiAmount, emiMonths, loanPrincipal);
            Toast.makeText(getContext(), "Large Purchase Updated", Toast.LENGTH_SHORT).show();
        }
        
        dismiss();
    }
}
