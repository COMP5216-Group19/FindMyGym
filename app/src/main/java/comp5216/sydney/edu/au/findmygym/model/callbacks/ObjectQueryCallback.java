package comp5216.sydney.edu.au.findmygym.model.callbacks;

import comp5216.sydney.edu.au.findmygym.model.Gym;

public interface ObjectQueryCallback
{
	void onSucceed(Object object);
	void onFailed(Exception e);
}
