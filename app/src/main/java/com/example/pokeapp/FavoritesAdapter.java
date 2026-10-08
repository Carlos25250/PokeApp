package com.example.pokeapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(String pokemonId);
    }

    private final Context context;
    private final List<FavoriteItem> favoriteList;
    private final OnItemClickListener listener;

    // Paleta de colores para variarlos tipo Dashboard/Escuela
    private final String[] colors = {"#4CAF50", "#E91E63", "#2196F3", "#9C27B0", "#FF9800", "#00BCD4", "#3F51B5"};

    public static class FavoriteItem {
        String id;
        String name;

        public FavoriteItem(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public FavoritesAdapter(Context context, List<FavoriteItem> favoriteList, OnItemClickListener listener) {
        this.context = context;
        this.favoriteList = favoriteList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteItem item = favoriteList.get(position);
        holder.tvName.setText(item.name);

        // Asignar color según la posición
        int colorIndex = position % colors.length;
        holder.cardContainer.setBackgroundColor(Color.parseColor(colors[colorIndex]));

        // Cargar imagen del Pokémon
        String imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/" + item.id + ".png";
        Glide.with(context).load(imageUrl).into(holder.ivPokemon);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item.id));
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPokemon;
        TextView tvName;
        LinearLayout cardContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPokemon = itemView.findViewById(R.id.ivFavoritePokemon);
            tvName = itemView.findViewById(R.id.tvFavoriteName);
            cardContainer = itemView.findViewById(R.id.cardContainer);
        }
    }
}