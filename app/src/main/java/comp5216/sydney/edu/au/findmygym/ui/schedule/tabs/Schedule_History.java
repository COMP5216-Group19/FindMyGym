package comp5216.sydney.edu.au.findmygym.ui.schedule.tabs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.model.Gym;
import comp5216.sydney.edu.au.findmygym.model.PersonalTrainer;
import comp5216.sydney.edu.au.findmygym.model.PurchaseRecord;
import comp5216.sydney.edu.au.findmygym.model.ScheduleList;
import comp5216.sydney.edu.au.findmygym.model.Timeslot;
import comp5216.sydney.edu.au.findmygym.model.UserData;
import comp5216.sydney.edu.au.findmygym.ui.schedule.tabs.HistoryAdapter;

public class Schedule_History extends Fragment
{
    private final String TAG = "[Schedule_History]";
    Context mContext;
    RecyclerView recyclerView;
    HistoryAdapter historyAdapter;
    ArrayList<ScheduleList> scheduleLists = new ArrayList<>();
    UserData userData;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        Log.e(TAG, "onCreateView: TEST=================");
        return inflater.inflate(R.layout.fragment_schedule_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mContext = getContext();
        userData = UserData.getInstance();

        Bitmap bitmap1 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fitness_gym_example_1484x983);
        Bitmap bitmap2 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fitness_gym_example_1484x983);
        Bitmap bitmap3 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fitness_gym_example_1484x983);
        Bitmap bitmap4 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fitness_gym_example_1484x983);
        Bitmap bitmap5 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fitness_gym_example_1484x983);
        List<Bitmap> bitmapList = Arrays.asList(bitmap1,bitmap2,bitmap3,bitmap4,bitmap5);

        for (int i = 0; i < userData.getReservations().size(); i++)
        {
            Calendar now =  Calendar.getInstance();

            //get Gym name by id
            int gymId = userData.getReservations().get(i).getGymId();
            Gym gym = userData.findGymById(gymId);
            String gymName = gym.getGymName();

            //get Trainer name by id
            int trainerId = userData.getReservations().get(i).getTrainerId();
            PersonalTrainer trainer = userData.findTrainerById(trainerId);
            String trainerName = trainer.getName();

            // get reservation start time
            Timeslot reservationDate = userData.getReservations().get(i).getSelectedTimeSlot();
            Calendar reservationDateT = reservationDate.getBeginTime();


            if (now.after(reservationDateT)) {
                scheduleLists.add(new ScheduleList(gymName,trainerName, reservationDateT, getRandomItem(bitmapList)));
            }
        }

        userData.setScheduleLists(scheduleLists);

        recyclerView = getView().findViewById(R.id.SHistory_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        historyAdapter = new HistoryAdapter(mContext,scheduleLists);
        recyclerView.setAdapter(historyAdapter);

        TextView textView = getView().findViewById(R.id.SHistory_textview_title);
        textView.setText(TAG);


        userData.observe(getViewLifecycleOwner(), new Observer<UserData>()
        {
            @Override
            public void onChanged(UserData userData)
            {
                // historyAdapter.setHistoryArrayList(userData.getPurchaseRecords());
                historyAdapter.notifyDataSetChanged();
            }
        });

    }



    private <T> T getRandomItem(List<T> list){
        return  list.get(new Random().nextInt(list.size()));
    }
}