package comp5216.sydney.edu.au.findmygym.ui.wallet.tabs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vinaygaba.creditcardview.CardNumberFormat;
import com.vinaygaba.creditcardview.CardType;
import com.vinaygaba.creditcardview.CreditCardView;

import java.util.ArrayList;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.model.CreditCard;
import comp5216.sydney.edu.au.findmygym.model.PurchaseRecord;
import comp5216.sydney.edu.au.findmygym.model.UserData;

class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder>
{
	private final String TAG = "[CardAdapter]";
	
	private UserData userData;
	Context mContext;
	ArrayList<CreditCard> creditCardList;
	
	public interface OnItemLongClickListener {
		public boolean onItemLongClicked(int position);
	}
	
	public CardAdapter(Context mContext)
	{
		userData = UserData.getInstance();
		this.mContext = mContext;
		this.creditCardList = userData.getCreditCards();
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
		
		holder.creditCardView.setType(CardType.AUTO);
		holder.creditCardView.setCardNumberFormat(CardNumberFormat.MASKED_ALL_BUT_LAST_FOUR);
		holder.creditCardView.setCardName(creditCardList.get(position).getCardName());
		holder.creditCardView.setCardNumber(creditCardList.get(position).getCardNumber());
		holder.creditCardView.setExpiryDate(creditCardList.get(position).getExpiryDate());
		holder.creditCardView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Toast.makeText(mContext, "Clicked on" + creditCardList.get(position).getCardName(), Toast.LENGTH_SHORT).show();
			}
		});
		
		holder.creditCardView.setOnLongClickListener(new View.OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View view)
			{
				Toast.makeText(mContext, "Removed" + creditCardList.get(position).getCardName(), Toast.LENGTH_SHORT).show();
				userData.removeCreditCard(position);
				return false;
			}
		});
		

	}
	
	@Override
	public int getItemCount()
	{
		return creditCardList.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder{
		CreditCardView creditCardView;
		public ViewHolder(@NonNull View itemView)
		{
			super(itemView);
			creditCardView = itemView.findViewById(R.id.wallet_card_item_creditCardView);
		}
	}
}
