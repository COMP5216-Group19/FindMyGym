package comp5216.sydney.edu.au.findmygym.ui.gym;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.Utils.ImageUtil;
import comp5216.sydney.edu.au.findmygym.model.Gym;
import comp5216.sydney.edu.au.findmygym.model.Review;
import comp5216.sydney.edu.au.findmygym.model.Timeslot;
import comp5216.sydney.edu.au.findmygym.model.UserData;
import comp5216.sydney.edu.au.findmygym.model.callbacks.ObjectQueryCallback;

public class GymInfoFragment extends Fragment {

    private GymViewModel mViewModel;
    private DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT);

    TextInputEditText reviewText;
    RatingBar reviewRatingBar;
    LinearLayout reviewsList;
    Button postReviewButton;
    TextView gymAvgRating;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(requireActivity()).get(GymViewModel.class);
        mViewModel.setInfoFragment(this);

        return inflater.inflate(R.layout.gym_info_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        mViewModel = new ViewModelProvider(requireActivity()).get(GymViewModel.class);
        mViewModel.setInfoFragment(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView gymNameSmall = view.findViewById(R.id.gym_name);
        TextView gymOpenHrs = view.findViewById(R.id.gym_open_hrs);
        gymAvgRating = view.findViewById(R.id.gym_avg_rate);
        TextView gymAddress = view.findViewById(R.id.gym_address);
        TextView gymContact = view.findViewById(R.id.gym_contact);
        ChipGroup equipmentsContainer = view.findViewById(R.id.gym_equipments_group);
        ImageView gymImageView = view.findViewById(R.id.gym_image_view);
        reviewsList = view.findViewById(R.id.gym_reviews_list);

        reviewText = view.findViewById(R.id.gym_review_input);
        reviewRatingBar = view.findViewById(R.id.gym_review_rating);
        postReviewButton = view.findViewById(R.id.gym_post_review_button);
        TextInputLayout inputLayout = view.findViewById(R.id.gym_review_input_layout);

        mViewModel.enableCommentIfVisited(inputLayout, reviewText, reviewRatingBar);

        Gym gym = mViewModel.getGym();

        reviewText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                postReviewButton.setEnabled(s.length() > 0);
            }
        });

        gymNameSmall.setText(gym.getGymName());
        gymOpenHrs.setText(
                getString(R.string.gym_timeslot,
                        Timeslot.calendarToTimeInDay(getContext(), gym.getOpenTime()),
                        Timeslot.calendarToTimeInDay(getContext(), gym.getCloseTime())));
        gymAvgRating.setText(getString(R.string.gym_rate_format, gym.getAvgRating()));
        gymAddress.setText(gym.getAddress());
        gymContact.setText(gym.getContact());

        ImageUtil.loadImage(gym.getGymId(), gymImageView, getContext());

        int[] chipColorIds = new int[]{
                R.color.light_orange,
                R.color.teal_700,
                R.color.pink_red,
                R.color.light_green
        };
        equipmentsContainer.removeAllViews();

        for (String equip : gym.getEquipments()) {
            Chip chip = new Chip(view.getContext());
            int colorId = chipColorIds[(int) (Math.random() * chipColorIds.length)];
            chip.setChipBackgroundColor(
                    ColorStateList.valueOf(ContextCompat.getColor(view.getContext(), colorId)));
            chip.setTextColor(Color.WHITE);
            chip.setText(equip);

            equipmentsContainer.addView(chip);
        }

        refreshReviewsList();
    }

    void clearInputs() {
        reviewText.setText("");
        reviewRatingBar.setRating(5);
    }

    void newReviewPosted() {
        gymAvgRating.setText(getString(R.string.gym_rate_format,
                mViewModel.getGym().getAvgRating()));
        refreshReviewsList();
    }

    private void refreshReviewsList() {
        View postView = reviewsList.getChildAt(reviewsList.getChildCount() - 1);
        reviewsList.removeAllViews();
        reviewsList.addView(postView);

        List<Review> sortedReviews = mViewModel.getGym().getReviews().stream()
                .sorted(Comparator.comparing(Review::getDateTime)).collect(Collectors.toList());

        for (Review review : sortedReviews) {
            View itemView = makeReviewView(review, reviewsList);
            reviewsList.addView(itemView, 0);
            UserData.getInstance().getUsernameByUID(review.getUserId(),
                    new ObjectQueryCallback<String>() {
                @Override
                public void onSucceed(String object) {
                    TextView userNameText = itemView.findViewById(R.id.review_user_name);
                    userNameText.setText((String) object);
                }

                @Override
                public void onFailed(Exception e) {

                }
            });
        }
    }

    private View makeReviewView(Review review, LinearLayout parent) {
        View itemView = LayoutInflater.from(getContext())
                .inflate(R.layout.review_item_holder, parent, false);
        ImageView avatarView = itemView.findViewById(R.id.review_avatar_image);
        RatingBar ratingBar = itemView.findViewById(R.id.review_user_rating);
        TextView userNameText = itemView.findViewById(R.id.review_user_name);
        TextView timeText = itemView.findViewById(R.id.review_date);
        TextView commentText = itemView.findViewById(R.id.review_content);

        ImageUtil.loadAvatarByUid(review.getUserId(), avatarView, getContext());

        userNameText.setText(review.getUserId());
        ratingBar.setRating(review.getRating());
        timeText.setText(dateFormat.format(review.getDateTime().getTime()));
        commentText.setText(review.getComments());
        return itemView;
    }

    String getReview() {
        return reviewText.getText().toString();
    }

    int getRating() {
        return (int) reviewRatingBar.getRating();
    }
}
