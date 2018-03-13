package ideal.worldcup.automoney.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.squareup.picasso.Picasso;

import android.accounts.Account;
import android.accounts.OnAccountsUpdateListener;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import ideal.worldcup.automoney.R;
import ideal.worldcup.automoney.activity.ThemeResultActivity;
import ideal.worldcup.automoney.activity.ThemeStartActivity;
import ideal.worldcup.automoney.cms.BaseResponse;
import ideal.worldcup.automoney.cms.ThemeContestList;
import ideal.worldcup.automoney.cms.ThemeContestListItem;
import ideal.worldcup.automoney.fragment.Fragment_Event.Content_status_Async;
import ideal.worldcup.automoney.util.DBopenHelper_Result;
import ideal.worldcup.automoney.util.JsonClient;
import ideal.worldcup.automoney.util.LogHelper;
import ideal.worldcup.automoney.util.PreferenceUtil;
import ideal.worldcup.automoney.util.UtilHelper;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class Fragment_Special extends Fragment implements OnScrollListener {
	private int pageno = 1;      
    private int totalPage;     
	private LinearLayout layout_gridview, layout_nodata;
	private GridView list_gridview;
	private ArrayList<ThemeContestListItem> list;
	private JsonClient client;
	private boolean mLockListView;
	private FragmentAdapter adapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_special,container, false);
		init_ui(view);
		pageno = 1;
		clear_list();
		content_status_async = new Content_status_Async();
		content_status_async.execute();
		return view;
	
	}
	
	private void init_ui(View view) {
		client = JsonClient.getInstance(getActivity());
		layout_gridview = (LinearLayout)view.findViewById(R.id.layout_gridview);
		layout_nodata = (LinearLayout)view.findViewById(R.id.layout_nodata);
		list_gridview = (GridView)view.findViewById(R.id.list_gridview);
		list_gridview.setOnScrollListener(this);
	}
	
	private void clear_list(){
        list = new ArrayList<ThemeContestListItem>();
        list.clear();
    }
	
	private Content_status_Async content_status_async = null;
	private String content_status;
    public class Content_status_Async extends AsyncTask<String, Integer, String> {
        int id;
        public Content_status_Async(){
        }
        @Override
        protected String doInBackground(String... params) {
            String sTag;
            try{
                String str = "http://cion49235.cafe24.com/vs_content_status/content_status.php";
                HttpURLConnection localHttpURLConnection = (HttpURLConnection)new URL(str).openConnection();
                HttpURLConnection.setFollowRedirects(false);
                localHttpURLConnection.setConnectTimeout(15000);
                localHttpURLConnection.setReadTimeout(15000);
                localHttpURLConnection.setRequestMethod("GET");
                localHttpURLConnection.connect();
                InputStream inputStream = new URL(str).openStream(); //open Stream을 사용하여 InputStream을 생성합니다.
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(inputStream, "EUC-KR"); //euc-kr로 언어를 설정합니다. utf-8로 하니깐 깨지더군요
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                    }else if (eventType == XmlPullParser.END_DOCUMENT) {
                    }else if (eventType == XmlPullParser.START_TAG){
                        sTag = xpp.getName();
                        if(sTag.equals("Content")){
                            id = Integer.parseInt(xpp.getAttributeValue(null, "id") + "");
                        }else if(sTag.equals("content_status")){
                        	content_status = xpp.nextText()+"";
                        }
                    } else if (eventType == XmlPullParser.END_TAG){
                        sTag = xpp.getName();
                        if(sTag.equals("Finish")){

                        }
                    } else if (eventType == XmlPullParser.TEXT) {
                    }
                    eventType = xpp.next();
                }
            }
            catch (SocketTimeoutException localSocketTimeoutException)
            {
            }
            catch (ClientProtocolException localClientProtocolException)
            {
            }
            catch (IOException localIOException)
            {
            }
            catch (Resources.NotFoundException localNotFoundException)
            {
            }
            catch (NullPointerException NullPointerException)
            {
            }
            catch (Exception e)
            {
            }
            return content_status;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String content_status) {
            super.onPostExecute(content_status);
            try{
                Log.i("dsu", "content_status : " + content_status);
                if(content_status != null) {
                	PreferenceUtil.setStringSharedData(getActivity(), PreferenceUtil.PREF_CONTENT_STATUS, content_status);
                	getCpThemeList(content_status);
                }
            }catch(NullPointerException e){
            }
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }
	
	private void getCpThemeList(final String content_status) {
		mLockListView = true;
        client.init(R.string.contest_theme_list_by_me, pageno, 20, getString(R.string.app_uid), "B");
        client.post(new BaseResponse<ThemeContestList>(getActivity()) {
            @Override
            public void onResponse(ThemeContestList response) {
                LogHelper.debug(this, response.toString());
                Log.i("dsu","code : " + response.code);
                if (!response.code.equals("000")) {
//                    Toast.makeText(getActivity(), response.msg, Toast.LENGTH_SHORT).show();
                    return;
                }
                totalPage = ((response.total_cnt-1)/10)+1;
                if ( response.list != null ) {
//                	for (int i = 0; i < response.list.size(); i++) {
                	for (int i =  response.list.size()-1; i>=0; i--) {
                		ThemeContestListItem tcItem = response.list.get(i);
                		if(content_status.equals("Y")) {
                			if(tcItem.ct_title.indexOf("모두의") != -1 || tcItem.ct_title.indexOf("꿀티비") != -1) {
                    		}else {
                    			list.add(tcItem);
                    		}
                		}else {
                			if(tcItem.ct_title.indexOf("대학교 강의실") != -1 || tcItem.ct_title.indexOf("이민 가고 싶은") != -1
                			|| tcItem.ct_title.indexOf("혼술 안주 편의점") != -1 || tcItem.ct_title.indexOf("점심메뉴") != -1
                			|| tcItem.ct_title.indexOf("무인도에 간다면") != -1 || tcItem.ct_title.indexOf("15년동안 한가지만") != -1
                			|| tcItem.ct_title.indexOf("갖고싶은 마법의") != -1 || tcItem.ct_title.indexOf("최고의 소주 안주") != -1
                			|| tcItem.ct_title.indexOf("죽기전에 가봐야 하는") != -1 || tcItem.ct_title.indexOf("오늘의 연애운") != -1
                			|| tcItem.ct_title.indexOf("가장 비호감") != -1 || tcItem.ct_title.indexOf("회식음식으로") != -1
                			|| tcItem.ct_title.indexOf("워크샵으로") != -1 || tcItem.ct_title.indexOf("나에게 동물의 능력") != -1
                			|| tcItem.ct_title.indexOf("연휴에 떠나고") != -1) {
                				list.add(tcItem);
                			}
                		}
                	}
                	display_list();
                    mLockListView = false;
                }else {
//                	Toast.makeText(getActivity(), getString(R.string.MainFragmentActivity05), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
	
	private void display_list(){
		if(pageno < 2){
			adapter = new FragmentAdapter();
	        list_gridview.setAdapter(adapter);
		}else {
			adapter.notifyDataSetChanged();
		}
	}
	
	public class FragmentAdapter extends BaseAdapter implements OnClickListener{
		int ct_seq = -1;
	    DBopenHelper_Result mydb;
		public FragmentAdapter() {
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
					LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
					view = layoutInflater.inflate(R.layout.fragment_special_listrow, parent, false);
					ViewHolder holder = new ViewHolder();	
					holder.layout_iv = (LinearLayout)view.findViewById(R.id.layout_iv);
					holder.ivLeft = (ImageView)view.findViewById(R.id.ivLeft);
					holder.ivRight = (ImageView)view.findViewById(R.id.ivRight);
					holder.tv_title = (TextView)view.findViewById(R.id.tv_title);
					holder.tvTargetCnt = (TextView)view.findViewById(R.id.tvTargetCnt);
					holder.btStart = (ImageButton)view.findViewById(R.id.btStart);
					holder.btResult = (ImageButton)view.findViewById(R.id.btResult);
					
					holder.layout_iv.setFocusable(false);
					holder.ivLeft.setFocusable(false);
					holder.ivRight.setFocusable(false);
					holder.btStart.setFocusable(false);
					holder.btResult.setSelected(false);
					view.setTag(holder);
				}
				final ViewHolder holder = (ViewHolder)view.getTag();
				
				/*Picasso.with(getActivity())
                .load("http://dbdbdeep.com" + list.get(position).file_name_1)
                .placeholder(R.drawable.main_vs_picture_im_left)
                .error(R.drawable.main_vs_picture_im_left)
                .into(holder.ivLeft);
				
				Picasso.with(getActivity())
                .load("http://dbdbdeep.com" + list.get(position).file_name_2)
                .placeholder(R.drawable.main_vs_picture_im_right)
                .error(R.drawable.main_vs_picture_im_right)
                .into(holder.ivRight);*/
				
				UtilHelper.getInstance().loadImage(getActivity(), holder.ivLeft, list.get(position).file_name_1);
	            UtilHelper.getInstance().loadImage(getActivity(), holder.ivRight, list.get(position).file_name_2);
				
				holder.tv_title.setText(list.get(position).ct_title);
				
				String byNickname = "";
	            if ( list.get(position).nickname != null && list.get(position).nickname.length() > 0 ) {
	                byNickname = "- by " + list.get(position).nickname;
	            }
				holder.tvTargetCnt.setText(UtilHelper.getInstance().getNumberFormat(list.get(position).ct_cnt_participate)+getString(R.string.FragmentPopularity02) +" (" +list.get(position).ct_cnt_target+getString(R.string.FragmentPopularity03) + ")");
				
				holder.layout_iv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						if(list.get(position).ct_status.equals("N")){
							Toast.makeText(getActivity(), getActivity().getString(R.string.FragmentPopularity04), Toast.LENGTH_SHORT).show();
						}else if(list.get(position).cnt_my_participate > 0 && list.get(position).ct_once_participate.toString().trim().equals("Y")) {
							Toast.makeText(getActivity(), getActivity().getString(R.string.FragmentPopularity05), Toast.LENGTH_SHORT).show();
						}else{
							Intent intent = new Intent(getActivity(), ThemeStartActivity.class);
							intent.putExtra("CT_SEQ", list.get(position).ct_seq);
							startActivity(intent);
						}
					}
				});
				
				holder.btStart.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						if(list.get(position).ct_status.equals("N")){
							Toast.makeText(getActivity(), getActivity().getString(R.string.FragmentPopularity04), Toast.LENGTH_SHORT).show();
						}else if(list.get(position).cnt_my_participate > 0 && list.get(position).ct_once_participate.toString().trim().equals("Y")) {
							Toast.makeText(getActivity(), getActivity().getString(R.string.FragmentPopularity05), Toast.LENGTH_SHORT).show();
						}else{
							Intent intent = new Intent(getActivity(), ThemeStartActivity.class);
							intent.putExtra("CT_SEQ", list.get(position).ct_seq);
							startActivity(intent);
						}
					}
				});
				
				try{
		    		mydb = new DBopenHelper_Result(getActivity());
					Cursor cursor = mydb.getReadableDatabase().rawQuery(
							"select * from result_list where ct_seq = '"+list.get(position).ct_seq+"'", null);
					if(null != cursor && cursor.moveToFirst()){
						ct_seq = cursor.getInt(cursor.getColumnIndex("ct_seq"));
					}else{
						ct_seq = -1;
					}
					if(ct_seq == -1){
						holder.btResult.setImageResource(R.drawable.thema_list_bt_02_over);
					}else {
						holder.btResult.setImageResource(R.drawable.thema_list_bt_02);
					}
				}catch (Exception e) {
				}finally{
					if(mydb != null) {
						 mydb.close();
					}
				}
				
				holder.btResult.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						try{
				    		mydb = new DBopenHelper_Result(getActivity());
							Cursor cursor = mydb.getReadableDatabase().rawQuery(
									"select * from result_list where ct_seq = '"+list.get(position).ct_seq+"'", null);
							if(null != cursor && cursor.moveToFirst()){
								ct_seq = cursor.getInt(cursor.getColumnIndex("ct_seq"));
							}else{
								ct_seq = -1;
							}
							if(ct_seq == -1){
								Toast.makeText(getActivity(), getActivity().getString(R.string.FragmentPopularity06), Toast.LENGTH_SHORT).show();
							}else {
								Intent intent = new Intent(getActivity(), ThemeResultActivity.class);
		                        intent.putExtra("CT_SEQ", list.get(position).ct_seq);
		                        intent.putExtra("BACKPRESSED", true);
		                        startActivity(intent);
							}
						}catch (Exception e) {
						}finally{
							if(mydb != null) {
								 mydb.close();
							}
						}
						
					}
				});
				
			}catch (Exception e) {
			}
			return view;
		}
		
		@Override
		public void onClick(View view) {
		}
		
	}
	
	private class ViewHolder {
		public LinearLayout layout_iv;
		public ImageView ivLeft;
		public ImageView ivRight;
		public TextView tv_title;
		public TextView tvTargetCnt;
		public ImageButton btStart;
		public ImageButton btResult;
		
	}

	@Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		int count = totalItemCount - visibleItemCount;
        if(firstVisibleItem >= count && totalItemCount != 0 && mLockListView == false ) {
        	if(PreferenceUtil.getStringSharedData(getActivity(), PreferenceUtil.PREF_CONTENT_STATUS, "Y").equals("Y")) {
        	if (pageno < totalPage) {
                pageno++;
                getCpThemeList(PreferenceUtil.getStringSharedData(getActivity(), PreferenceUtil.PREF_CONTENT_STATUS, "Y"));
            }
        	}else {
        		if (pageno < 3) {
                    pageno++;
                    getCpThemeList(PreferenceUtil.getStringSharedData(getActivity(), PreferenceUtil.PREF_CONTENT_STATUS, "Y"));
                }
        	}
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState){
    	if(scrollState == OnScrollListener.SCROLL_STATE_FLING){
    		list_gridview.setFastScrollEnabled(true);
		}else{
			list_gridview.setFastScrollEnabled(false);
		}
    }
	
}
