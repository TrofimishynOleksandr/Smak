package com.itstep.smakmobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itstep.smakmobile.R;
import com.itstep.smakmobile.dto.InstructionStepDto;
import com.squareup.picasso.Picasso;

import java.util.List;

public class InstructionStepAdapter extends RecyclerView.Adapter<InstructionStepAdapter.ViewHolder> {
    private List<InstructionStepDto> steps;
    private static final String BASE_URL = "http://192.168.0.103:5127/";

    public InstructionStepAdapter(List<InstructionStepDto> steps) {
        this.steps = steps;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView stepText;
        ImageView stepImage;

        public ViewHolder(View view) {
            super(view);
            stepText = view.findViewById(R.id.stepNumber);
            stepImage = view.findViewById(R.id.stepImage);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_instruction_step, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        InstructionStepDto step = steps.get(position);
        holder.stepText.setText(String.format("%d) %s", step.getStepNumber(), step.getDescription()));

        String imageUrl = step.getImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            holder.stepImage.setVisibility(View.GONE);
        } else {
            holder.stepImage.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(BASE_URL + imageUrl)
                    .into(holder.stepImage);
        }
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }
}

