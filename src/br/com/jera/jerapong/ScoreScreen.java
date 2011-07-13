package br.com.jera.jerapong;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.util.HorizontalAlign;

public class ScoreScreen {

	/** ######## GLOBAL ######## **/

	private int CAMERA_WIDTH;
	private int CAMERA_HEIGHT;

	private MenuScreen menuScreen;

	/** ######## GAME ######## **/

	private Texture textureBackground;
	private Texture textureFontScore;
	private Texture textureBtnBack;
	private Texture textureBtnNewGame;
	
	private TextureRegion textureRegionBackground;
	private TextureRegion textureRegionBtnBack;
	private TextureRegion textureRegionBtnNewGame;
	
	private Sprite spriteBtnBack;
	private Sprite spriteBtnNewGame;
	
	private Font fontScore;
	
	private Scene scene;

	/** ######## GAME ######## **/
	
	public ScoreScreen(MenuScreen menuScreen) {
		this.menuScreen = menuScreen;
	}
	
	public void ScoreScene(String[] vectorPlayer, double[] vectorScore) {
		
		this.scene = new Scene(4);
		this.scene.setOnAreaTouchTraversalFrontToBack();
		
		/** Background */
		
		final Sprite background = new Sprite(0, 0, this.textureRegionBackground);
		float scalaXBG = (float)CAMERA_WIDTH / (float)this.textureRegionBackground.getWidth();
		float scalaYBG = (float)CAMERA_HEIGHT / (float)this.textureRegionBackground.getHeight();
		background.setScaleCenter(0f,0f);
		background.setScaleX(scalaXBG);
		background.setScaleY(scalaYBG);		
		this.scene.attachChild(background);
		
		/** Background */
		int height = (CAMERA_HEIGHT - CAMERA_HEIGHT) + 200;
		/** Table for ranking */
		for(int x = 0; 5 > x; x++){
			
			String txtScore = "" + vectorScore[x] + "s";
			
			Text position = new Text(((CAMERA_WIDTH / 2) - 250), height, this.fontScore, "" + (x + 1), HorizontalAlign.CENTER);
			this.scene.attachChild(position);
			
			Text player = new Text(((CAMERA_WIDTH / 2) - 38), height, this.fontScore, "" + vectorPlayer[x], HorizontalAlign.CENTER);
			this.scene.attachChild(player);
			
			Text score = new Text(((CAMERA_WIDTH / 2) + 190), height, this.fontScore, txtScore.replace(".", ","), HorizontalAlign.CENTER);
			this.scene.attachChild(score);
			
			height += 60;
			
		}
		
		/** Table for ranking */
		if(menuScreen.ScoreMode != 1){
			int WidthBtnNewGame = (this.CAMERA_WIDTH / 2) - this.textureRegionBtnNewGame.getWidth() - 30;
			int HeightBtnNewGame = (this.CAMERA_HEIGHT /2) - (this.textureRegionBtnNewGame.getHeight() / 2) + 165;
			
			this.spriteBtnNewGame = new Sprite(WidthBtnNewGame, HeightBtnNewGame, this.textureRegionBtnNewGame){
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					
					SelectMapDialog dialog = new SelectMapDialog(menuScreen);
					menuScreen.modeSelected = 1;
					dialog.show();
					
					return false;
				};
			};
			scene.attachChild(this.spriteBtnNewGame);
			scene.registerTouchArea(this.spriteBtnNewGame);
		}
		int WidthBtnBack = (this.CAMERA_WIDTH / 2) + 70;
		int HeightBtnBack = (this.CAMERA_HEIGHT /2) - (this.textureRegionBtnBack.getHeight() / 2) + 165;
		
		this.spriteBtnBack = new Sprite(WidthBtnBack, HeightBtnBack, this.textureRegionBtnBack){
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {				
				ScoreScreen.this.menuScreen.getEngine().setScene(ScoreScreen.this.menuScreen.onLoadScene());				
				return false;
			};
		};
		scene.attachChild(this.spriteBtnBack);
		scene.registerTouchArea(this.spriteBtnBack);
		
		menuScreen.getEngine().setScene(this.scene);
	}

	
	public int getCAMERA_WIDTH() { return CAMERA_WIDTH; }
	public int getCAMERA_HEIGHT() { return CAMERA_HEIGHT; }
	public void setCAMERA_WIDTH(int cAMERA_WIDTH) { CAMERA_WIDTH = cAMERA_WIDTH;}	
	public void setCAMERA_HEIGHT(int cAMERA_HEIGHT) { CAMERA_HEIGHT = cAMERA_HEIGHT; }

	public void setTextureBackground(Texture textureBackground) { this.textureBackground = textureBackground; }
	public void setTextureRegionBackground(TextureRegion textureRegionBackground) { this.textureRegionBackground = textureRegionBackground; }
	public void setFontScore(Font fontScore) { this.fontScore = fontScore; }
	public void setTextureFontScore(Texture textureFontScore) { this.textureFontScore = textureFontScore; }
	public void setSpriteBtnBack(Sprite spriteBtnBack) { this.spriteBtnBack = spriteBtnBack; }
	public void setTextureBtnBack(Texture textureBtnBack) { this.textureBtnBack = textureBtnBack; }
	public void setTextureRegionBtnBack(TextureRegion textureRegionBtnBack) { this.textureRegionBtnBack = textureRegionBtnBack; }
	public void setTextureBtnNewGame(Texture textureBtnNewGame) { this.textureBtnNewGame = textureBtnNewGame; }
	public void setTextureRegionBtnNewGame(TextureRegion textureRegionBtnNewGame) { this.textureRegionBtnNewGame = textureRegionBtnNewGame; }
	public void setSpriteBtnNewGame(Sprite spriteBtnNewGame) { this.spriteBtnNewGame = spriteBtnNewGame; }
	
	public TextureRegion getTextureRegionBackground() { return textureRegionBackground; }
	public Texture getTextureBackground() { return textureBackground; }
	public Font getFontScore() { return fontScore; }
	public Texture getTextureFontScore() { return textureFontScore; }
	public Texture getTextureBtnBack() { return textureBtnBack; }
	public TextureRegion getTextureRegionBtnBack() { return textureRegionBtnBack; }
	public Sprite getSpriteBtnBack() { return spriteBtnBack; }
	public Texture getTextureBtnNewGame() { return textureBtnNewGame; }
	public TextureRegion getTextureRegionBtnNewGame() { return textureRegionBtnNewGame; }
	public Sprite getSpriteBtnNewGame() { return spriteBtnNewGame; }

	
	
	
	
}
