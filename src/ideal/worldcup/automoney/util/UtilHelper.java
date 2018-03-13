package ideal.worldcup.automoney.util;

import java.text.DecimalFormat;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestManager;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import ideal.worldcup.automoney.R;

public class UtilHelper {
	
	private static UtilHelper current = null;

	public static UtilHelper getInstance() {
		if (current == null) {
			current = new UtilHelper();
		}

		return current;
	}
	
	
	public void loadImage(Context context, ImageView imageView, String url) {
		this.loadImage(context, imageView, url, -1, -1);
	}

	public void loadImage(Context context, ImageView imageView, String url,
						  int width, int height) {

		if ( url.indexOf("http://") == -1 && url.indexOf("file://") == -1) {
			url = context.getString(R.string.url_image_prefix) + url;
		}

		RequestManager manager = Glide.with(context);
		DrawableTypeRequest<String> request = manager.load(url);
		//request.diskCacheStrategy(DiskCacheStrategy.RESULT);
		request.skipMemoryCache(true);
		//request.thumbnail(0.1f);

		if (width > -1 && height > -1) {
			request.centerCrop();
			request.override(width, height);
		}

		request.into(imageView);
	}
	
	public void loadCacheImg(Activity activity, String url1, String url2) {

		if ( url1.indexOf("http://") == -1 && url1.indexOf("file://") == -1) {
			url1 = activity.getString(R.string.url_image_prefix) + url1;
		}

		if ( url2.indexOf("http://") == -1 && url2.indexOf("file://") == -1) {
			url2 = activity.getString(R.string.url_image_prefix) + url2;
		}

		View tempView = activity.getLayoutInflater().inflate(R.layout.temp_contest_image_cache, null);
		ImageButton tempLeft = (ImageButton) tempView.findViewById(R.id.ibLeft);
		ImageButton tempRight = (ImageButton) tempView.findViewById(R.id.ibRight);

		Glide.with(activity)
				.load(url1)
				.skipMemoryCache(true)
				.priority(Priority.HIGH)
				.into(tempLeft);

		Glide.with(activity)
				.load(url2)
				.skipMemoryCache(true)
				.priority(Priority.HIGH)
				.into(tempRight);
	}
	
	public void loadContestImg(Context context, ImageButton imageView, String url) {

		if ( url.indexOf("http://") == -1 && url.indexOf("file://") == -1) {
			url = context.getString(R.string.url_image_prefix) + url;
		}

		Glide.with(context)
				.load(url)
				.skipMemoryCache(true)
				.priority(Priority.IMMEDIATE)
				.into(imageView);
	}
	
	public String getNumberFormat(int num) {
		DecimalFormat df = new DecimalFormat("#,###");
		return df.format(num).toString();
	}
	
	 // onCreate() : 큰 배경이미지 사용시 OutOfMemoryError 나는 경우에 사용
    public void setBackground(Context context, ViewGroup bgLayout, int imgResource) {
        // 하드웨어 가속 끄기
        if ( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT ) {
            bgLayout.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        freeBackground(bgLayout);

        if ( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN ) {
            bgLayout.setBackgroundDrawable(new BitmapDrawable(context.getResources(), BitmapFactory.decodeResource(context.getResources(), imgResource)));
        } else {
            bgLayout.setBackground(new BitmapDrawable(context.getResources(), BitmapFactory.decodeResource(context.getResources(), imgResource)));
        }
    }
    
 // onDestroy()에서 호출
    public void freeBackground(ViewGroup bgLayout) {
        if ( bgLayout != null ) {
            Drawable bg = bgLayout.getBackground();
            if ( bg != null ) {
                bg.setCallback(null);
                ((BitmapDrawable)bg).getBitmap().recycle();

                if ( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN ) {
                    bgLayout.setBackgroundDrawable(null);
                } else {
                    bgLayout.setBackground(null);
                }
            }
        }
    }
	
	// onDestroy()에서 호출
    public void freeImageResource(ImageView imgView) {
        if ( imgView != null ) {
            imgView.setImageDrawable(null);
        }
    }
    
    public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0 || "NULL".equals(str) || "null".equals(str);
	}
    
 // 키보드 내리기.
 	public void downKeyboard(Context context, EditText editText) {
 		InputMethodManager mInputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
 		mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
 	}
 	
 // 포커스 및 키보드 올리기.
 	public void upKeyboard(Context context) {
 		InputMethodManager mInputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
 		mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
 	}
 	
 	public int getResourceInteger(Context context, int resourceId) {
		return Integer.parseInt(context.getResources().getString(resourceId));
	}

}
