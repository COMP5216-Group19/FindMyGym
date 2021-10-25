package comp5216.sydney.edu.au.findmygym.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.se.omapi.Session;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.model.callbacks.GymQueryCallback;
import comp5216.sydney.edu.au.findmygym.model.callbacks.TrainerQueryCallback;
import comp5216.sydney.edu.au.findmygym.ui.gym.GymViewModel;

public class UserData extends LiveData<UserData>
{
	private final String TAG = "[UserData]";
	
	private ArrayList<PurchaseRecord> purchaseRecords;
	private ArrayList<ScheduleList> scheduleLists;
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
	private StorageReference gymPictureRef;
	private StorageReference trainerAvatarRef;
	private FirebaseDatabase database;
	private DatabaseReference dbRef;
	private DatabaseReference gymsRef;
	private DatabaseReference trainersRef;
	private Context mContext;

	// This list will load when launching this app
	// Displays these gyms on map
	// TODO: 让这个list在loading界面load，load完了再进map
	// TODO: 或者观察这个list，随list更新map
	private List<SimpleGym> allSimpleGyms;

	// todo: mock的list
	public List<Gym> allGyms;
	public List<PersonalTrainer> allTrainers;
	
	private volatile static UserData UserData;
	
	/**
	 * Default Constructor
	 */
	private UserData()
	{
		this.purchaseRecords = new ArrayList<>();
		this.scheduleLists = new ArrayList<>(1);
		database = FirebaseDatabase.getInstance();
		dbRef = database.getReference();
		gymsRef = dbRef.child("gyms");
		trainersRef = dbRef.child("trainers");
		FirebaseStorage storage = FirebaseStorage.getInstance();
		gymPictureRef = storage.getReference("gymPictures");
		trainerAvatarRef = storage.getReference("trainerAvatars");

		loadGymsInfo();
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
//					UserData.addMockGym();
					UserData.addMockReservations();

				}
			}
		}
		return UserData;
	}

	private void loadGymsInfo() {
		allSimpleGyms = new ArrayList<>();
		gymsRef.get().addOnSuccessListener(dataSnapshot -> {
			for (DataSnapshot ds : dataSnapshot.getChildren()) {
				Gym.GymData gd = ds.getValue(Gym.GymData.class);
				if (gd != null) {
					allSimpleGyms.add(SimpleGym.fromData(gd));
				}
			}
			// TODO: load finished
			System.out.println(allSimpleGyms);
		}).addOnFailureListener(e -> {
			Log.e(TAG, Arrays.toString(e.getStackTrace()));
		});
	}

	/*
	Methods for mock database
	 */

	private void addGymToDatabase(Gym gym) {
		// Name of gym picture
		StorageReference pictureRef = gymPictureRef.child(gym.getGymId() + ".jpg");

		// upload gym picture
		if (gym.getGymPhoto() != null) {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			gym.getGymPhoto().compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
			byte[] data = outputStream.toByteArray();
			UploadTask task = pictureRef.putBytes(data);
			task.addOnFailureListener(e -> {
				Log.e(TAG, Arrays.toString(e.getStackTrace()));
			}).addOnSuccessListener(taskSnapshot -> {
				gymsRef.child(String.valueOf(gym.getGymId()))
						.setValue(gym.toData(pictureRef.getDownloadUrl().getResult().toString()));
			});
		} else {
			gymsRef.child(String.valueOf(gym.getGymId()))
					.setValue(gym.toData(null));
		}
	}

	private void addTrainerToDatabase(PersonalTrainer trainer) {
		// Name of gym picture
		StorageReference pictureRef = trainerAvatarRef.child(trainer.getTrainerId() + ".jpg");

		// upload gym picture
		if (trainer.getAvatar() != null) {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			trainer.getAvatar().compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
			byte[] data = outputStream.toByteArray();
			UploadTask task = pictureRef.putBytes(data);
			task.addOnFailureListener(e -> {
				Log.e(TAG, Arrays.toString(e.getStackTrace()));
			}).addOnSuccessListener(taskSnapshot -> {
				trainersRef.child(String.valueOf(trainer.getTrainerId()))
						.setValue(trainer.toData(
								pictureRef.getDownloadUrl().getResult().toString()));
			});
		} else {
			trainersRef.child(String.valueOf(trainer.getTrainerId()))
					.setValue(trainer.toData(null));
		}
	}

	private void addMockTrainersInThisWeek(Gym gym, int trainerId,
										   String trainerName, double price) {

		Calendar cal = GymViewModel.beginOfADay(Calendar.getInstance());
		Calendar openTime = gym.getOpenTime();
		cal.set(Calendar.HOUR_OF_DAY, openTime.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, openTime.get(Calendar.MINUTE));
		double openHours = (double) (gym.getCloseTime().getTimeInMillis() -
				openTime.getTimeInMillis()) / 3_600_000;
		int segments = (int) Math.floor(openHours);

		PersonalTrainer trainer = new PersonalTrainer(trainerId, trainerName, price);

		for (int day = 0; day < 7; day++) {
			Calendar calInDay = (Calendar) cal.clone();
			calInDay.add(Calendar.DAY_OF_MONTH, day);
			for (int hour = 0; hour < segments; hour++) {
				trainer.addTimeSlot(new Timeslot((Calendar) calInDay.clone(), 60));
				calInDay.add(Calendar.HOUR_OF_DAY, 1);
			}
		}
		allTrainers.add(trainer);
		gym.getPersonalTrainers().add(trainer);
	}

	private void addReviewToDatabase(Review review) {

	}

	public void addMockGym() {
		allTrainers = new ArrayList<>();

		allGyms = new ArrayList<>();
		Gym gym0 = new Gym(
				0,
				"Minus Fitness Gym Chatswood",
				CalendarUtil.stringToCalendarNoDate("08:30"),
				CalendarUtil.stringToCalendarNoDate("17:30"),
				19.9,
				"763 Pacific Hwy, Chatswood NSW 2067",
				"123-4567",
				151.1792,
				-33.79911
		);
		addMockTrainersInThisWeek(gym0, 0, "Otto",40.0);
		addMockTrainersInThisWeek(gym0, 1, "Mary", 30.0);

		Gym gym1 = new Gym(
				1,
				"Minus Fitness Crows Nest",
				CalendarUtil.stringToCalendarNoDate("08:30"),
				CalendarUtil.stringToCalendarNoDate("17:30"),
				20,
				"400 Pacific Hwy, Crows Nest NSW 2065",
				"123-4567",
				151.19854,
				-33.82581
		);
		addMockTrainersInThisWeek(gym1, 2, "Jack", 35.0);

		Gym gym2 = new Gym(
				2,
				"Fitness Second St Leonards",
				CalendarUtil.stringToCalendarNoDate("09:00"),
				CalendarUtil.stringToCalendarNoDate("19:00"),
				20,
				"55 Christie St, St Leonards NSW 2065",
				"123-4567",
				151.19584,
				-33.82445
		);
		addMockTrainersInThisWeek(gym2, 3, "Tom", 32.0);
		addMockTrainersInThisWeek(gym2, 4, "Jerry", 36.0);

		Gym gym3 = new Gym(
				3,
				"Fitness Second North Sydney",
				CalendarUtil.stringToCalendarNoDate("09:00"),
				CalendarUtil.stringToCalendarNoDate("19:00"),
				20,
				"1 Elizabeth Plaza, North Sydney NSW 2060",
				"123-4567",
				-33.83945,
				151.20809
		);
		addMockTrainersInThisWeek(gym3, 5, "Aaron", 20.0);

		Gym gym4 = new Gym(
				4,
				"Fitness Second Bond St",
				CalendarUtil.stringToCalendarNoDate("09:00"),
				CalendarUtil.stringToCalendarNoDate("19:00"),
				20,
				"20 Bond St, Sydney NSW 2000",
				"123-4567",
				151.20829,
				-33.86441
		);
		addMockTrainersInThisWeek(gym4, 6, "Subaru", 30.0);
		addMockTrainersInThisWeek(gym4, 7, "Emilia", 40.0);
		addMockTrainersInThisWeek(gym4, 8, "Rem", 40.0);

		Gym gym5 = new Gym(
				5,
				"Minus Fitness Market Street",
				CalendarUtil.stringToCalendarNoDate("09:00"),
				CalendarUtil.stringToCalendarNoDate("19:00"),
				16,
				"25 Market St, Sydney NSW 2000",
				"123-4567",
				151.20522,
				-33.87115
		);
		addMockTrainersInThisWeek(gym5, 9, "Peter", 22.0);

		Gym gym6 = new Gym(
				6,
				"Minus Fitness Waterloo",
				CalendarUtil.stringToCalendarNoDate("09:00"),
				CalendarUtil.stringToCalendarNoDate("19:00"),
				15,
				"11A Lachlan St, Waterloo NSW 2017",
				"123-4567",
				151.21178,
				-33.90103
		);
		addMockTrainersInThisWeek(gym6, 10, "Larry", 28.0);

		Gym gym7 = new Gym(
				7,
				"Notime Fitness North Sydey",
				CalendarUtil.stringToCalendarNoDate("09:00"),
				CalendarUtil.stringToCalendarNoDate("19:00"),
				20,
				"118 Walker St, North Sydney NSW 2060",
				"123-4567",
				151.208801,
				-33.837711
		);
		addMockTrainersInThisWeek(gym7, 11, "Harry", 30.0);

		Gym gym8 = new Gym(
				8,
				"Notime Fitness City",
				CalendarUtil.stringToCalendarNoDate("09:00"),
				CalendarUtil.stringToCalendarNoDate("19:00"),
				20,
				"227 Elizabeth St, Sydney NSW 2000",
				"123-4567",
				151.2102227,
				-33.8706586
		);
		Gym gym9 = new Gym(
				9,
				"Sliver's Gym",
				CalendarUtil.stringToCalendarNoDate("09:00"),
				CalendarUtil.stringToCalendarNoDate("19:00"),
				20,
				"7-9 West St, North Sydney NSW 2060",
				"123-4567",
				151.2052855,
				-33.8334692
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

		for (Gym gym : allGyms) {
			addGymToDatabase(gym);
		}
		for (PersonalTrainer personalTrainer : allTrainers) {
			addTrainerToDatabase(personalTrainer);
		}
	}

	public void addMockReservations()
	{
		reservations = new ArrayList<Reservation>();
		// todo: user id
		Reservation rev1 = new Reservation(
				1,
				1,
				1,
				new Timeslot(CalendarUtil.stringToCalendar("2021-10-23 09:00"), 60)
		);
		Reservation rev2 = new Reservation(
				1,
				2,
				0,
				new Timeslot(CalendarUtil.stringToCalendar("2021-10-20 09:00"), 60)
		);
		Reservation rev3 = new Reservation(
				1,
				2,
				0,
				new Timeslot(CalendarUtil.stringToCalendar("2021-10-23 08:00"), 120)
		);

		reservations.add(rev1);
		reservations.add(rev2);
		reservations.add(rev3);
	}

	public List<SimpleGym> getAllSimpleGyms() {
		return allSimpleGyms;
	}

	public List<Gym> getAllGyms() {
		return allGyms;
	}

	public int getUserId() {
		// TODO:
		return 0;
	}

	public List<PersonalTrainer> getAllTrainers() {
		return allTrainers;
	}

	public void findGymById(int gymId, GymQueryCallback callback) {
		gymsRef.child(String.valueOf(gymId)).get().addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				Gym.GymData gymData = task.getResult().getValue(Gym.GymData.class);
				if (gymData == null) {
					callback.onFailed(new NullPointerException(
							"Query result of gym " + gymId + " is null"));
					return;
				}

				// Then query trainers of this gym
				populateTrainersOfGym(gymData, callback);
			} else {
				Log.e(TAG, Arrays.toString(task.getException().getStackTrace()));
				callback.onFailed(task.getException());
			}
		});
	}

	private void populateTrainersOfGym(Gym.GymData gymData, GymQueryCallback callback) {
		List<PersonalTrainer> trainers = new ArrayList<>();
		for (String tid : gymData.trainerIds) {
			findTrainerById(Integer.parseInt(tid), new TrainerQueryCallback() {
				@Override
				public void onSucceed(PersonalTrainer trainer) {
					trainers.add(trainer);
					if (trainers.size() == gymData.trainerIds.size()) {
						// Last trainer has been added, ready to open
						if (gymData.picturePath == null) {
							Gym gym = Gym.fromGymData(gymData, trainers, null);
							callback.onSucceed(gym);
						} else {
							// todo
						}
					}
				}

				@Override
				public void onFailed(Exception exception) {
					Log.e(TAG, Arrays.toString(exception.getStackTrace()));
				}
			});
		}
	}

	public Gym findGymById(int gymId) {
		// TODO: firebase
		for (Gym gym : getAllGyms()) {
			if (gym.getGymId() == gymId) {
				return gym;
			}
		}
		return null;
	}

	public void findTrainerById(int trainerId, TrainerQueryCallback callback) {
		trainersRef.child(String.valueOf(trainerId)).get().addOnSuccessListener(dataSnapshot -> {
			PersonalTrainer.TrainerData td =
					dataSnapshot.getValue(PersonalTrainer.TrainerData.class);
			if (td == null) {
				callback.onFailed(new NullPointerException(
						"Query result of trainer " + trainerId + " is null"));
				return;
			}
			if (td.avatarPath == null) {
				PersonalTrainer trainer = PersonalTrainer.fromData(td, null);
				callback.onSucceed(trainer);
			} else {
				// todo
			}
		}).addOnFailureListener(callback::onFailed);
	}

	public PersonalTrainer findTrainerById(int trainerId) {
		for (PersonalTrainer trainer : getAllTrainers()) {
			if (trainer.getTrainerId() == trainerId) {
				return trainer;
			}
		}
		return null;
	}

	/**
	 * ScheduleLists
	 */

	public ArrayList<ScheduleList> getScheduleLists()
	{
		return scheduleLists;
	}

	public void addScheduleList(ScheduleList scheduleList)
	{
		this.scheduleLists.add(scheduleList);
		sortScheduleLists();
		postValue(this);
	}

	public void sortScheduleLists(){
		Log.e(TAG,"---------------------"+this.getScheduleLists().size());
		this.scheduleLists.sort(new Comparator<ScheduleList>()
		{
			@Override
			public int compare(ScheduleList t1, ScheduleList t2)
			{
				Calendar c1 = t1.getTime();
				Calendar c2 = t2.getTime();
				if (c1.after(c2))
				{
					return 1;
				}
				if (c1.before(c2))
				{
					return -1;
				}
				return 0;
			}
		});
	}



	public void setScheduleLists(ArrayList<ScheduleList> scheduleLists)
	{
		this.scheduleLists = scheduleLists;
		sortScheduleLists();
		postValue(this);
	}

	public void removePurchaseRecord(int position)
	{
		this.purchaseRecords.remove(position);
		postValue(this);
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
	
	public void removeScheduleList(int position)
	{
		this.scheduleLists.remove(position);
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

	/**
	 * Return whether this user has been to the given gym at least once.
	 *
	 * @param gymId id of the given gym
	 * @return whether this user has been to the given gym at least once
	 */
	public boolean hasBeenToGym(int gymId) {
		List<Reservation> rsvList = getReservations();
		Calendar now = Calendar.getInstance();
		for (Reservation rsv : rsvList) {
			if (rsv.getGymId() == gymId && now.after(rsv.getSelectedTimeSlot().getBeginTime())) {
				// Only past reservation counts
				return true;
			}
		}
		return false;
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