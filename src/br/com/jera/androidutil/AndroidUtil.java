package br.com.jera.androidutil;

import android.app.Activity;
import br.com.jeramobstats.JeraAgent;

import com.xtify.android.sdk.PersistentLocationManager;

public class AndroidUtil {

	private PersistentLocationManager persistentLocationManager;

	public AndroidUtil(Activity activity) {

		if (Preferences.isDynamicSplash()) {
			new SplashDialog(activity);
		}

		if (Preferences.isFlurry()) {
			JeraAgent.onStartSession(activity, Preferences.getFlurryKey());
		}

		if (Preferences.isAppRate()) {
			AppRater.app_launched(activity);
		}

		if (Preferences.isPushNotification()) {
			persistentLocationManager = new PersistentLocationManager(activity);

			Thread xtifyThread = new Thread(new Runnable() {
				public void run() {
					persistentLocationManager.setNotificationIcon(R.drawable.notification);
					persistentLocationManager.setNotificationDetailsIcon(R.drawable.icon);
					boolean trackLocation = persistentLocationManager.isTrackingLocation();
					boolean deliverNotifications = persistentLocationManager.isDeliveringNotifications();
					if (trackLocation || deliverNotifications) {
						persistentLocationManager.startService();
					}
				}
			});
			xtifyThread.start();
		}
	}

	public void onStart(Activity activity) {
		if (Preferences.isFlurry()) {
			JeraAgent.onStartSession(activity, Preferences.getFlurryKey());
		}
	}

	public void onStop(Activity activity) {
		if (Preferences.isFlurry()) {
			JeraAgent.onEndSession(activity);
		}
	}
}