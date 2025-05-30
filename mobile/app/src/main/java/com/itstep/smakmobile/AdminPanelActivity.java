package com.itstep.smakmobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.itstep.smakmobile.adapter.CategoryAdminAdapter;
import com.itstep.smakmobile.adapter.UserAdminAdapter;
import com.itstep.smakmobile.dto.CategoryDto;
import com.itstep.smakmobile.dto.UserAdminDto;
import com.itstep.smakmobile.service.AdminApiService;
import com.itstep.smakmobile.service.ApiClient;
import com.itstep.smakmobile.utils.FileUtils;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPanelActivity extends BaseActivity {

    private RecyclerView usersRecyclerView, categoriesRecyclerView;
    private EditText categoryNameEditText;
    private ImageView categoryPreviewImage;
    private Button selectImageButton, createCategoryButton;
    private Uri selectedImageUri;
    private AdminApiService adminApi;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        setupHeader();

        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        categoryNameEditText = findViewById(R.id.categoryNameEditText);
        categoryPreviewImage = findViewById(R.id.categoryPreviewImage);
        selectImageButton = findViewById(R.id.selectImageButton);
        createCategoryButton = findViewById(R.id.createCategoryButton);

        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adminApi = ApiClient.getClient(this).create(AdminApiService.class);

        loadUsers();
        loadCategories();

        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        createCategoryButton.setOnClickListener(v -> {
            try {
                createCategory();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void loadUsers() {
        adminApi.getAllUsers().enqueue(new Callback<List<UserAdminDto>>() {
            @Override
            public void onResponse(Call<List<UserAdminDto>> call, Response<List<UserAdminDto>> response) {
                if (response.isSuccessful()) {
                    UserAdminAdapter adapter = new UserAdminAdapter(response.body(), adminApi, () -> loadUsers());
                    usersRecyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<UserAdminDto>> call, Throwable t) {
                Toast.makeText(AdminPanelActivity.this, "Помилка завантаження користувачів", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCategories() {
        adminApi.getAllCategories().enqueue(new Callback<List<CategoryDto>>() {
            @Override
            public void onResponse(Call<List<CategoryDto>> call, Response<List<CategoryDto>> response) {
                if (response.isSuccessful()) {
                    CategoryAdminAdapter adapter = new CategoryAdminAdapter(response.body(), adminApi, () -> loadCategories());
                    categoriesRecyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<CategoryDto>> call, Throwable t) {
                Toast.makeText(AdminPanelActivity.this, "Помилка завантаження категорій", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createCategory() throws Exception {
        String name = categoryNameEditText.getText().toString().trim();
        if (name.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Введіть назву та оберіть зображення", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody namePart = RequestBody.create(MediaType.parse("text/plain"), name);

        File file = FileUtils.getFile(this, selectedImageUri);
        RequestBody imageRequest = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", file.getName(), imageRequest);

        adminApi.createCategory(namePart, imagePart).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminPanelActivity.this, "Категорію створено", Toast.LENGTH_SHORT).show();
                    categoryNameEditText.setText("");
                    categoryPreviewImage.setVisibility(View.GONE);
                    selectedImageUri = null;
                    loadCategories();
                } else {
                    Toast.makeText(AdminPanelActivity.this, "Помилка створення", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminPanelActivity.this, "Помилка", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            categoryPreviewImage.setImageURI(selectedImageUri);
            categoryPreviewImage.setVisibility(View.VISIBLE);
        }
    }
}