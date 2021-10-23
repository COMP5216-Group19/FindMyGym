package comp5216.sydney.edu.au.findmygym;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import java.util.Calendar;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gym_activity);
        mContext = GymActivity.this;

        mViewModel = new ViewModelProvider(this).get(GymViewModel.class);
        // todo: set gym

        isFavourite = mViewModel.getGym().isFavourite();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.gym_container, GymFragment.newInstance())
                    .commitNow();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                DialogInterface.OnClickListener setListener = null;
                AlertDialog.Builder optDialog = new AlertDialog.Builder(mContext);
                optDialog.setTitle("Confirm");
                optDialog.setMessage("Are u sure to leave?");
                optDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                optDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
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
//            item.setIcon(android.R.drawable.btn_star_big_off);
            item.setIcon(R.drawable.outline_star_border_24);
            isFavourite = false;
            UserData.getInstance().removeFromFavouriteGyms(mViewModel.getGym().getGymId());
            Toast.makeText(this.getBaseContext(), "Removed from favourite!", Toast.LENGTH_SHORT).show();
        } else {
//            item.setIcon(android.R.drawable.btn_star_big_on);
            item.setIcon(R.drawable.outline_star_24);
            isFavourite = true;
            UserData.getInstance().addToFavouriteGyms(mViewModel.getGym().getGymId());
            Toast.makeText(this.getBaseContext(), "Added to favourite!", Toast.LENGTH_SHORT).show();
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

    private void onTimeSelectedListener(TimePickerDialog view,
                                        int hourOfDay, int minute, int second) {
        Timepoint timepoint = new Timepoint(hourOfDay, minute);
        mViewModel.getTrainerListAdapter().setSelectedTime(timepoint);
        mViewModel.getTrainerListAdapter().refresh();
    }

    public void onDatePickerClicked(View view) {
        DatePickerDialog pickerDialog = DatePickerDialog.newInstance(
                this::onDateSelectedListener,
                mViewModel.getToday().get(Calendar.YEAR),
                mViewModel.getToday().get(Calendar.MONTH),
                mViewModel.getToday().get(Calendar.DAY_OF_MONTH)
        );
        pickerDialog.setSelectableDays(mViewModel.getTrainerListAdapter().getSelectableDays());
        pickerDialog.setHighlightedDays(
                new Calendar[]{mViewModel.getTrainerListAdapter().getSelectedDate()});
        pickerDialog.show(getSupportFragmentManager(), TAG);
    }

    public void onTimePickerClicked(View view) {
        TimePickerDialog pickerDialog = TimePickerDialog.newInstance(
                this::onTimeSelectedListener,
                false
        );
        Calendar openTime = mViewModel.getGym().getOpenTime();
        Timepoint openTp = new Timepoint(openTime.get(Calendar.HOUR_OF_DAY),
                        openTime.get(Calendar.MINUTE));
        Calendar closeTime = mViewModel.getGym().getCloseTime();
        Timepoint closeTp = new Timepoint(closeTime.get(Calendar.HOUR_OF_DAY),
                closeTime.get(Calendar.MINUTE));
        pickerDialog.setMinTime(openTp);
        pickerDialog.setMaxTime(closeTp);
        System.out.println(openTp);
        System.out.println(closeTp);
        pickerDialog.show(getSupportFragmentManager(), TAG);
    }

    /**
     * Click handler of "RESERVE" button in reservation page.
     */
    public void onConfirmReservationClicked(View view) {
        TrainerReservation trainerRsv = mViewModel.getReservation();

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.gym_confirm_rsv)
                .setMessage(R.string.gym_confirm_rsv)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    Reservation reservation = new Reservation(
                            UserData.getInstance().getUserId(),
                            mViewModel.getGym().getGymId(),
                            trainerRsv == null ? null : trainerRsv.trainer.getTrainerId(),
                            mViewModel.getSelectedGymTimeslot());
                    makeReservation(reservation);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    // Nothing happens
                });
        builder.create().show();


    }

    /**
     * Make a reservation.
     *
     * @param reservation the reservation to be made
     */
    public void makeReservation(Reservation reservation) {
        // TODO: 预约
    }
}