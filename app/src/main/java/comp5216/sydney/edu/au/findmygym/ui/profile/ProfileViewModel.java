package comp5216.sydney.edu.au.findmygym.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel
{
	private final String TAG = "[ProfileViewModel]";
	
	private MutableLiveData<String> mText;
	
	public ProfileViewModel()
	{
		mText = new MutableLiveData<>();
		mText.setValue("This is profile fragment");
	}
	
	public LiveData<String> getText()
	{
		return mText;
	}
}