package com.example.pokeapp.api;

import com.example.pokeapp.model.Pokemon;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PokeApiService {

    @GET("pokemon/{nameOrId}")
    Call<Pokemon> getPokemon(@Path("nameOrId") String nameOrId);
}