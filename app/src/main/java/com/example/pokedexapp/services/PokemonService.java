package com.example.pokedexapp.services;

import com.example.pokedexapp.models.Habilidade;
import com.example.pokedexapp.models.Pokemon;
import com.example.pokedexapp.models.Tipo;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PokemonService {
    @GET("/pokemons")
    Call<List<Pokemon>> getPokemons();

    @GET("/pokemons/habilidades/{habilidade}")
    Call<List<Pokemon>> getPokemonsPorHabilidade(@Path("habilidade") String habilidade);

    @GET("/pokemons/tipos/{tipo}")
    Call<List<Pokemon>> getPokemonsPorTipo(@Path("tipo") String tipo);

    @GET("/pokemons/{id}")
    Call<Pokemon> getPokemonPorId(@Path("id") Long id);

    @POST("/pokemons")
    Call<Pokemon> cadastrarPokemon(@Body Pokemon pokemon);

    @PUT("/pokemons/{id}")
    Call<Pokemon> atualizarPokemon(@Path("id") Long id, @Body Pokemon pokemon);

    @DELETE("/pokemons/{id}")
    Call<ResponseBody> deletarPokemon(@Path("id") Long id);

    @GET("/pokemons/habilidades/toptres")
    Call<List<Habilidade>> getTopTresHabilidades();

    @GET("/pokemons/tipos/toptres")
    Call<List<Tipo>> getTopTresTipos();

}
