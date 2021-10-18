package comp5216.sydney.edu.au.findmygym.ui.gym;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.ChipGroup;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.databinding.GymFragmentBinding;

public class GymInfoFragment extends Fragment {

    TextView gymNameSmall;
    TextView gymOpenHrs;
    TextView gymAvgRating;
    TextView gymAddress;
    TextView gymContact;
    ChipGroup equipmentsContainer;

    private GymViewModel mViewModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(requireActivity()).get(GymViewModel.class);

        return inflater.inflate(R.layout.gym_info_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gymNameSmall = view.findViewById(R.id.gym_name);
        gymOpenHrs = view.findViewById(R.id.gym_open_hrs);
        gymAvgRating = view.findViewById(R.id.gym_avg_rate);
        gymAddress = view.findViewById(R.id.gym_address);
        gymContact = view.findViewById(R.id.gym_contact);
        equipmentsContainer = view.findViewById(R.id.gym_equipments_group);

        ImageView imageView = view.findViewById(R.id.gym_image_view);
        imageView.setImageResource(R.drawable.fitness_gym_example_1484x983);
    }
}
