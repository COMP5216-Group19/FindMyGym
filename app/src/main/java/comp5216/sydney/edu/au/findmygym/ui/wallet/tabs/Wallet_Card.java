package comp5216.sydney.edu.au.findmygym.ui.wallet.tabs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.data.model.User;
import com.vinaygaba.creditcardview.CreditCardView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.model.CreditCard;
import comp5216.sydney.edu.au.findmygym.model.PurchaseRecord;
import comp5216.sydney.edu.au.findmygym.model.UserData;

public class Wallet_Card extends Fragment
{
	private final String TAG = "[Wallet_Card]";
	Context context;
	CreditCardView creditCardView;
	RecyclerView recyclerView;
	CardAdapter cardAdapter;
	UserData userData;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		Log.e(TAG, "onCreateView: TEST=================");
		return inflater.inflate(R.layout.fragment_wallet_card, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		this.context = getContext();
		
		TextView textView = getView().findViewById(R.id.card_textview_title);
		textView.setText(TAG);
		
		
		Bitmap bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.diana);
		Bitmap bitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.ybb);
		Bitmap bitmap3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.azi);
		Bitmap bitmap4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.onion);
		Bitmap bitmap5 = BitmapFactory.decodeResource(context.getResources(), R.drawable.mea);
		List<Bitmap> bitmapList = Arrays.asList(bitmap1,bitmap2,bitmap3,bitmap4,bitmap5);
		
		ArrayList<CreditCard> cards = new ArrayList<>();
		// cards.add(new CreditCard("5555555555554444", "master","0000"));
		
		for (int i = 0; i < 2; i++)
		{
			String str ="";
			for (int j = 0; j < 16; j++)
			{
				Random r = new Random();
				int result = r.nextInt(9-0) + 0;
				str+=String.valueOf(result);
			}
			cards.add(new CreditCard(str,"Card"+i, "0000"));
		}
		
		// userData.setPurchaseRecords(historyList);
		userData = UserData.getInstance();
		userData.setCreditCards(cards);
		recyclerView = getView().findViewById(R.id.card_recyclerview);
		recyclerView.setLayoutManager(new LinearLayoutManager(context));
		cardAdapter = new CardAdapter(context);
		recyclerView.setAdapter(cardAdapter);
		
		userData.observe(getViewLifecycleOwner(), new Observer<UserData>()
		{
			@Override
			public void onChanged(UserData userData)
			{
				// historyAdapter.setHistoryArrayList(userData.getPurchaseRecords());
				cardAdapter.notifyDataSetChanged();
			}
		});
		
		Button addCardBtn = getView().findViewById(R.id.wallet_card_button_addcard);
		addCardBtn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				userData.addCreditCard(new CreditCard("0000000000000000","Click to edit me!", "0000"));
			}
		});
	}
}