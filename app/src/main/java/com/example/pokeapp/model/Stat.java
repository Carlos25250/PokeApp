package com.example.pokeapp.model;

import com.google.gson.annotations.SerializedName;

public class Stat {
    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }
}