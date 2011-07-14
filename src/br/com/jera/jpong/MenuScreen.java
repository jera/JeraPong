package br.com.jera.jpong;

import java.io.IOException;

import org.anddev.andengine.audio.sound.SoundFactory;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
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
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import br.com.jera.androidutil.AndroidUtil;
import br.com.jeramobstats.JeraAgent;

public class MenuScreen extends BaseGameActivity implements IOnSceneTouchListener {

	/** ######## GLOBAL ######## **/

	private int CAMERA_WIDTH = 0;
	private int CAMERA_HEIGHT = 0;
	public GameMultiPlayer gameMultiPlayer;
	public GameSinglePlayer gameSinglePlayer;
	public ScoreScreen scoreScreen;
	public static String choiceMap;
	public int sound = 1;
	public boolean LoadingGameSinglePlayer = false;
	public boolean LoadingGameMultiPlayer = false;
	public int ScoreMode = 0;

	/** ######## ENGINE ######## **/

	Camera camera;

	/** ######## MENU ######## **/

	private Texture textureBackground;
	private Texture textureSinglePlayer;
	private Texture textureMultiPlayer;
	private Texture textureHighScore;
	private Texture textureSoundOn;
	private Texture textureSoundOff;
	private Texture textureExit;

	private TextureRegion textureRegionBackground;
	private TextureRegion textureRegionSinglePlayer;
	private TextureRegion textureRegionMultiPlayer;
	private TextureRegion textureRegionHighScore;
	private TextureRegion textureRegionSoundOn;
	private TextureRegion textureRegionSoundOff;
	private TextureRegion textureRegionExit;

	private Sprite spriteSinglePlayer;
	private Sprite spriteMultiPlayer;
	private Sprite spriteHighScore;
	private Sprite spriteSoundOn;
	private Sprite spriteSoundOff;
	public Sprite spriteExit;
	public String selectedMap;
	public int modeSelected = 0;
	public float timePassed;
	public boolean gameRunning = false;
	private boolean onOff = true;
	Vibrator v;

	Scene scene;

	/** ######## MENU ######## **/

	@Override
	public Engine onLoadEngine() {

		this.gameSinglePlayer = new GameSinglePlayer(this);

		CAMERA_HEIGHT = getWindowManager().getDefaultDisplay().getHeight();
		CAMERA_WIDTH = getWindowManager().getDefaultDisplay().getWidth();
		Log.e("width", "" + CAMERA_WIDTH);
		Log.e("height", "" + CAMERA_HEIGHT);
		v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE, new FillResolutionPolicy(), camera).setNeedsSound(true);
		final Engine engine = new Engine(engineOptions);
		try {
			if (MultiTouch.isSupported(this)) {
				engine.setTouchController(new MultiTouchController());
			} else {
				Toast.makeText(this, "This device does not support multitouch", Toast.LENGTH_LONG).show();
			}
		} catch (final MultiTouchException e) {
			Toast.makeText(this, "This android does not support multitouch", Toast.LENGTH_LONG).show();
		}
		return engine;
	}

	@Override
	public void onLoadResources() {
		Log.e("resources menu", "loading");

		this.textureBackground = new Texture(2048, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.textureSinglePlayer = new Texture(1024, 256, TextureOptions.DEFAULT);
		this.textureMultiPlayer = new Texture(1024, 256, TextureOptions.DEFAULT);
		this.textureHighScore = new Texture(256, 64, TextureOptions.DEFAULT);
		this.textureSoundOn = new Texture(256, 64, TextureOptions.DEFAULT);
		this.textureSoundOff = new Texture(256, 64, TextureOptions.DEFAULT);
		this.textureExit = new Texture(256, 64, TextureOptions.DEFAULT);

		this.textureRegionBackground = TextureRegionFactory.createFromAsset(this.textureBackground, this, "gfx/menu/menu_bg.jpg", 0, 0);
		this.textureRegionSinglePlayer = TextureRegionFactory.createFromAsset(this.textureSinglePlayer, this, "gfx/menu/button_singleplayer.png", 0, 0);
		this.textureRegionMultiPlayer = TextureRegionFactory.createFromAsset(this.textureMultiPlayer, this, "gfx/menu/button_multiplayer.png", 0, 0);
		this.textureRegionHighScore = TextureRegionFactory.createFromAsset(this.textureHighScore, this, "gfx/menu/btn_high_scores.png", 0, 0);
		this.textureRegionSoundOn = TextureRegionFactory.createFromAsset(this.textureSoundOn, this, "gfx/menu/button_sound_on.png", 0, 0);
		this.textureRegionSoundOff = TextureRegionFactory.createFromAsset(this.textureSoundOff, this, "gfx/menu/button_sound_off.png", 0, 0);
		this.textureRegionExit = TextureRegionFactory.createFromAsset(this.textureExit, this, "gfx/menu/button_exit.png", 0, 0);

		this.mEngine.getTextureManager().loadTexture(this.textureBackground);
		this.mEngine.getTextureManager().loadTexture(this.textureSinglePlayer);
		this.mEngine.getTextureManager().loadTexture(this.textureMultiPlayer);
		this.mEngine.getTextureManager().loadTexture(this.textureHighScore);
		this.mEngine.getTextureManager().loadTexture(this.textureSoundOff);
		this.mEngine.getTextureManager().loadTexture(this.textureSoundOn);
		this.mEngine.getTextureManager().loadTexture(this.textureExit);

		Log.e("resources menu", "ok");
	}

	@Override
	public Scene onLoadScene() {
		return SceneMenu(this);
	}

	@Override
	public void onLoadComplete() {
	}

	public Scene SceneMenu(final Activity activity) {
		scene = new Scene(1);
		// scene.setOnSceneTouchListener(this);
		/**
		 * Menu background resources
		 */
		final Sprite background = new Sprite(0, 0, this.textureRegionBackground);
		float scalaXBG = (float) CAMERA_WIDTH / (float) this.textureRegionBackground.getWidth();
		float scalaYBG = (float) CAMERA_HEIGHT / (float) this.textureRegionBackground.getHeight();
		background.setScaleCenter(0f, 0f);
		background.setScaleX(scalaXBG);
		background.setScaleY(scalaYBG);
		scene.attachChild(background);

		/**
		 * Loading menu resources
		 */
		final int middleScreenHorizontal = CAMERA_WIDTH / 2;
		int widthSinglePlayer = middleScreenHorizontal - (this.textureRegionSinglePlayer.getWidth() + 20);
		int widthMultiPlayer = middleScreenHorizontal + 20;
		int widthOptions = middleScreenHorizontal - middleTextureRegionHorizontalSizeByTwo(this.textureRegionHighScore);
		int widthSound = widthOptions - (this.textureRegionSoundOn.getWidth() + 20);
		int widthExit = widthOptions + (this.textureRegionExit.getWidth() + 20);

		final int middleScreenVertical = CAMERA_HEIGHT / 2;
		int heightSinglePlayer = middleScreenVertical - middleTextureRegionVerticalSizeByTwo(this.textureRegionSinglePlayer);
		int heightMultiPlayer = heightSinglePlayer;
		int heightOptions = middleScreenVertical + this.textureRegionHighScore.getHeight();
		int heightSound = heightOptions;
		int heightExit = heightOptions;

		/**
		 * Loading single player button resources
		 */

		this.spriteSinglePlayer = new Sprite(widthSinglePlayer, heightSinglePlayer, this.textureRegionSinglePlayer) {
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				JeraAgent.logEvent("SINGLE_PLAYER_START");
				SelectMapDialog dialog = new SelectMapDialog(MenuScreen.this);
				modeSelected = 1;
				dialog.show();
				return false;
			};
		};
		scene.attachChild(this.spriteSinglePlayer);
		scene.registerTouchArea(this.spriteSinglePlayer);

		/**
		 * Loading multi player button resources
		 */
		this.spriteMultiPlayer = new Sprite(widthMultiPlayer, heightMultiPlayer, this.textureRegionMultiPlayer) {
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				JeraAgent.logEvent("MULTI_PLAYER_START");
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
		this.spriteHighScore = new Sprite(widthOptions, heightOptions, this.textureRegionHighScore) {
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				JeraAgent.logEvent("SINGLE_PLAYER_START");
				ScoreMode = 1;
				DataHelper dataHelper = new DataHelper(MenuScreen.this.getBaseContext());
				Cursor cursor = dataHelper.select();
				int x = 0;
				String[] vectorPlayer = new String[5];
				double[] vectorScore = new double[5];

				while (cursor.moveToNext()) {
					String player = cursor.getString(1);
					double score = cursor.getDouble(2);

					vectorPlayer[x] = player;
					vectorScore[x] = score;
					x++;
				}
				cursor.close();
				dataHelper.close();

				MenuScreen.this.LoadingScoreScreen();
				MenuScreen.this.scoreScreen.ScoreScene(vectorPlayer, vectorScore);

				return false;
			};
		};
		scene.attachChild(this.spriteHighScore);
		scene.registerTouchArea(this.spriteHighScore);

		this.spriteSoundOff = new Sprite(widthSound, heightSound, this.textureRegionSoundOff);

		/**
		 * Loading sound button on resources
		 */
		this.spriteSoundOn = new Sprite(widthSound, heightSound, this.textureRegionSoundOn) {
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {				
				if (MenuScreen.this.onOff == true) {
					MenuScreen.this.sound = 0;
					scene.attachChild(spriteSoundOff);
					scene.detachChild(spriteSoundOn);
					MenuScreen.this.onOff = false;
				} else {
					MenuScreen.this.sound = 1;
					scene.attachChild(spriteSoundOn);
					scene.detachChild(spriteSoundOff);
					MenuScreen.this.onOff = true;
				}

				return false;
			};
		};
		scene.attachChild(this.spriteSoundOn);
		scene.registerTouchArea(this.spriteSoundOn);

		/**
		 * Loading exit button resources
		 */
		this.spriteExit = new Sprite(widthExit, heightExit, this.textureRegionExit) {
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

	public void LoadingGameSinglePlayer(String currentMap) {
		
		Toast loading = Toast.makeText(getBaseContext(), "Loading", Toast.LENGTH_SHORT);
		loading.setGravity(0, 0, 220);
		loading.show();

		this.gameSinglePlayer.setCAMERA_HEIGHT(CAMERA_HEIGHT);
		this.gameSinglePlayer.setCAMERA_WIDTH(CAMERA_WIDTH);

		this.gameSinglePlayer.setTextureBackground(new Texture(2048, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.gameSinglePlayer.setTextureRegionBackground(TextureRegionFactory.createFromAsset(this.gameSinglePlayer.getTextureBackground(), this, "gfx/maps/" + currentMap + ".jpg", 0, 0));
		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTextureBackground());

		this.gameSinglePlayer.setTexturePlayer1(new Texture(128, 256, TextureOptions.DEFAULT));
		this.gameSinglePlayer.setTextureBall(new Texture(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.gameSinglePlayer.setTextureScore(new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.gameSinglePlayer.setTextureVictory(new Texture(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.gameSinglePlayer.setTextureReadySetGo(new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.gameSinglePlayer.setTexturePause(new Texture(256, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.gameSinglePlayer.setTextureBarRight(new Texture(128, 1024, TextureOptions.DEFAULT));
		this.gameSinglePlayer.setTextureBGScore(new Texture(256, 128, TextureOptions.DEFAULT));
		this.gameSinglePlayer.setTextureBackgroundPause(new Texture(2048, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.gameSinglePlayer.setTexturePauseContinue(new Texture(256, 64, TextureOptions.DEFAULT));
		this.gameSinglePlayer.setTexturePauseNewGame(new Texture(256, 64, TextureOptions.DEFAULT));
		this.gameSinglePlayer.setTexturePauseMainMenu(new Texture(256, 64, TextureOptions.DEFAULT));

		this.gameSinglePlayer.setTextureRegionPlayer1(TextureRegionFactory.createFromAsset(this.gameSinglePlayer.getTexturePlayer1(), this, "gfx/game/racket_right.png", 0, 0));
		this.gameSinglePlayer.setTextureRegionBall(TextureRegionFactory.createFromAsset(this.gameSinglePlayer.getTextureBall(), this, "gfx/game/disc.png", 0, 0));
		this.gameSinglePlayer.setTextureRegionPause(TextureRegionFactory.createFromAsset(this.gameSinglePlayer.getTexturePause(), this, "gfx/game/pause.png", 0, 0));
		this.gameSinglePlayer.setTextureRegionBarRight(TextureRegionFactory.createFromAsset(this.gameSinglePlayer.getTextureBarRight(), this, "gfx/game/bar_right.png", 0, 0));
		this.gameSinglePlayer.setTextureRegionBGScore(TextureRegionFactory.createFromAsset(this.gameSinglePlayer.getTextureBGScore(), this, "gfx/game/score_bg_sp.png", 0, 0));
		this.gameSinglePlayer.setTextureRegionBackgroundPause(TextureRegionFactory.createFromAsset(this.gameSinglePlayer.getTextureBackgroundPause(), this, "gfx/pause/pause_bg.jpg", 0, 0));
		this.gameSinglePlayer.setTextureRegionPauseContinue(TextureRegionFactory.createFromAsset(this.gameSinglePlayer.getTexturePauseContinue(), this, "gfx/pause/button_continue.png", 0, 0));
		this.gameSinglePlayer.setTextureRegionPauseNewGame(TextureRegionFactory.createFromAsset(this.gameSinglePlayer.getTexturePauseNewGame(), this, "gfx/pause/button_new_game.png", 0, 0));
		this.gameSinglePlayer.setTextureRegionPauseMainMenu(TextureRegionFactory.createFromAsset(this.gameSinglePlayer.getTexturePauseMainMenu(), this, "gfx/pause/button_main_menu.png", 0, 0));
		this.gameSinglePlayer.setFontScore(new Font(this.gameSinglePlayer.getTextureScore(), Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 48, true, Color.WHITE));
		this.gameSinglePlayer.setFontVictory(new Font(this.gameSinglePlayer.getTextureVictory(), Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 30, true, Color.WHITE));
		this.gameSinglePlayer.setFontReadySetGo(new Font(this.gameSinglePlayer.getTextureReadySetGo(), Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 40, true, Color.BLACK));

		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTexturePlayer1());
		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTextureBall());
		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTextureScore());
		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTextureVictory());
		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTextureReadySetGo());
		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTexturePause());
		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTextureBarRight());
		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTextureBGScore());
		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTextureBackgroundPause());
		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTexturePauseContinue());
		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTexturePauseNewGame());
		this.mEngine.getTextureManager().loadTexture(this.gameSinglePlayer.getTexturePauseMainMenu());
		this.mEngine.getFontManager().loadFont(this.gameSinglePlayer.getFontScore());
		this.mEngine.getFontManager().loadFont(this.gameSinglePlayer.getFontVictory());
		this.mEngine.getFontManager().loadFont(this.gameSinglePlayer.getFontReadySetGo());

		try {
			this.gameSinglePlayer.pingSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "sfx/ping.mp3");
			this.gameSinglePlayer.finalSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "sfx/final_sp.mp3");
		} catch (final IOException e) {
			Debug.e(e);
		}
	}

	public void LoadingGameMultiPlayer(String currentMap) {		
		Toast loading = Toast.makeText(getBaseContext(), "Loading", Toast.LENGTH_SHORT);
		loading.setGravity(0, 0, 220);
		loading.show();
		this.gameMultiPlayer = new GameMultiPlayer(this);

		this.gameMultiPlayer.setCAMERA_HEIGHT(CAMERA_HEIGHT);
		this.gameMultiPlayer.setCAMERA_WIDTH(CAMERA_WIDTH);

		this.gameMultiPlayer.setTextureBackground(new Texture(2048, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.gameMultiPlayer.setTexturePlayer1(new Texture(128, 256, TextureOptions.DEFAULT));
		this.gameMultiPlayer.setTexturePlayer2(new Texture(128, 256, TextureOptions.DEFAULT));
		this.gameMultiPlayer.setTextureWin(new Texture(512, 256, TextureOptions.DEFAULT));
		this.gameMultiPlayer.setTextureLoose(new Texture(512, 256, TextureOptions.DEFAULT));
		this.gameMultiPlayer.setTextureBall(new Texture(128, 128, TextureOptions.DEFAULT));
		this.gameMultiPlayer.setTextureScore(new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.gameMultiPlayer.setTextureReadySetGo(new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.gameMultiPlayer.setTextureVictory(new Texture(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.gameMultiPlayer.setTexturePause(new Texture(256, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.gameMultiPlayer.setTextureBarRight(new Texture(128, 1024, TextureOptions.DEFAULT));
		this.gameMultiPlayer.setTextureBarLeft(new Texture(128, 1024, TextureOptions.DEFAULT));
		this.gameMultiPlayer.setTextureBGScore(new Texture(256, 128, TextureOptions.DEFAULT));
		this.gameMultiPlayer.setTextureBackgroundPause(new Texture(2048, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.gameMultiPlayer.setTexturePauseContinue(new Texture(256, 64, TextureOptions.DEFAULT));
		this.gameMultiPlayer.setTexturePauseNewGame(new Texture(256, 64, TextureOptions.DEFAULT));
		this.gameMultiPlayer.setTexturePauseMainMenu(new Texture(256, 64, TextureOptions.DEFAULT));

		this.gameMultiPlayer.setTextureRegionBackground(TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTextureBackground(), this, "gfx/maps/" + currentMap + ".jpg", 0, 0));
		this.gameMultiPlayer.setTextureRegionPlayer1(TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTexturePlayer1(), this, "gfx/game/racket_right.png", 0, 0));
		this.gameMultiPlayer.setTextureRegionPlayer2(TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTexturePlayer2(), this, "gfx/game/racket_left.png", 0, 0));
		this.gameMultiPlayer.setTextureRegionWin(TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTextureWin(), this, "gfx/game/you_win.png", 0, 0));
		this.gameMultiPlayer.setTextureRegionLoose(TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTextureLoose(), this, "gfx/game/you_lose.png", 0, 0));
		this.gameMultiPlayer.setTextureRegionBall(TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTextureBall(), this, "gfx/game/disc.png", 0, 0));
		this.gameMultiPlayer.setTextureRegionPause(TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTexturePause(), this, "gfx/game/pause.png", 0, 0));
		this.gameMultiPlayer.setTextureRegionBarRight(TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTextureBarRight(), this, "gfx/game/bar_right.png", 0, 0));
		this.gameMultiPlayer.setTextureRegionBarLeft(TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTextureBarLeft(), this, "gfx/game/bar_left.png", 0, 0));
		this.gameMultiPlayer.setTextureRegionBGScore(TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTextureBGScore(), this, "gfx/game/score_bg_mp.png", 0, 0));
		this.gameMultiPlayer.setTextureRegionBackgroundPause(TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTextureBackgroundPause(), this, "gfx/pause/pause_bg.jpg", 0, 0));
		this.gameMultiPlayer.setTextureRegionPauseContinue(TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTexturePauseContinue(), this, "gfx/pause/button_continue.png", 0, 0));
		this.gameMultiPlayer.setTextureRegionPauseNewGame(TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTexturePauseNewGame(), this, "gfx/pause/button_new_game.png", 0, 0));
		this.gameMultiPlayer.setTextureRegionPauseMainMenu(TextureRegionFactory.createFromAsset(this.gameMultiPlayer.getTexturePauseMainMenu(), this, "gfx/pause/button_main_menu.png", 0, 0));

		this.gameMultiPlayer.setFontScore(new Font(this.gameMultiPlayer.getTextureScore(), Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 48, true, Color.WHITE));
		this.gameMultiPlayer.setFontVictory(new Font(this.gameMultiPlayer.getTextureVictory(), Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 30, true, Color.WHITE));
		this.gameMultiPlayer.setFontReadySetGo(new Font(this.gameMultiPlayer.getTextureReadySetGo(), Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 40, true, Color.BLACK));

		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTextureBackground());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTexturePlayer1());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTexturePlayer2());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTextureWin());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTextureLoose());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTextureBall());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTextureScore());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTextureReadySetGo());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTextureVictory());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTexturePause());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTextureBarRight());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTextureBarLeft());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTextureBGScore());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTextureBackgroundPause());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTexturePauseContinue());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTexturePauseNewGame());
		this.mEngine.getTextureManager().loadTexture(this.gameMultiPlayer.getTexturePauseMainMenu());
		this.mEngine.getFontManager().loadFont(this.gameMultiPlayer.getFontScore());
		this.mEngine.getFontManager().loadFont(this.gameMultiPlayer.getFontVictory());
		this.mEngine.getFontManager().loadFont(this.gameMultiPlayer.getFontReadySetGo());

		try {
			this.gameMultiPlayer.pingSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "sfx/ping.mp3");
			this.gameMultiPlayer.finalSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "sfx/final_mp.mp3");
		} catch (final IOException e) {
			Debug.e(e);
		}
	}

	public void LoadingScoreScreen() {

		this.scoreScreen = new ScoreScreen(this);

		this.scoreScreen.setCAMERA_HEIGHT(CAMERA_HEIGHT);
		this.scoreScreen.setCAMERA_WIDTH(CAMERA_WIDTH);

		this.scoreScreen.setTextureBackground(new Texture(2048, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.scoreScreen.setTextureBtnNewGame(new Texture(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.scoreScreen.setTextureBtnBack(new Texture(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA));
		this.scoreScreen.setTextureFontScore(new Texture(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA));

		this.scoreScreen.setTextureRegionBackground(TextureRegionFactory.createFromAsset(this.scoreScreen.getTextureBackground(), this, "gfx/score/score_bg.png", 0, 0));
		this.scoreScreen.setTextureRegionBtnNewGame(TextureRegionFactory.createFromAsset(this.scoreScreen.getTextureBtnNewGame(), this, "gfx/score/button_new_game.png", 0, 0));
		this.scoreScreen.setTextureRegionBtnBack(TextureRegionFactory.createFromAsset(this.scoreScreen.getTextureBtnBack(), this, "gfx/score/back_to_menu.png", 0, 0));
		this.scoreScreen.setFontScore(new Font(this.scoreScreen.getTextureFontScore(), Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, true, Color.WHITE));

		this.mEngine.getTextureManager().loadTexture(this.scoreScreen.getTextureBackground());
		this.mEngine.getTextureManager().loadTexture(this.scoreScreen.getTextureBtnNewGame());
		this.mEngine.getTextureManager().loadTexture(this.scoreScreen.getTextureBtnBack());
		this.mEngine.getTextureManager().loadTexture(this.scoreScreen.getTextureFontScore());
		this.mEngine.getFontManager().loadFont(this.scoreScreen.getFontScore());

	}

	public static String getChoiceMap() {
		return choiceMap;
	}

	public static void setChoiceMap(String choiceMap) {
		MenuScreen.choiceMap = choiceMap;
	}

	public int middleTextureRegionHorizontalSizeByTwo(TextureRegion tr) {
		return (tr.getWidth() / 2);
	}

	public int middleTextureRegionVerticalSizeByTwo(TextureRegion tr) {
		return (tr.getHeight() / 2);
	}

	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if (pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			if (modeSelected == 1) {
				this.gameSinglePlayer.GameMenu();
			} else if (modeSelected == 2) {
				this.gameMultiPlayer.GameMenu();
			}
			return true;
		}
		if (pKeyCode == KeyEvent.KEYCODE_BACK && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			if (modeSelected == 1) {
				this.gameSinglePlayer.GameMenu();
			} else if (modeSelected == 2) {
				this.gameMultiPlayer.GameMenu();
			}
			return true;
		} else {
			return super.onKeyDown(pKeyCode, pEvent);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
		case GameSinglePlayer.SUBMIT_DIALOG:
			dialog = new SubmitScore(this);
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	public void nextLevel() {
		this.getEngine().getScene().clearUpdateHandlers();
		this.getEngine().getScene().clearChildScene();
		this.getEngine().getScene().clearEntityModifiers();
		this.getEngine().getScene().clearTouchAreas();
		this.getEngine().getScene().clearUpdateHandlers();
	}

	private static AndroidUtil util;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		util = new AndroidUtil(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		util.onStop(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		util.onStart(this);
	}

}
