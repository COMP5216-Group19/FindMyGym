package comp5216.sydney.edu.au.findmygym.model;

public class SimpleGym {

    private int gymId;
    private String name;
    private double longitude;
    private double latitude;

    public SimpleGym(int gymId, String name, double longitude, double latitude) {
        this.gymId = gymId;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public int getGymId() {
        return gymId;
    }

    public String getName() {
        return name;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
