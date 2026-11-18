package com.example.omnistock.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.omnistock.R;
import com.example.omnistock.ui.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsuario, etPassword;
    private Button btnIngresar;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        btnIngresar = findViewById(R.id.btnIngresar);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        loginViewModel.getUsuarioAutenticado().observe(this, usuario -> {
            if (usuario != null) {
                Toast.makeText(this, "Bienvenido " + usuario.getNombre() + " (" + usuario.getRol() + ")", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("USER_ID", usuario.getId());
                intent.putExtra("USER_ROL", usuario.getRol());
                startActivity(intent);
                finish();
            }
        });

        loginViewModel.getMensajeError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        btnIngresar.setOnClickListener(v -> {
            String user = etUsuario.getText().toString();
            String pass = etPassword.getText().toString();
            loginViewModel.autenticar(user, pass);
        });
    }
}