package com.example.pokeapp;

public class MenuItem {
    private String title;
    private String subtitle;
    private String imageUrl;
    private String colorHex;
    private String shapeType;
    private int iconResId; // Ícono vectorial distintivo (ej. R.drawable.ic_search)
    private int spanSize;
    private int heightDp;

    public MenuItem(String title, String subtitle, String imageUrl, String colorHex, String shapeType, int iconResId, int spanSize, int heightDp) {
        this.title = title;
        this.subtitle = subtitle;
        this.imageUrl = imageUrl;
        this.colorHex = colorHex;
        this.shapeType = shapeType;
        this.iconResId = iconResId;
        this.spanSize = spanSize;
        this.heightDp = heightDp;
    }

    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getImageUrl() { return imageUrl; }
    public String getColorHex() { return colorHex; }
    public String getShapeType() { return shapeType; }
    public int getIconResId() { return iconResId; }
    public int getSpanSize() { return spanSize; }
    public int getHeightDp() { return heightDp; }
}