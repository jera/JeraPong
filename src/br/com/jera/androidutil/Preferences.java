package br.com.jera.androidutil;

import java.io.IOException;
import java.util.Properties;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;

public class Preferences {

	public static boolean PAID;
	private static String APP_TITLE;
	private static String PACKAGE_PATH;
	private static String PATH;
	private static String SPLASH_IMG_URL;
	private static String SPLASH_LINK_URL;
	private static String SPLASH_FILENAME;
	private static String PACKAGE_NAME;
	private static String FLURRY_KEY;
	private static String START_CLASS;

	private static Boolean APP_RATE;
	private static Boolean SPLASH;
	private static Boolean FLURRY;
	private static Boolean DYNAMIC_SPLASH;
	private static Boolean PUSH_NOTIFICATION;

	private Preferences() {
	}

	public static void initialize(Activity activity) {
		Properties properties = new Properties();
		try {
			if (AndroidConfigUtil.isDebugMode(activity)) {
				properties.load(activity.getAssets().open("debugutil.properties"));
			} else {
				properties.load(activity.getAssets().open("util.properties"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		PAID = (properties.getProperty("paid").equals("0")) ? false : true;
		APP_TITLE = properties.getProperty("appTitle");
		
		PACKAGE_PATH = properties.getProperty("packagePath");
		PATH = "/data/data/" + PACKAGE_PATH + "/";
		PACKAGE_NAME = properties.getProperty((PAID) ? "packageNamePaid" : "packageName");
		FLURRY_KEY = properties.getProperty((PAID) ? "flurryKeyPaid" : "flurryKey");
		START_CLASS = properties.getProperty("startClass");

		APP_RATE = (properties.getProperty("appRate").equals("0")) ? false : true;
		FLURRY = (properties.getProperty("flurry").equals("0")) ? false : true;
		DYNAMIC_SPLASH = (properties.getProperty("dynamicSplash").equals("0")) ? false : true;
		PUSH_NOTIFICATION = (properties.getProperty("pushNotification").equals("0")) ? false : true;
		SPLASH = (properties.getProperty("splash").equals("0")) ? false : true;

		String SPLASH_URL = "http://assets.jera.com.br/splash/";
		SPLASH_IMG_URL = SPLASH_URL + PACKAGE_NAME + "/splash.jpg";
		SPLASH_LINK_URL = SPLASH_URL + PACKAGE_NAME + "/splash.dat";
		SPLASH_FILENAME = PATH + "splash.jpg";
		onCreate(activity);
	}

	public static void onCreate(Activity activity) {
		int delay = 0;
		if (SPLASH) {
			activity.setContentView(br.com.jera.androidutil.R.layout.splash);
			activity.findViewById(br.com.jera.androidutil.R.id.splash_bg).setBackgroundColor(Color.WHITE);
			delay = 2000;
		}
		new Handler().postDelayed((Runnable) activity, delay);
	}

	public static String readString(Activity activity, String key) {
		SharedPreferences settings = activity.getSharedPreferences(PACKAGE_NAME, 0);
		return settings.getString(key, null);
	}

	public static boolean readBoolean(Activity activity, String key) {
		SharedPreferences settings = activity.getSharedPreferences(PACKAGE_NAME, 0);
		return settings.getBoolean(key, false);
	}

	public static void write(Activity activity, String key, Object value) {
		SharedPreferences settings = activity.getSharedPreferences(PACKAGE_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		if (value instanceof Boolean) {
			editor.putBoolean(key, (Boolean) value);
		} else if (value instanceof String) {
			editor.putString(key, (String) value);
		}
		editor.commit();
	}

	public static boolean isPaid() {
		return PAID;
	}

	public static String getAppTitle() {
		return APP_TITLE;
	}

	public static String getPackagePath() {
		return PACKAGE_PATH;
	}

	public static String getPath() {
		return PATH;
	}

	public static String getSplashImgUrl() {
		return SPLASH_IMG_URL;
	}

	public static String getSplashLinkUrl() {
		return SPLASH_LINK_URL;
	}

	public static String getSplashFilename() {
		return SPLASH_FILENAME;
	}

	public static String getPackageName() {
		return PACKAGE_NAME;
	}

	public static String getFlurryKey() {
		return FLURRY_KEY;
	}

	public static Boolean isAppRate() {
		return APP_RATE;
	}

	public static Boolean isFlurry() {
		return FLURRY;
	}

	public static Boolean isPushNotification() {
		return PUSH_NOTIFICATION;
	}

	public static Boolean isDynamicSplash() {
		return DYNAMIC_SPLASH;
	}

	public static Class<?> getStartClass() {
		try {
			return Class.forName(START_CLASS);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}