package comp5216.sydney.edu.au.findmygym.ui.wallet.tabs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.Utils.ImageUtil;
import comp5216.sydney.edu.au.findmygym.model.Gym;
import comp5216.sydney.edu.au.findmygym.model.PurchaseRecord;
import comp5216.sydney.edu.au.findmygym.model.UserData;
import comp5216.sydney.edu.au.findmygym.model.callbacks.ObjectQueryCallback;
import pl.droidsonroids.gif.GifImageView;

class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>
{
	private final String TAG = "[HistoryAdapter]";
	
	private UserData userData;
	Context context;
	
	public interface OnItemLongClickListener
	{
		public boolean onItemLongClicked(int position);
	}
	
	public HistoryAdapter(Context mContext)
	{
		userData = UserData.getInstance();
		this.context = mContext;
	}
	
	
	@NonNull
	@Override
	public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_wallet_history_item, parent, false);
		return new ViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position)
	{
		Log.d(TAG, "onBindViewHolder: "+this.getItemCount());
		
		String gymID = userData.getPurchaseRecords().get(position).getGymId();
		holder.tv_title.setText(userData.getPurchaseRecords().get(position).getGymId());
		holder.tv_description.setText(userData.getPurchaseRecords().get(position).getTimeStr());
		holder.tv_cost.setText(userData.getPurchaseRecords().get(position).getCostStr());
		
		UserData.getInstance().getGymByID(gymID, new ObjectQueryCallback<Gym>()
		{
			@Override
			public void onSucceed(Gym gym)
			{
				ImageUtil.loadImage(gymID, holder.giv_image, context);
				holder.tv_title.setText(gym.getGymName());
			}
			
			@Override
			public void onFailed(Exception e)
			{
				Log.e(TAG, e.toString());
				e.printStackTrace();
			}
		});
		
		holder.cardView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Toast.makeText(context, "Clicked on" + userData.getPurchaseRecords().get(position).getTitle(), Toast.LENGTH_SHORT).show();
				
			}
		});
		
		holder.cardView.setOnLongClickListener(new View.OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View view)
			{
				new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
						.setTitleText("Delete this record?")
						.setContentText("Deleted record CANNOT be recovered")
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
								CollectionReference reference = db.collection(userData.KEY_TRANSACTIONS);
								reference.document(userData.getPurchaseRecords().get(position).getID())
										.delete()
										.addOnSuccessListener(new OnSuccessListener<Void>()
										{
											@Override
											public void onSuccess(Void unused)
											{
												userData.removePurchaseRecord(position);
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
		Log.d(TAG, "TEST: getItemCount()"+userData.getPurchaseRecords().size());
		return userData.getPurchaseRecords().size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder
	{
		CardView cardView;
		GifImageView giv_image;
		TextView tv_title;
		TextView tv_description;
		TextView tv_cost;
		
		public ViewHolder(@NonNull View itemView)
		{
			super(itemView);
			cardView = itemView.findViewById(R.id.item_history_cardView);
			giv_image = itemView.findViewById(R.id.item_history_GifImageView_image);
			tv_title = itemView.findViewById(R.id.item_history_textview_title);
			tv_description = itemView.findViewById(R.id.item_history_textview_description);
			tv_cost = itemView.findViewById(R.id.item_history_textview_cost);
		}
	}
}
