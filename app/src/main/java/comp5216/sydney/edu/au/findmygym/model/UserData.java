package comp5216.sydney.edu.au.findmygym.model;

import android.graphics.Bitmap;
import android.se.omapi.Session;

import androidx.lifecycle.LiveData;

import com.google.firebase.storage.StorageReference;

public class UserData extends LiveData<UserData>
{
	private final String TAG = "[UserData]";
	
	private int timer;
	private String userName;
	private String userMail;
	private Bitmap userAvatar;
	private Session userSession;
	private StorageReference userStorageRef;
	
	public int getTimer()
	{
		return timer;
	}
	
	public void setTimer(int timer)
	{
		this.timer = timer;
		postValue(this);
	}
	
	public String getUserName()
	{
		return userName;
	}
	
	public void setUserName(String userName)
	{
		this.userName = userName;
		postValue(this);
	}
}