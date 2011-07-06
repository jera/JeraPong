package br.com.jera.jerapong;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.Text;
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
	
	private TextureRegion textureRegionBackground;
	
	private Font fontScore;
	
	private Scene scene;

	/** ######## GAME ######## **/
	
	public ScoreScreen(MenuScreen menuScreen) {
		this.menuScreen = menuScreen;
	}
	
	public void ScoreScene(String[] vectorPlayer, double[] vectorScore) {
		
		this.scene = new Scene(2);
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
		int height = 100;
		/** Table for ranking */
		for(int x = 0; 5 > x; x++){
			
			String txtScore = "" + vectorScore[x] + "s";
			
			Text position = new Text(((CAMERA_WIDTH / 2) - 250), height, this.fontScore, "" + (x + 1), HorizontalAlign.CENTER);
			this.scene.attachChild(position);
			
			Text player = new Text(((CAMERA_WIDTH / 2) - 30), height, this.fontScore, "" + vectorPlayer[x], HorizontalAlign.CENTER);
			this.scene.attachChild(player);
			
			Text score = new Text(((CAMERA_WIDTH / 2) + 190), height, this.fontScore, txtScore.replace(".", ","), HorizontalAlign.CENTER);
			this.scene.attachChild(score);
			
			height += 65;
			
		}

		/** Table for ranking */
		
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
	
	public TextureRegion getTextureRegionBackground() { return textureRegionBackground; }
	public Texture getTextureBackground() { return textureBackground; }
	public Font getFontScore() { return fontScore; }
	public Texture getTextureFontScore() { return textureFontScore; }

}
