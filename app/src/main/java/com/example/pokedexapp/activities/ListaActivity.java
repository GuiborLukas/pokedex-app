package com.example.pokedexapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokedexapp.R;
import com.example.pokedexapp.adapter.AdapterPokemon;
import com.example.pokedexapp.adapter.RecyclerItemClickListener;
import com.example.pokedexapp.models.Pokemon;
import com.example.pokedexapp.models.Usuario;

import java.util.ArrayList;
import java.util.List;


public class ListaActivity extends AppCompatActivity {
    private List<Pokemon> pokemonList = new ArrayList<>();
    private Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        Bundle bundle = getIntent().getExtras();
        this.user = (Usuario) bundle.getSerializable("user");
        this.pokemonList = (List<Pokemon>) bundle.getSerializable("pokemons");

        RecyclerView recyclerViewPokemon = findViewById(R.id.recyclerViewPokemons);
        AdapterPokemon adapter = new AdapterPokemon(pokemonList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewPokemon.setLayoutManager(layoutManager);
        recyclerViewPokemon.setHasFixedSize(true);
        recyclerViewPokemon.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
        recyclerViewPokemon.setAdapter(adapter);
        recyclerViewPokemon.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerViewPokemon, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                Pokemon pokemon = pokemonList.get(position);
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


    }

}