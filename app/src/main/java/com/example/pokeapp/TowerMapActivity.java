package com.example.pokeapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TowerMapActivity extends AppCompatActivity {

    private RecyclerView rvTowerFloors;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#0F172A"));
        }

        setContentView(R.layout.activity_tower_map);

        sharedPreferences = getSharedPreferences("PokeTowerPrefs", Context.MODE_PRIVATE);
        int unlockedFloor = sharedPreferences.getInt("UNLOCKED_FLOOR", 1);

        rvTowerFloors = findViewById(R.id.rvTowerFloors);
        rvTowerFloors.setLayoutManager(new LinearLayoutManager(this));

        List<FloorItem> floors = new ArrayList<>();
        floors.add(new FloorItem(1, "MAESTRO LUCHA", "Angélica", "3 Pokémon: MACHAMP, BLAZIKEN, LUCARIO", R.drawable.piso1, 1 <= unlockedFloor));
        floors.add(new FloorItem(2, "MAESTRO FANTASMA", "Víctor", "3 Pokémon: MISMAGIUS, CHANDELURE, GENGAR", R.drawable.piso2, 2 <= unlockedFloor));
        floors.add(new FloorItem(3, "MAESTRO DRAGÓN", "Campa", "3 Pokémon: DRAGONITE, SALAMENCE, GARCHOMP", R.drawable.piso3, 3 <= unlockedFloor));
        floors.add(new FloorItem(4, "MAESTRO FUEGO", "Alina", "3 Pokémon: ARCANINE, MOLTRES, CHARIZARD", R.drawable.piso4, 4 <= unlockedFloor));
        floors.add(new FloorItem(5, "MAESTRO ÉLITE", "Rita", "3 Pokémon: ALAKAZAM, METAGROSS, MEWTWO", R.drawable.piso5, 5 <= unlockedFloor));
        floors.add(new FloorItem(6, "MAESTRO SUPREMO", "Everardo", "3 Pokémon: DRAGONITE, MEWTWO, RAYQUAZA", R.drawable.piso6, 6 <= unlockedFloor));

        TowerFloorAdapter adapter = new TowerFloorAdapter(floors, item -> {
            if (item.isUnlocked) {
                Intent intent = new Intent(TowerMapActivity.this, BattleActivity.class);
                intent.putExtra("FLOOR_NUMBER", item.floorNumber);
                intent.putExtra("BOSS_NAME", item.bossName);
                startActivity(intent);
            } else {
                Toast.makeText(TowerMapActivity.this, "¡Supera los pisos anteriores para desbloquear!", Toast.LENGTH_SHORT).show();
            }
        });

        rvTowerFloors.setAdapter(adapter);
    }

    public static class FloorItem {
        int floorNumber;
        String subtitle;
        String bossName;
        String teamDesc;
        int imageResId;
        boolean isUnlocked;

        public FloorItem(int floorNumber, String subtitle, String bossName, String teamDesc, int imageResId, boolean isUnlocked) {
            this.floorNumber = floorNumber;
            this.subtitle = subtitle;
            this.bossName = bossName;
            this.teamDesc = teamDesc;
            this.imageResId = imageResId;
            this.isUnlocked = isUnlocked;
        }
    }

    private static class TowerFloorAdapter extends RecyclerView.Adapter<TowerFloorAdapter.ViewHolder> {

        interface OnItemClickListener {
            void onItemClick(FloorItem item);
        }

        private final List<FloorItem> floors;
        private final OnItemClickListener listener;

        public TowerFloorAdapter(List<FloorItem> floors, OnItemClickListener listener) {
            this.floors = floors;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tower_floor, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            FloorItem item = floors.get(position);

            holder.tvFloorSubtitle.setText("PISO " + item.floorNumber + " • " + item.subtitle);
            holder.tvBossName.setText(item.bossName);
            holder.tvBossTeam.setText(item.teamDesc);

            // Muestra foto en chiquito si está desbloqueado, o el candado rojo si está bloqueado
            if (item.isUnlocked) {
                holder.ivFloorThumb.setImageResource(item.imageResId);
                holder.ivFloorThumb.setAlpha(1.0f);
                holder.ivLockIcon.setVisibility(View.GONE);

                holder.btnFight.setEnabled(true);
                holder.btnFight.setText("⚔ PELEAR");
                holder.btnFight.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#00E5FF")));
                holder.btnFight.setTextColor(Color.parseColor("#0F172A"));
            } else {
                holder.ivFloorThumb.setImageResource(item.imageResId);
                holder.ivFloorThumb.setAlpha(0.2f); // Oscurece la miniatura
                holder.ivLockIcon.setVisibility(View.VISIBLE);

                holder.btnFight.setEnabled(false);
                holder.btnFight.setText("BLOQUEADO");
                holder.btnFight.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#334155")));
                holder.btnFight.setTextColor(Color.parseColor("#94A3B8"));
            }

            holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
            holder.btnFight.setOnClickListener(v -> listener.onItemClick(item));
        }

        @Override
        public int getItemCount() {
            return floors.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivFloorThumb, ivLockIcon;
            TextView tvFloorSubtitle, tvBossName, tvBossTeam;
            Button btnFight;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivFloorThumb = itemView.findViewById(R.id.ivFloorThumb);
                ivLockIcon = itemView.findViewById(R.id.ivLockIcon);
                tvFloorSubtitle = itemView.findViewById(R.id.tvFloorSubtitle);
                tvBossName = itemView.findViewById(R.id.tvBossName);
                tvBossTeam = itemView.findViewById(R.id.tvBossTeam);
                btnFight = itemView.findViewById(R.id.btnFight);
            }
        }
    }
}