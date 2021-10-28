package comp5216.sydney.edu.au.findmygym.ui.map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import comp5216.sydney.edu.au.findmygym.GymActivity;
import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.databinding.FragmentMapBinding;
import comp5216.sydney.edu.au.findmygym.model.Gym;
import comp5216.sydney.edu.au.findmygym.model.SimpleGym;
import comp5216.sydney.edu.au.findmygym.model.UserData;
import comp5216.sydney.edu.au.findmygym.model.callbacks.ObjectQueryCallback;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowLongClickListener
{
	private final String TAG = "[MapFragment]";
	private GoogleMap mMap;
	private MapViewModel mapViewModel;
	private FragmentMapBinding binding;
	private SupportMapFragment mMapFragment;
	private SearchView mSearchView;
	Context context;
	
	private FusedLocationProviderClient mFusedLocationProviderClient;
	private UserData userData = UserData.getInstance();
	public List<Double> distancelist = new ArrayList<Double>();
	public List<Marker> markers = new ArrayList<>();
	
	// The geographical location where the device is currently located. That is, the last-known location retrieved by the Fused Location Provider.
	private Location mLastKnownLocation = new Location("");
	private CameraPosition mCameraPosition;
	
	// A default location (Sydney, Australia) and default zoom to use when location permission is not granted.
	private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
	private static int DEFAULT_ZOOM = 15;
	private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
	private boolean mLocationPermissionGranted = false;
	private TextView locationTextView;
	
	// Keys for storing activity state.
	private static final String KEY_CAMERA_POSITION = "camera_position";
	private static final String KEY_LOCATION = "location";
	
	public ArrayList<String> list = new ArrayList<String>();
	RadioButton closest, favourite;
	
	
	public View onCreateView(@NonNull LayoutInflater inflater,
	                         ViewGroup container, Bundle savedInstanceState)
	{
		mapViewModel =
				new ViewModelProvider(this).get(MapViewModel.class);
		
		binding = FragmentMapBinding.inflate(inflater, container, false);
		View root = binding.getRoot();
		
		final TextView textView = binding.textMap;
		mapViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>()
		{
			@Override
			public void onChanged(@Nullable String s)
			{
				textView.setText(s);
			}
		});
		return root;
	}
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		binding = null;
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		
		mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_mapView);
		if (savedInstanceState != null)
		{
			mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
			mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
		}
		locationTextView = getView().findViewById(R.id.text_map);
		
		mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
		
		SearchView mSearchView = getView().findViewById(R.id.idSearchView);
		mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
		{
			@Override
			public boolean onQueryTextSubmit(String query)
			{
				// on below line we are getting the
				// location name from search view.
				String name = mSearchView.getQuery().toString();
				
				
				// checking if the entered location is null or not.
				if (name != null || name.equals(""))
				{
					// on below line we are creating and initializing a geo coder.
					Geocoder geocoder = new Geocoder(getActivity());
					
					for (Marker marker : markers)
					{
						String title = marker.getTitle();
						if (title.equals(name))
						{
							mMap.animateCamera(CameraUpdateFactory
									.newLatLngZoom(marker.getPosition(), DEFAULT_ZOOM));
							Log.d(TAG, "latlong is" + marker.getPosition());
							Log.d(TAG, "name is" + name);
							Log.d(TAG, "title is" + title);
						}
						
					}
					
				}
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String newText)
			{
				return false;
			}
		});
		mMapFragment.getMapAsync(this);
	}
	
	public String findGymName(String id)
	{
		String name = "";
		if (userData.getAllSimpleGyms() == null)
		{
			return name;
		}
		for (int i = 0; i < userData.getAllSimpleGyms().size(); i++)
		{
			if (userData.getAllSimpleGyms().get(i).getGymId().equals(id))
			{
				name = userData.getAllSimpleGyms().get(i).getGymName();
			}
		}
		return name;
	}
	
	public String findGymAddress(String id)
	{
		String address = "";
		for (int i = 0; i < userData.getAllSimpleGyms().size(); i++)
		{
			if (userData.getAllSimpleGyms().get(i).getGymId().equals(id))
			{
				address = userData.getAllSimpleGyms().get(i).getAddress();
			}
		}
		return address;
	}
	
	public double findGymlat(String id)
	{
		double latitude = 0;
		for (int i = 0; i < userData.getAllSimpleGyms().size(); i++)
		{
			if (userData.getAllSimpleGyms().get(i).getGymId().equals(id))
			{
				latitude = userData.getAllSimpleGyms().get(i).getLatitude();
			}
		}
		return latitude;
	}
	
	public double findGymlong(String id)
	{
		double longitude = 0;
		for (int i = 0; i < userData.getAllSimpleGyms().size(); i++)
		{
			if (userData.getAllSimpleGyms().get(i).getGymId().equals(id))
			{
				longitude = userData.getAllSimpleGyms().get(i).getLongitude();
			}
		}
		return longitude;
	}
	
	
	@Override
	public void onMapReady(GoogleMap googleMap)
	{
		mMap = googleMap;
		getLocationPermission();
		updateLocationUI();
		getDeviceLocation();
		mMap.setMyLocationEnabled(true);
		mMap.setOnInfoWindowLongClickListener(this);

		if (userData.getAllSimpleGyms() == null) return;
		for (int i = 0; i < userData.getAllSimpleGyms().size(); i++){
			list.add(userData.getAllSimpleGyms().get(i).getGymId());
		}


		for(int i = 0; i<userData.getAllSimpleGyms().size(); i++){
			markers.add(mMap.addMarker(new MarkerOptions()
					.position(new LatLng(findGymlat(list.get(i)), findGymlong(list.get(i))))
					.title(findGymName(list.get(i)))
					.snippet(findGymAddress(list.get(i)))));

		};

		Button filterButton = getView().findViewById(R.id.filter_button);

		filterButton.setOnClickListener(new View.OnClickListener() {


			@Override
			public void onClick(View v) {

				AlertDialog dialog = null;

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				LayoutInflater inflater = getActivity().getLayoutInflater();
				View view = inflater.inflate(R.layout.marker_select, null);
				builder.setView(view);

				dialog = builder.create();
				dialog.show();
				closest = view.findViewById(R.id.radioButton1);
				favourite = view.findViewById(R.id.radioButton2);
				Button okButton = view.findViewById(R.id.okButton);
				Button cancelButton = view.findViewById(R.id.cancelButton);
				AlertDialog finalDialog = dialog;
				cancelButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						finalDialog.dismiss();
					}
				});
				AlertDialog finalDialog1 = dialog;
				okButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (closest.isChecked()) {
							//hide others and show closet markers
							//if (view.getId() == R.id.checkBox2){

							for (Marker marker : markers){
								String title = marker.getTitle();
								marker.setVisible(false);
								if (title.equals(findGymName(list.get(closestdistance())))){
									marker.setVisible(true);
									DEFAULT_ZOOM=14;
									mMap.animateCamera(CameraUpdateFactory
											.newLatLngZoom(marker.getPosition(), DEFAULT_ZOOM));
								}
							}
							finalDialog1.dismiss();


						}

					}
				});


			}
		});


//		calculatedistance();
	}

	public List<Marker> getList() {
		return markers;
	}

	public void onMarkerClick(MenuItem item)
	{
		Intent intent = new Intent(getActivity(), GymActivity.class);
		startActivity(intent);
	}



	private void getLocationPermission() {
		/*
		 * Request location permission, so that we can get the location of the
		 * device. The result of the permission request is handled by a callback,
		 * onRequestPermissionsResult.
		 */
		if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
				android.Manifest.permission.ACCESS_FINE_LOCATION)
				== PackageManager.PERMISSION_GRANTED) {
			mLocationPermissionGranted = true;
		} else {
			ActivityCompat.requestPermissions(getActivity(),
					new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
					PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
		}
	}
	private void updateLocationUI() {
		if (mMap == null) {
			return;
		}
		try {
			// Check if location permission is granted
			if (mLocationPermissionGranted) {
				mMap.getUiSettings().setMyLocationButtonEnabled(true); // false to disable my location button
				mMap.getUiSettings().setZoomControlsEnabled(true); // false to disable zoom controls
				mMap.getUiSettings().setCompassEnabled(true); // false to disable compass
				mMap.getUiSettings().setRotateGesturesEnabled(true); // false to disable rotate gesture
			} else {
				mMap.getUiSettings().setMyLocationButtonEnabled(false);
				mLastKnownLocation = null;
				getLocationPermission();
			}
		} catch (SecurityException e)  {
			Log.e("Exception: %s", e.getMessage());
		}
	}
	@SuppressLint("MissingPermission")
	private void getDeviceLocation() {
		try {
			if (mLocationPermissionGranted) {
				Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
				locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
					@Override
					public void onComplete(@NonNull Task<Location> task) {
						if (task.isSuccessful()) {
							// Obtain the current location of the device
							mLastKnownLocation = task.getResult();
							String currentOrDefault = "Current";

							if (mLastKnownLocation != null) {
								mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
										new LatLng(mLastKnownLocation.getLatitude(),
												mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
							} else {
								Log.d(TAG, "Current location is null. Using defaults.");
								currentOrDefault = "Default";
								mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
								mMap.getUiSettings().setMyLocationButtonEnabled(false);

								// Set current location to the default location
								mLastKnownLocation = new Location("");
								mLastKnownLocation.setLatitude(mDefaultLocation.latitude);
								mLastKnownLocation.setLongitude(mDefaultLocation.longitude);
							}

							// Show location details on the location TextView
							String msg = currentOrDefault + " Location: " +
									Double.toString(mLastKnownLocation.getLatitude()) + ", " +
									Double.toString(mLastKnownLocation.getLongitude());
							locationTextView.setText(msg);


						} else {
							Log.d(TAG, "Current location is null. Using defaults.");
							Log.e(TAG, "Exception: %s", task.getException());
							mMap.moveCamera(CameraUpdateFactory
									.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
							mMap.getUiSettings().setMyLocationButtonEnabled(false);
						}
					}
				});
			}
		} catch (SecurityException e)  {
			Log.e("Exception: %s", e.getMessage());
		}
	}


	private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
		// below line is use to generate a drawable.
		Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

		// below line is use to set bounds to our vector drawable.
		vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

		// below line is use to create a bitmap for our
		// drawable which we have added.
		Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

		// below line is use to add bitmap in our canvas.
		Canvas canvas = new Canvas(bitmap);

		// below line is use to draw our
		// vector drawable in canvas.
		vectorDrawable.draw(canvas);

		// after generating our bitmap we are returning our bitmap.
		return BitmapDescriptorFactory.fromBitmap(bitmap);
	}


	@Override
	public void onInfoWindowLongClick(Marker marker) {
		String title = marker.getTitle();
		Log.d(TAG, title);
		Log.d(TAG, list.toString());
		String gymId = null;
		for (SimpleGym simpleGym : userData.getAllSimpleGyms()) {
			if (simpleGym.getGymName().equals(title)) {
				gymId = simpleGym.getGymId();
				break;
			}
		}

		if (gymId != null) {
			UserData.getInstance().getGymByID(gymId, new ObjectQueryCallback() {
				@Override
				public void onSucceed(Object object) {
					Gym gym = (Gym) object;
					Intent intent = new Intent(getContext(), GymActivity.class);
					intent.putExtra("gym", gym);
					startActivity(intent);
				}

				@Override
				public void onFailed(Exception e) {
					Log.d(TAG, "failed to get gym", e);
				}
			});
		} else {
			Log.wtf(TAG, "Cannot find gym id with the clicked name/");
		}
	}

	public Integer closestdistance() {
		getDeviceLocation();

		for(int i = 0; i<userData.getAllSimpleGyms().size(); i++){

			Location markerLocation = new Location("");
			markerLocation.setLatitude(findGymlat(list.get(i)));
			markerLocation.setLongitude(findGymlong(list.get(i)));
			double distance = mLastKnownLocation.distanceTo(markerLocation);
			distancelist.add(distance);

		};

		return distancelist.indexOf(Collections.min(distancelist));
	}

//	AlertDialog dialog;
//	RadioButton closest, member;
//
//	public void filterTheMarkers(View view) {
//		if (dialog == null) {
//			AlertDialog.Builder builder;
//			builder = new AlertDialog.Builder(this);
//			LayoutInflater inflater = this.getLayoutInflater();
//			View checkBoxView = inflater.inflate(R.layout.marker_select, null);
//			builder.setView(checkBoxView);
//			closest = (RadioButton) findViewById(R.id.radioButton1);
//			member = (RadioButton) findViewById(R.id.radioButton2);
//			//Button okButton = (Button) checkBoxView.findViewById(R.id.okButton);
//			//Button cancelButton = (Button) checkBoxView.findViewById(R.id.cancelButton);
//			dialog = builder.create();
//		}
//		dialog.show();
//	}
//
////	public void displaySelectedMarkers(View view) {
////
////		dialog.dismiss();
////		Log.i("TAG", "member Status " + member.isChecked() + " closet Status  " + closest.isChecked());
////		//according these check boxes status execute your code to show/hide markers
//////		if (member.isChecked() && !closest.isChecked()) {
//////			//show member and hide other markers
//////			//if (view.getId() == R.id.checkBox1){
//////			for (Marker marker : memberlist){
//////				marker.setVisible(false);
//////			}
//////			//}
////		if (closest.isChecked()) {
////			//hide others and show closet markers
////			//if (view.getId() == R.id.checkBox2){
////			for (Marker marker : markers){
////				marker.setVisible(false);
////
////		}
////	}
//
//	public void doNothing(View view)
//	{ dialog.dismiss(); }

}