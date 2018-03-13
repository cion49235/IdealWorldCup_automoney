package ideal.worldcup.automoney.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import ideal.worldcup.automoney.R;
import ideal.worldcup.automoney.cms.ThemeContest;
import ideal.worldcup.automoney.util.UtilHelper;

public class ThemeContestIntroActivity extends Activity {

    ThemeContest contestTheme;
    int backgroundIdx;
    int nowTournamentIdx;
    int nowRound;

    RelativeLayout backgroundLayout;
    ImageView ivTournament;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_theme_contest_intro);
        init_ui();
        Intent fromIntent = getIntent();
        contestTheme = (ThemeContest) fromIntent.getSerializableExtra("THEME_CONTEST");
        backgroundIdx = fromIntent.getIntExtra("BACKGROUND_IDX", -1);
        nowTournamentIdx = fromIntent.getIntExtra("NOW_TOURNAMENT_IDX", 0);
        nowRound = fromIntent.getIntExtra("NOW_ROUND", 1);

        // 이미지 리소스 배열 세팅
        int[] imgIntroBackground = {
                R.drawable.contest_interval_bg_01,
                R.drawable.contest_interval_bg_02,
                R.drawable.contest_interval_bg_03};

        int[] imgTournament = {R.drawable.contest_interval_2r,
                R.drawable.contest_interval_4r,
                R.drawable.contest_interval_8r,
                R.drawable.contest_interval_16r};

        // 인트로 백그라운드 이미지 설정
        int introBackgroundIdx = 2;
        if ( nowTournamentIdx == 0 ) introBackgroundIdx = 0;
        else if ( nowTournamentIdx == 1 ) introBackgroundIdx = 1;

        //backgroundLayout.setBackgroundResource(imgIntroBackground[introBackgroundIdx]);
        UtilHelper.getInstance().setBackground(this, backgroundLayout, imgIntroBackground[introBackgroundIdx]);
        ivTournament.setImageResource(imgTournament[nowTournamentIdx]);

        // 다음이미지 캐싱
        UtilHelper.getInstance().loadCacheImg(ThemeContestIntroActivity.this, contestTheme.list.get(0).cti_file_name, contestTheme.list.get(1).cti_file_name);

        // Thread 딜레이 처리
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 그 밖의 경우
                Intent intent = new Intent(ThemeContestIntroActivity.this, ThemeContestActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra("THEME_CONTEST", contestTheme);
                intent.putExtra("BACKGROUND_IDX", backgroundIdx);
                intent.putExtra("NOW_TOURNAMENT_IDX", nowTournamentIdx);
                intent.putExtra("NOW_ROUND", nowRound);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }
    
    private void init_ui() {
        backgroundLayout = (RelativeLayout)findViewById(R.id.backgroundLayout);
        ivTournament = (ImageView)findViewById(R.id.ivTournament);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UtilHelper.getInstance().freeBackground(backgroundLayout);
        UtilHelper.getInstance().freeImageResource(ivTournament);
    }
}
