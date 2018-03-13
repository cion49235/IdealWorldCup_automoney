package ideal.worldcup.automoney.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import ideal.worldcup.automoney.R;


/**
 * Created by LeeDongSu on 2017-07-11.
 */
public class SoundUtil {
    static SoundPool soundPool;
    public static void init_sound_pool(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(1).build();
            //setMaxStream() 동시에 재생할 수 있는 개수를 5개 설정
        }
        else {
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
            //SoundPool 첫인자 동시에 재생할 수 있는 개수를 5개 설정
        }
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(sampleId, 1f, 1f, 0, 0, 1f);
            }
        });
    }

    public static void destory_sound_pool(){
        soundPool.release();
        soundPool = null;
    }

    public static void load_play_sound_pool(Context context){
    	if(PreferenceUtil.getStringSharedData(context, PreferenceUtil.PREF_CONTENT_STATUS, "Y").equals("Y")) {
    		soundPool.load(context, R.raw.bg_theme01_finish, 1);
            soundPool.play(R.raw.bg_theme01_finish, 1f, 1f, 0, 0, 1f);	
    	}
    }

    static MediaPlayer mediaPlayer;
    public static void start_background_bgm(Context context){
    	try {
    		if(PreferenceUtil.getStringSharedData(context, PreferenceUtil.PREF_CONTENT_STATUS, "Y").equals("Y")) {
    			int do_random_sound_effect = random_sound_effect();
            	Log.i("dsu", "랜덤재생 : " + do_random_sound_effect);
            	if(do_random_sound_effect == 0){
            		mediaPlayer = MediaPlayer.create(context, R.raw.bg_theme01);
            		mediaPlayer.setLooping(true);
                    mediaPlayer.start();
            	}else if(do_random_sound_effect == 1){
            		mediaPlayer = MediaPlayer.create(context, R.raw.bg_theme02);
            		mediaPlayer.setLooping(true);
                    mediaPlayer.start();
            	}else if(do_random_sound_effect == 2){
            		mediaPlayer = MediaPlayer.create(context, R.raw.bg_theme03);
            		mediaPlayer.setLooping(true);
                    mediaPlayer.start();
            	}else if(do_random_sound_effect == 3){
            		mediaPlayer = MediaPlayer.create(context, R.raw.bg_theme04);
            		mediaPlayer.setLooping(true);
                    mediaPlayer.start();
            	}else if(do_random_sound_effect == 4){
            		mediaPlayer = MediaPlayer.create(context, R.raw.bg_theme05);
            		mediaPlayer.setLooping(true);
                    mediaPlayer.start();
            	}else if(do_random_sound_effect == 5){
            		mediaPlayer = MediaPlayer.create(context, R.raw.bg_theme06);
            		mediaPlayer.setLooping(true);
                    mediaPlayer.start();
            	}
    		}
    	}catch (NullPointerException e) {
		}
    	
    }
    
    // 효과음 랜덤 생성
    static int random;
    static int random_sound_effect(){
        for(int i=1; i < 7; i++){
            random = (int)(Math.random() * 7);
        }
        return random;
    }

    public static void stop_background_bgm(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
