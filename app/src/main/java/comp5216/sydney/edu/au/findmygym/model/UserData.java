package comp5216.sydney.edu.au.findmygym.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.se.omapi.Session;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.model.callbacks.GymQueryCallback;
import comp5216.sydney.edu.au.findmygym.model.callbacks.ReviewQueryCallback;
import comp5216.sydney.edu.au.findmygym.model.callbacks.TrainerQueryCallback;
import comp5216.sydney.edu.au.findmygym.ui.gym.GymViewModel;

public class UserData extends LiveData<UserData>
{
	private final String TAG = "[UserData]";
	
	public final String KEY_uid = "UID";
	public final String KEY_userName = "USERNAME";
	public final String KEY_userEmail = "EMAIL";
	public final String KEY_CARD_name = "CARD_NAME";
	public final String KEY_CARD_number = "CARD_NUMBER";
	public final String KEY_CARD_expiryDate = "CARD_DATE";
	public final String KEY_login_first_time = "LOGIN_FIRST_TIME";
	public final String KEY_login_last_time = "LOGIN_LAST_TIME";
	public final String KEY_login_counter = "LOGIN_COUNTER";
	
	private ArrayList<PurchaseRecord> purchaseRecords;
	private ArrayList<ScheduleList> scheduleLists;
	private ArrayList<CreditCard> creditCards;
	private ArrayList<Membership> memberships;
	private FirebaseUser firebaseUser;
	private String userId;
	private String userName;
	private String userMail;
	private Bitmap userAvatar;
	private List<String> favouriteGyms;

	// All reservations of this user, updated realtime with firebase
	private List<Reservation> reservations;
	private Session userSession;
	private StorageReference userStorageRef;
	private StorageReference gymPictureRef;
	private StorageReference trainerAvatarRef;
	private FirebaseDatabase database;
	private DatabaseReference dbRef;
	private DatabaseReference gymsRef;
	private DatabaseReference trainersRef;
	private DatabaseReference reviewsRef;
	private DatabaseReference userRef;
	private Context mContext;

	private boolean isSuccessful = false;
	private boolean downloadFinished = false;

	// This list will load when launching this app
	// Displays these gyms on map
	// TODO: 让这个list在loading界面load，load完了再进map
	// TODO: 或者观察这个list，随list更新map
	private List<Gym> allGyms;

	private List<PersonalTrainer> allTrainers;

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
		reviewsRef = dbRef.child("reviews");
		FirebaseStorage storage = FirebaseStorage.getInstance();
		gymPictureRef = storage.getReference("gymPictures");
		trainerAvatarRef = storage.getReference("trainerAvatars");

		loadAllGyms();
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

				}
			}
		}
		return UserData;
	}

	private void loadAllGyms() {
		allGyms = new ArrayList<>();
		allTrainers = new ArrayList<>();
		gymsRef.get().addOnSuccessListener(dataSnapshot -> {
			List<Gym.GymData> gymDataList = new ArrayList<>();
			for (DataSnapshot ds : dataSnapshot.getChildren()) {
				Gym.GymData gd = ds.getValue(Gym.GymData.class);
				if (gd != null) {
					gymDataList.add(gd);
				} else {
					Log.e(TAG, "null gym");
				}
			}

			for (Gym.GymData gd : gymDataList) {
				if (gd.trainerIds == null) {
					allGyms.add(Gym.fromGymData(gd, new ArrayList<>(),
							new ArrayList<>(), null));
				} else {
					// Then query trainers of this gym
					populateTrainersOfGym(gd, new GymQueryCallback() {
						@Override
						public void onSucceed(Gym gym) {
							allGyms.add(gym);
							Log.d(TAG, "Downloaded gym " + gym.getGymId());
							if (allGyms.size() == gymDataList.size()) {
								downloadFinished = true;
								Log.d(TAG, "All gyms downloaded!");
							}
						}

						@Override
						public void onFailed(Exception exception) {
							Log.e(TAG, Arrays.toString(exception.getStackTrace()));
						}
					});
				}
			}
		}).addOnFailureListener(e -> {
			Log.e(TAG, "Gym download error", e);
		});
	}

	/*
	Methods for mock database
	 */

	private void addGymToDatabase(Gym gym) {
		// Name of gym picture
		StorageReference pictureRef = gymPictureRef.child(gym.getGymId() + ".jpg");

		if (gym.getGymPhoto() != null) {
			// upload gym picture
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

	private void addReviewToDb(List<Review> reviews) {
		Map<String, Review.ReviewData> rds = new HashMap<>();
		for (Review review : reviews) {
			// todo: upload user avatar
			rds.put(review.getReviewId(), review.toData(null));
		}
		reviewsRef.setValue(rds).addOnSuccessListener(unused -> {
			Log.d(TAG, "Upload mock reviews succeed");
		}).addOnFailureListener(e -> {
			Log.e(TAG, "Upload mock reviews failed", e);
		});
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

	private void randomAddEquipment(Gym gym) {
		String[] equipments = new String[]{"Barbell", "Bicycle", "Climbing", "Dumbbell",
				"Rowing", "Swimming", "Treadmill"};
		int num = (int) (Math.random() * equipments.length);
		List<String> lst = Arrays.asList(equipments);
		Collections.shuffle(lst);
		for (int i = 0; i < num; i++) gym.getEquipments().add(lst.get(i));
	}

	private void addMockTrainersInThisWeek(List<PersonalTrainer> list, Gym gym, String trainerId,
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
		list.add(trainer);
		gym.getPersonalTrainers().add(trainer);
	}

	private void addMockUser() {
		Reservation rev1 = new Reservation(
				getUserId(),
				"Minus Fitness Gym Chatswood",
				null,
				20,
				new Timeslot(CalendarUtil.stringToCalendar("2021-10-28 10:00"), 60)
		);
		Reservation rev2 = new Reservation(
				getUserId(),
				"Fitness Second St Leonards",
				"Tom",
				52,
				new Timeslot(CalendarUtil.stringToCalendar("2021-10-22 09:00"), 60)
		);
		Reservation rev3 = new Reservation(
				getUserId(),
				"Fitness Second St Leonards",
				"Jerry",
				56,
				new Timeslot(CalendarUtil.stringToCalendar("2021-10-23 11:00"), 120)
		);

		UserDataContent udc = new UserDataContent();
		udc.reservations = new ArrayList<>();
		for (Reservation rsv : new Reservation[]{rev1, rev2, rev3}) {
			udc.reservations.add(rsv.toData());
		}

		Map<String, String> mem1 = new HashMap<>();
		mem1.put("gymID", "Minus Fitness Gym Chatswood");
		mem1.put("title", "Yearly plan");
		mem1.put("startTime", "2021-05-09 00:00");
		mem1.put("endTime", "2022-05-08 23:59");

		Map<String, String> mem2 = new HashMap<>();
		mem2.put("gymID", "Minus Fitness Crows Nest");
		mem2.put("title", "Yearly plan");
		mem2.put("startTime", "2021-08-16 00:00");
		mem2.put("endTime", "2022-08-15 23:59");

		udc.memberships = new ArrayList<>();
		udc.memberships.add(mem1);
		udc.memberships.add(mem2);

		udc.favouriteGyms = new ArrayList<>();
		udc.favouriteGyms.add("Minus Fitness Gym Chatswood");
		udc.favouriteGyms.add("Minus Fitness Crows Nest");
		udc.favouriteGyms.add("Fitness Second Bond St");

		userRef.setValue(udc).addOnSuccessListener(unused -> {
			addUserInfoChangeListener();
		}).addOnFailureListener(e -> {
			Log.e(TAG, "Failed to add mock user", e);
		});
	}

	public void addMockGym() {
		List<PersonalTrainer> allTrainers = new ArrayList<>();
		List<Gym> allGyms = new ArrayList<>();
		List<Review> allReviews = new ArrayList<>();

		Gym gym0 = new Gym(
				"Minus Fitness Gym Chatswood",
				"Minus Fitness Gym Chatswood",
				CalendarUtil.stringToCalendarNoDate("08:30"),
				CalendarUtil.stringToCalendarNoDate("17:30"),
				19.9,
				"763 Pacific Hwy, Chatswood NSW 2067",
				"123-4567",
				151.1792,
				-33.79911
		);
		addMockTrainersInThisWeek(allTrainers, gym0, "Otto", "Otto",40.0);
		addMockTrainersInThisWeek(allTrainers, gym0, "Mary", "Mary", 30.0);

		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DATE, -1);
		Review r0 = new Review("Steven", null, 2,
				"What a terrible place!", yesterday);
		gym0.getReviews().add(r0);

		Calendar someDaysAgo = Calendar.getInstance();
		yesterday.add(Calendar.DATE, -8);
		Review r1 = new Review("Elisabeth", null, 5,
				"Recommended! Various kind of equipments, enough space, " +
						"a swimming pool inside. Will come again and advise to " +
						"my friends.",
				someDaysAgo);
		gym0.getReviews().add(r1);
		allReviews.add(r0);
		allReviews.add(r1);

		Gym gym1 = new Gym(
				"Minus Fitness Crows Nest",
				"Minus Fitness Crows Nest",
				CalendarUtil.stringToCalendarNoDate("08:30"),
				CalendarUtil.stringToCalendarNoDate("17:30"),
				20,
				"400 Pacific Hwy, Crows Nest NSW 2065",
				"123-4567",
				151.19854,
				-33.82581
		);
		addMockTrainersInThisWeek(allTrainers, gym1, "Jack", "Jack", 35.0);

		Gym gym2 = new Gym(
				"Fitness Second St Leonards",
				"Fitness Second St Leonards",
				CalendarUtil.stringToCalendarNoDate("09:00"),
				CalendarUtil.stringToCalendarNoDate("19:00"),
				20,
				"55 Christie St, St Leonards NSW 2065",
				"123-4567",
				151.19584,
				-33.82445
		);
		addMockTrainersInThisWeek(allTrainers, gym2, "Tom", "Tom", 32.0);
		addMockTrainersInThisWeek(allTrainers, gym2, "Jerry", "Jerry", 36.0);

		Gym gym3 = new Gym(
				"Fitness Second North Sydney",
				"Fitness Second North Sydney",
				CalendarUtil.stringToCalendarNoDate("09:00"),
				CalendarUtil.stringToCalendarNoDate("19:00"),
				20,
				"1 Elizabeth Plaza, North Sydney NSW 2060",
				"123-4567",
				-33.83945,
				151.20809
		);
		addMockTrainersInThisWeek(allTrainers, gym3, "Aaron", "Aaron", 20.0);

		Gym gym4 = new Gym(
				"Fitness Second Bond St",
				"Fitness Second Bond St",
				CalendarUtil.stringToCalendarNoDate("09:00"),
				CalendarUtil.stringToCalendarNoDate("19:00"),
				20,
				"20 Bond St, Sydney NSW 2000",
				"123-4567",
				151.20829,
				-33.86441
		);
		addMockTrainersInThisWeek(allTrainers, gym4, "Subaru", "Subaru", 30.0);
		addMockTrainersInThisWeek(allTrainers, gym4, "Emiria", "Emiria", 40.0);
		addMockTrainersInThisWeek(allTrainers, gym4, "Rem", "Rem", 40.0);

		Gym gym5 = new Gym(
				"Minus Fitness Market Street",
				"Minus Fitness Market Street",
				CalendarUtil.stringToCalendarNoDate("09:00"),
				CalendarUtil.stringToCalendarNoDate("19:00"),
				16,
				"25 Market St, Sydney NSW 2000",
				"123-4567",
				151.20522,
				-33.87115
		);
		addMockTrainersInThisWeek(allTrainers, gym5, "Peter", "Peter", 22.0);

		Gym gym6 = new Gym(
				"Minus Fitness Waterloo",
				"Minus Fitness Waterloo",
				CalendarUtil.stringToCalendarNoDate("09:00"),
				CalendarUtil.stringToCalendarNoDate("19:00"),
				15,
				"11A Lachlan St, Waterloo NSW 2017",
				"123-4567",
				151.21178,
				-33.90103
		);
		addMockTrainersInThisWeek(allTrainers, gym6, "Larry", "Larry", 28.0);

		Gym gym7 = new Gym(
				"Notime Fitness North Sydey",
				"Notime Fitness North Sydey",
				CalendarUtil.stringToCalendarNoDate("09:00"),
				CalendarUtil.stringToCalendarNoDate("19:00"),
				20,
				"118 Walker St, North Sydney NSW 2060",
				"123-4567",
				151.208801,
				-33.837711
		);
		addMockTrainersInThisWeek(allTrainers, gym7, "Henry", "Henry", 30.0);

		Gym gym8 = new Gym(
				"Notime Fitness City",
				"Notime Fitness City",
				CalendarUtil.stringToCalendarNoDate("09:00"),
				CalendarUtil.stringToCalendarNoDate("19:00"),
				20,
				"227 Elizabeth St, Sydney NSW 2000",
				"123-4567",
				151.2102227,
				-33.8706586
		);
		addMockTrainersInThisWeek(allTrainers, gym8, "Jenny", "Jenny", 30.0);

		Gym gym9 = new Gym(
				"Sliver's Gym",
				"Sliver's Gym",
				CalendarUtil.stringToCalendarNoDate("09:00"),
				CalendarUtil.stringToCalendarNoDate("19:00"),
				20,
				"7-9 West St, North Sydney NSW 2060",
				"123-4567",
				151.2052855,
				-33.8334692
		);
		addMockTrainersInThisWeek(allTrainers, gym9, "Nofe", "Nofe", 18.0);

		randomAddEquipment(gym0);
		randomAddEquipment(gym1);
		randomAddEquipment(gym2);
		randomAddEquipment(gym3);
		randomAddEquipment(gym4);
		randomAddEquipment(gym5);
		randomAddEquipment(gym6);
		randomAddEquipment(gym7);
		randomAddEquipment(gym8);
		randomAddEquipment(gym9);
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
		addReviewToDb(allReviews);
	}

//	private void signupUser

//	public void addMockReservations()
//	{
//		// todo: user id
//		Reservation rev1 = new Reservation(
//				getUserId(),
//				"Minus Fitness Gym Chatswood",
//				"Otto",
//				new Timeslot(CalendarUtil.stringToCalendar("2021-10-23 09:00"), 60)
//		);
//		Reservation rev2 = new Reservation(
//				getUserId(),
//				"Fitness Second St Leonards",
//				"Tom",
//				new Timeslot(CalendarUtil.stringToCalendar("2021-10-20 09:00"), 60)
//		);
//		Reservation rev3 = new Reservation(
//				getUserId(),
//				"Fitness Second St Leonards",
//				null,
//				new Timeslot(CalendarUtil.stringToCalendar("2021-10-23 08:00"), 120)
//		);
//
//		reservations.add(rev1);
//		reservations.add(rev2);
//		reservations.add(rev3);
//	}

	public List<Gym> getAllGyms() {
		return allGyms;
	}

	@Deprecated
	public List<PersonalTrainer> getAllTrainers() {
		return allTrainers;
	}

	public void findGymById(String gymId, GymQueryCallback callback) {
		gymsRef.child(gymId).get().addOnCompleteListener(task -> {
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
		List<Review> reviews = new ArrayList<>();
		for (String tid : gymData.trainerIds) {
			findTrainerById(tid, new TrainerQueryCallback() {
				@Override
				public void onSucceed(PersonalTrainer trainer) {
					trainers.add(trainer);
					allTrainers.add(trainer);
					if (trainers.size() == gymData.trainerIds.size()) {
						// Last trainer has been added, ready to open
						if (gymData.reviewIds != null) {
							for (String rid : gymData.reviewIds) {
								// todo
							}
						}
						if (gymData.picturePath == null) {
							Gym gym = Gym.fromGymData(gymData, trainers, reviews, null);
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

	public Gym findGymById(String gymId) {
		Log.d(TAG, "Looking for id " + gymId);
		Log.d(TAG, allGyms.toString());
		for (Gym gym : allGyms) {
			Log.d(TAG, "Scanning " + gym.getGymId());
			if (gymId.equals(gym.getGymId())) {
				return gym;
			}
		}
		return null;
	}

	public void findTrainerById(String trainerId, TrainerQueryCallback callback) {
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

	public void postNewReservation(Reservation reservation) {
		userRef.child(getUserId()).child(reservation.getRsvId()).setValue(reservation.toData())
				.addOnSuccessListener(unused -> {
					Log.d(TAG, "Uploaded new reservation");
					if (reservation.getTrainerId() != null) {
						Log.d(TAG, "Updating trainer timeslots");
//						trainersRef.child(reservation.getTrainerId()).child("availableTime")
//								.equalTo()
					}
				}).addOnFailureListener(e -> {
					Log.e(TAG, "Failed to upload new reservation", e);
		});
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

	public void addPurchaseRecordToDatabase(PurchaseRecord purchaseRecord) {
		Map<String, String> recordItem = new HashMap<>();
		recordItem.put("price", String.valueOf(purchaseRecord.getCost()));
		recordItem.put("gymId", purchaseRecord.getTitle());
		recordItem.put("time", purchaseRecord.getTimeStr());
		userRef.child("purchaseRecord").child(String.valueOf(purchaseRecords.size() - 1))
				.setValue(recordItem).addOnSuccessListener(unused -> {

		}).addOnFailureListener(e -> {
			Log.e(TAG, "Failed to upload purchaseRecord");
		});
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

		userSignIn();

//		userRsvRef = userRef.child("reservations");

		postValue(this);
	}

	private void userSignIn() {
		userRef = dbRef.child("users").child(getUserId());
		// Check if the user's info is in firebase
		userRef.get().addOnCompleteListener(task -> {
			DataSnapshot snapshot = task.getResult();
			UserDataContent udc = snapshot.getValue(UserDataContent.class);
			if (udc == null) {
				// First time login
				addMockUser();
			} else {
				addUserInfoChangeListener();
			}
		});
	}

	private void addUserInfoChangeListener() {
		userRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				UserDataContent udc = snapshot.getValue(UserDataContent.class);
				if (udc == null) {
					Log.d(TAG, "User is null");
					return;
				}
				reservations = new ArrayList<>();
				if (udc.reservations != null) {
					for (Reservation.ReservationData rd : udc.reservations) {
						reservations.add(Reservation.fromData(rd));
					}
				}
				memberships = new ArrayList<>();
				if (udc.memberships != null) {
					for (Map<String, String> map : udc.memberships) {
						Membership mem = new Membership(map.get("gymID"),
								map.get("title"),
								null,
								CalendarUtil.stringToCalendar(map.get("startTime")),
								CalendarUtil.stringToCalendar(map.get("endTime")));
						memberships.add(mem);
					}
				}
				favouriteGyms = new ArrayList<>();
				if (udc.favouriteGyms != null) {
					favouriteGyms.addAll(udc.favouriteGyms);
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {

			}
		});
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
		Log.w(TAG, "Not logged in!");
		return "GUEST: JOHN DOE";
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
		postValue(this);
	}

	public String getUserId() {
		if (userId != null) {
			return userId;
		} else if (firebaseUser != null) {
			userId = firebaseUser.getUid();
			return userId;
		}
		Log.w(TAG, "Not logged in!");
		return "";
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
		Log.w(TAG, "Not logged in!");
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

	public List<String> getFavouriteGyms() {
		if (favouriteGyms != null) {

		} else {
			favouriteGyms = new ArrayList<>();
//			favouriteGyms.add();
		}
		return this.favouriteGyms;
	}

	public void addToFavouriteGyms(String gymId) {
		favouriteGyms.add(gymId);
		// todo: 其他操作
	}

	public void removeFromFavouriteGyms(String gymId) {
		favouriteGyms.remove(gymId);
		// todo: 其他操作
	}

	public void setFavouriteGyms(List<String> favouriteGyms) {
		this.favouriteGyms = favouriteGyms;
		postValue(this);
	}

	public void setContext(Context mContext)
	{
		this.mContext = mContext;
	}

	public List<Reservation> getReservations() {
		if (reservations == null) {
			reservations = new ArrayList<>();
		}
		return reservations;
	}

	/**
	 * Return whether this user has been to the given gym at least once.
	 *
	 * @param gymId id of the given gym
	 * @return whether this user has been to the given gym at least once
	 */
	public boolean hasBeenToGym(String gymId) {
		List<Reservation> rsvList = getReservations();
		Calendar now = Calendar.getInstance();
		for (Reservation rsv : rsvList) {
			if (rsv.getGymId().equals(gymId) &&
					now.after(rsv.getSelectedTimeSlot().getBeginTime())) {
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

	public void setSuccessful(boolean successful) {
		isSuccessful = successful;
	}

	public boolean isSuccessful() {
		return isSuccessful && downloadFinished;
	}
}