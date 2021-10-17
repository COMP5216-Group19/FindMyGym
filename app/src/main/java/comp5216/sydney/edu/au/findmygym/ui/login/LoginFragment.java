package comp5216.sydney.edu.au.findmygym.ui.login;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import comp5216.sydney.edu.au.findmygym.LoginActivity;
import comp5216.sydney.edu.au.findmygym.R;

public class LoginFragment extends Fragment
{
	private final String TAG = "[LoginFragment]";
	private LoginViewModel mViewModel;
	private LoginActivity mActivity;
	public static LoginFragment newInstance()
	{
		return new LoginFragment();
	}
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.login_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
		// TODO: Use the ViewModel
		
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		mActivity = (LoginActivity) getActivity();
		mActivity.findViewById(R.id.login_button_google).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				mActivity.signIn();
			}
		});
		
	}
}