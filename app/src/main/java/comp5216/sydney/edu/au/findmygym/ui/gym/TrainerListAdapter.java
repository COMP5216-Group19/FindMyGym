package comp5216.sydney.edu.au.findmygym.ui.gym;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;

import java.util.List;

import comp5216.sydney.edu.au.findmygym.R;

public class TrainerListAdapter extends RecyclerView.Adapter<TrainerListAdapter.ViewHolder> {

    private List<PersonalTrainer> trainersList;

    TrainerListAdapter(List<PersonalTrainer> trainersList) {
        this.trainersList = trainersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
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
