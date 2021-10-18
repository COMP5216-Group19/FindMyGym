package comp5216.sydney.edu.au.findmygym.model;

import java.util.List;

public class PersonalTrainerInfo {

    /**
     * Name of this trainer
     */
    private final String name;

    /**
     * Available timeslots of this trainer
     */
    private final List<TimeSlot> availableTimes;

    public PersonalTrainerInfo(String name, List<TimeSlot> availableTimes) {
        this.name = name;
        this.availableTimes = availableTimes;
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
