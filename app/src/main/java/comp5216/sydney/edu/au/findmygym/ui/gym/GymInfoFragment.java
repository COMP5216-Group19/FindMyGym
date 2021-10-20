package comp5216.sydney.edu.au.findmygym.ui.gym;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.databinding.GymFragmentBinding;
import comp5216.sydney.edu.au.findmygym.model.Gym;

public class GymInfoFragment extends Fragment {

    private static final int[] EQUIPMENTS_COLORS = {
            0x0077ff, 0xff7777, 0x33ccff, 0xeeaa33
    };

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

        TextView gymNameSmall = view.findViewById(R.id.gym_name);
        TextView gymOpenHrs = view.findViewById(R.id.gym_open_hrs);
        TextView gymAvgRating = view.findViewById(R.id.gym_avg_rate);
        TextView gymAddress = view.findViewById(R.id.gym_address);
        TextView gymContact = view.findViewById(R.id.gym_contact);
        ChipGroup equipmentsContainer = view.findViewById(R.id.gym_equipments_group);
        ImageView gymImageView = view.findViewById(R.id.gym_image_view);

        Gym gym = mViewModel.getGym();

        gymNameSmall.setText(gym.getGymName());
        gymOpenHrs.setText(
                getString(R.string.gym_time_format, gym.getOpenTime(), gym.getCloseTime()));
        gymAvgRating.setText(getString(R.string.gym_rate_format, gym.getAvgRating()));
        gymAddress.setText(gym.getAddress());
        gymContact.setText(gym.getContact());

        Bitmap gymImage = gym.getGymPhoto();
        if (gymImage == null) {
            gymImageView.setImageResource(R.drawable.fitness_gym_example_1484x983);
        } else {
            gymImageView.setImageBitmap(gymImage);
        }

        int[] chipColorIds = new int[]{
                R.color.light_orange,
                R.color.teal_700,
                R.color.pink_red,
                R.color.light_green
        };
        equipmentsContainer.removeAllViews();

        for (String equip : gym.getEquipments()) {
            Chip chip = new Chip(view.getContext());
            int colorId = chipColorIds[(int) (Math.random() * chipColorIds.length)];
            chip.setChipBackgroundColor(
                    ColorStateList.valueOf(ContextCompat.getColor(view.getContext(), colorId)));
            chip.setTextColor(Color.WHITE);
            chip.setText(equip);

            equipmentsContainer.addView(chip);
        }
    }
}
