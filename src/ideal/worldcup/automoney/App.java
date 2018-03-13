package ideal.worldcup.automoney;


import android.app.Application;
import ideal.worldcup.automoney.util.NetworkHelper;

public class App extends Application{
	private static App INSTANCE			= null;
	/* (non-Javadoc)
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		INSTANCE = this;
	}

	/**
	 * Application?�� 리턴?��?��.
	 * @return
	 */
	public static App getApplication(){
		return INSTANCE;
	}


	/* (non-Javadoc)
	 * @see android.app.Application#onTerminate()
	 */
	@Override
	public void onTerminate() {

		NetworkHelper.getInstance().close();

		INSTANCE = null;
		super.onTerminate();
	}
	
	public static App getInstance() {
		return INSTANCE;
  }
}
