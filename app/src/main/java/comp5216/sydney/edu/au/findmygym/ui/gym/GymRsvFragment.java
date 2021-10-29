package comp5216.sydney.edu.au.findmygym.ui.gym;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import comp5216.sydney.edu.au.findmygym.R;

public class GymRsvFragment extends Fragment {

    ProgressBar progressBar;
    private GymViewModel mViewModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(GymViewModel.class);
        mViewModel.rsvFragment = this;

        View view = inflater.inflate(R.layout.gym_reservation_fragment, container, false);

        progressBar = view.findViewById(R.id.gym_rsv_progress_bar);
        RecyclerView recyclerView = view.findViewById(R.id.gym_trainer_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        if (mViewModel.trainerListAdapter == null) {
            mViewModel.trainerListAdapter =
                    new TrainerListAdapter(mViewModel.getGym().getPersonalTrainers(),
                            recyclerView,
                            mViewModel,
                            view);
        } else {
            mViewModel.trainerListAdapter.recyclerView = recyclerView;
            mViewModel.trainerListAdapter.reload(view);
        }
        recyclerView.setAdapter(mViewModel.trainerListAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
