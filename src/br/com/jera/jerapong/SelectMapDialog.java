package br.com.jera.jerapong;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SelectMapDialog extends Dialog {

	public static final int SELECT_MAP = 666;

	public SelectMapDialog(final Activity activity) {
		super(activity);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		update(activity);
	}

	public void update(final Activity activity) {
		setContentView(R.layout.select_map);
		LinearLayout layout = (LinearLayout) findViewById(R.id.layout_root);
		
		for (int i = 1; i <= 5; i++) {
			final String currentMap = String.format("map%02d", i);
				final int count = i;
				ImageButton button = (ImageButton) getLayoutInflater().inflate(R.layout.select_map_button, null);
				layout.addView(button);
				button.setImageResource(activity.getApplicationContext().getResources().getIdentifier(currentMap + "_thumb", "drawable", Preferences.PACKAGE_NAME));
				button.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						MenuScreen menu = new MenuScreen();
						Toast.makeText(menu.getBaseContext(), "TESTE", 100).show();
						menu.setChoiceMap(currentMap);
						dismiss();
					}
				});
			}
	}

    private void registerStats(String currentMap)
    {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("map", currentMap);
        //JeraAgent.logEvent("MAP_SELECT", parameters);
    }
}
