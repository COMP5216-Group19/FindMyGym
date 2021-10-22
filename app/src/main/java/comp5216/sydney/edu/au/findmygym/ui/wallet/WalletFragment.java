package comp5216.sydney.edu.au.findmygym.ui.wallet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTabHost;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.databinding.FragmentWalletBinding;
import comp5216.sydney.edu.au.findmygym.ui.wallet.tabs.Wallet_History;
import comp5216.sydney.edu.au.findmygym.ui.wallet.tabs.Wallet_Membership;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WalletFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WalletFragment extends Fragment
{
	private final String TAG = "[WalletFragment]";
	
	private static final String ARG_COUNT = "param1";
	private Integer counter;
	
	private FragmentTabHost mTabHost;
	private WalletViewModel walletViewModel;
	private FragmentWalletBinding binding;
	TabLayout tabLayout;
	FragmentAdapter fragmentAdapter;
	ViewPager2 viewPager;

	
	public WalletFragment() {
		// Required empty public constructor
	}
	
	public static WalletFragment newInstance(Integer counter) {
		WalletFragment fragment = new WalletFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_COUNT, counter);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			counter = getArguments().getInt(ARG_COUNT);
		}
		
	}
	
	public View onCreateView(@NonNull LayoutInflater inflater,
	                         ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_wallet, container, false);
	}
	
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

		super.onViewCreated(view, savedInstanceState);

		tabLayout = getView().findViewById(R.id.wallet_tabLayout);
		viewPager = getView().findViewById(R.id.wallet_viewpager2);
		FragmentAdapter fragmentAdapter = new FragmentAdapter(getActivity().getSupportFragmentManager(), getLifecycle());
		
		viewPager.setAdapter(fragmentAdapter);
		
		tabLayout.addTab(tabLayout.newTab().setText("Memberships"));
		tabLayout.addTab(tabLayout.newTab().setText("Transactions"));
		tabLayout.addTab(tabLayout.newTab().setText("Cards"));
		
		
		
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
