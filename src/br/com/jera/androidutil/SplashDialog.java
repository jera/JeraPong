package br.com.jera.androidutil;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import br.com.jeramobstats.JeraAgent;

public class SplashDialog extends Dialog {

	private Activity activity;

	public SplashDialog(final Activity activity) {
		super(activity);
		this.activity = activity;
		if (AndroidNetUtil.isOnline(activity)) {
			AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					checkUpdates();
					return null;
				}
			};
			task.execute((Void[]) null);
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	public void update() {
		if (new File(Preferences.getSplashFilename()).exists() && !isSplashed()) {
			setContentView(R.layout.dynamic_splash);
			ImageView image = (ImageView) findViewById(R.id.splash);
			image.setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeFile(Preferences.getSplashFilename())));
			findViewById(R.id.close_splash).setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
			});
			if (Preferences.readString(activity, "splash_link") != null && !Preferences.readString(activity, "splash_link").equals("")) {
				createSplashLink(image);
			}
			setSplashed(true);
			activity.runOnUiThread(new Runnable() {
				public void run() {
					show();
				}
			});
		}
	}

	private void createSplashLink(ImageView image) {
		image.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				activity.runOnUiThread(new Runnable() {
					public void run() {
						JeraAgent.logEvent("SPLASH_CLICK");
						Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(Preferences.readString(activity, "splash_link")));
						activity.startActivity(intent);
					}
				});
			}
		});
	}

	private void checkUpdates() {
		File web = new File(Preferences.getSplashFilename() + ".tmp");
		File old = new File(Preferences.getSplashFilename());
		AndroidNetUtil.downloadFromUrl("SplashManager", Preferences.getSplashImgUrl(), web.getAbsolutePath());
		if (!old.exists() || !AndroidFileUtil.fileEquals(web, old)) {
			Log.d("SplashManager", "new image detected");
			old.delete();
			web.renameTo(old);
			setSplashed(false);
			try {
				String fileDatPath = Preferences.getPath() + "splash.dat.tmp";
				AndroidNetUtil.downloadFromUrl("SplashManager", Preferences.getSplashLinkUrl(), fileDatPath);
				DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(new File(fileDatPath))));
				Preferences.write(activity, "splash_link", dis.readLine());
			} catch (Exception e) {
				e.printStackTrace();
			}
			update();
		}
	}

	public boolean isSplashed() {
		return Preferences.readBoolean(activity, "splashed");
	}

	public void setSplashed(Boolean splashed) {
		Preferences.write(activity, "splashed", splashed);
	}

}