package comp5216.sydney.edu.au.findmygym.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.se.omapi.Session;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.storage.StorageReference;

import comp5216.sydney.edu.au.findmygym.MainActivity;
import comp5216.sydney.edu.au.findmygym.R;

public class UserData extends LiveData<UserData>
{
	private final String TAG = "[UserData]";
	
	private String userName;
	private String userMail;
	private Bitmap userAvatar;
	private Session userSession;
	private StorageReference userStorageRef;
	private Context mContext;
	
	private volatile static UserData UserData;
	
	/**
	 * Default Constructor
	 */
	public UserData()
	{
	
	}
	
	/**
	 * DCL
	 */
	public static UserData getInstance() {
		if (UserData == null) {
			synchronized (UserData.class) {
				if (UserData == null) {
					UserData = new UserData();
				}
			}
		}
		return UserData;
	}
	
	public String getUserName()
	{
		if(userName == null){
			userName = "Jane Doe";
			postValue(this);
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
			postValue(this);
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
			Bitmap  bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
			postValue(this);
		}
		return userAvatar;
	}
	
	public void setUserAvatar(Bitmap userAvatar)
	{
		this.userAvatar = userAvatar;
		postValue(this);
	}
	
	public void setContext(Context mContext)
	{
		this.mContext = mContext;
	}
	
	@Override
	protected void onActive() {
		// 具有活跃的观察者时调用
		Log.d(TAG, "Get an observer!");
	}
	
	@Override
	protected void onInactive() {
		// 没有任何活跃的观察者时调用
		Log.d(TAG, "Get no observer!");
	}
	
}