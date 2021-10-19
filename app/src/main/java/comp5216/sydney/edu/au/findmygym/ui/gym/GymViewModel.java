package comp5216.sydney.edu.au.findmygym.ui.gym;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import comp5216.sydney.edu.au.findmygym.model.Gym;
import comp5216.sydney.edu.au.findmygym.model.PersonalTrainer;
import comp5216.sydney.edu.au.findmygym.model.TimeSlot;

public class GymViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private final String TAG = "[GymViewModel]";

    Gym gym;

    public GymViewModel() {
        // todo: for test
        addTestGym();
    }

    private void addTestGym() {
        gym = new Gym(0,
                "Gym A",
                "9AM",
                "6PM",
                4.8,
                "Blah Ave. Blah Unit",
                "12345678");
        gym.addEquipment("Dumbbell");
        gym.addEquipment("Climbing");
        gym.addEquipment("Rowing");
        gym.addEquipment("Treadmill");

        try {
            PersonalTrainer pt1 = new PersonalTrainer("Mark");
            pt1.addTimeSlot(TimeSlot.timeSlotOnToday("9AM", "10:30AM"));
            pt1.addTimeSlot(TimeSlot.timeSlotOnToday("1:00PM", "2:30PM"));
            pt1.addTimeSlot(TimeSlot.timeSlotOnToday("3PM", "4:30PM"));
            pt1.addTimeSlot(TimeSlot.timeSlotOnToday("5PM", "6PM"));
            gym.addTrainer(pt1);

            PersonalTrainer pt2 = new PersonalTrainer("Ada");
            pt2.addTimeSlot(TimeSlot.timeSlotOnToday("9AM", "11AM"));
            pt2.addTimeSlot(TimeSlot.timeSlotOnToday("1:00 PM", "3 PM"));
            gym.addTrainer(pt2);
        } catch (ParseException e) {
            Log.d(TAG, Arrays.toString(e.getStackTrace()));
            // TODO: delete this
            throw new RuntimeException(e);
        }
    }

    public void setGym(Gym gym) {
        this.gym = gym;
    }

    public Gym getGym() {
        return gym;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}