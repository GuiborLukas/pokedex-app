package com.example.pokedexapp.services;

import androidx.annotation.NonNull;

import com.example.pokedexapp.models.Usuario;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LoginService {
    @POST("/login")
    Call<Usuario> loginRequest(@Body RequestBody requestBody);

    @GET("/usuarios/{id}")
    Call<Usuario> getUsuarioById(@Path("id") Long id);
}
