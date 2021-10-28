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

    @SuppressLint("DefaultLocale")
    public Reservation(String userId, String gymId, String trainerId, int price,
                       Timeslot timeslot) {
        this.userId = userId;
        this.gymId = gymId;
        this.trainerId = trainerId;
        this.timeslot = timeslot;
        this.price = price;

        this.rsvId = String.format("%s+%s+%s", userId, gymId, timeslot.toDatabaseString());
    }

    public static Reservation fromData(ReservationData data) {
        return new Reservation(data.userId, data.gymId,
                data.trainerId.isEmpty() ? null : data.trainerId,
                data.price,
                Timeslot.fromDatabaseString(data.timeslot));
    }

    public ReservationData toData() {
        ReservationData data = new ReservationData();
        data.userId = userId;
        data.gymId = gymId;
        data.trainerId = trainerId == null ? "" : trainerId;
        data.timeslot = timeslot.toDatabaseString();
        data.price = price;
        return data;
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

    //    public Reservation(PersonalTrainer trainer, Timeslot selectedTimeslot) {
//        this.trainer = trainer;
//        this.selectedTimeslot = selectedTimeslot;
//    }

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

    public static class ReservationData {
        public String userId;
        public String gymId;
        public String trainerId;
        public int price;
        public String timeslot;
    }
}
