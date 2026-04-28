package com.example.flowledger.ui.largepurchases;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowledger.R;
import com.example.flowledger.data.db.entity.LargePurchase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LargePurchaseAdapter extends RecyclerView.Adapter<LargePurchaseAdapter.ViewHolder> {

    private List<LargePurchase> purchases = new ArrayList<>();

    public void setPurchases(List<LargePurchase> purchases) {
        this.purchases = purchases;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_large_purchase, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LargePurchase purchase = purchases.get(position);
        
        holder.tvTitle.setText(purchase.getTitle());
        holder.tvAmount.setText(String.format(Locale.getDefault(), "₹%,.0f", purchase.getAmount()));
        holder.tvPurchaseType.setText(purchase.getPurchaseType());
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());
        holder.tvDate.setText(sdf.format(new Date(purchase.getPurchaseDate())));

        if (purchase.getPurchaseType().equals("EMI") || purchase.getPurchaseType().equals("LOAN")) {
            holder.tvEmiDetails.setVisibility(View.VISIBLE);
            holder.tvEmiDetails.setText(String.format(Locale.getDefault(), "₹%,.0f/mo (%dmo)", purchase.getEmiAmount(), purchase.getEmiMonths()));
        } else {
            holder.tvEmiDetails.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return purchases.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAmount, tvPurchaseType, tvDate, tvEmiDetails;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvPurchaseType = itemView.findViewById(R.id.tvPurchaseType);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvEmiDetails = itemView.findViewById(R.id.tvEmiDetails);
        }
    }
}
