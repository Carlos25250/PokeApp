package com.example.pokeapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private EditText etSearch;
    private Button btnSearch, btnPlaySound, btnNavMenu, btnNavFavorites;
    private ImageView ivPokemon;
    private TextView tvPokemonName, tvRarity, tvDescription, tvHp, tvAttack, tvDefense, tvSpAttack, tvSpDefense, tvSpeed;
    private ProgressBar pbHp, pbAttack, pbDefense, pbSpAttack, pbSpDefense, pbSpeed;
    private ImageView btnFavorite;
    private LinearLayout layoutEvolutions;

    private String audioUrl = "";
    private String defaultImageUrl = "";
    private String shinyImageUrl = "";
    private String currentPokemonName = "";
    private boolean isShinyShowing = false;
    private boolean isFavorite = false;
    private MediaPlayer mediaPlayer;

    private int currentPokemonId = 1;
    private GestureDetector gestureDetector;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("PokeFavorites", Context.MODE_PRIVATE);

        // Inicializar vistas
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        ivPokemon = findViewById(R.id.ivPokemon);
        tvPokemonName = findViewById(R.id.tvPokemonName);
        tvRarity = findViewById(R.id.tvRarity);
        tvDescription = findViewById(R.id.tvDescription);
        btnPlaySound = findViewById(R.id.btnPlaySound);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnNavMenu = findViewById(R.id.btnNavMenu);
        btnNavFavorites = findViewById(R.id.btnNavFavorites);
        layoutEvolutions = findViewById(R.id.layoutEvolutions);

        // Estadísticas
        tvHp = findViewById(R.id.tvHp);
        tvAttack = findViewById(R.id.tvAttack);
        tvDefense = findViewById(R.id.tvDefense);
        tvSpAttack = findViewById(R.id.tvSpAttack);
        tvSpDefense = findViewById(R.id.tvSpDefense);
        tvSpeed = findViewById(R.id.tvSpeed);

        pbHp = findViewById(R.id.pbHp);
        pbAttack = findViewById(R.id.pbAttack);
        pbDefense = findViewById(R.id.pbDefense);
        pbSpAttack = findViewById(R.id.pbSpAttack);
        pbSpDefense = findViewById(R.id.pbSpDefense);
        pbSpeed = findViewById(R.id.pbSpeed);

        // Configurar detección de gestos (Swipe)
        setupSwipeGesture();

        // Clic en Favoritos
        btnFavorite.setOnClickListener(v -> toggleFavorite());

        // Navegación
        if (btnNavMenu != null) {
            btnNavMenu.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, MenuActivity.class));
                finish();
            });
        }

        if (btnNavFavorites != null) {
            btnNavFavorites.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
            });
        }

        // Buscar Pokémon
        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim().toLowerCase();
            if (!query.isEmpty()) {
                fetchPokemonData(query);
            } else {
                Toast.makeText(MainActivity.this, "Escribe un nombre o ID", Toast.LENGTH_SHORT).show();
            }
        });

        // Reproducir sonido
        btnPlaySound.setOnClickListener(v -> playPokemonSound());

        // Cargar Bulbasaur por defecto (#1)
        fetchPokemonData("1");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Verificar favoritos por si se eliminó alguno desde FavoritesActivity
        checkFavoriteStatus();
    }

    private void setupSwipeGesture() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                toggleShinyVariant();
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null || e2 == null) return false;
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();

                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX < 0) {
                            fetchPokemonData(String.valueOf(currentPokemonId + 1));
                        } else {
                            if (currentPokemonId > 1) {
                                fetchPokemonData(String.valueOf(currentPokemonId - 1));
                            } else {
                                Toast.makeText(MainActivity.this, "Este es el primer Pokémon", Toast.LENGTH_SHORT).show();
                            }
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        ivPokemon.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }

    private void fetchPokemonData(String pokemonQuery) {
        new Thread(() -> {
            try {
                URL url = new URL("https://pokeapi.co/api/v2/pokemon/" + pokemonQuery);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) builder.append(line);
                    reader.close();

                    JSONObject json = new JSONObject(builder.toString());

                    currentPokemonId = json.getInt("id");
                    String name = json.getString("name");
                    currentPokemonName = name.substring(0, 1).toUpperCase() + name.substring(1);

                    JSONObject officialArtwork = json.getJSONObject("sprites")
                            .getJSONObject("other")
                            .getJSONObject("official-artwork");

                    defaultImageUrl = officialArtwork.optString("front_default", "");
                    shinyImageUrl = officialArtwork.optString("front_shiny", "");
                    isShinyShowing = false;

                    audioUrl = json.getJSONObject("cries").optString("latest", "");

                    JSONArray stats = json.getJSONArray("stats");
                    int hp = stats.getJSONObject(0).getInt("base_stat");
                    int attack = stats.getJSONObject(1).getInt("base_stat");
                    int defense = stats.getJSONObject(2).getInt("base_stat");
                    int spAttack = stats.getJSONObject(3).getInt("base_stat");
                    int spDefense = stats.getJSONObject(4).getInt("base_stat");
                    int speed = stats.getJSONObject(5).getInt("base_stat");

                    JSONObject speciesData = fetchSpeciesData(currentPokemonId);
                    String description = speciesData.optString("description", "Sin descripción.");
                    String evoChainUrl = speciesData.optString("evoChainUrl", "");

                    List<PokemonEvolutionNode> evolutions = new ArrayList<>();
                    if (!evoChainUrl.isEmpty()) {
                        evolutions = fetchEvolutionChain(evoChainUrl);
                    }

                    List<PokemonEvolutionNode> finalEvolutions = evolutions;
                    runOnUiThread(() -> {
                        tvPokemonName.setText("#" + String.format("%03d", currentPokemonId) + " " + currentPokemonName);

                        Glide.with(MainActivity.this).load(defaultImageUrl).into(ivPokemon);

                        tvHp.setText(String.valueOf(hp));
                        pbHp.setProgress(hp);

                        tvAttack.setText(String.valueOf(attack));
                        pbAttack.setProgress(attack);

                        tvDefense.setText(String.valueOf(defense));
                        pbDefense.setProgress(defense);

                        tvSpAttack.setText(String.valueOf(spAttack));
                        pbSpAttack.setProgress(spAttack);

                        tvSpDefense.setText(String.valueOf(spDefense));
                        pbSpDefense.setProgress(spDefense);

                        tvSpeed.setText(String.valueOf(speed));
                        pbSpeed.setProgress(speed);

                        tvDescription.setText(description);

                        // Comprobar e iluminar la estrella si ya es favorito
                        checkFavoriteStatus();

                        // Renderizar cadena evolutiva
                        renderEvolutions(finalEvolutions);
                    });

                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Pokémon no encontrado", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void checkFavoriteStatus() {
        Set<String> favorites = sharedPreferences.getStringSet("fav_list", new HashSet<>());
        isFavorite = false;

        for (String favItem : favorites) {
            try {
                JSONObject obj = new JSONObject(favItem);
                if (obj.optInt("id") == currentPokemonId) {
                    isFavorite = true;
                    break;
                }
            } catch (Exception e) {
                if (favItem.equals(String.valueOf(currentPokemonId))) {
                    isFavorite = true;
                    break;
                }
            }
        }
        updateFavoriteIcon();
    }

    private void toggleFavorite() {
        Set<String> favorites = new HashSet<>(sharedPreferences.getStringSet("fav_list", new HashSet<>()));
        String targetToRemove = null;

        for (String favItem : favorites) {
            try {
                JSONObject obj = new JSONObject(favItem);
                if (obj.optInt("id") == currentPokemonId) {
                    targetToRemove = favItem;
                    break;
                }
            } catch (Exception e) {
                if (favItem.equals(String.valueOf(currentPokemonId))) {
                    targetToRemove = favItem;
                    break;
                }
            }
        }

        if (isFavorite && targetToRemove != null) {
            favorites.remove(targetToRemove);
            isFavorite = false;
            Toast.makeText(this, currentPokemonName + " quitado de Favoritos", Toast.LENGTH_SHORT).show();
        } else {
            try {
                JSONObject newFav = new JSONObject();
                newFav.put("id", currentPokemonId);
                newFav.put("name", currentPokemonName);
                newFav.put("imageUrl", defaultImageUrl);

                favorites.add(newFav.toString());
                isFavorite = true;
                Toast.makeText(this, "★ " + currentPokemonName + " añadido a Favoritos!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        sharedPreferences.edit().putStringSet("fav_list", favorites).apply();
        updateFavoriteIcon();
    }

    private void updateFavoriteIcon() {
        if (isFavorite) {
            btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            btnFavorite.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }

    private JSONObject fetchSpeciesData(int pokemonId) {
        JSONObject result = new JSONObject();
        try {
            URL url = new URL("https://pokeapi.co/api/v2/pokemon-species/" + pokemonId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) builder.append(line);
                reader.close();

                JSONObject json = new JSONObject(builder.toString());

                JSONArray flavorEntries = json.getJSONArray("flavor_text_entries");
                String desc = "Descripción no disponible en español.";
                for (int i = 0; i < flavorEntries.length(); i++) {
                    JSONObject entry = flavorEntries.getJSONObject(i);
                    if (entry.getJSONObject("language").getString("name").equals("es")) {
                        desc = entry.getString("flavor_text").replace("\n", " ").replace("\r", " ");
                        break;
                    }
                }
                result.put("description", desc);
                result.put("evoChainUrl", json.getJSONObject("evolution_chain").getString("url"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<PokemonEvolutionNode> fetchEvolutionChain(String chainUrl) {
        List<PokemonEvolutionNode> list = new ArrayList<>();
        try {
            URL url = new URL(chainUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) builder.append(line);
                reader.close();

                JSONObject json = new JSONObject(builder.toString());
                JSONObject chain = json.getJSONObject("chain");

                parseChainNode(chain, list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private void parseChainNode(JSONObject node, List<PokemonEvolutionNode> list) throws Exception {
        JSONObject species = node.getJSONObject("species");
        String name = species.getString("name");
        String url = species.getString("url");
        String[] parts = url.split("/");
        String id = parts[parts.length - 1];

        String imgUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/" + id + ".png";
        list.add(new PokemonEvolutionNode(name, id, imgUrl));

        JSONArray evolvesTo = node.getJSONArray("evolves_to");
        if (evolvesTo.length() > 0) {
            parseChainNode(evolvesTo.getJSONObject(0), list);
        }
    }

    private void renderEvolutions(List<PokemonEvolutionNode> evolutions) {
        if (layoutEvolutions == null) return;
        layoutEvolutions.removeAllViews();

        for (int i = 0; i < evolutions.size(); i++) {
            PokemonEvolutionNode evo = evolutions.get(i);

            LinearLayout evoContainer = new LinearLayout(this);
            evoContainer.setOrientation(LinearLayout.VERTICAL);
            evoContainer.setGravity(Gravity.CENTER);
            evoContainer.setPadding(8, 8, 8, 8);

            ImageView img = new ImageView(this);
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(130, 130);
            img.setLayoutParams(imgParams);
            Glide.with(this).load(evo.imageUrl).into(img);

            TextView txt = new TextView(this);
            txt.setText(evo.name.substring(0, 1).toUpperCase() + evo.name.substring(1));
            txt.setTextColor(Color.WHITE);
            txt.setTextSize(11);
            txt.setGravity(Gravity.CENTER);

            evoContainer.addView(img);
            evoContainer.addView(txt);

            evoContainer.setOnClickListener(v -> fetchPokemonData(evo.id));

            layoutEvolutions.addView(evoContainer);

            if (i < evolutions.size() - 1) {
                TextView arrow = new TextView(this);
                arrow.setText(" > ");
                arrow.setTextColor(Color.parseColor("#38BDF8"));
                arrow.setTextSize(22);
                arrow.setTypeface(null, Typeface.BOLD);
                arrow.setGravity(Gravity.CENTER);
                layoutEvolutions.addView(arrow);
            }
        }
    }

    private void toggleShinyVariant() {
        if (shinyImageUrl.isEmpty()) {
            Toast.makeText(this, "Variante Shiny no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isShinyShowing) {
            Glide.with(this).load(defaultImageUrl).into(ivPokemon);
            Toast.makeText(this, "Versión Normal", Toast.LENGTH_SHORT).show();
            isShinyShowing = false;
        } else {
            Glide.with(this).load(shinyImageUrl).into(ivPokemon);
            Toast.makeText(this, "★ Variante Shiny!", Toast.LENGTH_SHORT).show();
            isShinyShowing = true;
        }
    }

    private void playPokemonSound() {
        if (audioUrl.isEmpty()) {
            Toast.makeText(this, "Sonido no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (mediaPlayer != null) mediaPlayer.release();

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al reproducir audio", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private static class PokemonEvolutionNode {
        String name;
        String id;
        String imageUrl;

        PokemonEvolutionNode(String name, String id, String imageUrl) {
            this.name = name;
            this.id = id;
            this.imageUrl = imageUrl;
        }
    }
}