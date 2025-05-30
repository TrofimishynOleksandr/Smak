package com.itstep.smakmobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itstep.smakmobile.ChefProfileActivity;
import com.itstep.smakmobile.R;
import com.itstep.smakmobile.dto.ChefDto;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChefAdapter extends RecyclerView.Adapter<ChefAdapter.ChefViewHolder> {

    private Context context;
    private List<ChefDto> chefs;
    private static final String BASE_URL = "http://192.168.0.103.2:5127/";

    public ChefAdapter(Context context, List<ChefDto> chefs) {
        this.context = context;
        this.chefs = chefs;
    }

    @NonNull
    @Override
    public ChefViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chef, parent, false);
        return new ChefViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChefViewHolder holder, int position) {
        ChefDto chef = chefs.get(position);
        holder.name.setText(chef.getName());
        Picasso.get()
                .load(BASE_URL + chef.getAvatarUrl())
                .placeholder(R.drawable.avatar_placeholder)
                .fit()
                .centerCrop()
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChefProfileActivity.class);
            intent.putExtra("chefId", chef.getId().toString());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chefs.size();
    }

    static class ChefViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;

        public ChefViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.chefImage);
            name = itemView.findViewById(R.id.chefName);
        }
    }
}

