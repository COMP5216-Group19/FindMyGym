package comp5216.sydney.edu.au.findmygym.ui.main;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import comp5216.sydney.edu.au.findmygym.LoginActivity;
import comp5216.sydney.edu.au.findmygym.MainActivity;
import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.model.UserData;

public class MainFragment extends Fragment
{
	private final String TAG = "[MainFragment]";
	
	private MainViewModel mViewModel;
	TextView textView_Message;
	MainActivity mActivity;
	public static MainFragment newInstance()
	{
		return new MainFragment();
	}
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.main_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		// TODO: Use the ViewModel
	
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
	{
		mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
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