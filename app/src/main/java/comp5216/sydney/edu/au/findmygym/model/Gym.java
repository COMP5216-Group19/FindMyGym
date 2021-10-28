package comp5216.sydney.edu.au.findmygym.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Class that represents a gym, passing data from map activity to gym activity.
 */
public class Gym implements Serializable {

    private final String gymId;

    /**
     * Name of this gym
     */
    private final String gymName;

    /**
     * List of personal trainers
     */
    private List<PersonalTrainer> personalTrainers;

    /**
     * List of user reviews
     */
    private List<Review> reviews;

    /**
     * The following two attributes represents the opening hours
     */
    private Calendar openTime;
    private Calendar closeTime;

    private String address;
    private String contact;

    private List<String> equipments;
    private double longitude;
    private double latitude;

    private int price;

    public Gym(String gymId,
               String gymName,
               List<PersonalTrainer> personalTrainers,
               Calendar openTime,
               Calendar closeTime,
               int price,
               String address,
               String contact,
               double longitude,
               double latitude,
               List<String> equipments,
               List<Review> reviews) {
        this.gymId = gymId;
        this.gymName = gymName;
        this.personalTrainers = personalTrainers;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.address = address;
        this.contact = contact;
        this.equipments = equipments;
        this.reviews = reviews;
        this.longitude = longitude;
        this.latitude = latitude;
        this.price = price;
    }

    public Gym(String gymId,
               String gymName,
               Calendar openTime,
               Calendar closeTime,
               int price,
               String address,
               String contact,
               double longitude,
               double latitude) {
        this(gymId,
                gymName,
                new ArrayList<>(),
                openTime,
                closeTime,
                price,
                address,
                contact,
                longitude,
                latitude,
                new ArrayList<>(),
                new ArrayList<>());
    }

    public static Gym fromGymData(GymData gymData,
                                  List<PersonalTrainer> trainers,
                                  List<Review> reviews) {
        List<String> equipments;
        if (gymData.equipments == null) {
            equipments = new ArrayList<>();
        } else {
            equipments = gymData.equipments;
        }

        Gym gym = new Gym(
                gymData.gymId,
                gymData.name,
                trainers,
                CalendarUtil.stringToCalendar(gymData.openTime),
                CalendarUtil.stringToCalendar(gymData.closeTime),
                gymData.price,
                gymData.address,
                gymData.contact,
                gymData.longitude,
                gymData.latitude,
                equipments,
                reviews
        );
        return gym;
    }

    public GymData toData(String picturePath) {
        GymData data = new GymData();
        data.gymId = String.valueOf(gymId);
        data.name = gymName;
        data.address = address;
        data.contact = contact;
        data.openTime = CalendarUtil.calendarToString(openTime);
        data.closeTime = CalendarUtil.calendarToString(closeTime);
        data.longitude = longitude;
        data.latitude = latitude;
        data.price = price;
        data.picturePath = picturePath;
        data.equipments = new ArrayList<>(equipments);
        data.trainerIds = new ArrayList<>();
        for (PersonalTrainer trainer : personalTrainers) {
            data.trainerIds.add(trainer.getTrainerId());
        }
        data.reviewIds = new ArrayList<>();
        for (Review review : reviews) {
            data.reviewIds.add(review.getReviewId());
        }
        return data;
    }

    public PersonalTrainer findTrainerById(String tid) {
        if (tid == null) return null;
        for (PersonalTrainer trainer : personalTrainers) {
            if (tid.equals(trainer.getTrainerId())) return trainer;
        }
        return null;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    /**
     * @return the id of this gym
     */
    public String getGymId() {
        return gymId;
    }

    /**
     * @return the name of this gym
     */
    public String getGymName() {
        return gymName;
    }

    /**
     * @return whether this gym is marked as "favourite"
     */
    public boolean isFavourite() {
        List<String> favouriteGyms = UserData.getInstance().getFavouriteGyms();
        for (String gid : favouriteGyms) {
            if (gid.equals(gymId)) return true;
        }
        return false;
    }

    /**
     * @return a list of current available personal trainers
     */
    public List<PersonalTrainer> getPersonalTrainers() {
        return personalTrainers;
    }

    /**
     * @return string representation of open time, e.g. 9AM
     */
    public Calendar getOpenTime() {
        return openTime;
    }

    /**
     * @return string representation of close time, e.g. 6PM
     */
    public Calendar getCloseTime() {
        return closeTime;
    }

    /**
     * @return average user rating of this gym
     */
    public double getAvgRating() {
        if (reviews.isEmpty()) return 5.0;
        double total = 0.0;
        for (Review review : reviews) {
            total += review.getRating();
        }
        return total / reviews.size();
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

    /**
     * @return list of users' reviews
     */
    public List<Review> getReviews() {
        return reviews;
    }

    public Calendar getTodayCloseTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, closeTime.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, closeTime.get(Calendar.MINUTE));
        calendar.get(Calendar.HOUR);
        return closeTime;
    }

    public static class GymData {
        public String gymId;
        public String name;
        public String address;
        public String contact;
        public String openTime;
        public String closeTime;
        public String picturePath;
        public double longitude;
        public double latitude;
        public int price;
        public List<String> trainerIds;
        public List<String> equipments;
        public List<String> reviewIds;
    }
}
