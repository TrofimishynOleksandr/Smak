package com.itstep.smakmobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.itstep.smakmobile.adapter.IngredientAdapter;
import com.itstep.smakmobile.adapter.InstructionStepAdapter;
import com.itstep.smakmobile.adapter.ReviewAdapter;
import com.itstep.smakmobile.auth.AuthManager;
import com.itstep.smakmobile.dto.RecipeDetailsDto;
import com.itstep.smakmobile.dto.ReviewRequest;
import com.itstep.smakmobile.service.ApiClient;
import com.itstep.smakmobile.service.RecipeApiService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeDetailsActivity extends BaseActivity {

    private ImageView recipeImage;
    private TextView recipeTitle, recipeDescription, recipeTime, authorLink;
    private ImageButton btnLike;
    private EditText commentEditText;
    private Button submitCommentButton;
    private RecyclerView ingredientsRecycler, instructionsRecycler, reviewsRecycler;
    private ImageView[] starViews;
    private int selectedRating = 0;
    private ImageView[] ratingStars;


    private UUID recipeId;
    private boolean isFavorite;
    private RecipeApiService recipeApiService;
    private AuthManager authManager;

    private ReviewAdapter reviewAdapter;
    private List<com.itstep.smakmobile.dto.ReviewDto> reviewList = new ArrayList<>();

    private static final String BASE_URL = "http://192.168.0.103:5127/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        setupHeader();

        authManager = new AuthManager(this);
        recipeApiService = ApiClient.getClient(this).create(RecipeApiService.class);

        recipeImage = findViewById(R.id.recipeImage);
        recipeTitle = findViewById(R.id.recipeTitle);
        recipeDescription = findViewById(R.id.recipeDescription);
        recipeTime = findViewById(R.id.recipeTime);
        authorLink = findViewById(R.id.authorLink);
        btnLike = findViewById(R.id.btnLike);
        commentEditText = findViewById(R.id.commentEditText);
        submitCommentButton = findViewById(R.id.submitCommentButton);

        ingredientsRecycler = findViewById(R.id.ingredientsRecycler);
        instructionsRecycler = findViewById(R.id.instructionsRecycler);
        reviewsRecycler = findViewById(R.id.reviewsRecycler);

        ingredientsRecycler.setLayoutManager(new LinearLayoutManager(this));
        instructionsRecycler.setLayoutManager(new LinearLayoutManager(this));
        reviewsRecycler.setLayoutManager(new LinearLayoutManager(this));

        starViews = new ImageView[] {
                findViewById(R.id.star1),
                findViewById(R.id.star2),
                findViewById(R.id.star3),
                findViewById(R.id.star4),
                findViewById(R.id.star5)
        };

        ratingStars = new ImageView[]{
                findViewById(R.id.ratingStar1),
                findViewById(R.id.ratingStar2),
                findViewById(R.id.ratingStar3),
                findViewById(R.id.ratingStar4),
                findViewById(R.id.ratingStar5)
        };

        for (int i = 0; i < ratingStars.length; i++) {
            final int index = i;
            ratingStars[i].setOnClickListener(v -> {
                selectedRating = index + 1;
                updateReviewRatingStars();
            });
        }

        String recipeIdStr = getIntent().getStringExtra("recipeId");
        if (recipeIdStr != null) {
            recipeId = UUID.fromString(recipeIdStr);
            loadRecipeDetails();
        }

        btnLike.setOnClickListener(v -> toggleFavorite());
        submitCommentButton.setOnClickListener(v -> submitComment());

        EditText reviewInput = findViewById(R.id.commentEditText);
        reviewInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                submitComment();
                return true;
            }
            return false;
        });
    }

    private void loadRecipeDetails() {
        recipeApiService.getRecipeDetails(recipeId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<RecipeDetailsDto> call, Response<RecipeDetailsDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bindRecipeDetails(response.body());
                } else {
                    Toast.makeText(RecipeDetailsActivity.this, "Не вдалося завантажити рецепт", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RecipeDetailsDto> call, Throwable t) {
                Toast.makeText(RecipeDetailsActivity.this, "Помилка з'єднання", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindRecipeDetails(RecipeDetailsDto dto) {
        recipeTitle.setText(dto.title);
        recipeDescription.setText(dto.description);
        recipeTime.setText(dto.cookTimeMinutes + " хв.");
        authorLink.setText(dto.authorName);
        authorLink.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChefProfileActivity.class);
            intent.putExtra("chefId", dto.authorId.toString());
            startActivity(intent);
        });

        isFavorite = dto.isFavorite;
        btnLike.setImageResource(isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart_empty);

        Picasso.get()
                .load(BASE_URL + dto.imageUrl)
                .placeholder(R.drawable.recipe_placeholder)
                .into(recipeImage);

        updateRatingStars(dto.averageRating);

        ingredientsRecycler.setAdapter(new IngredientAdapter(dto.ingredients));
        instructionsRecycler.setAdapter(new InstructionStepAdapter(dto.instructions));

        reviewList = new ArrayList<>(dto.reviews);
        reviewAdapter = new ReviewAdapter(this, reviewList, authManager.getUserId(), reviewId -> deleteReview(recipeId));
        reviewsRecycler.setAdapter(reviewAdapter);
    }

    private void updateRatingStars(double rating) {
        int fullStars = (int) rating;
        for (int i = 0; i < starViews.length; i++) {
            starViews[i].setImageResource(i < fullStars ? R.drawable.ic_star_filled : R.drawable.ic_star_empty);
        }
    }

    private void updateReviewRatingStars() {
        for (int i = 0; i < ratingStars.length; i++) {
            if (i < selectedRating) {
                ratingStars[i].setImageResource(R.drawable.ic_star_filled);
            } else {
                ratingStars[i].setImageResource(R.drawable.ic_star_empty);
            }
        }
    }

    private void toggleFavorite() {
        if (!authManager.isAuthenticated()) {
            Toast.makeText(this, "Увійдіть, щоб додати в улюблене", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean wasFavorite = isFavorite;
        isFavorite = !wasFavorite;
        btnLike.setImageResource(isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart_empty);

        Call<Void> call = isFavorite
                ? recipeApiService.addToFavorites(recipeId)
                : recipeApiService.removeFromFavorites(recipeId);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    isFavorite = wasFavorite;
                    btnLike.setImageResource(isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart_empty);
                    Toast.makeText(RecipeDetailsActivity.this, "Не вдалося оновити улюблене", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isFavorite = wasFavorite;
                btnLike.setImageResource(isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart_empty);
                Toast.makeText(RecipeDetailsActivity.this, "Помилка мережі", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitComment() {
        if (!authManager.isAuthenticated()) {
            Toast.makeText(this, "Увійдіть, щоб залишити відгук", Toast.LENGTH_SHORT).show();
            return;
        }

        String text = commentEditText.getText().toString().trim();

        ReviewRequest dto = new ReviewRequest(selectedRating, text);
        recipeApiService.addReview(recipeId, dto).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    commentEditText.setText("");
                    loadRecipeDetails();
                } else {
                    Toast.makeText(RecipeDetailsActivity.this, "Не вдалося додати коментар", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RecipeDetailsActivity.this, "Помилка з'єднання", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteReview(UUID recipeId) {
        recipeApiService.deleteReview(recipeId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    loadRecipeDetails();
                } else {
                    Toast.makeText(RecipeDetailsActivity.this, "Не вдалося видалити коментар", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RecipeDetailsActivity.this, "Помилка видалення", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
