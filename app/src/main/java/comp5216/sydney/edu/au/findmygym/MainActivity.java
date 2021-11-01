package comp5216.sydney.edu.au.findmygym;

import static comp5216.sydney.edu.au.findmygym.model.UserData.KEY_TRAINER_times;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.instantsearch.voice.VoiceSpeechRecognizer;
import com.algolia.instantsearch.voice.ui.VoiceInputDialogFragment;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import comp5216.sydney.edu.au.findmygym.Utils.ImageUtil;
import comp5216.sydney.edu.au.findmygym.databinding.ActivityMainBinding;
import comp5216.sydney.edu.au.findmygym.model.Membership;
import comp5216.sydney.edu.au.findmygym.model.Gym;
import comp5216.sydney.edu.au.findmygym.model.PersonalTrainer;
import comp5216.sydney.edu.au.findmygym.model.PurchaseRecord;
import comp5216.sydney.edu.au.findmygym.model.Reservation;
import comp5216.sydney.edu.au.findmygym.model.Timeslot;
import comp5216.sydney.edu.au.findmygym.model.UserData;
import comp5216.sydney.edu.au.findmygym.model.callbacks.ListQueryCallback;
import comp5216.sydney.edu.au.findmygym.model.callbacks.ObjectQueryCallback;
import comp5216.sydney.edu.au.findmygym.ui.map.MapFragment;
import comp5216.sydney.edu.au.findmygym.ui.profile.ProfileFragment;
import comp5216.sydney.edu.au.findmygym.ui.schedule.ScheduleFragment;
import comp5216.sydney.edu.au.findmygym.ui.wallet.WalletFragment;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends BaseActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, VoiceSpeechRecognizer.ResultsListener
{
	private final String TAG = "[MainActivity]";
	private static final int REQUEST_CODE_ENABLE = 11;
	
	private Context mContext;
	private AppBarConfiguration mAppBarConfiguration;
	private ActivityMainBinding binding;
	private GoogleMap mMap;
	private UserData userData;
	private NavigationView navigationView;
	private View headerView;
	private DrawerLayout drawer;
	private FragmentManager fragmentManager;
	ViewPager2 viewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.mContext = this.getBaseContext();
		this.userData = UserData.getInstance();
		
		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		fragmentManager = getSupportFragmentManager();
		
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		drawer = binding.drawerLayout;
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();
		
		navigationView = binding.navView;
		navigationView.bringToFront();
		navigationView.setNavigationItemSelectedListener(this);
		// Passing each menu ID as a set of Ids because each
		// menu should be considered as top level destinations.
		mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_map, R.id.nav_schedule, R.id.nav_profile, R.id.nav_wallet)
				.setOpenableLayout(drawer)
				.build();
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
		NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
		NavigationUI.setupWithNavController(navigationView, navController);//???
		
		headerView = navigationView.getHeaderView(0);
		setObserver();//update nav_header
		setDrawerListener();//setup drawer event handler
		setFabListener();
		setSupportActionBar(binding.appBarMain.toolbar);
		userData.setContext(mContext);
		
		//TEST AREA
		
		// UserData.uploadingGymImg();
		// userData.getGymByID("8Pp4nlV5Fc3XW06BXkhV");
		//
		// userData.getTrainerByID("8tQCqe4ZECfRHolAejCW", new ObjectQueryCallback()
		// {
		// 	@Override
		// 	public void onSucceed(Object object)
		// 	{
		//
		// 		Log.d(TAG, "DBTEST: " + ((PersonalTrainer)object).toString() );
		// 	}
		//
		// 	@Override
		// 	public void onFailed(Exception e)
		// 	{
		// 		Log.e(TAG,e.toString());
		// 		e.printStackTrace();
		// 	}
		//
		// });
		String gym_id_1 = "8Pp4nlV5Fc3XW06BXkhV";
		String gym_id_2 = "AVRANcg0CBCAUBos8t0G";
		String gym_id_3 = "PANGsw8b7ufa9ANPq6bx";
		String gym_id_4 = "Q0JiYLebVbR4RrXVUJXi";
		// userData.addReservation(new Reservation(null,userData.getUserId(),gym_id_1,null, 500,new Timeslot(Calendar.getInstance(),120)));
		// userData.getReservationByID("A58BwZ0Ck53NlC35ND60", new ObjectQueryCallback()
		// {
		// 	@Override
		// 	public void onSucceed(Object object)
		// 	{
		// 		Log.d(TAG, "DBTEST: " + ((Reservation)object).toString() );
		// 	}
		//
		// 	@Override
		// 	public void onFailed(Exception e)
		// 	{
		// 		Log.e(TAG,e.toString());
		// 		e.printStackTrace();
		// 	}
		// });
		
		// userData.getReservationsByUID(UserData.getInstance().getUserId(),new ListQueryCallback()
		// {
		// 	@Override
		// 	public void onSucceed(ArrayList list)
		// 	{
		// 		Log.d(TAG, "DBTEST: getReservationsByUID" + list.toString() );
		// 	}
		//
		// 	@Override
		// 	public void onFailed(Exception e)
		// 	{
		// 		Log.e(TAG,e.toString());
		// 		e.printStackTrace();
		// 	}
		// });
		// initMembership();
		// userData.getMembershipsByUID(userData.getUserId(), new ListQueryCallback()
		// {
		// 	@Override
		// 	public void onSucceed(ArrayList list)
		// 	{
		// 		Log.d(TAG, "DBTEST: getMembershipsByUID" + list.toString());
		// 	}
		//
		// 	@Override
		// 	public void onFailed(Exception e)
		// 	{
		// 		Log.e(TAG, e.toString());
		// 		e.printStackTrace();
		// 	}
		// });
		
		
		//
		// userData.getPurchaseRecordsByUID(userData.getUserId(), new ListQueryCallback()
		// {
		// 	@Override
		// 	public void onSucceed(ArrayList list)
		// 	{
		// 		Log.d(TAG, "DBTEST: getPurchaseRecordsByUID" + list.toString());
		// 	}
		//
		// 	@Override
		// 	public void onFailed(Exception e)
		// 	{
		// 		Log.e(TAG, e.toString());
		// 		e.printStackTrace();
		// 	}
		// });
		// initPurchaseRecords();
		// uploadingGymImg();
		// uploadingTrainerImg();
		initMembership();
	}
	
	private <T> T getRandomItem(List<T> list)
	{
		return list.get(new Random().nextInt(list.size()));
	}
	
	public void uploadingTrainerImg()
	{
		List<Integer> resIDlist = Arrays.asList(R.drawable.diana, R.drawable.ai, R.drawable.azi, R.drawable.azi2, R.drawable.cat, R.drawable.dog, R.drawable.mea,
				R.drawable.shark1, R.drawable.shark2, R.drawable.onion, R.drawable.ybb, R.drawable.ybb2, R.drawable.fox, R.drawable.matsuri, R.drawable.la);
		Context context = this.getApplicationContext();
		
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		CollectionReference ref = db.collection(userData.KEY_TRAINERS);
		ref.get().addOnCompleteListener(task ->
		{
			if (task.isSuccessful())
			{
				try
				{
					List<DocumentSnapshot> docList = task.getResult().getDocuments();
					for (DocumentSnapshot doc : docList)
					{
						ImageUtil.uploadImage(doc.getId(), getRandomItem(resIDlist), context);
					}
					
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				Log.d(TAG, "upload Trainers Image failed!");
			}
		});
	
	}
	
	public void uploadingGymImg()
	{
		Context context = this.getApplicationContext();
		ImageUtil.uploadImage("8Pp4nlV5Fc3XW06BXkhV", R.drawable.gym1, context);
		ImageUtil.uploadImage("AVRANcg0CBCAUBos8t0G", R.drawable.gym2, context);
		ImageUtil.uploadImage("PANGsw8b7ufa9ANPq6bx", R.drawable.gym3, context);
		ImageUtil.uploadImage("Q0JiYLebVbR4RrXVUJXi", R.drawable.gym4, context);
		ImageUtil.uploadImage("S6imrUoT5OVYapOJVXqO", R.drawable.gym5, context);
		ImageUtil.uploadImage("U8lxg0yrlVnxbtREcDNo", R.drawable.gym6, context);
		ImageUtil.uploadImage("UlBO5BGMClwdHXPPl6mh", R.drawable.gym7, context);
		ImageUtil.uploadImage("YixOfdt2iITE1VBb51ge", R.drawable.gym8, context);
		ImageUtil.uploadImage("ZMao9oZR2Hf2VRDnwmwk", R.drawable.gym9, context);
		ImageUtil.uploadImage("efa3VHgYwiwaoyX0Hz6h", R.drawable.gym10, context);
	}
	
	public void initPurchaseRecords()
	{
		String gym_id_1 = "8Pp4nlV5Fc3XW06BXkhV";
		String gym_id_2 = "AVRANcg0CBCAUBos8t0G";
		String gym_id_3 = "PANGsw8b7ufa9ANPq6bx";
		String gym_id_4 = "Q0JiYLebVbR4RrXVUJXi";
		userData.addPurchaseRecord(new PurchaseRecord(null, 3000, userData.getUserId(), gym_id_1, Calendar.getInstance()));
		userData.addPurchaseRecord(new PurchaseRecord(null, 2400, userData.getUserId(), gym_id_2, Calendar.getInstance()));
		userData.addPurchaseRecord(new PurchaseRecord(null, 2100, userData.getUserId(), gym_id_3, Calendar.getInstance()));
		userData.addPurchaseRecord(new PurchaseRecord(null, 6200, userData.getUserId(), gym_id_4, Calendar.getInstance()));
	}
	
	
	public void initMembership()
	{
		String gym_id_1 = "8Pp4nlV5Fc3XW06BXkhV";
		String gym_id_2 = "AVRANcg0CBCAUBos8t0G";
		String gym_id_3 = "PANGsw8b7ufa9ANPq6bx";
		String gym_id_4 = "Q0JiYLebVbR4RrXVUJXi";
		Calendar cal1 = Calendar.getInstance();
		cal1.set(Calendar.MONTH, cal1.get(Calendar.MONTH) + 1);
		Calendar cal2 = Calendar.getInstance();
		cal2.set(Calendar.MONTH, cal2.get(Calendar.MONTH) + 3);
		Calendar cal3 = Calendar.getInstance();
		cal3.set(Calendar.MONTH, cal3.get(Calendar.MONTH) + 6);
		Calendar cal4 = Calendar.getInstance();
		cal4.set(Calendar.MONTH, cal4.get(Calendar.MONTH) + 12);
		userData.addMembership(new Membership("HVlJ5L9BYdfQeCYdG9QvHV7YsLz2", gym_id_1, "Monthly Plan", Calendar.getInstance(), cal1));
		userData.addMembership(new Membership("HVlJ5L9BYdfQeCYdG9QvHV7YsLz2", gym_id_2, "Seasonal Plan", Calendar.getInstance(), cal2));
		userData.addMembership(new Membership("HVlJ5L9BYdfQeCYdG9QvHV7YsLz2", gym_id_3, "Half-year Plan", Calendar.getInstance(), cal3));
		userData.addMembership(new Membership("HVlJ5L9BYdfQeCYdG9QvHV7YsLz2", gym_id_4, "Yearly Plan", Calendar.getInstance(), cal4));
	}
	
	
	//TEST
	@Override
	protected void onResume()
	{
		super.onResume();
		// Bitmap ybb = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ybb);
		// GifImageView navAvatar = (GifImageView) headerView.findViewById(R.id.header_avatar);
		// navAvatar.setImageBitmap(ybb);
	}
	
	
	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item)
	{
		// Handle navigation view item clicks here.
		int id = item.getItemId();
		item.setChecked(true);
		drawer.closeDrawers();
		Log.e(TAG, "onNavigationItemSelected: " + id);
		
		switch (id)
		{
			case R.id.nav_map:
				switchFragment(new MapFragment());
				break;
			case R.id.nav_schedule:
			{
				// transaction.replace(android.R.id.content, new ScheduleFragment(), "SCHEDULE_FRAGMENT").commit();
				switchFragment(new ScheduleFragment());
				break;
			}
			case R.id.nav_profile:
			{
				//switchFragment(new ProfileFragment());
				fragmentManager.beginTransaction().replace(android.R.id.content, new ProfileFragment(), "PROFILE_FRAGMENT").commit();
				break;
			}
			case R.id.nav_wallet:
			{
				switchFragment(new WalletFragment());
				break;
			}
		}
		// close navigation drawer
		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Toast.makeText(this, item.getItemId(), Toast.LENGTH_SHORT).show();
		Log.d(TAG, "onOptionsItemSelected:" + String.valueOf(item.getItemId()));
		
		switch (item.getItemId())
		{
			case Menu.FIRST://16908332
				Toast.makeText(this, "Setting Activity", Toast.LENGTH_SHORT).show();
				break;
		}
		return false;
	}
	
	@Override
	public void onOptionsMenuClosed(Menu menu)
	{
		// Toast.makeText(this, "选项菜单关闭了", Toast.LENGTH_SHORT).show();
	}
	
	// event before the menu is displayed
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		// Toast.makeText(this, "选项菜单显示之前onPrepareOptionsMenu方法会被调用，你可以用此方法来根据打当时的情况调整菜单", Toast.LENGTH_SHORT).show();
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
	
	private void setObserver()
	{
		userData.observe(this, new Observer<UserData>()
		{
			@Override
			public void onChanged(UserData userData)
			{
				Log.d(TAG, "===========> userData changed");
				TextView navUsername = (TextView) headerView.findViewById(R.id.header_userName);
				navUsername.setText(userData.getUserName());
				
				TextView navEmail = (TextView) headerView.findViewById(R.id.header_email);
				navEmail.setText(userData.getUserMail());
				
				GifImageView navAvatar = (GifImageView) headerView.findViewById(R.id.header_avatar);
				// navAvatar.setImageBitmap(userData.getUserAvatar());
				
				try
				{
					// ImageUtil.loadImage("azi",navAvatar,mContext);
					Glide.with(mContext)
							.load(userData.getUserAvatarUri())
							.placeholder(R.drawable.ic_launcher_background)
							.into(navAvatar);
				} catch (Exception e)
				{
					Log.d(TAG, "updateUserdata: " + e.toString());
					e.printStackTrace();
					Bitmap tempAvatar = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.diana);
					userData.setUserAvatar(tempAvatar);
					navAvatar.setImageBitmap(userData.getUserAvatar());
				}
			}
		});
	}
	
	
	private void setDrawerListener()
	{
		drawer.addDrawerListener(new DrawerLayout.DrawerListener()
		{
			@Override
			public void onDrawerSlide(@NonNull View drawerView, float slideOffset)
			{
				//Log.i(TAG, "onDrawerSlide");
			}
			
			@Override
			public void onDrawerOpened(@NonNull View drawerView)
			{
				Log.i(TAG, "onDrawerOpened");
			}
			
			@Override
			public void onDrawerClosed(@NonNull View drawerView)
			{
				Log.i(TAG, "onDrawerClosed");
			}
			
			@Override
			public void onDrawerStateChanged(int newState)
			{
				// Log.i(TAG, "onDrawerStateChanged"+newState);
			}
		});
	}
	
	private void setFabListener()
	{
		binding.appBarMain.fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				// Snackbar.make(view, "Replace with your own fab action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
				
				// int permission = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
				// if (permission != PackageManager.PERMISSION_GRANTED) {
				// 	ActivityCompat.requestPermissions(getApplicationContext(), PERMISSION_AUDIO, GET_RECODE_AUDIO);
				// }
				
				if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
				{
					ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);
				}
				else
				{
					VoiceInputDialogFragment voiceInputDialogFragment = new VoiceInputDialogFragment();
					voiceInputDialogFragment.setSuggestions(
							"find the closest gym",
							"schedule",
							"direction"
					);
					voiceInputDialogFragment.show(getSupportFragmentManager(), "DIALOG_INPUT");
					voiceInputDialogFragment.setAutoStart(true);
				}
				
				// userData.loadAllSimpleGyms();
			}
		});
	}
	
	public Fragment getVisibleFragment()
	{
		FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
		List<Fragment> fragments = fragmentManager.getFragments();
		for (Fragment fragment : fragments)
		{
			if (fragment != null && fragment.isVisible())
				return fragment;
		}
		return null;
	}
	
	private void switchFragment(Fragment targetFragment)
	{
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if (!targetFragment.isAdded())
		{
			transaction.hide(getVisibleFragment())
					.add(R.id.nav_host_fragment_content_main, targetFragment)
					.commit();
		}
		else
		{
			transaction.hide(getVisibleFragment())
					.show(targetFragment)
					.commit();
		}
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
		//		Gym gym = UserData.getInstance().findGymById("Minus Fitness Gym Chatswood");
		//		if (gym != null) {
		//			Intent intent = new Intent(mContext, GymActivity.class);
		//			intent.putExtra("gym", gym);
		//			startActivity(intent);
		//		}
		
		UserData.getInstance().getGymByID("8Pp4nlV5Fc3XW06BXkhV", new ObjectQueryCallback<Gym>()
		{
			@Override
			public void onSucceed(Gym gym)
			{
				Intent intent = new Intent(mContext, GymActivity.class);
				intent.putExtra("gym", gym);
				startActivity(intent);
			}
			
			@Override
			public void onFailed(Exception e)
			{
			
			}
		});
		
	}
	
	//	public void onAvatarClicked(View view)
	//	{
	//		userData.setUserName("YBB!");
	//	}
	//
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
	
	
	@Override
	public void onResults(@NonNull String[] strings)
	{
		Toast.makeText(getApplicationContext(), Arrays.toString(strings), Toast.LENGTH_LONG).show();
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			View v = getCurrentFocus();
			if (v instanceof EditText)
			{
				Rect outRect = new Rect();
				v.getGlobalVisibleRect(outRect);
				if (!outRect.contains((int) event.getRawX(), (int) event.getRawY()))
				{
					Log.d("focus", "touchevent");
					v.clearFocus();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			}
		}
		return super.dispatchTouchEvent(event);
	}
	
}