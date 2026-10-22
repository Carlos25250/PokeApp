package com.example.pokeapp.model;

public class FloorBossModel {
    private int floorNumber;
    private String floorTag;
    private String bossName;
    private String bossTeam;
    private int backgroundResId;
    private String bossImageUrl;
    private boolean unlocked;

    // Constructor completo con Fondo de Piso local (piso1.jpg, piso2.jpg) y URL/Imagen del Jefe
    public FloorBossModel(int floorNumber, String floorTag, String bossName, String bossTeam, int backgroundResId, String bossImageUrl, boolean unlocked) {
        this.floorNumber = floorNumber;
        this.floorTag = floorTag;
        this.bossName = bossName;
        this.bossTeam = bossTeam;
        this.backgroundResId = backgroundResId;
        this.bossImageUrl = bossImageUrl;
        this.unlocked = unlocked;
    }

    // Constructor alternativo por si solo usas el fondo del piso local
    public FloorBossModel(int floorNumber, String floorTag, String bossName, String bossTeam, int backgroundResId, boolean unlocked) {
        this(floorNumber, floorTag, bossName, bossTeam, backgroundResId, "", unlocked);
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public String getFloorTag() {
        return floorTag;
    }

    public void setFloorTag(String floorTag) {
        this.floorTag = floorTag;
    }

    public String getBossName() {
        return bossName;
    }

    public void setBossName(String bossName) {
        this.bossName = bossName;
    }

    public String getBossTeam() {
        return bossTeam;
    }

    public void setBossTeam(String bossTeam) {
        this.bossTeam = bossTeam;
    }

    public int getBackgroundResId() {
        return backgroundResId;
    }

    public void setBackgroundResId(int backgroundResId) {
        this.backgroundResId = backgroundResId;
    }

    public String getBossImageUrl() {
        return bossImageUrl;
    }

    public void setBossImageUrl(String bossImageUrl) {
        this.bossImageUrl = bossImageUrl;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }
}