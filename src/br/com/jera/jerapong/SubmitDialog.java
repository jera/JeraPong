package br.com.jera.jerapong;

import android.app.Activity;
import android.app.Dialog;
import android.widget.Button;
import android.widget.EditText;

public class SubmitDialog extends Dialog {
	
	private GameSinglePlayer game;
	
	public SubmitDialog(Activity activity) {
		super(activity);
		
	}

	public void updateScore(double score) {
		
		EditText editText = (EditText) findViewById(R.id.button1);
		editText.setOnClickListener(null);
		
		Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(null);
	}

}
