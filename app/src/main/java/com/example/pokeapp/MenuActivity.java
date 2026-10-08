package com.example.pokeapp;

import android.content.Intent;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_FAVORITES = 101;
    private VideoView videoViewBg;
    private boolean isVideoPrepared = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        videoViewBg = findViewById(R.id.videoViewBg);

        try {
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videodefondo);
            videoViewBg.setVideoURI(videoUri);

            videoViewBg.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    isVideoPrepared = true;
                    mp.setLooping(true);
                    mp.setVolume(0f, 0f);
                    videoViewBg.start();
                }
            });

            videoViewBg.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return true;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        RecyclerView rvMenu = findViewById(R.id.rvMenu);
        List<MenuItem> menuItems = new ArrayList<>();

        // Altura extendida para abarcar la pantalla
        int alturaIdentica = 110;

        menuItems.add(new MenuItem("Pokédex Principal", "Consulta la lista e información", android.R.drawable.ic_menu_agenda, "#CC1E293B", 1, alturaIdentica));
        menuItems.add(new MenuItem("Mis Favoritos", "Tus Pokémon guardados", android.R.drawable.btn_star_big_on, "#CC581C87", 1, alturaIdentica));

        menuItems.add(new MenuItem("Batalla Solo", "Entrena tus habilidades", android.R.drawable.ic_menu_compass, "#CCB45309", 1, alturaIdentica));
        menuItems.add(new MenuItem("Modo Versus", "Compite contra otros", android.R.drawable.ic_menu_myplaces, "#CC1D4ED8", 1, alturaIdentica));

        menuItems.add(new MenuItem("Torre Desafío", "Supera niveles", android.R.drawable.ic_menu_day, "#CC4338CA", 1, alturaIdentica));
        menuItems.add(new MenuItem("¿Quién es?", "Minijuego de preguntas", android.R.drawable.ic_menu_help, "#CCA16207", 1, alturaIdentica));

        menuItems.add(new MenuItem("Zona Safari", "Captura raros", android.R.drawable.ic_menu_camera, "#CC15803D", 1, alturaIdentica));
        menuItems.add(new MenuItem("Tabla Tipos", "Efectividades", android.R.drawable.ic_menu_gallery, "#CCBE185D", 1, alturaIdentica));

        menuItems.add(new MenuItem("Mi Perfil", "Medallas y logros", android.R.drawable.ic_menu_manage, "#CC0369A1", 1, alturaIdentica));
        menuItems.add(new MenuItem("Cerrar Sesión", "Opciones de la app", android.R.drawable.ic_menu_preferences, "#CC334155", 1, alturaIdentica));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rvMenu.setLayoutManager(gridLayoutManager);

        // ItemDecoration con separación ligera y limpia entre hexágonos
        rvMenu.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);

                // Separación lateral justa
                outRect.left = -10;
                outRect.right = -10;

                // Ligera separación vertical (márgenes positivos leves)
                outRect.top = 6;
                outRect.bottom = 6;

                // Desfase para embonar la columna derecha conservando la pequeña distancia
                if (position % 2 != 0) {
                    outRect.top = 55;
                    outRect.bottom = -43;
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