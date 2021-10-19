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
import comp5216.sydney.edu.au.findmygym.model.PersonalTrainer;
import comp5216.sydney.edu.au.findmygym.model.TimeSlot;

public class TrainerListAdapter extends RecyclerView.Adapter<TrainerListAdapter.ViewHolder> {

    private final List<PersonalTrainer> trainersList;

    TrainerListAdapter(List<PersonalTrainer> trainersList) {
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
        PersonalTrainer trainer = trainersList.get(position);
        holder.bind(trainer);
    }

    @Override
    public int getItemCount() {
        return trainersList.size();
    }

    public static String calendarToTimeInDay(Context context, Calendar time) {
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
//        View divider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            trainerNameText = itemView.findViewById(R.id.trainer_name);
            trainerTimesGroup = itemView.findViewById(R.id.trainer_times_group);
        }

        void bind(PersonalTrainer trainer) {
            trainerNameText.setText(trainer.getName());
            Context context = itemView.getContext();
            trainerTimesGroup.removeAllViews();
            for (TimeSlot timeSlot : trainer.getAvailableTimes()) {
                Chip chip = (Chip) LayoutInflater.from(context)
                        .inflate(R.layout.trainer_timeslot_chip, null, false);
                chip.setText(
                        context.getString(R.string.gym_time_format,
                                calendarToTimeInDay(context, timeSlot.getBeginTime()),
                                calendarToTimeInDay(context, timeSlot.getEndTime()))
                );
                trainerTimesGroup.addView(chip);
            }
//            trainerTimesGroup.setOnCheckedChangeListener();
        }
    }
}
