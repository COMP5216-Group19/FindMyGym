package comp5216.sydney.edu.au.findmygym;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.romainpiel.shimmer.Shimmer;

public class LoadingActivity extends AppCompatActivity
{
	Shimmer shimmer;
	Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		context = getApplicationContext();
		setContentView(R.layout.activity_loading);
		getSupportActionBar().hide();
		shimmer = new Shimmer();
		shimmer.start(findViewById(R.id.shimmer_tv));
		shimmer.setRepeatCount(1)//1
				.setDuration(500)//500
				.setStartDelay(0)
				.setDirection(Shimmer.ANIMATION_DIRECTION_RTL)
				.setAnimatorListener(new Animator.AnimatorListener(){
					@Override
					public void onAnimationStart(Animator animator)
					{
					
					}
					
					@Override
					public void onAnimationEnd(Animator animator)
					{
						Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
						startActivity(intent);
					}
					
					@Override
					public void onAnimationCancel(Animator animator)
					{
					
					}
					
					@Override
					public void onAnimationRepeat(Animator animator)
					{
					
					}
				});
	}
}