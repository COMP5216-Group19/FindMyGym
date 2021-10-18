package comp5216.sydney.edu.au.findmygym.model;

import java.util.ArrayList;
import java.util.List;

public class PersonalTrainer {

    /**
     * Name of this trainer
     */
    private final String name;

    /**
     * Available timeslots of this trainer
     */
    private final List<TimeSlot> availableTimes;

    public PersonalTrainer(String name, List<TimeSlot> availableTimes) {
        this.name = name;
        this.availableTimes = availableTimes;
    }

    public PersonalTrainer(String name) {
        this(name, new ArrayList<>());
    }

    /**
     * Adds a timeslot to the list.
     *
     * @param timeSlot the object to be added
     */
    public void addTimeSlot(TimeSlot timeSlot) {
        availableTimes.add(timeSlot);
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
    public List<TimeSlot> getAvailableTimes() {
        return availableTimes;
    }
}
