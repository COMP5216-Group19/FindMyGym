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
import comp5216.sydney.edu.au.findmygym.Utils.CalendarUtil;
import comp5216.sydney.edu.au.findmygym.model.callbacks.ListQueryCallback;
import comp5216.sydney.edu.au.findmygym.model.callbacks.ObjectQueryCallback;
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
	
	public static final String KEY_TRAINER_name = "TRAINER_NAME";
	public static final String KEY_TRAINER_price = "TRAINER_PRICE";
	public static final String KEY_TRAINER_times = "TRAINER_TIMES";
	public static final String KEY_TRAINER_gymId = "TRAINER_GYM_ID";
	
	public final String KEY_USER_favourite = "USER_FAVOURITE";
	public final String KEY_USER_AVATAR_URI = "USER_AVATAR_URI";
	
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
	private List<PurchaseRecord> purchaseRecords;
	private List<ScheduleList> scheduleLists;
	private List<CreditCard> creditCards;
	private List<Membership> memberships;
	private FirebaseUser firebaseUser;
	private String userId;
	private String userName;
	private String userMail;
	private Bitmap userAvatar;
	private List<String> favouriteGyms;

	private Session userSession;
	private Context mContext;
	
	private boolean isSuccessful = false;
	private boolean downloadFinished = false;

	// Reservations of this user
	private List<Reservation> reservations = new ArrayList<>();
	private boolean reservationsUpToDate = false;
	
	// This list will load when launching this app
	// Displays these gyms on map
	// TODO: 让这个list在loading界面load，load完了再进map
	// TODO: 或者观察这个list，随list更新map
	private List<SimpleGym> allSimpleGyms = new ArrayList<>();
	
	private volatile static UserData UserData;
	
	/**
	 * Default Constructor
	 */
	private UserData()
	{
		this.memberships = new ArrayList<>();
		this.purchaseRecords = new ArrayList<>();
		this.scheduleLists = new ArrayList<>(1);
		
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
		map.put(this.KEY_TRANSACTIONS_time, CalendarUtil.calendarToString(purchaseRecord.getTime()));
		
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		CollectionReference reference = db.collection(KEY_TRANSACTIONS);
		reference.add(map)
				.addOnSuccessListener(new OnSuccessListener<DocumentReference>()
				{
					@Override
					public void onSuccess(DocumentReference documentReference)
					{
						Log.d(TAG, "Add PurchaseRecord Successfully: " + documentReference.getId());
						Log.d(TAG, "onSuccess: Before add" + getPurchaseRecords().size());
						purchaseRecord.setID(documentReference.getId());
						addPurchaseRecordToList(purchaseRecord);
						Log.d(TAG, "onSuccess: After add" + getPurchaseRecords().size());
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
	
	public void getPurchaseRecordsByUID(String UID, ListQueryCallback<PurchaseRecord> callback)
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
											CalendarUtil.stringToCalendar((String) document.get(KEY_TRANSACTIONS_time))
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
		map.put(this.KEY_MEMBERSHIP_startTime, CalendarUtil.calendarToString(membership.getStartTime()));
		map.put(this.KEY_MEMBERSHIP_endTime, CalendarUtil.calendarToString(membership.getEndTime()));
		
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		CollectionReference reference = db.collection(KEY_MEMBERSHIP);
		reference.add(map)
				.addOnSuccessListener(new OnSuccessListener<DocumentReference>()
				{
					@Override
					public void onSuccess(DocumentReference documentReference)
					{
						Log.d(TAG, "Add Membership Successfully: " + documentReference.getId());
						memberships.add(membership);
						post();
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
	
	
	public void getMembershipsByUID(String UID, ListQueryCallback<Membership> callback)
	{
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
								List<Membership> membershipList = new ArrayList<>();
								Log.d(TAG, "Found " + task.getResult().getDocuments().size() + " Memberships in DB");
								for (QueryDocumentSnapshot document : task.getResult())
								{
									membershipList.add(new Membership((String) document.get(KEY_MEMBERSHIP_ID_user),
											document.get(KEY_MEMBERSHIP_ID_gym, String.class),
											document.get(KEY_MEMBERSHIP_title, String.class),
											CalendarUtil.stringToCalendar(document.get(KEY_MEMBERSHIP_startTime, String.class)),
											CalendarUtil.stringToCalendar(document.get(KEY_MEMBERSHIP_endTime, String.class))
									));
								}
								Log.d(TAG, "getMembershipsByUID successfully!" + UID);
								memberships = membershipList;
								post();
								callback.onSucceed(membershipList);
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
	
	public void addReservation(Reservation reservation, ObjectQueryCallback<Reservation> callback)
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
						reservations.add(reservation);
						post();
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
	
	
	public void getReservationByID(String ID, ObjectQueryCallback<Reservation> callback)
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

	public void getReservationsOfThisUser(ListQueryCallback<Reservation> callback) {
		if (reservationsUpToDate) {
			Log.d(TAG, "Reservations already downloaded!");
			callback.onSucceed(reservations);
			return;
		}
		Log.d(TAG, "Reservations not downloaded, downloading from database!");
		getReservationsByUID(getUserId(), callback);
	}
	
	public void getReservationsByUID(String UID, ListQueryCallback<Reservation> callback)
	{
		if (UID.equals(getUserId())) {
			reservationsUpToDate = false;
		}
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		CollectionReference ref = db.collection(getInstance().KEY_RESERVATION);
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
								List<Reservation> reservationList = new ArrayList<>();
								Log.d(TAG, "Found " + task.getResult().getDocuments().size() + " Reservations in DB");
								for (QueryDocumentSnapshot document : task.getResult())
								{
									Reservation reservation = new Reservation(document.getId(),
											document.get(KEY_RES_ID_user, String.class),
											document.get(KEY_RES_ID_gym, String.class),
											document.get(KEY_RES_ID_trainer, String.class),
											Math.toIntExact(document.get(KEY_RES_price, Long.class)),
											Timeslot.fromDatabaseString(document.get(KEY_RES_timeSlot, String.class)));
									reservationList.add(reservation);
								}
								Log.d(TAG, "getReservationByUID successfully!" + UID);
								if (UID.equals(getUserId())) {
									reservations = reservationList;
									reservationsUpToDate = true;
								}
								post();
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
	
	public void getGymByID(String ID, ObjectQueryCallback<Gym> callback)
	{
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		DocumentReference gymRef = db.collection(KEY_GYMS).document(ID);
		gymRef.get().addOnCompleteListener(task -> {
			if (task.isSuccessful())
			{
				String name = task.getResult().get(KEY_GYM_name, String.class);
				String contact = task.getResult().get(KEY_GYM_contact, String.class);
				String address = task.getResult().get(KEY_GYM_address, String.class);
				int price = Math.toIntExact(task.getResult().get(KEY_GYM_price, Long.class));
				String openTime = task.getResult().get(KEY_GYM_openTime, String.class);
				String closeTime = task.getResult().get(KEY_GYM_closeTime, String.class);
				List<String> equipments = (List<String>) task.getResult().get(KEY_GYM_equipments);
				Double latitude = task.getResult().get(KEY_GYM_latitude, Double.class);
				Double longitude = task.getResult().get(KEY_GYM_longitude, Double.class);

				Log.d(TAG, ID + " name: " + name);
				Log.d(TAG, ID + " contact: " + contact);
				Log.d(TAG, ID + " latitude: " + latitude);
				Log.d(TAG, ID + " longitude: " + longitude);
				Log.d(TAG, ID + " equipments: " + equipments);

				getTrainersByGymId(ID, new ListQueryCallback<PersonalTrainer>()
				{
					@Override
					public void onSucceed(List<PersonalTrainer> list)
					{
						Log.d(TAG, " successfully get trainers of gym " + ID);
						final List<PersonalTrainer> trainerList = list;
						getReviewsByGymId(ID, new ListQueryCallback<Review>()
						{
							@Override
							public void onSucceed(List<Review> list)
							{
								Log.d(TAG, " successfully get reviews of gym " + ID);
								Gym gym = new Gym(
										ID,
										name,
										trainerList,
										CalendarUtil.stringToCalendar(openTime),
										CalendarUtil.stringToCalendar(closeTime),
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
								callback.onFailed(e);
							}
						});
					}

					@Override
					public void onFailed(Exception e)
					{
						Log.d(TAG, " failed to get trainers of gym " + ID, e);
						callback.onFailed(e);
					}
				});
			} else {
				callback.onFailed(task.getException());
			}
		});
	}
	
	
	public void getTrainerByID(String ID, ObjectQueryCallback<PersonalTrainer> callback)
	{
		
		String mTag = "[getTrainerByID]";
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		DocumentReference gymRef = db.collection(KEY_TRAINERS).document(ID);
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
								String name = task.getResult().get(KEY_TRAINER_name, String.class);
								int price = Math.toIntExact(task.getResult().get(KEY_TRAINER_price, Long.class));
								ArrayList<String> times = (ArrayList) task.getResult().get(KEY_TRAINER_times);
								ArrayList<Timeslot> timeslots = new ArrayList<>();
								if (times != null) {
									for (String time : times) {
										timeslots.add(Timeslot.fromDatabaseString(time));
									}
								}
								Log.d(TAG, "name: " + name);
								Log.d(TAG, "price: " + price);
								Log.d(TAG, "Timeslot: " + timeslots);
								PersonalTrainer personalTrainer = new PersonalTrainer(ID, name, price, timeslots);
								Log.d(TAG, "getTrainerByID successfully!" + ID);
								callback.onSucceed(personalTrainer);
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
	
	
	public void getAvatarByUid(String ID, ObjectQueryCallback<Uri> callback)
	{

		FirebaseFirestore db = FirebaseFirestore.getInstance();
		DocumentReference gymRef = db.collection(KEY_USERS).document(ID);
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
								String str = (String) task.getResult().get(comp5216.sydney.edu.au.findmygym.model.UserData.getInstance().KEY_USER_AVATAR_URI);
								Uri uri = Uri.parse(str);
								callback.onSucceed(uri);
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
	
	/*
	Methods for mock database
	 */
	
	public List<SimpleGym> getAllSimpleGyms()
	{
		return allSimpleGyms;
	}
	
	@Deprecated
	public List<PersonalTrainer> getAllTrainers()
	{
		return null;
	}
	
	/**
	 * ScheduleLists
	 */
	
	public List<ScheduleList> getScheduleLists()
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
	
	
	public void setScheduleLists(List<ScheduleList> scheduleLists)
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
	
	public List<PurchaseRecord> getPurchaseRecords()
	{
		return purchaseRecords;
	}
	
	public void setPurchaseRecords(List<PurchaseRecord> purchaseRecords)
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
	
	public List<CreditCard> getCreditCards()
	{
		return creditCards;
	}
	
	public void addCreditCard(CreditCard creditCard)
	{
		this.creditCards.add(0, creditCard);
		postValue(this);
	}
	
	public void setCreditCards(List<CreditCard> creditCards)
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
	
	public List<Membership> getMemberships()
	{
		return memberships;
	}
	
	
	public void setMemberships(List<Membership> memberships)
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
	
	public void addToFavouriteGym(String gymId)
	{
		favouriteGyms.add(gymId);
		postValue(this);
	}
	
	public void removeFromFavouriteGyms(String gymId)
	{
		favouriteGyms.remove(gymId);
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
	
//	public void getReservations()
//	{
//		if (reservations == null)
//		{
//			reservations = new ArrayList<>();
//		}
//		return reservations;
//	}
//
//	public boolean visitedGym(String gymId, ListQueryCallback<Reservation> callback)
//	{
//		List<Reservation> rsvList = getReservations();
//		Calendar now = Calendar.getInstance();
//		for (Reservation rsv : rsvList)
//		{
//			if (rsv.getGymId().equals(gymId) &&
//					now.after(rsv.getSelectedTimeSlot().getBeginTime()))
//			{
//				// Only past reservation counts
//				return true;
//			}
//		}
//		return false;
//	}
	
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
		mContext = null;
	}
	
	public void sortPurchaseRecords()
	{
		Log.e(TAG, "---------------------" + this.getPurchaseRecords().size());
		this.purchaseRecords.sort((t1, t2) -> {
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

	public void getUsernameByUID(String UID, ObjectQueryCallback<String> callback) {
		if (UID.equals(getUserId())) {
			callback.onSucceed(getUserName());
			return;
		}
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		db.collection(KEY_USERS).document(UID).get().addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				try {
					callback.onSucceed(task.getResult().get(KEY_userName, String.class));
				} catch (Exception e) {
					callback.onFailed(e);
				}
			} else {
				callback.onFailed(task.getException());
			}
		});
	}
	
	public void getTrainersByGymId(String GID, ListQueryCallback<PersonalTrainer> callback)
	{
		String mTag = "[getTrainersByGymId]";
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		CollectionReference ref = db.collection(KEY_TRAINERS);
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
								if (tsStrings != null) {
									for (String tss : tsStrings) {
										ts.add(Timeslot.fromDatabaseString(tss));
									}
								}
								PersonalTrainer trainer = new PersonalTrainer(
										document.getId(),
										document.get(KEY_TRAINER_name, String.class),
										Math.toIntExact(document.get(KEY_TRAINER_price, Long.class)),
										ts
								);
								trainersList.add(trainer);
							}
							Log.d(mTag, "getTrainersByGymId successfully!" + GID);
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
	
	public void getReviewsByGymId(String GID, ListQueryCallback<Review> callback)
	{
		String mTag = "[getReviewsByGymId]";
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		CollectionReference ref = db.collection(KEY_REVIEWS);
		ref.whereEqualTo(KEY_REVIEW_gymId, GID).get()
				.addOnCompleteListener(task -> {
					if (task.isSuccessful())
					{
						try
						{
							ArrayList<Review> reviewsList = new ArrayList<>();
							Log.d(TAG, "Found " + task.getResult().getDocuments().size() + " Reviews in DB");
							for (QueryDocumentSnapshot document : task.getResult())
							{
								Review review =
										new Review(document.get(KEY_REVIEW_userId, String.class),
												document.get(KEY_REVIEW_gymId, String.class),
												Math.toIntExact(document.get(KEY_REVIEW_rating, Long.class)),
												document.get(KEY_REVIEW_comments, String.class),
												CalendarUtil.timestampToCalendar(document.get(KEY_REVIEW_dateTime, Timestamp.class)));
								review.setReviewId(document.getId());
								reviewsList.add(review);
							}
							Log.d(mTag, "getReviewsByGymId successfully!" + GID);
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
				});
	}
	
	public void addReview(Review review, ObjectQueryCallback<Review> callback)
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
					callback.onSucceed(review);
				})
				.addOnFailureListener(e ->
				{
					Log.d(TAG, "Add review Failed: " + e);
					callback.onFailed(e);
				});
	}
}