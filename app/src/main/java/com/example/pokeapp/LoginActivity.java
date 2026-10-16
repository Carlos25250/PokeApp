package com.example.pokeapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {

    private TextView tvTabLogin, tvTabRegister, tvLoadingText;
    private EditText etEmail, etPassword, etConfirmPassword, etBirthDate;
    private CheckBox cbTerms;
    private LinearLayout layoutRegisterExtra;
    private Button btnSubmit;
    private FrameLayout loadingOverlay;
    private ImageView ivCharizardLoading;

    private ImageView ivStarterBulbasaur, ivStarterCharmander, ivStarterSquirtle, ivStarterPikachu;
    private int selectedStarterId = 1; // Por defecto Bulbasaur (#1)

    private FirebaseAuth mAuth;
    private boolean isRegisterMode = false;

    private final String charizardAnimatedUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/versions/generation-v/black-white/animated/6.gif";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            goToMenu();
            return;
        }

        tvTabLogin = findViewById(R.id.tvTabLogin);
        tvTabRegister = findViewById(R.id.tvTabRegister);
        tvLoadingText = findViewById(R.id.tvLoadingText);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etBirthDate = findViewById(R.id.etBirthDate);
        cbTerms = findViewById(R.id.cbTerms);
        layoutRegisterExtra = findViewById(R.id.layoutRegisterExtra);
        btnSubmit = findViewById(R.id.btnSubmit);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        ivCharizardLoading = findViewById(R.id.ivCharizardLoading);

        ivStarterBulbasaur = findViewById(R.id.ivStarterBulbasaur);
        ivStarterCharmander = findViewById(R.id.ivStarterCharmander);
        ivStarterSquirtle = findViewById(R.id.ivStarterSquirtle);
        ivStarterPikachu = findViewById(R.id.ivStarterPikachu);

        // Pre-cargar Charizard de Loading
        Glide.with(this).asGif().load(charizardAnimatedUrl).into(ivCharizardLoading);

        // Cargar Sprites Animados de los Iniciales
        loadStarterGifs();

        // Configurar Selección de Inicial
        setupStarterSelection();

        tvTabLogin.setOnClickListener(v -> setMode(false));
        tvTabRegister.setOnClickListener(v -> setMode(true));
        etBirthDate.setOnClickListener(v -> showDatePicker());

        btnSubmit.setOnClickListener(v -> {
            if (isRegisterMode) {
                performRegister();
            } else {
                performLogin();
            }
        });
    }

    private void loadStarterGifs() {
        String baseUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/versions/generation-v/black-white/animated/";
        Glide.with(this).asGif().load(baseUrl + "1.gif").into(ivStarterBulbasaur);
        Glide.with(this).asGif().load(baseUrl + "4.gif").into(ivStarterCharmander);
        Glide.with(this).asGif().load(baseUrl + "7.gif").into(ivStarterSquirtle);
        Glide.with(this).asGif().load(baseUrl + "25.gif").into(ivStarterPikachu);
    }

    private void setupStarterSelection() {
        selectStarter(1, ivStarterBulbasaur);

        ivStarterBulbasaur.setOnClickListener(v -> selectStarter(1, ivStarterBulbasaur));
        ivStarterCharmander.setOnClickListener(v -> selectStarter(4, ivStarterCharmander));
        ivStarterSquirtle.setOnClickListener(v -> selectStarter(7, ivStarterSquirtle));
        ivStarterPikachu.setOnClickListener(v -> selectStarter(25, ivStarterPikachu));
    }

    private void selectStarter(int pokemonId, ImageView selectedView) {
        selectedStarterId = pokemonId;

        // Resetear opacidad
        ivStarterBulbasaur.setAlpha(0.4f);
        ivStarterCharmander.setAlpha(0.4f);
        ivStarterSquirtle.setAlpha(0.4f);
        ivStarterPikachu.setAlpha(0.4f);

        // Destacar el elegido
        selectedView.setAlpha(1.0f);
    }

    private void setMode(boolean register) {
        isRegisterMode = register;
        if (register) {
            tvTabLogin.setTextColor(Color.parseColor("#64748B"));
            tvTabRegister.setTextColor(Color.parseColor("#00E5FF"));
            layoutRegisterExtra.setVisibility(View.VISIBLE);
            btnSubmit.setText("REGISTRARSE");
        } else {
            tvTabLogin.setTextColor(Color.parseColor("#00E5FF"));
            tvTabRegister.setTextColor(Color.parseColor("#64748B"));
            layoutRegisterExtra.setVisibility(View.GONE);
            btnSubmit.setText("INICIAR SESIÓN");
        }
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR) - 10;
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etBirthDate.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading("Autenticando...");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    hideLoading();
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "¡Bienvenido Entrenador!", Toast.LENGTH_SHORT).show();
                        goToMenu();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error de inicio de sesión: " +
                                (task.getException() != null ? task.getException().getMessage() : ""), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void performRegister() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String birthDate = etBirthDate.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(birthDate)) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Debes aceptar los Términos y Condiciones", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading("Registrando entrenador y asignando inicial...");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    hideLoading();
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "¡Cuenta creada! Has elegido tu inicial.", Toast.LENGTH_SHORT).show();
                        goToMenu();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error al registrar: " +
                                (task.getException() != null ? task.getException().getMessage() : ""), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showLoading(String message) {
        tvLoadingText.setText(message);
        loadingOverlay.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        loadingOverlay.setVisibility(View.GONE);
    }

    private void goToMenu() {
        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
        startActivity(intent);
        finish();
    }
}