package comp5216.sydney.edu.au.findmygym.Utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comp5216.sydney.edu.au.findmygym.model.Gym;
import comp5216.sydney.edu.au.findmygym.model.PersonalTrainer;
import comp5216.sydney.edu.au.findmygym.model.Review;
import comp5216.sydney.edu.au.findmygym.model.Timeslot;
import comp5216.sydney.edu.au.findmygym.model.UserData;
import comp5216.sydney.edu.au.findmygym.ui.gym.GymViewModel;

public class MockUtil {

    private static final String TAG = "Mocker";

    UserData userData;

    public MockUtil(UserData userData) {
        this.userData = userData;
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
//            addGymToDatabase(gym);
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


    private void addTrainerToDatabase(PersonalTrainer trainer)
    {
        // Name of gym picture
        Map<String, Object> tm = new HashMap<>();
        tm.put(UserData.KEY_TRAINER_name, trainer.getName());
        tm.put(UserData.KEY_TRAINER_price, trainer.getPrice());
        List<String> list = new ArrayList<>();
        for (Timeslot timeslot : trainer.getAvailableTimes())
        {
            list.add(timeslot.toDatabaseString());
        }
        tm.put(UserData.KEY_TRAINER_times, list);

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
}
