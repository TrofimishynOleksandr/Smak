package com.itstep.smakmobile;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.itstep.smakmobile.dto.ChefDto;
import com.itstep.smakmobile.dto.RecipeShortDto;
import com.itstep.smakmobile.service.ApiClient;
import com.itstep.smakmobile.service.RecipeApiService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChefProfileActivity extends BaseActivity {

    private ImageView chefAvatar;
    private TextView chefNameText;
    private RecyclerView recipesRecyclerView;
    private RecipeAdapter recipeAdapter;
    private List<RecipeShortDto> chefRecipes = new ArrayList<>();

    private String chefId;
    private RecipeApiService apiService;

    private static final String BASE_URL = "http://192.168.0.103:5127/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_profile);
        setupHeader();

        apiService = ApiClient.getClient(this).create(RecipeApiService.class);

        chefAvatar = findViewById(R.id.chefAvatar);
        chefNameText = findViewById(R.id.chefName);
        recipesRecyclerView = findViewById(R.id.chefRecipesRecyclerView);

        recipeAdapter = new RecipeAdapter(this, chefRecipes);
        recipesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recipesRecyclerView.setAdapter(recipeAdapter);

        chefId = getIntent().getStringExtra("chefId");
        if (chefId != null) {
            loadChefInfo();
            loadChefRecipes();
        }
    }

    private void loadChefInfo() {
        apiService.getChefById(UUID.fromString(chefId)).enqueue(new Callback<ChefDto>() {
            @Override
            public void onResponse(Call<ChefDto> call, Response<ChefDto> response) {
                if (response.isSuccessful()) {
                    ChefDto chef = response.body();
                    chefNameText.setText(chef.getName());

                    Picasso.get()
                            .load(BASE_URL + chef.getAvatarUrl())
                            .placeholder(R.drawable.avatar_placeholder)
                            .into(chefAvatar);
                }
            }

            @Override
            public void onFailure(Call<ChefDto> call, Throwable t) {
                Toast.makeText(ChefProfileActivity.this, "Помилка завантаження шефа", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadChefRecipes() {
        Map<String, String> params = new HashMap<>();
        params.put("authorId", chefId);

        apiService.searchRecipes(params).enqueue(new Callback<List<RecipeShortDto>>() {
                    @Override
                    public void onResponse(Call<List<RecipeShortDto>> call, Response<List<RecipeShortDto>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            chefRecipes.clear();
                            chefRecipes.addAll(response.body());
                            recipeAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<RecipeShortDto>> call, Throwable t) {
                        Toast.makeText(ChefProfileActivity.this, "Помилка завантаження рецептів", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}