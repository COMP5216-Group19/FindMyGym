package comp5216.sydney.edu.au.findmygym.ui.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.model.Gym;
import comp5216.sydney.edu.au.findmygym.model.PersonalTrainer;
import comp5216.sydney.edu.au.findmygym.model.Timeslot;
import comp5216.sydney.edu.au.findmygym.ui.gym.TrainerListAdapter;

public class FavGymAdapter extends RecyclerView.Adapter<FavGymAdapter.ViewHolder> {
    private final ArrayList<Integer> favGymList;
    private final List<Gym> gymList;

    public FavGymAdapter(ArrayList<Integer> favGymList, List<Gym> gymList) {
        this.favGymList = favGymList;
        this.gymList = gymList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textView = (TextView) view.findViewById(R.id.favGymItem);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    @NonNull
    @Override
    public FavGymAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.profile_favgym_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavGymAdapter.ViewHolder viewHolder, int position) {
        viewHolder.getTextView().setText(findGymById(favGymList, position));
    }

    public String findGymById(ArrayList<Integer> favGymList, int position) {
        int id = favGymList.get(position);
        String name = "";

        for (int i = 0; i < gymList.size(); i++) {
            if (gymList.get(i).getGymId() == id) {
                name = gymList.get(i).getGymName();
            }
        }
        return name;
    }

    @Override
    public int getItemCount() {
        return favGymList.size();
    }
}
