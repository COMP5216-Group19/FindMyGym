package comp5216.sydney.edu.au.findmygym.ui.wallet.tabs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.model.PurchaseRecord;
import comp5216.sydney.edu.au.findmygym.model.UserData;
import comp5216.sydney.edu.au.findmygym.model.callbacks.ListQueryCallback;


public class Wallet_History extends Fragment implements SwipeRefreshLayout.OnRefreshListener
{
	private final String TAG = "[Wallet_History]";
	Context context;
	RecyclerView recyclerView;
	HistoryAdapter historyAdapter;
	ArrayList<PurchaseRecord> historyList = new ArrayList<>();
	UserData userData;
	SwipeRefreshLayout swipeRefreshLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		Log.e(TAG, "onCreateView: TEST=================");
		return inflater.inflate(R.layout.fragment_wallet_history, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		context = getContext();
		userData = UserData.getInstance();
		swipeRefreshLayout = view.findViewById(R.id.wallet_history_refreshLayout);
		swipeRefreshLayout.setOnRefreshListener(this);

		// userData.setPurchaseRecords(new ArrayList<>());
		userData.getPurchaseRecordsByUID(userData.getUserId(), new ListQueryCallback<PurchaseRecord>()
		{
			@Override
			public void onSucceed(List<PurchaseRecord> list)
			{
				Log.d(TAG, "getPurchaseRecordsByUID successfully!"+list.size()+list.toString());
				// userData.setPurchaseRecords(list);
			}

			@Override
			public void onFailed(Exception e)
			{
				Log.e(TAG, e.toString());
				e.printStackTrace();
			}
		});

		// Bitmap bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.diana);
		// Bitmap bitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.ybb);
		// Bitmap bitmap3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.azi);
		// Bitmap bitmap4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.onion);
		// Bitmap bitmap5 = BitmapFactory.decodeResource(context.getResources(), R.drawable.mea);
		// List<Bitmap> bitmapList = Arrays.asList(bitmap1,bitmap2,bitmap3,bitmap4,bitmap5);
		//
		// for (int i = 0; i < 3; i++)
		// {
		// 	Calendar cal =  Calendar.getInstance();
		// 	System.out.println(cal);
		// 	Random random = new Random();
		// 	cal.set(Calendar.HOUR_OF_DAY,random.nextInt(23 - 0 + 1) + 0);
		// 	historyList.add(new PurchaseRecord(111,"Gym "+i, cal, getRandomItem(bitmapList)));
		// }
		//
		// userData.setPurchaseRecords(historyList);

		recyclerView = getView().findViewById(R.id.history_recyclerview);
		recyclerView.setLayoutManager(new LinearLayoutManager(context));
		historyAdapter = new HistoryAdapter(context);
		recyclerView.setAdapter(historyAdapter);

		TextView textView = getView().findViewById(R.id.history_textview_title);
		textView.setText(TAG);

		TextView backgroundText = getView().findViewById(R.id.history_textview_title);
		userData.observe(getViewLifecycleOwner(), new Observer<UserData>()
		{
			@Override
			public void onChanged(UserData userData)
			{
				historyAdapter.notifyDataSetChanged();

				int size = historyAdapter.getItemCount();
				if (size == 0)
				{

					backgroundText.setVisibility(View.VISIBLE);
					backgroundText.setText("Ops, there's no record here!");
				}
				else
				{
					backgroundText.setVisibility(View.GONE);
				}
			}
		});

	}


	@Override
	public void onRefresh()
	{
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getContext(),"Refreshed!",Toast.LENGTH_SHORT).show();
				swipeRefreshLayout.setRefreshing(false);
			}
		},2000);


	}

	private <T> T getRandomItem(List<T> list){
		return  list.get(new Random().nextInt(list.size()));
	}
}