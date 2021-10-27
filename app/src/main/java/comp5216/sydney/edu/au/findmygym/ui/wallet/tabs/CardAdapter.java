package comp5216.sydney.edu.au.findmygym.ui.wallet.tabs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.vinaygaba.creditcardview.CardNumberFormat;
import com.vinaygaba.creditcardview.CardType;
import com.vinaygaba.creditcardview.CreditCardView;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.model.CreditCard;
import comp5216.sydney.edu.au.findmygym.model.UserData;

class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder>
{
	private final String TAG = "[CardAdapter]";
	
	private UserData userData;
	Context context;
	
	public interface OnItemLongClickListener
	{
		public boolean onItemLongClicked(int position);
	}
	
	public CardAdapter(Context mContext)
	{
		userData = UserData.getInstance();
		this.context = mContext;
	}
	
	
	@NonNull
	@Override
	public CardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_wallet_card_item, parent, false);
		return new ViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(@NonNull CardAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position)
	{
		// double ran = Math.random();
		// if (ran < 0.5)
		// {
		// 	holder.creditCardView.setCardFrontBackground(R.drawable.cardbackground_sky);
		// 	holder.creditCardView.setCardBackBackground(R.drawable.cardbackground_sky);
		// }
		// else
		// {
		// 	holder.creditCardView.setCardFrontBackground(R.drawable.cardbackground_world);
		// 	holder.creditCardView.setCardBackBackground(R.drawable.cardbackground_world);
		// }
		holder.creditCardView.setCardFrontBackground(R.drawable.cardbackground_sky);
		holder.creditCardView.setCardBackBackground(R.drawable.cardbackground_sky);
		holder.creditCardView.setIsEditable(false);
		holder.creditCardView.setIsFlippable(true);
		holder.creditCardView.setType(CardType.AUTO);
		holder.creditCardView.setCardNumberFormat(CardNumberFormat.MASKED_ALL_BUT_LAST_FOUR);
		holder.creditCardView.setCardName(userData.getCreditCards().get(position).getCardName());
		holder.creditCardView.setCardNumber(userData.getCreditCards().get(position).getCardNumber());
		holder.creditCardView.setExpiryDate(userData.getCreditCards().get(position).getExpiryDate());
		
		
		holder.creditCardView.setCardBackBackground(R.drawable.cardbackground_sky);
		holder.creditCardView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Log.e(TAG, "Clicked on getCardName" + userData.getCreditCards().get(position).getCardName());
				Log.e(TAG, "Clicked on getCardNumber " + userData.getCreditCards().get(position).getCardNumber());
				Log.e(TAG, "Clicked on getExpiryDate" + userData.getCreditCards().get(position).getExpiryDate());
			}
		});
		
		holder.creditCardView.setOnLongClickListener(new View.OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View view)
			{
				new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
						.setTitleText("Delete this card?")
						.setContentText("Deleted card CANNOT be recovered")
						.setCancelText("NO")
						.setConfirmText("YES")
						.showCancelButton(true)
						// .setConfirmButtonBackgroundColor(R.color.red_100)
						// .setConfirmButtonTextColor(R.color.red_100)
						.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener()
						{
							@Override
							public void onClick(SweetAlertDialog sDialog)
							{
								sDialog.dismissWithAnimation();
							}
						})
						.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
						{
							@Override
							public void onClick(SweetAlertDialog sDialog)
							{
								FirebaseFirestore db = FirebaseFirestore.getInstance();
								CollectionReference cardsRef = db.collection("CARDS");
								cardsRef.document(userData.getCreditCards().get(position).getId())
										.delete()
										.addOnSuccessListener(new OnSuccessListener<Void>()
										{
											@Override
											public void onSuccess(Void unused)
											{
												userData.removeCreditCard(position);
												// notifyDataSetChanged();
												sDialog.setTitleText("Deleted!")
														.setContentText("Your record has been deleted!")
														.setConfirmClickListener(null)
														.showCancelButton(false)
														.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
											}
										})
										.addOnFailureListener(new OnFailureListener()
										{
											@Override
											public void onFailure(@NonNull Exception e)
											{
												sDialog.setTitleText("Failed!")
														.setContentText("Your record has NOT been deleted somehow!")
														.setConfirmClickListener(null)
														.showCancelButton(false)
														.changeAlertType(SweetAlertDialog.ERROR_TYPE);
											}
										});
								
								
							}
						})
						.show();
				return false;
			}
		});
		
		
	}
	
	@Override
	public int getItemCount()
	{
		return userData.getCreditCards().size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder
	{
		CreditCardView creditCardView;
		
		public ViewHolder(@NonNull View itemView)
		{
			super(itemView);
			creditCardView = itemView.findViewById(R.id.wallet_card_item_creditCardView);
		}
	}
}
