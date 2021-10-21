package comp5216.sydney.edu.au.findmygym.ui.wallet.tabs;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vinaygaba.creditcardview.CreditCardView;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.model.CreditCard;

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
		
		TextView textView = getView().findViewById(R.id.membership_textview_title);
		textView.setText(TAG);
		CreditCardView creditCardView = getView().findViewById(R.id.card1);
		creditCardView.setCardBackBackground(R.drawable.card_background_signature);
	}
}