package comp5216.sydney.edu.au.findmygym;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.Flow;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.AndroidException;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.ChipGroup;

import org.json.JSONObject;

import comp5216.sydney.edu.au.findmygym.model.Reservation;
import comp5216.sydney.edu.au.findmygym.ui.gym.GymFragment;
import comp5216.sydney.edu.au.findmygym.ui.gym.GymViewModel;

public class GymActivity extends AppCompatActivity {

    Context mContext;
    public boolean isFavourite;
    GymViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gym_activity);
        mContext = GymActivity.this;

        mViewModel = new ViewModelProvider(this).get(GymViewModel.class);

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

    public void onConfirmReservation(View view) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this)
                .setMessage(R.string.gym_confirm_rsv)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    Reservation reservation = mViewModel.getReservation();
                    System.out.println(reservation);
                    // TODO: confirm this reservation
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    // Nothing happens
                });

        builder.create().show();
    }
}