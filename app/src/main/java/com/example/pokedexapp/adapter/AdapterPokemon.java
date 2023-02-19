package com.example.pokedexapp.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokedexapp.R;
import com.example.pokedexapp.models.Pokemon;

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

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Pokemon pokemon = pokemonList.get(position);
        holder.nome.setText(pokemon.getNome());
        Bitmap image = byteToBitmap(pokemon.getFoto());
        holder.foto.setImageBitmap(image);
    }

    @Override
    public int getItemCount() {
        return this.pokemonList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView foto;
        TextView nome;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.foto = itemView.findViewById(R.id.imageViewFotoPokemon);
            this.nome = itemView.findViewById(R.id.textViewAdapterNome);
        }
    }

    public static Bitmap byteToBitmap(byte[] bytes) {
        return (bytes == null || bytes.length == 0) ? null : BitmapFactory
                .decodeByteArray(bytes, 0, bytes.length);
    }
}
