package comp5216.sydney.edu.au.findmygym.ui.wallet.tabs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.Utils.ImageUtil;
import comp5216.sydney.edu.au.findmygym.model.Gym;
import comp5216.sydney.edu.au.findmygym.model.Membership;
import comp5216.sydney.edu.au.findmygym.model.UserData;
import comp5216.sydney.edu.au.findmygym.model.callbacks.ObjectQueryCallback;

/**
 * Created by yarolegovich on 07.03.2017.
 */

public class MembershipAdapter extends RecyclerView.Adapter<MembershipAdapter.ViewHolder> {
	
	private UserData userData = UserData.getInstance();
	private Context context;
	public MembershipAdapter(Context context) {
		this.context = context;
	}
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View v = inflater.inflate(R.layout.fragment_wallet_membership_item, parent, false);
		return new ViewHolder(v);
	}
	
	public Membership getItem(int position){
		return userData.getMemberships().get(position);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		// Glide.with(holder.itemView.getContext())
		// 		.load(userData.getMemberships().get(position).getImage())
		// 		.into(holder.image);
		String gymID = userData.getMemberships().get(position).getGymID();
		holder.title.setText(gymID);
		UserData.getInstance().getGymByID(gymID, new ObjectQueryCallback<Gym>()
		{
			@Override
			public void onSucceed(Gym gym)
			{
				ImageUtil.loadImage(gymID, holder.image, context);
				holder.title.setText(gym.getGymName());
			}

			@Override
			public void onFailed(Exception e)
			{

			}
		});
		
		
	}
	
	@Override
	public int getItemCount() {
		return userData.getMemberships().size();
	}
	
	static class ViewHolder extends RecyclerView.ViewHolder {
		
		private ImageView image;
		private TextView title;
		public ViewHolder(View itemView) {
			super(itemView);
			image = itemView.findViewById(R.id.wallet_membership_item_imageview);
			title = itemView.findViewById(R.id.wallet_membership_item_textview_title);
		}
	}
}
