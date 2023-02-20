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

public class PesquisaTipoActivity extends AppCompatActivity {
    Usuario user;
    EditText editTextPesquisaTipo;
    ListView listViewPesquisaTipoResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisa_tipo);
        user = (Usuario) getIntent().getSerializableExtra("usuario");
        editTextPesquisaTipo = findViewById(R.id.editTextPesquisaTipo);
        listViewPesquisaTipoResultado = findViewById(R.id.listViewPesquisaTipoResultado);

        editTextPesquisaTipo.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String tipo = editTextPesquisaTipo.getText().toString();
                pesquisaPorTipo(tipo);
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            public void afterTextChanged(Editable editable) {}
        });

    }

    public void updateListView(List<String> pokemonNames){
        ArrayAdapter<String> array = new ArrayAdapter<>(PesquisaTipoActivity.this, android.R.layout.simple_list_item_1, pokemonNames);
        listViewPesquisaTipoResultado.setAdapter(array);
    }


    public void pesquisaPorTipo(String tipo) {
        if (editTextPesquisaTipo.length() == 0) {
            listViewPesquisaTipoResultado.setAdapter(null);
            Toast.makeText(this, "Insira um tipo para pesquisar!", Toast.LENGTH_SHORT).show();
            return;
        }
        Call<List<Pokemon>> listaPokemonsFiltrados = new RetrofitConfig().getPokemonsService().getPokemonsPorTipo(tipo);
        listaPokemonsFiltrados.enqueue(new Callback<List<Pokemon>>() {
            @Override
            public void onResponse(Call<List<Pokemon>> call, Response<List<Pokemon>> response) {
                if (response.isSuccessful()) {
                    List<Pokemon> pokemons = response.body();
                    List<String> pokemonNames = new ArrayList<>();
                    for (Pokemon pokemon : pokemons) {
                        pokemonNames.add(pokemon.getNome());
                    }
                    updateListView(pokemonNames);
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.e("ERRO", "pesquisaPorTipo " + jObjError.getString("error").toString());
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Pokemon>> call, Throwable t) {
                listViewPesquisaTipoResultado.setAdapter(null);
                Log.e("ERRO", "pesquisaPorhabilidade " + t.getMessage());
            }
        });
    }
}