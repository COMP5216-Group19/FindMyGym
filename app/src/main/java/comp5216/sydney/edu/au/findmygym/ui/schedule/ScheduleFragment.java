package comp5216.sydney.edu.au.findmygym.ui.schedule;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTabHost;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.databinding.FragmentScheduleBinding;
import comp5216.sydney.edu.au.findmygym.databinding.FragmentWalletBinding;
import comp5216.sydney.edu.au.findmygym.model.Reservation;
import comp5216.sydney.edu.au.findmygym.model.UserData;
import comp5216.sydney.edu.au.findmygym.model.callbacks.ListQueryCallback;
import comp5216.sydney.edu.au.findmygym.ui.wallet.FragmentAdapter;
import comp5216.sydney.edu.au.findmygym.ui.wallet.WalletViewModel;

public class ScheduleFragment extends Fragment
{
	private final String TAG = "[ScheduleFragment]";

	private static final String ARG_COUNT = "param1";

	private WalletViewModel walletViewModel;
	private FragmentWalletBinding binding;
	TabLayout tabLayout;
	FragmentAdapter fragmentAdapter;
	ViewPager2 viewPager;


	public ScheduleFragment() {
		// Required empty public constructor
	}

	public static ScheduleFragment newInstance(Integer counter) {
		ScheduleFragment fragment = new ScheduleFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_COUNT, counter);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			Integer counter = getArguments().getInt(ARG_COUNT);
		}

	}

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_schedule, container, false);
	}


	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		// demoCollectionAdapter = new DemoCollectionAdapter(this);
		// viewPager = view.findViewById(R.id.pager);
		// viewPager.setAdapter(demoCollectionAdapter);
		//
		// TabLayout tabLayout = view.findViewById(R.id.tab_layout);
		// new TabLayoutMediator(tabLayout, viewPager,
		// 		(tab, position) -> tab.setText("OBJECT " + (position + 1))
		// ).attach();
		super.onViewCreated(view, savedInstanceState);
		tabLayout = getView().findViewById(R.id.schedule_tabLayout);
		viewPager = getView().findViewById(R.id.schedule_viewpager2);
		ScheduleAdapter fragmentAdapter = new ScheduleAdapter(getActivity().getSupportFragmentManager(), getLifecycle());

		viewPager.setAdapter(fragmentAdapter);

		tabLayout.addTab(tabLayout.newTab().setText("Booking"));
		tabLayout.addTab(tabLayout.newTab().setText("History"));
		UserData userData = UserData.getInstance();
		
	
		
		
		userData.getReservationsOfThisUser(new ListQueryCallback<Reservation>()
		{
			@Override
			public void onSucceed(List<Reservation> list)
			{
				Log.d(TAG, "getPurchaseRecordsByUID successfully!"+list.size()+list.toString());
				// userData.setPurchaseRecords(list);
			}
			
			@Override
			public void onFailed(Exception e)
			{
				Log.e(TAG, e.toString());
				e.printStackTrace();
			}
		});



		this.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
		{
			@Override
			public void onTabSelected(TabLayout.Tab tab)
			{
				Log.e(TAG, "onTabSelected: "+tab.getTag() + "@" +tab.getPosition());
				viewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab)
			{

			}

			@Override
			public void onTabReselected(TabLayout.Tab tab)
			{

			}
		});

		viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback()
		{
			@Override
			public void onPageSelected(int position)
			{
				Log.e(TAG, "onPageSelected at position: "+position);
				tabLayout.selectTab(tabLayout.getTabAt(position));
			}
		});

	}




}