package comp5216.sydney.edu.au.findmygym.ui.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.model.UserData;

public class FavGymAdapter extends RecyclerView.Adapter<FavGymAdapter.ViewHolder> {
    private final UserData userData = UserData.getInstance();

    public FavGymAdapter() {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView favGymItem;
        private final TextView favGymAddress;

        public ViewHolder(View view) {
            super(view);
            favGymItem = view.findViewById(R.id.favGymItem);
            favGymAddress = view.findViewById(R.id.favGymAddress);
        }

        public TextView getFavGymItem() {
            return favGymItem;
        }

        public TextView getFavGymAddress() {
            return favGymAddress;
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
        viewHolder.getFavGymItem().setText(findGymById(userData.getFavouriteGyms(), position));
        viewHolder.getFavGymAddress().setText(findGymAddressById(userData.getFavouriteGyms(), position));
    }

    public String findGymById(List<String> favGymList, int position) {
        String id = favGymList.get(position);
        String name = "";

        if (userData.getAllGyms() == null) {
            return name;
        }

        for (int i = 0; i < userData.getAllGyms().size(); i++) {
            if (userData.getAllGyms().get(i).getGymId().equals(id)) {
                name = userData.getAllGyms().get(i).getGymName();
            }
        }
        return name;
    }

    public String findGymAddressById(List<String> favGymList, int position) {
        String id = favGymList.get(position);
        String address = "";

        if (userData.getAllGyms() == null) {
            return address;
        }

        for (int i = 0; i < userData.getAllGyms().size(); i++) {
            if (userData.getAllGyms().get(i).getGymId().equals(id)) {
                address = userData.getAllGyms().get(i).getAddress();
            }
        }
        return address;
    }

    @Override
    public int getItemCount() {
        return userData.getFavouriteGyms().size();
    }
}
