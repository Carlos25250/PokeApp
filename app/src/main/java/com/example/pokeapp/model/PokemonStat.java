package com.example.pokeapp.model;

import com.google.gson.annotations.SerializedName;

public class PokemonStat {
    @SerializedName("base_stat")
    private int baseStat;

    @SerializedName("stat")
    private Stat stat;

    public int getBaseStat() {
        return baseStat;
    }

    public Stat getStat() {
        return stat;
    }
}