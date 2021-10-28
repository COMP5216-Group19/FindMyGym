package comp5216.sydney.edu.au.findmygym.model;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class PurchaseRecord
{
	private int cost;
	private String gymId;
	private Calendar time;
	private Bitmap image;

	
	public PurchaseRecord(int cost, String gymId, Calendar time, Bitmap image)
	{
		this.cost = cost;
		this.gymId = gymId;
		this.time = time;
		this.image = image;
	}
	
	public int getCost()
	{
		return cost;
	}
	
	public void setCost(int cost)
	{
		this.cost = cost;
	}

	// Gym id
	public String getTitle()
	{
		return gymId;
	}
	
	public void setTitle(String title)
	{
		this.gymId = title;
	}

	
	public Calendar getTime()
	{
		return time;
	}
	
	public String getTimeStr()
	{
		@SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(this.time.getTimeInMillis());
		// Log.d("[TEST]",cal.getTime().toString());
		return df.format(cal.getTime());
	}
	
	
	public void setTime(Calendar time)
	{
		this.time = time;
	}
	
	public Bitmap getImage()
	{
		return image;
	}
	
	public void setImage(Bitmap image)
	{
		this.image = image;
	}
	
	public String getCostStr()
	{
		String ret = "$";
		ret += String.format("%.2f",(double)this.cost/100);
		return ret;
	}
}
