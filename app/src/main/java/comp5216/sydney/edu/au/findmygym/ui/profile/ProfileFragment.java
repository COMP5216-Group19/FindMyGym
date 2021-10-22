package comp5216.sydney.edu.au.findmygym.ui.profile;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.databinding.FragmentProfileBinding;
import comp5216.sydney.edu.au.findmygym.model.Reservation;
import comp5216.sydney.edu.au.findmygym.model.UserData;

public class ProfileFragment extends Fragment
{
	private final String TAG = "[ProfileFragment]";
	private static final int PICK_IMAGE = 100;

	private ProfileViewModel profileViewModel;
	private FragmentProfileBinding binding;
	private UserData userData;
	private ArrayList<Reservation> reservations;

	public View onCreateView(@NonNull LayoutInflater inflater,
	                         ViewGroup container, Bundle savedInstanceState)
	{
		profileViewModel =
				new ViewModelProvider(this).get(ProfileViewModel.class);

		binding = FragmentProfileBinding.inflate(inflater, container, false);
		View root = binding.getRoot();
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

		userData = UserData.getInstance();
		userData.setContext(this.getContext());
		reservations = userData.getReservations();
		//TODO: need to change allGyms into firebase databases
		FavGymAdapter favGymAdapter = new FavGymAdapter(userData.getFavouriteGyms(), userData.allGyms);

		binding.avatarImage.setImageBitmap(userData.getUserAvatar());
		binding.nameText.setText(userData.getUserName());
		binding.emailText.setText(userData.getUserMail());

		binding.favGymRecycler.setAdapter(favGymAdapter);
		binding.favGymRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));

		userData.observe(getViewLifecycleOwner(), new Observer<UserData>() {

			@Override
			public void onChanged(UserData userData) {
				favGymAdapter.notifyDataSetChanged();
			}

		});

		GraphView lineChart = (GraphView) getView().findViewById(R.id.profileGraphView1);
		LineGraphSeries<DataPoint> lineSeries = new LineGraphSeries<DataPoint>(new DataPoint[] {
				new DataPoint(0, 1),
				new DataPoint(1, 5),
				new DataPoint(2, 3),
				new DataPoint(3, 2),
				new DataPoint(4, 6)
		});
		lineChart.addSeries(lineSeries);

		GraphView pieChart = (GraphView) getView().findViewById(R.id.profileGraphView2);
		LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
				new DataPoint(0, 1),
				new DataPoint(1, 5),
				new DataPoint(2, 3),
				new DataPoint(3, 2),
				new DataPoint(4, 6)
		});
		pieChart.addSeries(series);
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		binding = null;
	}
}