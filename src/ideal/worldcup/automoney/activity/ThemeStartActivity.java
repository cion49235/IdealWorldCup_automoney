package ideal.worldcup.automoney.activity;


import com.bumptech.glide.Glide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ideal.worldcup.automoney.R;
import ideal.worldcup.automoney.cms.BaseResponse;
import ideal.worldcup.automoney.cms.ThemeContest;
import ideal.worldcup.automoney.util.JsonClient;
import ideal.worldcup.automoney.util.LogHelper;
import ideal.worldcup.automoney.util.PreferenceUtil;
import ideal.worldcup.automoney.util.SoundUtil;
import ideal.worldcup.automoney.util.UtilHelper;

public class ThemeStartActivity extends Activity implements OnClickListener {
	private JsonClient client;
	private ThemeContest themeContest;
	private int ct_seq;
	private TextView tvTitle;
	private TextView tvReward;
	private LinearLayout llReward;
	private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_theme_start);
        init_ui();
        client = JsonClient.getInstance(this);
        Intent fromIntent = getIntent();
        ct_seq = fromIntent.getIntExtra("CT_SEQ", -1);

        Glide.with(this).load(R.drawable.contest_thema_intro_bg_02).skipMemoryCache(true).into(imageButton);

        // 대전 내용 미리 가져오기
        client.init(R.string.contest_theme_item_sel, R.string.app_uid, this.ct_seq);
        client.post(new ContestResponse(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UtilHelper.getInstance().freeImageResource(imageButton);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(PreferenceUtil.getBooleanSharedData(this, PreferenceUtil.PREF_SOUND_EFFECT, true) == true){
            SoundUtil.start_background_bgm(this);//테마대전 효과음 플레이
        }
    }
    
    
    private void init_ui() {
    	llReward = (LinearLayout)findViewById(R.id.llReward);
    	tvTitle = (TextView)findViewById(R.id.tvTitle);
    	tvReward = (TextView)findViewById(R.id.tvReward);
    	imageButton = (ImageButton)findViewById(R.id.imageButton);
    	imageButton.setOnClickListener(this);
    }

    // 대전시작 클릭시
	@Override
	public void onClick(View view) {
		if(view == imageButton) {
			 Intent intent = new Intent(this, ThemeContestActivity.class);
			 intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			 intent.putExtra("THEME_CONTEST", themeContest);
			 intent.putExtra("MAX_TOURNAMENT", themeContest.ct_cnt_target);
			 startActivity(intent);
			 finish();
		}
	}

    class ContestResponse extends BaseResponse<ThemeContest> {
        public ContestResponse(Context context) {
            super(context);
        }

        @Override
        public void onResponse(ThemeContest response) {
            LogHelper.debug(this, response.toString());
            if (!response.code.equals("000")) {
                Toast.makeText(ThemeStartActivity.this, response.msg, Toast.LENGTH_SHORT).show();
                return;
            }

            themeContest = response;

            tvTitle.setText(response.ct_title);

            // 다음이미지 캐싱
            UtilHelper.getInstance().loadCacheImg(ThemeStartActivity.this, themeContest.list.get(0).cti_file_name, themeContest.list.get(1).cti_file_name);

        }
    }
    @Override
    public void onBackPressed() {//뒤로가기
        super.onBackPressed();
        if(PreferenceUtil.getBooleanSharedData(this, PreferenceUtil.PREF_SOUND_EFFECT, true) == true){
            SoundUtil.stop_background_bgm();//테마대전 효과음 종료
        }
    }

}