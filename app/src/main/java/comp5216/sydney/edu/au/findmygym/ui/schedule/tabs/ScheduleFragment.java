package comp5216.sydney.edu.au.findmygym.ui.schedule.tabs;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import comp5216.sydney.edu.au.findmygym.model.Gym;
import comp5216.sydney.edu.au.findmygym.model.PersonalTrainer;
import comp5216.sydney.edu.au.findmygym.model.Reservation;
import comp5216.sydney.edu.au.findmygym.model.ScheduleList;
import comp5216.sydney.edu.au.findmygym.model.Timeslot;
import comp5216.sydney.edu.au.findmygym.model.UserData;
import comp5216.sydney.edu.au.findmygym.model.callbacks.ListQueryCallback;
import comp5216.sydney.edu.au.findmygym.model.callbacks.ObjectQueryCallback;

public abstract class ScheduleFragment extends Fragment {

    Context mContext;
    RecyclerView recyclerView;
    ReservationAdapter adapter;
    ArrayList<ScheduleList> scheduleList = new ArrayList<>();
    UserData userData;

    protected abstract void addToList(Calendar now, Calendar reservationDateT, Gym gym,
                                      String trainerName);

    protected void loadData() {
        Calendar now = Calendar.getInstance();
        userData.getReservationsOfThisUser(new ListQueryCallback<Reservation>() {
            @Override
            public void onSucceed(List<Reservation> list) {
                for (Object obj : list) {
                    Reservation rsv = (Reservation) obj;
                    showReservation(rsv, now);
                }
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }

    protected void showReservation(Reservation rsv, Calendar now) {
        userData.getGymByID(rsv.getGymId(), new ObjectQueryCallback() {
            @Override
            public void onSucceed(Object object) {
                Gym gym = (Gym) object;

                //get Trainer name by id
                String trainerId = rsv.getTrainerId();
                PersonalTrainer tra = gym.findTrainerById(trainerId);
                String trainerName = tra == null ? "" : tra.getName();

                // get reservation start time
                Timeslot reservationDate = rsv.getSelectedTimeSlot();
                Calendar reservationDateT = reservationDate.getBeginTime();

                addToList(now, reservationDateT, gym, trainerName);
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }
}
