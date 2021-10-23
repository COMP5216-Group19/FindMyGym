package comp5216.sydney.edu.au.findmygym.ui.gym;

import comp5216.sydney.edu.au.findmygym.model.PersonalTrainer;
import comp5216.sydney.edu.au.findmygym.model.Reservation;
import comp5216.sydney.edu.au.findmygym.model.Timeslot;

public class TrainerReservation {

    public final PersonalTrainer trainer;
    public final Timeslot timeslot;

    TrainerReservation(PersonalTrainer trainer, Timeslot timeslot) {
        this.trainer = trainer;
        this.timeslot = timeslot;
    }
}
