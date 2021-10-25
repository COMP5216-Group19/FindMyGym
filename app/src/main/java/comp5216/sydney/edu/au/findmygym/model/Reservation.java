package comp5216.sydney.edu.au.findmygym.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;

/**
 * A class that records a personal trainer reservation.
 */
public class Reservation implements Serializable {

    // null if no trainer reserved
    private Integer trainerId;

    private int gymId;
    private int userId;

    private Timeslot timeslot;

    public Reservation(int userId, int gymId, Integer trainerId, Timeslot timeslot) {
        this.userId = userId;
        this.gymId = gymId;
        this.trainerId = trainerId;
        this.timeslot = timeslot;
    }

    public PersonalTrainer getTrainer() {
        // todo: 这里有点强行
        return UserData.getInstance().findTrainerById(trainerId);
    }

    public int getGymId() {
        return gymId;
    }

    public int getUserId() {
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
}
