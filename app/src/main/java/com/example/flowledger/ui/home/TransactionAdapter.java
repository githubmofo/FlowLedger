package com.example.flowledger.ui.home;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowledger.R;
import com.example.flowledger.data.db.entity.Transaction;
import com.example.flowledger.data.db.entity.TransactionWithCategory;

import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    public interface OnTransactionClickListener {
        void onTransactionClick(TransactionWithCategory transaction);
    }

    private List<TransactionWithCategory> transactions = new ArrayList<>();
    private OnTransactionClickListener listener;

    // Pre-parsed colors to avoid parsing on every bind
    private static final int COLOR_EXPENSE = Color.parseColor("#FF5252");
    private static final int COLOR_INCOME = Color.parseColor("#4CAF50");

    public void setTransactions(List<TransactionWithCategory> newTransactions) {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new TransactionDiffCallback(this.transactions, newTransactions));
        this.transactions = new ArrayList<>(newTransactions);
        result.dispatchUpdatesTo(this);
    }

    public void setOnTransactionClickListener(OnTransactionClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        TransactionWithCategory item = transactions.get(position);
        Transaction t = item.transaction;
        
        holder.tvNote.setText(t.getNote().isEmpty() ? "No Note" : t.getNote());
        holder.tvCategory.setText(item.category != null ? item.category.name : "Uncategorized");
        holder.tvIcon.setText(item.category != null ? item.category.icon : "💰");
        holder.tvPaymentMode.setText(t.getPaymentMode() != null ? t.getPaymentMode() : "Cash");
        
        boolean isExpense = "EXPENSE".equals(t.getType());
        String prefix = isExpense ? "-" : "+";
        holder.tvAmount.setText(prefix + "₹" + String.format("%.2f", t.getAmount()));
        holder.tvAmount.setTextColor(isExpense ? COLOR_EXPENSE : COLOR_INCOME);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTransactionClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    @Override
    public void onViewRecycled(@NonNull TransactionViewHolder holder) {
        super.onViewRecycled(holder);
        holder.itemView.setOnClickListener(null);
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcon, tvNote, tvCategory, tvAmount, tvPaymentMode;

        TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIcon = itemView.findViewById(R.id.tvIcon);
            tvNote = itemView.findViewById(R.id.tvNote);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvPaymentMode = itemView.findViewById(R.id.tvPaymentMode);
        }
    }

    /**
     * DiffUtil callback for efficient RecyclerView updates.
     * Only items that actually changed are rebound, instead of the entire list.
     */
    static class TransactionDiffCallback extends DiffUtil.Callback {
        private final List<TransactionWithCategory> oldList;
        private final List<TransactionWithCategory> newList;

        TransactionDiffCallback(List<TransactionWithCategory> oldList, List<TransactionWithCategory> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldPos, int newPos) {
            return oldList.get(oldPos).transaction.getId() == newList.get(newPos).transaction.getId();
        }

        @Override
        public boolean areContentsTheSame(int oldPos, int newPos) {
            Transaction oldT = oldList.get(oldPos).transaction;
            Transaction newT = newList.get(newPos).transaction;
            return oldT.getAmount() == newT.getAmount()
                    && oldT.getTimestamp() == newT.getTimestamp()
                    && oldT.getCategoryId() == newT.getCategoryId()
                    && safeEquals(oldT.getNote(), newT.getNote())
                    && safeEquals(oldT.getPaymentMode(), newT.getPaymentMode());
        }

        private boolean safeEquals(String a, String b) {
            if (a == null && b == null) return true;
            if (a == null || b == null) return false;
            return a.equals(b);
        }
    }
}
