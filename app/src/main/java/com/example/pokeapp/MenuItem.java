package com.example.pokeapp;

public class MenuItem {
    private String title;
    private String subtitle;
    private int iconResId;
    private String colorHex;
    private int spanSize; // 1 = Cuadro normal, 2 = Ancho completo
    private int heightDp; // Altura personalizada para dar espacio

    public MenuItem(String title, String subtitle, int iconResId, String colorHex, int spanSize, int heightDp) {
        this.title = title;
        this.subtitle = subtitle;
        this.iconResId = iconResId;
        this.colorHex = colorHex;
        this.spanSize = spanSize;
        this.heightDp = heightDp;
    }

    public MenuItem(String title, int iconResId, String colorHex, int spanSize, boolean isFeatured) {
        this.title = title;
        this.subtitle = "";
        this.iconResId = iconResId;
        this.colorHex = colorHex;
        this.spanSize = spanSize;
        this.heightDp = isFeatured ? 110 : 100;
    }

    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public int getIconResId() { return iconResId; }
    public String getColorHex() { return colorHex; }
    public int getSpanSize() { return spanSize; }
    public int getHeightDp() { return heightDp; }
}
