package com.example.pokeapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView rvFavorites;
    private TextView tvEmptyFavorites;
    private ImageView btnBack;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        sharedPreferences = getSharedPreferences("PokeFavorites", Context.MODE_PRIVATE);

        rvFavorites = findViewById(R.id.rvFavorites);
        tvEmptyFavorites = findViewById(R.id.tvEmptyFavorites);
        btnBack = findViewById(R.id.btnBack);

        rvFavorites.setLayoutManager(new GridLayoutManager(this, 2));

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
        Set<String> favoritesSet = sharedPreferences.getStringSet("fav_list", new HashSet<>());
        List<FavoritesAdapter.FavoriteItem> favoriteList = new ArrayList<>();

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
                try {
                    id = Integer.parseInt(favJson);
                    name = "Pokémon #" + id;
                    imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/" + id + ".png";
                } catch (Exception ignored) {}
            }

            if (id != 0) {
                favoriteList.add(new FavoritesAdapter.FavoriteItem(String.valueOf(id), name, imageUrl));
            }
        }

        if (favoriteList.isEmpty()) {
            rvFavorites.setVisibility(View.GONE);
            tvEmptyFavorites.setVisibility(View.VISIBLE);
        } else {
            rvFavorites.setVisibility(View.VISIBLE);
            tvEmptyFavorites.setVisibility(View.GONE);

            FavoritesAdapter adapter = new FavoritesAdapter(this, favoriteList, pokemonId -> {
                Intent intent = new Intent(FavoritesActivity.this, MainActivity.class);
                intent.putExtra("POKEMON_ID", pokemonId);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
            rvFavorites.setAdapter(adapter);
        }
    }
}
