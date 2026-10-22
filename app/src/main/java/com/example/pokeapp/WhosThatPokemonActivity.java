package com.example.pokeapp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class WhosThatPokemonActivity extends AppCompatActivity {

    private VideoView videoViewBg;
    private boolean isVideoPrepared = false;

    private ImageView ivPokemonImage, ivGameBg;
    private ProgressBar pbLoading;
    private TextView tvScore, tvResultStatus, tvResultHeader, tvRevealedPokemonName;
    private Button btnOption1, btnOption2, btnOption3, btnOption4, btnNext;
    private Button[] optionButtons;
    private LinearLayout layoutOptionsContainer, layoutResultContainer;
    private MaterialCardView cardResult;

    private int correctPokemonId;
    private String correctPokemonName = "";
    private int score = 0;
    private boolean isAnswered = false;

    // MOTOR DE VOZ TEXT-TO-SPEECH
    private TextToSpeech textToSpeech;
    private boolean isTtsReady = false;

    private final String[] fallbackNames = {
            "Bulbasaur", "Charmander", "Squirtle", "Pikachu", "Clefairy", "Vulpix",
            "Jigglypuff", "Meowth", "Psyduck", "Growlithe", "Poliwag", "Abra",
            "Machop", "Tentacool", "Geodude", "Ponyta", "Slowpoke", "Magnemite",
            "Gastly", "Onix", "Drowzee", "Koffing", "Rhyhorn", "Horsea",
            "Staryu", "Scyther", "Jynx", "Electabuzz", "Magmar", "Magikarp",
            "Lapras", "Ditto", "Eevee", "Snorlax", "Articuno", "Zapdos", "Moltres", "Mewtwo"
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

        setContentView(R.layout.activity_whos_that_pokemon);

        // INICIALIZAR EL MOTOR DE VOZ
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(new Locale("es", "ES"));
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isTtsReady = true;
                }
            }
        });

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

        ivGameBg = findViewById(R.id.ivGameBg);
        ivPokemonImage = findViewById(R.id.ivPokemonImage);
        pbLoading = findViewById(R.id.pbLoading);
        tvScore = findViewById(R.id.tvScore);
        tvResultStatus = findViewById(R.id.tvResultStatus);

        layoutOptionsContainer = findViewById(R.id.layoutOptionsContainer);
        layoutResultContainer = findViewById(R.id.layoutResultContainer);
        cardResult = findViewById(R.id.cardResult);
        tvResultHeader = findViewById(R.id.tvResultHeader);
        tvRevealedPokemonName = findViewById(R.id.tvRevealedPokemonName);

        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnOption4 = findViewById(R.id.btnOption4);
        btnNext = findViewById(R.id.btnNext);

        optionButtons = new Button[]{btnOption1, btnOption2, btnOption3, btnOption4};

        for (Button btn : optionButtons) {
            btn.setOnClickListener(this::onOptionSelected);
        }

        btnNext.setOnClickListener(v -> loadNewPokemon());

        loadNewPokemon();
    }

    private void loadNewPokemon() {
        isAnswered = false;

        // Detener voz si aún está hablando
        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }

        layoutOptionsContainer.setVisibility(View.VISIBLE);
        tvResultStatus.setVisibility(View.VISIBLE);
        layoutResultContainer.setVisibility(View.GONE);

        tvResultStatus.setText("Selecciona el Pokémon correcto:");
        tvResultStatus.setTextColor(Color.parseColor("#E2E8F0"));

        pbLoading.setVisibility(View.VISIBLE);
        ivPokemonImage.setImageDrawable(null);

        for (Button btn : optionButtons) {
            btn.setEnabled(false);
            btn.setText("...");
            btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1E293B")));
        }

        Random random = new Random();
        correctPokemonId = random.nextInt(151) + 1;

        new Thread(() -> {
            try {
                URL url = new URL("https://pokeapi.co/api/v2/pokemon/" + correctPokemonId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) builder.append(line);
                    reader.close();

                    JSONObject json = new JSONObject(builder.toString());
                    String name = json.getString("name");
                    correctPokemonName = name.substring(0, 1).toUpperCase() + name.substring(1);

                    runOnUiThread(this::setupPokemonView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void setupPokemonView() {
        pbLoading.setVisibility(View.GONE);

        ivPokemonImage.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);

        String animatedGifUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/versions/generation-v/black-white/animated/" + correctPokemonId + ".gif";

        Glide.with(this)
                .asGif()
                .load(animatedGifUrl)
                .into(ivPokemonImage);

        setupOptions();
    }

    private void setupOptions() {
        List<String> options = new ArrayList<>();
        options.add(correctPokemonName);

        Random random = new Random();
        while (options.size() < 4) {
            String randomName = fallbackNames[random.nextInt(fallbackNames.length)];
            if (!options.contains(randomName) && !randomName.equalsIgnoreCase(correctPokemonName)) {
                options.add(randomName);
            }
        }

        Collections.shuffle(options);

        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(options.get(i));
            optionButtons[i].setEnabled(true);
            optionButtons[i].setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1E293B")));
        }
    }

    private void onOptionSelected(View v) {
        if (isAnswered) return;
        isAnswered = true;

        Button selectedButton = (Button) v;
        String selectedName = selectedButton.getText().toString();

        ivPokemonImage.clearColorFilter();

        layoutOptionsContainer.setVisibility(View.GONE);
        tvResultStatus.setVisibility(View.GONE);

        tvRevealedPokemonName.setText(correctPokemonName.toUpperCase() + "!");

        // DECIR EL NOMBRE DEL POKÉMON EN VOZ ALTA
        speakPokemonName(correctPokemonName);

        if (selectedName.equalsIgnoreCase(correctPokemonName)) {
            score++;
            tvScore.setText("RACHA: " + score);
            tvResultHeader.setText("¡CORRECTO!");
            tvResultHeader.setTextColor(Color.parseColor("#00E5FF"));
            tvRevealedPokemonName.setTextColor(Color.parseColor("#22C55E"));
            cardResult.setStrokeColor(Color.parseColor("#22C55E"));
        } else {
            score = 0;
            tvScore.setText("RACHA: " + score);
            tvResultHeader.setText("¡INCORRECTO! ERA...");
            tvResultHeader.setTextColor(Color.parseColor("#EF4444"));
            tvRevealedPokemonName.setTextColor(Color.parseColor("#EF4444"));
            cardResult.setStrokeColor(Color.parseColor("#EF4444"));
        }

        layoutResultContainer.setVisibility(View.VISIBLE);
    }

    private void speakPokemonName(String name) {
        if (isTtsReady && textToSpeech != null) {
            String speechText = "¡Es " + name + "!";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textToSpeech.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                textToSpeech.speak(speechText, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
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
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}