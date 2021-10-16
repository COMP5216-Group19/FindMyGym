package comp5216.sydney.edu.au.findmygym;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity
{
	private final String TAG = "[SettingsActivity]";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);
		if (savedInstanceState == null)
		{
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.settings, new SettingsFragment())
					.commit();
		}
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null)
		{
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		
		
	}
	
	public static class SettingsFragment extends PreferenceFragmentCompat
	{
		@Override
		public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
		{
			setPreferencesFromResource(R.xml.root_preferences, rootKey);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.e(TAG,item.toString());
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}