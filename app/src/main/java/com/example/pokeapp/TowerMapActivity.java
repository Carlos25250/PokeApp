package com.example.pokeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TowerMapActivity extends AppCompatActivity {

    private ArrayList<String> playerTeam;
    private TextView tvTeamInfo;
    private RecyclerView rvFloors;

    public static class FloorBoss {
        int floor;
        String bossName;
        ArrayList<String> bossTeam;

        public FloorBoss(int floor, String bossName, String[] team) {
            this.floor = floor;
            this.bossName = bossName;
            this.bossTeam = new ArrayList<>(Arrays.asList(team));
        }
    }

    private final List<FloorBoss> floorsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tower_map);

        playerTeam = getIntent().getStringArrayListExtra("playerTeam");
        if (playerTeam == null) playerTeam = new ArrayList<>(Arrays.asList("pikachu", "charmander", "bulbasaur"));

        tvTeamInfo = findViewById(R.id.tvTeamInfo);
        tvTeamInfo.setText("Tu Equipo: " + playerTeam.get(0).toUpperCase() + ", " + playerTeam.get(1).toUpperCase() + ", " + playerTeam.get(2).toUpperCase());

        rvFloors = findViewById(R.id.rvFloors);
        rvFloors.setLayoutManager(new LinearLayoutManager(this));

        // Definir los jefes de los 10 pisos con sus 3 Pokémon establecidos
        floorsList.add(new FloorBoss(1, "Líder Brock", new String[]{"geodude", "onix", "vulpix"}));
        floorsList.add(new FloorBoss(2, "Líder Misty", new String[]{"staryu", "starmie", "psyduck"}));
        floorsList.add(new FloorBoss(3, "Líder Lt. Surge", new String[]{"voltorb", "pikachu", "raichu"}));
        floorsList.add(new FloorBoss(4, "Líder Erika", new String[]{"oddish", "gloom", "vileplume"}));
        floorsList.add(new FloorBoss(5, "Líder Koga", new String[]{"koffing", "grimer", "weezing"}));
        floorsList.add(new FloorBoss(6, "Líder Sabrina", new String[]{"abra", "kadabra", "alakazam"}));
        floorsList.add(new FloorBoss(7, "Líder Blaine", new String[]{"growlithe", "ponyta", "arcanine"}));
        floorsList.add(new FloorBoss(8, "Líder Giovanni", new String[]{"rhyhorn", "nidoqueen", "nidoking"}));
        floorsList.add(new FloorBoss(9, "Alto Mando Lorelei", new String[]{"jynx", "lapras", "cloyster"}));
        floorsList.add(new FloorBoss(10, "Campeón Gary", new String[]{"pidgeot", "alakazam", "charizard"}));

        FloorAdapter adapter = new FloorAdapter(floorsList, floorBoss -> {
            Intent intent = new Intent(this, TowerActivity.class);
            intent.putStringArrayListExtra("playerTeam", playerTeam);
            intent.putExtra("floorNumber", floorBoss.floor);
            intent.putExtra("bossName", floorBoss.bossName);
            intent.putStringArrayListExtra("bossTeam", floorBoss.bossTeam);
            startActivity(intent);
        });

        rvFloors.setAdapter(adapter);
    }

    private static class FloorAdapter extends RecyclerView.Adapter<FloorAdapter.ViewHolder> {
        private final List<FloorBoss> floors;
        private final OnFloorClickListener listener;

        interface OnFloorClickListener {
            void onFloorClick(FloorBoss floorBoss);
        }

        public FloorAdapter(List<FloorBoss> floors, OnFloorClickListener listener) {
            this.floors = floors;
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
            FloorBoss fb = floors.get(position);
            holder.tvFloorNumber.setText("PISO " + fb.floor);
            holder.tvBossName.setText(fb.bossName);
            holder.tvBossTeam.setText("Equipo: " + fb.bossTeam.get(0).toUpperCase() + ", " + fb.bossTeam.get(1).toUpperCase() + ", " + fb.bossTeam.get(2).toUpperCase());
            holder.btnFightFloor.setOnClickListener(v -> listener.onFloorClick(fb));
        }

        @Override
        public int getItemCount() {
            return floors.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvFloorNumber, tvBossName, tvBossTeam;
            Button btnFightFloor;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvFloorNumber = itemView.findViewById(R.id.tvFloorNumber);
                tvBossName = itemView.findViewById(R.id.tvBossName);
                tvBossTeam = itemView.findViewById(R.id.tvBossTeam);
                btnFightFloor = itemView.findViewById(R.id.btnFightFloor);
            }
        }
    }
}
