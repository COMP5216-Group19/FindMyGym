package comp5216.sydney.edu.au.findmygym.ui.schedule.tabs;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.model.PersonalTrainer;
import comp5216.sydney.edu.au.findmygym.model.Reservation;
import comp5216.sydney.edu.au.findmygym.model.ScheduleList;
import comp5216.sydney.edu.au.findmygym.model.SimpleGym;
import comp5216.sydney.edu.au.findmygym.model.UserData;
import comp5216.sydney.edu.au.findmygym.model.callbacks.ListQueryCallback;
import comp5216.sydney.edu.au.findmygym.model.callbacks.ObjectQueryCallback;

public abstract class ScheduleFragment extends Fragment {

    private static final String TAG = "[ScheduleFragment]";

    Context mContext;
    RecyclerView recyclerView;
    ReservationAdapter adapter;
    ProgressBar progressIndicator;
    ArrayList<ScheduleList> scheduleList = new ArrayList<>();
    UserData userData;

    private int numReservationsAdded = 0;

    protected abstract boolean isHistory();

    protected void loadData() {
        progressIndicator.setVisibility(View.VISIBLE);
        Calendar now = Calendar.getInstance();
        userData.getReservationsOfThisUser(new ListQueryCallback<Reservation>() {
            @Override
            public void onSucceed(List<Reservation> list) {
                List<Reservation> histories = new ArrayList<>();
                List<Reservation> bookings = new ArrayList<>();
                for (Object obj : list) {
                    Reservation rsv = (Reservation) obj;
                    if (rsv.getSelectedTimeSlot().getBeginTime().after(now)) {
                        bookings.add(rsv);
                    } else {
                        histories.add(rsv);
                    }
                }
                if (isHistory()) {
                    showReservations(histories);
                } else {
                    showReservations(bookings);
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(getContext(), R.string.gym_something_wrong, Toast.LENGTH_SHORT)
                        .show();
                progressIndicator.setVisibility(View.GONE);
            }
        });
    }

    protected SimpleGym findSimpleGymById(String gid) {
        for (SimpleGym simpleGym : userData.getAllSimpleGyms()) {
            if (gid.equals(simpleGym.getGymId())) return simpleGym;
        }
        return null;
    }

    protected void showReservations(List<Reservation> reservations) {
        numReservationsAdded = 0;
        for (Reservation rsv : reservations) {
            if (rsv.getTrainerId() == null) {
                numReservationsAdded++;
                if (numReservationsAdded == reservations.size()) {
                    progressIndicator.setVisibility(View.GONE);
                }
            } else {
                userData.getTrainerByID(rsv.getTrainerId(), new ObjectQueryCallback<PersonalTrainer>() {
                    @Override
                    public void onSucceed(PersonalTrainer object) {
                        SimpleGym simpleGym = findSimpleGymById(rsv.getGymId());

                        scheduleList.add(new ScheduleList(simpleGym.getGymName(), object.getName(),
                                rsv.getSelectedTimeSlot().getBeginTime(),
                                simpleGym.getGymId()));
                        adapter.notifyDataSetChanged();
                        numReservationsAdded++;

                        if (numReservationsAdded == reservations.size()) {
                            progressIndicator.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailed(Exception e) {
                        numReservationsAdded++;
                        Log.e(TAG, "Failed to load trainer id " + rsv.getTrainerId());

                        if (numReservationsAdded == reservations.size()) {
                            progressIndicator.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }
    }
}
