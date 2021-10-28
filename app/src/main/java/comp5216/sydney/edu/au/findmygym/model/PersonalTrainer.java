package comp5216.sydney.edu.au.findmygym.model;

import android.graphics.Bitmap;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PersonalTrainer implements Serializable {

    /**
     * Name of this trainer
     */
    private final String name;

    private final String trainerId;
    private double price;
    private Bitmap avatar;

    /**
     * Available timeslots of this trainer
     */
    private final List<Timeslot> availableTimes;

    public PersonalTrainer(String trainerId, String name, double price, List<Timeslot> availableTimes) {
        this.trainerId = trainerId;
        this.name = name;
        this.availableTimes = availableTimes;
        this.price = price;
    }

    public PersonalTrainer(String trainerId, String name, double price) {
        this(trainerId, name, price, new ArrayList<>());
    }

    public static PersonalTrainer fromData(TrainerData data, Bitmap avatar) {
        List<Timeslot> times = new ArrayList<>();
        if (data.availableTimes != null) {
            for (String s : data.availableTimes) {
                times.add(Timeslot.fromDatabaseString(s));
            }
        }

        PersonalTrainer pt = new PersonalTrainer(
                data.trainerId,
                data.name,
                data.price,
                times
        );
        pt.setAvatar(avatar);
        return pt;
    }

    public TrainerData toData(String avatarPath) {
        TrainerData data = new TrainerData();
        data.trainerId = String.valueOf(trainerId);
        data.name = name;
        data.price = price;
        data.avatarPath = avatarPath;
        data.availableTimes = getAvailableTimesDbStrings();
        return data;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Adds a timeslot to the list.
     *
     * @param timeSlot the object to be added
     */
    public void addTimeSlot(Timeslot timeSlot) {
        availableTimes.add(timeSlot);
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    /**
     * @return id of this trainer
     */
    public String getTrainerId() {
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

    public List<String> getAvailableTimesDbStrings() {
        List<String> list = new ArrayList<>();
        for (Timeslot ts : availableTimes) {
            list.add(ts.toDatabaseString());
        }
        return list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonalTrainer that = (PersonalTrainer) o;
        return trainerId.equals(that.trainerId) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, trainerId);
    }

    @NonNull
    @Override
    public String toString() {
        return "PersonalTrainer{" +
                "name='" + name + '\'' +
                ", availableTimes=" + availableTimes +
                '}';
    }

    public static class TrainerData {
        public String name;
        public String trainerId;
        public double price;
        public String avatarPath;
        public List<String> availableTimes;
    }
}
