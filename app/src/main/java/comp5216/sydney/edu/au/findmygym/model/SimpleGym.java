package comp5216.sydney.edu.au.findmygym.model;

import androidx.annotation.NonNull;

public class SimpleGym {

    public final String gymId;
    public final String gymName;
    public final double longitude;
    public final double latitude;

    private SimpleGym(String gymId, String gymName, double longitude, double latitude) {
        this.gymId = gymId;
        this.gymName = gymName;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public static SimpleGym fromData(Gym.GymData data) {
        return new SimpleGym(
                data.gymId,
                data.name,
                data.longitude,
                data.latitude);
    }

    @NonNull
    @Override
    public String toString() {
        return "SimpleGym{" +
                "gymId=" + gymId +
                ", gymName='" + gymName + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
