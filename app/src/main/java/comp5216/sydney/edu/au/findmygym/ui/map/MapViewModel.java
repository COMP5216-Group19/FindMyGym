package comp5216.sydney.edu.au.findmygym.ui.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MapViewModel extends ViewModel
{
	private final String TAG = "[MapViewModel]";
	
	private MutableLiveData<String> mText;
	
	public MapViewModel()
	{
		mText = new MutableLiveData<>();
		mText.setValue("This is map fragment");
	}
	
	public LiveData<String> getText()
	{
		return mText;
	}
}