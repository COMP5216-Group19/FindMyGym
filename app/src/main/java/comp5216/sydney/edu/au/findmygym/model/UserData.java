package comp5216.sydney.edu.au.findmygym.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.se.omapi.Session;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

import comp5216.sydney.edu.au.findmygym.MainActivity;
import comp5216.sydney.edu.au.findmygym.R;

public class UserData extends LiveData<UserData>
{
	private final String TAG = "[UserData]";
	
	private ArrayList<PurchaseRecord> purchaseRecords;
	private ArrayList<CreditCard> creditCards;
	private ArrayList<Membership> memberships;
	private FirebaseUser firebaseUser;
	private String userName;
	private String userMail;
	private Bitmap userAvatar;
	private ArrayList<Integer> userFavGym;
	private Session userSession;
	private StorageReference userStorageRef;
	private Context mContext;
	
	private volatile static UserData UserData;
	
	/**
	 * Default Constructor
	 */
	public UserData()
	{
		this.purchaseRecords = new ArrayList<>(1);
	}
	
	/**
	 * DCL
	 */
	public static UserData getInstance()
	{
		if (UserData == null)
		{
			synchronized (UserData.class)
			{
				if (UserData == null)
				{
					UserData = new UserData();
				}
			}
		}
		return UserData;
	}
	
	/**
	 * PurchaseRecords
	 */
	
	public ArrayList<PurchaseRecord> getPurchaseRecords()
	{
		return purchaseRecords;
	}
	
	public void addPurchaseRecord(PurchaseRecord purchaseRecord)
	{
		this.purchaseRecords.add(purchaseRecord);
		sortPurchaseRecords();
		postValue(this);
	}
	
	public void setPurchaseRecords(ArrayList<PurchaseRecord> purchaseRecords)
	{
		this.purchaseRecords = purchaseRecords;
		sortPurchaseRecords();
		postValue(this);
	}
	
	public void removePurchaseRecord(int position)
	{
		this.purchaseRecords.remove(position);
		postValue(this);
	}
	
	/**
	 * CreditCards
	 */
	
	public ArrayList<CreditCard> getCreditCards()
	{
		return creditCards;
	}
	
	public void addCreditCard(CreditCard creditCard)
	{
		this.creditCards.add(0,creditCard);
		postValue(this);
	}
	
	public void setCreditCards(ArrayList<CreditCard> creditCards)
	{
		this.creditCards = creditCards;
		postValue(this);
	}
	
	public void removeCreditCard(int position)
	{
		this.creditCards.remove(position);
		Log.e(TAG, "removeCreditCard: "+this.getCreditCards() );
		postValue(this);
	}
	
	/**
	 * Memberships
	 */
	
	public ArrayList<Membership> getMemberships()
	{
		return memberships;
	}
	
	public void addMembership(Membership membership)
	{
		this.memberships.add(0,membership);
		postValue(this);
	}
	
	public void setMemberships(ArrayList<Membership> memberships)
	{
		this.memberships = memberships;
		postValue(this);
	}
	
	public void removeMembership(int position)
	{
		this.memberships.remove(position);
		postValue(this);
	}
	
	
	
	public FirebaseUser getFirebaseUser()
	{
		return firebaseUser;
	}
	
	public void setFirebaseUser(FirebaseUser firebaseUser)
	{
		this.firebaseUser = firebaseUser;
		postValue(this);
	}
	
	public String getUserName()
	{
		if (userName != null)
		{
			return userName;
		}
		else if(firebaseUser != null){
			return firebaseUser.getDisplayName();
		}
		return "GUEST: JOHN DOE";
	}
	
	public void setUserName(String userName)
	{
		this.userName = userName;
		postValue(this);
	}
	
	public String getUserMail()
	{
		if (userMail != null)
		{
			return userMail;
		}
		else if(firebaseUser != null){
			return firebaseUser.getEmail();
		}
		return "Go sign in, NOW!";
	}
	
	public void setUserMail(String userMail)
	{
		this.userMail = userMail;
		postValue(this);
	}
	
	public Bitmap getUserAvatar()
	{
		if (userAvatar != null)
		{
			return this.userAvatar;
		}
		// else if(firebaseUser != null){
		// 	return BitmapFactory()firebaseUser.getPhotoUrl();
		// }
		else
		{
			userAvatar = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.diana);
			return userAvatar;
		}
	}
	//
	// private Bitmap loadAvatar(){
	// 	Bitmap avatar = null;
	// 	try
	// 	{
	// 		URL photoUrl =  new URL(firebaseUser.getPhotoUrl().toString());
	// 		Log.e(TAG, "loadAvatar"+photoUrl.toString() );
	// 		 avatar = BitmapFactory.decodeStream(photoUrl.openConnection().getInputStream());
	// 		Picasso.with(context).load(imageUri).into(ivBasicImage);
	// 	} catch (Exception e)
	// 	{
	// 		Log.e(TAG, "updateUserdata: "+e.toString());
	// 		userAvatar = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.diana);
	// 	}
	// 	return avatar;
	// }
	
	public Uri getUserAvatarUri()
	{
		return this.firebaseUser.getPhotoUrl();
	}

	public void setUserAvatar(Bitmap userAvatar)
	{
		this.userAvatar = userAvatar;
		postValue(this);
	}

	public ArrayList<Integer> getUserFavGym() {
		if (userFavGym != null) {
			if (userFavGym.size() == 0) {
				userFavGym.add(10000);
			}
		} else {
			userFavGym = new ArrayList<Integer>();
			userFavGym.add(1000000);
		}
		return this.userFavGym;
	}

	public void setUserFavGym(ArrayList<Integer> userFavGym) {
		this.userFavGym = userFavGym;
		postValue(this);
	}
	
	public void setContext(Context mContext)
	{
		this.mContext = mContext;
	}
	
	@Override
	protected void onActive()
	{
		// 具有活跃的观察者时调用
		Log.d(TAG, "Get an observer!");
	}
	
	@Override
	protected void onInactive()
	{
		// 没有任何活跃的观察者时调用
		Log.d(TAG, "Get no observer!");
	}
	
	public void logout()
	{
		firebaseUser = null;
		userName = null;
		userMail = null;
		userAvatar = null;
		userSession = null;
		userStorageRef = null;
		mContext = null;
	}
	
	public void sortPurchaseRecords(){
		Log.e(TAG,"---------------------"+this.getPurchaseRecords().size());
		this.purchaseRecords.sort(new Comparator<PurchaseRecord>()
		{
			@Override
			public int compare(PurchaseRecord t1, PurchaseRecord t2)
			{
				Calendar c1 = t1.getTime();
				Calendar c2 = t2.getTime();
				if (c1.before(c2))
				{
					return 1;
				}
				if (c1.after(c2))
				{
					return -1;
				}
				return 0;
			}
		});
	}
}