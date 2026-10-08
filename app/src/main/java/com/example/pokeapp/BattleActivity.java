package com.example.pokeapp;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.Random;

public class BattleActivity extends AppCompatActivity {

    private EditText etP1, etP2;
    private Button btnStartBattle, btnBattleHistory;
    private ImageView ivP1Sprite, ivP2Sprite;
    private TextView tvP1Name, tvP2Name, tvP1Hp, tvP2Hp, tvBattleLog;
    private ProgressBar pbP1Hp, pbP2Hp;
    private ScrollView svLog;

    private int hpP1 = 100, maxHpP1 = 100;
    private int hpP2 = 100, maxHpP2 = 100;
    private boolean isBattleRunning = false;
    private ToneGenerator toneGenerator;

    private String nameP1 = "Tu Pokémon", nameP2 = "Rival";
    private SharedPreferences historyPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        historyPrefs = getSharedPreferences("BattleHistory", MODE_PRIVATE);

        try {
            toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 80);
        } catch (Exception e) {
            e.printStackTrace();
        }

        etP1 = findViewById(R.id.etP1);
        etP2 = findViewById(R.id.etP2);
        btnStartBattle = findViewById(R.id.btnStartBattle);
        btnBattleHistory = findViewById(R.id.btnBattleHistory);

        ivP1Sprite = findViewById(R.id.ivP1Sprite);
        ivP2Sprite = findViewById(R.id.ivP2Sprite);

        tvP1Name = findViewById(R.id.tvP1Name);
        tvP2Name = findViewById(R.id.tvP2Name);
        tvP1Hp = findViewById(R.id.tvP1Hp);
        tvP2Hp = findViewById(R.id.tvP2Hp);
        tvBattleLog = findViewById(R.id.tvBattleLog);

        pbP1Hp = findViewById(R.id.pbP1Hp);
        pbP2Hp = findViewById(R.id.pbP2Hp);
        svLog = findViewById(R.id.svLog);

        btnStartBattle.setOnClickListener(v -> {
            if (isBattleRunning) return;

            String input1 = etP1.getText().toString().trim().toLowerCase();
            String input2 = etP2.getText().toString().trim().toLowerCase();

            if (input1.isEmpty() || input2.isEmpty()) {
                Toast.makeText(this, "Escribe el nombre de ambos Pokémon", Toast.LENGTH_SHORT).show();
                return;
            }

            prepararBatalla(input1, input2);
        });

        btnBattleHistory.setOnClickListener(v -> mostrarHistorial());
    }

    private void prepararBatalla(String input1, String input2) {
        isBattleRunning = true;
        nameP1 = capitalize(input1);
        nameP2 = capitalize(input2);

        tvP1Name.setText(nameP1);
        tvP2Name.setText(nameP2);

        String urlGifP1 = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/back/" + input1 + ".gif";
        String urlGifP2 = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/" + input2 + ".gif";

        Glide.with(this).asGif().load(urlGifP1).placeholder(android.R.drawable.ic_menu_help).into(ivP1Sprite);
        Glide.with(this).asGif().load(urlGifP2).placeholder(android.R.drawable.ic_menu_help).into(ivP2Sprite);

        hpP1 = 100;
        hpP2 = 100;
        actualizarHP();

        tvBattleLog.setText("¡Comienza el combate entre " + nameP1 + " y " + nameP2 + "!\n");
        reproducirSonido(ToneGenerator.TONE_PROP_BEEP);

        ejecutarTurno(true);
    }

    private void ejecutarTurno(boolean turnoP1) {
        if (hpP1 <= 0 || hpP2 <= 0) {
            isBattleRunning = false;
            String ganador = (hpP1 > 0) ? nameP1 : nameP2;

            if (hpP1 > 0) {
                appendLog("\n🏆 ¡" + nameP1 + " ha ganado el combate!");
                reproducirSonido(ToneGenerator.TONE_SUP_RINGTONE);
            } else {
                appendLog("\n🏆 ¡" + nameP2 + " ha ganado el combate!");
                reproducirSonido(ToneGenerator.TONE_CDMA_CALLDROP_LITE);
            }

            guardarEnHistorial(nameP1 + " VS " + nameP2 + " ➔ Ganador: " + ganador);
            return;
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Random r = new Random();
            int daño = r.nextInt(20) + 10;
            String[] ataques = {"Placaje", "Ataque Rápido", "Rayo Solar", "Llamarada", "Hidrobomba", "Impactrueno"};
            String ataqueElegido = ataques[r.nextInt(ataques.length)];

            if (turnoP1) {
                appendLog("⚔️ " + nameP1 + " usó " + ataqueElegido + " e infligió " + daño + " de daño.");
                hpP2 = Math.max(0, hpP2 - daño);
                reproducirSonido(ToneGenerator.TONE_CDMA_HIGH_L);
                animarAtaque(ivP1Sprite, ivP2Sprite, true);
            } else {
                appendLog("💥 " + nameP2 + " respondió con " + ataqueElegido + " haciendo " + daño + " de daño.");
                hpP1 = Math.max(0, hpP1 - daño);
                reproducirSonido(ToneGenerator.TONE_CDMA_LOW_L);
                animarAtaque(ivP2Sprite, ivP1Sprite, false);
            }

            actualizarHP();
            ejecutarTurno(!turnoP1);
        }, 1600);
    }

    private void guardarEnHistorial(String resultado) {
        String previo = historyPrefs.getString("records", "");
        String nuevoHistorial = "• " + resultado + "\n" + previo;
        historyPrefs.edit().putString("records", nuevoHistorial).apply();
    }

    private void mostrarHistorial() {
        String historial = historyPrefs.getString("records", "Aún no hay combates registrados.");
        new AlertDialog.Builder(this)
                .setTitle("📜 Registro de Batallas")
                .setMessage(historial)
                .setPositiveButton("Cerrar", null)
                .setNeutralButton("Limpiar", (dialog, which) -> {
                    historyPrefs.edit().clear().apply();
                    Toast.makeText(this, "Historial borrado", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void animarAtaque(ImageView atacante, ImageView objetivo, boolean esP1) {
        float deltaX = esP1 ? 35f : -35f;
        float deltaY = esP1 ? -35f : 35f;

        TranslateAnimation anim = new TranslateAnimation(0, deltaX, 0, deltaY);
        anim.setDuration(120);
        anim.setRepeatCount(1);
        anim.setRepeatMode(Animation.REVERSE);
        atacante.startAnimation(anim);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            objetivo.setAlpha(0.2f);
            new Handler(Looper.getMainLooper()).postDelayed(() -> objetivo.setAlpha(1.0f), 100);
        }, 120);
    }

    private void actualizarHP() {
        pbP1Hp.setProgress(hpP1);
        tvP1Hp.setText(hpP1 + "/" + maxHpP1 + " HP");

        pbP2Hp.setProgress(hpP2);
        tvP2Hp.setText(hpP2 + "/" + maxHpP2 + " HP");
    }

    private void reproducirSonido(int toneType) {
        if (toneGenerator != null) {
            try {
                toneGenerator.startTone(toneType, 150);
            } catch (Exception ignored) {}
        }
    }

    private void appendLog(String msg) {
        tvBattleLog.append(msg + "\n");
        svLog.post(() -> svLog.fullScroll(ScrollView.FOCUS_DOWN));
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (toneGenerator != null) {
            toneGenerator.release();
        }
    }
}