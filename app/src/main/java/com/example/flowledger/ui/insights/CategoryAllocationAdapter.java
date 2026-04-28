package com.example.flowledger.ui.insights;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.flowledger.R;
import com.example.flowledger.data.db.entity.CategorySpending;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import java.util.ArrayList;
import java.util.List;

public class CategoryAllocationAdapter extends RecyclerView.Adapter<CategoryAllocationAdapter.ViewHolder> {
    private List<CategorySpending> items = new ArrayList<>();
    private double totalAmount = 0;

    public void setData(List<CategorySpending> newItems, double total) {
        this.items = newItems;
        this.totalAmount = total;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_spending, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategorySpending item = items.get(position);
        holder.tvCategory.setText(item.categoryName);
        holder.tvAmount.setText(String.format("₹%.0f", item.totalAmount));
        
        int progress = 0;
        if (totalAmount > 0) {
            progress = (int) ((item.totalAmount / totalAmount) * 100);
        }
        holder.progressCategory.setProgressCompat(progress, true);
        
        try {
            int color = Color.parseColor(item.categoryColor);
            holder.progressCategory.setIndicatorColor(color);
        } catch (Exception e) {
            // fallback handled by xml
        }
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvAmount;
        LinearProgressIndicator progressCategory;

        ViewHolder(View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            progressCategory = itemView.findViewById(R.id.progressCategory);
        }
    }
}
