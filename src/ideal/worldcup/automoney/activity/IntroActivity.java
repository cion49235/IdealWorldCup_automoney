package ideal.worldcup.automoney.activity;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import ideal.worldcup.automoney.MainFragmentActivity;
import ideal.worldcup.automoney.R;



public class IntroActivity extends Activity{
	public Handler handler;
	public Context context;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_intro);
        context = this;
    	
        handler = new Handler();
        handler.postDelayed(runnable, 2000);
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if(handler != null){
    		handler.removeCallbacks(runnable);
    	}
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    }
    @Override
    protected void onPause() {
    	super.onPause();
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    }
    
    Runnable runnable = new Runnable() {
		@Override
		public void run() {
			Intent intent = new Intent(context, MainFragmentActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			//fade_animation
			overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		}
	};
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(handler != null) handler.removeCallbacks(runnable);
		finish();
	}
}
