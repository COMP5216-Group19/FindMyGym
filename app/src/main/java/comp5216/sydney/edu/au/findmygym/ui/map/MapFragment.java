package comp5216.sydney.edu.au.findmygym.ui.map;

import android.Manifest;
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
import android.widget.Toast;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
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
	ArrayList<String> favList = new ArrayList<String>();

	private boolean isQueryingGym = false;

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
	
	RadioButton closest, favourite;


	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		mapViewModel =
				new ViewModelProvider(this).get(MapViewModel.class);

		binding = FragmentMapBinding.inflate(inflater, container, false);
		View root = binding.getRoot();

		final TextView textView = binding.textMap;
		mapViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
			@Override
			public void onChanged(@Nullable String s) {
				textView.setText(s);
			}
		});
		return root;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_mapView);
		if (savedInstanceState != null) {
			mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
			mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
		}
		locationTextView = getView().findViewById(R.id.text_map);

		mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

		FirebaseFirestore db = FirebaseFirestore.getInstance();
		DocumentReference ref = db.collection(userData.KEY_USERS).document(userData.getUserId());
		ref.get()
				.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
					@Override
					public void onComplete(@NonNull Task<DocumentSnapshot> task) {
						if (task.isSuccessful()) {

							DocumentSnapshot doc = task.getResult();
							favList = (ArrayList) doc.get(userData.KEY_USER_favourite);

							Log.d(TAG, "favourite gyms are"+favList);

						} else {
							Log.d(TAG, "check favourite gym failed!");
						}
					}
				});

		SearchView mSearchView = getView().findViewById(R.id.idSearchView);
		mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				// on below line we are getting the
				// location name from search view.
				String name = mSearchView.getQuery().toString();


				// checking if the entered location is null or not.
				if (name != null || name.equals("")) {
					// on below line we are creating and initializing a geo coder.
					Geocoder geocoder = new Geocoder(getActivity());

					for (Marker marker : markers) {
						String title = marker.getTitle();
						if (title.equals(name)) {
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
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
		mMapFragment.getMapAsync(this);
		

	}


	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		getLocationPermission();
		updateLocationUI();
		getDeviceLocation();
		userData.observe(getViewLifecycleOwner(), new Observer<UserData>()
		{
			@Override
			public void onChanged(UserData userData)
			{
				Log.d(TAG, "observe onChanged:" +userData.getAllSimpleGyms().toString());
				updateMarkers();
			}
		});
		if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
				PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
				Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

			ActivityCompat.requestPermissions(getActivity(),
					new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
					PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
			mMap.moveCamera(CameraUpdateFactory
					.newLatLngZoom(mDefaultLocation, 12));
		}else{
			mMap.setMyLocationEnabled(true);
		}


		mMap.setOnInfoWindowClickListener(this);

		Button filterButton = getView().findViewById(R.id.filter_button);
		Button resetButton = getView().findViewById(R.id.reset_button);
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
				List<String> favouriteGyms = UserData.getInstance().getFavouriteGyms();


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
							//hide others and show closet marker

							for (Marker marker : markers){
								String title = marker.getTitle();
								marker.setVisible(false);
								if (title.equals(userData.getAllSimpleGyms().get(closestdistance()).getGymName())){
									marker.setVisible(true);
									DEFAULT_ZOOM=14;
									mMap.animateCamera(CameraUpdateFactory
											.newLatLngZoom(marker.getPosition(), DEFAULT_ZOOM));
								}
							}
							finalDialog1.dismiss();
							resetButton.setVisibility(View.VISIBLE);
						}
						if(favourite.isChecked()){
							for (Marker marker : markers){
								String title = marker.getTitle();
								marker.setVisible(false);
								for (String id : favList){
									UserData.getInstance().getGymByID(id, new ObjectQueryCallback<Gym>()
									{
										@Override
										public void onSucceed(Gym gym)
										{
											if (gym.getGymName().equals(title)){
											marker.setVisible(true);
											DEFAULT_ZOOM=13;
											mMap.animateCamera(CameraUpdateFactory
													.newLatLngZoom(marker.getPosition(), DEFAULT_ZOOM));
										}
										}

										@Override
										public void onFailed(Exception e) { }
									});

								}

							}

							finalDialog1.dismiss();
							resetButton.setVisibility(View.VISIBLE);

						}

					}
				});


			}
		});
		resetButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				updateMarkers();
				resetButton.setVisibility(View.INVISIBLE);
			}
		});


//		calculatedistance();
	}
	
	private void updateMarkers()
	{
		mMap.clear();
		markers = new ArrayList<>();
		for(SimpleGym gym : userData.getAllSimpleGyms()){
			MarkerOptions markerOptions = new MarkerOptions()
					.position(new LatLng(gym.getLatitude(), gym.getLongitude()))
					.title(gym.getGymName())
					.snippet(gym.getAddress());
			markers.add(mMap.addMarker(markerOptions));
		};
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
	public void onInfoWindowClick(Marker marker) {
		if (isQueryingGym) {
			Toast.makeText(getContext(), R.string.map_querying_gym, Toast.LENGTH_SHORT).show();
			return;
		}
		String title = marker.getTitle();
		Log.d(TAG, title);
		Log.d(TAG, userData.getAllSimpleGyms().toString());
		String gymId = null;
		for (SimpleGym simpleGym : userData.getAllSimpleGyms()) {
			if (simpleGym.getGymName().equals(title)) {
				gymId = simpleGym.getGymId();
				break;
			}
		}

		if (gymId != null) {
			isQueryingGym = true;
			UserData.getInstance().getGymByID(gymId, new ObjectQueryCallback<Gym>() {
				@Override
				public void onSucceed(Gym gym) {
					isQueryingGym = false;
					Intent intent = new Intent(getContext(), GymActivity.class);
					intent.putExtra("gym", gym);
					startActivity(intent);
				}

				@Override
				public void onFailed(Exception e) {
					isQueryingGym = false;
					Toast.makeText(getContext(), R.string.gym_something_wrong, Toast.LENGTH_SHORT).show();
					Log.d(TAG, "failed to get gym", e);
				}
			});
		} else {
			Log.wtf(TAG, "Cannot find gym id with the clicked name/");
		}
	}

	public int closestdistance() {
		getDeviceLocation();

		for(int i = 0; i<userData.getAllSimpleGyms().size(); i++){

			Location markerLocation = new Location("");
			markerLocation.setLatitude(userData.getAllSimpleGyms().get(i).getLatitude());
			markerLocation.setLongitude(userData.getAllSimpleGyms().get(i).getLongitude());
			double distance = mLastKnownLocation.distanceTo(markerLocation);
			distancelist.add(distance);

		};

		return distancelist.indexOf(Collections.min(distancelist));
	}



}