package comp5216.sydney.edu.au.findmygym.model;

import java.util.List;
import java.util.Map;

/**
 * User data structure in firebase
 */
public class UserDataContent {

    public List<Reservation.ReservationData> reservations;
    public List<Map<String, String>> memberships;
    public List<String> favouriteGyms;
}
