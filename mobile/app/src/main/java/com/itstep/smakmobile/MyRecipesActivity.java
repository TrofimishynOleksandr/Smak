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
import com.itstep.smakmobile.auth.AuthManager;
import com.itstep.smakmobile.dto.RecipeSearchQuery;
import com.itstep.smakmobile.dto.RecipeShortDto;
import com.itstep.smakmobile.service.ApiClient;
import com.itstep.smakmobile.service.RecipeApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRecipesActivity extends BaseActivity  {

    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private List<RecipeShortDto> myRecipes = new ArrayList<>();
    private RecipeApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipes);
        setupHeader();

        recyclerView = findViewById(R.id.myRecipeList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new RecipeAdapter(this, myRecipes);
        recyclerView.setAdapter(adapter);

        apiService = ApiClient.getClient(this).create(RecipeApiService.class);

        loadMyRecipes();
    }

    private void loadMyRecipes() {
        UUID userId = new AuthManager(this).getUserId();

        Map<String, String> params = new HashMap<>();
        params.put("authorId", userId.toString());

        apiService.searchRecipes(params).enqueue(new Callback<List<RecipeShortDto>>() {
            @Override
            public void onResponse(Call<List<RecipeShortDto>> call, Response<List<RecipeShortDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    myRecipes.clear();
                    myRecipes.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<RecipeShortDto>> call, Throwable t) {
                Toast.makeText(MyRecipesActivity.this, "Не вдалося завантажити рецепти", Toast.LENGTH_SHORT).show();
            }
        });
    }

}