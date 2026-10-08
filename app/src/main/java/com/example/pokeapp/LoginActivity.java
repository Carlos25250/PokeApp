package com.example.pokeapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // BOTÓN INICIAR SESIÓN
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "¡Bienvenido de nuevo!", Toast.LENGTH_SHORT).show();
                                irAlMenu();
                            } else {
                                Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Ingresa correo y contraseña", Toast.LENGTH_SHORT).show();
            }
        });

        // BOTÓN CREAR CUENTA
        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Cuenta registrada correctamente", Toast.LENGTH_SHORT).show();
                                irAlMenu();
                            } else {
                                Toast.makeText(LoginActivity.this, "Error al crear cuenta: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Ingresa correo y contraseña", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Verifica si el usuario ya inició sesión previamente
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Si ya hay una sesión activa, entra directamente al menú sin pedir contraseña
            irAlMenu();
        }
    }

    private void irAlMenu() {
        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
        startActivity(intent);
        finish(); // Cierra LoginActivity para evitar regresar con el botón atrás
    }
}