package ideal.worldcup.automoney;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.admixer.AdAdapter;
import com.admixer.AdInfo;
import com.admixer.AdMixerManager;
import com.admixer.AdView.AdAnimation;
import com.admixer.AdViewListener;
import com.admixer.CustomPopup;
import com.admixer.CustomPopupListener;
import com.admixer.InterstitialAd;
import com.admixer.InterstitialAdListener;
import com.admixer.PopupInterstitialAdOption;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;
import ideal.worldcup.automoney.activity.GeneralThemeCreateActivity;
import ideal.worldcup.automoney.fragment.Fragment_Event;
import ideal.worldcup.automoney.fragment.Fragment_General;
import ideal.worldcup.automoney.fragment.Fragment_Popularity;
import ideal.worldcup.automoney.fragment.Fragment_Special;
import ideal.worldcup.automoney.util.PreferenceUtil;
import kr.co.inno.autocash.service.AutoLoginServiceActivity;
import kr.co.inno.autocash.service.AutoServiceActivity;

public class MainFragmentActivity extends SherlockFragmentActivity implements AdViewListener, CustomPopupListener, InterstitialAdListener {
	private Context context;
	private ActionBar actionbar;
	private ViewPager viewpager;
	private Tab tab;
	private TabContentAdapter adapter;
	private RelativeLayout ad_layout;
	private int current_page = 0;
	private InterstitialAd interstialAd;
	public boolean flag;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		setContentView(R.layout.fragment_main);
		context = this;
		autoappstart();
		actionbar = getSupportActionBar();
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#66cdaa")));
		actionbar.setStackedBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		actionbar.setTitle(Html.fromHtml("<font color=\"#ffffff\"><b>" + context.getString(R.string.app_name) + " VS." + "</b></font>"));
		actionbar.setDisplayShowHomeEnabled(false); // remove the icon
		/*actionbar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
		actionbar.setHomeButtonEnabled(false); // disable the button
		actionbar.setDisplayHomeAsUpEnabled(false); // remove the left caret*/
		
		viewpager = (CustomViewPager)findViewById(R.id.pager);//swipe��� ����
		FragmentManager fm = getSupportFragmentManager();
		ViewPager.SimpleOnPageChangeListener ViewPagerListener = new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				actionbar.setSelectedNavigationItem(position);
			}
		};
		
		viewpager.setOnPageChangeListener(ViewPagerListener);
		adapter = new TabContentAdapter(fm);
		viewpager.setAdapter(adapter);
		current_page = PreferenceUtil.getIntSharedData(context, PreferenceUtil.PREF_TAB_SELECTED, current_page);
		Log.i("dsu", "current_page : " + current_page);
		viewpager.postDelayed(new Runnable() {
			@Override
			public void run() {
				viewpager.setCurrentItem(current_page, true);
			}
		}, 100);
		
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				viewpager.setCurrentItem(tab.getPosition());
				if(tab.getPosition() == 0){
					PreferenceUtil.setIntSharedData(context, PreferenceUtil.PREF_TAB_SELECTED, tab.getPosition());
				}else if(tab.getPosition() == 1){
					PreferenceUtil.setIntSharedData(context, PreferenceUtil.PREF_TAB_SELECTED, tab.getPosition());
				}else if(tab.getPosition() == 2){
					PreferenceUtil.setIntSharedData(context, PreferenceUtil.PREF_TAB_SELECTED, tab.getPosition());
				}else if(tab.getPosition() == 3){
					PreferenceUtil.setIntSharedData(context, PreferenceUtil.PREF_TAB_SELECTED, tab.getPosition());
				}
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			}
			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
			}
		};
		
		tab = actionbar.newTab().setText(context.getString(R.string.MainFragmentActivity01)).setTabListener(tabListener);
		actionbar.addTab(tab);
		
		tab = actionbar.newTab().setText(context.getString(R.string.MainFragmentActivity02)).setTabListener(tabListener);
		actionbar.addTab(tab);
		
		tab = actionbar.newTab().setText(context.getString(R.string.MainFragmentActivity03)).setTabListener(tabListener);
		actionbar.addTab(tab);
		
		tab = actionbar.newTab().setText(context.getString(R.string.MainFragmentActivity04)).setTabListener(tabListener);
		actionbar.addTab(tab);
		
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMIXER, "emd75whx");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMOB, "ca-app-pub-4637651494513698/7149430504");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMOB_FULL, "ca-app-pub-4637651494513698/5782230713");
//		  Custom Popup 시작
		CustomPopup.setCustomPopupListener(this);
		CustomPopup.startCustomPopup(this, "emd75whx");
		addBannerView();
		exit_handler();
		auto_service();
	}
	
	public void addBannerView() {
    	AdInfo adInfo = new AdInfo("emd75whx");
    	adInfo.setTestMode(false);
        com.admixer.AdView adView = new com.admixer.AdView(this);
        adView.setAdInfo(adInfo, this);
        adView.setAdViewListener(this);
        adView.setAdAnimation(AdAnimation.TopSlide);
        ad_layout = (RelativeLayout)findViewById(R.id.ad_layout);
        if(ad_layout != null){
        	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            ad_layout.addView(adView, params);	
        }
    }
	
	private void addInterstitialView() {
    	if(interstialAd != null)
			return;
		
		AdInfo adInfo = new AdInfo("emd75whx");
		adInfo.setInterstitialTimeout(0); // 초단위로 전면 광고 타임아웃 설정 (기본값 : 0, 0 이면 서버 지정 시간(20)으로 처리됨)
		adInfo.setUseRTBGPSInfo(false);
		adInfo.setMaxRetryCountInSlot(-1); // 리로드 시간 내에 전체 AdNetwork 반복 최대 횟수(-1 : 무한, 0 : 반복 없음, n : n번 반복)
		adInfo.setBackgroundAlpha(true); // 고수익 전면광고 노출 시 광고 외 영역 반투명처리 여부 (true: 반투명, false: 처리안함)

//		 이 주석을 제거하시면 고수익 전면광고가 팝업형으로 노출됩니다.
		// 팝업형 전면광고 세부설정을 원하시면 아래 PopupInterstitialAdOption 설정하세요
		PopupInterstitialAdOption adConfig = new PopupInterstitialAdOption();
		// 팝업형 전면광고 노출 상태에서 뒤로가기 버튼 방지 (true : 비활성화, false : 활성화)
		adConfig.setDisableBackKey(true);
		// 왼쪽버튼. 디폴트로 제공되며, 광고를 닫는 기능이 적용되는 버튼 (버튼문구, 버튼색상)
		adConfig.setButtonLeft(context.getString(R.string.txt_finish_no), "#234234");
		// 오른쪽 버튼을 사용하고자 하면 반드시 설정하세요. 앱을 종료하는 기능을 적용하는 버튼. 미설정 시 위 광고종료 버튼만 노출
		adConfig.setButtonRight(context.getString(R.string.txt_finish_yes), "#234234");
		// 버튼영역 색상지정
		adConfig.setButtonFrameColor(null);
		// 팝업형 전면광고 추가옵션 (com.admixer.AdInfo$InterstitialAdType.Basic : 일반전면, com.admixer.AdInfo$InterstitialAdType.Popup : 버튼이 있는 팝업형 전면)
		adInfo.setInterstitialAdType(AdInfo.InterstitialAdType.Popup, adConfig);
		
		interstialAd = new InterstitialAd(this);
		interstialAd.setAdInfo(adInfo, this);
		interstialAd.setInterstitialAdListener(this);
		interstialAd.startInterstitial();
    }
	
	@Override
	protected void onStart() {
		super.onStart();
		PreferenceUtil.setBooleanSharedData(context, PreferenceUtil.PREF_AD_VIEW, false);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Custom Popup 종료
		CustomPopup.stopCustomPopup();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
	}
	
	private void auto_service() {
        Intent intent = new Intent(context, AutoServiceActivity.class);
        context.stopService(intent);
        context.startService(intent);
    }
	
	private void autoappstart() {
		SharedPreferences prefs = getSharedPreferences("kr.co.byapps", MODE_PRIVATE);
        final  String loginID = prefs.getString("loginID", "");
        String googlePasswd = prefs.getString("googlePasswd", "");
        if(!TextUtils.isEmpty(loginID) && !TextUtils.isEmpty(googlePasswd)){
            auto_service();
            auto_login_service();
        }
	}

    private void auto_login_service() {
        Intent intent = new Intent(context, AutoLoginServiceActivity.class);
        context.stopService(intent);
        context.startService(intent);
    }
	
	private void datasetchanged(){
		adapter.notifyDataSetChanged();
	}
	
	public class TabContentAdapter extends FragmentPagerAdapter {
		private int PAGE_COUNT;
		public TabContentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if(position == 0){
				Fragment_Popularity fragment_theme1 = new Fragment_Popularity();
				return fragment_theme1;
			}else if(position == 1){
				Fragment_General fragment_theme2 = new Fragment_General();
				return fragment_theme2;
			}else if(position == 2){
				Fragment_Special fragment_theme3 = new Fragment_Special();
				return fragment_theme3;
			}else if(position == 3){
				Fragment_Event fragment_theme4 = new Fragment_Event();
				return fragment_theme4;
			}
			return null;
		}
		
		@Override
		public int getCount() {
			PAGE_COUNT = 4;
			return PAGE_COUNT;
		}
		
		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}
	}
	
	
	@Override  
	public boolean onCreateOptionsMenu(Menu menu) { 
		menu.add(0, 0, 0, context.getString(R.string.MainFragmentActivity06))
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(0, 1, 0, context.getString(R.string.MainFragmentActivity07))
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;  
	}  
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			go_create_general_theme();
			return true;
		case 1:
			alert_sound_effect();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void go_create_general_theme() {
		Intent intent = new Intent(this, GeneralThemeCreateActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	private int SOUND_EFFECT_SET = 0;
	private void alert_sound_effect() {
		String[] sound_effect_list = {
				context.getString(R.string.MainFragmentActivity08),
				context.getString(R.string.MainFragmentActivity09)};
		new AlertDialog.Builder(context)
		.setTitle(R.string.MainFragmentActivity07)
		.setSingleChoiceItems(sound_effect_list, PreferenceUtil.getIntSharedData(context, PreferenceUtil.PREF_SOUND_EFFECT_SET, SOUND_EFFECT_SET), new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which){
				dialog.dismiss();
				if(which == 0){
					SOUND_EFFECT_SET = 0;
					PreferenceUtil.setBooleanSharedData(context, PreferenceUtil.PREF_SOUND_EFFECT, true);
				}else if(which == 1){
					SOUND_EFFECT_SET = 1;
					PreferenceUtil.setBooleanSharedData(context, PreferenceUtil.PREF_SOUND_EFFECT, false);
				}
				PreferenceUtil.setIntSharedData(context, PreferenceUtil.PREF_SOUND_EFFECT_SET, SOUND_EFFECT_SET);
			}
		}).show();	
	}
	
	Handler handler = new Handler();
	public void exit_handler(){
    	handler = new Handler(){
    		@Override
    		public void handleMessage(Message msg) {
    			if(msg.what == 0){
    				flag = false;
    			}
    		}
    	};
    }
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if(keyCode == KeyEvent.KEYCODE_BACK){
			 if(!flag){
				 Toast.makeText(context, context.getString(R.string.MainFragmentActivity13) , Toast.LENGTH_SHORT).show();
				 flag = true;
				 handler.sendEmptyMessageDelayed(0, 2000);
			 return false;
			 }else{
				 try{
					 handler.postDelayed(new Runnable() {
						 @Override
						 public void run() {
							 PreferenceUtil.setBooleanSharedData(context, PreferenceUtil.PREF_AD_VIEW, true);
							 finish();
						 }
					 },0);
						 
				 }catch(Exception e){
				 }
			 }
            return false;	 
		 }
		return super.onKeyDown(keyCode, event);
	}
	
	
	//** BannerAd 이벤트들 *************
	@Override
	public void onClickedAd(String arg0, com.admixer.AdView arg1) {
	}

	@Override
	public void onFailedToReceiveAd(int arg0, String arg1,
			com.admixer.AdView arg2) {
	}

	@Override
	public void onReceivedAd(String arg0, com.admixer.AdView arg1) {
	}	
	
	//** CustomPopup 이벤트들 *************
	@Override
	public void onCloseCustomPopup(String arg0) {
	}

	@Override
	public void onHasNoCustomPopup() {
	}

	@Override
	public void onShowCustomPopup(String arg0) {
	}

	@Override
	public void onStartedCustomPopup() {
	}

	@Override
	public void onWillCloseCustomPopup(String arg0) {
	}

	@Override
	public void onWillShowCustomPopup(String arg0) {
	}	
	
	//** InterstitialAd 이벤트들 *************
	@Override
	public void onInterstitialAdClosed(InterstitialAd arg0) {
		interstialAd = null;
	}

	@Override
	public void onInterstitialAdFailedToReceive(int arg0, String arg1, InterstitialAd arg2) {
		interstialAd = null;
		AlertDialog.Builder alertdialog = new AlertDialog.Builder(this);
		alertdialog.setIcon(R.drawable.icon64);
		alertdialog.setTitle(context.getString(R.string.app_name)); 
		alertdialog.setMessage(context.getString(R.string.txt_finish_ment)); 
		alertdialog.setCancelable(false);
		alertdialog.setPositiveButton(context.getString(R.string.txt_finish_yes), new DialogInterface.OnClickListener() { 
            public void onClick(DialogInterface dialog, int which) { 
        		PreferenceUtil.setIntSharedData(context, PreferenceUtil.PREF_TAB_SELECTED, 0);
            	finish();
            } 
        });
        alertdialog.setNegativeButton(context.getString(R.string.txt_finish_no), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).create().show(); 
	}

	@Override
	public void onInterstitialAdReceived(String arg0, InterstitialAd arg1) {
		interstialAd = null;
	}

	@Override
	public void onInterstitialAdShown(String arg0, InterstitialAd arg1) {
	}

	@Override
	public void onLeftClicked(String arg0, InterstitialAd arg1) {
	}

	@Override
	public void onRightClicked(String arg0, InterstitialAd arg1) {
		PreferenceUtil.setIntSharedData(context, PreferenceUtil.PREF_TAB_SELECTED, 0);
    	finish();
	}
}
