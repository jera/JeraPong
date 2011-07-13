package br.com.jera.jerapong;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class SelectMapDialog extends Dialog {

	public static final int SELECT_MAP = 6;
	private MenuScreen menu;
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
		for (int i = 1; i <= 5; i++) {
			final String currentMap = String.format("map%01d", i);
			Log.e("map", "Map Current : " + currentMap);
				ImageButton button = (ImageButton) getLayoutInflater().inflate(R.layout.select_map_button, null);
				layout.addView(button);
				button.setImageResource(activity.getApplicationContext().getResources().getIdentifier(currentMap + "_thumb", "drawable", "br.com.jera.jerapong"));
				button.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if(menu.modeSelected == 1){
								menu.timePassed = menu.getEngine().getSecondsElapsedTotal();
								menu.gameRunning = true;
								menu.LoadingGameSinglePlayer(currentMap);							
								menu.gameSinglePlayer.GameScene();
								menu.LoadingGameSinglePlayer = true;
						}
						else{
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

    /*private void registerStats(String currentMap)
    {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("map", currentMap);
        //JeraAgent.logEvent("MAP_SELECT", parameters);
    }*/
}
