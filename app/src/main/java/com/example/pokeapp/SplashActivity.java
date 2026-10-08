package com.example.pokeapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private VideoView videoViewSplash;
    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        videoViewSplash = findViewById(R.id.videoViewSplash);
        btnStart = findViewById(R.id.btnStart);

        // Ruta del video en res/raw/splash_video.mp4
        int videoResId = getResources().getIdentifier("splash_video", "raw", getPackageName());
        if (videoResId != 0) {
            String videoPath = "android.resource://" + getPackageName() + "/" + videoResId;
            Uri uri = Uri.parse(videoPath);
            videoViewSplash.setVideoURI(uri);

            // Repetir el video en bucle
            videoViewSplash.setOnPreparedListener(mp -> {
                mp.setLooping(true);
                videoViewSplash.start();
            });
        }

        // Evento al presionar el botón de Ingresar
        btnStart.setOnClickListener(v -> navigateToMain());
    }

    private void navigateToMain() {
        if (videoViewSplash.isPlaying()) {
            videoViewSplash.stopPlayback();
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // Cierra la pantalla de splash para no volver al presionar 'Atrás'
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoViewSplash != null && !videoViewSplash.isPlaying()) {
            videoViewSplash.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoViewSplash != null && videoViewSplash.isPlaying()) {
            videoViewSplash.pause();
        }
    }
}