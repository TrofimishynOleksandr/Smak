package com.itstep.smakmobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.itstep.smakmobile.adapter.IngredientCreateAdapter;
import com.itstep.smakmobile.adapter.InstructionStepCreateAdapter;
import com.itstep.smakmobile.dto.CategoryDto;
import com.itstep.smakmobile.dto.IngredientCreateDto;
import com.itstep.smakmobile.dto.IngredientDto;
import com.itstep.smakmobile.dto.InstructionCreateDto;
import com.itstep.smakmobile.dto.UnitDto;
import com.itstep.smakmobile.service.ApiClient;
import com.itstep.smakmobile.service.RecipeApiService;
import com.itstep.smakmobile.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateRecipeActivity extends BaseActivity {

    private EditText titleInput, descriptionInput, cookTimeInput;
    private Spinner categorySpinner;
    private ImageView imagePreview;
    private Button selectImageButton, addIngredientButton, addStepButton, submitButton;
    private RecyclerView ingredientRecyclerView, instructionRecyclerView;
    private Uri selectedImageUri;

    private IngredientCreateAdapter ingredientAdapter;
    private InstructionStepCreateAdapter instructionAdapter;

    private List<IngredientDto> availableIngredients = new ArrayList<>();
    private List<UnitDto> availableUnits = new ArrayList<>();
    private List<IngredientCreateDto> ingredients = new ArrayList<>();
    private List<InstructionCreateDto> instructions = new ArrayList<>();
    private List<CategoryDto> categories = new ArrayList<>();
    private UUID selectedCategoryId = null;

    private RecipeApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);

        apiService = ApiClient.getClient(this).create(RecipeApiService.class);

        titleInput = findViewById(R.id.inputTitle);
        descriptionInput = findViewById(R.id.inputDescription);
        cookTimeInput = findViewById(R.id.inputCookTime);
        categorySpinner = findViewById(R.id.categorySpinner);
        imagePreview = findViewById(R.id.imagePreview);
        selectImageButton = findViewById(R.id.selectImageButton);
        addIngredientButton = findViewById(R.id.addIngredientButton);
        addStepButton = findViewById(R.id.addStepButton);
        submitButton = findViewById(R.id.submitRecipeButton);

        ingredientRecyclerView = findViewById(R.id.ingredientRecyclerView);
        instructionRecyclerView = findViewById(R.id.instructionRecyclerView);

        fetchCategories();
        fetchIngredientsAndUnits();

        selectImageButton.setOnClickListener(v -> pickImage());
        addIngredientButton.setOnClickListener(v -> {
            if (ingredientAdapter != null) ingredientAdapter.addEmpty();
        });
        addStepButton.setOnClickListener(v -> {
            if (instructionAdapter != null) instructionAdapter.addEmpty();
        });
        submitButton.setOnClickListener(v -> {
            try {
                submitRecipe();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            int index = requestCode - 1000;
            instructionAdapter.setImageForStep(index, data.getData());
        }
    }

    private void fetchIngredientsAndUnits() {
        apiService.getAvailableIngredients().enqueue(new Callback<List<IngredientDto>>() {
            @Override
            public void onResponse(Call<List<IngredientDto>> call, Response<List<IngredientDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    availableIngredients = response.body();
                    tryInitIngredientAdapter();
                }
            }

            @Override
            public void onFailure(Call<List<IngredientDto>> call, Throwable t) {
                Toast.makeText(CreateRecipeActivity.this, "Помилка інгредієнтів", Toast.LENGTH_SHORT).show();
            }
        });

        apiService.getUnits().enqueue(new Callback<List<UnitDto>>() {
            @Override
            public void onResponse(Call<List<UnitDto>> call, Response<List<UnitDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    availableUnits = response.body();
                    tryInitIngredientAdapter();
                }
            }

            @Override
            public void onFailure(Call<List<UnitDto>> call, Throwable t) {
                Toast.makeText(CreateRecipeActivity.this, "Помилка одиниць виміру", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void tryInitIngredientAdapter() {
        if (!availableIngredients.isEmpty() && !availableUnits.isEmpty()) {
            ingredientAdapter = new IngredientCreateAdapter(ingredients, availableIngredients, availableUnits);
            ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            ingredientRecyclerView.setAdapter(ingredientAdapter);
            ingredientAdapter.addEmpty();
            setupInstructionRecyclerView();
        }
    }

    private void setupInstructionRecyclerView() {
        instructionAdapter = new InstructionStepCreateAdapter(this, instructions);
        instructionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        instructionRecyclerView.setAdapter(instructionAdapter);
        instructionAdapter.addEmpty();
    }

    private void fetchCategories() {
        apiService.getCategories().enqueue(new Callback<List<CategoryDto>>() {
            @Override
            public void onResponse(Call<List<CategoryDto>> call, Response<List<CategoryDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories = response.body();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateRecipeActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            categories.stream().map(c -> c.getName()).collect(Collectors.toList()));
                    categorySpinner.setAdapter(adapter);

                    categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedCategoryId = categories.get(position).getId();
                        }
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                }
            }

            @Override
            public void onFailure(Call<List<CategoryDto>> call, Throwable t) {
                Toast.makeText(CreateRecipeActivity.this, "Помилка завантаження категорій", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        resultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    imagePreview.setImageURI(selectedImageUri);
                }
            });

    private void submitRecipe() throws Exception {
        String title = titleInput.getText().toString();
        String description = descriptionInput.getText().toString();
        int cookTime = Integer.parseInt(cookTimeInput.getText().toString());
        UUID categoryId = selectedCategoryId;
        Uri mainImageUri = selectedImageUri;

        if (mainImageUri == null) {
            Toast.makeText(this, "Оберіть зображення", Toast.LENGTH_SHORT).show();
            return;
        }

        List<IngredientCreateDto> ingredients = collectIngredients();
        List<InstructionCreateDto> instructions = collectInstructions();

        Gson gson = new Gson();
        RequestBody ingredientsJson = RequestBody.create(
                MediaType.parse("application/json"), gson.toJson(ingredients));
        RequestBody instructionsJson = RequestBody.create(
                MediaType.parse("application/json"), gson.toJson(instructions));

        RequestBody titlePart = RequestBody.create(MultipartBody.FORM, title);
        RequestBody descPart = RequestBody.create(MultipartBody.FORM, description);
        RequestBody catPart = RequestBody.create(MultipartBody.FORM, categoryId.toString());
        RequestBody timePart = RequestBody.create(MultipartBody.FORM, String.valueOf(cookTime));

        File mainImage = new File(FileUtils.getPath(this, mainImageUri));
        RequestBody mainImageBody = RequestBody.create(MediaType.parse("image/*"), mainImage);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("Image", mainImage.getName(), mainImageBody);

        List<MultipartBody.Part> instructionImages = createInstructionImageParts(instructions);

        apiService.createRecipe(
                imagePart, titlePart, descPart, catPart, timePart,
                ingredientsJson, instructionsJson, instructionImages
        ).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreateRecipeActivity.this, "Рецепт створено!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CreateRecipeActivity.this, "Помилка: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CreateRecipeActivity.this, "Мережа: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<MultipartBody.Part> createInstructionImageParts(List<InstructionCreateDto> instructions) {
        List<MultipartBody.Part> parts = new ArrayList<>();

        for (int i = 0; i < instructions.size(); i++) {
            Uri uri = instructions.get(i).getImageUri();
            if (uri != null) {
                File file = new File(FileUtils.getPath(this, uri));
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                parts.add(MultipartBody.Part.createFormData("InstructionImages", file.getName(), requestFile));
            }
        }

        return parts;
    }

    private List<IngredientCreateDto> collectIngredients() {
        return ingredientAdapter.getItems();
    }

    private List<InstructionCreateDto> collectInstructions() {
        return instructionAdapter.getItems();
    }
}