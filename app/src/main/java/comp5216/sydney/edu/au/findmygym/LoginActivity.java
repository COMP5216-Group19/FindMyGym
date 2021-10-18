package comp5216.sydney.edu.au.findmygym;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import comp5216.sydney.edu.au.findmygym.model.UserData;
import comp5216.sydney.edu.au.findmygym.ui.login.LoginFragment;

public class LoginActivity extends BaseActivity
{
	private static final int RC_SIGN_IN = 123;
	private final String TAG = "[LoginActivity]";
	private FirebaseAuth mAuth;
	
	public Context mContext;
	private StorageReference mStorageRef;
	FirebaseUser firebaseUser;
	boolean isSignedIn = false;
	private GoogleSignInClient mGoogleSignInClient;
	private UserData userData;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		mAuth = FirebaseAuth.getInstance();
		mContext = getApplicationContext();
		userData = UserData.getInstance();
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
		
		
		if (savedInstanceState == null)
		{
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.gym_container, LoginFragment.newInstance())
					.commitNow();
		}
		
		//get google auth request
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(getString(R.string.default_web_client_id))
				.requestEmail()
				.build();
		mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
		
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
			Log.d(TAG, "Login Successfully!");
			Toast.makeText(mContext, "Login Successfully!" + "\n" + "Hi," + firebaseUser.getDisplayName(), Toast.LENGTH_LONG).show();
		}
		else
		{
			isSignedIn = false;
			Log.d(TAG, "Login Failed!");
			Toast.makeText(mContext, "Login Failed!", Toast.LENGTH_LONG).show();
		}
	}
	
	public void onLoginButtonClick(View view)
	{
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
	public void onGoogleClicked(View view)
	{
		Log.e(TAG, "onGoogleClicked: clicked!");
		signIn();
	}
	
	public void onAppIconClicked(View view)
	{
		Log.e(TAG, "onGoogleClicked: clicked!");
	}
	
	public void signIn() {
		Intent signInIntent = mGoogleSignInClient.getSignInIntent();
		startActivityForResult(signInIntent, RC_SIGN_IN);
	}
	
	
	@Override
	public void onStart()
	{
		super.onStart();
		// Start Login if necessary
		FirebaseUser currentUser = mAuth.getCurrentUser();
		if (firebaseUser != null)
		{
			Log.d("[Login]", String.valueOf(isSignedIn));
			intentToMain();
		}

	}
	
	
	/**
	 * initialize login launcher
	 */
	private void startSignIn()
	{
		Log.d("[Login]", "startSignIn()");
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
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
		if (requestCode == RC_SIGN_IN) {
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			try {
				// Google Sign In was successful, authenticate with Firebase
				GoogleSignInAccount account = task.getResult(ApiException.class);
				Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
				firebaseAuthWithGoogle(account.getIdToken());
				
			} catch (ApiException e) {
				// Google Sign In failed, update UI appropriately
				Log.w(TAG, "Google sign in failed", e);
				Toast.makeText(mContext, "Login Failed!", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	private void firebaseAuthWithGoogle(String idToken) {
		AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
		mAuth.signInWithCredential(credential)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							// Sign in success, update UI with the signed-in user's information
							Log.d(TAG, "signInWithCredential:success");
							firebaseUser = mAuth.getCurrentUser();
							updateUserdata();
							Toast.makeText(mContext, "Login Successfully!" + "\n" + "Hi," + firebaseUser.getDisplayName(), Toast.LENGTH_LONG).show();
							// updateUI(user);
							intentToMain();
						} else {
							// If sign in fails, display a message to the user.
							Log.w(TAG, "signInWithCredential:failure", task.getException());
							Toast.makeText(mContext, "Auth Failed!", Toast.LENGTH_LONG).show();
						}
					}
				});
	}
	
	private void intentToMain(){
		Intent intent = new Intent(mContext, MainActivity.class);
		startActivity(intent);
	}
	
	private void updateUserdata(){
		userData.setContext(mContext);
		userData.setFirebaseUser(firebaseUser);
	}
	

}