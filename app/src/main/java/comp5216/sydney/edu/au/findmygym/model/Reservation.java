package comp5216.sydney.edu.au.findmygym.model;

import android.annotation.SuppressLint;

import java.io.Serializable;

import comp5216.sydney.edu.au.findmygym.R;

/**
 * A class that records a personal trainer reservation.
 */
public class Reservation implements Serializable {

    private String rsvId;
    // null if no trainer reserved
    private String trainerId;

    private String gymId;
    private String userId;
    private int price;
    private Timeslot timeslot;
    
    public void setRsvId(String rsvId)
    {
        this.rsvId = rsvId;
    }
    
    @SuppressLint("DefaultLocale")
    public Reservation(String rsvId, String userId, String gymId, String trainerId, int price,
                       Timeslot timeslot) {
        this.userId = userId;
        this.gymId = gymId;
        this.trainerId = trainerId;
        this.timeslot = timeslot;
        this.price = price;

        this.rsvId = rsvId;
    }
    
    @Override
    public String toString()
    {
        return "Reservation{" +
                "rsvId='" + rsvId + '\'' +
                ", trainerId='" + trainerId + '\'' +
                ", gymId='" + gymId + '\'' +
                ", userId='" + userId + '\'' +
                ", price=" + price +
                ", timeslot=" + timeslot +
                '}';
    }

    public int getPrice() {
        return price;
    }

    public String getRsvId() {
        return rsvId;
    }

    public String getGymId() {
        return gymId;
    }

    public String getUserId() {
        return userId;
    }

    /**
     * @return the personal trainer to reserve
     */
    public String getTrainerId() {
        return trainerId;
    }

    /**
     * @return the timeslot of this reservation
     */
    public Timeslot getSelectedTimeSlot() {
        return timeslot;
    }
}
