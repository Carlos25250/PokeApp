package com.example.pokeapp;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(MenuItem item);
    }

    private final List<MenuItem> itemList;
    private final OnItemClickListener listener;

    public MenuAdapter(List<MenuItem> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_card, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem item = itemList.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvSubtitle.setText(item.getSubtitle());
        holder.ivIcon.setImageResource(item.getIconResId());

        // Convertir altura DP a Pixeles
        int heightPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                item.getHeightDp(),
                holder.itemView.getContext().getResources().getDisplayMetrics()
        );

        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        params.height = heightPx;
        holder.itemView.setLayoutParams(params);

        // Asigna el color dinámico al hexágono pintándolo directamente en la vista
        try {
            int color = Color.parseColor(item.getColorHex());
            holder.cardContainer.setHexagonColor(color);
        } catch (Exception e) {
            holder.cardContainer.setHexagonColor(Color.parseColor("#CC1E293B"));
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle, tvSubtitle;
        HexagonView cardContainer;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
            cardContainer = itemView.findViewById(R.id.cardContainer);
        }
    }
}