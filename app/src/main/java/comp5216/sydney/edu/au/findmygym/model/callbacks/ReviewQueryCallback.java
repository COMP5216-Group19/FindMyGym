package comp5216.sydney.edu.au.findmygym.model.callbacks;

import comp5216.sydney.edu.au.findmygym.model.Review;

public interface ReviewQueryCallback {
    void onSucceed(Review review);

    void onFailed(Exception exception);
}
