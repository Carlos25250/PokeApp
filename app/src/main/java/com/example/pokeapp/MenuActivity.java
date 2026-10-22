package com.example.pokeapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_FAVORITES = 101;
    private VideoView videoViewBg;
    private boolean isVideoPrepared = false;

    private ImageView ivTrainerAvatar;
    private TextView tvTrainerEmail;

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

        setContentView(R.layout.activity_menu);

        videoViewBg = findViewById(R.id.videoViewBg);
        try {
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videodefondo);
            videoViewBg.setVideoURI(videoUri);
            videoViewBg.setOnPreparedListener(mp -> {
                isVideoPrepared = true;
                mp.setLooping(true);
                mp.setVolume(0f, 0f);
                videoViewBg.start();
            });
            videoViewBg.setOnErrorListener((mp, what, extra) -> true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ivTrainerAvatar = findViewById(R.id.ivTrainerAvatar);
        tvTrainerEmail = findViewById(R.id.tvTrainerEmail);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            tvTrainerEmail.setText(user.getEmail());
            String defaultAvatarGif = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/versions/generation-v/black-white/animated/25.gif";
            Glide.with(this).asGif().load(defaultAvatarGif).into(ivTrainerAvatar);
        }

        RecyclerView rvMenu = findViewById(R.id.rvMenu);
        List<MenuItem> menuItems = new ArrayList<>();

        int alturaIdentica = 160;

        String pokeBallUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/poke-ball.png";
        String lucarioUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/448.png";
        String charizardUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/6.png";
        String pikachuUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/25.png";
        String gengarUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/94.png";
        String psyduckUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/54.png";
        String snorlaxUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/143.png";
        String eeveeUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/133.png";
        String mewtwoUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/150.png";
        String garchompUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/445.png";

        menuItems.add(new MenuItem("Pokédex Principal", "Lista completa", pokeBallUrl, "#F00F172A", "pokeball", android.R.drawable.ic_menu_search, 1, alturaIdentica));
        menuItems.add(new MenuItem("Mis Favoritos", "Guardados", lucarioUrl, "#F0581C87", "pokemon", android.R.drawable.btn_star_big_on, 1, alturaIdentica));

        menuItems.add(new MenuItem("Batalla Solo", "Entrena", charizardUrl, "#F0B45309", "pokemon", android.R.drawable.ic_menu_compass, 1, alturaIdentica));
        menuItems.add(new MenuItem("Modo Versus", "Compite", pikachuUrl, "#F01D4ED8", "pokemon", android.R.drawable.ic_menu_send, 1, alturaIdentica));

        menuItems.add(new MenuItem("Torre Desafío", "Supera niveles", gengarUrl, "#F04338CA", "pokemon", android.R.drawable.ic_menu_myplaces, 1, alturaIdentica));
        menuItems.add(new MenuItem("¿Quién es?", "Minijuego", psyduckUrl, "#F0A16207", "pokemon", android.R.drawable.ic_menu_help, 1, alturaIdentica));

        menuItems.add(new MenuItem("Zona Safari", "Captura raros", snorlaxUrl, "#F015803D", "pokemon", android.R.drawable.ic_menu_mapmode, 1, alturaIdentica));
        menuItems.add(new MenuItem("Tabla Tipos", "Efectividades", eeveeUrl, "#F0BE185D", "pokemon", android.R.drawable.ic_menu_sort_by_size, 1, alturaIdentica));

        menuItems.add(new MenuItem("Mi Perfil", "Medallas y logros", mewtwoUrl, "#F00369A1", "pokemon", android.R.drawable.ic_menu_my_calendar, 1, alturaIdentica));
        menuItems.add(new MenuItem("Cerrar Sesión", "Opciones", garchompUrl, "#F0334155", "pokemon", android.R.drawable.ic_lock_power_off, 1, alturaIdentica));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rvMenu.setLayoutManager(gridLayoutManager);

        rvMenu.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);

                outRect.left = 8;
                outRect.right = 8;
                outRect.top = 8;
                outRect.bottom = 8;

                if (position % 2 != 0) {
                    outRect.top = 35;
                    outRect.bottom = -15;
                }
            }
        });

        MenuAdapter adapter = new MenuAdapter(menuItems, item -> {
            String title = item.getTitle();

            if (title.contains("Pokédex")) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (title.contains("Favoritos")) {
                Intent intent = new Intent(MenuActivity.this, FavoritesActivity.class);
                startActivityForResult(intent, REQUEST_CODE_FAVORITES);
            } else if (title.contains("Batalla") || title.contains("Versus")) {
                Intent intent = new Intent(MenuActivity.this, BattleActivity.class);
                startActivity(intent);
            } else if (title.contains("Torre")) {
                Intent intent = new Intent(MenuActivity.this, TowerMapActivity.class);
                startActivity(intent);
            } else if (title.contains("¿Quién es?")) {
                Intent intent = new Intent(MenuActivity.this, WhosThatPokemonActivity.class);
                startActivity(intent);
            } else if (title.contains("Cerrar Sesión")) {
                cerrarSesion();
            } else {
                Toast.makeText(this, "Abriendo " + title, Toast.LENGTH_SHORT).show();
            }
        });

        rvMenu.setAdapter(adapter);
    }

    private void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoViewBg != null && isVideoPrepared && !videoViewBg.isPlaying()) {
            videoViewBg.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoViewBg != null && videoViewBg.isPlaying()) {
            videoViewBg.pause();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FAVORITES && resultCode == RESULT_OK && data != null) {
            String selectedPokemonId = data.getStringExtra("selectedPokemonId");
            if (selectedPokemonId != null) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                intent.putExtra("selectedPokemonId", selectedPokemonId);
                startActivity(intent);
            }
        }
    }
}