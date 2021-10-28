package comp5216.sydney.edu.au.findmygym.ui.schedule.tabs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.model.Gym;
import comp5216.sydney.edu.au.findmygym.model.ScheduleList;
import comp5216.sydney.edu.au.findmygym.model.UserData;

public class ScheduleHistory extends ScheduleFragment
{
    private final String TAG = " ";

    @Override
    protected void addToList(Calendar now, Calendar reservationDateT, Gym gym, String trainerName) {
        if (reservationDateT.before(now)) {
            scheduleList.add(new ScheduleList(gym.getGymName(), trainerName, reservationDateT,
                    gym.getGymId()));
            adapter.notifyDataSetChanged();
        }
    }

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

        loadData();

        userData.setScheduleLists(scheduleList);

        recyclerView = getView().findViewById(R.id.SHistory_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new ReservationAdapter(mContext, scheduleList);
        recyclerView.setAdapter(adapter);

        TextView textView = getView().findViewById(R.id.SHistory_textview_title);
        textView.setText(TAG);


        userData.observe(getViewLifecycleOwner(), new Observer<UserData>()
        {
            @Override
            public void onChanged(UserData userData)
            {
                // historyAdapter.setHistoryArrayList(userData.getPurchaseRecords());
                adapter.notifyDataSetChanged();
            }
        });

    }



    private <T> T getRandomItem(List<T> list){
        return  list.get(new Random().nextInt(list.size()));
    }
}
