package com.example.pokedexapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pokedexapp.R;
import com.example.pokedexapp.controllers.RetrofitConfig;
import com.example.pokedexapp.models.Login;
import com.example.pokedexapp.models.Usuario;
import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    EditText editTextLogin, editTextSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextLogin = findViewById(R.id.editTextLogin);
        editTextSenha = findViewById(R.id.editTextSenha);
    }

    //Função de Login
    public void login(View view) {
        if ((editTextLogin.length() == 0) || (editTextSenha.length() == 0)) {
            Toast.makeText(this, "Informe o usuário e a senha para login!", Toast.LENGTH_SHORT).show();
            return;
        }
        String username = editTextLogin.getText().toString();
        String senha = editTextSenha.getText().toString();

        Login login = new Login();
        login.setLogin(username);
        login.setSenha(senha);

        Gson gson = new Gson();
        String jsonUser = gson.toJson(login);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonUser);

        Call<Usuario> userCall = new RetrofitConfig().loginService().loginRequest(requestBody);
        userCall.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Usuario usuario = response.body();
                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("usuario", usuario);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
                else {
                    new AlertDialog.Builder(MainActivity.this).setTitle("Erro!").setMessage("Login ou Senha incorretos").show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e("CALL ERROR", "onFailure: " + t.getMessage(), t);
                new AlertDialog.Builder(MainActivity.this).setTitle("Erro!").setMessage(t.getMessage()).show();
            }
        });

    }
}