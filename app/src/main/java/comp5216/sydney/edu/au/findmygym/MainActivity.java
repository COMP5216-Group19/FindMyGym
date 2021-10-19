package comp5216.sydney.edu.au.findmygym;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.StateListDrawable;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;

import java.net.URL;

import comp5216.sydney.edu.au.findmygym.databinding.ActivityMainBinding;
import comp5216.sydney.edu.au.findmygym.model.UserData;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends BaseActivity
{
	private final String TAG = "[MainActivity]";
	
	private Context mContext;
	private AppBarConfiguration mAppBarConfiguration;
	private ActivityMainBinding binding;
	private GoogleMap mMap;
	private UserData userData;
	public NavigationView navigationView;
	public View headerView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.mContext = this.getBaseContext();
		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		setSupportActionBar(binding.appBarMain.toolbar);
		binding.appBarMain.fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Snackbar.make(view, "Replace with your own fab action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});
		
		DrawerLayout drawer = binding.drawerLayout;
		navigationView = binding.navView;
		// Passing each menu ID as a set of Ids because each
		// menu should be considered as top level destinations.
		mAppBarConfiguration = new AppBarConfiguration.Builder(
				R.id.nav_home, R.id.nav_schedule, R.id.nav_profile)
				.setOpenableLayout(drawer)
				.build();
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
		NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
		NavigationUI.setupWithNavController(navigationView, navController);
		
		//update nav_header
		userData = UserData.getInstance();
		headerView = navigationView.getHeaderView(0);
		userData.observe(this, new Observer<UserData>()
		{
			@Override
			public void onChanged(UserData userData)
			{
				Log.e(TAG,"===========> userData changed");
				TextView navUsername = (TextView) headerView.findViewById(R.id.header_userName);
				navUsername.setText(userData.getUserName());
				
				TextView navEmail = (TextView) headerView.findViewById(R.id.header_email);
				navEmail.setText(userData.getUserMail());
				
				GifImageView navAvatar = (GifImageView)  headerView.findViewById(R.id.header_avatar);
				navAvatar.setImageBitmap(userData.getUserAvatar());
				
				try
				{
					Glide.with(mContext)
							.load(userData.getUserAvatarUri())
							.placeholder(R.drawable.ic_launcher_background)
							.into(navAvatar);
				} catch (Exception e)
				{
					Log.e(TAG, "updateUserdata: "+e.toString());
					e.printStackTrace();
					Bitmap tempAvatar = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.diana);
					userData.setUserAvatar(tempAvatar);
					navAvatar.setImageBitmap(userData.getUserAvatar());
				}
			}
		});
		
		
	}
	
	//TEST
	@Override
	protected void onResume()
	{
		super.onResume();
		Bitmap ybb = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ybb);
		GifImageView navAvatar = (GifImageView)  headerView.findViewById(R.id.header_avatar);
		navAvatar.setImageBitmap(ybb);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		Toast.makeText(this, item.getItemId(), Toast.LENGTH_SHORT).show();
		Log.e(TAG,String.valueOf(item.getItemId()));
		
		switch (item.getItemId()) {
			case Menu.FIRST://16908332
				Toast.makeText(this, "Setting Activity", Toast.LENGTH_SHORT).show();
				break;
		}
		return false;
	}
	
	@Override
	public void onOptionsMenuClosed(Menu menu) {
		Toast.makeText(this, "选项菜单关闭了", Toast.LENGTH_SHORT).show();
	}
	
	// event before the menu is displayed
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Toast.makeText(this, "选项菜单显示之前onPrepareOptionsMenu方法会被调用，你可以用此方法来根据打当时的情况调整菜单", Toast.LENGTH_SHORT).show();
		return true;
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onSupportNavigateUp()
	{
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
		return NavigationUI.navigateUp(navController, mAppBarConfiguration)
				|| super.onSupportNavigateUp();
	}
	
	
	public void onSettingMenuClicked(MenuItem item)
	{
		Intent intent = new Intent(mContext, SettingsActivity.class);
		startActivity(intent);
	}
	
	public void onGymMemuClicked(MenuItem item)
	{
		Intent intent = new Intent(mContext, GymActivity.class);
		startActivity(intent);
	}
	
	public void onAvatarClicked(View view)
	{
		userData.setUserName("YBB!");
	}
	
	public void onLogoutClicked(MenuItem item)
	{
		userData.logout();
		Intent intent = new Intent(mContext, LoginActivity.class);
		startActivity(intent);
	}
	
	
	// 传递数据
	// private void sendTestFunction()
	// {
	// 	MainFragment fragment = new MainFragment();
	// 	Bundle bundle = new Bundle();
	// 	bundle.putString("title", "DATA U ASKED");
	// 	fragment.setArguments(bundle);
	// 	FragmentManager fragmentManager = getSupportFragmentManager();
	// 	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
	// 	fragmentTransaction.replace(R.id.mainFragment, fragment);
	// 	fragmentTransaction.commit();
	// }
}