package comp5216.sydney.edu.au.findmygym.ui.gym;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import java.text.ParseException;
import java.util.Arrays;

import comp5216.sydney.edu.au.findmygym.model.Gym;
import comp5216.sydney.edu.au.findmygym.model.PersonalTrainer;
import comp5216.sydney.edu.au.findmygym.model.Reservation;
import comp5216.sydney.edu.au.findmygym.model.Timeslot;

public class GymViewModel extends ViewModel {
    private static final String TAG = "[GymViewModel]";
    /**
     * Adapter of reservation RecyclerView.
     * <p>
     * Store it here for simplifying the accessing from GymActivity
     */
    TrainerListAdapter trainerListAdapter;
    private Gym gym;

    public GymViewModel() {
        // todo: for test
        gym = testGym();
    }

    private static Gym testGym() {
        Gym gym = new Gym(0,
                "Gym A",
                "9 AM",
                "6 PM",
                4.8,
                "Blah Ave. Blah Unit",
                "12345678",
                false);
        gym.addEquipment("Barbell");
        gym.addEquipment("Bicycle");
        gym.addEquipment("Climbing");
        gym.addEquipment("Dumbbell");
        gym.addEquipment("Rowing");
        gym.addEquipment("Swimming");
        gym.addEquipment("Treadmill");

        try {
            PersonalTrainer pt1 = new PersonalTrainer(1, "Mark");
            pt1.addTimeSlot(Timeslot.timeSlotOnToday("9AM", "10:30AM"));
            pt1.addTimeSlot(Timeslot.timeSlotOnToday("1:00PM", "2:30PM"));
            pt1.addTimeSlot(Timeslot.timeSlotOnToday("3PM", "4:30PM"));
            pt1.addTimeSlot(Timeslot.timeSlotOnToday("5PM", "6PM"));
            gym.addTrainer(pt1);

            PersonalTrainer pt2 = new PersonalTrainer(2, "Ada");
            pt2.addTimeSlot(Timeslot.timeSlotOnToday("9AM", "11AM"));
            pt2.addTimeSlot(Timeslot.timeSlotOnToday("1:00 PM", "3 PM"));
            gym.addTrainer(pt2);
        } catch (ParseException e) {
            Log.d(TAG, Arrays.toString(e.getStackTrace()));
        }
        return gym;
    }

    /**
     * @return the gym object stored in this model
     */
    public Gym getGym() {
        return gym;
    }

    /**
     * Sets the gym object.
     *
     * @param gym the new gym object
     */
    public void setGym(Gym gym) {
        this.gym = gym;
    }

    /**
     * @return the selected reservation, or null if nothing selected
     */
    public Reservation getReservation() {
        if (trainerListAdapter == null) {
            Log.e(TAG, "Recycler view adapter is null");
            return null;
        }
        return trainerListAdapter.getSelection();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}