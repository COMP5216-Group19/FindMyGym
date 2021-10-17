package comp5216.sydney.edu.au.findmygym.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.se.omapi.Session;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.storage.StorageReference;

public class UserData extends LiveData<UserData>
{
	private final String TAG = "[UserData]";
	
	private String userName;
	private String userMail;
	private Bitmap userAvatar;
	private Session userSession;
	private StorageReference userStorageRef;
	
	private volatile static UserData UserData;
	
	/**
	 * Default Constructor
	 */
	private UserData(){
		//TODO
	}
	
	/**
	 * DCL
	 */
	public static UserData newInstance() {
		if (UserData == null) {
			synchronized (UserData.class) {
				if (UserData == null) {
					UserData = new UserData();
				}
			}
		}
		return UserData;
	}
	

	
	public static UserData getInstance() {
		return UserData.getInstance();
	}
	
	
	public String getUserName()
	{
		if(userName == null){
			userName = "Jane Doe";
		}
		return userName;
	}
	
	public void setUserName(String userName)
	{
		this.userName = userName;
		postValue(this);
	}
	
	public String getUserMail()
	{
		if(userMail == null){
			userMail = "xxxx@xxxx.com";
		}
		return userMail;
	}
	
	public void setUserMail(String userMail)
	{
		this.userMail = userMail;
		postValue(this);
	}
	
	public Bitmap getUserAvatar()
	{
		if(userAvatar == null){
			userAvatar = BitmapFactory.decodeStream(getClass().getResourceAsStream("/res/drawable/test.png"));
		}
		return userAvatar;
	}
	
	public void setUserAvatar(Bitmap userAvatar)
	{
		this.userAvatar = userAvatar;
		postValue(this);
	}
}