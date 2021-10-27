package comp5216.sydney.edu.au.findmygym.ui.wallet.tabs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vinaygaba.creditcardview.CreditCardView;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import comp5216.sydney.edu.au.findmygym.AddCardDialog;
import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.model.CreditCard;
import comp5216.sydney.edu.au.findmygym.model.PurchaseRecord;
import comp5216.sydney.edu.au.findmygym.model.UserData;

public class Wallet_Card extends Fragment
{
	private final String TAG = "[Wallet_Card]";
	Context context;
	CreditCardView creditCardView;
	DiscreteScrollView discreteScrollView;
	CardAdapter cardAdapter;
	UserData userData;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		Log.e(TAG, "onCreateView: TEST=================");
		return inflater.inflate(R.layout.fragment_wallet_card, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		
		super.onViewCreated(view, savedInstanceState);
		userData = UserData.getInstance();
		this.context = getContext();
		Bitmap bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.diana);
		Bitmap bitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.ybb);
		Bitmap bitmap3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.azi);
		Bitmap bitmap4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.onion);
		Bitmap bitmap5 = BitmapFactory.decodeResource(context.getResources(), R.drawable.mea);
		List<Bitmap> bitmapList = Arrays.asList(bitmap1, bitmap2, bitmap3, bitmap4, bitmap5);
		
		ArrayList<CreditCard> cards = new ArrayList<>();
		// cards.add(new CreditCard("5555555555554444", "master","0000"));
		
		for (int i = 0; i < 2; i++)
		{
			String str = "";
			for (int j = 0; j < 16; j++)
			{
				Random r = new Random();
				int result = r.nextInt(9 - 0) + 0;
				str += String.valueOf(result);
			}
			cards.add(new CreditCard(str, "Card" + i, "0000",String.valueOf(i+1)));
		}
		
		importCardsFromDB();

		// userData.setCreditCards(cards);
		discreteScrollView = getView().findViewById(R.id.wallet_card_discreteScrollView);
		// discreteScrollView.setLayoutManager(new LinearLayoutManager(context));
		CardAdapter cardAdapter = new CardAdapter(context);
		// scrollView.setAdapter(membershipAdapter);
		InfiniteScrollAdapter infiniteScrollAdapter = InfiniteScrollAdapter.wrap(cardAdapter);
		discreteScrollView.setAdapter(infiniteScrollAdapter);
		discreteScrollView.setOverScrollEnabled(true);
		discreteScrollView.setItemTransformer(new ScaleTransformer.Builder()
				.setMaxScale(1.05f)
				.setMinScale(0.4f)
				.setPivotX(Pivot.X.LEFT) // CENTER is a default one
				.setPivotY(Pivot.Y.BOTTOM) // CENTER is a default one
				.build());
		
		TextView gym = getView().findViewById(R.id.wallet_card_textview_title);
		
		discreteScrollView.addOnItemChangedListener(new DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>()
		{
			@Override
			public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition)
			{
				
				int position = infiniteScrollAdapter.getRealPosition(adapterPosition) + 1;
				int size = infiniteScrollAdapter.getRealItemCount();
				gym.setText("Card(" + position + "/" + size + ")");
			}
		});
		
		TextView backgroundText = getView().findViewById(R.id.card_textview_title);
		userData.observe(getViewLifecycleOwner(), new Observer<UserData>()
		{
			@Override
			public void onChanged(UserData userData)
			{
				// historyAdapter.setHistoryArrayList(userData.getPurchaseRecords());
				infiniteScrollAdapter.notifyDataSetChanged();
				int size = infiniteScrollAdapter.getRealItemCount();
				
				
				Log.e(TAG, "onChanged: infiniteScrollAdapter.getRealItemCount() " + infiniteScrollAdapter.getRealItemCount());
				if (infiniteScrollAdapter.getRealItemCount() == 0)
				{
					backgroundText.setVisibility(View.VISIBLE);
					backgroundText.setText("Ops, you haven't added a card!");
					gym.setText("Card(0/0)");
				}
				else
				{
					backgroundText.setVisibility(View.GONE);
				}
				
			}
		});
		
		Button addCardBtn = getView().findViewById(R.id.wallet_card_button_addcard);
		addCardBtn.setOnClickListener(new View.OnClickListener()
		{
			String cardName = "";
			String cardDate = "";
			String cardNumber = "";
			
			@Override
			public void onClick(View view)
			{
				Dialog dialog = new Dialog(context);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setCancelable(true);
				dialog.setContentView(R.layout.dialog_add_card);
				
				CreditCardView creditCardView = dialog.findViewById(R.id.dialog_add_card_creditCardView);
				Button btn_yes = dialog.findViewById(R.id.dialog_add_card_yes);
				Button btn_no = dialog.findViewById(R.id.dialog_add_card_no);
				
				dialog.show();
				
				btn_yes.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						cardName = creditCardView.getCardName();
						cardDate = creditCardView.getExpiryDate();
						cardNumber = creditCardView.getCardNumber();
						Log.e(TAG, "creditCardView: " +cardName);
						Log.e(TAG, "creditCardView: " + cardNumber);
						Log.e(TAG, "creditCardView: " + cardDate);
						Log.e(TAG, "onCreate: UID" + userData.getUserId());
						// userData.addCreditCard(new CreditCard(cardNumber, cardName, cardDate));
						addCardToDB(cardNumber,cardName,cardDate);
						
						dialog.dismiss();
					}
				});
				btn_no.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						dialog.dismiss();
					}
				});
				
				// userData.addCreditCard(new CreditCard("","Click to edit me!", ""));
				// discreteScrollView.scrollToPosition(0);
			}
		});
		
		
	}
	
	public void importCardsFromDB()
	{
		ProgressDialog progressDialog = new ProgressDialog(this.context);
		progressDialog.setTitle("Fetching credit cards from server...");
		progressDialog.show();
		UserData.getInstance().setCreditCards(new ArrayList<>());
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		CollectionReference cardsRef = db.collection("CARDS");
		cardsRef.whereEqualTo("UID", userData.getUserId())
				.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
		{
			@Override
			public void onComplete(@NonNull Task<QuerySnapshot> task)
			{
				if (task.isSuccessful()) {
					Log.d(TAG, "Found " + task.getResult().getDocuments().size()+ " Cards in DB");
					for (QueryDocumentSnapshot document : task.getResult()) {
						String number = document.getData().get(userData.KEY_CARD_number).toString();
						String name = document.getData().get(userData.KEY_CARD_name).toString();
						String date = document.getData().get(userData.KEY_CARD_expiryDate).toString();
						String id = document.getId();
						Log.d(TAG, "Card ID "+ id);
						Log.d(TAG, "KEY_CARD_number "+number);
						Log.d(TAG, "KEY_CARD_name "+name);
						Log.d(TAG, "KEY_CARD_expiryDate "+date);
						userData.addCreditCard(new CreditCard(number,name,date, id));
					}
					progressDialog.dismiss();
				} else {
					progressDialog.dismiss();
					Log.d(TAG, "Failed to getting Cards from remote DataBase", task.getException());
					Toast.makeText(context,"Failed to getting Cards from remote DataBase",Toast.LENGTH_LONG);
				}
			}
		} );
	}
	
	public void addCardToDB(String cardNumber, String cardName, String cardDate){
		Map<String, Object> newCard = new HashMap<>();
		newCard.put(userData.KEY_CARD_number,cardNumber);
		newCard.put(userData.KEY_CARD_name,cardName);
		newCard.put(userData.KEY_CARD_expiryDate,cardDate);
		newCard.put(userData.KEY_uid, userData.getUserId());
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		CollectionReference cardsRef = db.collection("CARDS");
		cardsRef.add(newCard)
		// cardsRef.add(new CreditCard())
				.addOnSuccessListener(new OnSuccessListener<DocumentReference>()
				{
					@Override
					public void onSuccess(DocumentReference documentReference)
					{
						importCardsFromDB();
						Log.d(TAG,"Add card successfully: "+documentReference.getId());
					}
				})
				.addOnFailureListener(new OnFailureListener()
				{
					@Override
					public void onFailure(@NonNull Exception e)
					{
						Log.d(TAG,"Add card Failed: "+e.toString());
						e.printStackTrace();
					}
				});
	}
	
}