package com.example.pokeapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TowerMapActivity extends AppCompatActivity {

    private ArrayList<String> playerTeam;
    private RecyclerView rvFloors;
    private int currentUnlockedFloor = 1;
    private SharedPreferences prefs;

    public static class FloorBoss {
        int floor;
        String titleType;
        String bossName;
        ArrayList<String> bossTeam;

        public FloorBoss(int floor, String titleType, String bossName, String[] team) {
            this.floor = floor;
            this.titleType = titleType;
            this.bossName = bossName;
            this.bossTeam = new ArrayList<>(Arrays.asList(team));
        }
    }

    private final List<FloorBoss> floorsList = new ArrayList<>();
    private FloorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tower_map);

        prefs = getSharedPreferences("TowerProgress", MODE_PRIVATE);
        currentUnlockedFloor = prefs.getInt("unlocked_floor", 1);

        playerTeam = getIntent().getStringArrayListExtra("playerTeam");
        if (playerTeam == null || playerTeam.isEmpty()) {
            playerTeam = new ArrayList<>(Arrays.asList("pikachu", "charmander", "bulbasaur"));
        }

        rvFloors = findViewById(R.id.rvFloors);
        rvFloors.setLayoutManager(new LinearLayoutManager(this));

        // Ordenar de la cima (Piso 6) a la base (Piso 1) tal como en la referencia visual
        floorsList.add(new FloorBoss(6, "PISO 6 • MAESTRO SUPREMO", "Gran Maestro Víctor", new String[]{"dragonite", "mewtwo", "rayquaza"}));
        floorsList.add(new FloorBoss(5, "PISO 5 • MAESTRO ÉLITE", "Maestro Ricardo", new String[]{"alakazam", "metagross", "mewtwo"}));
        floorsList.add(new FloorBoss(4, "PISO 4 • MAESTRO FUEGO", "Maestro Campa", new String[]{"arcanine", "moltres", "charizard"}));
        floorsList.add(new FloorBoss(3, "PISO 3 • MAESTRO DRAGÓN", "Maestro Edgar", new String[]{"dragonite", "salamence", "garchomp"}));
        floorsList.add(new FloorBoss(2, "PISO 2 • MAESTRO FANTASMA", "Maestro Everardo", new String[]{"mismagius", "chandelure", "gengar"}));
        floorsList.add(new FloorBoss(1, "PISO 1 • MAESTRO LUCHA", "Maestro Víctor", new String[]{"machamp", "blaziken", "lucario"}));

        adapter = new FloorAdapter(floorsList, currentUnlockedFloor, floorBoss -> {
            if (floorBoss.floor <= currentUnlockedFloor) {
                Intent intent = new Intent(this, TowerActivity.class);
                intent.putStringArrayListExtra("playerTeam", playerTeam);
                intent.putExtra("floorNumber", floorBoss.floor);
                intent.putExtra("bossName", floorBoss.bossName);
                intent.putStringArrayListExtra("bossTeam", floorBoss.bossTeam);
                startActivityForResult(intent, 200);
            }
        });

        rvFloors.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentUnlockedFloor = prefs.getInt("unlocked_floor", 1);
        if (adapter != null) {
            adapter.updateUnlockedFloor(currentUnlockedFloor);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            int nextFloor = data.getIntExtra("next_floor", currentUnlockedFloor);
            currentUnlockedFloor = Math.max(currentUnlockedFloor, nextFloor);
            prefs.edit().putInt("unlocked_floor", currentUnlockedFloor).apply();
            if (adapter != null) {
                adapter.updateUnlockedFloor(currentUnlockedFloor);
            }
        }
    }

    private static class FloorAdapter extends RecyclerView.Adapter<FloorAdapter.ViewHolder> {
        private final List<FloorBoss> floors;
        private int unlockedFloor;
        private final OnFloorClickListener listener;

        interface OnFloorClickListener {
            void onFloorClick(FloorBoss floorBoss);
        }

        public FloorAdapter(List<FloorBoss> floors, int unlockedFloor, OnFloorClickListener listener) {
            this.floors = floors;
            this.unlockedFloor = unlockedFloor;
            this.listener = listener;
        }

        public void updateUnlockedFloor(int unlockedFloor) {
            this.unlockedFloor = unlockedFloor;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_floor_boss, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            FloorBoss fb = floors.get(position);
            holder.tvFloorType.setText(fb.titleType);
            holder.tvBossName.setText(fb.bossName);
            holder.tvBossTeam.setText("3 Pokémon: " + fb.bossTeam.get(0).toUpperCase() + ", " + fb.bossTeam.get(1).toUpperCase() + ", " + fb.bossTeam.get(2).toUpperCase());

            // Cargar miniatura del Pokémon principal del jefe
            String firstPokemon = fb.bossTeam.get(0);
            String spriteUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + getPokemonIdByName(firstPokemon) + ".png";
            Glide.with(holder.itemView.getContext())
                    .load(spriteUrl)
                    .placeholder(android.R.drawable.ic_menu_myplaces)
                    .into(holder.ivBossAvatar);

            if (fb.floor < unlockedFloor) {
                holder.tvFloorStatus.setText("🏆 GANADA");
                holder.tvFloorStatus.setTextColor(Color.parseColor("#22C55E"));
                holder.itemView.setAlpha(0.9f);
                holder.itemView.setOnClickListener(v -> listener.onFloorClick(fb));
            } else if (fb.floor == unlockedFloor) {
                holder.tvFloorStatus.setText("⚔️ PELEAR");
                holder.tvFloorStatus.setTextColor(Color.parseColor("#38BDF8"));
                holder.itemView.setAlpha(1.0f);
                holder.itemView.setOnClickListener(v -> listener.onFloorClick(fb));
            } else {
                holder.tvFloorStatus.setText("🔒 BLOQUEADO");
                holder.tvFloorStatus.setTextColor(Color.parseColor("#64748B"));
                holder.itemView.setAlpha(0.5f);
                holder.itemView.setOnClickListener(null);
            }
        }

        private int getPokemonIdByName(String name) {
            switch (name.toLowerCase()) {
                case "machamp": return 68;
                case "blaziken": return 257;
                case "lucario": return 448;
                case "mismagius": return 429;
                case "chandelure": return 609;
                case "gengar": return 94;
                case "dragonite": return 149;
                case "salamence": return 373;
                case "garchomp": return 445;
                case "arcanine": return 59;
                case "moltres": return 146;
                case "charizard": return 6;
                case "alakazam": return 65;
                case "metagross": return 376;
                case "mewtwo": return 150;
                case "rayquaza": return 384;
                default: return 25;
            }
        }

        @Override
        public int getItemCount() {
            return floors.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvFloorType, tvBossName, tvBossTeam, tvFloorStatus;
            ImageView ivBossAvatar;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvFloorType = itemView.findViewById(R.id.tvFloorType);
                tvBossName = itemView.findViewById(R.id.tvBossName);
                tvBossTeam = itemView.findViewById(R.id.tvBossTeam);
                tvFloorStatus = itemView.findViewById(R.id.tvFloorStatus);
                ivBossAvatar = itemView.findViewById(R.id.ivBossAvatar);
            }
        }
    }
}
