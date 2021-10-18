package comp5216.sydney.edu.au.findmygym.ui.gym;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabPagerAdapter extends FragmentStateAdapter {

    private GymInfoFragment gymInfoFragment;
    private GymRsvFragment gymRsvFragment;

    public TabPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            if (gymInfoFragment == null) {
                gymInfoFragment = new GymInfoFragment();
            }
            return gymInfoFragment;
        } else {
            if (gymRsvFragment == null) {
                gymRsvFragment = new GymRsvFragment();
            }
            return gymRsvFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
