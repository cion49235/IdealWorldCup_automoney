package ideal.worldcup.automoney.activity;

import java.util.Random;

import com.bumptech.glide.Glide;
import com.easyandroidanimations.library.Animation;
import com.easyandroidanimations.library.AnimationListener;
import com.easyandroidanimations.library.FlipHorizontalAnimation;
import com.easyandroidanimations.library.FoldAnimation;
import com.easyandroidanimations.library.PuffInAnimation;
import com.easyandroidanimations.library.PuffOutAnimation;
import com.easyandroidanimations.library.ScaleInAnimation;
import com.easyandroidanimations.library.SlideInAnimation;
import com.easyandroidanimations.library.FoldLayout.Orientation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ideal.worldcup.automoney.R;
import ideal.worldcup.automoney.cms.ThemeContest;
import ideal.worldcup.automoney.util.PreferenceUtil;
import ideal.worldcup.automoney.util.SoundUtil;
import ideal.worldcup.automoney.util.UtilHelper;

public class ThemeContestActivity extends Activity implements OnClickListener {
    View resultView; // 결과화면 레이아웃
    ThemeContest themeContestList; // 대전 데이터
    int backgroundIdx = 0;
    int nowTournamentIdx = -1; // 0=결승, 1=4강, 2=8강, 3=16강
    int nowRound = 0;
    boolean isRun = false;
    ImageView ivFace;
    RelativeLayout relativeLayout;
    ImageView ivRound;
    ImageView ivPlayTextFront;
    ImageView ivPlayTextBack;
    ImageButton ibLeft;
    ImageButton ibRight;
    ImageButton btClose;
    TextView tvLeft;
    TextView tvRight;
    LinearLayout container;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_theme_contest);
        context = this;
        init_ui();
        // 초기 세팅값
        Intent fromIntent = getIntent();
        themeContestList = (ThemeContest) fromIntent.getSerializableExtra("THEME_CONTEST");
        backgroundIdx = fromIntent.getIntExtra("BACKGROUND_IDX", -1);
        nowTournamentIdx = fromIntent.getIntExtra("NOW_TOURNAMENT_IDX", -1);
        nowRound = fromIntent.getIntExtra("NOW_ROUND", 1);

        // 최초 설정값
        if ( backgroundIdx == -1 ) {
            // 백그라운드 이미지 설정
            Random random = new Random();
            backgroundIdx = random.nextInt(7);  // 0~6

            int maxTournament = fromIntent.getIntExtra("MAX_TOURNAMENT", -1);
            while( maxTournament > 1 ) {
                nowTournamentIdx += 1;
                maxTournament /= 2;
            }
        }

        // 이미지 리소스 배열 세팅
        int[] imgBackground = {
                R.drawable.contest_play_bg_01,
                R.drawable.contest_play_bg_02,
                R.drawable.contest_play_bg_03,
                R.drawable.contest_play_bg_04,
                R.drawable.contest_play_bg_05,
                R.drawable.contest_play_bg_06,
                R.drawable.contest_play_bg_07};

        int[] imgNowRound = {
                R.drawable.contest_play_text_front_1r_white,
                R.drawable.contest_play_text_front_2r_white,
                R.drawable.contest_play_text_front_3r_white,
                R.drawable.contest_play_text_front_4r_white,
                R.drawable.contest_play_text_front_5r_white,
                R.drawable.contest_play_text_front_6r_white,
                R.drawable.contest_play_text_front_7r_white,
                R.drawable.contest_play_text_front_8r_white,
                R.drawable.contest_play_text_front_9r_white,
                R.drawable.contest_play_text_front_10r_white,
                R.drawable.contest_play_text_front_11r_white,
                R.drawable.contest_play_text_front_12r_white,
                R.drawable.contest_play_text_front_13r_white,
                R.drawable.contest_play_text_front_14r_white,
                R.drawable.contest_play_text_front_15r_white,
                R.drawable.contest_play_text_front_16r_white};

        int[] imgNowRound2 = {
                R.drawable.contest_play_text_front_1r_black,
                R.drawable.contest_play_text_front_2r_black,
                R.drawable.contest_play_text_front_3r_black,
                R.drawable.contest_play_text_front_4r_black,
                R.drawable.contest_play_text_front_5r_black,
                R.drawable.contest_play_text_front_6r_black,
                R.drawable.contest_play_text_front_7r_black,
                R.drawable.contest_play_text_front_8r_black,
                R.drawable.contest_play_text_front_9r_black,
                R.drawable.contest_play_text_front_10r_black,
                R.drawable.contest_play_text_front_11r_black,
                R.drawable.contest_play_text_front_12r_black,
                R.drawable.contest_play_text_front_13r_black,
                R.drawable.contest_play_text_front_14r_black,
                R.drawable.contest_play_text_front_15r_black,
                R.drawable.contest_play_text_front_16r_black};

        int[] imgMaxRound = {
                R.drawable.contest_play_text_back_1r,
                R.drawable.contest_play_text_back_2r,
                R.drawable.contest_play_text_back_4r,
                R.drawable.contest_play_text_back_8r,
                R.drawable.contest_play_text_back_16r};

        int[] imgTournament = {
                R.drawable.contest_play_title_2,
                R.drawable.contest_play_title_4,
                R.drawable.contest_play_title_8,
                R.drawable.contest_play_title_16,
                R.drawable.contest_play_title_32};


        // 배경색에 따른 라운드 텍스트 이미지
        if ( backgroundIdx == 0 || backgroundIdx == 2 || backgroundIdx == 3 || backgroundIdx == 4 ) {
            System.arraycopy(imgNowRound2, 0, imgNowRound, 0, imgNowRound2.length);
        }
        // 배경색에 따른 닉네임 색상
        if ( backgroundIdx == 4 ) {
            tvLeft.setTextColor(Color.BLACK);
            tvRight.setTextColor(Color.BLACK);
        } else {
            tvLeft.setTextColor(Color.WHITE);
            tvRight.setTextColor(Color.WHITE);
        }

        // 이미지 세팅
        //relativeLayout.setBackgroundResource(imgBackground[backgroundIdx]);
        UtilHelper.getInstance().setBackground(this, relativeLayout, imgBackground[backgroundIdx]);
        ivPlayTextFront.setImageResource(imgNowRound[nowRound -1]);
        ivPlayTextBack.setImageResource(imgMaxRound[nowTournamentIdx]);
        ivRound.setImageResource(imgTournament[nowTournamentIdx]);

        // 대전 사진
        UtilHelper.getInstance().loadContestImg(this, ibLeft, themeContestList.list.get(0).cti_file_name);
        UtilHelper.getInstance().loadContestImg(this, ibRight, themeContestList.list.get(1).cti_file_name);

        // 다음이미지 캐싱
        UtilHelper.getInstance().loadCacheImg(this, themeContestList.list.get(2).cti_file_name, themeContestList.list.get(3).cti_file_name);

        // 닉네임 설정
        tvLeft.setText(themeContestList.list.get(0).cti_name);
        tvRight.setText(themeContestList.list.get(1).cti_name);
        

        // 애니메이션을 위한 이미지 숨김
        ibLeft.setImageAlpha(0);
        ibRight.setImageAlpha(0);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 등장시 애니메이션
                ibLeft.setImageAlpha(255);
                ibRight.setImageAlpha(255);
//                YoYo.with(Techniques.BounceInLeft).duration(300).playOn(ibLeft);
//                YoYo.with(Techniques.BounceInRight).duration(300).playOn(ibRight);
                new ScaleInAnimation(ibLeft).animate();
                new ScaleInAnimation(ibRight).animate();
                
            }
        }, 150);

        // 결과화면 레이아웃을 별도로 로드한다
        resultView = getLayoutInflater().inflate(R.layout.view_contest_round_result, null);

        // 결과화면을 현재 화면에 숨김상태로 추가
        resultView.setAlpha(0);
        relativeLayout.addView(resultView, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

    }
    
    private void init_ui() {
    	relativeLayout = (RelativeLayout)findViewById(R.id.activity_theme_contest);
    	ivRound = (ImageView)findViewById(R.id.ivRound);
    	ivPlayTextFront = (ImageView)findViewById(R.id.ivPlayTextFront);
    	ivPlayTextBack = (ImageView)findViewById(R.id.ivPlayTextBack);
    	ibLeft = (ImageButton)findViewById(R.id.ibLeft);
    	ibRight = (ImageButton)findViewById(R.id.ibRight);
    	btClose = (ImageButton)findViewById(R.id.btClose);
    	tvLeft = (TextView)findViewById(R.id.tvLeft);
    	tvRight = (TextView)findViewById(R.id.tvRight);
    	container = (LinearLayout)findViewById(R.id.container);
    	
    	ibLeft.setOnClickListener(this);
    	ibRight.setOnClickListener(this);
    	btClose.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UtilHelper.getInstance().freeBackground(relativeLayout);
        UtilHelper.getInstance().freeImageResource(ibLeft);
        UtilHelper.getInstance().freeImageResource(ibRight);
        UtilHelper.getInstance().freeImageResource(ivFace);
        UtilHelper.getInstance().freeImageResource(ivRound);
        UtilHelper.getInstance().freeImageResource(ivPlayTextFront);
        UtilHelper.getInstance().freeImageResource(ivPlayTextBack);
    }

    // 백버튼 클릭시
    @Override
    public void onBackPressed() {
    	vs_close();
    }


    // 다음 라운드 진행
    public void nextRound() {
        // 라운드 종료시
        if ( nowRound >= Math.pow(2, nowTournamentIdx) ) {
            nowRound = 0;
            nowTournamentIdx -= 1;

            // 대전 종료
            if ( nowTournamentIdx < 0 ) {
                SoundUtil.stop_background_bgm();
                Intent intent = new Intent(this, ThemeEndActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("THEME_CONTEST", themeContestList);
                startActivity(intent);
                finish();
            }
            // 다음 토너먼트
            else {
                Intent intent = new Intent(this, ThemeContestIntroActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("THEME_CONTEST", themeContestList);
                intent.putExtra("BACKGROUND_IDX", backgroundIdx);
                intent.putExtra("NOW_TOURNAMENT_IDX", nowTournamentIdx);
                intent.putExtra("NOW_ROUND", ++nowRound);
                startActivity(intent);
                finish();
            }
        }
        // 다음 라운드
        else {
            Intent intent = new Intent(this, ThemeContestActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra("THEME_CONTEST", themeContestList);
            intent.putExtra("BACKGROUND_IDX", backgroundIdx);
            intent.putExtra("NOW_TOURNAMENT_IDX", nowTournamentIdx);
            intent.putExtra("NOW_ROUND", ++nowRound);
            startActivity(intent);
            finish();
        }
    }

    // 에니메이션 죽이기
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
    
    Drawable choicePhoto;
    String choiceNickname;
    @Override
	public void onClick(View view) {
    	
		if(view == ibLeft) {// 왼쪽, 오른쪽 선택시
			// 한번만 실행
	        if ( isRun ) {
	            return;
	        } else {
	            isRun = true;
	        }

	        // 결과정보를 표시할 컴포넌트 가져오기
	        ivFace = (ImageView) resultView.findViewById(R.id.ivFace);
	        TextView tvNickName = (TextView) resultView.findViewById(R.id.tvNickName);
			// 4강이상일때
            if ( nowTournamentIdx > 0 ) {
                themeContestList.list.add((int) Math.pow(2, nowTournamentIdx + 1), themeContestList.list.get(1));
                themeContestList.list.add((int) Math.pow(2, nowTournamentIdx + 1) - (nowRound - 1), themeContestList.list.get(0));
                themeContestList.list.remove(1);
                themeContestList.list.remove(0);
            }
            // 결승일때
            else {
                // 변화없음
            }

            // 선택된 정보
            choicePhoto = ibLeft.getDrawable();
            choiceNickname = tvLeft.getText().toString().trim();
            
            
         // 배경색에 따른 색상 변경
            if ( backgroundIdx == 0 || backgroundIdx == 2 || backgroundIdx == 3 || backgroundIdx == 4 ) {
                tvNickName.setTextColor(Color.BLACK);
            } else {
                tvNickName.setTextColor(Color.WHITE);
            }

            // 결과정보 뿌리기
            ivFace.setImageDrawable(choicePhoto);
            tvNickName.setText(choiceNickname);

            // 진동 랜덤패턴
            if(PreferenceUtil.getBooleanSharedData(this, PreferenceUtil.PREF_SOUND_EFFECT, true) == true){
                int do_random_sound_effect = random_sound_effect();
                int rawSound = 0;
                Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                if(do_random_sound_effect == 0){
                    long[] pattern = {100,100,100,200,200,0};
                    vibrator.vibrate(pattern,-1);
                }else if(do_random_sound_effect == 1){
                    long[] pattern = {500,100,500,100,200,0};
                    vibrator.vibrate(pattern,-1);
                }else if(do_random_sound_effect == 2){
                    vibrator.vibrate(500);
                }else if(do_random_sound_effect == 3){
                    long[] pattern = {200,100,0,100,200,0};
                    vibrator.vibrate(pattern,-1);
                }else if(do_random_sound_effect == 4){
                    long[] pattern = {300,100,200,100,150,0};
                    vibrator.vibrate(pattern,-1);
                }
            }
            // 기본 대전 정보 숨김
//            YoYo.with(Techniques.FadeOut).duration(500).playOn(container);
            // 결과정보 출력
//            YoYo.with(Techniques.DropOut).duration(500).playOn(resultView);
            
            new PuffOutAnimation(container).animate();
			new PuffInAnimation(resultView).animate();
            
            // import android.os.Handler
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nextRound();
                }
            }, 1000);
         
		}else if(view == ibRight) {
			// 한번만 실행
	        if ( isRun ) {
	            return;
	        } else {
	            isRun = true;
	        }

	        // 결과정보를 표시할 컴포넌트 가져오기
	        ivFace = (ImageView) resultView.findViewById(R.id.ivFace);
	        TextView tvNickName = (TextView) resultView.findViewById(R.id.tvNickName);
			
			
			// 4강이상일때
            if ( nowTournamentIdx > 0 ) {
                themeContestList.list.add((int) Math.pow(2, nowTournamentIdx + 1), themeContestList.list.get(0));
                themeContestList.list.add((int) Math.pow(2, nowTournamentIdx + 1) - (nowRound - 1), themeContestList.list.get(1));
                themeContestList.list.remove(1);
                themeContestList.list.remove(0);
            }
            // 결승일때
            else {
                themeContestList.list.add(2, themeContestList.list.get(0));
                themeContestList.list.remove(0);
            }

            // 선택된 정보
            choicePhoto = ibRight.getDrawable();
            choiceNickname = tvRight.getText().toString().trim();
            
         // 배경색에 따른 색상 변경
            if ( backgroundIdx == 0 || backgroundIdx == 2 || backgroundIdx == 3 || backgroundIdx == 4 ) {
                tvNickName.setTextColor(Color.BLACK);
            } else {
                tvNickName.setTextColor(Color.WHITE);
            }

            // 결과정보 뿌리기
            ivFace.setImageDrawable(choicePhoto);
            tvNickName.setText(choiceNickname);

            // 진동 랜덤패턴
            if(PreferenceUtil.getBooleanSharedData(this, PreferenceUtil.PREF_SOUND_EFFECT, true) == true){
                int do_random_sound_effect = random_sound_effect();
                int rawSound = 0;
                Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                if(do_random_sound_effect == 0){
                    long[] pattern = {100,100,100,200,200,0};
                    vibrator.vibrate(pattern,-1);
                }else if(do_random_sound_effect == 1){
                    long[] pattern = {500,100,500,100,200,0};
                    vibrator.vibrate(pattern,-1);
                }else if(do_random_sound_effect == 2){
                    vibrator.vibrate(500);
                }else if(do_random_sound_effect == 3){
                    long[] pattern = {200,100,0,100,200,0};
                    vibrator.vibrate(pattern,-1);
                }else if(do_random_sound_effect == 4){
                    long[] pattern = {300,100,200,100,150,0};
                    vibrator.vibrate(pattern,-1);
                }
            }
            // 기본 대전 정보 숨김
//            YoYo.with(Techniques.FadeOut).duration(500).playOn(container);
            // 결과정보 출력
//            YoYo.with(Techniques.DropOut).duration(500).playOn(resultView);

            // import android.os.Handler
            new PuffOutAnimation(container).animate();
			new PuffInAnimation(resultView).animate();
            
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nextRound();
                }
            }, 1000);
            
		}else if(view == btClose) {
			vs_close();
		}
	}
    
    private void vs_close() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(context.getString(R.string.ThemeContestActivity01));
        builder.setIcon(R.drawable.icon64);
        builder.setMessage(context.getString(R.string.ThemeContestActivity02));
        builder.setCancelable(false);

        builder.setNegativeButton(context.getString(R.string.ThemeContestActivity03), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });

        builder.setPositiveButton(context.getString(R.string.ThemeContestActivity04), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SoundUtil.stop_background_bgm();
                finish();
            }
        });

        builder.create().show();
    }

    // 효과음 랜덤 생성
    int random;
    private int random_sound_effect(){
        for(int i=1; i < 5; i++){
            random = (int)(Math.random() * 5);
        }
        return random;
    }
	
}