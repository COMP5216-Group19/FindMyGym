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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.model.PurchaseRecord;
import comp5216.sydney.edu.au.findmygym.model.UserData;
import comp5216.sydney.edu.au.findmygym.ui.schedule.tabs.HistoryAdapter;

public class Schedule_History extends Fragment
{
    private final String TAG = "[Schedule_History]";
    Context mContext;
    RecyclerView recyclerView;
    HistoryAdapter historyAdapter;
    ArrayList<PurchaseRecord> historyList = new ArrayList<>();
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

        Bitmap bitmap1 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.diana);
        Bitmap bitmap2 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ybb);
        Bitmap bitmap3 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.azi);
        Bitmap bitmap4 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.onion);
        Bitmap bitmap5 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.mea);
        List<Bitmap> bitmapList = Arrays.asList(bitmap1,bitmap2,bitmap3,bitmap4,bitmap5);

        for (int i = 0; i < 20; i++)
        {
            Calendar cal =  Calendar.getInstance();
            Random random = new Random();
            cal.set(Calendar.HOUR_OF_DAY,random.nextInt(23 - 0 + 1) + 0);
            historyList.add(new PurchaseRecord(111,"Gym "+i,  cal, getRandomItem(bitmapList)));
        }

        userData.setPurchaseRecords(historyList);

        recyclerView = getView().findViewById(R.id.SHistory_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        historyAdapter = new HistoryAdapter(mContext,historyList);
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
