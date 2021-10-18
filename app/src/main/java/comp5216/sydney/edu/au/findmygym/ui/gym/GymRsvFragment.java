package comp5216.sydney.edu.au.findmygym.ui.gym;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import comp5216.sydney.edu.au.findmygym.R;

public class GymRsvFragment extends Fragment {

    TrainerListAdapter trainerListAdapter;
    private GymViewModel mViewModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(GymViewModel.class);

        return inflater.inflate(R.layout.gym_reservation_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.gym_trainer_recycler);
        trainerListAdapter = new TrainerListAdapter(mViewModel.getGym().getPersonalTrainers());
        recyclerView.setAdapter(trainerListAdapter);
    }
}
