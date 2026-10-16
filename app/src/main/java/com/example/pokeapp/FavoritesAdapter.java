package com.example.pokeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    public static class FavoriteItem {
        String id;
        String name;
        String imageUrl;

        public FavoriteItem(String id, String name, String imageUrl) {
            this.id = id;
            this.name = name;
            this.imageUrl = imageUrl;
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
        holder.tvName.setText(item.name.toUpperCase());
        holder.tvId.setText("#" + String.format("%03d", Integer.parseInt(item.id)));

        // Cargar imagen del Pokémon
        String url = item.imageUrl;
        if (url == null || url.isEmpty()) {
            url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/" + item.id + ".png";
        }
        Glide.with(context).load(url).into(holder.ivPokemon);

        // Animación de entrada
        holder.itemView.setAlpha(0f);
        holder.itemView.setScaleX(0.8f);
        holder.itemView.setScaleY(0.8f);
        holder.itemView.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setStartDelay(position * 30L)
                .start();

        // Clic con efecto rebote
        holder.itemView.setOnClickListener(v -> {
            v.animate()
                    .scaleX(0.92f)
                    .scaleY(0.92f)
                    .setDuration(80)
                    .withEndAction(() -> v.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(80)
                            .withEndAction(() -> listener.onItemClick(item.id))
                            .start())
                    .start();
        });
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPokemon;
        TextView tvName, tvId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPokemon = itemView.findViewById(R.id.ivFavoritePokemon);
            tvName = itemView.findViewById(R.id.tvFavoriteName);
            tvId = itemView.findViewById(R.id.tvFavoriteId);
        }
    }
}
