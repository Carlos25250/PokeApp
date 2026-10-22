package com.example.pokeapp;

import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(MenuItem item);
    }

    private final List<MenuItem> items;
    private final OnItemClickListener listener;

    public MenuAdapter(List<MenuItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuItem item = items.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final HexagonView cardContainer;
        private final ImageView ivIcon, ivBadgeIcon;
        private final TextView tvTitle, tvSubtitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardContainer = itemView.findViewById(R.id.cardContainer);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            ivBadgeIcon = itemView.findViewById(R.id.ivBadgeIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
        }

        public void bind(final MenuItem item, final OnItemClickListener listener) {
            tvTitle.setText(item.getTitle());
            tvSubtitle.setText(item.getSubtitle());

            cardContainer.setShapeType(item.getShapeType());

            // Ícono de insignia cibernética (Cian Neón)
            if (item.getIconResId() != 0) {
                ivBadgeIcon.setImageResource(item.getIconResId());
                ivBadgeIcon.setColorFilter(Color.parseColor("#00E5FF")); // Tinte Neón Cian
                ivBadgeIcon.setVisibility(View.VISIBLE);
            } else {
                ivBadgeIcon.setVisibility(View.GONE);
            }

            // Cargar arte a color completo de PokeAPI sin filtro plano
            if ("pokeball".equals(item.getShapeType())) {
                ivIcon.setVisibility(View.GONE);
            } else {
                ivIcon.setVisibility(View.VISIBLE);

                // Quitamos el ColorFilter plano para ver el Pokémon a todo color HD
                ivIcon.clearColorFilter();

                Glide.with(itemView.getContext())
                        .load(item.getImageUrl())
                        .into(ivIcon);
            }

            itemView.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

                // Reproducir rugido del Pokémon
                playPokemonCry(item.getImageUrl());

                // Animación de interacción Cyber-Tech
                v.animate()
                        .scaleX(0.88f)
                        .scaleY(0.88f)
                        .setDuration(70)
                        .withEndAction(() -> {
                            v.animate()
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .setDuration(70)
                                    .withEndAction(() -> listener.onItemClick(item))
                                    .start();
                        })
                        .start();
            });
        }

        private void playPokemonCry(String imageUrl) {
            if (imageUrl == null || !imageUrl.contains("/official-artwork/")) return;

            try {
                String[] parts = imageUrl.split("/");
                String lastPart = parts[parts.length - 1];
                String pokemonIdStr = lastPart.replace(".png", "");

                int pokemonId = Integer.parseInt(pokemonIdStr);
                String cryUrl = "https://raw.githubusercontent.com/PokeAPI/cries/main/cries/pokemon/latest/" + pokemonId + ".ogg";

                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                );
                mediaPlayer.setDataSource(cryUrl);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(MediaPlayer::start);
                mediaPlayer.setOnCompletionListener(mp -> {
                    mp.reset();
                    mp.release();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}