package com.itstep.smakmobile.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itstep.smakmobile.R;
import com.itstep.smakmobile.dto.InstructionCreateDto;
import com.itstep.smakmobile.utils.SimpleTextWatcher;

import java.util.List;

public class InstructionStepCreateAdapter extends RecyclerView.Adapter<InstructionStepCreateAdapter.InstructionViewHolder> {

    private final List<InstructionCreateDto> instructions;
    private final Activity activity;
    private final int PICK_IMAGE_REQUEST = 1000;

    public InstructionStepCreateAdapter(Activity activity, List<InstructionCreateDto> instructions) {
        this.activity = activity;
        this.instructions = instructions;
    }

    public void addEmpty() {
        instructions.add(new InstructionCreateDto("", null));
        notifyItemInserted(instructions.size() - 1);
    }

    public List<InstructionCreateDto> getInstructions() {
        return instructions;
    }

    @NonNull
    @Override
    public InstructionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_create_instruction, parent, false);
        return new InstructionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionViewHolder holder, int position) {
        InstructionCreateDto model = instructions.get(position);

        holder.stepDescription.setText(model.getDescription());
        holder.stepDescription.addTextChangedListener(new SimpleTextWatcher(() ->
                model.setDescription(holder.stepDescription.getText().toString())
        ));

        if (model.getImageUri() != null) {
            holder.stepImage.setImageURI(model.getImageUri());
        }

        holder.stepImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            activity.startActivityForResult(intent, PICK_IMAGE_REQUEST + position);
        });
    }

    @Override
    public int getItemCount() {
        return instructions.size();
    }

    public void setImageForStep(int position, Uri uri) {
        if (position >= 0 && position < instructions.size()) {
            instructions.get(position).setImageUri(uri);
            notifyItemChanged(position);
        }
    }

    static class InstructionViewHolder extends RecyclerView.ViewHolder {
        EditText stepDescription;
        ImageView stepImage;

        public InstructionViewHolder(@NonNull View itemView) {
            super(itemView);
            stepDescription = itemView.findViewById(R.id.editStepDescription);
            stepImage = itemView.findViewById(R.id.imageStep);
        }
    }

    public List<InstructionCreateDto> getItems() {
        return instructions;
    }
}
