package com.itstep.smakmobile.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itstep.smakmobile.R;
import com.itstep.smakmobile.dto.ReviewDto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private final Context context;
    private final List<ReviewDto> reviews;
    private final UUID currentUserId;
    private final OnReviewDeleteListener deleteListener;

    public interface OnReviewDeleteListener {
        void onDeleteReview(UUID reviewId);
    }

    public ReviewAdapter(Context context, List<ReviewDto> reviews, UUID currentUserId, OnReviewDeleteListener listener) {
        this.context = context;
        this.reviews = reviews;
        this.currentUserId = currentUserId;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReviewDto review = reviews.get(position);

        holder.reviewerName.setText(review.getAuthor());
        String comment;
        if (review.getComment().isEmpty()) {
            comment = "Без коментаря";
            SpannableString spannableString = new SpannableString(comment);
            spannableString.setSpan(new StyleSpan(Typeface.ITALIC), 0, comment.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.reviewText.setText(spannableString);
        }
        else {
            holder.reviewText.setText(review.getComment());
        }
        holder.reviewDate.setText(formatDate(review.getCreatedAt()));

        int rating = review.getRating();
        for (int i = 0; i < 5; i++) {
            holder.stars[i].setImageResource(i < rating ? R.drawable.ic_star_filled : R.drawable.ic_star_empty);
        }

        if (review.getAuthorId().equals(currentUserId)) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> deleteListener.onDeleteReview(review.getId()));
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView reviewerName, reviewText, reviewDate;
        ImageView[] stars;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewerName = itemView.findViewById(R.id.reviewerName);
            reviewText = itemView.findViewById(R.id.reviewText);
            reviewDate = itemView.findViewById(R.id.reviewDate);
            btnDelete = itemView.findViewById(R.id.btnDeleteReview);

            stars = new ImageView[]{
                    itemView.findViewById(R.id.star1),
                    itemView.findViewById(R.id.star2),
                    itemView.findViewById(R.id.star3),
                    itemView.findViewById(R.id.star4),
                    itemView.findViewById(R.id.star5),
            };
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return outputFormat.format(date);
    }
}
