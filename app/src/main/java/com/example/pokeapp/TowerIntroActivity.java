package com.example.pokeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.pokeapp.api.PokeApiService;
import com.example.pokeapp.model.Pokemon;
import java.util.ArrayList;
import java.util.Random;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TowerIntroActivity extends AppCompatActivity {

    private ImageView ivTeam1, ivTeam2, ivTeam3, ivTowerIntroBg;
    private TextView tvTeam1, tvTeam2, tvTeam3;
    private Button btnEnterTowerBattle;

    private final String[] teamNames = new String[3];
    private final Random random = new Random();
    private PokeApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tower_intro);

        ivTeam1 = findViewById(R.id.ivTeam1);
        ivTeam2 = findViewById(R.id.ivTeam2);
        ivTeam3 = findViewById(R.id.ivTeam3);
        tvTeam1 = findViewById(R.id.tvTeam1);
        tvTeam2 = findViewById(R.id.tvTeam2);
        tvTeam3 = findViewById(R.id.tvTeam3);
        ivTowerIntroBg = findViewById(R.id.ivTowerIntroBg);
        Button btnRerollTeam = findViewById(R.id.btnRerollTeam);
        btnEnterTowerBattle = findViewById(R.id.btnEnterTowerBattle);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(PokeApiService.class);

        // Animación de entrada de la torre
        animarTorre();

        generarEquipoAleatorio();

        btnRerollTeam.setOnClickListener(v -> generarEquipoAleatorio());

        btnEnterTowerBattle.setOnClickListener(v -> {
            if (teamNames[0] != null && teamNames[1] != null && teamNames[2] != null) {
                // Animación de entrada a la torre
                ivTowerIntroBg.animate()
                        .scaleX(1.4f)
                        .scaleY(1.4f)
                        .alpha(0f)
                        .setDuration(600)
                        .withEndAction(() -> {
                            ArrayList<String> playerTeam = new ArrayList<>();
                            playerTeam.add(teamNames[0]);
                            playerTeam.add(teamNames[1]);
                            playerTeam.add(teamNames[2]);

                            Intent intent = new Intent(TowerIntroActivity.this, TowerMapActivity.class);
                            intent.putStringArrayListExtra("playerTeam", playerTeam);
                            startActivity(intent);
                            finish();
                        })
                        .start();
            } else {
                Toast.makeText(this, "Generando equipo, espera un momento...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void animarTorre() {
        AlphaAnimation fadeIn = new AlphaAnimation(0.2f, 1.0f);
        fadeIn.setDuration(1000);
        ivTowerIntroBg.startAnimation(fadeIn);
    }

    private void generarEquipoAleatorio() {
        teamNames[0] = null;
        teamNames[1] = null;
        teamNames[2] = null;
        tvTeam1.setText("Cargando...");
        tvTeam2.setText("Cargando...");
        tvTeam3.setText("Cargando...");
        ivTeam1.setImageResource(android.R.drawable.ic_menu_help);
        ivTeam2.setImageResource(android.R.drawable.ic_menu_help);
        ivTeam3.setImageResource(android.R.drawable.ic_menu_help);
        btnEnterTowerBattle.setEnabled(false);

        // Generar 3 IDs aleatorios de Kanto (1 a 151)
        int id1 = random.nextInt(151) + 1;
        int id2 = random.nextInt(151) + 1;
        int id3 = random.nextInt(151) + 1;

        cargarPokemonSlot(String.valueOf(id1), ivTeam1, tvTeam1, 0);
        cargarPokemonSlot(String.valueOf(id2), ivTeam2, tvTeam2, 1);
        cargarPokemonSlot(String.valueOf(id3), ivTeam3, tvTeam3, 2);
    }

    private void cargarPokemonSlot(String idOrName, ImageView iv, TextView tv, int index) {
        apiService.getPokemon(idOrName).enqueue(new Callback<Pokemon>() {
            @Override
            public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Pokemon p = response.body();
                    String name = p.getName();
                    String sprite = p.getSprites().getFrontDefault();

                    teamNames[index] = name;
                    tv.setText(name.toUpperCase());
                    Glide.with(TowerIntroActivity.this).load(sprite).into(iv);

                    if (teamNames[0] != null && teamNames[1] != null && teamNames[2] != null) {
                        btnEnterTowerBattle.setEnabled(true);
                    }
                }
            }

            @Override
            public void onFailure(Call<Pokemon> call, Throwable t) {
                tv.setText("Error");
            }
        });
    }
}
