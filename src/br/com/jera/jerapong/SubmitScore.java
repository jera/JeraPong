package br.com.jera.jerapong;

import br.com.jeramobstats.JeraAgent;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SubmitScore extends Dialog {

	private MenuScreen menu;
	private DataHelper dataHelper;
	
	public SubmitScore(final MenuScreen menu) {
		super(menu);
		this.menu = (MenuScreen) menu;
		
		this.setContentView(R.layout.main);

		final EditText editText = (EditText) this.findViewById(R.id.editText1);
		final TextView erro = (TextView) this.findViewById(R.id.erro);
		
		Button button = (Button) this.findViewById(R.id.button1);
		
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(editText.getEditableText().toString().equals("") || editText.getEditableText().toString().equals(" ")){
					erro.setText("O nome do jogador não pode ser vazio!");
					erro.setVisibility(1);
				}else{
					JeraAgent.logEvent("SUBMIT_SCORE");
					erro.setText("");
					menu.ScoreMode = 0;
					dataHelper = new DataHelper(menu.getBaseContext());
					dataHelper.insert(editText.getEditableText().toString(), menu.gameSinglePlayer.getPlayerScore());
					menu.timePassed = menu.getEngine().getSecondsElapsedTotal();
					dismiss();
				}
			}
		});
		
		this.show();
		
	}


}
