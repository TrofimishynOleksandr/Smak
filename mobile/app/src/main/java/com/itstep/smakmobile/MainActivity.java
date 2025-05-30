package com.itstep.smakmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.itstep.smakmobile.adapter.CategoryAdapter;
import com.itstep.smakmobile.adapter.ChefAdapter;
import com.itstep.smakmobile.adapter.RecipeCollectionAdapter;
import com.itstep.smakmobile.auth.AuthManager;
import com.itstep.smakmobile.dto.CategoryDto;
import com.itstep.smakmobile.dto.ChefDto;
import com.itstep.smakmobile.dto.RecipeCollectionDto;
import com.itstep.smakmobile.service.ApiClient;
import com.itstep.smakmobile.service.CategoryApiService;
import com.itstep.smakmobile.service.ChefApiService;
import com.itstep.smakmobile.service.RecipeApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity  {

    private RecyclerView recipeCollectionsRecyclerView;
    private RecyclerView categoriesRecyclerView;
    private RecyclerView chefsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupHeader();

        categoriesRecyclerView = findViewById(R.id.categoryRecyclerView);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        loadCategories();

        recipeCollectionsRecyclerView = findViewById(R.id.recipeCollectionsRecyclerView);
        recipeCollectionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadRecipeCollections();

        chefsRecyclerView = findViewById(R.id.chefRecyclerView);
        chefsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        loadPopularChefs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupHeader();
    }

    private void loadCategories() {
        CategoryApiService apiService = ApiClient.getClient(this).create(CategoryApiService.class);
        apiService.getCategories().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<CategoryDto>> call, Response<List<CategoryDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CategoryAdapter adapter = new CategoryAdapter(MainActivity.this, response.body());
                    categoriesRecyclerView.setHasFixedSize(true);
                    categoriesRecyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(MainActivity.this, "Не вдалося завантажити категорії", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CategoryDto>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Помилка мережі: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadRecipeCollections() {
        RecipeApiService apiService = ApiClient.getClient(this).create(RecipeApiService.class);
        apiService.getRecipeCollections().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<RecipeCollectionDto>> call, Response<List<RecipeCollectionDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RecipeCollectionAdapter adapter = new RecipeCollectionAdapter(MainActivity.this, response.body());
                    recipeCollectionsRecyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(MainActivity.this, "Не вдалося завантажити колекції", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<RecipeCollectionDto>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Помилка мережі: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPopularChefs() {
        ChefApiService apiService = ApiClient.getClient(this).create(ChefApiService.class);
        apiService.getPopularChefs().enqueue(new Callback<List<ChefDto>>() {
            @Override
            public void onResponse(Call<List<ChefDto>> call, Response<List<ChefDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ChefAdapter adapter = new ChefAdapter(MainActivity.this, response.body());
                    chefsRecyclerView.setHasFixedSize(true);
                    chefsRecyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(MainActivity.this, "Не вдалося завантажити авторів", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ChefDto>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Помилка мережі: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}