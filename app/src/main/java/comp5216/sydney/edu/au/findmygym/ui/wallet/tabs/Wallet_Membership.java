package comp5216.sydney.edu.au.findmygym.ui.wallet.tabs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vinaygaba.creditcardview.CreditCardView;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.model.CreditCard;
import comp5216.sydney.edu.au.findmygym.model.Membership;
import comp5216.sydney.edu.au.findmygym.model.UserData;
import comp5216.sydney.edu.au.findmygym.model.callbacks.ListQueryCallback;

public class Wallet_Membership extends Fragment
{
	private final String TAG = "[Wallet_Membership]";
	Context context;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		Log.e(TAG, "onCreateView: TEST=================");
		return inflater.inflate(R.layout.fragment_wallet_membership, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		this.context = getContext();
		

		Bitmap bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.diana);
		Bitmap bitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.ybb);
		Bitmap bitmap3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.azi);
		Bitmap bitmap4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.onion);
		Bitmap bitmap5 = BitmapFactory.decodeResource(context.getResources(), R.drawable.mea);
		List<Bitmap> bitmapList = Arrays.asList(bitmap1, bitmap2, bitmap3, bitmap4, bitmap5);
		
		
		//mock data
		// ArrayList<Membership> memberships = new ArrayList<>();
		// for (int i = 0; i < 3; i++)
		// {
		// 	Calendar start =  Calendar.getInstance();
		// 	Calendar end =  Calendar.getInstance();
		// 	Random random = new Random();
		// 	end.set(Calendar.DAY_OF_MONTH,end.get(Calendar.DAY_OF_MONTH)+1);
		// 	start.set(Calendar.HOUR_OF_DAY,random.nextInt(23 - 0 + 1) + 0);
		// 	memberships.add(new Membership(String.valueOf(i),"Membership"+i,start,end));
		// }
		
		
		DiscreteScrollView scrollView = getView().findViewById(R.id.wallet_membership_discreteScrollView);
		UserData userData = UserData.getInstance();
		
		userData.getMembershipsByUID(userData.getUserId(), new ListQueryCallback<Membership>()
		{
			@Override
			public void onSucceed(List<Membership> list)
			{
				Log.d(TAG, "getMembershipsByUID successfully!"+list.toString());
			}
			
			@Override
			public void onFailed(Exception e)
			{
				Log.e(TAG, e.toString());
				e.printStackTrace();
			}
		});
		
		MembershipAdapter membershipAdapter = new MembershipAdapter(context);
		// scrollView.setAdapter(membershipAdapter);
		InfiniteScrollAdapter infiniteScrollAdapter = InfiniteScrollAdapter.wrap(membershipAdapter);

		scrollView.setAdapter(infiniteScrollAdapter);
		
		scrollView.setOverScrollEnabled(true);
		scrollView.setItemTransformer(new ScaleTransformer.Builder()
				.setMaxScale(1.05f)
				.setMinScale(0.4f)
				.setPivotX(Pivot.X.LEFT) // CENTER is a default one
				.setPivotY(Pivot.Y.BOTTOM) // CENTER is a default one
				.build());
		
		scrollView.addOnItemChangedListener(new DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>()
		{
			@Override
			public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition)
			{
				
				int position = infiniteScrollAdapter.getRealPosition(adapterPosition);
				Log.e(TAG,"real: "+position);
				Log.e(TAG,"selected: "+adapterPosition);
				TextView gym = getView().findViewById(R.id.membership_textview_gym);
				gym.setText(membershipAdapter.getItem(position).getTitle());
				TextView start = getView().findViewById(R.id.membership_textview_startTime);
				start.setText("Member since: "+membershipAdapter.getItem(position).getStartTimeStr());
				TextView end = getView().findViewById(R.id.membership_textview_endTime);
				end.setText("Subscription ends at: "+membershipAdapter.getItem(position).getEndTimeStr());
			}
		});
		
		TextView backgroundText = getView().findViewById(R.id.membership_textview_title);
		userData.observe(getViewLifecycleOwner(), new Observer<UserData>()
		{
			@Override
			public void onChanged(UserData userData)
			{
				// historyAdapter.setHistoryArrayList(userData.getPurchaseRecords());
				membershipAdapter.notifyDataSetChanged();
				int size = membershipAdapter.getItemCount();
				if (size == 0)
				{
					backgroundText.setVisibility(View.VISIBLE);
					backgroundText.setText("Ops, you haven't subscribe to any gym!");
					getView().findViewById(R.id.membership_textview_gym).setVisibility(View.GONE);
					getView().findViewById(R.id.membership_textview_startTime).setVisibility(View.GONE);
					getView().findViewById(R.id.membership_textview_endTime).setVisibility(View.GONE);
				}
				else
				{
					backgroundText.setVisibility(View.GONE);
					getView().findViewById(R.id.membership_textview_gym).setVisibility(View.VISIBLE);
					getView().findViewById(R.id.membership_textview_startTime).setVisibility(View.VISIBLE);
					getView().findViewById(R.id.membership_textview_endTime).setVisibility(View.VISIBLE);
				}
			}
		});
	}
	
	private <T> T getRandomItem(List<T> list)
	{
		return list.get(new Random().nextInt(list.size()));
	}
}