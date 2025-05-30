package com.itstep.smakmobile;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.itstep.smakmobile.adapter.RecipeAdapter;
import com.itstep.smakmobile.dto.RecipeShortDto;
import com.itstep.smakmobile.service.ApiClient;
import com.itstep.smakmobile.service.RecipeApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteRecipesActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private RecipeApiService apiService;
    private List<RecipeShortDto> recipes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_recipes);
        setupHeader();

        recyclerView = findViewById(R.id.favoritesRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new RecipeAdapter(this, recipes);
        recyclerView.setAdapter(adapter);

        apiService = ApiClient.getClient(this).create(RecipeApiService.class);
        loadFavorites();
    }

    private void loadFavorites() {
        apiService.getFavoriteRecipes().enqueue(new Callback<List<RecipeShortDto>>() {
            @Override
            public void onResponse(Call<List<RecipeShortDto>> call, Response<List<RecipeShortDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recipes.clear();
                    recipes.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<RecipeShortDto>> call, Throwable t) {
                Toast.makeText(FavoriteRecipesActivity.this, "Помилка при завантаженні улюблених", Toast.LENGTH_SHORT).show();
            }
        });
    }
}