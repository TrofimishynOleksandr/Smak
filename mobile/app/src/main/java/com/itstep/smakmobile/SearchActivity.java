package com.itstep.smakmobile;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.itstep.smakmobile.adapter.RecipeAdapter;
import com.itstep.smakmobile.dto.CategoryDto;
import com.itstep.smakmobile.dto.RecipeSearchQuery;
import com.itstep.smakmobile.dto.RecipeShortDto;
import com.itstep.smakmobile.service.ApiClient;
import com.itstep.smakmobile.service.RecipeApiService;
import com.itstep.smakmobile.utils.SimpleItemSelectedListener;
import com.itstep.smakmobile.utils.SimpleTextWatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends BaseActivity {

    private EditText searchInput, maxCookTimeInput, minRatingInput;
    private Spinner categorySpinner, sortBySpinner;
    private RecyclerView recipeList;

    private RecipeAdapter adapter;
    private List<CategoryDto> categories = new ArrayList<>();
    private List<RecipeShortDto> recipes = new ArrayList<>();

    private RecipeApiService apiService;
    private ArrayAdapter<String> categoryAdapter;
    private ArrayAdapter<String> sortAdapter;

    private final Handler debounceHandler = new Handler();
    private Runnable debounceRunnable;

    private String categoryName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupHeader();

        apiService = ApiClient.getClient(this).create(RecipeApiService.class);

        searchInput = findViewById(R.id.searchInput);
        maxCookTimeInput = findViewById(R.id.maxCookTimeInput);
        minRatingInput = findViewById(R.id.minRatingInput);
        categorySpinner = findViewById(R.id.categorySpinner);
        sortBySpinner = findViewById(R.id.sortBySpinner);
        recipeList = findViewById(R.id.recipeList);

        String searchQuery = getIntent().getStringExtra("searchQuery");
        if (searchQuery != null) {
            searchInput.setText(searchQuery);
            submitSearch();
        }

        adapter = new RecipeAdapter(this, recipes);
        recipeList.setLayoutManager(new GridLayoutManager(this, 2));
        recipeList.setAdapter(adapter);

        setupSortSpinner();
        loadCategories();
        setupFilterListeners();

        categoryName = getIntent().getStringExtra("categoryName");
        if (categoryName != null) {
            submitSearch();
        }
    }

    private void selectCategoryInSpinner() {
        if(categoryName != null) {
            for (int i = 0; i < categorySpinner.getCount(); i++) {
                String category = (String) categorySpinner.getItemAtPosition(i);
                if (category.equals(categoryName)) {
                    categorySpinner.setSelection(i);
                    categoryName = null;
                    break;
                }
            }
        }
    }

    private void setupSortSpinner() {
        sortAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                Arrays.asList("Без сортування", "За рейтингом", "За часом", "За популярністю"));
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBySpinner.setAdapter(sortAdapter);
    }

    private void loadCategories() {
        apiService.getCategories().enqueue(new Callback<List<CategoryDto>>() {
            @Override
            public void onResponse(Call<List<CategoryDto>> call, Response<List<CategoryDto>> response) {
                if (response.isSuccessful()) {
                    categories = response.body();
                    List<String> names = new ArrayList<>();
                    names.add("Всі категорії");
                    for (CategoryDto c : categories) names.add(c.getName());
                    categoryAdapter = new ArrayAdapter<>(SearchActivity.this,
                            android.R.layout.simple_spinner_item, names);
                    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(categoryAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<CategoryDto>> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Не вдалося завантажити категорії", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupFilterListeners() {
        TextWatcher debounceTextWatcher = new SimpleTextWatcher(() -> debounceSearch());
        searchInput.addTextChangedListener(debounceTextWatcher);
        maxCookTimeInput.addTextChangedListener(debounceTextWatcher);
        minRatingInput.addTextChangedListener(debounceTextWatcher);

        categorySpinner.setOnItemSelectedListener(new SimpleItemSelectedListener(this::debounceSearch));
        sortBySpinner.setOnItemSelectedListener(new SimpleItemSelectedListener(this::debounceSearch));
    }

    private void debounceSearch() {
        if (debounceRunnable != null) debounceHandler.removeCallbacks(debounceRunnable);
        debounceRunnable = this::submitSearch;
        debounceHandler.postDelayed(debounceRunnable, 400);
    }

    private void submitSearch() {
        String search = searchInput.getText().toString().trim();
        String maxTime = maxCookTimeInput.getText().toString().trim();
        String minRating = minRatingInput.getText().toString().trim();
        String sortByValue = sortBySpinner.getSelectedItemPosition() == 0 ? null : getSortKey();

        UUID categoryId = null;
        selectCategoryInSpinner();
        int catIndex = categorySpinner.getSelectedItemPosition();
        if (catIndex > 0 && catIndex <= categories.size()) {
            categoryId = categories.get(catIndex - 1).getId();
        }

        Map<String, String> params = new HashMap<>();
        if (!search.isEmpty()) params.put("search", search);
        if (categoryId != null) params.put("categoryId", categoryId.toString());
        if (!maxTime.isEmpty()) params.put("maxCookTime", maxTime);
        if (!minRating.isEmpty()) params.put("minRating", minRating);
        if (sortByValue != null) params.put("sortBy", sortByValue);

        apiService.searchRecipes(params).enqueue(new Callback<List<RecipeShortDto>>() {
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
                Toast.makeText(SearchActivity.this, "Помилка пошуку", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getSortKey() {
        switch (sortBySpinner.getSelectedItemPosition()) {
            case 1: return "rating";
            case 2: return "time";
            case 3: return "popular";
            default: return null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("search", searchInput.getText().toString());
        outState.putString("maxTime", maxCookTimeInput.getText().toString());
        outState.putString("minRating", minRatingInput.getText().toString());
        outState.putInt("category", categorySpinner.getSelectedItemPosition());
        outState.putInt("sortBy", sortBySpinner.getSelectedItemPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        searchInput.setText(savedInstanceState.getString("search", ""));
        maxCookTimeInput.setText(savedInstanceState.getString("maxTime", ""));
        minRatingInput.setText(savedInstanceState.getString("minRating", ""));
        final int categoryIndex = savedInstanceState.getInt("category", 0);
        final int sortByIndex = savedInstanceState.getInt("sortBy", 0);
        new Handler().postDelayed(() -> {
            categorySpinner.setSelection(categoryIndex);
            sortBySpinner.setSelection(sortByIndex);
        }, 200);
    }
}