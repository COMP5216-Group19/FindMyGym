package comp5216.sydney.edu.au.findmygym.ui.gym;

import androidx.lifecycle.ViewModel;

import comp5216.sydney.edu.au.findmygym.model.Gym;

public class GymViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    TrainerListAdapter trainerListAdapter;

    Gym gym;

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}