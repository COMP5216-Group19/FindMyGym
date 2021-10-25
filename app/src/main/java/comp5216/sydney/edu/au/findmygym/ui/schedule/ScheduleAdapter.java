package comp5216.sydney.edu.au.findmygym.ui.schedule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.model.PurchaseRecord;
import comp5216.sydney.edu.au.findmygym.model.UserData;
import comp5216.sydney.edu.au.findmygym.ui.schedule.tabs.Schedule_Booking;
import comp5216.sydney.edu.au.findmygym.ui.schedule.tabs.Schedule_History;
import comp5216.sydney.edu.au.findmygym.ui.wallet.tabs.Wallet_History;
import comp5216.sydney.edu.au.findmygym.ui.wallet.tabs.Wallet_Membership;
import pl.droidsonroids.gif.GifImageView;

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
                return new Schedule_Booking();
            case 1:
                Log.e(TAG, "createFragment: History"+position);
                return new Schedule_History();
        }
        return new Schedule_Booking();
    }

    @Override
    public int getItemCount()
    {
        return 2;
    }
}


