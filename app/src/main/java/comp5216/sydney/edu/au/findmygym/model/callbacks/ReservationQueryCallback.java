package comp5216.sydney.edu.au.findmygym.model.callbacks;

import comp5216.sydney.edu.au.findmygym.model.Reservation;

public interface ReservationQueryCallback {
    void onSucceed(Reservation reservation);

    void onFailed(Exception exception);
}
