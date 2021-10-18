package comp5216.sydney.edu.au.findmygym.ui.gym;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.chip.ChipGroup;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.databinding.GymActivityBinding;
import comp5216.sydney.edu.au.findmygym.databinding.GymFragmentBinding;
import comp5216.sydney.edu.au.findmygym.ui.main.MainViewModel;

public class GymFragment extends Fragment
{
	private final String TAG = "[GymFragment]";

	TextView gymNameBig, gymNameSmall;
	TextView gymOpenHrs;
	TextView gymAvgRating;
	TextView gymAddress;
	TextView gymContact;
	ChipGroup equipmentsContainer;
	
	private GymViewModel mViewModel;
	private String gym_name;
	private GymFragmentBinding binding;
	public static GymFragment newInstance()
	{
		return new GymFragment();
	}
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState)
	{
		binding = GymFragmentBinding.inflate(inflater, container, false);

		mViewModel = new ViewModelProvider(requireActivity()).get(GymViewModel.class);
		View view = inflater.inflate(R.layout.gym_fragment, container, false);

		gymNameBig = view.findViewById(R.id.gym_name_big);
		gymNameSmall = view.findViewById(R.id.gym_name);
		gymOpenHrs = view.findViewById(R.id.gym_open_hrs);
		gymAvgRating = view.findViewById(R.id.gym_avg_rate);
		gymAddress = view.findViewById(R.id.gym_address);
		gymContact = view.findViewById(R.id.gym_contact);
		equipmentsContainer = view.findViewById(R.id.gym_equipments_group);

		return view;
	}
	
//	@Override
//	public void onActivityCreated(@Nullable Bundle savedInstanceState)
//	{
//		super.onActivityCreated(savedInstanceState);
//		mViewModel = new ViewModelProvider(this).get(GymViewModel.class);
//		// TODO: Use the ViewModel
//	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
	{
		inflater.inflate(R.menu.gym,menu);
		//
		// super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
	{
		gym_name = "TESTING GYM";
		ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
		Log.e("TEST", String.valueOf(actionBar==null));
		actionBar.setTitle(gym_name);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);//home button on
	}

}