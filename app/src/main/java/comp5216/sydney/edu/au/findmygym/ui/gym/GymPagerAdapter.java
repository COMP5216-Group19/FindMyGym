package comp5216.sydney.edu.au.findmygym.ui.gym;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class GymPagerAdapter extends FragmentStateAdapter {

    public static final int INFO_PAGE_POSITION = 0;
    public static final int RESERVATION_PAGE_POSITION = 1;

    private GymInfoFragment gymInfoFragment;
    private GymRsvFragment gymRsvFragment;

    public GymPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == INFO_PAGE_POSITION) {
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
