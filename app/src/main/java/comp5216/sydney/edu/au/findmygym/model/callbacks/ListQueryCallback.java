package comp5216.sydney.edu.au.findmygym.model.callbacks;

import java.util.List;

public interface ListQueryCallback<T>
{
	void onSucceed(List<T> list);
	void onFailed(Exception e);
}
