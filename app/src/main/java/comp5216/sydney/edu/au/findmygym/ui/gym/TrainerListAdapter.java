package comp5216.sydney.edu.au.findmygym.ui.gym;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.model.PersonalTrainer;
import comp5216.sydney.edu.au.findmygym.model.Reservation;
import comp5216.sydney.edu.au.findmygym.model.Timeslot;

public class TrainerListAdapter extends RecyclerView.Adapter<TrainerListAdapter.ViewHolder> {

    private final List<PersonalTrainer> trainersList;
    private final RecyclerView recyclerView;
    private Button confirmButton;

    TrainerListAdapter(List<PersonalTrainer> trainersList,
                       RecyclerView recyclerView,
                       Button confirmButton) {
        this.trainersList = trainersList;
        this.recyclerView = recyclerView;
        this.confirmButton = confirmButton;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trainer_view_holder, parent, false);
        return new ViewHolder(view, this);
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

    /**
     * @return the selected trainer-timeslot, or null if not selected
     */
    public Reservation getSelection() {
        for (int i = 0; i < getItemCount(); ++i) {
            View view = recyclerView.getChildAt(i);
            ViewHolder viewHolder =
                    (ViewHolder) recyclerView.getChildViewHolder(view);
            int checkedId = viewHolder.trainerTimesGroup.getCheckedChipId();
            if (checkedId != View.NO_ID) {
                return new Reservation(viewHolder.trainer,
                        viewHolder.chipTimeslotMap.get(checkedId));
            }
        }
        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final Map<Integer, Timeslot> chipTimeslotMap = new TreeMap<>();
        TextView trainerNameText;
        ChipGroup trainerTimesGroup;
        private PersonalTrainer trainer;

        public ViewHolder(@NonNull View itemView, TrainerListAdapter adapter) {
            super(itemView);

            trainerNameText = itemView.findViewById(R.id.trainer_name);
            trainerTimesGroup = itemView.findViewById(R.id.trainer_times_group);

            trainerTimesGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId != View.NO_ID) {
                    // A chip is selected in this group, clear all selections in other groups
                    for (int i = 0; i < adapter.getItemCount(); ++i) {
                        View view = adapter.recyclerView.getChildAt(i);
                        ViewHolder viewHolder =
                                (ViewHolder) adapter.recyclerView.getChildViewHolder(view);
                        if (viewHolder != this) {
                            viewHolder.trainerTimesGroup.clearCheck();
                        }
                    }
                    adapter.confirmButton.setEnabled(true);
                } else {
                    adapter.confirmButton.setEnabled(false);
                }
            });
        }

        void bind(PersonalTrainer trainer) {
            this.trainer = trainer;

            trainerNameText.setText(trainer.getName());
            Context context = itemView.getContext();
            trainerTimesGroup.removeAllViews();
            chipTimeslotMap.clear();
            for (Timeslot timeSlot : trainer.getAvailableTimes()) {
                Chip chip = (Chip) LayoutInflater.from(context)
                        .inflate(R.layout.trainer_timeslot_chip, null, false);
                chip.setText(timeSlot.toString(context));
                trainerTimesGroup.addView(chip);
                chipTimeslotMap.put(chip.getId(), timeSlot);
            }
        }

        @NonNull
        @Override
        public String toString() {
            return "ViewHolder{" + trainerNameText.getText() + "}";
        }
    }
}
