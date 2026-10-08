package com.example.pokeapp.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PokeApiService {

    @GET("pokemon/{nameOrId}")
    Call<PokemonData> getPokemon(@Path("nameOrId") String nameOrId);

    // Clases para mapear la respuesta JSON de PokéAPI
    class PokemonData {
        public int id;
        public String name;
        public List<StatItem> stats;
    }

    class StatItem {
        @SerializedName("base_stat")
        public int baseStat;
        public StatDetail stat;
    }

    class StatDetail {
        public String name;
    }
}