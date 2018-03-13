package ideal.worldcup.automoney.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.admixer.AdAdapter;
import com.admixer.AdInfo;
import com.admixer.AdMixerManager;
import com.admixer.AdView.AdAnimation;
import com.admixer.AdViewListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.loopj.android.http.RequestParams;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import ideal.worldcup.automoney.MainFragmentActivity;
import ideal.worldcup.automoney.R;
import ideal.worldcup.automoney.cms.BaseModel;
import ideal.worldcup.automoney.cms.BaseResponse;
import ideal.worldcup.automoney.cms.ThemeContestGeneralIns;
import ideal.worldcup.automoney.util.JsonClient;
import ideal.worldcup.automoney.util.LogHelper;
import ideal.worldcup.automoney.util.PreferenceUtil;
import ideal.worldcup.automoney.util.UtilHelper;
import kr.co.inno.autocash.AutoLayoutGoogleActivity;
import kr.co.inno.autocash.service.AutoLoginServiceActivity;
import kr.co.inno.autocash.service.AutoServiceActivity;

public class GeneralThemeCreateActivity extends Activity implements View.OnClickListener, View.OnLongClickListener, View.OnDragListener, AdViewListener{
    // 최대 선택 가능한 사진 갯수
    int max_photo_count = 0;
    // 현재 선택한 사진 갯수
    int now_photo_count = 0;
    // 업로드 시작전 업로드 된 파일의 수
    int upload_count = 0;

    // 개별 이미지 클릭시, 클릭된 객체
    View currentImageView;
    // 사진 첨부 모드 값
    boolean multiChoice = true;
    // 출발지 태그
    String fromTag;

    // drag&drop 시 영역 밖으로 끌었는지를 체크하기 위한 boolean.
    boolean dragEnabled = false;

    // 등록중인가?
    boolean isUploading = false;

    JsonClient client;
    
    class ThemeItem implements OnClickListener {
        LinearLayout container;
        ImageView imageView;
        TextView textView;
        boolean imageSetup;
        String filePath;
        
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
    }

    ThemeItem[] themeItems;
    int[] themeItemsSort;
//    HashMap itemMap = new HashMap();
    Context context;
    LinearLayout llWraper;
    EditText editText;
    Button spinner;
    Button btTotalUpload;
    Button btCancel;
    private int SDK_INT = android.os.Build.VERSION.SDK_INT;
    private RelativeLayout ad_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_theme_create);
        context = this;
        AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMIXER, "emd75whx");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMOB, "ca-app-pub-4637651494513698/7149430504");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMOB_FULL, "ca-app-pub-4637651494513698/5782230713");
		addBannerView();
        init_ui();
//        check_popup();
    }
    
    @Override
    protected void onRestart() {
    	super.onRestart();
//    	check_popup();
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
    
    View view;
    private void checkPermission(View v) {
		if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                // Explain to the user why we need to write the permission.
//            	Return_AlertShow(context.getString(R.string.permission_cancel));
            }
            requestPermissions(new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            view = v;
        } else {
        	multiChoice = false;
	        currentImageView = v;
	        view = v;
	        pick_picture();		
        }
	}
    
    public void Return_AlertShow(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage(msg);
        builder.setNeutralButton(context.getString(R.string.txt_finish_yes), new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton){
            	PreferenceUtil.setBooleanSharedData(context, PreferenceUtil.PREF_AD_VIEW, true);
                finish();
            	dialog.dismiss();
            }
        });
        AlertDialog myAlertDialog = builder.create();
        myAlertDialog.show();
    }
    
    
    @Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
	    switch (requestCode) {
	        case 0:
	            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
	                    && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
	            	multiChoice = false;
			        currentImageView = view;
			        pick_picture();		
	            } else {
	            	Toast.makeText(context, context.getString(R.string.permission_cancel), Toast.LENGTH_LONG).show();
	            	finish();
	            }
	            break;
	    	}
		}
    
    private void init_ui() {
    	client = JsonClient.getInstance(this);
    	llWraper = (LinearLayout)findViewById(R.id.llWrapper);
	    editText = (EditText)findViewById(R.id.editText);
	    spinner = (Button)findViewById(R.id.spinner);
	    spinner.setText(context.getString(R.string.MainFragmentActivity10));
	    spinner.setFocusable(false);
	    spinner.setOnClickListener(this);
	    btTotalUpload = (Button)findViewById(R.id.btTotalUpload);
	    btTotalUpload.setOnClickListener(this);
	    btCancel = (Button)findViewById(R.id.btCancel);
	    btCancel.setOnClickListener(this);
	    
	    selected_spinner(0);
    }
    
    
    @Override
    public void onClick(View view) {
    	if(view == btCancel) {
    		removeDir(path);
    		finish();
    	}else if(view == spinner) {
    		alert_selected_spinner();
    	}else if(view == btTotalUpload) {
    		/** 제목 입력 여부 검사 */
            String ct_title = editText.getText().toString().trim();
            if (ct_title.equals("")) {
//                LoadingDialogHelper.getInstance(GeneralThemeCreateActivity.this).hide();
                Toast.makeText(this, context.getString(R.string.GeneralThemeCreateActivity03), Toast.LENGTH_SHORT).show();
                return;
            }
            
         // 등록된 사진과 이름이 완전한지 검사
//          for (ThemeItem item : themeItems) {

          int cnt = 1;
          for (int itemIdx : themeItemsSort) {
              if (!themeItems[itemIdx].imageSetup) {
//                  LoadingDialogHelper.getInstance(GeneralThemeCreateActivity.this).hide();
                  Toast.makeText(this, cnt + context.getString(R.string.GeneralThemeCreateActivity04), Toast.LENGTH_SHORT).show();
                  return;
              }

              if (themeItems[itemIdx].textView.getText().toString().trim().equals("")) {
//                  LoadingDialogHelper.getInstance(GeneralThemeCreateActivity.this).hide();
            	  Toast.makeText(this, cnt + context.getString(R.string.GeneralThemeCreateActivity05), Toast.LENGTH_SHORT).show();
                  return;
              }

              cnt += 1;
          }

          if ( isUploading ) {
        	  Toast.makeText(this, cnt + context.getString(R.string.GeneralThemeCreateActivity06), Toast.LENGTH_SHORT).show();
              return;
          } else {
        	  Toast.makeText(this, cnt + context.getString(R.string.GeneralThemeCreateActivity06), Toast.LENGTH_SHORT).show();
              isUploading = true;
          }

          String title = editText.getText().toString().trim();
          client.init(R.string.contest_theme_general_ins, context.getString(R.string.app_uid), title, max_photo_count);
          client.post(new BaseResponse<ThemeContestGeneralIns>(context) {
              @Override
              public void onResponse(ThemeContestGeneralIns response) {
                  if (!response.code.equals("000")) {
//                      LoadingDialogHelper.getInstance(GeneralThemeCreateActivity.this).hide();
//                      Toast.makeText(getApplicationContext(), response.msg, Toast.LENGTH_SHORT).show();
                      isUploading = false;
                      return;
                  }

                  uploadPhoto(response.ct_seq);
              }
          });
    	}
    }
    
    private void alert_selected_spinner() {
		String[] selected_list = {
				context.getString(R.string.MainFragmentActivity10),
				context.getString(R.string.MainFragmentActivity11),
				context.getString(R.string.MainFragmentActivity12)};
		new AlertDialog.Builder(context)
		.setTitle(R.string.MainFragmentActivity07)
		.setItems(selected_list, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which){
				dialog.dismiss();
				if(which == 0){
					selected_spinner(0);
					spinner.setText(context.getString(R.string.MainFragmentActivity10));
				}else if(which == 1){
					selected_spinner(1);
					spinner.setText(context.getString(R.string.MainFragmentActivity11));
				}else if(which == 2){
					selected_spinner(2);
					spinner.setText(context.getString(R.string.MainFragmentActivity12));
				}
			}
		}).show();	
	}
    
    private void selected_spinner(int index) {
    	int count = 1;

        for (int i = 0; i < index; i++) {
            count *= 2;
        }

        // 사진의 최대 선택 갯수
        max_photo_count = count * 4;
        // 사진은 처음부터 다시 선택해야 하므로 현재 선택 수량도 초기화
        now_photo_count = 0;

        llWraper.removeAllViews();

        if (themeItems != null) {
            themeItems = null;
        }

        themeItems = new ThemeItem[count * 4];
        themeItemsSort = new int[count * 4];

        LayoutInflater inflater = getLayoutInflater();

        // 한 줄의 Container에 대한 id값들..
        int[] containers = {R.id.llContainer1, R.id.llContainer2, R.id.llContainer3, R.id.llContainer4};

        for (int i = 0; i < count; i++) {
            View v = inflater.inflate(R.layout.inc_general_theme_create_container, null);
            llWraper.addView(v);

            ((TextView) v.findViewById(R.id.tvRoundLeft)).setText(String.valueOf((i + 1) * 2 - 1) + "R");
            ((TextView) v.findViewById(R.id.tvRoundRight)).setText(String.valueOf((i + 1) * 2) + "R");

            for (int j = 0; j < 4; j++) {
                String tagName = "itemContainer" + (i * 4 + j);

                View item = inflater.inflate(R.layout.inc_general_theme_create_item, null);
                LinearLayout itemContainer = (LinearLayout) v.findViewById(containers[j]);
                itemContainer.addView(item);
                itemContainer.setOnDragListener(this);
                itemContainer.setTag(tagName);

                themeItems[i * 4 + j] = new ThemeItem();
//                themeItems[i * 4 + j].index = i * 4 + j + 1;
                themeItems[i * 4 + j].container = (LinearLayout) item;
                themeItems[i * 4 + j].imageView = (ImageView) item.findViewById(R.id.ivPhoto);
                themeItems[i * 4 + j].textView = (TextView) item.findViewById(R.id.tvName);
                themeItems[i * 4 + j].imageSetup = false;

                setName(themeItems[i * 4 + j].textView);

                // 현재 클래스 안에서 onClick을 찾아서 호출한다.
                themeItems[i * 4 + j].imageView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
				        if (SDK_INT >= Build.VERSION_CODES.M){ 
				    		checkPermission(v);
				    	}else {
				    		multiChoice = false;
					        currentImageView = v;
					        pick_picture();				    		
				    	}
					}
				});

                // 이동시킬 View에게 드래그를 구현하기 위해서 태그 설정
                item.setTag("Item" + (i * 4 + j));

                // 롱클릭 이벤트 연결
                themeItems[i * 4 + j].imageView.setOnLongClickListener(this);
                themeItems[i * 4 + j].textView.setOnLongClickListener(this);
                item.setOnLongClickListener(this);

                themeItemsSort[i * 4 + j] = i * 4 + j;
            }
        }
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode != RESULT_OK){
			String tmp_photo = path + filename;
			if(tmp_photo != null);
			 File file = new File(tmp_photo);
			 if(file.exists()){
				 file.delete();
			 }
			return;
		}

		switch(requestCode)
		{
		case PICK_FROM_ALBUM:
		{
			uri = data.getData();
			File original_file = getImageFile(uri);
			uri = createSaveCropFile();
			File cpoy_file = new File(uri.getPath()); 
			copyFile(original_file , cpoy_file);
			
			int i = 0;
            for (; i < themeItems.length; i++) {
                if (themeItems[i].imageView == currentImageView) {
                    break;
                }
            }
            UtilHelper.getInstance().loadImage(context, themeItems[i].imageView, uri.toString(), 360, 480);
            themeItems[i].filePath = uri.getPath();

            // 신규 이미지라면 카운트 정보 갱신
            if (!themeItems[i].imageSetup) {
                now_photo_count++;
                themeItems[i].imageSetup = true;
            }
			
		}

		case PICK_FROM_CAMERA:
		{
			int i = 0;
            for (; i < themeItems.length; i++) {
                if (themeItems[i].imageView == currentImageView) {
                    break;
                }
            }
			UtilHelper.getInstance().loadImage(context, themeItems[i].imageView, uri.toString(), 360, 480);
			themeItems[i].filePath = uri.getPath();

            // 신규 이미지라면 카운트 정보 갱신
            if (!themeItems[i].imageSetup) {
                now_photo_count++;
                themeItems[i].imageSetup = true;
            }
			break;
		}
		}
	}
    
    private File getImageFile(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		if (uri == null) {
			uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		}

		Cursor mCursor = getContentResolver().query(uri, projection, null, null, 
				MediaStore.Images.Media.DATE_MODIFIED + " desc");
		if(mCursor == null || mCursor.getCount() < 1) {
			return null; // no cursor or no record
		}
		int column_index = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		mCursor.moveToFirst();

		String path = mCursor.getString(column_index);

		if (mCursor !=null ) {
			mCursor.close();
			mCursor = null;
		}

		return new File(path);
	}
    
    public static boolean copyFile(File srcFile, File destFile) {
		boolean result = false;
		try {
			InputStream in = new FileInputStream(srcFile);
			try {
				result = copyToFile(in, destFile);
			} finally  {
				in.close();
			}
		} catch (IOException e) {
			result = false;
		}
		return result;
	}
    
    private static boolean copyToFile(InputStream inputStream, File destFile) {
		try {
			OutputStream out = new FileOutputStream(destFile);
			try {
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) >= 0) {
					out.write(buffer, 0, bytesRead);
				}
			} finally {
				out.close();
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}
    
    private final int PICK_FROM_CAMERA = 0;
	private final int PICK_FROM_ALBUM = 1;
	private  Uri uri;
    private void pick_picture() {
    	String[] items ={context.getString(R.string.GeneralThemeCreateActivity01),
				context.getString(R.string.GeneralThemeCreateActivity02)};
		new AlertDialog.Builder(context)
		.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which == 0){//카메라
					File file = new File(path);
	                file.mkdirs();
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					uri = createSaveCropFile();
					intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
					startActivityForResult(intent, PICK_FROM_CAMERA);
				}else if(which == 1){//갤러리
					
					File file = new File(path);
	                file.mkdirs();
					
					Intent intent = new Intent(Intent.ACTION_PICK);
					intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
					startActivityForResult(intent, PICK_FROM_ALBUM);
					
				}
			}
		}).show();
    }
    
    public static String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/theme_vs/";
    private String filename;
    private Uri createSaveCropFile(){
		Uri uri;
		filename = String.valueOf(System.currentTimeMillis()) + ".jpg";
		uri = Uri.fromFile(new File(path, filename));
		return uri;
	}
    
    public void setName(final TextView textView) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder
                        = new AlertDialog.Builder(GeneralThemeCreateActivity.this);
                builder.setTitle(context.getString(R.string.picturetitle02));

                final View view = getLayoutInflater().inflate(R.layout.view_general_theme_create_name_prompt, null);
                builder.setView(view);

                builder.setPositiveButton(context.getString(R.string.GeneralThemeCreateActivity08), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) view.findViewById(R.id.editText);
                        String input = editText.getText().toString().trim();
                        if (input.equals("")) {
                            Toast.makeText(context,context.getString(R.string.GeneralThemeCreateActivity09), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        textView.setText(input);
                        UtilHelper.getInstance().downKeyboard(getApplicationContext(), editText);
                    }
                });

                builder.setNegativeButton(context.getString(R.string.GeneralThemeCreateActivity10), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    	UtilHelper.getInstance().downKeyboard(getApplicationContext(), editText);
                    }
                });

                builder.create();
                builder.show();

                UtilHelper.getInstance().upKeyboard(context);
            }
        });
    }


    void uploadPhoto(int seq) {
        final int ct_seq = seq;
        String prefix = getString(R.string.url_image_upload_prefix);
        String apiUrl = getString(R.string.contest_theme_general_item_ins);
        int p = apiUrl.indexOf("?");
        if (p > -1) {
            apiUrl = apiUrl.substring(0, p);
        }

        final String url = prefix + apiUrl;

        // 업로드 시작전 업로드 된 파일의 수를 0으로 리셋
        upload_count = 0;

//        LoadingDialogHelper.getInstance(GeneralThemeCreateActivity.this).show();

        for (int i = 0; i < now_photo_count; i++) {
            String name = themeItems[themeItemsSort[i]].textView.getText().toString().trim();

            final RequestParams params = new RequestParams();
            params.put("m_app_uid", getString(R.string.app_uid));
            params.put("cti_name", name);
            params.put("ct_seq", ct_seq);
            params.put("cti_sort", i);
            params.put("service_key", "app_w");
/*
            try {
                File f = new File(themeItems[themeItemsSort[i]].filePath);
                params.put("file_item", f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
*/
            // 이미지 리사이즈 크기 설정 --> 360x480 (px)
            SimpleTarget target = new SimpleTarget<Bitmap>(UtilHelper.getInstance().getResourceInteger(this, R.string.upload_img_width), UtilHelper.getInstance().getResourceInteger(this, R.string.upload_img_height)) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                    // Bitmap을 Stream으로 변환
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    resource.compress(Bitmap.CompressFormat.PNG, 0, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
                    Log.d("resize", "Bitmap is resized");

                    // 파일첨부
                    params.put("file_item", bs, "temp_name.png");

                    client.init(url, params);
                    LogHelper.debug(this, client.toString());
                    client.post(new BaseResponse<BaseModel>(GeneralThemeCreateActivity.this) {
                        @Override
                        public void onResponse(BaseModel response) {
                            LogHelper.debug(this, response.toString());

                            // 업로드 된 파일의 수를 1 증가
                            upload_count++;

                            if (upload_count >= max_photo_count) {
                                client.init(R.string.contest_theme_general_ins_done, context.getString(R.string.app_uid), ct_seq);
                                client.post(new BaseResponse<BaseModel>(GeneralThemeCreateActivity.this) {
                                    @Override
                                    public void onResponse(BaseModel response) {
//                                        LoadingDialogHelper.getInstance(GeneralThemeCreateActivity.this).hide();

                                        if (!response.code.equals("000")) {
                                            Toast.makeText(getApplicationContext(), response.msg, Toast.LENGTH_SHORT).show();
                                            isUploading = false;
                                            return;
                                        }
                                        go_main();
                                    }
                                });
                            }
                        }
                    });
                }
            };

            // 그 크기에 맞게 크롭
            Glide.with(this).load(themeItems[themeItemsSort[i]].filePath).asBitmap().centerCrop().into(target);
            // 비율 유지 리사이즈
            //Glide.with(this).load(themeItems[i].filePath).asBitmap().fitCenter().into(target);
        }
    }
    
    private void go_main() {
    	removeDir(path);
    	PreferenceUtil.setIntSharedData(context, PreferenceUtil.PREF_TAB_SELECTED, 1);
    	Intent intent = new Intent(this, MainFragmentActivity.class);
    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
        finish();
    }

    /**
     * 사진 롱클릭시 처리할 이벤트 (드래그드롭 구현용)
     */
    @Override
    public boolean onLongClick(View v) {
        LogHelper.debug(this, "LongClick");

        View targetView = v;

        // 클릭한 항목이 LinearLayout이 아닌 경우
        if ((v instanceof LinearLayout) == false) {
            targetView = (View) ((View) v.getParent()).getParent();
        }

        // 부모의 부모 --> 출발지
        View startView = (View) targetView.getParent();

        // 출발지 태그 기억하기
        fromTag = String.valueOf(startView.getTag());

        // 드래드할 수 있도록 클립을 만들기
        ClipData.Item item = new ClipData.Item((CharSequence) targetView.getTag());

        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
        ClipData data = new ClipData(targetView.getTag().toString(), mimeTypes, item);
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(targetView);

        targetView.startDrag(data, // data to be dragged
                shadowBuilder, // drag shadow
                targetView, // 드래그 드랍할  Vew
                0 // 필요없은 플래그
        );

        targetView.setVisibility(View.INVISIBLE);

        dragEnabled = false;

        return true;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {

        // 이벤트 시작
        switch (event.getAction()) {
            // 이미지를 드래그 시작될때
            case DragEvent.ACTION_DRAG_STARTED:
                Log.d("DragClickListener", "ACTION_DRAG_STARTED");
                break;
            // 드래그한 이미지를 옮길려는 지역으로 들어왔을때
            case DragEvent.ACTION_DRAG_ENTERED:
                Log.d("DragClickListener", "ACTION_DRAG_ENTERED");
                break;
            // 드래그한 이미지가 영역을 빠져 나갈때
            case DragEvent.ACTION_DRAG_EXITED:
                Log.d("DragClickListener", "ACTION_DRAG_EXITED");
                break;
            // 이미지를 드래그해서 드랍시켰을때
            case DragEvent.ACTION_DROP:
                // 도착지 태그
                String toTag = String.valueOf(v.getTag());

//                LogHelper.debug(this, "FROM: " + fromTag);
//                LogHelper.debug(this, "TO: " + toTag);

                if (toTag.equals(fromTag)) {
                    View v2 = (View) event.getLocalState();
                    v2.setVisibility(View.VISIBLE);
//                    Toast.makeText(this, "이미지를 같은 위치에 드랍할수 없습니다.", Toast.LENGTH_SHORT).show();
                } else if (toTag.indexOf("itemContainer") < 0) {
                    View v2 = (View) event.getLocalState();
                    v2.setVisibility(View.VISIBLE);
//                    Toast.makeText(this, "이미지를 다른 지역에 드랍할수 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    dragEnabled = true;
                    View view = (View) event.getLocalState();
                    ViewGroup viewgroup = (ViewGroup) view.getParent();
                    viewgroup.removeView(view);

                    LinearLayout containView = (LinearLayout) v;
                    View target = containView.getChildAt(0);
                    containView.removeAllViews();
                    containView.addView(view);
                    viewgroup.addView(target);
                    view.setVisibility(View.VISIBLE);

                    int fromIndex = Integer.parseInt(fromTag.substring(13));
                    int toIndex = Integer.parseInt(toTag.substring(13));

                    swapThemeItem(fromIndex, toIndex);
                }
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                Log.d("DragClickListener", "ACTION_DRAG_ENDED");
                Log.d("DragClickListener", "dragEnabled=" + dragEnabled);
                if (!dragEnabled) {
                    View v2 = (View) event.getLocalState();
                    v2.setVisibility(View.VISIBLE);
//                    Toast.makeText(this, "이미지를 다른 지역에 드랍할수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            default:
                break;
        }

        return true;
    }
    
  //파일 & 폴더 삭제
    private void removeDir(String mRootPath) {
    	try {
    		File file = new File(mRootPath);
            File[] childFileList = file.listFiles();
            for(File childFile : childFileList)
            {
                if(childFile.isDirectory()) {
                    removeDir(childFile.getAbsolutePath());    //하위 디렉토리
                }
                else {
                    childFile.delete();    //하위 파일
                }
            }
            file.delete();    //root 삭제
    	}catch (NullPointerException e) {
		}finally {
			rescan_file();    	
		}
    }
    
    private void rescan_file(){
    	File fileCacheItem = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
				+ path);
    	sendBroadcast(new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(fileCacheItem)) );
    }

    public void swapThemeItem(int fromIdx, int toIdx) {
        int fromVal = themeItemsSort[fromIdx];
        int toVal = themeItemsSort[toIdx];
        themeItemsSort[fromIdx] = toVal;
        themeItemsSort[toIdx] = fromVal;
    }

    @Override
    public void onBackPressed() {
        if (!isUploading)
            super.onBackPressed();
        removeDir(path);
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