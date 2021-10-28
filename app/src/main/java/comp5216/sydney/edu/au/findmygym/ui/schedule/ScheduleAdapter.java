package comp5216.sydney.edu.au.findmygym.ui.schedule;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import comp5216.sydney.edu.au.findmygym.ui.schedule.tabs.ScheduleBooking;
import comp5216.sydney.edu.au.findmygym.ui.schedule.tabs.ScheduleHistory;

public class ScheduleAdapter extends FragmentStateAdapter
{
    private final static String TAG = "[FragmentAdapter]";
    Context context;
    public ScheduleAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle)
    {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
        switch (position)
        {
            case 0:
                Log.e(TAG, "createFragment: Booking"+position);
                return new ScheduleBooking();
            case 1:
                Log.e(TAG, "createFragment: History"+position);
                return new ScheduleHistory();
        }
        return new ScheduleBooking();
    }

    @Override
    public int getItemCount()
    {
        return 2;
    }
}


