package comp5216.sydney.edu.au.findmygym.model.callbacks;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public interface ListQueryCallback<T>
{
	void onSucceed(ArrayList<T> list);
	void onFailed(Exception e);
}
