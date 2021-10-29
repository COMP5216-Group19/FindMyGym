package comp5216.sydney.edu.au.findmygym.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import comp5216.sydney.edu.au.findmygym.GymActivity;
import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.model.Gym;
import comp5216.sydney.edu.au.findmygym.model.UserData;
import comp5216.sydney.edu.au.findmygym.model.callbacks.ObjectQueryCallback;

public class FavGymAdapter extends RecyclerView.Adapter<FavGymAdapter.ViewHolder> {
    private final UserData userData = UserData.getInstance();
    private Context context;
    public FavGymAdapter(Context context) {
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView favGymItem;
        private final TextView favGymAddress;
        public ConstraintLayout constraintLayout;
        public ViewHolder(View view) {
            super(view);
            constraintLayout = view.findViewById(R.id.profile_favorite_item);
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
    public void onBindViewHolder(@NonNull FavGymAdapter.ViewHolder viewHolder, int position) { ;
        String gymId = userData.getFavouriteGyms().get(position);
        //Toast.makeText(context,gymId,Toast.LENGTH_SHORT).show();
        final Gym[] gym = {null};
        UserData.getInstance().getGymByID(gymId, new ObjectQueryCallback<Gym>() {
            @Override
            public void onSucceed(Gym object) {
                if (object == null)
                    return;
                gym[0] = object;
                viewHolder.getFavGymItem().setText(gym[0].getGymName());
                viewHolder.getFavGymAddress().setText(gym[0].getAddress());
            }
        
            @Override
            public void onFailed(Exception e) {
                Log.d("[PROFILE]", "failed to intent gym", e);
            }
        });
        
        viewHolder.constraintLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, GymActivity.class);
                intent.putExtra("gym", gym[0]);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userData.getFavouriteGyms().size();
    }
}
