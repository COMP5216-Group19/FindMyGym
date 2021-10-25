package comp5216.sydney.edu.au.findmygym.model.callbacks;

import comp5216.sydney.edu.au.findmygym.model.Gym;

public interface GymQueryCallback {

    void onSucceed(Gym gym);

    void onFailed(Exception exception);
}
