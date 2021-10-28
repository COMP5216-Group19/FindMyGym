package comp5216.sydney.edu.au.findmygym.model;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Membership
{
	String userID;
	String gymID;
	String title;
	Calendar startTime;
	Calendar endTime;
	
	
	public Membership(String userID, String gymID, String title, Calendar startTime, Calendar endTime)
	{
		this.userID = userID;
		this.gymID = gymID;
		this.title = title;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	@Override
	public String toString()
	{
		return "Membership{" +
				"userID='" + userID + '\'' +
				", gymID='" + gymID + '\'' +
				", title='" + title + '\'' +
				", startTime=" + startTime +
				", endTime=" + endTime +
				'}';
	}
	
	public String getUserID()
	{
		return userID;
	}
	
	public void setUserID(String userID)
	{
		this.userID = userID;
	}
	
	public String getGymID()
	{
		return gymID;
	}
	
	public void setGymID(String gymID)
	{
		this.gymID = gymID;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public Calendar getStartTime()
	{
		return startTime;
	}
	
	public String getStartTimeStr(){
		@SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(this.startTime.getTimeInMillis());
		// Log.d("[TEST]",cal.getTime().toString());
		return df.format(cal.getTime());
	}
	
	public void setStartTime(Calendar startTime)
	{
		this.startTime = startTime;
	}
	
	public Calendar getEndTime()
	{
		return endTime;
	}
	
	public String getEndTimeStr(){
		@SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(this.endTime.getTimeInMillis());
		// Log.d("[TEST]",cal.getTime().toString());
		return df.format(cal.getTime());
	}
	
	public void setEndTime(Calendar endTime)
	{
		this.endTime = endTime;
	}
	

	
	
}
