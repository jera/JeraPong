package br.com.jera.jerapong;

import android.app.Dialog;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SubmitScore extends Dialog {

	private MenuScreen menu;
	private DataHelper dataHelper;
	
	public SubmitScore(final MenuScreen menu) {
		super(menu);
		this.menu = (MenuScreen) menu;
		
		this.setContentView(R.layout.main);
		this.setTitle("Player Score");

		final EditText editText = (EditText) this.findViewById(R.id.editText1);
		
		Button button = (Button) this.findViewById(R.id.button1);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				menu.ScoreMode = 0;
				
				dataHelper = new DataHelper(menu.getBaseContext());
				dataHelper.insert(editText.getEditableText().toString(), menu.gameSinglePlayer.getPlayerScore());
				
				Cursor cursor = dataHelper.select();
				
				int x = 0;
				String[] vectorPlayer = new String[5];
				double[] vectorScore = new double[5];
				
				while(cursor.moveToNext()){
					String player = cursor.getString(1);
					double score = cursor.getDouble(2);
					
					vectorPlayer[x] = player;
					vectorScore[x] = score;
					x++;
				}
				
				cursor.close();
				dataHelper.close();
				
				menu.LoadingScoreScreen();
				menu.scoreScreen.ScoreScene(vectorPlayer,vectorScore);
				menu.timePassed = menu.getEngine().getSecondsElapsedTotal();
				dismiss();
			}
		});
		
		this.show();
		
	}


}
