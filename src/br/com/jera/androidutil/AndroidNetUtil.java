package br.com.jera.androidutil;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class AndroidNetUtil {


	public static boolean isOnline(Activity activity) {
		ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
	
	public static void downloadFromUrl(String logTag, String inputUrl, String outputFile) {
		try {
			URL url = new URL(inputUrl);

			long startTime = System.currentTimeMillis();
			Log.d(logTag, "download begining");
			Log.d(logTag, "download url:" + url);
			Log.d(logTag, "downloaded file name:" + outputFile);
			URLConnection ucon = url.openConnection();
			ucon.setConnectTimeout(5000);
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}
			FileOutputStream fos = new FileOutputStream(outputFile);
			fos.write(baf.toByteArray());
			fos.close();
			Log.d(logTag, "download ready in " + ((System.currentTimeMillis() - startTime) / 1000) + " sec");

		} catch (IOException e) {
			Log.d(logTag, "Error: " + e);
		}
	}
	
}
