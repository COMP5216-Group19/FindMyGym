package comp5216.sydney.edu.au.findmygym.ui.gym;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.model.Gym;
import comp5216.sydney.edu.au.findmygym.model.PersonalTrainer;
import comp5216.sydney.edu.au.findmygym.model.PurchaseRecord;
import comp5216.sydney.edu.au.findmygym.model.Reservation;
import comp5216.sydney.edu.au.findmygym.model.Review;
import comp5216.sydney.edu.au.findmygym.model.Timeslot;
import comp5216.sydney.edu.au.findmygym.model.UserData;
import comp5216.sydney.edu.au.findmygym.model.callbacks.ListQueryCallback;
import comp5216.sydney.edu.au.findmygym.model.callbacks.ObjectQueryCallback;

public class GymViewModel extends ViewModel {
    private static final String TAG = "[GymViewModel]";
    /**
     * Adapter of reservation RecyclerView.
     * <p>
     * Store it here for simplifying the accessing from GymActivity
     */
    TrainerListAdapter trainerListAdapter;
    private Calendar minSelectableDate;
    Calendar selectedDate;
    Timepoint beginTime;
    Timepoint endTime;
    int gymPrice;
    int trainerPrice;
    List<PersonalTrainer> allPersonalTrainers;
    private Calendar now = Calendar.getInstance();
    private final Calendar today = beginOfADay(now);
    private Gym gym;
    private GymInfoFragment infoFragment;
    GymRsvFragment rsvFragment;

    public GymViewModel() {
//        addTestGym();
//
//        // after set gym
//        generateValuesByGym();
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
     * @param date             the date
     * @param probableTomorrow another date
     * @return whether {@code probableTomorrow} is the next day of {@code date}
     */
    public static boolean isNextDayOf(Calendar date, Calendar probableTomorrow) {
        Calendar tom = (Calendar) probableTomorrow.clone();
        tom.add(Calendar.DATE, -1);
        return isSameDate(date, tom);
    }

    public PersonalTrainer findTrainerById(String trainerId) {
        for (PersonalTrainer pt : allPersonalTrainers) {
            if (pt.getTrainerId().equals(trainerId)) {
                return pt;
            }
        }
        return null;
    }

    public int getTotalPrice() {
        return gymPrice + trainerPrice;
    }

    /**
     * Make a reservation.
     *
     * @param reservation the reservation to be made
     */
    public void makeReservation(Activity activity,
                                Reservation reservation,
                                TrainerReservation trainerReservation) {
        rsvFragment.progressBar.setVisibility(View.VISIBLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        UserData userData = UserData.getInstance();
        if (reservation.getPrice() > 0) {
            PurchaseRecord pr = new PurchaseRecord(
                    null,
                    reservation.getPrice(),
                    UserData.getInstance().getUserId(),
                    gym.getGymId(),
                    reservation.getSelectedTimeSlot().getBeginTime());
            userData.addPurchaseRecord(pr);
        }
        userData.addReservation(reservation, new ObjectQueryCallback<Reservation>() {
            @Override
            public void onSucceed(Reservation object) {
                Toast.makeText(activity, R.string.gym_reserve_success, Toast.LENGTH_SHORT).show();
                activity.finish();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(activity, R.string.gym_something_wrong, Toast.LENGTH_SHORT).show();
                rsvFragment.progressBar.setVisibility(View.GONE);
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
        if (trainerReservation != null) {
            userData.removeTrainerTimeslot(trainerReservation.trainer.getTrainerId(),
                    trainerReservation.timeslot);
        }
    }

    /**
     * Post a review.
     */
    public void postReview() {
        String text = infoFragment.getReview();
        int rating = infoFragment.getRating();
        infoFragment.postReviewButton.setEnabled(false);
        infoFragment.postReviewButton.setText(R.string.gym_review_posting);
        UserData.getInstance().addReview(
                new Review(UserData.getInstance().getUserId(),
                        gym.getGymId(),
                        rating,
                        text,
                        Calendar.getInstance()),
                new ObjectQueryCallback<Review>() {
                    @Override
                    public void onSucceed(Review object) {
                        gym.getReviews().add(object);
                        Toast.makeText(infoFragment.getContext(),
                                R.string.gym_review_posted,
                                Toast.LENGTH_SHORT).show();
                        infoFragment.postReviewButton.setText(R.string.gym_post_review);
                        infoFragment.newReviewPosted();
                        infoFragment.clearInputs();
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Toast.makeText(infoFragment.getContext(),
                                R.string.gym_something_wrong,
                                Toast.LENGTH_SHORT).show();
                        infoFragment.postReviewButton.setText(R.string.gym_post_review);
                        infoFragment.postReviewButton.setEnabled(true);
                    }
                }
        );
    }

    private void generateValuesByGym() {
        gymPrice = gym.getPrice();

        if (now.after(gym.getTodayCloseTime())) {
            Log.d(TAG, "Closed!");
            // Already closed or nearly closed
            selectedDate = (Calendar) now.clone();
            selectedDate.add(Calendar.DATE, 1);
            selectedDate.get(Calendar.DATE);
            Log.d(TAG, "Date is " + selectedDate.get(Calendar.DATE));
            beginTime = new Timepoint(gym.getOpenTime().get(Calendar.HOUR_OF_DAY),
                    gym.getOpenTime().get(Calendar.MINUTE));
        } else {
            selectedDate = (Calendar) now.clone();
            beginTime = new Timepoint(now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE));
        }
        minSelectableDate = (Calendar) selectedDate.clone();
        endTime = new Timepoint(gym.getCloseTime().get(Calendar.HOUR_OF_DAY),
                gym.getCloseTime().get(Calendar.MINUTE));
    }

    public Calendar getMinSelectableDate() {
        return minSelectableDate;
    }

    /**
     * Updates time period after change date
     */
    void updateDefaultTimePeriod() {
        now = Calendar.getInstance();
        if (isSameDate(selectedDate, now)) {
            if (now.before(gym.getTodayOpenTime())) {
                beginTime = new Timepoint(gym.getOpenTime().get(Calendar.HOUR_OF_DAY),
                        gym.getOpenTime().get(Calendar.MINUTE));
            } else {
                beginTime = new Timepoint(now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE));
            }
        } else {
            beginTime = new Timepoint(gym.getOpenTime().get(Calendar.HOUR_OF_DAY),
                    gym.getOpenTime().get(Calendar.MINUTE));
        }
        endTime = new Timepoint(gym.getCloseTime().get(Calendar.HOUR_OF_DAY),
                gym.getCloseTime().get(Calendar.MINUTE));
    }

//    private void addTestGym() {
//        allPersonalTrainers = new ArrayList<>();
//        gym = new Gym("Gym A",
//                "Gym A",
//                CalendarUtil.stringToCalendarNoDate("09:00"),
//                CalendarUtil.stringToCalendarNoDate("18:00"),
//                20,
//                "Blah Ave. Blah Unit",
//                "12345678",
//                123.45,
//                -27.5);
//        gym.getEquipments().add("Barbell");
//        gym.getEquipments().add("Bicycle");
//        gym.getEquipments().add("Climbing");
//        gym.getEquipments().add("Dumbbell");
//        gym.getEquipments().add("Rowing");
//        gym.getEquipments().add("Swimming");
//        gym.getEquipments().add("Treadmill");
//
//        Calendar yesterday = Calendar.getInstance();
//        yesterday.add(Calendar.DATE, -1);
//        gym.getReviews().add(
//                new Review("Steven", null, 2,
//                        "What a terrible place!", yesterday));
//
//        Calendar someDaysAgo = Calendar.getInstance();
//        yesterday.add(Calendar.DATE, -8);
//        gym.getReviews().add(
//                new Review("Elisabeth", null, 5,
//                        "Recommended! Various kind of equipments, enough space, " +
//                                "a swimming pool inside. Will come again and advise to " +
//                                "my friends.",
//                        someDaysAgo));
//
//        try {
//            addMockTrainersInThisWeek("Mark", "Mark", 3000);
//            addMockTrainersInThisWeek("Ada", "Ada", 4000);
//        } catch (ParseException e) {
//            Log.d(TAG, "Calendar parse error", e);
//        }
//    }
//
//    private void addMockTrainersInThisWeek(String trainerId, String trainerName, int price)
//            throws ParseException {
//        Calendar cal = (Calendar) today.clone();
//        Calendar openTime = gym.getOpenTime();
//        cal.set(Calendar.HOUR_OF_DAY, openTime.get(Calendar.HOUR_OF_DAY));
//        cal.set(Calendar.MINUTE, openTime.get(Calendar.MINUTE));
//        double openHours = (double) (gym.getCloseTime().getTimeInMillis() -
//                openTime.getTimeInMillis()) / 3_600_000;
//        int segments = (int) Math.floor(openHours);
//
//        PersonalTrainer trainer = new PersonalTrainer(trainerId, trainerName, price);
//
//        for (int day = 0; day < 7; day++) {
//            Calendar calInDay = (Calendar) cal.clone();
//            calInDay.add(Calendar.DAY_OF_MONTH, day);
//            for (int hour = 0; hour < segments; hour++) {
//                trainer.addTimeSlot(new Timeslot((Calendar) calInDay.clone(), 60));
//                calInDay.add(Calendar.HOUR_OF_DAY, 1);
//            }
//        }
//        allPersonalTrainers.add(trainer);
//        gym.getPersonalTrainers().add(trainer);
//    }

    public Calendar getSelectedDate() {
        return selectedDate;
    }

    public Timepoint getBeginTime() {
        return beginTime;
    }

    public Timepoint getEndTime() {
        return endTime;
    }

    public Calendar getBeginDateTime() {
        Calendar dateTime = (Calendar) getSelectedDate().clone();
        dateTime.set(Calendar.HOUR_OF_DAY, beginTime.getHour());
        dateTime.set(Calendar.MINUTE, beginTime.getMinute());
        return dateTime;
    }

    public Calendar getEndDateTime() {
        Calendar dateTime = (Calendar) getSelectedDate().clone();
        dateTime.set(Calendar.HOUR_OF_DAY, endTime.getHour());
        dateTime.set(Calendar.MINUTE, endTime.getMinute());
        return dateTime;
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
        if (gym != null) {
            this.gym = gym;
            generateValuesByGym();
        }
    }

    /**
     * @return the selected reservation, or null if nothing selected
     */
    public TrainerReservation getReservation() {
        if (trainerListAdapter == null) {
            Log.e(TAG, "Recycler view adapter is null");
            return null;
        }
        return trainerListAdapter.getSelection();
    }

    public Timeslot getSelectedGymTimeslot() {
        if (trainerListAdapter == null) {
            Log.e(TAG, "Recycler view adapter is null");
            return null;
        }
        Calendar date = getSelectedDate();
        Timepoint begin = getBeginTime();
        Timepoint end = getEndTime();
        Calendar dateTime = (Calendar) date.clone();
        dateTime.set(Calendar.HOUR_OF_DAY, begin.getHour());
        dateTime.set(Calendar.MINUTE, begin.getMinute());
        int length = end.getHour() * 60 + end.getMinute() -
                begin.getHour() * 60 - begin.getMinute();
        return new Timeslot(dateTime, length);
    }

    void setInfoFragment(GymInfoFragment infoFragment) {
        this.infoFragment = infoFragment;
    }

    void enableCommentIfVisited(TextInputLayout inputLayout, TextInputEditText editText,
                                RatingBar reviewRatingBar) {
        inputLayout.setHint(R.string.gym_go_first_then_comment);
        inputLayout.setEnabled(false);
        editText.setEnabled(false);
        reviewRatingBar.setEnabled(false);

        UserData.getInstance().getReservationsOfThisUser(new ListQueryCallback<Reservation>() {
            @Override
            public void onSucceed(List<Reservation> list) {
                for (Reservation rsv : list) {
                    if (gym.getGymId().equals(rsv.getGymId())) {
                        inputLayout.setEnabled(true);
                        editText.setEnabled(true);
                        inputLayout.setHint(R.string.gym_write_review_hint);
                        reviewRatingBar.setEnabled(true);
                    }
                }
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
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