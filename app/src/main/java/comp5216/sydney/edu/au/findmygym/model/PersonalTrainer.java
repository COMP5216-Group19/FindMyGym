package comp5216.sydney.edu.au.findmygym.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class PersonalTrainer {

    /**
     * Name of this trainer
     */
    private final String name;

    private final int trainerId;

    /**
     * Available timeslots of this trainer
     */
    private final List<Timeslot> availableTimes;

    public PersonalTrainer(int trainerId, String name, List<Timeslot> availableTimes) {
        this.trainerId = trainerId;
        this.name = name;
        this.availableTimes = availableTimes;
    }

    public PersonalTrainer(int trainerId, String name) {
        this(trainerId, name, new ArrayList<>());
    }

    /**
     * Adds a timeslot to the list.
     *
     * @param timeSlot the object to be added
     */
    public void addTimeSlot(Timeslot timeSlot) {
        availableTimes.add(timeSlot);
    }

    /**
     * @return id of this trainer
     */
    public int getTrainerId() {
        return trainerId;
    }

    /**
     * @return name of this trainer
     */
    public String getName() {
        return name;
    }

    /**
     * @return list of available timeslots of this trainer
     */
    public List<Timeslot> getAvailableTimes() {
        return availableTimes;
    }

    @NonNull
    @Override
    public String toString() {
        return "PersonalTrainer{" +
                "name='" + name + '\'' +
                ", availableTimes=" + availableTimes +
                '}';
    }
}
