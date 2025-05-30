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
import com.itstep.smakmobile.dto.UserAdminDto;
import com.itstep.smakmobile.service.AdminApiService;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAdminAdapter extends RecyclerView.Adapter<UserAdminAdapter.ViewHolder> {

    private List<UserAdminDto> users;
    private AdminApiService adminApi;
    private Runnable reloadCallback;

    private static final String BASE_URL = "http://192.168.0.103:5127/";

    public UserAdminAdapter(List<UserAdminDto> users, AdminApiService adminApi, Runnable reloadCallback) {
        this.users = users;
        this.adminApi = adminApi;
        this.reloadCallback = reloadCallback;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, emailText, roleText;
        ImageView avatarImage;
        Button assignButton, deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            emailText = itemView.findViewById(R.id.emailText);
            roleText = itemView.findViewById(R.id.roleText);
            avatarImage = itemView.findViewById(R.id.avatarImage);
            assignButton = itemView.findViewById(R.id.assignButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserAdminDto user = users.get(position);
        holder.nameText.setText(user.name);
        holder.emailText.setText(user.email);
        holder.roleText.setText("Роль: " + user.role);
        Picasso.get()
                .load(BASE_URL + user.avatarUrl)
                .placeholder(R.drawable.avatar_placeholder)
                .into(holder.avatarImage);

        holder.assignButton.setVisibility(user.role.equals("Chef") ? View.GONE : View.VISIBLE);

        holder.assignButton.setOnClickListener(v -> {
            adminApi.assignChef(user.id).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Toast.makeText(v.getContext(), "Призначено поваром", Toast.LENGTH_SHORT).show();
                    reloadCallback.run();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(v.getContext(), "Помилка", Toast.LENGTH_SHORT).show();
                }
            });
        });

        holder.deleteButton.setOnClickListener(v -> {
            adminApi.deleteUser(user.id).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Toast.makeText(v.getContext(), "Видалено", Toast.LENGTH_SHORT).show();
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
        return users.size();
    }
}

