package comp5216.sydney.edu.au.findmygym.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.se.omapi.Session;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

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
	private ArrayList<Integer> favouriteGyms;
	private ArrayList<Reservation> reservations;
	private Session userSession;
	private StorageReference userStorageRef;
	private Context mContext;

	// todo: mock的list
	public List<Gym> allGyms;
	
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
					UserData.addMockGym();
					UserData.addMockReservations();
				}
			}
		}
		return UserData;
	}

	public void addMockGym() {
		allGyms = new ArrayList<>();
		Gym gym0 = new Gym(
				0,
				"Minus Fitness Gym Chatswood",
				null,
				null,
				null,
				"763 Pacific Hwy, Chatswood NSW 2067",
				"123-4567",
				-33.79911,
				151.1792,
				null,
				null
		);
		Gym gym1 = new Gym(
				1,
				"Minus Fitness Crows Nest",
				null,
				null,
				null,
				"400 Pacific Hwy, Crows Nest NSW 2065",
				"123-4567",
				-33.82581,
				151.19854,
				null,
				null
		);
		Gym gym2 = new Gym(
				2,
				"Fitness Second St Leonards",
				null,
				null,
				null,
				"55 Christie St, St Leonards NSW 2065",
				"123-4567",
				-33.82445,
				151.19584,
				null,
				null
		);
		Gym gym3 = new Gym(
				3,
				"Fitness Second North Sydney",
				null,
				null,
				null,
				"1 Elizabeth Plaza, North Sydney NSW 2060",
				"123-4567",
				-33.83945,
				151.20809,
				null,
				null
		);
		Gym gym4 = new Gym(
				4,
				"Fitness Second Bond St",
				null,
				null,
				null,
				"20 Bond St, Sydney NSW 2000",
				"123-4567",
				-33.86441,
				151.20829,
				null,
				null
		);
		Gym gym5 = new Gym(
				5,
				"Minus Fitness Market Street",
				null,
				null,
				null,
				"25 Market St, Sydney NSW 2000",
				"123-4567",
				-33.87115,
				151.20522,
				null,
				null
		);
		Gym gym6 = new Gym(
				6,
				"Minus Fitness Waterloo",
				null,
				null,
				null,
				"11A Lachlan St, Waterloo NSW 2017",
				"123-4567",
				-33.90103,
				151.21178,
				null,
				null
		);
		Gym gym7 = new Gym(
				7,
				"Notime Fitness North Sydney",
				null,
				null,
				null,
				"118 Walker St, North Sydney NSW 2060",
				"123-4567",
				-33.837711,
				151.208801,
				null,
				null
		);
		Gym gym8 = new Gym(
				8,
				"Notime Fitness City",
				null,
				null,
				null,
				"227 Elizabeth St, Sydney NSW 2000",
				"123-4567",
				-33.8706586,
				151.2102227,
				null,
				null
		);
		Gym gym9 = new Gym(
				9,
				"Sliver's Gym",
				null,
				null,
				null,
				"7-9 West St, North Sydney NSW 2060",
				"123-4567",
				-33.8334692,
				151.2052855,
				null,
				null
		);
		allGyms.add(gym0);
		allGyms.add(gym1);
		allGyms.add(gym2);
		allGyms.add(gym3);
		allGyms.add(gym4);
		allGyms.add(gym5);
		allGyms.add(gym6);
		allGyms.add(gym7);
		allGyms.add(gym8);
		allGyms.add(gym9);
	}

	public void addMockReservations()
	{
		reservations = new ArrayList<Reservation>();
		Reservation rev1 = new Reservation(
				new PersonalTrainer(1, "Marry", null),
				new Timeslot(CalendarUtil.stringToCalendar("2021-10-23 09:00"), 60)
		);
		Reservation rev2 = new Reservation(
				new PersonalTrainer(0, "Jack", null),
				new Timeslot(CalendarUtil.stringToCalendar("2021-10-20 09:00"), 60)
		);
		Reservation rev3 = new Reservation(
				new PersonalTrainer(0, "Jack", null),
				new Timeslot(CalendarUtil.stringToCalendar("2021-10-23 08:00"), 120)
		);

		reservations.add(rev1);
		reservations.add(rev2);
		reservations.add(rev3);
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

	public ArrayList<Integer> getFavouriteGyms() {
		if (favouriteGyms != null) {

		} else {
			favouriteGyms = new ArrayList<Integer>();
			favouriteGyms.add(1);
		}
		return this.favouriteGyms;
	}

	public void addToFavouriteGyms(int gymId) {
		favouriteGyms.add(gymId);
		// todo: 其他操作
	}

	public void removeFromFavouriteGyms(int gymId) {
		favouriteGyms.remove(Integer.valueOf(gymId));
		// todo: 其他操作
	}

	public void setFavouriteGyms(ArrayList<Integer> favouriteGyms) {
		this.favouriteGyms = favouriteGyms;
		postValue(this);
	}
	
	public void setContext(Context mContext)
	{
		this.mContext = mContext;
	}

	public ArrayList<Reservation> getReservations() {
		if (reservations != null) {

		} else {
			reservations = new ArrayList<Reservation>();
			//reservations.add(null, new Timeslot("",60 ));
		}
		return reservations;
	}

	public void setReservations(ArrayList<Reservation> reservations) {
		this.reservations = reservations;
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