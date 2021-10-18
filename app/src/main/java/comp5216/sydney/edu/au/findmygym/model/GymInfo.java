package comp5216.sydney.edu.au.findmygym.model;

import java.util.List;

/**
 * Class that represents a gym, passing data from map activity to gym activity.
 */
public class GymInfo {

    /**
     * Name of this gym
     */
    private String gymName;

    /**
     * List of personal trainers
     */
    private List<PersonalTrainerInfo> personalTrainers;

    /**
     * The following two attributes represents the opening hours
     */
    private String openTime;
    private String closeTime;

    /**
     * Average rating
     */
    private double avgRating;

    private String address;
    private String contact;

    private List<String> equipments;

    public GymInfo(String gymName,
                   List<PersonalTrainerInfo> personalTrainers,
                   String openTime,
                   String closeTime,
                   double avgRating,
                   String address,
                   String contact,
                   List<String> equipments) {
        this.gymName = gymName;
        this.personalTrainers = personalTrainers;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.avgRating = avgRating;
        this.address = address;
        this.contact = contact;
        this.equipments = equipments;
    }

    /**
     * @return the name of this gym
     */
    public String getGymName() {
        return gymName;
    }

    /**
     * @return a list of current available personal trainers
     */
    public List<PersonalTrainerInfo> getPersonalTrainers() {
        return personalTrainers;
    }

    /**
     * @return string representation of open time, e.g. 9AM
     */
    public String getOpenTime() {
        return openTime;
    }

    /**
     * @return string representation of close time, e.g. 6PM
     */
    public String getCloseTime() {
        return closeTime;
    }

    /**
     * @return average user rating of this gym
     */
    public double getAvgRating() {
        return avgRating;
    }

    /**
     * @return address of this gym
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return contact info of this gym
     */
    public String getContact() {
        return contact;
    }

    /**
     * @return list of available equipments of this gym
     */
    public List<String> getEquipments() {
        return equipments;
    }
}
