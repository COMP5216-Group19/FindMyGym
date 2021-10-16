package comp5216.sydney.edu.au.findmygym;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.ActionBar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.List;

import comp5216.sydney.edu.au.findmygym.ui.login.LoginFragment;

public class LoginActivity extends BaseActivity
{
	private final String TAG = "[LoginActivity]";
	
	public Context mContext;
	private StorageReference mStorageRef;
	FirebaseUser firebaseUser;
	boolean isSignedIn = false;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		
		//Full screen
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
		// 	getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		// 	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// 	getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// } else {
		// 	getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// }
		
		//Hide menu bar
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.hide();
		}
		
		mContext = LoginActivity.this;
		
		if (savedInstanceState == null)
		{
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.gym_container, LoginFragment.newInstance())
					.commitNow();
		}
	}
	
	public void onLogin3rdClick(View view){
		List<AuthUI.IdpConfig> providers = Arrays.asList(
				new AuthUI.IdpConfig.EmailBuilder().build(),
				new AuthUI.IdpConfig.PhoneBuilder().build(),
				new AuthUI.IdpConfig.GoogleBuilder().build());
		Intent signInIntent = AuthUI.getInstance()
				.createSignInIntentBuilder()
				.setAvailableProviders(providers)
				.build();
		signInLauncher.launch(signInIntent);
	}
	
	/**
	 * listening to the login result
	 */
	private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
			new FirebaseAuthUIActivityResultContract(),
			new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>()
			{
				@Override
				public void onActivityResult(FirebaseAuthUIAuthenticationResult result)
				{
					onSignInResult(result);
				}
			}
	);
	
	/**
	 * action after getting different login result,
	 * if login success, register the sync service, and setup the daily sync alarm
	 * if login fail, nothing happened
	 */
	private void onSignInResult(FirebaseAuthUIAuthenticationResult result)
	{
		IdpResponse response = result.getIdpResponse();
		if (result.getResultCode() == RESULT_OK)
		{
			
			// Successfully signed in
			Log.d("[Login]", "RESULT_OK");
			
			//setup login info
			isSignedIn = true;
			firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
			
		}
		else
		{
			isSignedIn = false;
			Log.d("[Login]", "Login Failed!");
			Toast.makeText(mContext, "Login Failed!", Toast.LENGTH_LONG).show();
		}
	}
	
	public void onLoginButtonClick(View view)
	{
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
}