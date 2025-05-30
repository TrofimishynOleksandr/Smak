package com.itstep.smakmobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.itstep.smakmobile.dto.AvatarResponse;
import com.itstep.smakmobile.dto.UserInfoDto;
import com.itstep.smakmobile.service.ApiClient;
import com.itstep.smakmobile.service.UserApiService;
import com.itstep.smakmobile.utils.FileUtils;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends BaseActivity {

    private ImageView avatarImageView;
    private TextView userNameText, userEmailText;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private UserApiService apiService;

    private static final String BASE_URL = "http://192.168.0.103:5127/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupHeader();

        avatarImageView = findViewById(R.id.avatarImageView);
        userNameText = findViewById(R.id.userNameText);
        userEmailText = findViewById(R.id.userEmailText);
        Button uploadButton = findViewById(R.id.uploadAvatarButton);
        Button removeButton = findViewById(R.id.removeAvatarButton);

        apiService = ApiClient.getClient(this).create(UserApiService.class);

        loadUserData();

        uploadButton.setOnClickListener(v -> openFilePicker());
        removeButton.setOnClickListener(v -> removeAvatar());
    }

    private void loadUserData() {
        apiService.getMe().enqueue(new Callback<UserInfoDto>() {
            @Override
            public void onResponse(Call<UserInfoDto> call, Response<UserInfoDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserInfoDto user = response.body();
                    userNameText.setText("Ім’я: " + user.getName());
                    userEmailText.setText("Email: " + user.getEmail());
                    Picasso.get()
                            .load(BASE_URL + user.getAvatarUrl())
                            .placeholder(R.drawable.avatar_placeholder)
                            .into(avatarImageView);
                }
            }

            @Override
            public void onFailure(Call<UserInfoDto> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Помилка завантаження", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void removeAvatar() {
        apiService.deleteAvatar().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    avatarImageView.setImageResource(R.drawable.avatar_placeholder);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Помилка видалення", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                uploadAvatar(selectedImageUri);
            }
        }
    }

    private void uploadAvatar(Uri imageUri) {
        try {
            File file = FileUtils.getFileFromUri(this, imageUri);
            String mimeType = getContentResolver().getType(imageUri);
            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);

            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

            apiService.uploadAvatar(body).enqueue(new Callback<AvatarResponse>() {
                @Override
                public void onResponse(Call<AvatarResponse> call, Response<AvatarResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Picasso.get()
                                .load(BASE_URL + response.body().getAvatarUrl())
                                .placeholder(R.drawable.avatar_placeholder)
                                .into(avatarImageView);
                        Toast.makeText(ProfileActivity.this, "Аватар оновлено", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Помилка: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<AvatarResponse> call, Throwable t) {
                    Toast.makeText(ProfileActivity.this, "Помилка завантаження: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Не вдалося обробити зображення", Toast.LENGTH_SHORT).show();
        }
    }
}
