package br.com.jera.androidutil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity implements Runnable {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Preferences.initialize(this);
	}

	@Override
	public void run() {
		this.finish();
		startActivity(new Intent(this, Preferences.getStartClass()));
	}
}
