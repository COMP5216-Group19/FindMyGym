package comp5216.sydney.edu.au.findmygym.model.callbacks;

import comp5216.sydney.edu.au.findmygym.model.PersonalTrainer;

public interface TrainerQueryCallback {
    void onSucceed(PersonalTrainer trainer);

    void onFailed(Exception exception);
}
