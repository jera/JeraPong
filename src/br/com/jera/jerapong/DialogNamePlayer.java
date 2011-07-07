package br.com.jera.jerapong;

import android.app.AlertDialog;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DialogNamePlayer extends Dialog {

	public static final int SELECT_MAP = 666;
	private MenuScreen menu;
	
	public DialogNamePlayer(final MenuScreen menu,final String score) {
		super(menu);
		this.menu = (MenuScreen) menu;
		
		this.setContentView(R.layout.main);
		this.setTitle("Player Score");

		final EditText editText = (EditText) this.findViewById(R.id.editText1);
		
		Button button = (Button) this.findViewById(R.id.button1);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				DataHelper data = new DataHelper(getContext());
				data.insert(editText.getEditableText().toString(), score);
				menu.getEngine().setScene(menu.SceneMenu(menu));
				dismiss();
			}
		});
		
		this.show();
		
	}


}
