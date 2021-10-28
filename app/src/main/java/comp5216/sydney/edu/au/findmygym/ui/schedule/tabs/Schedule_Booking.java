package comp5216.sydney.edu.au.findmygym.ui.schedule.tabs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import comp5216.sydney.edu.au.findmygym.ui.schedule.tabs.BookingAdapter;

public class Schedule_Booking extends Fragment
{
    private final String TAG = " ";
    Context mContext;
    RecyclerView recyclerView;
    BookingAdapter historyAdapter;
    ArrayList<ScheduleList> bookList = new ArrayList<>();
    UserData userData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        Log.e(TAG, "onCreateView: TEST=================");
        return inflater.inflate(R.layout.fragment_schedule_booking, container, false);
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
            String gymId = userData.getReservations().get(i).getGymId();
            System.out.println(gymId);
            Gym gym = userData.findGymById(gymId);
            if (gym != null) {
                String gymName = gym.getGymName();

                //get Trainer name by id
                String trainerId = userData.getReservations().get(i).getTrainerId();
                PersonalTrainer tra = gym.findTrainerById(trainerId);
                String trainerName = tra == null ? "" : tra.getName();

                // get reservation start time
                Timeslot reservationDate = userData.getReservations().get(i).getSelectedTimeSlot();
                Calendar reservationDateT = reservationDate.getBeginTime();


                if (reservationDateT.after(now)) {
                    bookList.add(new ScheduleList(gymName, trainerName, reservationDateT, getRandomItem(bitmapList)));
                }
            }
        }



        userData.setScheduleLists(bookList);

        recyclerView = getView().findViewById(R.id.booking_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        historyAdapter = new BookingAdapter(mContext,bookList);
        recyclerView.setAdapter(historyAdapter);

        TextView textView = getView().findViewById(R.id.booking_textview_title);
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
