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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.model.PersonalTrainer;
import comp5216.sydney.edu.au.findmygym.model.Reservation;
import comp5216.sydney.edu.au.findmygym.model.Timeslot;

public class TrainerListAdapter extends RecyclerView.Adapter<TrainerListAdapter.TrainerViewHolder> {

    private final List<PersonalTrainer> trainersList;
    private final GymViewModel mViewModel;
    /**
     * Date selected of reservation maker
     */
    Calendar selectedDate;
    RecyclerView recyclerView;
    Button reserveButton;
    TextView dateText;

    private Set<Calendar> availableDays = new TreeSet<>();
    private List<PersonalTrainer> trainersOfSelectedDate;
    private Reservation reservation;

    TrainerListAdapter(List<PersonalTrainer> trainersList,
                       RecyclerView recyclerView,
                       GymViewModel viewModel,
                       Button reserveButton,
                       TextView dateText) {
        this.mViewModel = viewModel;
        this.trainersList = trainersList;
        this.recyclerView = recyclerView;
        this.reserveButton = reserveButton;
        this.dateText = dateText;
        makeAvailableDays(trainersList);

        setSelectedDate(mViewModel.getToday());
    }

    private void makeAvailableDays(List<PersonalTrainer> personalTrainers) {
        for (PersonalTrainer trainer : personalTrainers) {
            for (Timeslot timeslot : trainer.getAvailableTimes()) {
                Calendar beginTime = timeslot.getBeginTime();
                Calendar thatDay = GymViewModel.beginOfADay(beginTime);

                availableDays.add(thatDay);
            }
        }
    }

    public void setSelectedDate(Calendar selectedDate) {
        this.selectedDate = selectedDate;

        trainersOfSelectedDate = new ArrayList<>();
        for (PersonalTrainer trainer : trainersList) {
            for (Timeslot timeslot : trainer.getAvailableTimes()) {
                if (GymViewModel.isSameDate(timeslot.getBeginTime(), selectedDate)) {
                    trainersOfSelectedDate.add(trainer);
                    break;
                }
            }
        }

        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT);
        String dateStr = dateFormat.format(selectedDate.getTime());
        String fullStr;
        if (GymViewModel.isSameDate(selectedDate, mViewModel.getToday())) {
            fullStr = recyclerView.getContext().getString(R.string.gym_today, dateStr);
        } else if (GymViewModel.isNextDayOf(mViewModel.getToday(), selectedDate)) {
            fullStr = recyclerView.getContext().getString(R.string.gym_tomorrow, dateStr);
        } else {
            fullStr = dateStr;
        }

        dateText.setText(fullStr);
    }

    public Calendar getSelectedDate() {
        return selectedDate;
    }

    public Calendar[] getSelectableDays() {
        return availableDays.toArray(new Calendar[0]);
    }

    @NonNull
    @Override
    public TrainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trainer_item_holder, parent, false);
        return new TrainerViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainerViewHolder holder, int position) {
        PersonalTrainer trainer = trainersOfSelectedDate.get(position);
        holder.bind(trainer, selectedDate);
    }

    @Override
    public int getItemCount() {
        return trainersOfSelectedDate.size();
    }

    /**
     * @return the selected trainer-timeslot, or null if not selected
     */
    public Reservation getSelection() {
        return reservation;
//        for (int i = 0; i < getItemCount(); ++i) {
//            View view = recyclerView.getChildAt(i);
//            TrainerViewHolder trainerViewHolder =
//                    (TrainerViewHolder) recyclerView.getChildViewHolder(view);
//            int checkedId = trainerViewHolder.trainerTimesGroup.getCheckedChipId();
//            if (checkedId != View.NO_ID) {
//                return new Reservation(trainerViewHolder.trainer,
//                        trainerViewHolder.chipTimeslotMap.get(checkedId));
//            }
//        }
//        return null;
    }

    public void refresh() {
        notifyDataSetChanged();
        reserveButton.setEnabled(false);
        reservation = null;
    }

    public static class TrainerViewHolder extends RecyclerView.ViewHolder {
        private final Map<Integer, Timeslot> chipTimeslotMap = new TreeMap<>();
        TextView trainerNameText;
        ChipGroup trainerTimesGroup;
        private PersonalTrainer trainer;
        private TrainerListAdapter adapter;

        public TrainerViewHolder(@NonNull View itemView, TrainerListAdapter adapter) {
            super(itemView);

            this.adapter = adapter;

            trainerNameText = itemView.findViewById(R.id.trainer_name);
            trainerTimesGroup = itemView.findViewById(R.id.trainer_times_group);

            trainerTimesGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId != View.NO_ID) {
                    adapter.reservation = new Reservation(trainer,
                            chipTimeslotMap.get(checkedId));
                    // A chip is selected in this group, clear all selections in other groups
                    for (int i = 0; i < adapter.getItemCount(); ++i) {
                        View view = adapter.recyclerView.getChildAt(i);
                        TrainerViewHolder trainerViewHolder =
                                (TrainerViewHolder) adapter.recyclerView.getChildViewHolder(view);
                        if (trainerViewHolder != this) {
                            trainerViewHolder.trainerTimesGroup.clearCheck();
                        }
                    }
                    adapter.reserveButton.setEnabled(true);
                } else {
                    adapter.reserveButton.setEnabled(false);
                }
            });
        }

        void bind(PersonalTrainer trainer, Calendar date) {
            this.trainer = trainer;

            trainerNameText.setText(trainer.getName());
            Context context = itemView.getContext();
            trainerTimesGroup.removeAllViews();
            chipTimeslotMap.clear();

//            int checkId = View.NO_ID;
            for (Timeslot timeSlot : trainer.getAvailableTimes()) {
                if (GymViewModel.isSameDate(timeSlot.getBeginTime(), date)) {
                    Chip chip = (Chip) LayoutInflater.from(context)
                            .inflate(R.layout.trainer_timeslot_chip, trainerTimesGroup, false);
                    chip.setText(timeSlot.toStringWithoutDate(context));
                    trainerTimesGroup.addView(chip);
                    chipTimeslotMap.put(chip.getId(), timeSlot);

//                    if (adapter.reservation != null &&
//                            adapter.reservation.getTrainer() == trainer &&
//                            adapter.reservation.getSelectedTimeSlot() == timeSlot) {
//                        checkId = chip.getId();
//                    }
                }
            }

//            if (checkId != View.NO_ID) {
//                trainerTimesGroup.check(checkId);
//            }
        }

        @NonNull
        @Override
        public String toString() {
            return "ViewHolder{" + trainerNameText.getText() + "}";
        }
    }
}
