package comp5216.sydney.edu.au.findmygym.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Calendar;

import javax.annotation.Nullable;

/**
 * A class that represents a review item.
 */
public class Review implements Serializable {

    private String reviewId;
    private String gymId;
    public int rating;
    private String userId;
    private Bitmap userAvatar;
    private String comments;
    private Calendar dateTime;

    public Review(String userId, String gymId, int rating, String comments,
                  Calendar dateTime) {
        this.userId = userId;
        this.gymId = gymId;
        this.rating = rating;
        this.comments = comments;
        this.dateTime = dateTime;

        // Potential bug: two users with same name post reviews at a same millisecond
        this.reviewId = String.format("%s+%s", userId, CalendarUtil.calendarToString(dateTime));
    }

    public String getGymId() {
        return gymId;
    }

//    public static Review fromData(ReviewData data, Bitmap userAvatar) {
//        return new Review(
//                data.userName,
//                userAvatar,
//                data.rating,
//                data.comments,
//                CalendarUtil.stringToCalendar(data.dateTime)
//        );
//    }

    public ReviewData toData(String avatarPath) {
        ReviewData data = new ReviewData();
        data.avatarPath = avatarPath;
        data.userName = userId;
        data.rating = rating;
        data.dateTime = CalendarUtil.calendarToString(dateTime);
        data.comments = comments;
        return data;
    }

    public String getReviewId() {
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
    public String getUserId() {
        return userId;
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

    public static class ReviewData {
        public String userName;
        public int rating;
        public String comments;
        public String dateTime;
        public String avatarPath;
    }
}
