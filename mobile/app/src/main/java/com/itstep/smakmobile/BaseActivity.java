package com.itstep.smakmobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;

import com.itstep.smakmobile.auth.AuthManager;

public abstract class BaseActivity extends AppCompatActivity {

    protected AuthManager authManager;
    protected Button profileButton;
    protected EditText searchEditText;
    protected TextView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authManager = new AuthManager(this);
    }

    protected void setupHeader() {
        profileButton = findViewById(R.id.profileButton);
        searchEditText = findViewById(R.id.searchEditText);
        logo = findViewById(R.id.logo);

        if (authManager.isAuthenticated()) {
            profileButton.setText(authManager.getUserName());
            profileButton.setOnClickListener(this::showPopupMenu);
        } else {
            profileButton.setText("Увійти");
            profileButton.setVisibility(View.VISIBLE);
            profileButton.setOnClickListener(v -> {
                new LoginDialogFragment().show(getSupportFragmentManager(), "loginDialog");
            });
        }

        logo.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = searchEditText.getText().toString();
                if (!query.isEmpty()) {
                    Intent intent = new Intent(this, SearchActivity.class);
                    intent.putExtra("searchQuery", query);
                    this.startActivity(intent);
                    searchEditText.setText("");
                    searchEditText.clearFocus();
                }
                return true;
            }
            return false;
        });
    }

    private void showPopupMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.getMenuInflater().inflate(R.menu.header_popup_menu, popupMenu.getMenu());
        MenuCompat.setGroupDividerEnabled(popupMenu.getMenu(), true);

        if (!authManager.isAuthenticated()) {
            popupMenu.getMenu().clear();
        } else {
            if (!authManager.isChef()) {
                popupMenu.getMenu().removeItem(R.id.my_recipes);
                popupMenu.getMenu().removeItem(R.id.create_recipe);
            }
            if (!authManager.isAdmin()) {
                popupMenu.getMenu().removeItem(R.id.admin_panel);
            }
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else if (id == R.id.favorites) {
                startActivity(new Intent(this, FavoriteRecipesActivity.class));
            } else if (id == R.id.my_recipes) {
                startActivity(new Intent(this, MyRecipesActivity.class));
            } else if (id == R.id.create_recipe) {
                startActivity(new Intent(this, CreateRecipeActivity.class));
            } else if (id == R.id.admin_panel) {
                startActivity(new Intent(this, AdminPanelActivity.class));
            } else if (id == R.id.logout) {
                authManager.logout();
                Intent intent = new Intent(this, MainActivity.class);
                this.startActivity(intent);
                recreate();
            }
            return true;
        });

        popupMenu.show();
    }
}

