package comp5216.sydney.edu.au.findmygym;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class BaseActivity extends AppCompatActivity
{
	private final String TAG = "[BaseActivity]";
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
	}
}