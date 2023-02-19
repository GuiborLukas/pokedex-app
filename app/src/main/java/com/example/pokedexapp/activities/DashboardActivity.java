package com.example.pokedexapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pokedexapp.R;
import com.example.pokedexapp.controllers.RetrofitConfig;
import com.example.pokedexapp.models.Habilidade;
import com.example.pokedexapp.models.Pokemon;
import com.example.pokedexapp.models.Tipo;
import com.example.pokedexapp.models.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {
    TextView textViewTotalPokemons,
            textViewTopTresHabilidades,
            textViewTopTresTipos;
    Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        textViewTotalPokemons = findViewById(R.id.textViewTotalPokemons);
        textViewTopTresHabilidades = findViewById(R.id.textViewTopTresHabilidades);
        textViewTopTresTipos = findViewById(R.id.textViewTopTresTipos);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        user = (Usuario) bundle.getSerializable("usuario");
        if(user == null){
            Intent it = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(it);
            finish();
        }
        countPokemons();
        getTopTipos();
        getTopHabilidades();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()) {
            case R.id.menuItemCadastro: {
                cadastrarPokemon();
                break;
            }
            case R.id.menuItemListar: {
                listaPokemon();
                break;
            }
            case R.id.menuItemPesquisaTipo: {
                pesquisaPokemonsPorTipo();
                break;
            }
            case R.id.menuItemPesquisaHabilidade: {
                pesquisaPokemonsPorHabilidade();
                break;
            }
            case R.id.menuItemSair: {
                exitApp();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void countPokemons() {
        Call<List<Pokemon>> callPokemon = new RetrofitConfig().getPokemonsService().getPokemons();
        callPokemon.enqueue(new Callback<List<Pokemon>>() {
            @Override
            public void onResponse(Call<List<Pokemon>> call, Response<List<Pokemon>> response) {
                if (response.isSuccessful()) {
                    List<Pokemon> pokemonList = response.body();
                    textViewTotalPokemons.setText(String.valueOf(pokemonList.size()));
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.e("ERRO", "countPokemons " + jObjError.getString("error").toString());
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Pokemon>> call, Throwable t) {
                Log.e("ERRO", "countPokemons: " + t.getMessage());
            }
        });
    }

    private void getTopTipos() {
        Call<List<Tipo>> topAbilities = new RetrofitConfig().getPokemonsService().getTopTresTipos();
        topAbilities.enqueue(new Callback<List<Tipo>>() {
            @Override
            public void onResponse(Call<List<Tipo>> call, Response<List<Tipo>> response) {
                if (response.isSuccessful()) {
                    List<Tipo> tipos = response.body();
                    for (Tipo tipo : tipos) {
                        textViewTopTresHabilidades.append(String.format("%s (%d)\n", tipo.getNome(), tipo.getQuantidade()));
                    }
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.e("ERRO", "getTopTipos " + jObjError.getString("error").toString());
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Tipo>> call, Throwable t) {
                Log.e("ERRO", "getTopTipos " + t.getMessage());
            }
        });
    }

    private void getTopHabilidades() {
        Call<List<Habilidade>> topAbilities = new RetrofitConfig().getPokemonsService().getTopTresHabilidades();
        topAbilities.enqueue(new Callback<List<Habilidade>>() {
            @Override
            public void onResponse(Call<List<Habilidade>> call, Response<List<Habilidade>> response) {
                if (response.isSuccessful()) {
                    List<Habilidade> habilidades = response.body();
                    for (Habilidade habilidade : habilidades) {
                        textViewTopTresTipos.append(String.format("%s (%d)\n", habilidade.getNome(), habilidade.getQuantidade()));
                    }
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.e("ERRO", "getTopHabilidades " + jObjError.getString("error").toString());
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Habilidade>> call, Throwable t) {
                Log.e("ERRO", "getTopHabilidades " + t.getMessage());
            }
        });
    }

    public void exitApp() {
        finishAffinity();
    }

    public void cadastrarPokemon() {
        Intent intent = new Intent(DashboardActivity.this, CadastroActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void listaPokemon() {
        Call<List<Pokemon>> callPokemons = new RetrofitConfig().getPokemonsService().getPokemons();
        callPokemons.enqueue(new Callback<List<Pokemon>>() {
            @Override
            public void onResponse(Call<List<Pokemon>> call, Response<List<Pokemon>> response) {
                if (response.isSuccessful()) {
                    List<Pokemon> pokemonList = new ArrayList<>(response.body());
                    if (pokemonList.size() == 0) {
                        new AlertDialog.Builder(DashboardActivity.this).setTitle("Atenção").setMessage("Nenhum pokemon encontrado para exibição").show();
                        return;
                    }
                    Log.i("PokemonList", "onResponse: " + pokemonList.toString());
                    Intent intent = new Intent(DashboardActivity.this, ListaActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("pokemons", (Serializable) pokemonList);
                    bundle.putSerializable("usuario", user);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        new AlertDialog.Builder(DashboardActivity.this).setTitle(String.format("Erro %d", response.code())).setMessage(jObjError.getString("error")).show();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Pokemon>> call, Throwable t) {
                Log.e("ERRO", "listaPokemons: " + t.getMessage());
            }
        });
    }

    public void pesquisaPokemonsPorHabilidade() {
        Intent intent = new Intent(DashboardActivity.this, PesquisaHabilidadeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("usuario", user);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void pesquisaPokemonsPorTipo() {
        Intent intent = new Intent(DashboardActivity.this, PesquisaTipoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("usuario", user);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}