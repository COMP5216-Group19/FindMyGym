package comp5216.sydney.edu.au.findmygym;

import android.content.Context;
import android.content.Intent;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;

import comp5216.sydney.edu.au.findmygym.databinding.ActivityMainBinding;
import comp5216.sydney.edu.au.findmygym.model.UserData;

public class MainActivity extends BaseActivity implements OnMapReadyCallback
{
	private final String TAG = "[MainActivity]";
	
	private Context mContext;
	private AppBarConfiguration mAppBarConfiguration;
	private ActivityMainBinding binding;
	private GoogleMap mMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.mContext = this.getBaseContext();
		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		
		//YOU need to use API key to init the google map
		// SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
		// 		.findFragmentById(R.id.map);
		// mapFragment.getMapAsync(this);
		
		
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
		NavigationView navigationView = binding.navView;
		// Passing each menu ID as a set of Ids because each
		// menu should be considered as top level destinations.
		mAppBarConfiguration = new AppBarConfiguration.Builder(
				R.id.nav_home, R.id.nav_schedule, R.id.nav_profile)
				.setOpenableLayout(drawer)
				.build();
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
		NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
		NavigationUI.setupWithNavController(navigationView, navController);
		
		UserData userData = UserData.getInstance();
		String mail = userData.getUserMail();
		Log.e(TAG, "onViewCreated"+mail);
		
		View headerView = navigationView.getHeaderView(0);
		TextView navUsername = (TextView) headerView.findViewById(R.id.header_userName);
		navUsername.setText(UserData.getInstance().getUserName());
		TextView navEmail = (TextView) headerView.findViewById(R.id.header_email);
		navEmail.setText(UserData.getInstance().getUserMail());
		ImageView navAvatar = (ImageView)  headerView.findViewById(R.id.header_avatar);
		navAvatar.setImageBitmap(UserData.getInstance().getUserAvatar());
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		Toast.makeText(this, item.getItemId(), Toast.LENGTH_SHORT).show();

		switch (item.getItemId()) {
			case Menu.FIRST:
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
		Toast.makeText(this, "选项菜单显示之前onPrepareOptionsMenu方法会被调用，你可以用此方法来根据打当时的情况调整菜单", Toast.LENGTH_LONG).show();
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
	
	/**
	 * Manipulates the map once available.
	 * This callback is triggered when the map is ready to be used.
	 * This is where we can add markers or lines, add listeners or move the camera. In this case,
	 * we just add a marker near Sydney, Australia.
	 * If Google Play services is not installed on the device, the user will be prompted to install
	 * it inside the SupportMapFragment. This method will only be triggered once the user has
	 * installed Google Play services and returned to the app.
	 */
	@Override
	public void onMapReady(GoogleMap googleMap)
	{
		mMap = googleMap;
		
		// Add a marker in Sydney and move the camera
		LatLng sydney = new LatLng(-34, 151);
		// mMap.setMyLocationEnabled(true);
		mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
		mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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