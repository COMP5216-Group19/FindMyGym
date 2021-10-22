package comp5216.sydney.edu.au.findmygym.ui.profile;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.FileNotFoundException;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.databinding.FragmentProfileBinding;
import comp5216.sydney.edu.au.findmygym.model.UserData;

public class ProfileFragment extends Fragment
{
	private final String TAG = "[ProfileFragment]";
	private static final int PICK_IMAGE = 100;
	
	private ProfileViewModel profileViewModel;
	private FragmentProfileBinding binding;
	private UserData userData;
	private FragmentManager fragmentManager;

	public View onCreateView(@NonNull LayoutInflater inflater,
	                         ViewGroup container, Bundle savedInstanceState)
	{
		profileViewModel =
				new ViewModelProvider(this).get(ProfileViewModel.class);

		binding = FragmentProfileBinding.inflate(inflater, container, false);
		View root = binding.getRoot();
		//		final TextView textView = binding.textProfile;
		//		profileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>()
		//		{
		//			@Override
		//			public void onChanged(@Nullable String s)
		//			{
		//				textView.setText(s);
		//			}
		//		});
		return root;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		userData = UserData.getInstance();
		userData.setContext(this.getContext());
		FavGymAdapter favGymAdapter = new FavGymAdapter(userData.getFavouriteGyms());

		binding.avatarImage.setImageBitmap(userData.getUserAvatar());
		binding.nameText.setText(userData.getUserName());
		binding.emailText.setText(userData.getUserMail());
		binding.favGymRecycler.setAdapter(favGymAdapter);
		binding.favGymRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));

		ImageView avatar = binding.avatarImage;
		avatar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ProfileFragment.this.getContext())
						.setTitle("Changing avatar?")
						.setPositiveButton(R.string.confirm, (dialog, which) -> {
							openGallery();
						})
						.setNegativeButton(R.string.cancel, (dialog, which) -> {
							// Nothing happens
						});
				builder.create().show();
			}
		});

		EditText nickName = binding.nameText;
		String originalName = userData.getUserName();
		nickName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					Log.d("focus", "focus lost");
					MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ProfileFragment.this.getContext())
							.setTitle("Changing Username?")
							.setPositiveButton(R.string.confirm, (dialog, which) -> {
								userData.setUserName(nickName.getText().toString());
							})
							.setNegativeButton(R.string.cancel, (dialog, which) -> {
								nickName.setText(originalName);
							});
					builder.create().show();

				} else {
					Log.d("focus", "focused");
				}
			}
		});

		GraphView graph = (GraphView) getView().findViewById(R.id.profile_graphview);
		LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
				new DataPoint(0, 1),
				new DataPoint(1, 5),
				new DataPoint(2, 3),
				new DataPoint(3, 2),
				new DataPoint(4, 6)
		});
		graph.addSeries(series);
	}
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		binding = null;
	}

	private void openGallery() {
		Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
		startActivityForResult(gallery, PICK_IMAGE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
			Uri imageUri = data.getData();
			Bitmap bitmap = null;
			try {
				bitmap = BitmapFactory.decodeStream(this.getContext().getContentResolver().openInputStream(imageUri));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			binding.avatarImage.setImageURI(imageUri);
			userData.setUserAvatar(bitmap);
		}
	}
}