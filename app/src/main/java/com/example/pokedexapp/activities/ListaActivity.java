package com.example.pokedexapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokedexapp.R;
import com.example.pokedexapp.adapter.AdapterPokemon;
import com.example.pokedexapp.adapter.RecyclerItemClickListener;
import com.example.pokedexapp.controllers.RetrofitConfig;
import com.example.pokedexapp.models.Pokemon;
import com.example.pokedexapp.models.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ListaActivity extends AppCompatActivity {
    private List<Pokemon> pokemonList;
    private Usuario user;
    private RecyclerView recyclerViewPokemons;
    private AdapterPokemon adapterPokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        Bundle bundle = getIntent().getExtras();
        this.user = (Usuario) bundle.getSerializable("usuario");
        getListaPokemon();
    }

    public void getListaPokemon() {
        Call<List<Pokemon>> callPokemon = new RetrofitConfig().getPokemonsService().getPokemons();
        callPokemon.enqueue(new Callback<List<Pokemon>>() {
            @Override
            public void onResponse(Call<List<Pokemon>> call, Response<List<Pokemon>> response) {
                if (response.isSuccessful()) {
                    List<Pokemon> pokemons = response.body();
                    recyclerViewPokemons = findViewById(R.id.recyclerViewPokemons);
                    adapterPokemon = new AdapterPokemon(pokemons);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                    recyclerViewPokemons.setLayoutManager(layoutManager);
                    recyclerViewPokemons.setHasFixedSize(true);
                    recyclerViewPokemons.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
                    recyclerViewPokemons.setAdapter(adapterPokemon);
                    recyclerViewPokemons.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerViewPokemons, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                            Pokemon pokemon = pokemons.get(position);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("usuario", user);
                            bundle.putSerializable("pokemon", pokemon);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onLongItemClick(View view, int position) {
                            onItemClick(view, position);
                        }

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        }
                    }));
                    if (pokemons.size() == 0) {
                        new AlertDialog.Builder(ListaActivity.this).setTitle("Atenção").setMessage("Nenhum pokemon encontrado para exibição").show();
                        return;
                    }
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        new AlertDialog.Builder(ListaActivity.this).setTitle(String.format("Erro %d", response.code())).setMessage(jObjError.getString("error")).show();
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

}