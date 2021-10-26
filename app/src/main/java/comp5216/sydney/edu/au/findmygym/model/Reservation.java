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
    private Integer trainerId;

    private int gymId;
    private String userId;

    private Timeslot timeslot;

    @SuppressLint("DefaultLocale")
    public Reservation(String userId, int gymId, Integer trainerId, Timeslot timeslot) {
        this.userId = userId;
        this.gymId = gymId;
        this.trainerId = trainerId;
        this.timeslot = timeslot;

        this.rsvId = String.format("%s+%d+%s", userId, gymId, timeslot.toDatabaseString());
    }

    public static Reservation fromData(ReservationData data) {
        return new Reservation(data.userId, data.gymId, data.trainerId,
                Timeslot.fromDatabaseString(data.timeslot));
    }

    public ReservationData toData() {
        ReservationData data = new ReservationData();
        data.userId = userId;
        data.gymId = gymId;
        data.trainerId = trainerId;
        data.timeslot = timeslot.toDatabaseString();
        return data;
    }

    public String getRsvId() {
        return rsvId;
    }

    public int getGymId() {
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
    public Integer getTrainerId() {
        return trainerId;
    }

    /**
     * @return the timeslot of this reservation
     */
    public Timeslot getSelectedTimeSlot() {
        return timeslot;
    }

    public static class ReservationData {
        String userId;
        int gymId;
        Integer trainerId;
        String timeslot;
    }
}
