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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.databinding.FragmentScheduleBinding;

public class ScheduleFragment extends Fragment
{
	private final String TAG = "[ScheduleFragment]";
	
	private ScheduleViewModel scheduleViewModel;
	private FragmentScheduleBinding binding;
	private ScheduleViewModel mViewModel;
	TextView textView_Message;
	
	public static ScheduleFragment newInstance()
	{
		return new ScheduleFragment();
	}
	
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		// TODO: Use the ViewModel
	}
	
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
		
		binding = FragmentScheduleBinding.inflate(inflater, container, false);
		View root = binding.getRoot();
		
		final TextView textView = binding.textSchedule;
		scheduleViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>()
		{
			@Override
			public void onChanged(@Nullable String s)
			{
				textView.setText(s);
			}
		});
		return root;
	}
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		binding = null;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
	{
		mViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
		textView_Message = (TextView) getView().findViewById(R.id.message);
		Log.e(TAG, textView_Message.getText().toString());
		
		mViewModel.getUserTimer().observe(getViewLifecycleOwner(), new Observer<Integer>()
		{
			@Override
			public void onChanged(Integer userTimer)
			{
				Log.e(TAG,"userData changed");
				// textView_Message.setText(mViewModel.getUserTimer().toString());
				textView_Message.setText(String.valueOf(userTimer));
				
			}
		});
		
		textView_Message.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e(TAG, "textView_Message - onClick");
				mViewModel.getUserTimer().setValue(0);
				mViewModel.currentSecond= 0;
			}
		});
		mViewModel.startTiming();
	}
	
}