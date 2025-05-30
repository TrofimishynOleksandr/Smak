package com.itstep.smakmobile.adapter;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.itstep.smakmobile.R;
import com.itstep.smakmobile.RecipeDetailsActivity;
import com.itstep.smakmobile.auth.AuthManager;
import com.itstep.smakmobile.dto.RecipeShortDto;
import com.itstep.smakmobile.service.ApiClient;
import com.itstep.smakmobile.service.RecipeApiService;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private static final String BASE_URL = "http://192.168.0.103:5127/";
    private List<RecipeShortDto> recipes;
    private Context context;

    public RecipeAdapter(Context context, List<RecipeShortDto> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe_card, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        RecipeShortDto recipe = recipes.get(position);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetailsActivity.class);
            intent.putExtra("recipeId", recipe.getId().toString());
            context.startActivity(intent);
        });

        holder.title.setText(recipe.getTitle());
        holder.description.setText(recipe.getDescription());
        holder.time.setText(recipe.getCookTimeMinutes() + " хв");

        Picasso.get()
                .load(BASE_URL + recipe.getImageUrl())
                .placeholder(R.drawable.recipe_placeholder)
                .into(holder.image);

//        int rating = (int) Math.round(recipe.getAverageRating());
        int fullStars = (int) recipe.getAverageRating();
        for (int i = 0; i < 5; i++) {
            holder.stars[i].setImageResource(i < fullStars ? R.drawable.ic_star_filled : R.drawable.ic_star_empty);
        }

        AuthManager authManager = new AuthManager(context);

        holder.likeButton.setImageResource(recipe.isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart_empty);
        holder.likeButton.setOnClickListener(v -> {
            if (!authManager.isAuthenticated())
            {
                holder.likeButton.setImageResource(R.drawable.ic_heart_empty);
                holder.likeButton.setEnabled(false);
                return;
            }

            RecipeApiService api = ApiClient.getClient(context).create(RecipeApiService.class);
            boolean wasFavorite = recipe.isFavorite;
            recipe.isFavorite = !wasFavorite;
            holder.likeButton.setImageResource(recipe.isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart_empty);

            Call<Void> call = recipe.isFavorite
                    ? api.addToFavorites(recipe.getId())
                    : api.removeFromFavorites(recipe.getId());

            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (!response.isSuccessful()) {
                        recipe.isFavorite = wasFavorite;
                        holder.likeButton.setImageResource(recipe.isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart_empty);
                        Toast.makeText(context, "Не вдалося змінити улюблене", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    recipe.isFavorite = wasFavorite;
                    holder.likeButton.setImageResource(recipe.isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart_empty);
                    Toast.makeText(context, "Помилка мережі", Toast.LENGTH_SHORT).show();
                }
            });
        });

        holder.ratingCount.setText(String.valueOf(recipe.getRatingsCount()));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, time, description, ratingCount;
        ImageView[] stars;
        ImageButton likeButton;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.recipeImage);
            title = itemView.findViewById(R.id.recipeTitle);
            time = itemView.findViewById(R.id.recipeTime);
            description = itemView.findViewById(R.id.textDescription);
            ratingCount = itemView.findViewById(R.id.textRatingCount);
            likeButton = itemView.findViewById(R.id.btnLike);

            stars = new ImageView[] {
                    itemView.findViewById(R.id.star1),
                    itemView.findViewById(R.id.star2),
                    itemView.findViewById(R.id.star3),
                    itemView.findViewById(R.id.star4),
                    itemView.findViewById(R.id.star5),
            };
        }
    }
}


