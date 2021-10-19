package comp5216.sydney.edu.au.findmygym.model;

import androidx.annotation.NonNull;

/**
 * A class that records a personal trainer reservation.
 */
public class Reservation {

    private final PersonalTrainer trainer;
    private final Timeslot selectedTimeslot;

    public Reservation(PersonalTrainer trainer, Timeslot selectedTimeslot) {
        this.trainer = trainer;
        this.selectedTimeslot = selectedTimeslot;
    }

    /**
     * @return the personal trainer to reserve
     */
    public PersonalTrainer getTrainer() {
        return trainer;
    }

    /**
     * @return the timeslot of this reservation
     */
    public Timeslot getSelectedTimeSlot() {
        return selectedTimeslot;
    }

    @NonNull
    @Override
    public String toString() {
        return "Reservation{" +
                "trainer=" + trainer +
                ", selectedTimeSlot=" + selectedTimeslot +
                '}';
    }
}
