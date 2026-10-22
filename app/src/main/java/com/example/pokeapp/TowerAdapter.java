package com.example.pokeapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pokeapp.model.FloorBossModel;

import java.util.List;

public class TowerAdapter extends RecyclerView.Adapter<TowerAdapter.ViewHolder> {

    public interface OnBossClickListener {
        void onBossClick(FloorBossModel boss);
    }

    private final List<FloorBossModel> bossList;
    private final OnBossClickListener listener;

    public TowerAdapter(List<FloorBossModel> bossList, OnBossClickListener listener) {
        this.bossList = bossList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_floor_boss, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FloorBossModel boss = bossList.get(position);

        holder.tvFloorTag.setText(boss.getFloorTag());
        holder.tvBossName.setText(boss.getBossName());
        holder.tvBossTeam.setText(boss.getBossTeam());

        Glide.with(holder.itemView.getContext())
                .load(boss.getBossImageUrl())
                .into(holder.ivBossAvatar);

        if (boss.isUnlocked()) {
            holder.ivLockStatus.setVisibility(View.GONE);
            holder.btnFight.setEnabled(true);
            holder.btnFight.setAlpha(1.0f);
            holder.btnFight.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBossClick(boss);
                }
            });
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBossClick(boss);
                }
            });
        } else {
            holder.ivLockStatus.setVisibility(View.VISIBLE);
            holder.btnFight.setEnabled(false);
            holder.btnFight.setAlpha(0.5f);
            holder.btnFight.setOnClickListener(null);
            holder.itemView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return bossList != null ? bossList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBossAvatar;
        ImageView ivLockStatus;
        TextView tvFloorTag;
        TextView tvBossName;
        TextView tvBossTeam;
        Button btnFight;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBossAvatar = itemView.findViewById(R.id.ivBossAvatar);
            ivLockStatus = itemView.findViewById(R.id.ivLockStatus);
            tvFloorTag = itemView.findViewById(R.id.tvFloorTag);
            tvBossName = itemView.findViewById(R.id.tvBossName);
            tvBossTeam = itemView.findViewById(R.id.tvBossTeam);
            btnFight = itemView.findViewById(R.id.btnFight);
        }
    }
}
