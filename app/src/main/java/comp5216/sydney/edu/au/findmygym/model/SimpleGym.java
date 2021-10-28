package comp5216.sydney.edu.au.findmygym.model;

import androidx.annotation.NonNull;

public class SimpleGym {

    public final String gymId;
    public final String gymName;
    public final String address;
    public final double longitude;
    public final double latitude;

    public SimpleGym(String gymId, String gymName, String address, double longitude, double latitude) {
        this.gymId = gymId;
        this.gymName = gymName;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getGymId() {
        return gymId;
    }

    public String getGymName() {
        return gymName;
    }

    public String getAddress() {
        return address;
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
