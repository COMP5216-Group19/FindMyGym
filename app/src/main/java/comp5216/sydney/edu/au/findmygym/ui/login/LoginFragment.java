package comp5216.sydney.edu.au.findmygym.ui.login;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.royrodriguez.transitionbutton.TransitionButton;

import comp5216.sydney.edu.au.findmygym.LoginActivity;
import comp5216.sydney.edu.au.findmygym.MainActivity;
import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.databinding.FragmentScheduleBinding;
import comp5216.sydney.edu.au.findmygym.model.UserData;
import comp5216.sydney.edu.au.findmygym.ui.schedule.ScheduleViewModel;

public class LoginFragment extends Fragment
{
	private final String TAG = "[LoginFragment]";
	private LoginViewModel mViewModel;
	private LoginActivity mActivity;
	public static LoginFragment newInstance()
	{
		return new LoginFragment();
	}

	TextView textView_Message;
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
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
		TransitionButton transitionButton;
		transitionButton = getView().findViewById(R.id.login_button_google);
		transitionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Start the loading animation when the user tap the button
				transitionButton.startAnimation();
				((LoginActivity) getActivity()).onGoogleClicked();
				// Do your networking task or background work here.
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						// Choose a stop animation if your call was succesful or not
						if (UserData.getInstance().isSuccessful()) {
							transitionButton.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
								@Override
								public void onAnimationStopEnd() {
									Intent intent = new Intent(getContext(), MainActivity.class);
									intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
									startActivity(intent);
								}
							});
						}
						else {
//							transitionButton.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
						}
					}
				}, 2000);
			}
		});
	}
}