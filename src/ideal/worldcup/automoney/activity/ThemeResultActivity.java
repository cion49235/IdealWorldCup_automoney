package ideal.worldcup.automoney.activity;

import java.util.ArrayList;

import com.admixer.AdAdapter;
import com.admixer.AdInfo;
import com.admixer.AdMixerManager;
import com.admixer.AdViewListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ideal.worldcup.automoney.MainFragmentActivity;
import ideal.worldcup.automoney.R;
import ideal.worldcup.automoney.cms.BaseResponse;
import ideal.worldcup.automoney.cms.ExpandableHeightListView;
import ideal.worldcup.automoney.cms.ThemeContestItem;
import ideal.worldcup.automoney.cms.ThemeContestListItem;
import ideal.worldcup.automoney.cms.ThemeContestResult;
import ideal.worldcup.automoney.fragment.Fragment_Popularity.FragmentAdapter;
import ideal.worldcup.automoney.util.JsonClient;
import ideal.worldcup.automoney.util.LogHelper;
import ideal.worldcup.automoney.util.UtilHelper;

public class ThemeResultActivity extends Activity implements AdViewListener {
	private int ct_seq;
    private String ct_type;
    private int reply_cnt;
    private JsonClient client;
    private AlertDialog alertDialog;
    private TextView tvContestTitle;
    private TextView tvCntParticipate;
    private ExpandableHeightListView lvThemeItems;
    private Adapter adapter;
    private LinearLayout lvThemeOthers;
    private Context context;
    private ArrayList<ThemeContestItem> list;
    private boolean backpressed = false;
    private RelativeLayout ad_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_result);
        context = this;
        AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMIXER, "emd75whx");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMOB, "ca-app-pub-4637651494513698/7149430504");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMOB_FULL, "ca-app-pub-4637651494513698/5782230713");
		addBannerView();
        getdata();
        init_ui();
        clear_list();
        getThemeResult();
    }
    
    public void addBannerView() {
    	AdInfo adInfo = new AdInfo("emd75whx");
    	adInfo.setTestMode(false);
        com.admixer.AdView adView = new com.admixer.AdView(this);
        adView.setAdInfo(adInfo, this);
        adView.setAdViewListener(this);
        ad_layout = (RelativeLayout)findViewById(R.id.ad_layout);
        if(ad_layout != null){
        	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            ad_layout.addView(adView, params);	
        }
    }
    
    private void init_ui() {
    	client = JsonClient.getInstance(this);
   	    tvContestTitle = (TextView)findViewById(R.id.tvContestTitle);
   	    tvCntParticipate = (TextView)findViewById(R.id.tvCntParticipate);
   	    lvThemeItems = (ExpandableHeightListView)findViewById(R.id.lvThemeItems);
    }
    
    private void clear_list(){
        list = new ArrayList<ThemeContestItem>();
        list.clear();
    }
    
    private void getdata() {
    	Intent fromIntent = getIntent();
        ct_seq = fromIntent.getIntExtra("CT_SEQ", 0);
        backpressed = fromIntent.getBooleanExtra("BACKPRESSED", false);
        Log.i("dsu", "ct_seq : " + ct_seq);
    }

    public void getThemeResult() {
        /** 통신처리 */
        client.init(R.string.contest_theme_result_sel, context.getString(R.string.app_uid), ct_seq);
        client.post(new BaseResponse<ThemeContestResult>(context) {
            @Override
            public void onResponse(ThemeContestResult response) {
                LogHelper.debug(this, response.toString());
                Log.i("dsu", "대전결과1 : " + response.toString() + "\nRespnse코드 : " + response.code);
                if (!response.code.equals("000")) {
//                    Toast.makeText(ThemeResultActivity.this, response.msg, Toast.LENGTH_SHORT).show();
                    return;
                }
                ct_type = response.ct_type.trim();
                tvContestTitle.setText(response.ct_title);
                tvCntParticipate.setText(context.getString(R.string.ThemeResultActivity04)+UtilHelper.getInstance().getNumberFormat(response.ct_cnt_participate)+context.getString(R.string.ThemeResultActivity05));
                
                if (response.list_item != null) {
                    for (int i = 0; i < response.list_item.size(); i++) {
                    	ThemeContestItem tbItem = response.list_item.get(i);
                    	Log.i("dsu", "대전결과2 : " + response.list_item.get(i).cti_name);
						list.add(tbItem);
                    }
                    display_list();
                }
            }
        });
    }
    
    private void display_list(){
    	adapter = new Adapter();
    	lvThemeItems.setAdapter(adapter);
    }

    public class Adapter extends BaseAdapter implements OnClickListener{
		public Adapter() {
		}
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			try{
				if(view == null){
					LayoutInflater layoutInflater = LayoutInflater.from(context);
					view = layoutInflater.inflate(R.layout.activity_theme_result_listrow, parent, false);
					ViewHolder holder = new ViewHolder();	
					holder.tvRanking = (TextView)view.findViewById(R.id.tvRanking);
					holder.ivItemImg = (ImageView)view.findViewById(R.id.ivItemImg);
					holder.tvItemName = (TextView)view.findViewById(R.id.tvItemName);
					holder.tvCnt = (TextView)view.findViewById(R.id.tvCnt);
					view.setTag(holder);
				}
				final ViewHolder holder = (ViewHolder)view.getTag();
				
				UtilHelper.getInstance().loadImage(ThemeResultActivity.this, holder.ivItemImg, list.get(position).cti_file_name);				
				holder.tvRanking.setText(String.valueOf(list.get(position).rnum));
				holder.tvItemName.setText(list.get(position).cti_name);
				holder.tvCnt.setText("("+list.get(position).cti_win_cnt+context.getString(R.string.ThemeResultActivity06));
			}catch (Exception e) {
			}
			return view;
		}
		
		@Override
		public void onClick(View view) {
		}
		
	}
    
    private class ViewHolder {
    	public TextView tvRanking;
    	public ImageView ivItemImg;
		public TextView tvItemName;
		public TextView tvCnt;
		
	}
    
    @Override
    public void onBackPressed() {
    	super.onBackPressed();
    	finish();
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
    
}