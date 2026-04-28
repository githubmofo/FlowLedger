package com.example.flowledger.ui.addexpense;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowledger.R;
import com.example.flowledger.data.db.entity.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categories = new ArrayList<>();
    private int selectedPosition = -1;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_chip, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.tvName.setText(category.getIcon() + " " + category.getName());
        
        if (selectedPosition == position) {
            holder.itemView.setBackgroundResource(R.drawable.bg_category_selected);
            holder.tvName.setTextColor(Color.WHITE);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_category_unselected);
            holder.tvName.setTextColor(Color.parseColor("#80FFFFFF"));
        }

        holder.itemView.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public Category getSelectedCategory() {
        if (selectedPosition != -1) {
            return categories.get(selectedPosition);
        }
        return null;
    }

    public void setSelectedCategoryId(int categoryId) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId() == categoryId) {
                int oldPosition = selectedPosition;
                selectedPosition = i;
                notifyItemChanged(oldPosition);
                notifyItemChanged(selectedPosition);
                break;
            }
        }
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}
