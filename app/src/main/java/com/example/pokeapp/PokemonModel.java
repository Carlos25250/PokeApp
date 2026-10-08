package com.example.pokeapp.model;

public class PokemonModel {
    private String name;
    private String imageUrl;
    private int maxHp;
    private int currentHp;
    private int attack;
    private int defense;
    private int speed;

    public PokemonModel(String name, String imageUrl, int hp, int attack, int defense, int speed) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.maxHp = hp;
        this.currentHp = hp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
    }

    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public int getMaxHp() { return maxHp; }
    public int getCurrentHp() { return currentHp; }
    public void setCurrentHp(int currentHp) { this.currentHp = Math.max(0, currentHp); }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getSpeed() { return speed; }
}