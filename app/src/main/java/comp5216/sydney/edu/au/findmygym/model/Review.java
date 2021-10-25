package comp5216.sydney.edu.au.findmygym.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Calendar;

import javax.annotation.Nullable;

/**
 * A class that represents a review item.
 */
public class Review implements Serializable {

    private int reviewId;
    public int rating;
    private String userName;
    private Bitmap userAvatar;
    private String comments;
    private Calendar dateTime;

    public Review(int reviewId, String userName, Bitmap userAvatar, int rating, String comments,
                  Calendar dateTime) {
        this.reviewId = reviewId;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.rating = rating;
        this.comments = comments;
        this.dateTime = dateTime;
    }

    public int getReviewId() {
        return reviewId;
    }

    /**
     * @return the avatar of the user who wrote this review, nullable
     */
    @Nullable
    public Bitmap getUserAvatar() {
        return userAvatar;
    }

    /**
     * @return the name of the user who wrote this review
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @return the user's rating, ranged from 1 to 5
     */
    public int getRating() {
        return rating;
    }

    /**
     * @return the review text
     */
    public String getComments() {
        return comments;
    }

    /**
     * @return when this review had been post
     */
    public Calendar getDateTime() {
        return dateTime;
    }
}
