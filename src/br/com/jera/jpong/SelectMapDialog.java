package br.com.jera.jpong;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import br.com.jera.jpong.R;
import br.com.jeramobstats.JeraAgent;

public class SelectMapDialog extends Dialog {

	public static final int SELECT_MAP = 6;
	private MenuScreen menu;
	private int i;

	public SelectMapDialog(final Activity activity) {
		super(activity);
		activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.select_map);
		this.menu = (MenuScreen) activity;
		update(activity);
	}

	public void update(final Activity activity) {
		LinearLayout layout = (LinearLayout) findViewById(R.id.layout_root);
		for (i = 1; i <= 5; i++) {
			final String currentMap = String.format("map%01d", i);
			Log.e("map", "Map Current : " + currentMap);
			ImageButton button = (ImageButton) getLayoutInflater().inflate(R.layout.select_map_button, null);
			layout.addView(button);
			button.setImageResource(activity.getApplicationContext().getResources().getIdentifier(currentMap + "_thumb", "drawable", "br.com.jera.jpong"));
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if (menu.modeSelected == 1) {
						//JeraAgent.logEvent("SP_SELECTED_MAP: " + i);
						Map<String, String> parameters = new HashMap<String, String>();
						parameters.put("map", currentMap);
						JeraAgent.logEvent("MAP_SELECT_SP", parameters);
						menu.timePassed = menu.getEngine().getSecondsElapsedTotal();
						menu.gameRunning = true;
						menu.LoadingGameSinglePlayer(currentMap);
						menu.gameSinglePlayer.GameScene();
						menu.LoadingGameSinglePlayer = true;
					} else {
						Map<String, String> parameters = new HashMap<String, String>();
						parameters.put("map", currentMap);
						JeraAgent.logEvent("MAP_SELECT_MP", parameters);
						menu.timePassed = menu.getEngine().getSecondsElapsedTotal();
						menu.gameRunning = true;
						menu.LoadingGameMultiPlayer(currentMap);
						menu.gameMultiPlayer.GameScene();
						menu.LoadingGameMultiPlayer = true;
					}
					dismiss();
				}
			});
		}
	}

	/*
	 * private void registerStats(String currentMap) { Map<String, String>
	 * parameters = new HashMap<String, String>(); parameters.put("map",
	 * currentMap); //JeraAgent.logEvent("MAP_SELECT", parameters); }
	 */
}
