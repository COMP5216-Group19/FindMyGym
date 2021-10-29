package comp5216.sydney.edu.au.findmygym.model.callbacks;

import comp5216.sydney.edu.au.findmygym.model.Gym;

public interface ObjectQueryCallback<T>
{
	void onSucceed(T object);
	void onFailed(Exception e);
}
