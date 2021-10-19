package comp5216.sydney.edu.au.findmygym.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import comp5216.sydney.edu.au.findmygym.databinding.FragmentProfileBinding;
import comp5216.sydney.edu.au.findmygym.model.UserData;

public class ProfileFragment extends Fragment {
    private final String TAG = "[ProfileFragment]";

    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;

    private UserData userData;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        userData = UserData.getInstance();
        userData.setContext(this.getContext());

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

       binding.avatarImage.setImageBitmap(userData.getUserAvatar());
       binding.nameText.setText(userData.getUserName());
       binding.emailText.setText(userData.getUserMail());

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}