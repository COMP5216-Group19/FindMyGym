package comp5216.sydney.edu.au.findmygym.ui.gym;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabPagerAdapter extends FragmentStateAdapter {

    public static final int INFO_PAGE_POSITION = 0;
    public static final int RESERVATION_PAGE_POSITION = 1;
    public static final int REVIEW_PAGE_POSITION = 2;

    private GymInfoFragment gymInfoFragment;
    private GymRsvFragment gymRsvFragment;
    private GymReviewsFragment gymReviewsFragment;

    public TabPagerAdapter(@NonNull Fragment fragment) {
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
        } else if (position == RESERVATION_PAGE_POSITION) {
            if (gymRsvFragment == null) {
                gymRsvFragment = new GymRsvFragment();
            }
            return gymRsvFragment;
        } else {
            if (gymReviewsFragment == null) {
                gymReviewsFragment = new GymReviewsFragment();
            }
            return gymReviewsFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
