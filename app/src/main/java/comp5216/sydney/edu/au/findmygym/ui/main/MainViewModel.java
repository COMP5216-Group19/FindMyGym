package comp5216.sydney.edu.au.findmygym.ui.main;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Timer;
import java.util.TimerTask;

public class MainViewModel extends ViewModel
{
	private final String TAG = "[MainViewModel]";
	
	private Timer timer;
	public int currentSecond;
	private MutableLiveData<Integer> userTimer;
	private MutableLiveData<String> userName;
	
	public void startTiming()
	{
		if (timer == null)
		{
			currentSecond = 0;
			timer = new Timer();
			TimerTask timerTask = new TimerTask()
			{
				@Override
				public void run()
				{
					currentSecond++;
					setUserTimer(currentSecond);
				}
			};
			timer.schedule(timerTask, 1000, 1000);//延迟3秒执行
		}
	}
	
	/**
	屏幕旋转
	 */
	@Override
	protected void onCleared()
	{
		super.onCleared();
		Log.d(TAG, "onCleared()");
		timer.cancel();
	}
	

	
	public MutableLiveData<Integer> getUserTimer()
	{
		if(userTimer == null){
			userTimer = new MutableLiveData<>();
		}
		return this.userTimer;
	}
	
	public void setUserTimer(int timer)
	{
		this.userTimer.postValue(timer);
	}
	
	public MutableLiveData<String> getUserName()
	{
		if(userName == null){
			userName = new MutableLiveData<>();
		}
		return userName;
	}
	
	public void setUserName(String userName)
	{
		this.userName.postValue(userName);
	}
	
}