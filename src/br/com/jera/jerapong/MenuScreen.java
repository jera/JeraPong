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
import org.anddev.andengine.entity.scene.background.ColorBackground;
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
	
	/** ######## ENGINE ######## **/
	
	Camera camera;
	
	/** ######## MENU ######## **/
	
	private Texture textureBackground;
	private Texture textureNameGame;
	private Texture textureSinglePlayer;
	private Texture textureMultiPlayer;
	private Texture textureScore;
	private Texture textureCredits;
	private Texture textureExitGame;
	
	private TextureRegion textureRegionBackground;
	private TextureRegion textureRegionNamegame;
	private TextureRegion textureRegionSinglePlayer;
	private TextureRegion textureRegionMultiPlayer;
	private TextureRegion textureRegionScore;
	private TextureRegion textureRegionCredits;
	private TextureRegion textureRegionExitGame;
	
	private Sprite spriteNameGame;
	private Sprite spriteSinglePlayer;
	private Sprite spriteMultiPlayer;
	private Sprite spriteScore;
	private Sprite spriteCredits;
	private Sprite spriteExitGame;
	public String selectedMap;
	public int modeSelected = 0;
	public float timePassed;
	public boolean gameRunning = false;
	/** ######## MENU ######## **/
	
	@Override
	public Engine onLoadEngine() {		
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
		
		this.textureBackground = new Texture(1024, 1024, TextureOptions.DEFAULT);
		this.textureNameGame = new Texture(256, 256, TextureOptions.DEFAULT);
		this.textureSinglePlayer = new Texture(256,256,TextureOptions.DEFAULT);
		this.textureMultiPlayer = new Texture(256,256,TextureOptions.DEFAULT);
		this.textureScore = new Texture(256,256,TextureOptions.DEFAULT);
		this.textureCredits = new Texture(256,256,TextureOptions.DEFAULT);
		this.textureExitGame = new Texture(256,256,TextureOptions.DEFAULT);
		
		this.textureRegionBackground = TextureRegionFactory.createFromAsset(this.textureBackground, this, "gfx/menu_background.png",0,0);
		this.textureRegionNamegame = TextureRegionFactory.createFromAsset(this.textureNameGame, this, "gfx/menu_name_game.png",0,0);
		this.textureRegionSinglePlayer = TextureRegionFactory.createFromAsset(this.textureSinglePlayer, this, "gfx/menu_single_player.png",0,0);
		this.textureRegionMultiPlayer = TextureRegionFactory.createFromAsset(this.textureMultiPlayer, this, "gfx/menu_multiplayer.png",0,0);
		this.textureRegionScore = TextureRegionFactory.createFromAsset(this.textureScore, this, "gfx/menu_score.png",0,0);
		this.textureRegionCredits = TextureRegionFactory.createFromAsset(this.textureCredits, this, "gfx/menu_creditos.png",0,0);
		this.textureRegionExitGame = TextureRegionFactory.createFromAsset(this.textureExitGame, this, "gfx/menu_exit_game.png",0,0);
		
		this.mEngine.getTextureManager().loadTexture(this.textureBackground);
		this.mEngine.getTextureManager().loadTexture(this.textureNameGame);
		this.mEngine.getTextureManager().loadTexture(this.textureSinglePlayer);
		this.mEngine.getTextureManager().loadTexture(this.textureMultiPlayer);
		this.mEngine.getTextureManager().loadTexture(this.textureExitGame);
		this.mEngine.getTextureManager().loadTexture(this.textureScore);
		this.mEngine.getTextureManager().loadTexture(this.textureCredits);

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
		
		//---BackGround---
		scene.setBackground(new ColorBackground(1,1,1));
		//TextureRegionFactory.createFromAsset(this.textureBackground,this, pAssetPath)
		//final Sprite background = new Sprite(0, 0, this.textureRegionBackground);
		//scene.attachChild(background);
		
		/**
		 * Loading menu resources
		 */
		final int middleScreenHorizontal = CAMERA_WIDTH / 2;
		int widthSinglePlayer = middleScreenHorizontal - middleTextureRegionHorizontalSizeByTwo(this.textureRegionSinglePlayer);
		int widthMultiPlayer = middleScreenHorizontal - middleTextureRegionHorizontalSizeByTwo(textureRegionMultiPlayer);
		int widthScore = middleScreenHorizontal - middleTextureRegionHorizontalSizeByTwo(this.textureRegionScore);
		int widthCredits = middleScreenHorizontal - middleTextureRegionHorizontalSizeByTwo(this.textureRegionCredits);
		int widthExitGame = middleScreenHorizontal - middleTextureRegionHorizontalSizeByTwo(this.textureRegionExitGame);
		int widthNameGame = middleScreenHorizontal - middleTextureRegionHorizontalSizeByTwo(this.textureRegionNamegame);
		
		final int middleScreenVertical = CAMERA_HEIGHT / 2;
		int heightNameGame = middleScreenVertical - middleTextureRegionVerticalSizeByTwo(this.textureRegionNamegame) - 140;
		int heightSinglePlayer = middleScreenVertical -middleTextureRegionVerticalSizeByTwo(this.textureRegionSinglePlayer) - 40;
		int heightMultiPlayer = middleScreenVertical - middleTextureRegionVerticalSizeByTwo(this.textureRegionMultiPlayer) + 30;
		int heightScore = middleScreenVertical - middleTextureRegionVerticalSizeByTwo(this.textureRegionScore) + 90;
		int heightCredits = middleScreenVertical - middleTextureRegionVerticalSizeByTwo(this.textureRegionCredits) + 150;
		int heightExitGame = middleScreenVertical - middleTextureRegionVerticalSizeByTwo(this.textureRegionExitGame) + 200;
		
		/**
		 * Loading single player resources
		 */
		this.spriteNameGame = new Sprite(widthNameGame, heightNameGame, this.textureRegionNamegame);
		scene.getLastChild().attachChild(this.spriteNameGame);
		
		//---Single Player---
		this.spriteSinglePlayer = new Sprite(widthSinglePlayer, heightSinglePlayer, this.textureRegionSinglePlayer){
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				//Toast.makeText(getBaseContext(), "Loading multi player...", 100).show();
				SelectMapDialog dialog = new SelectMapDialog(MenuScreen.this);
				modeSelected = 1;
				dialog.show();				
				return false;
			};
		};
		scene.getLastChild().attachChild(this.spriteSinglePlayer);
		scene.registerTouchArea(this.spriteSinglePlayer);
		
		/**
		 * Loading multi player resources
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
		scene.getLastChild().attachChild(this.spriteMultiPlayer);
		scene.registerTouchArea(this.spriteMultiPlayer);
		
		/**
		 * Loading score resources
		 */
		this.spriteScore = new Sprite(widthScore, heightScore, this.textureRegionScore){
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				return true;
			};
		};
		scene.getLastChild().attachChild(this.spriteScore);
		scene.registerTouchArea(this.spriteScore);
		
		/**
		 * Loading credits resources
		 */
		this.spriteCredits = new Sprite(widthCredits, heightCredits, this.textureRegionCredits){
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				return true;
			};
		};
		scene.getLastChild().attachChild(this.spriteCredits);
		scene.registerTouchArea(this.spriteCredits);
		
		/**
		 * Loading exit resources
		 */
		this.spriteExitGame = new Sprite(widthExitGame, heightExitGame, this.textureRegionExitGame){
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				finish();
				return true;
			};
		};
		scene.getLastChild().attachChild(this.spriteExitGame);
		scene.registerTouchArea(this.spriteExitGame);
		
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
		
		this.gameSinglePlayer.setTextureBackground( new Texture(1024, 1024, TextureOptions.DEFAULT));
		this.gameSinglePlayer.setTexturePlayer1(new Texture(256,256,TextureOptions.DEFAULT));
		this.gameSinglePlayer.setTextureBall(new Texture(64,64,TextureOptions.DEFAULT));
		this.gameSinglePlayer.setTextureScore(new Texture(256,256,TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.gameSinglePlayer.setTextureVictory(new Texture(512,512,TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.gameSinglePlayer.setTexturePause(new Texture(256,64,TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		
		this.gameSinglePlayer.setTextureRegionBackground( TextureRegionFactory.createFromAsset(this.gameSinglePlayer.getTextureBackground(), this, "gfx/" + currentMap + ".png",0,0));
		this.gameSinglePlayer.setTextureRegionPlayer1(TextureRegionFactory.createFromAsset(this.gameSinglePlayer.getTexturePlayer1(), this, "gfx/player.png",0,0));
		this.gameSinglePlayer.setTextureRegionBall(TextureRegionFactory.createFromAsset(this.gameSinglePlayer.getTextureBall(), this, "gfx/ball.png",0,0));
		this.gameSinglePlayer.setTextureRegionPause(TextureRegionFactory.createFromAsset(this.gameSinglePlayer.getTexturePause(), this, "gfx/pause.png", 0, 0));
		this.gameSinglePlayer.setFontScore(new Font(this.gameSinglePlayer.getTextureScore(), Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 48, true, Color.WHITE));
		this.gameSinglePlayer.setFontVictory(new Font(this.gameSinglePlayer.getTextureVictory(), Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 30, true, Color.WHITE));

		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTextureBackground());
		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTexturePlayer1());
		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTextureBall());
		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTextureScore());
		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTextureVictory());
		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTexturePause());
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
		
		this.gameMultiPlayer.setTextureBackground( new Texture(1024, 1024, TextureOptions.DEFAULT));
		this.gameMultiPlayer.setTexturePlayer1(new Texture(256,256,TextureOptions.DEFAULT));
		this.gameMultiPlayer.setTexturePlayer2(new Texture(256,256,TextureOptions.DEFAULT));
		this.gameMultiPlayer.setTextureBall(new Texture(64,64,TextureOptions.DEFAULT));
		this.gameMultiPlayer.setTextureScore(new Texture(256,256,TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.gameMultiPlayer.setTextureVictory(new Texture(512,512,TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.gameMultiPlayer.setTexturePause(new Texture(256,64,TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		
		this.gameMultiPlayer.setTextureRegionBackground( TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTextureBackground(), this, "gfx/" + currentMap + ".png",0,0));
		this.gameMultiPlayer.setTextureRegionPlayer1(TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTexturePlayer1(), this, "gfx/player.png",0,0));
		this.gameMultiPlayer.setTextureRegionPlayer2(TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTexturePlayer2(), this, "gfx/player.png",0,0));
		this.gameMultiPlayer.setTextureRegionBall(TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTextureBall(), this, "gfx/ball.png",0,0));
		this.gameMultiPlayer.setTextureRegionPause(TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTexturePause(), this, "gfx/pause.png", 0, 0));
		this.gameMultiPlayer.setFontScore(new Font(this.gameMultiPlayer.getTextureScore(), Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 48, true, Color.WHITE));
		this.gameMultiPlayer.setFontVictory(new Font(this.gameMultiPlayer.getTextureVictory(), Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 30, true, Color.WHITE));

		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTextureBackground());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTexturePlayer1());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTexturePlayer2());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTextureBall());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTextureScore());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTextureVictory());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTexturePause());
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
