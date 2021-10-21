package comp5216.sydney.edu.au.findmygym.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.databinding.FragmentProfileBinding;
import comp5216.sydney.edu.au.findmygym.model.UserData;

public class ProfileFragment extends Fragment
{
	private final String TAG = "[ProfileFragment]";
	
	private ProfileViewModel profileViewModel;
	private FragmentProfileBinding binding;
	private FavGymAdapter favGymAdapter;
	
	private UserData userData;
	
	public View onCreateView(@NonNull LayoutInflater inflater,
	                         ViewGroup container, Bundle savedInstanceState)
	{
		profileViewModel =
				new ViewModelProvider(this).get(ProfileViewModel.class);
		
		userData = UserData.getInstance();
		userData.setContext(this.getContext());
		favGymAdapter = new FavGymAdapter(userData.getUserFavGym());
		
		binding = FragmentProfileBinding.inflate(inflater, container, false);
		View root = binding.getRoot();
		
		binding.avatarImage.setImageBitmap(userData.getUserAvatar());
		binding.nameText.setText(userData.getUserName());
		binding.emailText.setText(userData.getUserMail());
		binding.favGymRecycler.setAdapter(favGymAdapter);
		binding.favGymRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
		
		//		final TextView textView = binding.textProfile;
		//		profileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>()
		//		{
		//			@Override
		//			public void onChanged(@Nullable String s)
		//			{
		//				textView.setText(s);
		//			}
		//		});
		return root;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		GraphView graph = (GraphView) getView().findViewById(R.id.profile_graphview);
		LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
				new DataPoint(0, 1),
				new DataPoint(1, 5),
				new DataPoint(2, 3),
				new DataPoint(3, 2),
				new DataPoint(4, 6)
		});
		graph.addSeries(series);
	}
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		binding = null;
	}
	
}