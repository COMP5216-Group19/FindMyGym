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
import comp5216.sydney.edu.au.findmygym.model.UserData;
import pl.droidsonroids.gif.GifImageView;

class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder>
{
    private final String TAG = "[BookingAdapter]";

    private UserData userData;
    Context mContext;
    ArrayList<PurchaseRecord> historyArrayList ;

    public interface OnItemLongClickListener {
        public boolean onItemLongClicked(int position);
    }

    public BookingAdapter(Context mContext, ArrayList<PurchaseRecord> historyArrayList)
    {
        userData = UserData.getInstance();
        this.mContext = mContext;
        this.historyArrayList = userData.getPurchaseRecords();

    }


    @NonNull
    @Override
    public BookingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_wallet_history_item, parent, false);
        return new BookingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position)
    {

        holder.giv_image.setImageBitmap(historyArrayList.get(position).getImage());
        holder.tv_title.setText(historyArrayList.get(position).getTitle());
        holder.tv_description.setText(historyArrayList.get(position).getTimeStr());
        holder.tv_cost.setText(historyArrayList.get(position).getCostStr());
        holder.cardView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(mContext, "Clicked on" + historyArrayList.get(position).getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                Toast.makeText(mContext, "LongClicked on" + historyArrayList.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                userData.removePurchaseRecord(position);
                return false;
            }
        });


    }

    @Override
    public int getItemCount()
    {
        return historyArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        GifImageView giv_image;
        TextView tv_title;
        TextView tv_description;
        TextView tv_cost;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            cardView = itemView.findViewById(R.id.item_history_cardView);
            giv_image = itemView.findViewById(R.id.item_history_GifImageView_image);
            tv_title = itemView.findViewById(R.id.item_history_textview_title);
            tv_description = itemView.findViewById(R.id.item_history_textview_description);
            tv_cost = itemView.findViewById(R.id.item_history_textview_cost);
        }
    }
}
