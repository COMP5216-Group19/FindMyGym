package comp5216.sydney.edu.au.findmygym;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.AndroidException;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import comp5216.sydney.edu.au.findmygym.ui.gym.GymFragment;

public class GymActivity extends AppCompatActivity
{
	
	String gym_name;
	Context mContext;
	private JSONObject json;
	public boolean isFavourite;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gym_activity);
		mContext = GymActivity.this;
		
		isFavourite = false;//Check the received json.
		
		if (savedInstanceState == null)
		{
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.gym_container, GymFragment.newInstance())
					.commitNow();
		}
		

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				DialogInterface.OnClickListener setListener = null;
				AlertDialog.Builder optDialog = new AlertDialog.Builder(mContext);
				optDialog.setTitle("Confirm");
				optDialog.setMessage("Are u sure to leave?");
				optDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialogInterface, int i)
					{
						finish();
					}
				});
				optDialog.setNegativeButton("No", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialogInterface, int i)
					{
					
					}
				});
				optDialog.setIcon(android.R.drawable.stat_sys_warning)
						.show();
				
				return true;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onAddToFavouriteClicked(MenuItem item)
	{
		
		if(isFavourite){
			item.setIcon(android.R.drawable.btn_star_big_off);
			isFavourite = false;
			Toast.makeText(this.getBaseContext(),"Removed from favourite!",Toast.LENGTH_SHORT).show();
		}else{
			item.setIcon(android.R.drawable.btn_star_big_on);
			isFavourite = true;
			Toast.makeText(this.getBaseContext(),"Added to favourite!",Toast.LENGTH_SHORT).show();
		}
		
	}
}