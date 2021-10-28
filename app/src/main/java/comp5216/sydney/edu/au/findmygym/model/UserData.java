package comp5216.sydney.edu.au.findmygym.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.se.omapi.Session;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.Utils.ImageUtil;
import comp5216.sydney.edu.au.findmygym.model.callbacks.GymQueryCallback;
import comp5216.sydney.edu.au.findmygym.model.callbacks.ListQueryCallback;
import comp5216.sydney.edu.au.findmygym.model.callbacks.ObjectQueryCallback;
import comp5216.sydney.edu.au.findmygym.model.callbacks.TrainerQueryCallback;
import comp5216.sydney.edu.au.findmygym.ui.gym.GymViewModel;

public class UserData extends LiveData<UserData>
{
	private final String TAG = "[UserData]";
	
	
	public final String KEY_GYMS = "GYMS";
	public final String KEY_CARDS = "CARDS";
	public final String KEY_USERS = "USERS";
	public final String KEY_TRAINERS = "TRAINERS";
	public final String KEY_RESERVATION = "RESERVATION";
	public final String KEY_MEMBERSHIP = "MEMBERSHIPS";
	public final String KEY_REVIEWS = "REVIEWS";
	public final String KEY_TRANSACTIONS = "TRANSACTIONS";
	
	public final String KEY_uid = "UID";
	public final String KEY_userName = "USERNAME";
	public final String KEY_userEmail = "EMAIL";
	public final String KEY_CARD_name = "CARD_NAME";
	public final String KEY_CARD_number = "CARD_NUMBER";
	public final String KEY_CARD_expiryDate = "CARD_DATE";
	public final String KEY_login_first_time = "LOGIN_FIRST_TIME";
	public final String KEY_login_last_time = "LOGIN_LAST_TIME";
	public final String KEY_login_counter = "LOGIN_COUNTER";
	
	public final String KEY_GYM_name = "GYM_NAME";
	public final String KEY_GYM_address = "GYM_ADDRESS";
	public final String KEY_GYM_latitude = "GYM_LATITUDE";
	public final String KEY_GYM_longitude = "GYM_LONGITUDE";
	public final String KEY_GYM_contact = "GYM_CONTACT";
	public final String KEY_GYM_closeTime = "GYM_CLOSETIME";
	public final String KEY_GYM_openTime = "GYM_OPENTIME";
	public final String KEY_GYM_price = "GYM_PRICE";
	public final String KEY_GYM_equipments = "GYM_EQUIPMENTS";
	public final String KEY_GYM_trainers = "GYM_TRAINERS";
	public final String KEY_GYM_reviews = "GYM_REVIEWS";
	
	public final String KEY_RES_ID_user = "KEY_RES_ID_USER";
	public final String KEY_RES_ID_gym = "KEY_RES_ID_GYM";
	public final String KEY_RES_ID_trainer = "KEY_RES_ID_TRAINER";
	public final String KEY_RES_price = "KEY_RES_PRICE";
	public final String KEY_RES_timeSlot = "KEY_RES_TIMESLOT";
	
	public final String KEY_TRAINER_name = "TRAINER_NAME";
	public final String KEY_TRAINER_price = "TRAINER_PRICE";
	public final String KEY_TRAINER_times = "TRAINER_TIMES";
	public final String KEY_TRAINER_gymId = "TRAINER_GYM_ID";
	
	public final String KEY_REVIEW_userId = "REVIEW_USER_ID";
	public final String KEY_REVIEW_gymId = "REVIEW_GYM_ID";
	public final String KEY_REVIEW_rating = "REVIEW_RATING";
	public final String KEY_REVIEW_comments = "REVIEW_COMMENTS";
	public final String KEY_REVIEW_dateTime = "REVIEW_DATE_TIME";
	
	public final String KEY_MEMBERSHIP_ID_user = "MEMBERSHIP_ID_USER";
	public final String KEY_MEMBERSHIP_ID_gym = "MEMBERSHIP_ID_GYM";
	public final String KEY_MEMBERSHIP_title = "MEMBERSHIP_ID_TITLE";
	public final String KEY_MEMBERSHIP_startTime = "MEMBERSHIP_TIME_START";
	public final String KEY_MEMBERSHIP_endTime = "MEMBERSHIP_TIME_END";
	
	public final String KEY_TRANSACTIONS_ID_user = "TRANSACTIONS_ID_USER";
	public final String KEY_TRANSACTIONS_ID_gym = "TRANSACTIONS_ID_GYM";
	public final String KEY_TRANSACTIONS_cost = "TRANSACTIONS_COST";
	public final String KEY_TRANSACTIONS_time = "TRANSACTIONS_TIME";
	
	public final String URL_STORAGE_ORIGINAL_IMAGE = "gs://findmygym-e9f2e.appspot.com/Original/";
	public final String URL_STORAGE_REDUCED_IMAGE = "gs://findmygym-e9f2e.appspot.com/Reduced/";
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
	private List<SimpleGym> allSimpleGyms = new ArrayList<>();
	
	private List<PersonalTrainer> allTrainers;
	
	private volatile static UserData UserData;
	
	/**
	 * Default Constructor
	 */
	private UserData()
	{
		this.memberships = new ArrayList<>();
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
		
		loadAllSimpleGyms();
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
	
	public void post()
	{
		postValue(this);
	}
	

	
	
	private void addMockGyms()
	{
		Map<String, Object> newGyms = new HashMap<>();
		newGyms.put(this.KEY_GYM_name, "Fitness Second Bond St");
		newGyms.put(this.KEY_GYM_address, "20 Bond St, Sydney NSW 2000");
		newGyms.put(this.KEY_GYM_latitude, -33.86441);
		newGyms.put(this.KEY_GYM_longitude, 151.20829);
		newGyms.put(this.KEY_GYM_contact, "123-4567");
		newGyms.put(this.KEY_GYM_closeTime, "1970-01-01 19:00");
		newGyms.put(this.KEY_GYM_openTime, "1970-01-01 09:00");
		newGyms.put(this.KEY_GYM_price, 20);
		newGyms.put(this.KEY_GYM_equipments, Arrays.asList("Climbing", "Barbell", "Bicycle", "Rowing", "Treadmill"));
		newGyms.put(this.KEY_GYM_trainers, Arrays.asList("111", "222", "333", "444", "555"));
		
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		CollectionReference cardsRef = db.collection("GYMS");
		cardsRef.add(newGyms)
				// cardsRef.add(new CreditCard())
				.addOnSuccessListener(new OnSuccessListener<DocumentReference>()
				{
					@Override
					public void onSuccess(DocumentReference documentReference)
					{
						// importCardsFromDB();
						Log.d(TAG, "Add mocked gyms successfully: " + documentReference.getId());
					}
				})
				.addOnFailureListener(new OnFailureListener()
				{
					@Override
					public void onFailure(@NonNull Exception e)
					{
						Log.d(TAG, "Add mocked gyms Failed: " + e.toString());
						e.printStackTrace();
					}
				});
	}
	
	public void addPurchaseRecordToList(PurchaseRecord purchaseRecord)
	{
		this.purchaseRecords.add(purchaseRecord);
		sortPurchaseRecords();
		postValue(this);
	}
	
	public void addPurchaseRecord(PurchaseRecord purchaseRecord)
	{
		Map<String, Object> map = new HashMap<>();
		map.put(this.KEY_TRANSACTIONS_ID_user, purchaseRecord.getUserID());
		map.put(this.KEY_TRANSACTIONS_ID_gym, purchaseRecord.getGymId());
		map.put(this.KEY_TRANSACTIONS_cost, purchaseRecord.getCost());
		map.put(this.KEY_TRANSACTIONS_time, comp5216.sydney.edu.au.findmygym.Utils.CalendarUtil.calendarToString(purchaseRecord.getTime()));
		
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		CollectionReference reference = db.collection(KEY_TRANSACTIONS);
		reference.add(map)
				.addOnSuccessListener(new OnSuccessListener<DocumentReference>()
				{
					@Override
					public void onSuccess(DocumentReference documentReference)
					{
						Log.d(TAG, "Add PurchaseRecord Successfully: " + documentReference.getId());
						Log.d(TAG, "onSuccess: Before add"+comp5216.sydney.edu.au.findmygym.model.UserData.getInstance().getPurchaseRecords().size());
						purchaseRecord.setID(documentReference.getId());
						comp5216.sydney.edu.au.findmygym.model.UserData.getInstance().addPurchaseRecordToList(purchaseRecord);
						Log.d(TAG, "onSuccess: After add"+comp5216.sydney.edu.au.findmygym.model.UserData.getInstance().getPurchaseRecords().size());
					}
				})
				.addOnFailureListener(new OnFailureListener()
				{
					@Override
					public void onFailure(@NonNull Exception e)
					{
						Log.d(TAG, "Add PurchaseRecord Failed: " + e.toString());
						e.printStackTrace();
					}
				});
	}
	
	public void getPurchaseRecordsByUID(String UID, ListQueryCallback callback)
	{
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		CollectionReference ref = db.collection(getInstance().KEY_TRANSACTIONS);
		ref.whereEqualTo(KEY_TRANSACTIONS_ID_user, getUserId()).get()
				.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
				{
					@Override
					public void onComplete(@NonNull Task<QuerySnapshot> task)
					{
						if (task.isSuccessful())
						{
							try
							{
								ArrayList<PurchaseRecord> purchaseRecords = new ArrayList<>();
								Log.d(TAG, "Found " + task.getResult().getDocuments().size() + " Memberships in DB");
								for (QueryDocumentSnapshot document : task.getResult())
								{
									purchaseRecords.add(new PurchaseRecord(document.getId(),
											Math.toIntExact((long)(document.get(KEY_TRANSACTIONS_cost))),
											(String) document.get(KEY_TRANSACTIONS_ID_user),
											(String) document.get(KEY_TRANSACTIONS_ID_gym),
											comp5216.sydney.edu.au.findmygym.Utils.CalendarUtil.stringToCalendar((String) document.get(KEY_TRANSACTIONS_time))
									));
								}
								Log.d(TAG, "getMembershipsByUID successfully!" + UID);
								comp5216.sydney.edu.au.findmygym.model.UserData.getInstance().setPurchaseRecords(purchaseRecords);
								callback.onSucceed(purchaseRecords);
							} catch (Exception e)
							{
								callback.onFailed(e);
							}
						}
						else
						{
							Log.d(TAG, "getMembershipsByUID failed!" + UID);
						}
					}
				});
	}
	
	
	public void addMembership(Membership membership)
	{
		Map<String, Object> map = new HashMap<>();
		map.put(this.KEY_MEMBERSHIP_ID_user, membership.getUserID());
		map.put(this.KEY_MEMBERSHIP_ID_gym, membership.getGymID());
		map.put(this.KEY_MEMBERSHIP_title, membership.getTitle());
		map.put(this.KEY_MEMBERSHIP_startTime, comp5216.sydney.edu.au.findmygym.Utils.CalendarUtil.calendarToString(membership.getStartTime()));
		map.put(this.KEY_MEMBERSHIP_endTime, comp5216.sydney.edu.au.findmygym.Utils.CalendarUtil.calendarToString(membership.getEndTime()));
		
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		CollectionReference reference = db.collection(KEY_MEMBERSHIP);
		reference.add(map)
				.addOnSuccessListener(new OnSuccessListener<DocumentReference>()
				{
					@Override
					public void onSuccess(DocumentReference documentReference)
					{
						Log.d(TAG, "Add Membership Successfully: " + documentReference.getId());
						comp5216.sydney.edu.au.findmygym.model.UserData.getInstance().memberships.add(membership);
						comp5216.sydney.edu.au.findmygym.model.UserData.getInstance().post();
					}
				})
				.addOnFailureListener(new OnFailureListener()
				{
					@Override
					public void onFailure(@NonNull Exception e)
					{
						Log.d(TAG, "Add Reservation Failed: " + e.toString());
						e.printStackTrace();
					}
				});
	}
	
	
	public void getMembershipsByUID(String UID, ListQueryCallback callback)
	{
		String mTag = "[getMembershipsByUID]";
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		CollectionReference ref = db.collection(getInstance().KEY_MEMBERSHIP);
		ref.whereEqualTo(KEY_MEMBERSHIP_ID_user, getUserId()).get()
				.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
				{
					@Override
					public void onComplete(@NonNull Task<QuerySnapshot> task)
					{
						if (task.isSuccessful())
						{
							try
							{
								ArrayList<Membership> memberships = new ArrayList<>();
								Log.d(TAG, "Found " + task.getResult().getDocuments().size() + " Memberships in DB");
								for (QueryDocumentSnapshot document : task.getResult())
								{
									memberships.add(new Membership((String) document.get(KEY_MEMBERSHIP_ID_user),
											(String) document.get(KEY_MEMBERSHIP_ID_gym),
											(String) document.get(KEY_MEMBERSHIP_title),
											comp5216.sydney.edu.au.findmygym.Utils.CalendarUtil.stringToCalendar((String) document.get(KEY_MEMBERSHIP_startTime)),
											comp5216.sydney.edu.au.findmygym.Utils.CalendarUtil.stringToCalendar((String) document.get(KEY_MEMBERSHIP_endTime))
									));
								}
								Log.d(TAG, "getMembershipsByUID successfully!" + UID);
								comp5216.sydney.edu.au.findmygym.model.UserData.getInstance().memberships = memberships;
								comp5216.sydney.edu.au.findmygym.model.UserData.getInstance().post();
								callback.onSucceed(memberships);
							} catch (Exception e)
							{
								callback.onFailed(e);
							}
						}
						else
						{
							Log.d(TAG, "getMembershipsByUID failed!" + UID);
						}
					}
				});
	}

	public void removeTrainerTimeslot(String trainerId, Timeslot timeslot) {

		FirebaseFirestore db = FirebaseFirestore.getInstance();
		CollectionReference reference = db.collection(KEY_TRAINERS);
		DocumentReference dr = reference.document(trainerId);
		String strToRemove = timeslot.toDatabaseString();
		dr.get().addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				List<String> list = (List<String>) task.getResult().get(KEY_TRAINER_times);
				if (list != null && list.remove(strToRemove)) {
					dr.update(KEY_TRAINER_times, list).addOnCompleteListener(task1 -> {
						Log.d(TAG, "Update trainer timeslot: " + task1.isSuccessful());
					});
				} else {
					Log.e(TAG, "Update trainer timeslot failed: no such timeslot");
				}
			} else {
				Log.e(TAG, "Update trainer timeslot failed", task.getException());
			}
		});

	}

	public void addReservation(Reservation reservation) {
		addReservation(reservation, null);
	}
	
	public void addReservation(Reservation reservation, ObjectQueryCallback callback)
	{
		Map<String, Object> map = new HashMap<>();
		map.put(this.KEY_RES_ID_user, reservation.getUserId());
		map.put(this.KEY_RES_ID_gym, reservation.getGymId());
		map.put(this.KEY_RES_ID_trainer, reservation.getTrainerId());
		map.put(this.KEY_RES_price, reservation.getPrice());
		map.put(this.KEY_RES_timeSlot, reservation.getSelectedTimeSlot().toDatabaseString());
		
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		CollectionReference reference = db.collection(KEY_RESERVATION);
		reference.add(map)
				// cardsRef.add(new CreditCard())
				.addOnSuccessListener(new OnSuccessListener<DocumentReference>()
				{
					@Override
					public void onSuccess(DocumentReference documentReference)
					{
						// importCardsFromDB();
						Log.d(TAG, "Add Reservation Successfully: " + documentReference.getId());
						reservation.setRsvId(documentReference.getId());

						if (callback != null) {
							callback.onSucceed(null);
						}
						comp5216.sydney.edu.au.findmygym.model.UserData.getInstance().reservations.add(reservation);
						comp5216.sydney.edu.au.findmygym.model.UserData.getInstance().post();
					}
				})
				.addOnFailureListener(new OnFailureListener()
				{
					@Override
					public void onFailure(@NonNull Exception e)
					{
						Log.d(TAG, "Add Reservation Failed: " + e.toString());
						e.printStackTrace();
						if (callback != null) {
							callback.onFailed(null);
						}
					}
				});
	}
	
	
	public void getReservationByID(String ID, ObjectQueryCallback callback)
	{
		String mTag = "[getTrainerByID]";
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		DocumentReference gymRef = db.collection(getInstance().KEY_RESERVATION).document(ID);
		final Reservation[] reservations = {null};
		gymRef.get()
				.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
				{
					@Override
					public void onComplete(@NonNull Task<DocumentSnapshot> task)
					{
						if (task.isSuccessful())
						{
							try
							{
								Log.d(mTag, "getReservationByID DonComplete: ");
								DocumentSnapshot result = task.getResult();
								reservations[0] = new Reservation(result.getId(), (String) result.get(KEY_RES_ID_user), (String) result.get(KEY_RES_ID_gym), (String) result.get(KEY_RES_ID_trainer),
										Math.toIntExact((long) result.get(KEY_RES_price)), Timeslot.fromDatabaseString((String) result.get(KEY_RES_timeSlot)));
								Log.d(TAG, "getTrainerByID successfully!" + ID);
								callback.onSucceed(reservations[0]);
							} catch (Exception e)
							{
								callback.onFailed(e);
							}
						}
						else
						{
							Log.d(TAG, "getTrainerByID failed!" + ID);
						}
					}
				});
	}
	
	
	public void getReservationsByUID(String UID, ListQueryCallback callback)
	{
		String mTag = "[getTrainerByID]";
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		CollectionReference ref = db.collection(getInstance().KEY_RESERVATION);
		final Reservation[] reservations = {null};
		ref.whereEqualTo(KEY_RES_ID_user, getUserId()).get()
				.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
				{
					@Override
					public void onComplete(@NonNull Task<QuerySnapshot> task)
					{
						if (task.isSuccessful())
						{
							try
							{
								ArrayList<Reservation> reservationList = new ArrayList<>();
								Log.d(TAG, "Found " + task.getResult().getDocuments().size() + " Reservations in DB");
								for (QueryDocumentSnapshot document : task.getResult())
								{
									reservations[0] = new Reservation(document.getId(), (String) document.get(KEY_RES_ID_user), (String) document.get(KEY_RES_ID_gym), (String) document.get(KEY_RES_ID_trainer),
											Math.toIntExact((long) document.get(KEY_RES_price)), Timeslot.fromDatabaseString((String) document.get(KEY_RES_timeSlot)));
									reservationList.add(reservations[0]);
								}
								Log.d(TAG, "getReservationByUID successfully!" + UID);
								comp5216.sydney.edu.au.findmygym.model.UserData.getInstance().reservations = reservationList;
								comp5216.sydney.edu.au.findmygym.model.UserData.getInstance().post();
								callback.onSucceed(reservationList);
							} catch (Exception e)
							{
								callback.onFailed(e);
							}
						}
						else
						{
							Log.d(TAG, "getTrainerByID failed!" + UID);
						}
					}
				});
	}

	private void loadAllSimpleGyms() {
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		db.collection(KEY_GYMS).get().addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				System.out.println(task.getResult());
				for (QueryDocumentSnapshot snapshot : task.getResult()) {
					SimpleGym simpleGym = new SimpleGym(
							snapshot.getId(),
							snapshot.get(KEY_GYM_name, String.class),
							snapshot.get(KEY_GYM_address, String.class),
							snapshot.get(KEY_GYM_longitude, Double.class),
							snapshot.get(KEY_GYM_latitude, Double.class)
					);
					Log.d(TAG, "Downloaded simple gym " + snapshot.getId());
					allSimpleGyms.add(simpleGym);
				}
				postValue(this);
			} else {
				Log.e(TAG, "Failed to read all gyms info from database");
			}
		});
	}
	
	public void getGymByID(String ID, ObjectQueryCallback callback)
	{
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		DocumentReference gymRef = db.collection(KEY_GYMS).document(ID);
		gymRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
		{
			@Override
			public void onComplete(@NonNull Task<DocumentSnapshot> task)
			{
				if (task.isSuccessful())
				{
					String name = (String) task.getResult().get(KEY_GYM_name);
					String contact = (String) task.getResult().get(KEY_GYM_contact);
					String address = (String) task.getResult().get(KEY_GYM_address);
					int price = Math.toIntExact((Long) task.getResult().get(KEY_GYM_price));
					String openTime = (String) task.getResult().get(KEY_GYM_openTime);
					String closeTime = (String) task.getResult().get(KEY_GYM_closeTime);
					ArrayList<String> equipments = (ArrayList) task.getResult().get(KEY_GYM_equipments);
//					ArrayList<String> reviews = (ArrayList) task.getResult().get(KEY_GYM_reviews);
					//					ArrayList<String> trainers = (ArrayList) task.getResult().get(KEY_GYM_trainers);
					Double latitude = (Double) task.getResult().get(KEY_GYM_latitude);
					Double longitude = (Double) task.getResult().get(KEY_GYM_longitude);
					
					Log.d(TAG, ID + " name: " + name);
					Log.d(TAG, ID + " contact: " + contact);
					Log.d(TAG, ID + " latitude: " + latitude);
					Log.d(TAG, ID + " longitude: " + longitude);
					Log.d(TAG, ID + " equipments: " + equipments);
//					Log.d(TAG, ID + " reviews: " + reviews);
					//					Log.d(TAG, ID + " trainers: " + trainers);
					
					getTrainersByGymId(ID, new ListQueryCallback()
					{
						@Override
						public void onSucceed(ArrayList list)
						{
							Log.d(TAG, " successfully get trainers of gym " + ID);
							final List<PersonalTrainer> trainerList = list;
							getReviewsByGymId(ID, new ListQueryCallback()
							{
								@Override
								public void onSucceed(ArrayList list)
								{
									Log.d(TAG, " successfully get reviews of gym " + ID);
									Gym gym = new Gym(
											ID,
											name,
											trainerList,
											comp5216.sydney.edu.au.findmygym.Utils.CalendarUtil.stringToCalendar(openTime),
											comp5216.sydney.edu.au.findmygym.Utils.CalendarUtil.stringToCalendar(closeTime),
											price,
											address,
											contact,
											longitude,
											latitude,
											equipments,
											list
									);
									callback.onSucceed(gym);
								}
								
								@Override
								public void onFailed(Exception e)
								{
									Log.d(TAG, " failed to get reviews of gym " + ID, e);
								}
							});
						}
						
						@Override
						public void onFailed(Exception e)
						{
							Log.d(TAG, " failed to get trainers of gym " + ID, e);
						}
					});
				}
			}
		});
	}
	
	
	public void getTrainerByID(String ID, ObjectQueryCallback callback)
	{
		
		String mTag = "[getTrainerByID]";
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		DocumentReference gymRef = db.collection(comp5216.sydney.edu.au.findmygym.model.UserData.getInstance().KEY_TRAINERS).document(ID);
		final PersonalTrainer[] personalTrainer = {null};
		gymRef.get()
				.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
				{
					@Override
					public void onComplete(@NonNull Task<DocumentSnapshot> task)
					{
						Log.d(mTag, "getTrainerByIDonComplete: ");
						if (task.isSuccessful())
						{
							try
							{
								Log.d(mTag, "getTrainerByIDonComplete: ");
								String name = (String) task.getResult().get(KEY_TRAINER_name);
								int price = Math.toIntExact((Long) task.getResult().get(KEY_TRAINER_price));
								ArrayList<String> times = (ArrayList) task.getResult().get(KEY_TRAINER_times);
								ArrayList<Timeslot> timeslots = new ArrayList<>();
								for (String time : times)
								{
									timeslots.add(Timeslot.fromDatabaseString(time));
								}
								Log.d(TAG, "name: " + name);
								Log.d(TAG, "price: " + price);
								Log.d(TAG, "Timeslot: " + timeslots);
								personalTrainer[0] = new PersonalTrainer(ID, name, price, timeslots);
								Log.d(TAG, "getTrainerByID successfully!" + ID);
								callback.onSucceed(personalTrainer[0]);
							} catch (Exception e)
							{
								callback.onFailed(e);
							}
						}
						else
						{
							Log.d(TAG, "getTrainerByID failed!" + ID);
						}
					}
				});
	}
	
	
	private void loadAllGyms()
	{
		// allGyms = new ArrayList<>();
		// allTrainers = new ArrayList<>();
		// gymsRef.get().addOnSuccessListener(dataSnapshot -> {
		// 	List<Gym.GymData> gymDataList = new ArrayList<>();
		// 	for (DataSnapshot ds : dataSnapshot.getChildren()) {
		// 		Gym.GymData gd = ds.getValue(Gym.GymData.class);
		// 		if (gd != null) {
		// 			gymDataList.add(gd);
		// 		} else {
		// 			Log.e(TAG, "null gym");
		// 		}
		// 	}
		//
		// 	for (Gym.GymData gd : gymDataList) {
		// 		if (gd.trainerIds == null) {
		// 			allGyms.add(Gym.fromGymData(gd, new List<>(),
		// 					new List<>(), null));
		// 		} else {
		// 			// Then query trainers of this gym
		// 			populateTrainersOfGym(gd, new GymQueryCallback() {
		// 				@Override
		// 				public void onSucceed(Gym gym) {
		// 					allGyms.add(gym);
		// 					Log.d(TAG, "Downloaded gym " + gym.getGymId());
		// 					if (allGyms.size() == gymDataList.size()) {
		// 						downloadFinished = true;
		// 						Log.d(TAG, "All gyms downloaded!");
		// 					}
		// 				}
		//
		// 				@Override
		// 				public void onFailed(Exception exception) {
		// 					Log.e(TAG, Arrays.toString(exception.getStackTrace()));
		// 				}
		// 			});
		// 		}
		// 	}
		// }).addOnFailureListener(e -> {
		// 	Log.e(TAG, "Gym download error", e);
		// });
	}

	/*
	Methods for mock database
	 */
	
	private void addGymToDatabase(Gym gym)
	{
		// // Name of gym picture
		// StorageReference pictureRef = gymPictureRef.child(gym.getGymId() + ".jpg");
		//
		// if (gym.getGymPhoto() != null) {
		// 	// upload gym picture
		// 	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		// 	gym.getGymPhoto().compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
		// 	byte[] data = outputStream.toByteArray();
		// 	UploadTask task = pictureRef.putBytes(data);
		// 	task.addOnFailureListener(e -> {
		// 		Log.e(TAG, Arrays.toString(e.getStackTrace()));
		// 	}).addOnSuccessListener(taskSnapshot -> {
		// 		gymsRef.child(String.valueOf(gym.getGymId()))
		// 				.setValue(gym.toData(pictureRef.getDownloadUrl().getResult().toString()));
		// 	});
		// } else {
		// 	gymsRef.child(String.valueOf(gym.getGymId()))
		// 			.setValue(gym.toData(null));
		// }
	}
	
	private void addReviewToDb(List<Review> reviews)
	{
		Map<String, Review.ReviewData> rds = new HashMap<>();
		for (Review review : reviews)
		{
			// todo: upload user avatar
			rds.put(review.getReviewId(), review.toData(null));
		}
		reviewsRef.setValue(rds).addOnSuccessListener(unused ->
		{
			Log.d(TAG, "Upload mock reviews succeed");
		}).addOnFailureListener(e ->
		{
			Log.e(TAG, "Upload mock reviews failed", e);
		});
	}
	
	private void addTrainerToDatabase(PersonalTrainer trainer)
	{
		// Name of gym picture
		Map<String, Object> tm = new HashMap<>();
		tm.put(KEY_TRAINER_name, trainer.getName());
		tm.put(KEY_TRAINER_price, trainer.getPrice());
		List<String> list = new ArrayList<>();
		for (Timeslot timeslot : trainer.getAvailableTimes())
		{
			list.add(timeslot.toDatabaseString());
		}
		tm.put(KEY_TRAINER_times, list);
		
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		CollectionReference cardsRef = db.collection("TRAINERS");
		cardsRef.add(tm)
				.addOnSuccessListener(new OnSuccessListener<DocumentReference>()
				{
					@Override
					public void onSuccess(DocumentReference documentReference)
					{
						// importCardsFromDB();
						Log.d(TAG, "Add trainer gyms successfully: " + documentReference.getId());
					}
				})
				.addOnFailureListener(new OnFailureListener()
				{
					@Override
					public void onFailure(@NonNull Exception e)
					{
						Log.d(TAG, "Add trainer gyms Failed: " + e.toString());
						e.printStackTrace();
					}
				});
		
		//		StorageReference pictureRef = trainerAvatarRef.child(trainer.getTrainerId() + ".jpg");
		//
		//		// upload gym picture
		//		if (trainer.getAvatar() != null) {
		//			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		//			trainer.getAvatar().compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
		//			byte[] data = outputStream.toByteArray();
		//			UploadTask task = pictureRef.putBytes(data);
		//			task.addOnFailureListener(e -> {
		//				Log.e(TAG, Arrays.toString(e.getStackTrace()));
		//			}).addOnSuccessListener(taskSnapshot -> {
		//				trainersRef.child(String.valueOf(trainer.getTrainerId()))
		//						.setValue(trainer.toData(
		//								pictureRef.getDownloadUrl().getResult().toString()));
		//			});
		//		} else {
		//			trainersRef.child(String.valueOf(trainer.getTrainerId()))
		//					.setValue(trainer.toData(null));
		//		}
	}
	
	private void randomAddEquipment(Gym gym)
	{
		String[] equipments = new String[]{"Barbell", "Bicycle", "Climbing", "Dumbbell",
				"Rowing", "Swimming", "Treadmill"};
		int num = (int) (Math.random()*equipments.length);
		List<String> lst = Arrays.asList(equipments);
		Collections.shuffle(lst);
		for (int i = 0; i < num; i++)
			gym.getEquipments().add(lst.get(i));
	}
	
	private void addMockTrainersInThisWeek(List<PersonalTrainer> list, Gym gym, String trainerId,
	                                       String trainerName, int price)
	{
		
		Calendar cal = GymViewModel.beginOfADay(Calendar.getInstance());
		Calendar openTime = gym.getOpenTime();
		cal.set(Calendar.HOUR_OF_DAY, openTime.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, openTime.get(Calendar.MINUTE));
		double openHours = (double) (gym.getCloseTime().getTimeInMillis() -
				openTime.getTimeInMillis())/3_600_000;
		int segments = (int) Math.floor(openHours);
		
		PersonalTrainer trainer = new PersonalTrainer(trainerId, trainerName, price);
		
		for (int day = 0; day < 7; day++)
		{
			Calendar calInDay = (Calendar) cal.clone();
			calInDay.add(Calendar.DAY_OF_MONTH, day);
			for (int hour = 0; hour < segments; hour++)
			{
				trainer.addTimeSlot(new Timeslot((Calendar) calInDay.clone(), 60));
				calInDay.add(Calendar.HOUR_OF_DAY, 1);
			}
		}
		list.add(trainer);
		if (gym != null)
			gym.getPersonalTrainers().add(trainer);
	}
	
	private void addMockUser()
	{
		// Reservation rev1 = new Reservation(
		// 		getUserId(),
		// 		"Minus Fitness Gym Chatswood",
		// 		null,
		// 		20,
		// 		new Timeslot(CalendarUtil.stringToCalendar("2021-10-28 10:00"), 60)
		// );
		// Reservation rev2 = new Reservation(
		// 		getUserId(),
		// 		"Fitness Second St Leonards",
		// 		"Tom",
		// 		52,
		// 		new Timeslot(CalendarUtil.stringToCalendar("2021-10-22 09:00"), 60)
		// );
		// Reservation rev3 = new Reservation(
		// 		getUserId(),
		// 		"Fitness Second St Leonards",
		// 		"Jerry",
		// 		56,
		// 		new Timeslot(CalendarUtil.stringToCalendar("2021-10-23 11:00"), 120)
		// );
		//
		// UserDataContent udc = new UserDataContent();
		// udc.reservations = new ArrayList<>();
		// for (Reservation rsv : new Reservation[]{rev1, rev2, rev3})
		// {
		// 	udc.reservations.add(rsv.toData());
		// }
		//
		// Map<String, String> mem1 = new HashMap<>();
		// mem1.put("gymID", "Minus Fitness Gym Chatswood");
		// mem1.put("title", "Yearly plan");
		// mem1.put("startTime", "2021-05-09 00:00");
		// mem1.put("endTime", "2022-05-08 23:59");
		//
		// Map<String, String> mem2 = new HashMap<>();
		// mem2.put("gymID", "Minus Fitness Crows Nest");
		// mem2.put("title", "Yearly plan");
		// mem2.put("startTime", "2021-08-16 00:00");
		// mem2.put("endTime", "2022-08-15 23:59");
		//
		// udc.memberships = new ArrayList<>();
		// udc.memberships.add(mem1);
		// udc.memberships.add(mem2);
		//
		// udc.favouriteGyms = new ArrayList<>();
		// udc.favouriteGyms.add("Minus Fitness Gym Chatswood");
		// udc.favouriteGyms.add("Minus Fitness Crows Nest");
		// udc.favouriteGyms.add("Fitness Second Bond St");
		//
		// userRef.setValue(udc).addOnSuccessListener(unused ->
		// {
		// 	addUserInfoChangeListener();
		// }).addOnFailureListener(e ->
		// {
		// 	Log.e(TAG, "Failed to add mock user", e);
		// });
	}
	
	public void addMockGymsToDatabase()
	{
		List<PersonalTrainer> allTrainers = new ArrayList<>();
		List<Gym> allGyms = new ArrayList<>();
		List<Review> allReviews = new ArrayList<>();
		
		Gym gym0 = new Gym(
				"Minus Fitness Gym Chatswood",
				"Minus Fitness Gym Chatswood",
				CalendarUtil.stringToCalendarNoDate("08:30"),
				CalendarUtil.stringToCalendarNoDate("17:30"),
				1990,
				"763 Pacific Hwy, Chatswood NSW 2067",
				"123-4567",
				151.1792,
				-33.79911
		);
		addMockTrainersInThisWeek(allTrainers, gym0, "Otto", "Otto", 4000);
		addMockTrainersInThisWeek(allTrainers, gym0, "Mary", "Mary", 3000);
		
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
				2000,
				"400 Pacific Hwy, Crows Nest NSW 2065",
				"123-4567",
				151.19854,
				-33.82581
		);
		addMockTrainersInThisWeek(allTrainers, gym1, "Jack", "Jack", 3500);
		
		Gym gym2 = new Gym(
				"Fitness Second St Leonards",
				"Fitness Second St Leonards",
				CalendarUtil.stringToCalendarNoDate("09:00"),
				CalendarUtil.stringToCalendarNoDate("19:00"),
				2000,
				"55 Christie St, St Leonards NSW 2065",
				"123-4567",
				151.19584,
				-33.82445
		);
		addMockTrainersInThisWeek(allTrainers, gym2, "Tom", "Tom", 3200);
		addMockTrainersInThisWeek(allTrainers, gym2, "Jerry", "Jerry", 3600);
		
		Gym gym3 = new Gym(
				"Fitness Second North Sydney",
				"Fitness Second North Sydney",
				CalendarUtil.stringToCalendarNoDate("09:00"),
				CalendarUtil.stringToCalendarNoDate("19:00"),
				2000,
				"1 Elizabeth Plaza, North Sydney NSW 2060",
				"123-4567",
				-33.83945,
				151.20809
		);
		addMockTrainersInThisWeek(allTrainers, gym3, "Aaron", "Aaron", 2000);
		
		Gym gym4 = new Gym(
				"Fitness Second Bond St",
				"Fitness Second Bond St",
				CalendarUtil.stringToCalendarNoDate("09:00"),
				CalendarUtil.stringToCalendarNoDate("19:00"),
				2000,
				"20 Bond St, Sydney NSW 2000",
				"123-4567",
				151.20829,
				-33.86441
		);
		addMockTrainersInThisWeek(allTrainers, gym4, "Subaru", "Subaru", 3000);
		addMockTrainersInThisWeek(allTrainers, gym4, "Emiria", "Emiria", 4000);
		addMockTrainersInThisWeek(allTrainers, gym4, "Rem", "Rem", 4000);
		
		Gym gym5 = new Gym(
				"Minus Fitness Market Street",
				"Minus Fitness Market Street",
				CalendarUtil.stringToCalendarNoDate("09:00"),
				CalendarUtil.stringToCalendarNoDate("19:00"),
				1600,
				"25 Market St, Sydney NSW 2000",
				"123-4567",
				151.20522,
				-33.87115
		);
		addMockTrainersInThisWeek(allTrainers, gym5, "Peter", "Peter", 2200);
		
		Gym gym6 = new Gym(
				"Minus Fitness Waterloo",
				"Minus Fitness Waterloo",
				CalendarUtil.stringToCalendarNoDate("09:00"),
				CalendarUtil.stringToCalendarNoDate("19:00"),
				1500,
				"11A Lachlan St, Waterloo NSW 2017",
				"123-4567",
				151.21178,
				-33.90103
		);
		addMockTrainersInThisWeek(allTrainers, gym6, "Larry", "Larry", 2800);
		
		Gym gym7 = new Gym(
				"Notime Fitness North Sydey",
				"Notime Fitness North Sydey",
				CalendarUtil.stringToCalendarNoDate("09:00"),
				CalendarUtil.stringToCalendarNoDate("19:00"),
				2000,
				"118 Walker St, North Sydney NSW 2060",
				"123-4567",
				151.208801,
				-33.837711
		);
		addMockTrainersInThisWeek(allTrainers, gym7, "Henry", "Henry", 3000);
		
		Gym gym8 = new Gym(
				"Notime Fitness City",
				"Notime Fitness City",
				CalendarUtil.stringToCalendarNoDate("09:00"),
				CalendarUtil.stringToCalendarNoDate("19:00"),
				2000,
				"227 Elizabeth St, Sydney NSW 2000",
				"123-4567",
				151.2102227,
				-33.8706586
		);
		addMockTrainersInThisWeek(allTrainers, gym8, "Jenny", "Jenny", 3000);
		
		Gym gym9 = new Gym(
				"Sliver's Gym",
				"Sliver's Gym",
				CalendarUtil.stringToCalendarNoDate("09:00"),
				CalendarUtil.stringToCalendarNoDate("19:00"),
				2000,
				"7-9 West St, North Sydney NSW 2060",
				"123-4567",
				151.2052855,
				-33.8334692
		);
		addMockTrainersInThisWeek(allTrainers, gym9, "Nofe", "Nofe", 1800);
		
		addMockTrainersInThisWeek(allTrainers, gym9, "Alex", "Alex", 2100);
		addMockTrainersInThisWeek(allTrainers, gym9, "Leon", "Leon", 2190);
		addMockTrainersInThisWeek(allTrainers, gym9, "Bill", "Bill", 2000);
		addMockTrainersInThisWeek(allTrainers, gym9, "Zoe", "Zoe", 1700);
		addMockTrainersInThisWeek(allTrainers, gym9, "Aatrox", "Aatrox", 2500);
		addMockTrainersInThisWeek(allTrainers, gym9, "Jinx", "Jinx", 2400);
		addMockTrainersInThisWeek(allTrainers, gym9, "Jax", "Jax", 2300);
		addMockTrainersInThisWeek(allTrainers, gym9, "Trundle", "Trundle", 2600);
		addMockTrainersInThisWeek(allTrainers, gym9, "Denny", "Denny", 2000);
		
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
		
		for (Gym gym : allGyms)
		{
			addGymToDatabase(gym);
		}
		//		for (PersonalTrainer personalTrainer : allTrainers) {
		//			addTrainerToDatabase(personalTrainer);
		//		}
		//		addReviewToDb(allReviews);
		
		//		for (Gym gym : allGyms) {
		//			addGymToDatabase(gym);
		//		}
		for (PersonalTrainer personalTrainer : allTrainers)
		{
			addTrainerToDatabase(personalTrainer);
		}
		//		addReviewToDb(allReviews);
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
	
	public List<SimpleGym> getAllSimpleGyms()
	{
		return allSimpleGyms;
	}
	
	@Deprecated
	public List<PersonalTrainer> getAllTrainers()
	{
		return allTrainers;
	}
	
	// public void findGymById(String gymId, GymQueryCallback callback)
	// {
	// 	// gymsRef.child(gymId).get().addOnCompleteListener(task ->
	// 	// {
	// 	// 	if (task.isSuccessful())
	// 	// 	{
	// 	// 		Gym.GymData gymData = task.getResult().getValue(Gym.GymData.class);
	// 	// 		if (gymData == null)
	// 	// 		{
	// 	// 			callback.onFailed(new NullPointerException(
	// 	// 					"Query result of gym " + gymId + " is null"));
	// 	// 			return;
	// 	// 		}
	// 	//
	// 	// 		// Then query trainers of this gym
	// 	// 		populateTrainersOfGym(gymData, callback);
	// 	// 	}
	// 	// 	else
	// 	// 	{
	// 	// 		Log.e(TAG, Arrays.toString(task.getException().getStackTrace()));
	// 	// 		callback.onFailed(task.getException());
	// 	// 	}
	// 	// });
	// }
	
	private void populateTrainersOfGym(Gym.GymData gymData, GymQueryCallback callback)
	{
		// List<PersonalTrainer> trainers = new ArrayList<>();
		// List<Review> reviews = new ArrayList<>();
		// for (String tid : gymData.trainerIds)
		// {
		// 	findTrainerById(tid, new TrainerQueryCallback()
		// 	{
		// 		@Override
		// 		public void onSucceed(PersonalTrainer trainer)
		// 		{
		// 			trainers.add(trainer);
		// 			allTrainers.add(trainer);
		// 			if (trainers.size() == gymData.trainerIds.size())
		// 			{
		// 				// Last trainer has been added, ready to open
		// 				if (gymData.reviewIds != null)
		// 				{
		// 					for (String rid : gymData.reviewIds)
		// 					{
		// 						// todo
		// 					}
		// 				}
		// 				if (gymData.picturePath == null)
		// 				{
		// 					Gym gym = Gym.fromGymData(gymData, trainers, reviews, null);
		// 					callback.onSucceed(gym);
		// 				}
		// 				else
		// 				{
		// 					// todo
		// 				}
		// 			}
		// 		}
		//
		// 		@Override
		// 		public void onFailed(Exception exception)
		// 		{
		// 			Log.e(TAG, Arrays.toString(exception.getStackTrace()));
		// 		}
		// 	});
		// }
	}
	
	public Gym findGymById(String gymId)
	{
//		Log.d(TAG, "Looking for id " + gymId);
//		Log.d(TAG, allSimpleGyms.toString());
//		for (Gym gym : allSimpleGyms)
//		{
//			Log.d(TAG, "Scanning " + gym.getGymId());
//			if (gymId.equals(gym.getGymId()))
//			{
//				return gym;
//			}
//		}
		return null;
	}
	
	public void findTrainerById(String trainerId, TrainerQueryCallback callback)
	{
		trainersRef.child(String.valueOf(trainerId)).get().addOnSuccessListener(dataSnapshot ->
		{
			PersonalTrainer.TrainerData td =
					dataSnapshot.getValue(PersonalTrainer.TrainerData.class);
			if (td == null)
			{
				callback.onFailed(new NullPointerException(
						"Query result of trainer " + trainerId + " is null"));
				return;
			}
			if (td.avatarPath == null)
			{
				PersonalTrainer trainer = PersonalTrainer.fromData(td, null);
				callback.onSucceed(trainer);
			}
			else
			{
				// todo
			}
		}).addOnFailureListener(callback :: onFailed);
	}
	
	public DatabaseReference getUserRef()
	{
		return userRef;
	}
	
	public DatabaseReference getTrainersRef()
	{
		return trainersRef;
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
	
	public void sortScheduleLists()
	{
		Log.e(TAG, "---------------------" + this.getScheduleLists().size());
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
	
	
	public void addPurchaseRecordToDatabase(PurchaseRecord purchaseRecord)
	{
		Map<String, String> recordItem = new HashMap<>();
		recordItem.put("price", String.valueOf(purchaseRecord.getCost()));
		recordItem.put("gymId", purchaseRecord.getTitle());
		recordItem.put("time", purchaseRecord.getTimeStr());
		userRef.child("purchaseRecord").child(String.valueOf(purchaseRecords.size() - 1))
				.setValue(recordItem).addOnSuccessListener(unused ->
		{
		
		}).addOnFailureListener(e ->
		{
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
		this.creditCards.add(0, creditCard);
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
		Log.e(TAG, "removeCreditCard: " + this.getCreditCards());
		postValue(this);
	}
	
	/**
	 * Memberships
	 */
	
	public ArrayList<Membership> getMemberships()
	{
		return memberships;
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
		
		// userSignIn();
		
		//		userRsvRef = userRef.child("reservations");
		
		postValue(this);
	}
	
	// private void userSignIn()
	// {
	// 	userRef = dbRef.child("users").child(getUserId());
	// 	// Check if the user's info is in firebase
	// 	userRef.get().addOnCompleteListener(task ->
	// 	{
	// 		DataSnapshot snapshot = task.getResult();
	// 		UserDataContent udc = snapshot.getValue(UserDataContent.class);
	// 		if (udc == null)
	// 		{
	// 			// First time login
	// 			addMockUser();
	// 		}
	// 		else
	// 		{
	// 			addUserInfoChangeListener();
	// 		}
	// 	});
	// }
	
	// private void addUserInfoChangeListener()
	// {
	// 	userRef.addValueEventListener(new ValueEventListener()
	// 	{
	// 		@Override
	// 		public void onDataChange(@NonNull DataSnapshot snapshot)
	// 		{
	// 			UserDataContent udc = snapshot.getValue(UserDataContent.class);
	// 			if (udc == null)
	// 			{
	// 				Log.d(TAG, "User is null");
	// 				return;
	// 			}
	// 			reservations = new ArrayList<>();
	// 			if (udc.reservations != null)
	// 			{
	// 				for (Reservation.ReservationData rd : udc.reservations)
	// 				{
	// 					reservations.add(Reservation.fromData(rd));
	// 				}
	// 			}
	// 			memberships = new ArrayList<>();
	// 			if (udc.memberships != null)
	// 			{
	// 				for (Map<String, String> map : udc.memberships)
	// 				{
	// 					Membership mem = new Membership(map.get("gymID"),
	// 							map.get("title"),
	// 							null,
	// 							CalendarUtil.stringToCalendar(map.get("startTime")),
	// 							CalendarUtil.stringToCalendar(map.get("endTime")));
	// 					memberships.add(mem);
	// 				}
	// 			}
	// 			favouriteGyms = new ArrayList<>();
	// 			if (udc.favouriteGyms != null)
	// 			{
	// 				favouriteGyms.addAll(udc.favouriteGyms);
	// 			}
	// 		}
	//
	// 		@Override
	// 		public void onCancelled(@NonNull DatabaseError error)
	// 		{
	//
	// 		}
	// 	});
	// }
	
	public String getUserName()
	{
		if (userName != null)
		{
			return userName;
		}
		else if (firebaseUser != null)
		{
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
	
	public String getUserId()
	{
		if (userId != null)
		{
			return userId;
		}
		else if (firebaseUser != null)
		{
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
		else if (firebaseUser != null)
		{
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
	
	public List<String> getFavouriteGyms()
	{
		if (favouriteGyms == null)
		{
			favouriteGyms = new ArrayList<>();
		}
		return this.favouriteGyms;
	}
	
	public void addToFavouriteGyms(String gymId)
	{
		favouriteGyms.add(gymId);
		userRef.child(getUserId()).child("favouriteGyms").setValue(favouriteGyms);
		postValue(this);
	}
	
	public void removeFromFavouriteGyms(String gymId)
	{
		favouriteGyms.remove(gymId);
		userRef.child(getUserId()).child("favouriteGyms").setValue(favouriteGyms);
		postValue(this);
	}
	
	public void setFavouriteGyms(List<String> favouriteGyms)
	{
		this.favouriteGyms = favouriteGyms;
		postValue(this);
	}
	
	public void setContext(Context mContext)
	{
		this.mContext = mContext;
	}
	
	public List<Reservation> getReservations()
	{
		if (reservations == null)
		{
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
	public boolean hasBeenToGym(String gymId)
	{
		List<Reservation> rsvList = getReservations();
		Calendar now = Calendar.getInstance();
		for (Reservation rsv : rsvList)
		{
			if (rsv.getGymId().equals(gymId) &&
					now.after(rsv.getSelectedTimeSlot().getBeginTime()))
			{
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
	
	public void sortPurchaseRecords()
	{
		Log.e(TAG, "---------------------" + this.getPurchaseRecords().size());
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
	
	public void setSuccessful(boolean successful)
	{
		isSuccessful = successful;
	}
	
	public boolean isSuccessful()
	{
		return isSuccessful && downloadFinished;
	}

	public void getUsernameByUID(String UID, ObjectQueryCallback callback) {
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		db.collection(KEY_USERS).document(UID).get().addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				try {
					callback.onSucceed(task.getResult().get(KEY_userName));
				} catch (Exception e) {
					callback.onFailed(e);
				}
			} else {
				callback.onFailed(task.getException());
			}
		});
	}
	
	public void getTrainersByGymId(String GID, ListQueryCallback callback)
	{
		String mTag = "[getTrainersByGymId]";
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		CollectionReference ref = db.collection(KEY_TRAINERS);
		final PersonalTrainer[] trainers = {null};
		ref.whereEqualTo(KEY_TRAINER_gymId, GID).get()
				.addOnCompleteListener(task ->
				{
					if (task.isSuccessful())
					{
						try
						{
							ArrayList<PersonalTrainer> trainersList = new ArrayList<>();
							Log.d(TAG, "Found " + task.getResult().getDocuments().size() + " trainers in DB");
							for (QueryDocumentSnapshot document : task.getResult())
							{
								List<String> tsStrings = (List<String>) document.get(KEY_TRAINER_times);
								List<Timeslot> ts = new ArrayList<>();
								for (String tss : tsStrings)
								{
									ts.add(Timeslot.fromDatabaseString(tss));
								}
								PersonalTrainer trainer = new PersonalTrainer(
										document.getId(),
										document.get(KEY_TRAINER_name, String.class),
										Math.toIntExact((long) document.get(KEY_TRAINER_price)),
										ts
								);
								trainers[0] = trainer;
								trainersList.add(trainer);
							}
							Log.d(mTag, "getTrainersByGymId successfully!" + GID);
							//								comp5216.sydney.edu.au.findmygym.model.UserData.getInstance().rev = reviewsList;
							callback.onSucceed(trainersList);
						} catch (Exception e)
						{
							callback.onFailed(e);
						}
					}
					else
					{
						Log.d(mTag, "getTrainersByGymId failed!" + GID);
					}
				});
	}
	
	public void getReviewsByGymId(String GID, ListQueryCallback callback)
	{
		String mTag = "[getReviewsByGymId]";
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		CollectionReference ref = db.collection(KEY_REVIEWS);
		final Review[] reviews = {null};
		ref.whereEqualTo(KEY_REVIEW_gymId, GID).get()
				.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
				{
					@Override
					public void onComplete(@NonNull Task<QuerySnapshot> task)
					{
						if (task.isSuccessful())
						{
							try
							{
								ArrayList<Review> reviewsList = new ArrayList<>();
								Log.d(TAG, "Found " + task.getResult().getDocuments().size() + " Reviews in DB");
								for (QueryDocumentSnapshot document : task.getResult())
								{
									Review review =
											new Review((String) document.get(KEY_REVIEW_userId),
													(String) document.get(KEY_REVIEW_gymId),
													Math.toIntExact((Long) document.get(KEY_REVIEW_rating)),
													(String) document.get(KEY_REVIEW_comments),
													comp5216.sydney.edu.au.findmygym.Utils.CalendarUtil.timestampToCalendar((Timestamp) document.get(KEY_REVIEW_dateTime)));
									review.setReviewId(document.getId());
									reviews[0] = review;
									reviewsList.add(review);
								}
								Log.d(mTag, "getReviewsByGymId successfully!" + GID);
								//								comp5216.sydney.edu.au.findmygym.model.UserData.getInstance().rev = reviewsList;
								callback.onSucceed(reviewsList);
							} catch (Exception e)
							{
								callback.onFailed(e);
							}
						}
						else
						{
							Log.d(mTag, "getReviewsByGymId failed!" + GID);
						}
					}
				});
	}
	
	public void addReview(Review review)
	{
		Map<String, Object> map = new HashMap<>();
		map.put(KEY_REVIEW_gymId, review.getGymId());
		map.put(KEY_REVIEW_dateTime, new Timestamp(review.getDateTime().getTime()));
		map.put(KEY_REVIEW_rating, review.getRating());
		map.put(KEY_REVIEW_comments, review.getComments());
		map.put(KEY_REVIEW_userId, review.getUserId());
		
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		CollectionReference reviewsRef = db.collection("REVIEWS");
		reviewsRef.add(map)
				.addOnSuccessListener(documentReference ->
				{
					Log.d(TAG, "Add review successfully: " + documentReference.getId());
				})
				.addOnFailureListener(e ->
				{
					Log.d(TAG, "Add review Failed: " + e.toString());
					e.printStackTrace();
				});
	}
}