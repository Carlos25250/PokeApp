package com.example.pokeapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class FavoritesActivity extends AppCompatActivity {

    private GridLayout gridFavorites;
    private ImageView btnBack;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        sharedPreferences = getSharedPreferences("PokeFavorites", Context.MODE_PRIVATE);

        gridFavorites = findViewById(R.id.gridFavorites);
        btnBack = findViewById(R.id.btnBack);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void loadFavorites() {
        if (gridFavorites == null) return;
        gridFavorites.removeAllViews();

        Set<String> favoritesSet = sharedPreferences.getStringSet("fav_list", new HashSet<>());

        if (favoritesSet.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("No tienes Pokémon guardados en favoritos.");
            emptyText.setTextColor(Color.GRAY);
            emptyText.setTextSize(16);
            emptyText.setPadding(32, 64, 32, 32);
            emptyText.setGravity(Gravity.CENTER);
            gridFavorites.addView(emptyText);
            return;
        }

        // Colores de fondo para las tarjetas de la cuadrícula
        String[] cardColors = {"#4ADE80", "#F43F5E", "#38BDF8", "#FACC15", "#A855F7", "#FB923C"};
        int colorIndex = 0;

        for (String favJson : favoritesSet) {
            int id = 0;
            String name = "";
            String imageUrl = "";

            try {
                JSONObject obj = new JSONObject(favJson);
                id = obj.optInt("id", 0);
                name = obj.optString("name", "Desconocido");
                imageUrl = obj.optString("imageUrl", "");
            } catch (Exception e) {
                // Compatibilidad en caso de que hubiera un ID antiguo en texto
                try {
                    id = Integer.parseInt(favJson);
                    name = "Pokémon #" + id;
                    imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/" + id + ".png";
                } catch (Exception ignored) {}
            }

            if (id == 0) continue;

            final int pokemonId = id;

            // Contenedor CardView
            CardView card = new CardView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(16, 16, 16, 16);
            card.setLayoutParams(params);
            card.setRadius(24f);
            card.setCardElevation(8f);
            card.setCardBackgroundColor(Color.parseColor(cardColors[colorIndex % cardColors.length]));
            colorIndex++;

            // Layout vertical interno
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setGravity(Gravity.CENTER);
            layout.setPadding(24, 24, 24, 24);

            // Imagen del Pokémon
            ImageView img = new ImageView(this);
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(180, 180);
            img.setLayoutParams(imgParams);

            if (!imageUrl.isEmpty()) {
                Glide.with(this).load(imageUrl).into(img);
            } else {
                Glide.with(this)
                        .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/" + pokemonId + ".png")
                        .into(img);
            }

            // Nombre e ID
            TextView tvName = new TextView(this);
            tvName.setText("#" + pokemonId + " " + name);
            tvName.setTextColor(Color.WHITE);
            tvName.setTextSize(14);
            tvName.setTypeface(null, android.graphics.Typeface.BOLD);
            tvName.setPadding(0, 12, 0, 0);

            layout.addView(img);
            layout.addView(tvName);
            card.addView(layout);

            // Al hacer clic abre la pantalla principal con ese Pokémon
            card.setOnClickListener(v -> {
                Intent intent = new Intent(FavoritesActivity.this, MainActivity.class);
                intent.putExtra("POKEMON_ID", String.valueOf(pokemonId));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });

            gridFavorites.addView(card);
        }
    }
}