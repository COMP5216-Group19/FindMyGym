package comp5216.sydney.edu.au.findmygym.ui.wallet;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

import comp5216.sydney.edu.au.findmygym.ui.wallet.tabs.Wallet_Card;
import comp5216.sydney.edu.au.findmygym.ui.wallet.tabs.Wallet_History;
import comp5216.sydney.edu.au.findmygym.ui.wallet.tabs.Wallet_Membership;

public class FragmentAdapter extends FragmentStateAdapter
{
	private final static String TAG = "[FragmentAdapter]";
	Context context;
	public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle)
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
				Log.e(TAG, "createFragment: Wallet_Membership"+position);
				return new Wallet_Membership();
			case 1:
				Log.e(TAG, "createFragment: Wallet_History"+position);
				return new Wallet_History();
			case 2:
				Log.e(TAG, "createFragment: Wallet_Card"+position);
				return new Wallet_Card();
		}
		return new Wallet_Membership();
	}
	
	@Override
	public int getItemCount()
	{
		return 3;
	}
}
