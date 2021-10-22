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

import java.util.Calendar;

import comp5216.sydney.edu.au.findmygym.model.Reservation;
import comp5216.sydney.edu.au.findmygym.ui.gym.GymFragment;
import comp5216.sydney.edu.au.findmygym.ui.gym.GymViewModel;

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
            item.setIcon(android.R.drawable.btn_star_big_off);
            isFavourite = false;
            Toast.makeText(this.getBaseContext(), "Removed from favourite!", Toast.LENGTH_SHORT).show();
        } else {
            item.setIcon(android.R.drawable.btn_star_big_on);
            isFavourite = true;
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

    /**
     * Click handler of "RESERVE" button in reservation page.
     */
    public void onConfirmReservationClicked(View view) {
        Reservation reservation = mViewModel.getReservation();
        if (reservation != null) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.gym_confirm_rsv)
                    .setMessage(getString(R.string.gym_confirm_rsv_msg,
                            reservation.getTrainer().getName(),
                            reservation.getSelectedTimeSlot().toString(this)))
                    .setPositiveButton(R.string.confirm, (dialog, which) -> {
                        makeReservation(reservation);
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                        // Nothing happens
                    });
            builder.create().show();
        } else {
            // This should not happen, because when nothing selected, the "reserve"
            // button should be disabled.
            Log.e(TAG, "Reservation is null");
            Toast.makeText(this, R.string.gym_rsv_null_error,
                    Toast.LENGTH_SHORT).show();
        }

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