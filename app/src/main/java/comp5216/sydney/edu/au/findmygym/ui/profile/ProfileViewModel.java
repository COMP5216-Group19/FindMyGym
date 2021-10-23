package comp5216.sydney.edu.au.findmygym.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.findmygym.model.Gym;
import comp5216.sydney.edu.au.findmygym.model.UserData;

public class ProfileViewModel extends ViewModel
{
	private final String TAG = "[ProfileViewModel]";
	private List<Gym> gymList;
	private MutableLiveData<String> mText;

	private MutableLiveData<UserData> pUserData;
	
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