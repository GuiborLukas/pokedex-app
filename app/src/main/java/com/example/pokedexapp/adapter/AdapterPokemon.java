package com.example.pokedexapp.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokedexapp.R;
import com.example.pokedexapp.models.Pokemon;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class AdapterPokemon extends RecyclerView.Adapter<AdapterPokemon.MyViewHolder> {
    private List<Pokemon> pokemonList;

    public AdapterPokemon(List<Pokemon> pokemonList) {
        this.pokemonList = pokemonList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_lista_pokemons, parent, false);
        return new MyViewHolder(listItem);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Pokemon pokemon = pokemonList.get(position);
        holder.textViewAdapterNome.setText(pokemon.getNome());
        byte[] bytes = Base64.getDecoder().decode(pokemon.getFoto());
        Bitmap image =  BitmapFactory.decodeStream(new ByteArrayInputStream(bytes), null, null);
        holder.imageViewFotoPokemon.setImageBitmap(image);
    }

    @Override
    public int getItemCount() {
        return this.pokemonList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewFotoPokemon;
        TextView textViewAdapterNome;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageViewFotoPokemon = itemView.findViewById(R.id.imageViewFotoPokemon);
            this.textViewAdapterNome = itemView.findViewById(R.id.textViewAdapterNome);
        }
    }

}
