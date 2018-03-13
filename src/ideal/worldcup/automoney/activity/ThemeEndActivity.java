package ideal.worldcup.automoney.activity;

import com.admixer.AdAdapter;
import com.admixer.AdInfo;
import com.admixer.AdMixerManager;
import com.admixer.InterstitialAd;
import com.admixer.InterstitialAdListener;
import com.bumptech.glide.Glide;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ideal.worldcup.automoney.R;
import ideal.worldcup.automoney.cms.BaseModel;
import ideal.worldcup.automoney.cms.BaseResponse;
import ideal.worldcup.automoney.cms.ThemeContest;
import ideal.worldcup.automoney.util.DBopenHelper_Result;
import ideal.worldcup.automoney.util.JsonClient;
import ideal.worldcup.automoney.util.PreferenceUtil;
import ideal.worldcup.automoney.util.SoundUtil;
import ideal.worldcup.automoney.util.UtilHelper;

public class ThemeEndActivity extends Activity implements OnClickListener, InterstitialAdListener {

    // 대전 데이터
    ThemeContest themeContestList;
    String winName;
    String winImage;
    JsonClient client;

    TextView tvCtTitle;
    LinearLayout llReward;
    LinearLayout llEndText;
    TextView tvReward;
    TextView tvEndText;
    TextView tvItemName;
    ImageView ivItemImg;
    ImageButton btResult;
    LinearLayout bgLayout;
    Context context;
    InterstitialAd interstialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_end);
        context = this;
        init_ui();
        UtilHelper.getInstance().setBackground(this, bgLayout, R.drawable.contest_result_bg_2);

        Intent fromIntent = getIntent();
        themeContestList = (ThemeContest) fromIntent.getSerializableExtra("THEME_CONTEST");

        setWinnerList();
        if(PreferenceUtil.getBooleanSharedData(this, PreferenceUtil.PREF_SOUND_EFFECT, true) == true){
            SoundUtil.init_sound_pool();//테마대전 결과페이지 효과음 초기화
            SoundUtil.load_play_sound_pool(this);//테먀대전 효과음 플레이
        }
        
        AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMIXER, "emd75whx");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMOB, "ca-app-pub-4637651494513698/7149430504");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMOB_FULL, "ca-app-pub-4637651494513698/5782230713");
		
		addInterstitialView();
    }
    
    public void addInterstitialView() {
    	if(interstialAd == null) {
        	AdInfo adInfo = new AdInfo("emd75whx");
//        	adInfo.setTestMode(false);
        	interstialAd = new InterstitialAd(this);
        	interstialAd.setAdInfo(adInfo, this);
        	interstialAd.setInterstitialAdListener(this);
        	interstialAd.startInterstitial();
    	}
    }
    
    private void init_ui() {
    	 client = JsonClient.getInstance(this);
    	 tvCtTitle = (TextView)findViewById(R.id.tvCtTitle);
    	 llEndText = (LinearLayout)findViewById(R.id.llEndText);
 	     llReward = (LinearLayout)findViewById(R.id.llReward);
 	     tvReward = (TextView)findViewById(R.id.tvReward);
 	     tvEndText = (TextView)findViewById(R.id.tvEndText);
 	     tvItemName = (TextView)findViewById(R.id.tvItemName);
 	     ivItemImg = (ImageView)findViewById(R.id.ivItemImg);
 	     btResult = (ImageButton)findViewById(R.id.btResult);
 	     btResult.setOnClickListener(this);
 	     bgLayout = (LinearLayout)findViewById(R.id.bgLayout);  

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UtilHelper.getInstance().freeBackground(bgLayout);
        if(PreferenceUtil.getBooleanSharedData(this, PreferenceUtil.PREF_SOUND_EFFECT, true) == true){
            SoundUtil.destory_sound_pool();//테마대전 결과페이지 효과음 해제
        }
    }
    
    int ct_seq = -1;
    private DBopenHelper_Result mydb;
    private void check_result_ct_seq_db(int ct_seq) {
    	try{
    		mydb = new DBopenHelper_Result(context);
			Cursor cursor = mydb.getReadableDatabase().rawQuery(
					"select * from result_list where ct_seq = '"+ct_seq+"'", null);
			if(null != cursor && cursor.moveToFirst()){
				this.ct_seq = cursor.getInt(cursor.getColumnIndex("ct_seq"));
			}else{
				this.ct_seq = -1;
			}
			
			if(this.ct_seq == -1){
				ContentValues cv = new ContentValues();
				cv.put("ct_seq", ct_seq);
				mydb.getWritableDatabase().insert("result_list", null, cv);
			}
		}catch (Exception e) {
		}finally{
			if(mydb != null) {
				 mydb.close();
			}
		}
    }

    private void setWinnerList() {
        View modelView;
        TextView textViewMain, textViewSub;
        ImageView ivRank;
        Button btFavorite, btMessage, btFanRoom;

        winName = themeContestList.list.get(0).cti_name;
        winImage = themeContestList.list.get(0).cti_file_name;

        tvCtTitle.setText(themeContestList.ct_title);
        tvItemName.setText(themeContestList.list.get(0).cti_name);

        if (themeContestList.ct_type.equals("C")) {
            tvReward.setText(themeContestList.ct_reward_txt);
            tvEndText.setText(themeContestList.end_text);
        } else {
        	llEndText.setVisibility(View.GONE);
        	llReward.setVisibility(View.GONE);
        }

        Glide.with(ThemeEndActivity.this).load(getString(R.string.url_image_prefix)+themeContestList.list.get(0).cti_file_name).into(ivItemImg);
        
        check_result_ct_seq_db(themeContestList.ct_seq);
        Log.i("dsu", "ct_seq : " + themeContestList.ct_seq);

        client.init(R.string.contest_theme_win_upd, getString(R.string.app_uid), themeContestList.ct_seq, themeContestList.list.get(0).cti_seq);
        client.post(new BaseResponse<BaseModel>(this) {
            @Override
            public void onResponse(BaseModel response) {
                if (!response.code.equals("000")) {
                    Toast.makeText(getApplicationContext(), response.msg, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
    
    private void go_themeresult() {
    	Intent intent = new Intent(this, ThemeResultActivity.class);
        intent.putExtra("CT_SEQ", themeContestList.ct_seq);
        startActivity(intent);
        finish();
    }
    
    @Override
	public void onClick(View view) {
    	if(view == btResult) {
    		go_themeresult();
    	}
	}
    
	@Override
	public void onInterstitialAdClosed(com.admixer.InterstitialAd arg0) {
		interstialAd = null;
		go_themeresult();
	}

	@Override
	public void onInterstitialAdFailedToReceive(int arg0, String arg1,
			com.admixer.InterstitialAd arg2) {
		interstialAd = null;
	}

	@Override
	public void onInterstitialAdReceived(String arg0,
			com.admixer.InterstitialAd arg1) {
		interstialAd = null;
	}

	@Override
	public void onInterstitialAdShown(String arg0,
			com.admixer.InterstitialAd arg1) {
	}

	@Override
	public void onLeftClicked(String arg0, com.admixer.InterstitialAd arg1) {
	}

	@Override
	public void onRightClicked(String arg0, com.admixer.InterstitialAd arg1) {
	}


}