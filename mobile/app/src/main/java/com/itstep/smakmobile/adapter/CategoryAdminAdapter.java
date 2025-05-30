package com.itstep.smakmobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.itstep.smakmobile.R;
import com.itstep.smakmobile.dto.CategoryDto;
import com.itstep.smakmobile.service.AdminApiService;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryAdminAdapter extends RecyclerView.Adapter<CategoryAdminAdapter.ViewHolder> {

    private List<CategoryDto> categories;
    private AdminApiService adminApi;
    private Runnable reloadCallback;

    private static final String BASE_URL = "http://192.168.0.103:5127/";

    public CategoryAdminAdapter(List<CategoryDto> categories, AdminApiService adminApi, Runnable reloadCallback) {
        this.categories = categories;
        this.adminApi = adminApi;
        this.reloadCallback = reloadCallback;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        ImageView imageView;
        Button deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.categoryNameText);
            imageView = itemView.findViewById(R.id.categoryImage);
            deleteButton = itemView.findViewById(R.id.deleteCategoryButton);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CategoryDto category = categories.get(position);
        holder.nameText.setText(category.name);
        Picasso.get()
                .load(BASE_URL + category.imageUrl)
                .placeholder(R.drawable.category_placeholder)
                .into(holder.imageView);

        holder.deleteButton.setOnClickListener(v -> {
            adminApi.deleteCategory(category.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Toast.makeText(v.getContext(), "Категорію видалено", Toast.LENGTH_SHORT).show();
                    reloadCallback.run();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(v.getContext(), "Помилка", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}

