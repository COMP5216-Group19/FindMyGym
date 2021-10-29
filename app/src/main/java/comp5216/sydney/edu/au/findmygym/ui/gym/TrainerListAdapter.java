package comp5216.sydney.edu.au.findmygym.ui.gym;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.Utils.ImageUtil;
import comp5216.sydney.edu.au.findmygym.model.PersonalTrainer;
import comp5216.sydney.edu.au.findmygym.model.Timeslot;

public class TrainerListAdapter extends RecyclerView.Adapter<TrainerListAdapter.TrainerViewHolder> {

    private static final String TAG = "TrainerListAdapter";

    private final List<PersonalTrainer> trainersList;
    private final GymViewModel mViewModel;

    RecyclerView recyclerView;
//    Button reserveButton;
    Button dateButton;
    Button beginTimeButton;
    Button endTimeButton;
    TextView noTrainerAvailText;
    TextView gymPriceText;
    TextView trainerPriceText;
    TextView totalPriceText;

//    private Set<Calendar> availableDays = new TreeSet<>();
    private List<PersonalTrainer> trainersOfSelectedDate;
    private TrainerReservation reservation;

    TrainerListAdapter(List<PersonalTrainer> trainersList,
                       RecyclerView recyclerView,
                       GymViewModel viewModel,
                       View parentView) {
        this.mViewModel = viewModel;
        this.trainersList = trainersList;
        this.recyclerView = recyclerView;
//        this.reserveButton = parentView.findViewById(R.id.gym_reserve_button);
        reload(parentView);

        setSelectedDate(mViewModel.getSelectedDate());
        setBeginTime(mViewModel.getBeginTime());
        setEndTime(mViewModel.getEndTime());

        updatePrices();
    }

    void reload(View parentView) {
        this.dateButton = parentView.findViewById(R.id.gym_date_picker_button);
        this.beginTimeButton = parentView.findViewById(R.id.gym_begin_time_button);
        this.endTimeButton = parentView.findViewById(R.id.gym_end_time_button);
        this.noTrainerAvailText = parentView.findViewById(R.id.gym_no_trainer_text);

        this.gymPriceText = parentView.findViewById(R.id.gym_self_price);
        this.trainerPriceText = parentView.findViewById(R.id.gym_trainer_price);
        this.totalPriceText = parentView.findViewById(R.id.gym_total_price);
    }

    public void setBeginTime(Timepoint begin) {
        mViewModel.beginTime = begin;

        beginTimeButton.setText(Timeslot.hourMinutesToString(recyclerView.getContext(),
                                begin.getHour(), begin.getMinute(), false));
    }

    public void setEndTime(Timepoint end) {
        mViewModel.endTime = end;

        endTimeButton.setText(Timeslot.hourMinutesToString(recyclerView.getContext(),
                end.getHour(), end.getMinute(), false));
    }

    public void setSelectedDate(Calendar selectedDate) {
        mViewModel.selectedDate = selectedDate;
        mViewModel.updateDefaultTimePeriod();
        filterTrainers();

        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT);
        String dateStr = dateFormat.format(selectedDate.getTime());
        String fullStr;
        if (GymViewModel.isSameDate(mViewModel.getToday(), selectedDate)) {
            fullStr = recyclerView.getContext().getString(R.string.gym_today, dateStr);
        } else if (GymViewModel.isNextDayOf(mViewModel.getToday(), selectedDate)) {
            fullStr = recyclerView.getContext().getString(R.string.gym_tomorrow, dateStr);
        } else {
            fullStr = dateStr;
        }

        dateButton.setText(fullStr);
    }

    private void filterTrainers() {
        trainersOfSelectedDate = new ArrayList<>();
        Calendar beginDateTime = mViewModel.getBeginDateTime();
        Calendar endDateTime = mViewModel.getEndDateTime();
        boolean found = false;
        for (PersonalTrainer trainer : trainersList) {
            for (Timeslot timeslot : trainer.getAvailableTimes()) {
                if (containedInSelectedPeriod(beginDateTime, endDateTime, timeslot)) {
                    trainersOfSelectedDate.add(trainer);
                    found = true;
                    break;
                }
            }
        }

        if (found) {
            noTrainerAvailText.setVisibility(View.GONE);
        } else {
            noTrainerAvailText.setVisibility(View.VISIBLE);
        }
    }

    private boolean containedInSelectedPeriod(Calendar beginDateTime,
                                              Calendar endDateTime,
                                              Timeslot timeslot) {
        return (beginDateTime.before(timeslot.getBeginTime()) ||
                beginDateTime.equals(timeslot.getBeginTime())) &&
                (endDateTime.after(timeslot.getEndTime()) ||
                        endDateTime.equals(timeslot.getEndTime()));
    }

    void updateSelection(TrainerReservation trainerReservation) {
        reservation = trainerReservation;
        Log.d(TAG, "Updated " + reservation);
        if (reservation == null) {
            mViewModel.trainerPrice = 0;
        } else {
            mViewModel.trainerPrice = trainerReservation.trainer.getPrice();
        }
        updatePrices();
    }

    private void updatePrices() {
        gymPriceText.setText(recyclerView.getContext()
                .getString(R.string.gym_money_fmt, (double) mViewModel.gymPrice / 100));
        trainerPriceText.setText(recyclerView.getContext()
                .getString(R.string.gym_money_fmt, (double) mViewModel.trainerPrice / 100));
        totalPriceText.setText(recyclerView.getContext()
                .getString(R.string.gym_money_fmt,
                        (double) mViewModel.getTotalPrice() / 100));
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
        holder.bind(trainer);
    }

    @Override
    public int getItemCount() {
        return trainersOfSelectedDate.size();
    }

    /**
     * @return the selected trainer-timeslot, or null if not selected
     */
    public TrainerReservation getSelection() {
        return reservation;
    }

    public void refresh() {
        notifyDataSetChanged();
        reservation = null;
    }

    public static class TrainerViewHolder extends RecyclerView.ViewHolder {
        private final Map<Integer, Timeslot> chipTimeslotMap = new TreeMap<>();
        TextView trainerNameText;
        ChipGroup trainerTimesGroup;
        ImageView trainerAvatarView;
        private PersonalTrainer trainer;
        private TrainerListAdapter adapter;

        public TrainerViewHolder(@NonNull View itemView, TrainerListAdapter adapter) {
            super(itemView);

            this.adapter = adapter;

            trainerNameText = itemView.findViewById(R.id.trainer_name);
            trainerTimesGroup = itemView.findViewById(R.id.trainer_times_group);
            trainerAvatarView = itemView.findViewById(R.id.trainer_avatar);

            trainerTimesGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId != View.NO_ID) {
                    adapter.updateSelection(new TrainerReservation(trainer,
                            chipTimeslotMap.get(checkedId)));
                    // A chip is selected in this group, clear all selections in other groups
                    for (int i = 0; i < adapter.getItemCount(); ++i) {
                        View view = adapter.recyclerView.getChildAt(i);
                        TrainerViewHolder trainerViewHolder =
                                (TrainerViewHolder) adapter.recyclerView.getChildViewHolder(view);
                        if (trainerViewHolder != this) {
                            trainerViewHolder.trainerTimesGroup.clearCheck();
                        }
                    }
                } else {
                    // Check all chip groups, if none is selected, clear trainer
                    for (int index = 0; index < adapter.getItemCount(); index++) {
                        TrainerViewHolder vh =
                                (TrainerViewHolder) adapter.recyclerView.getChildViewHolder(
                                        adapter.recyclerView.getChildAt(index));
                        if (vh.trainerTimesGroup.getCheckedChipId() != View.NO_ID) {
                            return;
                        }
                    }
                    adapter.updateSelection(null);
                }
            });
        }

        void bind(PersonalTrainer trainer) {
            this.trainer = trainer;

            trainerNameText.setText(trainer.getName());
            Context context = itemView.getContext();
            trainerTimesGroup.removeAllViews();
            chipTimeslotMap.clear();

            Calendar beginDateTime = adapter.mViewModel.getBeginDateTime();
            Calendar endDateTime = adapter.mViewModel.getEndDateTime();

            for (Timeslot timeSlot : trainer.getAvailableTimes()) {
                if (adapter.containedInSelectedPeriod(beginDateTime, endDateTime, timeSlot)) {
                    Chip chip = (Chip) LayoutInflater.from(context)
                            .inflate(R.layout.trainer_timeslot_chip, trainerTimesGroup, false);
                    chip.setText(timeSlot.toStringWithoutDate(context));
                    trainerTimesGroup.addView(chip);
                    chipTimeslotMap.put(chip.getId(), timeSlot);
                }
            }

            ImageUtil.loadImage(trainer.getTrainerId(), trainerAvatarView, context);
        }

        @NonNull
        @Override
        public String toString() {
            return "ViewHolder{" + trainerNameText.getText() + "}";
        }
    }
}
