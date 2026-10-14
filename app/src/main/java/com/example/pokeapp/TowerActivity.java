package com.example.pokeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.pokeapp.api.PokeApiService;
import com.example.pokeapp.model.Pokemon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TowerActivity extends AppCompatActivity {

    private TextView tvPisoActual, tvRivalName, tvPlayerName, tvRivalHp, tvPlayerHp, tvTowerLog, tvTeamStatusBattle;
    private ProgressBar pbRivalHp, pbPlayerHp;
    private ImageView ivRivalSprite, ivPlayerSprite, ivTowerBg;
    private Button btnMove1, btnMove2, btnMove3, btnMove4, btnHuir;
    private ScrollView svTowerLog;

    private int pisoActual = 1, maxPisos = 10;
    private ArrayList<String> playerTeam;
    private ArrayList<String> bossTeam;
    private String bossName = "Jefe";

    private int currentPlayerIndex = 0;
    private int currentBossIndex = 0;

    private int playerHp = 100, playerMaxHp = 100;
    private int rivalHp = 100, rivalMaxHp = 100;

    private final Random random = new Random();
    private PokeApiService apiService;

    // Clase interna para los ataques específicos de cada Pokémon
    public static class Move {
        String name;
        int minDmg;
        int maxDmg;
        boolean isHeal;
        int healVal;

        public Move(String name, int minDmg, int maxDmg, boolean isHeal, int healVal) {
            this.name = name;
            this.minDmg = minDmg;
            this.maxDmg = maxDmg;
            this.isHeal = isHeal;
            this.healVal = healVal;
        }
    }

    private final Move[] currentMoves = new Move[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tower);

        // Recibir datos del Intent
        playerTeam = getIntent().getStringArrayListExtra("playerTeam");
        if (playerTeam == null || playerTeam.isEmpty()) {
            playerTeam = new ArrayList<>();
            playerTeam.add("pikachu");
            playerTeam.add("charmander");
            playerTeam.add("bulbasaur");
        }

        pisoActual = getIntent().getIntExtra("floorNumber", 1);
        configurarJefeParaPiso();

        // Vincular vistas
        tvPisoActual = findViewById(R.id.tvPisoActual);
        tvTeamStatusBattle = findViewById(R.id.tvTeamStatusBattle);
        tvRivalName = findViewById(R.id.tvRivalName);
        tvPlayerName = findViewById(R.id.tvPlayerName);
        tvRivalHp = findViewById(R.id.tvRivalHp);
        tvPlayerHp = findViewById(R.id.tvPlayerHp);
        pbRivalHp = findViewById(R.id.pbRivalHp);
        pbPlayerHp = findViewById(R.id.pbPlayerHp);
        ivRivalSprite = findViewById(R.id.ivRivalSprite);
        ivPlayerSprite = findViewById(R.id.ivPlayerSprite);
        ivTowerBg = findViewById(R.id.ivTowerBg);
        
        btnMove1 = findViewById(R.id.btnMove1);
        btnMove2 = findViewById(R.id.btnMove2);
        btnMove3 = findViewById(R.id.btnMove3);
        btnMove4 = findViewById(R.id.btnMove4);
        btnHuir = findViewById(R.id.btnHuir);
        svTowerLog = findViewById(R.id.svTowerLog);
        tvTowerLog = findViewById(R.id.tvTowerLog);

        // Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(PokeApiService.class);

        actualizarFondoPiso();
        cargarPokemonJugador();
        cargarPokemonRival();

        btnMove1.setOnClickListener(v -> ejecutarTurnoJugador(1));
        btnMove2.setOnClickListener(v -> ejecutarTurnoJugador(2));
        btnMove3.setOnClickListener(v -> ejecutarTurnoJugador(3));
        btnMove4.setOnClickListener(v -> ejecutarTurnoJugador(4));
        btnHuir.setOnClickListener(v -> finish());
    }

    private void configurarJefeParaPiso() {
        switch (pisoActual) {
            case 1: bossName = "Líder Brock"; bossTeam = new ArrayList<>(Arrays.asList("geodude", "onix", "vulpix")); break;
            case 2: bossName = "Líder Misty"; bossTeam = new ArrayList<>(Arrays.asList("staryu", "starmie", "psyduck")); break;
            case 3: bossName = "Líder Lt. Surge"; bossTeam = new ArrayList<>(Arrays.asList("voltorb", "pikachu", "raichu")); break;
            case 4: bossName = "Líder Erika"; bossTeam = new ArrayList<>(Arrays.asList("oddish", "gloom", "vileplume")); break;
            case 5: bossName = "Líder Koga"; bossTeam = new ArrayList<>(Arrays.asList("koffing", "grimer", "weezing")); break;
            case 6: bossName = "Líder Sabrina"; bossTeam = new ArrayList<>(Arrays.asList("abra", "kadabra", "alakazam")); break;
            case 7: bossName = "Líder Blaine"; bossTeam = new ArrayList<>(Arrays.asList("growlithe", "ponyta", "arcanine")); break;
            case 8: bossName = "Líder Giovanni"; bossTeam = new ArrayList<>(Arrays.asList("rhyhorn", "nidoqueen", "nidoking")); break;
            case 9: bossName = "Alto Mando Lorelei"; bossTeam = new ArrayList<>(Arrays.asList("jynx", "lapras", "cloyster")); break;
            case 10: bossName = "Campeón Gary"; bossTeam = new ArrayList<>(Arrays.asList("pidgeot", "alakazam", "charizard")); break;
            default: bossName = "Líder de Piso"; bossTeam = new ArrayList<>(Arrays.asList("rattata", "pidgey", "spearow")); break;
        }
    }

    private void actualizarFondoPiso() {
        int resId = getResources().getIdentifier("piso" + pisoActual, "drawable", getPackageName());
        if (resId == 0) resId = getResources().getIdentifier("piso_" + pisoActual, "drawable", getPackageName());
        if (resId != 0) {
            ivTowerBg.setImageResource(resId);
        } else {
            ivTowerBg.setImageResource(R.drawable.battle_bg);
        }
    }

    private void actualizarMovimientosPokemon(String pokemonName) {
        String lower = pokemonName.toLowerCase();
        if (lower.contains("pikachu")) {
            currentMoves[0] = new Move("Impactrueno", 12, 20, false, 0);
            currentMoves[1] = new Move("Ataque Rápido", 10, 16, false, 0);
            currentMoves[2] = new Move("Rayo", 20, 32, false, 0);
            currentMoves[3] = new Move("Agilidad (Curar)", 0, 0, true, 25);
        } else if (lower.contains("charmander") || lower.contains("charizard") || lower.contains("charmeleon")) {
            currentMoves[0] = new Move("Arañazo", 10, 16, false, 0);
            currentMoves[1] = new Move("Ascuas", 12, 22, false, 0);
            currentMoves[2] = new Move("Lanzallamas", 22, 35, false, 0);
            currentMoves[3] = new Move("Respiro (Curar)", 0, 0, true, 30);
        } else if (lower.contains("bulbasaur") || lower.contains("ivysaur") || lower.contains("venusaur")) {
            currentMoves[0] = new Move("Placaje", 10, 16, false, 0);
            currentMoves[1] = new Move("Látigo Cepa", 14, 22, false, 0);
            currentMoves[2] = new Move("Bomba Lodo", 20, 32, false, 0);
            currentMoves[3] = new Move("Síntesis (Curar)", 0, 0, true, 25);
        } else if (lower.contains("squirtle") || lower.contains("wartortle") || lower.contains("blastoise")) {
            currentMoves[0] = new Move("Placaje", 10, 16, false, 0);
            currentMoves[1] = new Move("Pistola Agua", 12, 22, false, 0);
            currentMoves[2] = new Move("Hidrobomba", 24, 38, false, 0);
            currentMoves[3] = new Move("Refugio (Curar)", 0, 0, true, 25);
        } else {
            // Ataques temáticos personalizados para cualquier otro Pokémon
            currentMoves[0] = new Move("Placaje", 10, 16, false, 0);
            currentMoves[1] = new Move("Ataque Rápido", 12, 20, false, 0);
            currentMoves[2] = new Move("Golpe Feroz", 18, 30, false, 0);
            currentMoves[3] = new Move("Descanso (Curar)", 0, 0, true, 25);
        }

        btnMove1.setText("⚡ " + currentMoves[0].name);
        btnMove2.setText("⚔️ " + currentMoves[1].name);
        btnMove3.setText("🔥 " + currentMoves[2].name);
        btnMove4.setText("🌿 " + currentMoves[3].name);
    }

    private void cargarPokemonJugador() {
        String name = playerTeam.get(currentPlayerIndex);
        apiService.getPokemon(name).enqueue(new Callback<Pokemon>() {
            @Override
            public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Pokemon p = response.body();
                    String imgUrl = p.getSprites().getFrontDefault();
                    Glide.with(TowerActivity.this).load(imgUrl).into(ivPlayerSprite);
                    String pokeName = p.getName().toUpperCase();
                    tvPlayerName.setText(pokeName);
                    playerMaxHp = 100;
                    playerHp = playerMaxHp;
                    actualizarMovimientosPokemon(p.getName());
                    actualizarUI();
                    log("¡Adelante, " + pokeName + "!");
                }
            }
            @Override
            public void onFailure(Call<Pokemon> call, Throwable t) {}
        });
    }

    private void cargarPokemonRival() {
        String name = bossTeam.get(currentBossIndex);
        apiService.getPokemon(name).enqueue(new Callback<Pokemon>() {
            @Override
            public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String imgUrl = response.body().getSprites().getFrontDefault();
                    Glide.with(TowerActivity.this).load(imgUrl).into(ivRivalSprite);
                    tvRivalName.setText(bossName + " saca a " + response.body().getName().toUpperCase());
                    rivalMaxHp = 80 + (pisoActual * 15);
                    rivalHp = rivalMaxHp;
                    actualizarUI();
                    log("¡" + bossName + " envía a " + response.body().getName().toUpperCase() + "!");
                }
            }
            @Override
            public void onFailure(Call<Pokemon> call, Throwable t) {}
        });
    }

    private void ejecutarTurnoJugador(int tipoMovimiento) {
        if (playerHp <= 0 || rivalHp <= 0) return;

        Move move = currentMoves[tipoMovimiento - 1];
        String playerNameStr = tvPlayerName.getText().toString();

        if (move.isHeal) {
            playerHp = Math.min(playerMaxHp, playerHp + move.healVal);
            log("¡" + playerNameStr + " usa " + move.name + " y recupera " + move.healVal + " HP!");
            animarGolpe(ivPlayerSprite);
            actualizarUI();
            ivRivalSprite.postDelayed(this::ejecutarTurnoRival, 1000);
            return;
        }

        int dano = random.nextInt(move.maxDmg - move.minDmg + 1) + move.minDmg;
        rivalHp = Math.max(0, rivalHp - dano);
        log("¡" + playerNameStr + " usa " + move.name + " y causa " + dano + " de daño (-" + dano + " HP)!");
        animarGolpe(ivRivalSprite);
        actualizarUI();

        // Verificar si el rival actual fue derrotado
        if (rivalHp <= 0) {
            log("¡El Pokémon rival ha caído!");
            currentBossIndex++;
            if (currentBossIndex < bossTeam.size()) {
                log("¡" + bossName + " manda al siguiente Pokémon!");
                setControlesActivos(false);
                ivRivalSprite.postDelayed(() -> {
                    cargarPokemonRival();
                    setControlesActivos(true);
                }, 1500);
            } else {
                log("¡Has derrotado a " + bossName + " en el Piso " + pisoActual + "! 🏆");
                setControlesActivos(false);
                Toast.makeText(this, "¡Piso " + pisoActual + " superado!", Toast.LENGTH_LONG).show();
                ivRivalSprite.postDelayed(() -> {
                    if (pisoActual >= maxPisos) {
                        Toast.makeText(this, "¡FELICIDADES! ¡Has conquistado la Torre!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(TowerActivity.this, TowerVictoryActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        pisoActual++;
                        currentBossIndex = 0;
                        currentPlayerIndex = 0; // Resetea el orden de los Pokémon al inicial
                        configurarJefeParaPiso();
                        log("¡Piso superado! Tu equipo se ha curado y vuelve al orden inicial.");
                        cargarPokemonJugador(); // Carga el primer Pokémon del equipo con HP al máximo
                        intentarSiguientePiso();
                    }
                }, 2000);
            }
            return;
        }

        // Si el rival sigue vivo, responde la máquina (turno del rival)
        setControlesActivos(false);
        ivRivalSprite.postDelayed(this::ejecutarTurnoRival, 1200);
    }

    private void ejecutarTurnoRival() {
        if (playerHp <= 0 || rivalHp <= 0) {
            setControlesActivos(true);
            return;
        }

        int danoRival = random.nextInt(14) + 10;
        playerHp = Math.max(0, playerHp - danoRival);
        log("¡El rival contraataca y causa " + danoRival + " de daño (-" + danoRival + " HP)!");
        animarGolpe(ivPlayerSprite);
        actualizarUI();

        if (playerHp <= 0) {
            log("¡Tu Pokémon ha sido derrotado!");
            currentPlayerIndex++;
            if (currentPlayerIndex < playerTeam.size()) {
                log("¡Tu equipo tiene más Pokémon! Entra el siguiente.");
                ivPlayerSprite.postDelayed(this::cargarPokemonJugador, 1500);
            } else {
                log("¡Todo tu equipo ha caído! Has perdido en la torre...");
                Toast.makeText(this, "¡Derrota! Vuelve a intentar.", Toast.LENGTH_LONG).show();
                btnMove1.postDelayed(() -> {
                    Intent intent = new Intent(TowerActivity.this, TowerSelectActivity.class);
                    startActivity(intent);
                    finish();
                }, 2000);
                return;
            }
        }
        setControlesActivos(true);
    }

    private void intentarSiguientePiso() {
        actualizarFondoPiso();
        cargarPokemonRival();
        setControlesActivos(true);
    }

    private void animarGolpe(ImageView vista) {
        Animation anim = new TranslateAnimation(-10, 10, 0, 0);
        anim.setDuration(50);
        anim.setRepeatCount(4);
        anim.setRepeatMode(Animation.REVERSE);
        vista.startAnimation(anim);
    }

    private void setControlesActivos(boolean activo) {
        btnMove1.setEnabled(activo);
        btnMove2.setEnabled(activo);
        btnMove3.setEnabled(activo);
        btnMove4.setEnabled(activo);
    }

    private void actualizarUI() {
        pbPlayerHp.setMax(playerMaxHp);
        pbPlayerHp.setProgress(playerHp);
        tvPlayerHp.setText(playerHp + "/" + playerMaxHp + " HP");

        pbRivalHp.setMax(rivalMaxHp);
        pbRivalHp.setProgress(rivalHp);
        tvRivalHp.setText(rivalHp + "/" + rivalMaxHp + " HP");

        tvPisoActual.setText("PISO " + pisoActual + " / " + maxPisos);
        tvTeamStatusBattle.setText("Tu equipo: " + (playerTeam.size() - currentPlayerIndex) + " vivos | " + bossName + ": " + (bossTeam.size() - currentBossIndex) + " vivos");
    }

    private void log(String mensaje) {
        tvTowerLog.append("\n" + mensaje);
        svTowerLog.post(() -> svTowerLog.fullScroll(View.FOCUS_DOWN));
    }
}
