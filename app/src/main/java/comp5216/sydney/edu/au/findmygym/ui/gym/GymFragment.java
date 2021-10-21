package comp5216.sydney.edu.au.findmygym.ui.gym;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.databinding.GymFragmentBinding;

public class GymFragment extends Fragment {
    private final String TAG = "[GymFragment]";

    GymPagerAdapter pagerAdapter;

    private GymViewModel mViewModel;
    private GymFragmentBinding binding;

    public static GymFragment newInstance() {
        return new GymFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = GymFragmentBinding.inflate(inflater, container, false);

        mViewModel = new ViewModelProvider(requireActivity()).get(GymViewModel.class);
        View view = inflater.inflate(R.layout.gym_fragment, container, false);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.gym, menu);
        //
        // super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        Log.e("TEST", String.valueOf(actionBar == null));
        actionBar.setTitle(mViewModel.getGym().getGymName());
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);//home button on

        TabLayout tabLayout = view.findViewById(R.id.gym_tab_layout);
        ViewPager2 viewPager = view.findViewById(R.id.gym_pager);

        pagerAdapter = new GymPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == GymPagerAdapter.INFO_PAGE_POSITION) {
                tab.setText(R.string.gym_info_tab);
                tab.setIcon(R.drawable.outline_info_24);
            } else if (position == GymPagerAdapter.RESERVATION_PAGE_POSITION) {
                tab.setText(R.string.gym_reservation_tab);
                tab.setIcon(R.drawable.outline_event_24);
            }
        }).attach();
    }

}