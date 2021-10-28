package comp5216.sydney.edu.au.findmygym.ui.schedule.tabs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.model.PurchaseRecord;
import comp5216.sydney.edu.au.findmygym.model.ScheduleList;
import comp5216.sydney.edu.au.findmygym.model.UserData;
import pl.droidsonroids.gif.GifImageView;

class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>
{
    private final String TAG = "[BookingAdapter]";

    private UserData userData;
    Context mContext;
    ArrayList<ScheduleList> scheduleList ;

    public interface OnItemLongClickListener {
        public boolean onItemLongClicked(int position);
    }

    public HistoryAdapter(Context mContext, ArrayList<ScheduleList> historyArrayList)
    {
        userData = UserData.getInstance();
        this.mContext = mContext;
        this.scheduleList = userData.getScheduleLists();

    }


    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_schedule_item, parent, false);
        return new HistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position)
    {

        holder.giv_image.setImageBitmap(scheduleList.get(position).getImage());
        holder.tv_title.setText(scheduleList.get(position).getTitle());
        holder.tv_trainer.setText(scheduleList.get(position).getTrainer());
        holder.tv_time.setText(scheduleList.get(position).getTimeStr());



    }

    @Override
    public int getItemCount()
    {
        return scheduleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        GifImageView giv_image;
        TextView tv_title;
        TextView tv_trainer;
        TextView tv_time;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            cardView = itemView.findViewById(R.id.item_schedule_cardView);
            giv_image = itemView.findViewById(R.id.item_schedule_GifImageView_image);
            tv_title = itemView.findViewById(R.id.item_schedule_textview_gymName);
            tv_trainer = itemView.findViewById(R.id.item_schedule_textview_trainer);
            tv_time = itemView.findViewById(R.id.item_schedule_textview_time);
        }
    }
}
