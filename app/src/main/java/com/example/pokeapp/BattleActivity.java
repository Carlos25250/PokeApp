package com.example.pokeapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleActivity extends AppCompatActivity {

    private ImageView ivBattleBg, ivPlayerSprite, ivEnemySprite;
    private TextView tvFloorTitle, tvBossNameHeader, tvPlayerName, tvEnemyName, tvBattleLog, tvHistoryContent, tvHistoryTitle;
    private ProgressBar pbPlayerHp, pbEnemyHp;
    private Button btnStartBattle;
    private CardView cardHistoryBox;

    private List<Integer> playerTeam;
    private int currentPokemonIndex = 0;
    private int floorNumber = 1;
    private String bossName = "Jefe";

    private int playerHp = 100;
    private int enemyHp = 100;

    private Handler autoBattleHandler = new Handler(Looper.getMainLooper());
    private boolean isBattleRunning = false;
    private boolean isBattleFinished = false;
    private boolean hasWon = false;
    private SharedPreferences sharedPreferences;

    // Lista de jefes por piso
    private final String[] floorBosses = {
            "Angélica", "Maestro Brock", "Teniente Surge", "Erika", "Koga", "Sabrina", "Blaine", "Giovanni"
    };

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

        setContentView(R.layout.activity_battle);

        sharedPreferences = getSharedPreferences("PokeBattleHistory", Context.MODE_PRIVATE);

        ivBattleBg = findViewById(R.id.ivBattleBg);
        ivPlayerSprite = findViewById(R.id.ivPlayerSprite);
        ivEnemySprite = findViewById(R.id.ivEnemySprite);
        tvFloorTitle = findViewById(R.id.tvFloorTitle);
        tvBossNameHeader = findViewById(R.id.tvBossNameHeader);
        tvPlayerName = findViewById(R.id.tvPlayerName);
        tvEnemyName = findViewById(R.id.tvEnemyName);
        tvBattleLog = findViewById(R.id.tvBattleLog);
        pbPlayerHp = findViewById(R.id.pbPlayerHp);
        pbEnemyHp = findViewById(R.id.pbEnemyHp);
        btnStartBattle = findViewById(R.id.btnStartBattle);

        cardHistoryBox = findViewById(R.id.cardHistoryBox);
        tvHistoryTitle = findViewById(R.id.tvHistoryTitle);
        tvHistoryContent = findViewById(R.id.tvHistoryContent);

        floorNumber = getIntent().getIntExtra("FLOOR_NUMBER", 1);
        bossName = getIntent().getStringExtra("BOSS_NAME");
        if (bossName == null || bossName.isEmpty()) {
            bossName = getBossForFloor(floorNumber);
        }

        playerTeam = getIntent().getIntegerArrayListExtra("SELECTED_TEAM");
        if (playerTeam == null || playerTeam.isEmpty()) {
            playerTeam = new ArrayList<>();
            Random random = new Random();
            for (int i = 0; i < 6; i++) {
                playerTeam.add(random.nextInt(151) + 1);
            }
        }

        initFloorState();

        // CONTROL DE BOTÓN MULTIFUNCIÓN (ATACAR / SIGUIENTE PISO / REINTENTAR)
        btnStartBattle.setOnClickListener(v -> {
            if (isBattleFinished) {
                if (hasWon) {
                    // Cargar el siguiente piso
                    floorNumber++;
                    bossName = getBossForFloor(floorNumber);
                    initFloorState();
                } else {
                    // Reintentar el piso actual
                    initFloorState();
                }
            } else if (!isBattleRunning) {
                isBattleRunning = true;
                btnStartBattle.setEnabled(false);
                btnStartBattle.setText("EN COMBATE...");
                startAutoBattle();
            }
        });

        // RECUADRO INTERACTIVO DE HISTORIAL
        cardHistoryBox.setOnClickListener(v -> {
            if (tvHistoryContent.getVisibility() == View.GONE) {
                String history = sharedPreferences.getString("history_log", "No hay batallas registradas aún.");
                tvHistoryContent.setText(history);
                tvHistoryContent.setVisibility(View.VISIBLE);
                tvHistoryTitle.setText("📜 OCULTAR HISTORIAL DE BATALLAS");
            } else {
                tvHistoryContent.setVisibility(View.GONE);
                tvHistoryTitle.setText("📜 VER HISTORIAL DE BATALLAS GUARDADAS");
            }
        });
    }

    private String getBossForFloor(int floor) {
        if (floor >= 1 && floor <= floorBosses.length) {
            return floorBosses[floor - 1];
        }
        return "Jefe Maestro #" + floor;
    }

    private void initFloorState() {
        isBattleRunning = false;
        isBattleFinished = false;
        hasWon = false;
        currentPokemonIndex = 0;

        tvBattleLog.setText("¡Piso " + floorNumber + " iniciado! Presiona '¡A ATACAR!' para combatir.");

        btnStartBattle.setEnabled(true);
        btnStartBattle.setText("¡A ATACAR!");
        btnStartBattle.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#DC2626")));

        tvFloorTitle.setText("TORRE DE COMBATE • PISO " + floorNumber);
        tvBossNameHeader.setText("Jefe: " + bossName);

        setupFloorBackground(floorNumber);
        loadCurrentPlayerPokemon();
        loadBossPokemon();
    }

    private void setupFloorBackground(int floor) {
        int bgResId;
        switch ((floor - 1) % 8 + 1) {
            case 2: bgResId = R.drawable.piso2; break;
            case 3: bgResId = R.drawable.piso3; break;
            case 4: bgResId = R.drawable.piso4; break;
            case 5: bgResId = R.drawable.piso5; break;
            case 6: bgResId = R.drawable.piso6; break;
            case 7: bgResId = R.drawable.piso7; break;
            case 8: bgResId = R.drawable.piso8; break;
            case 1:
            default: bgResId = R.drawable.piso1; break;
        }

        if (ivBattleBg != null) {
            ivBattleBg.setImageResource(bgResId);
        }
    }

    private void loadCurrentPlayerPokemon() {
        if (currentPokemonIndex < playerTeam.size()) {
            int pokemonId = playerTeam.get(currentPokemonIndex);
            tvPlayerName.setText("Pokémon #" + pokemonId);

            String spriteUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/versions/generation-v/black-white/animated/back/" + pokemonId + ".gif";
            Glide.with(this)
                    .asGif()
                    .load(spriteUrl)
                    .placeholder(android.R.drawable.ic_menu_compass)
                    .into(ivPlayerSprite);

            playerHp = 100;
            pbPlayerHp.setProgress(playerHp);
            appendLog("¡Entra al combate tu Pokémon #" + pokemonId + "!");
        }
    }

    private void loadBossPokemon() {
        int bossPokemonId = 68; // Machamp
        int floorMod = (floorNumber - 1) % 6;
        if (floorMod == 1) bossPokemonId = 94;       // Gengar
        else if (floorMod == 2) bossPokemonId = 149; // Dragonite
        else if (floorMod == 3) bossPokemonId = 59;  // Arcanine
        else if (floorMod == 4) bossPokemonId = 65;  // Alakazam
        else if (floorMod == 5) bossPokemonId = 150; // Mewtwo

        tvEnemyName.setText("Jefe " + bossName);

        String bossSpriteUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/versions/generation-v/black-white/animated/" + bossPokemonId + ".gif";
        Glide.with(this)
                .asGif()
                .load(bossSpriteUrl)
                .placeholder(android.R.drawable.ic_menu_camera)
                .into(ivEnemySprite);

        enemyHp = 100;
        pbEnemyHp.setProgress(enemyHp);
    }

    private void startAutoBattle() {
        autoBattleHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (playerHp <= 0 || enemyHp <= 0) return;

                Random random = new Random();
                int playerDamage = random.nextInt(25) + 15;
                int enemyDamage = random.nextInt(20) + 10;

                // Daño al enemigo
                enemyHp = Math.max(0, enemyHp - playerDamage);
                pbEnemyHp.setProgress(enemyHp);
                appendLog("⚔ Tu Pokémon causa " + playerDamage + " de daño.");

                if (enemyHp <= 0) {
                    String resultMsg = "🏆 ¡VICTORIA en el Piso " + floorNumber + " vs " + bossName + "!";
                    appendLog(resultMsg);
                    Toast.makeText(BattleActivity.this, "¡Piso Completado!", Toast.LENGTH_LONG).show();
                    saveBattleToHistory("VICTORIA - Piso " + floorNumber + " vs " + bossName);

                    isBattleRunning = false;
                    isBattleFinished = true;
                    hasWon = true;

                    // Cambiar botón a 'Siguiente Piso'
                    btnStartBattle.setEnabled(true);
                    btnStartBattle.setText("SIGUIENTE PISO ➔");
                    btnStartBattle.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#00E5FF")));
                    btnStartBattle.setTextColor(Color.parseColor("#0F172A"));
                    return;
                }

                // Daño al jugador
                playerHp = Math.max(0, playerHp - enemyDamage);
                pbPlayerHp.setProgress(playerHp);
                appendLog("💥 " + bossName + " causa " + enemyDamage + " de daño.");

                if (playerHp <= 0) {
                    currentPokemonIndex++;
                    if (currentPokemonIndex < playerTeam.size()) {
                        appendLog("💀 Pokémon debilitado. Cambiando al siguiente...");
                        loadCurrentPlayerPokemon();
                        autoBattleHandler.postDelayed(this, 1500);
                    } else {
                        String resultMsg = "❌ DERROTA en el Piso " + floorNumber + " vs " + bossName;
                        appendLog(resultMsg);
                        Toast.makeText(BattleActivity.this, "Derrota en el Desafío", Toast.LENGTH_LONG).show();
                        saveBattleToHistory("DERROTA - Piso " + floorNumber + " vs " + bossName);

                        isBattleRunning = false;
                        isBattleFinished = true;
                        hasWon = false;

                        // Cambiar botón a 'Reintentar'
                        btnStartBattle.setEnabled(true);
                        btnStartBattle.setText("REINTENTAR 🔄");
                        btnStartBattle.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#E11D48")));
                        btnStartBattle.setTextColor(Color.WHITE);
                    }
                } else {
                    autoBattleHandler.postDelayed(this, 1500);
                }
            }
        }, 1500);
    }

    private void saveBattleToHistory(String result) {
        String existingHistory = sharedPreferences.getString("history_log", "");
        String newEntry = "• " + result + "\n" + existingHistory;
        sharedPreferences.edit().putString("history_log", newEntry).apply();
    }

    private void appendLog(String message) {
        String currentLog = tvBattleLog.getText().toString();
        tvBattleLog.setText(currentLog + "\n" + message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        autoBattleHandler.removeCallbacksAndMessages(null);
    }
}