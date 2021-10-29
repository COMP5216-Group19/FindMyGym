package comp5216.sydney.edu.au.findmygym.model;

import java.io.Serializable;
import java.util.Calendar;

/**
 * A class that represents a review item.
 */
public class Review implements Serializable {

    public int rating;
    private String reviewId;
    private String gymId;
    private String userId;
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
//        this.reviewId = String.format("%s+%s", userId, CalendarUtil.calendarToString(dateTime));
    }

    public String getGymId() {
        return gymId;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
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
}
