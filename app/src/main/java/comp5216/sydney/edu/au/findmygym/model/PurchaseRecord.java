package comp5216.sydney.edu.au.findmygym.model;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class PurchaseRecord
{
	private String ID;
	private int cost;
	private String userID;
	private String gymId;
	private Calendar time;
	
	public PurchaseRecord(String ID, int cost, String userID, String gymId, Calendar time)
	{
		this.ID = ID;
		this.cost = cost;
		this.userID = userID;
		this.gymId = gymId;
		this.time = time;
	}
	
	@Override
	public String toString()
	{
		return "PurchaseRecord{" +
				"ID='" + ID + '\'' +
				", cost=" + cost +
				", userID='" + userID + '\'' +
				", gymId='" + gymId + '\'' +
				", time=" + time +
				'}';
	}
	
	public String getID()
	{
		return ID;
	}
	
	public void setID(String ID)
	{
		this.ID = ID;
	}
	
	public String getUserID()
	{
		return userID;
	}
	
	public void setUserID(String userID)
	{
		this.userID = userID;
	}
	
	public String getGymId()
	{
		return gymId;
	}
	
	public void setGymId(String gymId)
	{
		this.gymId = gymId;
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
	
	
	public String getCostStr()
	{
		String ret = "$";
		ret += String.format("%.2f",(double)(this.cost/100));
		return ret;
	}
}
