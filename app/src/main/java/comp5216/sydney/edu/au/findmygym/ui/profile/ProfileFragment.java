package comp5216.sydney.edu.au.findmygym.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import comp5216.sydney.edu.au.findmygym.databinding.FragmentProfileBinding;
import comp5216.sydney.edu.au.findmygym.model.UserData;

public class ProfileFragment extends Fragment {
    private final String TAG = "[ProfileFragment]";

    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;
    private RecyclerView profileRecyclerView;

    private UserData userData;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



//		final TextView textView = binding.textProfile;
//		profileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>()
//		{
//			@Override
//			public void onChanged(@Nullable String s)
//			{
//				textView.setText(s);
//			}
//		});
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}