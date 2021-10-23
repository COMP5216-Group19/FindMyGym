package comp5216.sydney.edu.au.findmygym.ui.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.databinding.FragmentMapBinding;


public class MapFragment extends Fragment implements OnMapReadyCallback
{
	private final String TAG = "[MapFragment]";
	private GoogleMap mMap;
	private MapViewModel mapViewModel;
	private FragmentMapBinding binding;
	private SupportMapFragment mMapFragment;
	private SearchView mSearchView;

	private FusedLocationProviderClient mFusedLocationProviderClient;

	// The geographical location where the device is currently located. That is, the last-known location retrieved by the Fused Location Provider.
	private Location mLastKnownLocation = new Location("");
	private CameraPosition mCameraPosition;

	// A default location (Sydney, Australia) and default zoom to use when location permission is not granted.
	private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
	private static final int DEFAULT_ZOOM = 15;
	private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
	private boolean mLocationPermissionGranted = false;
	private TextView locationTextView;

	// Keys for storing activity state.
	private static final String KEY_CAMERA_POSITION = "camera_position";
	private static final String KEY_LOCATION = "location";



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
		if (savedInstanceState != null) {
			mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
			mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
		}
		locationTextView = getView().findViewById(R.id.text_map);
		mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

		SearchView mSearchView = getView().findViewById(R.id.idSearchView);
		mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				// on below line we are getting the
				// location name from search view.
				String location = mSearchView.getQuery().toString();

				// below line is to create a list of address
				// where we will store the list of all address.
				List<Address> addressList = null;

				// checking if the entered location is null or not.
				if (location != null || location.equals("")) {
					// on below line we are creating and initializing a geo coder.
					Geocoder geocoder = new Geocoder(getActivity());
					try {
						// on below line we are getting location from the
						// location name and adding that location to address list.
						addressList = geocoder.getFromLocationName(location, 1);
					} catch (IOException e) {
						e.printStackTrace();
					}
					// on below line we are getting the location
					// from our list a first position.
					Address address = addressList.get(0);

					// on below line we are creating a variable for our location
					// where we will add our locations latitude and longitude.
					LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

					// on below line we are adding marker to that position.
					mMap.addMarker(new MarkerOptions().position(latLng).title(location));

					// below line is to animate camera to that position.
					mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
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
	public void onMapReady(GoogleMap googleMap)
	{
		mMap = googleMap;
		getLocationPermission();
		updateLocationUI();
		getDeviceLocation();
		mMap.setMyLocationEnabled(true);

		// Add a marker in Sydney and move the camera
		LatLng gym1 = new LatLng(-33.79911, 151.1792);
		mMap.addMarker(new MarkerOptions()
				.position(gym1)
				.title("Gym1")
				.snippet("Minus Fitness Gym Chatswood")
				.icon(BitmapFromVector(getActivity().getApplicationContext(),R.drawable.ic_baseline_fitness_center_24)));
//		mMap.moveCamera(CameraUpdateFactory.newLatLng(gym1));
//		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gym1, DEFAULT_ZOOM));

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

							// Add a marker for my current location on the map
//							MarkerOptions marker = new MarkerOptions().position(
//									new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()))
//									.title("I am here");
//							mMap.addMarker(marker);
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


}