package comp5216.sydney.edu.au.findmygym.Utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.model.UserData;
import comp5216.sydney.edu.au.findmygym.model.callbacks.ObjectQueryCallback;

public class ImageUtil

{
	private static final String TAG = "[ImageUtils]";
	
	public static void loadImage(String imageName, ImageView imageView, Context context)
	{
		ContextWrapper cw = new ContextWrapper(context);
		File directory = cw.getDir("IMAGE", Context.MODE_PRIVATE);
		File file = new File(directory, imageName + ".jpg");
		if(file.exists()){
			Log.d(TAG, "Loading Local Image to ImageView..." + imageName);
			try
			{
				Log.d(TAG, "Loading Local Image from" + file.toString());
				Glide.with(context).asBitmap()
						.load(file)
						.centerCrop()
						.placeholder(android.R.drawable.progress_indeterminate_horizontal) // need placeholder to avoid issue like glide annotations
						.error(android.R.drawable.stat_notify_error)
						.into(imageView);
			} catch (Exception e)
			{
				Log.e(TAG, "Load image failed!", e);
			}
		}else {
			Log.d(TAG, "Loading Online Image to ImageView..." + imageName);
			UserData userData = UserData.getInstance();
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			StorageReference picRef;
			if (wifiNetworkInfo.isConnected())
			{
				picRef = FirebaseStorage.getInstance().getReferenceFromUrl(userData.URL_STORAGE_ORIGINAL_IMAGE + imageName + ".jpg");
			}
			else
			{
				picRef = FirebaseStorage.getInstance().getReferenceFromUrl(userData.URL_STORAGE_REDUCED_IMAGE + imageName + ".jpg");
			}
			
			picRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
			{
				@Override
				public void onSuccess(Uri uri)
				{
					try
					{
						Log.d(TAG, "Loaded image successfully!" + picRef.toString());
						Glide.with(context).asBitmap()
								.load(uri)
								.centerCrop()
								.placeholder(android.R.drawable.progress_indeterminate_horizontal) // need placeholder to avoid issue like glide annotations
								.error(android.R.drawable.stat_notify_error)
								.into(new CustomTarget<Bitmap>() {
									
									@Override
									public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition)
									{
										imageView.setImageBitmap(resource);
										ImageUtil.saveToInternalStorage(resource, imageName, context);
									}
									
									@Override
									public void onLoadCleared(@Nullable Drawable placeholder)
									{
										Log.d(TAG, "Save image successfully!" + picRef.toString());
									}
								});
					} catch (Exception e)
					{
						Log.e(TAG, "Load image failed!", e);
					}
				}
			}).addOnFailureListener(new OnFailureListener()
			{
				@Override
				public void onFailure(@NonNull Exception e)
				{
					Log.e(TAG, "onFailure: Load Image Uri Failed" + picRef.toString());
				}
			});
		}
		
	}
	
	
	public static String saveToInternalStorage(Bitmap bitmapImage, String name, Context context)
	{
		ContextWrapper cw = new ContextWrapper(context);
		//directory/data/user/0/comp5216.sydney.edu.au.findmygym/app_IMAGE
		File directory = cw.getDir("IMAGE", Context.MODE_PRIVATE);
		Log.d(TAG, "directory"+directory.toString());
		File mypath = new File(directory, name + ".jpg");
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream(mypath);
			bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				fos.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		Log.d(TAG, "saveToInternalStorage: " + directory.getAbsolutePath());
		return directory.getAbsolutePath();
	}
	
	public static void uploadImage_Original(String name, int resID, Context context)
	{
		//get hashcode from bitmap
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resID);
		int[] buffer = new int[bitmap.getWidth()*bitmap.getHeight()];
		bitmap.getPixels(buffer, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
		ByteBuffer bb = ByteBuffer.allocate(buffer.length*4);
		bb.asIntBuffer()
				.put(buffer);
		String hashcode = "";
		try
		{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(bb);
			BigInteger bi = new BigInteger(1, md5.digest());
			hashcode = bi.toString(16);
			
		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		
		Log.d("TAG", "HASHCODE: " + hashcode);
		
		//Check hashcode from server
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		DocumentReference hashImgRef = db.collection("HASHCODES").document("IMAGE");
		String finalHashcode = hashcode;
		hashImgRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
		{
			@Override
			public void onComplete(@NonNull Task<DocumentSnapshot> task)
			{
				if (task.isSuccessful())
				{
					
					ArrayList<String> hashcodeArrayList = (ArrayList<String>) task.getResult().get("ORIGINAL");
					Log.d(TAG, "ORIGINAL HASHCODE LIST " + hashcodeArrayList.toString());
					if (hashcodeArrayList.contains(finalHashcode))
					{
						Log.d(TAG, "Image original hashcode already exists! " + finalHashcode);
					}
					else
					{
						//Uploading image
						StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(UserData.getInstance().URL_STORAGE_ORIGINAL_IMAGE + name + ".jpg");
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
						byte[] data = baos.toByteArray();
						UploadTask uploadTask = picRef.putBytes(data);
						uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
						{
							@Override
							public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
							{
								Log.d(TAG, "Upload Original Image Successfully! :" + name);
								hashcodeArrayList.add(finalHashcode);
								Map<String, Object> map = new HashMap<>();
								map.put("ORIGINAL", hashcodeArrayList);
								hashImgRef.update(map).addOnSuccessListener(new OnSuccessListener<Void>()
								{
									@Override
									public void onSuccess(Void unused)
									{
										Log.d(TAG, "Add Original Image Hashcode Successfully: " + name + " - " + finalHashcode);
									}
								}).addOnFailureListener(new OnFailureListener()
								{
									@Override
									public void onFailure(@NonNull Exception e)
									{
										Log.d(TAG, "Add Original Image Hashcode Failed: " + name + " - " + finalHashcode);
									}
								});
								
							}
						}).
								addOnFailureListener(new OnFailureListener()
								{
									@Override
									public void onFailure(@NonNull Exception exception)
									{
										Log.e(TAG, "Upload Original Image Failed!" + name);
									}
								});
					}
				}
				else
				{
					Log.d(TAG, "Failed to fetch ORIGINAL HASHCODE LIST", task.getException());
				}
			}
		});
	}
	
	public static void uploadImage_Reduced(String name, int resID, Context context)
	{
		Log.d(TAG, "Uploading reduced image: " + name);
		//get hashcode from bitmap
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resID);
		int[] buffer = new int[bitmap.getWidth()*bitmap.getHeight()];
		bitmap.getPixels(buffer, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
		ByteBuffer bb = ByteBuffer.allocate(buffer.length*4);
		bb.asIntBuffer()
				.put(buffer);
		String hashcode = "";
		try
		{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(bb);
			BigInteger bi = new BigInteger(1, md5.digest());
			hashcode = bi.toString(16);
			
		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		
		Log.d("TAG", "HASHCODE: " + hashcode);
		
		//Check hashcode from server
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		DocumentReference hashImgRef = db.collection("HASHCODES").document("IMAGE");
		String finalHashcode = hashcode;
		hashImgRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
		{
			@Override
			public void onComplete(@NonNull Task<DocumentSnapshot> task)
			{
				if (task.isSuccessful())
				{
					
					ArrayList<String> hashcodeArrayList = (ArrayList<String>) task.getResult().get("REDUCED");
					Log.d(TAG, "REDUCED HASHCODE LIST " + hashcodeArrayList.toString());
					if (hashcodeArrayList.contains(finalHashcode))
					{
						Log.d(TAG, "Image Reduced hashcode already exists! " + finalHashcode);
					}
					else
					{
						//Uploading image
						StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(UserData.getInstance().URL_STORAGE_REDUCED_IMAGE + name + ".jpg");
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
						byte[] data = baos.toByteArray();
						UploadTask uploadTask = picRef.putBytes(data);
						uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
						{
							@Override
							public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
							{
								Log.d(TAG, "Upload Reduced Image Successfully! :" + name);
								hashcodeArrayList.add(finalHashcode);
								Map<String, Object> map = new HashMap<>();
								map.put("REDUCED", hashcodeArrayList);
								hashImgRef.update(map).addOnSuccessListener(new OnSuccessListener<Void>()
								{
									@Override
									public void onSuccess(Void unused)
									{
										Log.d(TAG, "Add Reduced Image Hashcode Successfully: " + name + " - " + finalHashcode);
									}
								}).addOnFailureListener(new OnFailureListener()
								{
									@Override
									public void onFailure(@NonNull Exception e)
									{
										Log.d(TAG, "Add Reduced Image Hashcode Failed: " + name + " - " + finalHashcode);
									}
								});
								
							}
						}).
								addOnFailureListener(new OnFailureListener()
								{
									@Override
									public void onFailure(@NonNull Exception exception)
									{
										Log.e(TAG, "Upload Reduced Image Failed!" + name);
									}
								});
					}
				}
				else
				{
					Log.d(TAG, "Failed to fetch Reduced HASHCODE LIST", task.getException());
				}
			}
		});
	}
	
	public static void uploadImage(String name, int resID, Context context)
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetworkInfo.isConnected())
		{
			uploadImage_Original(name, resID, context);
		}
		uploadImage_Reduced(name, resID, context);
	}
	
	public static void loadImage(Uri uri, ImageView imageView, Context context)
	{
		try
		{
			Glide.with(context)
					.load(uri)
					.diskCacheStrategy(DiskCacheStrategy.DATA)
					.placeholder(R.drawable.outline_account_circle_24)
					.into(imageView);
			Log.d(TAG, "Loaded image successfully!" + uri);
		} catch (Exception e)
		{
			Log.e(TAG, "Load image failed!", e);
		}
	}
	
	public static void loadAvatarByUid(String uid, ImageView imageView, Context context)
	{
		UserData.getInstance().getAvatarByUid(uid, new ObjectQueryCallback<Uri>()
		{
			@Override
			public void onSucceed(Uri object)
			{
				loadImage(object, imageView, context);
			}
			
			@Override
			public void onFailed(Exception e)
			{
				e.printStackTrace();
			}
		});
	}
}
