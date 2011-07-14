package br.com.jera.androidutil;

import android.app.Activity;
import android.os.Bundle;

public class AppMainActivity extends Activity {
	private static AndroidUtil util;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		util = new AndroidUtil(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		util.onStop(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		util.onStart(this);
	}
}
