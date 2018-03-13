package ideal.worldcup.automoney.cms;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;

import cz.msebera.android.httpclient.Header;

/** ��� ��� ó���� ������ Ŭ���� ���� */
//--> import com.loopj.android.http.AsyncHttpResponseHandler;
public abstract class BaseResponse2<T> extends AsyncHttpResponseHandler {

 Context context;

 public BaseResponse2(Context context) {
     this.context = context;
 }

 /** ��ӹ޾Ƽ� ������ �ؾ� �ϴ� �޼��� */
 public abstract void onResponse(T response);

 /** ��� ���۽ÿ� ȣ��ȴ�. */
 @Override
 public void onStart() {
     super.onStart();
 }

 /**
  * ��� ���� ������ ȣ��ȴ�.
  * @param statusCode    �����ڵ�. (�������� ��� 200)
  * @param headers       HTTP Header
  * @param responseBody  HTTP Body (�������� �������� ����)
  */
 @Override
 public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
     // ��� ����� ���ڿ��� ��ȯ�Ѵ�.
     String response = null;
     try {
         response = new String(responseBody, "UTF-8");
     } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
     }

     T object = null;

     if (response != null) {
         object = (new Gson()).fromJson(response, (Class <T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
     }

     this.onResponse(object);
 }

 /**
  * ��� ���� ���н� ȣ��ȴ�.
  * // --> import cz.msebera.android.httpclient.Header;
  * @param statusCode        �����ڵ�. (404:Page Not Found, 50x: Server Error)
  * @param headers           HTTP Header ����
  * @param responseBody      HTTP Body ���� (�������� �������� ����)
  * @param error             �������� ��ü
  */
 @Override
 public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
     // �����޽��� ǥ��
     // ex) 500 Error - Server Error
     String errMsg = statusCode + " Error - " + error.getLocalizedMessage();
     Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show();
 }

 /** ��� ����,���п� ������� ����ÿ� ȣ��ȴ�. */
 @Override
 public void onFinish() {
     super.onFinish();
 }
}
