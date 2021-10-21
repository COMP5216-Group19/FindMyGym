package comp5216.sydney.edu.au.findmygym.ui.gym;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.List;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.model.Review;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewViewHolder> {

    private final List<Review> reviews;

    public ReviewListAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item_holder, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        holder.bind(reviews.get(position));
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {

        private final ImageView avatarView;
        private final RatingBar ratingBar;
        private final TextView userNameText;
        private final TextView timeText;
        private final TextView commentText;
        private final DateFormat dateFormat;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            avatarView = itemView.findViewById(R.id.review_avatar_image);
            ratingBar = itemView.findViewById(R.id.review_user_rating);
            userNameText = itemView.findViewById(R.id.review_user_name);
            timeText = itemView.findViewById(R.id.review_date);
            commentText = itemView.findViewById(R.id.review_content);

            dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT);
        }

        void bind(Review review) {
            Bitmap avatar = review.getUserAvatar();
            if (avatar != null) {
                avatarView.setImageBitmap(avatar);
            }
            userNameText.setText(review.getUserName());
            ratingBar.setRating(review.getRating());
            timeText.setText(dateFormat.format(review.getDateTime().getTime()));
            commentText.setText(review.getComments());
        }
    }
}
