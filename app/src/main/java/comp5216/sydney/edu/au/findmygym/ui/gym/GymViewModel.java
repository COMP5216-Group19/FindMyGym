package comp5216.sydney.edu.au.findmygym.ui.gym;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import comp5216.sydney.edu.au.findmygym.model.Gym;
import comp5216.sydney.edu.au.findmygym.model.PersonalTrainer;
import comp5216.sydney.edu.au.findmygym.model.Reservation;
import comp5216.sydney.edu.au.findmygym.model.Review;
import comp5216.sydney.edu.au.findmygym.model.Timeslot;

public class GymViewModel extends ViewModel {
    private static final String TAG = "[GymViewModel]";
    /**
     * Adapter of reservation RecyclerView.
     * <p>
     * Store it here for simplifying the accessing from GymActivity
     */
    TrainerListAdapter trainerListAdapter;

    private final Calendar today = beginOfADay(Calendar.getInstance());

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
        gym.getEquipments().add("Barbell");
        gym.getEquipments().add("Bicycle");
        gym.getEquipments().add("Climbing");
        gym.getEquipments().add("Dumbbell");
        gym.getEquipments().add("Rowing");
        gym.getEquipments().add("Swimming");
        gym.getEquipments().add("Treadmill");

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        gym.getReviews().add(
                new Review("Steven", null, 2,
                        "What a terrible place!", yesterday));

        Calendar someDaysAgo = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -8);
        gym.getReviews().add(
                new Review("Elisabeth", null, 5,
                        "Recommended! Various kind of equipments, enough space, " +
                                "a swimming pool inside. Will come again and advise to my friends.",
                        someDaysAgo));

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mma");
        try {
            PersonalTrainer pt1 = new PersonalTrainer(1, "Mark");
            pt1.addTimeSlot(Timeslot.timeSlotOnToday("10AM", "11:30AM"));
            pt1.addTimeSlot(Timeslot.timeSlotOnToday("1:00PM", "2:30PM"));
            pt1.addTimeSlot(Timeslot.timeSlotOnToday("3PM", "4:30PM"));
            pt1.addTimeSlot(Timeslot.timeSlotOnToday("5PM", "6PM"));

            // Not on today
            pt1.addTimeSlot(Timeslot.fromDate(df.parse("2021-10-23 9:30AM"), 90));
            gym.getPersonalTrainers().add(pt1);

            PersonalTrainer pt2 = new PersonalTrainer(2, "Ada");
            pt2.addTimeSlot(Timeslot.timeSlotOnToday("9AM", "11AM"));
            pt2.addTimeSlot(Timeslot.timeSlotOnToday("1:00 PM", "3 PM"));
            gym.getPersonalTrainers().add(pt2);
        } catch (ParseException e) {
            Log.d(TAG, Arrays.toString(e.getStackTrace()));
        }
        return gym;
    }

    /**
     * Returns a calendar object that stores the 00:00:000 of the day, which has the same
     * date as {@code time}.
     *
     * @param time the time to be calculated
     * @return Returns a calendar object that stores the 00:00:000 of the day, which has the same
     * date as {@code time}.
     */
    public static Calendar beginOfADay(Calendar time) {
        Calendar day = Calendar.getInstance();
        day.clear();
        day.set(time.get(Calendar.YEAR),
                time.get(Calendar.MONTH),
                time.get(Calendar.DAY_OF_MONTH));

        // This just forces calendar to compute fields
        day.set(Calendar.MILLISECOND, 0);
        return day;
    }

    public static boolean isSameDate(Calendar date1, Calendar date2) {
        return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) &&
                date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH) &&
                date1.get(Calendar.DAY_OF_MONTH) == date2.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Return whether {@code probableTomorrow} is the next day of {@code date}.
     *
     * @param date the date
     * @param probableTomorrow another date
     * @return whether {@code probableTomorrow} is the next day of {@code date}
     */
    public static boolean isNextDayOf(Calendar date, Calendar probableTomorrow) {
        Calendar tom = (Calendar) probableTomorrow.clone();
        tom.add(Calendar.DATE, 1);
        return isSameDate(date, tom);
    }

    public Calendar getToday() {
        return today;
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

    @NotNull
    public TrainerListAdapter getTrainerListAdapter() {
        return trainerListAdapter;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}