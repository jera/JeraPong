package br.com.jera.jerapong;

import java.io.IOException;

import org.anddev.andengine.audio.sound.SoundFactory;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.extension.input.touch.controller.MultiTouch;
import org.anddev.andengine.extension.input.touch.controller.MultiTouchController;
import org.anddev.andengine.extension.input.touch.exception.MultiTouchException;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.Debug;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class MenuScreen extends BaseGameActivity implements IOnSceneTouchListener {
	
	/** ######## GLOBAL ######## **/
	
	private int CAMERA_WIDTH = 0;
	private int CAMERA_HEIGHT = 0;	
	public GameMultiPlayer gameMultiPlayer;
	public GameSinglePlayer gameSinglePlayer;	
	public static String choiceMap;
	private Score score;
	private DataHelper dataHelper;
	
	/** ######## ENGINE ######## **/
	
	Camera camera;
	
	/** ######## MENU ######## **/
	
	private Texture textureBackground;
	private Texture textureSinglePlayer;
	private Texture textureMultiPlayer;
	private Texture textureOptions;
	private Texture textureSound;
	private Texture textureExit;
	
	private TextureRegion textureRegionBackground;
	private TextureRegion textureRegionSinglePlayer;
	private TextureRegion textureRegionMultiPlayer;
	private TextureRegion textureRegionOptions;
	private TextureRegion textureRegionSound;
	private TextureRegion textureRegionExit;
	
	private Sprite spriteSinglePlayer;
	private Sprite spriteMultiPlayer;
	private Sprite spriteOptions;
	private Sprite spriteSound;
	private Sprite spriteExit;
	public String selectedMap;
	public int modeSelected = 0;
	public float timePassed;
	public boolean gameRunning = false;
	/** ######## MENU ######## **/
	
	@Override
	public Engine onLoadEngine() {
		
		Log.e("DATABASE", "OPENHELPER loading");
		dataHelper = new DataHelper(getBaseContext());
		Log.e("DATABASE", "OPENHELPER OK");
		
		CAMERA_HEIGHT = getWindowManager().getDefaultDisplay().getHeight();
		CAMERA_WIDTH = getWindowManager().getDefaultDisplay().getWidth();
		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		final EngineOptions engineOptions = new EngineOptions(true,ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera).setNeedsSound(true);
		final Engine engine = new Engine(engineOptions);
		try {
			if (MultiTouch.isSupported(this)) {
				engine.setTouchController(new MultiTouchController());
			} else {
				Toast.makeText(this, "This device does not support multitouch",
						Toast.LENGTH_LONG).show();
			}
		} catch (final MultiTouchException e) {
			Toast.makeText(this, "This android does not support multitouch",
					Toast.LENGTH_LONG).show();
		}
		return engine;		
	}

	@Override
	public void onLoadResources() {		
		Log.e("resources menu","loading");
		
		this.textureBackground = new Texture(2048, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.textureSinglePlayer = new Texture(1024,256,TextureOptions.DEFAULT);
		this.textureMultiPlayer = new Texture(1024,256,TextureOptions.DEFAULT);
		this.textureOptions = new Texture(256,64,TextureOptions.DEFAULT);
		this.textureSound = new Texture(256,64,TextureOptions.DEFAULT);
		this.textureExit = new Texture(256,64,TextureOptions.DEFAULT);
		
		this.textureRegionBackground = TextureRegionFactory.createFromAsset(this.textureBackground, this, "gfx/menu/menu_bg.jpg",0,0);
		this.textureRegionSinglePlayer = TextureRegionFactory.createFromAsset(this.textureSinglePlayer, this, "gfx/menu/button_singleplayer.png",0,0);
		this.textureRegionMultiPlayer = TextureRegionFactory.createFromAsset(this.textureMultiPlayer, this, "gfx/menu/button_multiplayer.png",0,0);
		this.textureRegionOptions = TextureRegionFactory.createFromAsset(this.textureOptions, this, "gfx/menu/button_options.png",0,0);
		this.textureRegionSound = TextureRegionFactory.createFromAsset(this.textureSound, this, "gfx/menu/button_sound_on.png",0,0);
		this.textureRegionExit = TextureRegionFactory.createFromAsset(this.textureExit, this, "gfx/menu/button_exit.png",0,0);
		
		this.mEngine.getTextureManager().loadTexture(this.textureBackground);
		this.mEngine.getTextureManager().loadTexture(this.textureSinglePlayer);
		this.mEngine.getTextureManager().loadTexture(this.textureMultiPlayer);		
		this.mEngine.getTextureManager().loadTexture(this.textureOptions);
		this.mEngine.getTextureManager().loadTexture(this.textureSound);
		this.mEngine.getTextureManager().loadTexture(this.textureExit);

		Log.e("resources menu","ok");
	}

	@Override
	public Scene onLoadScene() {
		return SceneMenu(this);
	}

	@Override
	public void onLoadComplete() {
	}
	
	public Scene SceneMenu(final Activity activity){
		final Scene scene = new Scene(1);
		//scene.setOnSceneTouchListener(this);
		
		/**
		 * Menu background resources
		 */
		final Sprite background = new Sprite(0, 0, this.textureRegionBackground);
		float scalaXBG = (float)CAMERA_WIDTH / (float)this.textureRegionBackground.getWidth();
		float scalaYBG = (float)CAMERA_HEIGHT / (float)this.textureRegionBackground.getHeight();
		background.setScaleCenter(0f,0f);
		background.setScaleX(scalaXBG);
		background.setScaleY(scalaYBG);		
		scene.attachChild(background);
		
		/**
		 * Loading menu resources
		 */
		final int middleScreenHorizontal = CAMERA_WIDTH / 2;
		int widthSinglePlayer = middleScreenHorizontal - (this.textureRegionSinglePlayer.getWidth() + 20);
		int widthMultiPlayer = middleScreenHorizontal + 20;
		int widthOptions = middleScreenHorizontal - middleTextureRegionHorizontalSizeByTwo(this.textureRegionOptions);
		int widthSound = widthOptions - (this.textureRegionSound.getWidth() + 20);
		int widthExit = widthOptions + (this.textureRegionExit.getWidth() + 20);
		
		final int middleScreenVertical = CAMERA_HEIGHT / 2;
		int heightSinglePlayer = middleScreenVertical - middleTextureRegionVerticalSizeByTwo(this.textureRegionSinglePlayer);
		int heightMultiPlayer = heightSinglePlayer;
		int heightOptions = middleScreenVertical + this.textureRegionOptions.getHeight();
		int heightSound = heightOptions;
		int heightExit = heightOptions;
		
		/**
		 * Loading single player button resources
		 */
		this.spriteSinglePlayer = new Sprite(widthSinglePlayer, heightSinglePlayer, this.textureRegionSinglePlayer){
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				SelectMapDialog dialog = new SelectMapDialog(MenuScreen.this);
				modeSelected = 1;
				dialog.show();
				
				/*Cursor cursor = dataHelper.select();
					while(cursor.moveToNext()){
						int id = cursor.getInt(0);
						String player = cursor.getString(1);
						double score = cursor.getDouble(2);
						Log.e("Value DataBase","Id : " + id);
						Log.e("Value DataBase","Player : " + player);
						Log.e("Value DataBase","Score : " + score);
					}*/
				
				return false;
			};
		};
		scene.attachChild(this.spriteSinglePlayer);
		scene.registerTouchArea(this.spriteSinglePlayer);
		
		/**
		 * Loading multi player button resources
		 */
		this.spriteMultiPlayer = new Sprite(widthMultiPlayer, heightMultiPlayer, this.textureRegionMultiPlayer){
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {				
				//Toast.makeText(getBaseContext(), "Loading single player...", 100).show();
				SelectMapDialog dialog = new SelectMapDialog(MenuScreen.this);
				modeSelected = 2;
				dialog.show();
				return false;
			};
		};
		scene.attachChild(this.spriteMultiPlayer);
		scene.registerTouchArea(this.spriteMultiPlayer);
		
		/**
		 * Loading options button resources
		 */
		this.spriteOptions = new Sprite(widthOptions, heightOptions, this.textureRegionOptions){
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				
				MenuScreen.this.score = new Score();
				
				MenuScreen.this.score.SaveScore(MenuScreen.this,"player_1 : 70,43|player_2 : 120,30|player_5 : 10,09|player_56 : 40,32|player_33 : 11,67|player_31 : 13,67|player_3 : 45,67|player_36 : 05,67|player_8 : 06,67|player_47 : 57,67");
				
				Log.e("Save Score", "OK");
				
				return false;
			};
		};
		scene.attachChild(this.spriteOptions);
		scene.registerTouchArea(this.spriteOptions);
		
		/**
		 * Loading sound button resources
		 */
		this.spriteSound = new Sprite(widthSound, heightSound, this.textureRegionSound){
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {

				
				
				return false;
			};
		};
		scene.attachChild(this.spriteSound);
		scene.registerTouchArea(this.spriteSound);
		
		/**
		 * Loading exit button resources
		 */
		this.spriteExit = new Sprite(widthExit, heightExit, this.textureRegionExit){
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				finish();
				return true;
			};
		};
		scene.attachChild(this.spriteExit);
		scene.registerTouchArea(this.spriteExit);
		
		Log.e("scene", "OK");
		
		return scene;
	}
	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		return false;
	}
	
	public void LoadingGameSinglePlayer(String currentMap){		
		this.gameSinglePlayer = new GameSinglePlayer(this);
		
		this.gameSinglePlayer.setCAMERA_HEIGHT(CAMERA_HEIGHT);
		this.gameSinglePlayer.setCAMERA_WIDTH(CAMERA_WIDTH);
		
		this.gameSinglePlayer.setTextureBackground( new Texture(2048, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.gameSinglePlayer.setTexturePlayer1(new Texture(128,256,TextureOptions.DEFAULT));
		this.gameSinglePlayer.setTextureBall(new Texture(128,128,TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.gameSinglePlayer.setTextureScore(new Texture(256,256,TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.gameSinglePlayer.setTextureVictory(new Texture(512,512,TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.gameSinglePlayer.setTexturePause(new Texture(256,64,TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.gameSinglePlayer.setTextureBarRight(new Texture(128,1024,TextureOptions.DEFAULT));
		this.gameSinglePlayer.setTextureMiddleLine(new Texture(4,1024,TextureOptions.DEFAULT));
		this.gameSinglePlayer.setTextureBGScore(new Texture(256,128,TextureOptions.DEFAULT));
		
		this.gameSinglePlayer.setTextureRegionBackground( TextureRegionFactory.createFromAsset(this.gameSinglePlayer.getTextureBackground(), this, "gfx/maps/" + currentMap + ".jpg",0,0));
		this.gameSinglePlayer.setTextureRegionPlayer1(TextureRegionFactory.createFromAsset(this.gameSinglePlayer.getTexturePlayer1(), this, "gfx/game/racket_right.png",0,0));
		this.gameSinglePlayer.setTextureRegionBall(TextureRegionFactory.createFromAsset(this.gameSinglePlayer.getTextureBall(), this, "gfx/game/disc.png",0,0));
		this.gameSinglePlayer.setTextureRegionPause(TextureRegionFactory.createFromAsset(this.gameSinglePlayer.getTexturePause(), this, "gfx/game/pause.png", 0, 0));
		this.gameSinglePlayer.setTextureRegionBarRight(TextureRegionFactory.createFromAsset(this.gameSinglePlayer.getTextureBarRight(), this, "gfx/game/bar_right.png", 0, 0));
		this.gameSinglePlayer.setTextureRegionMiddleLine(TextureRegionFactory.createFromAsset(this.gameSinglePlayer.getTextureMiddleLine(), this, "gfx/game/middle_line.png", 0, 0));
		this.gameSinglePlayer.setTextureRegionBGScore(TextureRegionFactory.createFromAsset(this.gameSinglePlayer.getTextureBGScore(), this, "gfx/game/score_bg_sp.png", 0, 0));
		this.gameSinglePlayer.setFontScore(new Font(this.gameSinglePlayer.getTextureScore(), Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 48, true, Color.WHITE));
		this.gameSinglePlayer.setFontVictory(new Font(this.gameSinglePlayer.getTextureVictory(), Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 30, true, Color.WHITE));

		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTextureBackground());
		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTexturePlayer1());
		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTextureBall());
		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTextureScore());
		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTextureVictory());
		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTexturePause());
		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTextureBarRight());
		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTextureMiddleLine());
		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTextureBGScore());
		this.mEngine.getFontManager().loadFont(this.gameSinglePlayer.getFontScore());
		this.mEngine.getFontManager().loadFont(this.gameSinglePlayer.getFontVictory());
		
		try {
			this.gameSinglePlayer.pingSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "sfx/ping.wav");
		} catch (final IOException e) {
			Debug.e(e);
		}
		
	}
	
	public void LoadingGameMultiPlayer(String currentMap){		
		this.gameMultiPlayer = new GameMultiPlayer(this);
		
		this.gameMultiPlayer.setCAMERA_HEIGHT(CAMERA_HEIGHT);
		this.gameMultiPlayer.setCAMERA_WIDTH(CAMERA_WIDTH);
		
		this.gameMultiPlayer.setTextureBackground( new Texture(2048, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.gameMultiPlayer.setTexturePlayer1(new Texture(128,256,TextureOptions.DEFAULT));
		this.gameMultiPlayer.setTexturePlayer2(new Texture(128,256,TextureOptions.DEFAULT));
		this.gameMultiPlayer.setTextureBall(new Texture(128,128,TextureOptions.DEFAULT));
		this.gameMultiPlayer.setTextureScore(new Texture(256,256,TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.gameMultiPlayer.setTextureVictory(new Texture(512,512,TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.gameMultiPlayer.setTexturePause(new Texture(256,64,TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.gameMultiPlayer.setTextureBarRight(new Texture(128,1024,TextureOptions.DEFAULT));
		this.gameMultiPlayer.setTextureBarLeft(new Texture(128,1024,TextureOptions.DEFAULT));
		this.gameMultiPlayer.setTextureMiddleLine(new Texture(4,1024,TextureOptions.DEFAULT));
		this.gameMultiPlayer.setTextureBGScore(new Texture(256,128,TextureOptions.DEFAULT));
		
		this.gameMultiPlayer.setTextureRegionBackground( TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTextureBackground(), this, "gfx/maps/" + currentMap + ".jpg",0,0));
		this.gameMultiPlayer.setTextureRegionPlayer1(TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTexturePlayer1(), this, "gfx/game/racket_right.png",0,0));
		this.gameMultiPlayer.setTextureRegionPlayer2(TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTexturePlayer2(), this, "gfx/game/racket_left.png",0,0));
		this.gameMultiPlayer.setTextureRegionBall(TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTextureBall(), this, "gfx/game/disc.png",0,0));
		this.gameMultiPlayer.setTextureRegionPause(TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTexturePause(), this, "gfx/game/pause.png", 0, 0));
		this.gameMultiPlayer.setTextureRegionBarRight(TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTextureBarRight(), this, "gfx/game/bar_right.png", 0, 0));
		this.gameMultiPlayer.setTextureRegionBarLeft(TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTextureBarLeft(), this, "gfx/game/bar_left.png", 0, 0));
		this.gameMultiPlayer.setTextureRegionMiddleLine(TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTextureMiddleLine(), this, "gfx/game/middle_line.png", 0, 0));
		this.gameMultiPlayer.setTextureRegionBGScore(TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTextureBGScore(), this, "gfx/game/score_bg_mp.png", 0, 0));
		
		
		
		this.gameMultiPlayer.setFontScore(new Font(this.gameMultiPlayer.getTextureScore(), Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 48, true, Color.WHITE));
		this.gameMultiPlayer.setFontVictory(new Font(this.gameMultiPlayer.getTextureVictory(), Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 30, true, Color.WHITE));

		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTextureBackground());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTexturePlayer1());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTexturePlayer2());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTextureBall());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTextureScore());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTextureVictory());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTexturePause());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTextureBarRight());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTextureBarLeft());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTextureMiddleLine());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTextureBGScore());
		this.mEngine.getFontManager().loadFont(this.gameMultiPlayer.getFontScore());
		this.mEngine.getFontManager().loadFont(this.gameMultiPlayer.getFontVictory());
		
		try {
			this.gameMultiPlayer.pingSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "sfx/ping.wav");
		} catch (final IOException e) {
			Debug.e(e);
		}
	}
	
	

	public static String getChoiceMap() {
		return choiceMap;
	}

	public static void setChoiceMap(String choiceMap) {
		MenuScreen.choiceMap = choiceMap;
	}
	
	public int middleTextureRegionHorizontalSizeByTwo(TextureRegion tr){
		return (tr.getWidth() / 2);
	}
	
	public int middleTextureRegionVerticalSizeByTwo(TextureRegion tr){
		return (tr.getHeight() / 2);
	}
	
	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if (pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			if(modeSelected == 1){
				this.gameSinglePlayer.Pause();
			}
			else if(modeSelected == 2){
				this.gameMultiPlayer.Pause();
			}
			
			//if (this.getEngine().isRunning()) {
				//scene.setChildScene(this.pauseGameScene, false, true, true);
				//menuScreen.getEngine().stop();
			//} else {
				//this.scene.clearChildScene();
				//menuScreen.getEngine().start();
			//}
			return true;
		} else {
			return super.onKeyDown(pKeyCode, pEvent);
		}
	}
	
}
