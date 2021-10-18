package comp5216.sydney.edu.au.findmygym.ui.gym;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Calendar;
import java.util.List;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.model.PersonalTrainerInfo;
import comp5216.sydney.edu.au.findmygym.model.TimeSlot;

public class TrainerListAdapter extends RecyclerView.Adapter<TrainerListAdapter.ViewHolder> {

    private final List<PersonalTrainerInfo> trainersList;

    TrainerListAdapter(List<PersonalTrainerInfo> trainersList) {
        this.trainersList = trainersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trainer_view_holder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PersonalTrainerInfo trainer = trainersList.get(position);
        holder.trainerNameText.setText(trainer.getName());
        Context context = holder.itemView.getContext();
        for (TimeSlot timeSlot : trainer.getAvailableTimes()) {
            Chip chip = new Chip(context);
            chip.setText(
                    context.getString(R.string.gym_time_format,
                            minutesToString(context, timeSlot.getBeginTime()),
                            minutesToString(context, timeSlot.getEndTime()))
            );
            // Todo: only one chip can be selected
            holder.trainerTimesGroup.addView(chip);
        }
    }

    @Override
    public int getItemCount() {
        return trainersList.size();
    }

    public static String minutesToString(Context context, Calendar time) {
        int am_pm = time.get(Calendar.AM_PM);
        int hour = time.get(Calendar.HOUR);
        int minutes = time.get(Calendar.MINUTE);
        int fmtStrId = am_pm == Calendar.AM ?
                R.string.gym_time_format_am : R.string.gym_time_format_pm;
        return context.getString(fmtStrId, hour, minutes);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView trainerNameText;
        ChipGroup trainerTimesGroup;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            trainerNameText = itemView.findViewById(R.id.trainer_name);
            trainerTimesGroup = itemView.findViewById(R.id.trainer_times_group);
        }
    }
}
