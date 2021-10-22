package comp5216.sydney.edu.au.findmygym.ui.wallet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WalletViewModel extends ViewModel
{
	private final String TAG = "[WalletViewModel]";
	
	private MutableLiveData<String> mText;
	
	public WalletViewModel()
	{
		mText = new MutableLiveData<>();
		mText.setValue("This is wallet fragment");
	}
	
	public LiveData<String> getText()
	{
		return mText;
	}
}