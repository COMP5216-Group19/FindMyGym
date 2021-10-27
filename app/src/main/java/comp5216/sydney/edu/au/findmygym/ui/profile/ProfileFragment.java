package comp5216.sydney.edu.au.findmygym.ui.profile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.databinding.FragmentProfileBinding;
import comp5216.sydney.edu.au.findmygym.model.Reservation;
import comp5216.sydney.edu.au.findmygym.model.UserData;

public class ProfileFragment extends Fragment
{
	private final String TAG = "[ProfileFragment]";

	private ProfileViewModel profileViewModel;
	private FragmentProfileBinding binding;
	private UserData userData;
	private List<Reservation> reservations;
	public Map<Integer, Integer> exerciseLog = new HashMap<>();
	public Map<String, Integer> trainerLog = new HashMap<>();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	String currentTime = sdf.format(Calendar.getInstance().getTime());
	Integer currentTimeInt = Integer.parseInt(currentTime);

	public View onCreateView(@NonNull LayoutInflater inflater,
	                         ViewGroup container, Bundle savedInstanceState)
	{
		profileViewModel =
				new ViewModelProvider(this).get(ProfileViewModel.class);

		binding = FragmentProfileBinding.inflate(inflater, container, false);
		View root = binding.getRoot();

		return root;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		userData = UserData.getInstance();
		userData.setContext(this.getContext());
		reservations = userData.getReservations();
		exerciseLog = getExLogFromReservations(reservations);
		trainerLog = getTrainerLogFromReservations(reservations);

		//TODO: need to change allGyms into firebase databases
		FavGymAdapter favGymAdapter = new FavGymAdapter();
		Glide.with(this.getContext())
				.load(userData.getUserAvatarUri())
				.placeholder(R.drawable.ic_launcher_background)
				.into(binding.avatarImage);
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

		BarChart barChart = getView().findViewById(R.id.barchart);
		List<BarEntry> list=new ArrayList<>();
		int i = 0;
		while (i<7) {
			if (!exerciseLog.containsKey(currentTimeInt - i)) {
				list.add(new BarEntry(7 - i, 0));
				i++;
				continue;
			}

			list.add(new BarEntry(7 - i, exerciseLog.get(currentTimeInt - i)));
			i++;
		}

		BarDataSet barDataSet=new BarDataSet(list,"ExTime");   //list是你这条线的数据  "语文" 是你对这条线的描述
		BarData barData=new BarData(barDataSet);
		barChart.setData(barData);

		barChart.getDescription().setEnabled(false);

		Legend legend = barChart.getLegend();
		legend.setEnabled(false);

		XAxis xAxis= barChart.getXAxis();
		xAxis.setDrawGridLines(false);
		xAxis.setTextSize(10f);
		xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
		//X轴自定义坐标
		xAxis.setValueFormatter(new ValueFormatter() {   //X轴自定义坐标
			@Override
			public String getAxisLabel(float v, AxisBase axisBase) {
				if (v==1){
					return getNDaysBeforeDate(6);
				}
				if (v==4){
					return getNDaysBeforeDate(3);
				}
				if (v==7){
					return getNDaysBeforeDate(0);
				}
				return "";//注意这里需要改成 ""
			}
		});

		YAxis AxisLeft = barChart.getAxisLeft();
		YAxis AxisRight = barChart.getAxisRight();
		AxisLeft.setDrawGridLines(false);
		AxisRight.setDrawGridLines(false);
		barChart.getAxisRight().setEnabled(false);
		barChart.getAxisLeft().setEnabled(false);

		barChart.animateY(3000); //在Y轴的动画  参数是动画执行时间 毫秒为单位
		barChart.notifyDataSetChanged();
		barChart.invalidate();



		//Set PieChart
		PieChart pieChart = getView().findViewById(R.id.pieChart);

		Legend pieLegend = pieChart.getLegend();
		pieLegend.setEnabled(false);
		pieChart.getDescription().setEnabled(false);

		List<PieEntry> strings = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : trainerLog.entrySet()) {
			strings.add(new PieEntry((entry.getValue().floatValue() /reservations.size()) * 100F, entry.getKey()));
		}
		//(entry.getValue()/reservations.size()) * 100F

		PieDataSet pieDataSet = new PieDataSet(strings,"Label");

		ArrayList<Integer> colors = new ArrayList<Integer>();
		colors.add(Color.parseColor("#6BE61A"));
		colors.add(Color.parseColor("#4474BB"));
		colors.add(Color.parseColor("#AA7755"));
		colors.add(Color.parseColor("#BB5C44"));
		colors.add(Color.parseColor("#E61A1A"));
		pieDataSet.setColors(colors);

		PieData pieData = new PieData(pieDataSet);
		pieData.setDrawValues(true);

		pieChart.spin( 3000,0,-360f, Easing.EaseInOutQuad);

		pieChart.setData(pieData);
		pieChart.invalidate();

	}

	public String getNDaysBeforeDate(Integer N) {
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -N);

		return sdf2.format(calendar.getTime());
	}

	public Map<Integer, Integer> getExLogFromReservations(List<Reservation> reservations) {
		Map<Integer, Integer> exLog = new HashMap<>();

		for (Reservation rev: reservations) {
			Integer eachBeginTime = Integer.parseInt(sdf.format(rev.getSelectedTimeSlot().getBeginTime().getTime()));
			Integer eachLength = rev.getSelectedTimeSlot().getLengthMinutes();
			if (exLog.containsKey(eachBeginTime)) {
				exLog.put(eachBeginTime, exLog.get(eachBeginTime) + eachLength);
			} else {
				exLog.put(eachBeginTime, eachLength);
			}
		}

		return exLog;
	}

	public Map<String, Integer> getTrainerLogFromReservations(List<Reservation> reservations) {
		Map<String, Integer> trainerLog = new HashMap<>();
		for (Reservation rev: reservations) {
			String trainerName = findTrainerNameById(rev.getTrainerId());
			if (trainerLog.containsKey(trainerName)) {
				trainerLog.put(trainerName, trainerLog.get(trainerName) + 1);
			} else {
				trainerLog.put(trainerName, 1);
			}
		}

		return trainerLog;
	}

	public String findTrainerNameById(Integer id) {
		String name = "";
		if(userData.getAllSimpleGyms() == null) { return name; }
		for (int i = 0; i < userData.allTrainers.size(); i++) {
			if (userData.allTrainers.get(i).getTrainerId() == id) {
				name = userData.allTrainers.get(i).getName();
			}
		}
		return name;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		binding = null;
	}
}