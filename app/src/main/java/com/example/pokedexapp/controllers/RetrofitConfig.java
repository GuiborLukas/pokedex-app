package com.example.pokedexapp.controllers;

import com.example.pokedexapp.services.LoginService;
import com.example.pokedexapp.services.PokemonService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConfig {
    private final Retrofit retrofit;
    private final String baseURL = "http://10.0.2.2:5000";


    public RetrofitConfig() {

        this.retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public LoginService loginService() {
        return this.retrofit.create(LoginService.class);
    }

    public PokemonService getPokemonsService() {
        return this.retrofit.create(PokemonService.class);
    }
}
