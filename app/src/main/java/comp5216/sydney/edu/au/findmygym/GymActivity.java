package comp5216.sydney.edu.au.findmygym;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import java.util.ArrayList;
import java.util.Calendar;

import comp5216.sydney.edu.au.findmygym.model.CreditCard;
import comp5216.sydney.edu.au.findmygym.model.Gym;
import comp5216.sydney.edu.au.findmygym.model.Reservation;
import comp5216.sydney.edu.au.findmygym.model.UserData;
import comp5216.sydney.edu.au.findmygym.ui.gym.GymFragment;
import comp5216.sydney.edu.au.findmygym.ui.gym.GymViewModel;
import comp5216.sydney.edu.au.findmygym.ui.gym.TrainerReservation;

public class GymActivity extends AppCompatActivity {

    private final String TAG = "[GymActivity]";
    public boolean isFavourite;
    Context mContext;
    GymViewModel mViewModel;
    private Menu optionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gym_activity);
        mContext = GymActivity.this;

        mViewModel = new ViewModelProvider(this).get(GymViewModel.class);
        mViewModel.setGym((Gym) getIntent().getSerializableExtra("gym"));

        isFavourite = mViewModel.getGym().isFavourite();
        if (optionsMenu != null) {
            Log.d(TAG, "onCreateOptionsMenu called first");
            //            onOptionsItemSelected(optionsMenu);
        } else {
            Log.d(TAG, "favourite item still null");
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.gym_container, GymFragment.newInstance())
                    .commitNow();
        }

        //        UserData.getInstance().addReview(new Review(
        //                UserData.getInstance().getUserId(),
        //                "8Pp4nlV5Fc3XW06BXkhV",
        //                4,
        //                "WTF",
        //                Calendar.getInstance()
        //        ));

        //  store the menu to var when creating options menu
        // optionsMenu = menu;
        // optionsMenu.findItem(R.id.action_favourite).setIcon(R.drawable.azi);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean b = super.onCreateOptionsMenu(menu);
        // getMenuInflater().inflate(R.menu.gym, menu);
        //  store the menu to var when creating options menu
        optionsMenu = menu;
        UserData userData = UserData.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection(userData.KEY_USERS).document(userData.getUserId());
        ref.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        DocumentSnapshot doc = task.getResult();
                        ArrayList<String> favouriteList = (ArrayList) doc.get(userData.KEY_USER_favourite);

                        Log.d(TAG, "Checking " + " Favourite Gyms in DB" + favouriteList.toString());
                        if (favouriteList.contains(mViewModel.getGym().getGymId())) {
                            isFavourite = true;
                            optionsMenu.findItem(R.id.action_favourite).setIcon(R.drawable.outline_star_24);
                        } else {
                            isFavourite = false;
                            optionsMenu.findItem(R.id.action_favourite).setIcon(R.drawable.outline_star_border_24);
                        }

                    } else {
                        Log.d(TAG, "check favourite gym failed!");
                    }
                });

        System.out.println(menu);
        System.out.println(optionsMenu);

        //        if (mViewModel != null) {
        //            Log.d(TAG, "onCreate called first");
        //            updateFavouriteItemIcon(optionsMenu.f);
        //        }

        return b;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(TAG, "TEST ! onOptionsItemSelected: " + item.getItemId());
        switch (item.getItemId()) {
            case android.R.id.home:
                AlertDialog.Builder optDialog = new AlertDialog.Builder(mContext);
                optDialog.setTitle("Confirm");
                optDialog.setMessage("Are u sure to leave?");
                optDialog.setPositiveButton("Yes", (dialogInterface, i) -> finish());
                optDialog.setNegativeButton("No", (dialogInterface, i) -> {

                });
                optDialog.setIcon(android.R.drawable.stat_sys_warning)
                        .show();

                return true;
            default:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onAddToFavouriteClicked(MenuItem item) {

        if (isFavourite) {
            isFavourite = false;
            removeFavourite(mViewModel.getGym().getGymId());
        } else {
            isFavourite = true;
            addFavourite(mViewModel.getGym().getGymId());
        }
        updateFavouriteItemIcon(item);
    }

    private void updateFavouriteItemIcon(MenuItem item) {
        if (isFavourite) {
            item.setIcon(R.drawable.outline_star_24);
        } else {
            item.setIcon(R.drawable.outline_star_border_24);
        }
    }

    private void onDateSelectedListener(DatePickerDialog view,
                                        int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, monthOfYear, dayOfMonth);
        mViewModel.getTrainerListAdapter().setSelectedDate(calendar);
        mViewModel.getTrainerListAdapter().refresh();
    }

    private void onStartTimeSelected(TimePickerDialog view,
                                     int hourOfDay, int minute, int second) {
        Timepoint time = new Timepoint(hourOfDay, minute);
        mViewModel.getTrainerListAdapter().setBeginTime(time);
        mViewModel.getTrainerListAdapter().refresh();
    }

    private void onEndTimeSelected(TimePickerDialog view,
                                   int hourOfDay, int minute, int second) {
        Timepoint time = new Timepoint(hourOfDay, minute);
        mViewModel.getTrainerListAdapter().setEndTime(time);
        mViewModel.getTrainerListAdapter().refresh();
    }

    public void onDatePickerClicked(View view) {
        DatePickerDialog pickerDialog = DatePickerDialog.newInstance(
                this::onDateSelectedListener,
                mViewModel.getToday().get(Calendar.YEAR),
                mViewModel.getToday().get(Calendar.MONTH),
                mViewModel.getToday().get(Calendar.DAY_OF_MONTH)
        );
        pickerDialog.setMinDate(mViewModel.getToday());
        //        pickerDialog.setSelectableDays(mViewModel.getTrainerListAdapter().getSelectableDays());
        pickerDialog.setHighlightedDays(
                new Calendar[]{mViewModel.getSelectedDate()});
        pickerDialog.show(getSupportFragmentManager(), TAG);
    }

    public void onPickStartTimeClicked(View view) {
        TimePickerDialog pickerDialog = TimePickerDialog.newInstance(
                this::onStartTimeSelected,
                false
        );
        Calendar openTime = mViewModel.getGym().getOpenTime();
        Timepoint beginTp = new Timepoint(openTime.get(Calendar.HOUR_OF_DAY),
                openTime.get(Calendar.MINUTE));

        Calendar date = mViewModel.getSelectedDate();
        if (GymViewModel.isSameDate(date, mViewModel.getToday())) {
            // Cannot select a time before now
            Calendar now = Calendar.getInstance();
            if (now.compareTo(openTime) > 0) {
                beginTp = new Timepoint(now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE));
            }
        }

        Calendar closeTime = mViewModel.getGym().getCloseTime();
        Timepoint endTp = new Timepoint(closeTime.get(Calendar.HOUR_OF_DAY),
                closeTime.get(Calendar.MINUTE));
        pickerDialog.setMinTime(beginTp);
        pickerDialog.setMaxTime(endTp);
        pickerDialog.setTitle(getString(R.string.gym_start_time));
        pickerDialog.show(getSupportFragmentManager(), TAG);
    }

    public void onPickEndTimeCLicked(View view) {
        TimePickerDialog pickerDialog = TimePickerDialog.newInstance(
                this::onEndTimeSelected,
                false
        );

        Calendar closeTime = mViewModel.getGym().getCloseTime();
        Timepoint endTp = new Timepoint(closeTime.get(Calendar.HOUR_OF_DAY),
                closeTime.get(Calendar.MINUTE));
        pickerDialog.setMinTime(mViewModel.getBeginTime());
        pickerDialog.setMaxTime(endTp);
        pickerDialog.setTitle(getString(R.string.gym_start_time));
        pickerDialog.show(getSupportFragmentManager(), TAG);
    }

    /**
     * Click handler of "RESERVE" button in reservation page.
     */
    public void onConfirmReservationClicked(View view) {
        TrainerReservation trainerRsv = mViewModel.getReservation();
        UserData userdata = UserData.getInstance();
        ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Checking out...");
        progressDialog.show();
        UserData.getInstance().setCreditCards(new ArrayList<>());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cardsRef = db.collection("CARDS");
        cardsRef.whereEqualTo("UID", userdata.getUserId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Found " + task.getResult().getDocuments().size()+ " Cards in DB");
                    String last4digit = "";
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String number = document.getData().get(UserData.getInstance().KEY_CARD_number).toString();
                         last4digit = number.substring(number.length()-5,number.length()-1);
                        break;
                    }
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext)
                            .setTitle(R.string.gym_confirm_rsv)
                            .setMessage("You will be changed $"+ mViewModel.getTotalPrice()/100+" from card end with "+ last4digit + ", continue?")
                            .setPositiveButton(R.string.confirm, (dialog, which) ->
                            {
                                Reservation reservation = new Reservation(
                                        null,
                                        UserData.getInstance().getUserId(),
                                        mViewModel.getGym().getGymId(),
                                        trainerRsv == null ? null : trainerRsv.trainer.getTrainerId(),
                                        mViewModel.getTotalPrice(),
                                        mViewModel.getSelectedGymTimeslot());
                                mViewModel.makeReservation(GymActivity.this, reservation, trainerRsv);
                                finish();
                            })
                            .setNegativeButton(R.string.cancel, (dialog, which) ->
                            {
                                // Nothing happens
                            });
                    builder.create().show();
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Log.d(TAG, "Failed to getting Cards from remote DataBase", task.getException());
                }
            }
        } );
    }

    public void onPostReviewClicked(View view) {
        mViewModel.postReview();
    }

    public void addFavourite(String gymID) {
        UserData userData = UserData.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection(userData.KEY_USERS).document(userData.getUserId());
        ref.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        DocumentSnapshot doc = task.getResult();
                        ArrayList<String> favouriteList = (ArrayList) doc.get(userData.KEY_USER_favourite);

                        Log.d(TAG, "Found " + favouriteList.size() + " Favourite Gyms in DB");
                        if (!favouriteList.contains(gymID)) {
                            favouriteList.add(gymID);
                        }
                        ref.update(userData.KEY_USER_favourite, favouriteList).addOnSuccessListener(unused -> {
							Log.d(TAG, "add favourite gym successfully!" + gymID);
							userData.addToFavouriteGym(gymID);
							Toast.makeText(GymActivity.this, "Added to favourite!", Toast.LENGTH_SHORT).show();
						}).addOnFailureListener(e -> {
							Log.d(TAG, "add favourite gym failed!" + gymID);
							e.printStackTrace();
						});

                    } else {
                        Log.d(TAG, "add favourite gym failed!" + gymID);
                    }
                });
    }

    public void removeFavourite(String gymID) {
        UserData userData = UserData.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection(userData.KEY_USERS).document(userData.getUserId());
        ref.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        DocumentSnapshot doc = task.getResult();
                        ArrayList<String> favouriteList = (ArrayList) doc.get(userData.KEY_USER_favourite);

                        Log.d(TAG, "Found " + favouriteList.size() + " Favourite Gyms in DB");
                        if (favouriteList.contains(gymID)) {
                            favouriteList.remove(gymID);
                        }
                        ref.update(userData.KEY_USER_favourite, favouriteList).addOnSuccessListener(unused -> {
                            Log.d(TAG, "add favourite gym successfully!" + gymID);
                            userData.removeFromFavouriteGyms(gymID);
                            Toast.makeText(GymActivity.this, "Removed from favourite!", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Log.d(TAG, "add favourite gym failed!" + gymID);
                            e.printStackTrace();
                        });

                    } else {
                        Log.d(TAG, "add favourite gym failed!" + gymID);
                    }
                });
    }
}