package com.example.pokedexapp.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pokedexapp.R;
import com.example.pokedexapp.controllers.RetrofitConfig;
import com.example.pokedexapp.models.Pokemon;
import com.example.pokedexapp.models.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PesquisaHabilidadeActivity extends AppCompatActivity {
    Usuario user;
    EditText editTextPesquisaHabilidade;
    ListView listViewPesquisaHabilidadeResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisa_habilidade);
        user = (Usuario) getIntent().getSerializableExtra("usuario");
        listViewPesquisaHabilidadeResultado = findViewById(R.id.listViewPesquisaHabilidadeResultado);
        editTextPesquisaHabilidade = findViewById(R.id.editTextPesquisaHabilidade);

        editTextPesquisaHabilidade.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String habilidade = editTextPesquisaHabilidade.getText().toString();
                pesquisaPorhabilidade(habilidade);
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            public void afterTextChanged(Editable editable) {}
        });

    }



    public void pesquisaPorhabilidade(String habilidade) {
        if (editTextPesquisaHabilidade.length() == 0) {
            listViewPesquisaHabilidadeResultado.setAdapter(null);
            Toast.makeText(this, "Insira uma habilidade para pesquisar!", Toast.LENGTH_SHORT).show();
            return;
        }
        Call<List<Pokemon>> listaPokemonsFiltrados = new RetrofitConfig().getPokemonsService().getPokemonsPorHabilidade(habilidade);
        listaPokemonsFiltrados.enqueue(new Callback<List<Pokemon>>() {
            @Override
            public void onResponse(Call<List<Pokemon>> call, Response<List<Pokemon>> response) {
                if (response.isSuccessful()) {
                    List<Pokemon> pokemons = response.body();
                    List<String> pokemonNames = new ArrayList<>();
                    for (Pokemon pokemon : pokemons) {
                        pokemonNames.add(pokemon.getNome());
                    }
                    ArrayAdapter<String> array = new ArrayAdapter<>(PesquisaHabilidadeActivity.this, android.R.layout.simple_list_item_1, pokemonNames);
                    listViewPesquisaHabilidadeResultado.setAdapter(array);
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.e("ERRO", "pesquisaPorhabilidade " + jObjError.getString("error").toString());
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Pokemon>> call, Throwable t) {
                listViewPesquisaHabilidadeResultado.setAdapter(null);
                Log.e("ERRO", "pesquisaPorhabilidade " + t.getMessage());
            }
        });
    }
}