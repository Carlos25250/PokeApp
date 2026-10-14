package com.example.pokeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.pokeapp.api.PokeApiService;
import com.example.pokeapp.model.Pokemon;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TowerSelectActivity extends AppCompatActivity {

    private EditText etSearch;
    private Button btnSearch, btnAddTeam, btnConfirmTower;
    private ImageView ivPokemon, ivSlot1, ivSlot2, ivSlot3;
    private TextView tvName, tvTeamStatus, tvSlot1, tvSlot2, tvSlot3;
    private Pokemon pokemonSeleccionado;
    private final ArrayList<String> equipoPokemones = new ArrayList<>();
    private final ArrayList<String> equipoSprites = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tower_select);

        etSearch = findViewById(R.id.etTowerSearch);
        btnSearch = findViewById(R.id.btnTowerSearch);
        btnAddTeam = findViewById(R.id.btnAddTeam);
        btnConfirmTower = findViewById(R.id.btnConfirmTower);
        ivPokemon = findViewById(R.id.ivTowerPokemon);
        tvName = findViewById(R.id.tvTowerPokemonName);
        tvTeamStatus = findViewById(R.id.tvTeamStatus);

        ivSlot1 = findViewById(R.id.ivSlot1);
        ivSlot2 = findViewById(R.id.ivSlot2);
        ivSlot3 = findViewById(R.id.ivSlot3);
        tvSlot1 = findViewById(R.id.tvSlot1);
        tvSlot2 = findViewById(R.id.tvSlot2);
        tvSlot3 = findViewById(R.id.tvSlot3);

        btnSearch.setOnClickListener(v -> buscarPokemon());

        btnAddTeam.setOnClickListener(v -> {
            if (pokemonSeleccionado != null && equipoPokemones.size() < 3) {
                String name = pokemonSeleccionado.getName();
                String sprite = pokemonSeleccionado.getSprites().getFrontDefault();

                equipoPokemones.add(name);
                equipoSprites.add(sprite);

                actualizarSlotsEquipo();
                animarVista(btnAddTeam);

                Toast.makeText(this, "¡" + name.toUpperCase() + " añadido al equipo!", Toast.LENGTH_SHORT).show();
                btnAddTeam.setEnabled(false);
                etSearch.setText("");
                ivPokemon.setImageResource(android.R.drawable.ic_menu_help);
                tvName.setText("Busca un Pokémon");

                if (equipoPokemones.size() == 3) {
                    btnConfirmTower.setEnabled(true);
                    Toast.makeText(this, "¡Equipo completo! Ya puedes entrar a la torre.", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnConfirmTower.setOnClickListener(v -> {
            if (equipoPokemones.size() == 3) {
                Intent intent = new Intent(this, TowerActivity.class);
                intent.putStringArrayListExtra("playerTeam", equipoPokemones);
                intent.putExtra("floorNumber", 1);
                startActivity(intent);
                finish();
            }
        });
    }

    private void actualizarSlotsEquipo() {
        tvTeamStatus.setText("Equipo: " + equipoPokemones.size() + " / 3 seleccionados");

        if (equipoPokemones.size() >= 1) {
            Glide.with(this).load(equipoSprites.get(0)).into(ivSlot1);
            tvSlot1.setText(equipoPokemones.get(0).toUpperCase());
            animarVista(ivSlot1);
        }
        if (equipoPokemones.size() >= 2) {
            Glide.with(this).load(equipoSprites.get(1)).into(ivSlot2);
            tvSlot2.setText(equipoPokemones.get(1).toUpperCase());
            animarVista(ivSlot2);
        }
        if (equipoPokemones.size() >= 3) {
            Glide.with(this).load(equipoSprites.get(2)).into(ivSlot3);
            tvSlot3.setText(equipoPokemones.get(2).toUpperCase());
            animarVista(ivSlot3);
        }
    }

    private void buscarPokemon() {
        String query = etSearch.getText().toString().trim().toLowerCase();
        if (query.isEmpty()) {
            Toast.makeText(this, "Escribe un nombre o ID", Toast.LENGTH_SHORT).show();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PokeApiService service = retrofit.create(PokeApiService.class);
        service.getPokemon(query).enqueue(new Callback<Pokemon>() {
            @Override
            public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pokemonSeleccionado = response.body();
                    tvName.setText(pokemonSeleccionado.getName().toUpperCase());

                    String urlImagen = pokemonSeleccionado.getSprites().getFrontDefault();
                    Glide.with(TowerSelectActivity.this).load(urlImagen).into(ivPokemon);
                    animarVista(ivPokemon);

                    btnAddTeam.setEnabled(true);
                } else {
                    Toast.makeText(TowerSelectActivity.this, "Pokémon no encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Pokemon> call, Throwable t) {
                Toast.makeText(TowerSelectActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void animarVista(View view) {
        ScaleAnimation scale = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(400);
        view.startAnimation(scale);
    }
}
