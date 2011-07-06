package br.com.jera.jerapong;

import java.text.NumberFormat;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.entity.modifier.AlphaModifier;
import org.anddev.andengine.entity.modifier.ParallelEntityModifier;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.modifier.SequenceEntityModifier;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.CameraScene;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.entity.text.TickerText;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.util.HorizontalAlign;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class GameSinglePlayer implements /*IOnSceneTouchListener,*/ ContactListener {

	/** ######## GLOBAL ######## **/

	private int CAMERA_WIDTH;
	private int CAMERA_HEIGHT;
	private float PTM_RATIO;

	private MenuScreen menuScreen;

	/** ######## GAME ######## **/

	private Texture textureBackground;
	private Texture texturePlayer1;
	private Texture textureBall;
	private Texture textureScore;
	private Texture textureVictory;
	private Texture texturePause;
	private Texture textureBarRight;
	private Texture textureMiddleLine;
	private Texture textureBGScore;
	private Texture textureBackgroundPause;
	private Texture texturePauseContinue;
	private Texture texturePauseNewGame;
	private Texture texturePauseRestart;
	private Texture texturePauseMainMenu;
	

	private TextureRegion textureRegionBackground;
	private TextureRegion textureRegionPlayer1;
	private TextureRegion textureRegionBall;
	private TextureRegion textureRegionPause;
	private TextureRegion textureRegionBarRight;
	private TextureRegion textureRegionMiddleLine;
	private TextureRegion textureRegionBGScore;
	private TextureRegion textureRegionBackgroundPause;
	private TextureRegion textureRegionPauseContinue;
	private TextureRegion textureRegionPauseNewGame;
	private TextureRegion textureRegionPauseRestart;
	private TextureRegion textureRegionPauseMainMenu;

	private PhysicsWorld physicWorld;
	private static final FixtureDef FIXTURE_PLAYERS = PhysicsFactory
			.createFixtureDef(10f, 1f, 0f);
	private static final FixtureDef FIXTURE_BALL = PhysicsFactory
			.createFixtureDef(1f, 1f, 0f); // densidade,restituição,frição

	private Sprite spritePlayer1;
	private Shape shapeTouchPlayer1;
	private Body bodyPlayer1;
	
	private Sprite spriteBarRight;
	private Body bodyBarRight;

	private Sprite spriteBall;
	private Body bodyBall;

	private Font fontScore;
	private Font fontVictory;
	private ChangeableText timePlaying;

	private Runnable runBall;

	final float MAXIMUM_BALL_SPEED = 40f;
	final float MINIMUM_BALL_SPEED = 5f;
	final float WALL_WIDTH = 2;
	float speedX = 0;
	float speedY = 0;
	boolean refreshVelocity = false;
	boolean activeBall = false;

	final int PLAYER_BORDER_OFFSET = 100;
	boolean removeBall = false;
	boolean resetBall = false;
	int playerTime;
	float tempo;

	public Sound pingSound;
	Scene scene;
	private CameraScene pauseGameScene;	
	public String choiceMap;


	/** ######## GAME ######## **/
	
	
	public GameSinglePlayer(MenuScreen menuScreen) {
		this.menuScreen = menuScreen;
	}
	
	public void GameScene() {
		CreateGameMenu();
		
		scene = new Scene(2);
		scene.setOnAreaTouchTraversalFrontToBack();
		
		this.physicWorld = new PhysicsWorld(new Vector2(0, 0), false);
		this.physicWorld.setContactListener(this);
		this.PTM_RATIO = PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;

		final Shape ground = new Rectangle(0, CAMERA_HEIGHT, CAMERA_WIDTH,10);
		final Shape roof = new Rectangle(0, 0, CAMERA_WIDTH, 2);
		final Shape left = new Rectangle(0, 0, 2, CAMERA_HEIGHT);
		final Shape right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(10f,1f, 0f);
		PhysicsFactory.createBoxBody(this.physicWorld, ground,BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.physicWorld, roof,BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.physicWorld, left,BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.physicWorld, right,BodyType.StaticBody, wallFixtureDef);

		scene.getFirstChild().attachChild(ground);
		scene.getFirstChild().attachChild(roof);
		scene.getFirstChild().attachChild(left);
		scene.getFirstChild().attachChild(right);

		scene.registerUpdateHandler(this.physicWorld);
		
		/**
		 * Background
		 */
		final Sprite background = new Sprite(0, 0, this.textureRegionBackground);
		float scalaXBG = (float)CAMERA_WIDTH / (float)this.textureRegionBackground.getWidth();
		float scalaYBG = (float)CAMERA_HEIGHT / (float)this.textureRegionBackground.getHeight();
		background.setScaleCenter(0f,0f);
		background.setScaleX(scalaXBG);
		background.setScaleY(scalaYBG);		
		scene.attachChild(background);
		
		/**
		 * Background score
		 */
		int positionX = 10;
		int positionY = 20;
		final Sprite bgScore = new Sprite(positionX,positionY,this.textureRegionBGScore);
		scene.attachChild(bgScore);
		
		/**
		 * Timer
		 */
		timePlaying = new ChangeableText(20, 30,this.fontScore, "0.0", "00000.0".length());
		scene.attachChild(timePlaying);
		scene.registerUpdateHandler(new TimerHandler(0.1f, true,
				new ITimerCallback() {
					@Override
					public void onTimePassed(final TimerHandler pTimerHandler) {
						GameSinglePlayer.this.tempo = menuScreen.getEngine().getSecondsElapsedTotal() - menuScreen.timePassed;						
						NumberFormat tp = NumberFormat.getInstance();
						tp.setMinimumIntegerDigits(1);
						tp.setMaximumIntegerDigits(10);
						tp.setMinimumFractionDigits(1);
						tp.setMaximumFractionDigits(1);
						timePlaying.setText(tp.format(GameSinglePlayer.this.tempo) + " s");
					}
				}));
		
		/**
		 * Bar Right
		 */
		positionX = CAMERA_WIDTH - textureRegionBarRight.getWidth();
		positionY = 0;
		this.spriteBarRight = new Sprite(positionX,positionY,this.textureRegionBarRight);
		this.bodyBarRight = PhysicsFactory.createBoxBody(this.physicWorld, this.spriteBarRight, BodyType.StaticBody, FIXTURE_PLAYERS);
		scene.attachChild(spriteBarRight);
		
		/**
		 * Player1
		 */
		final int player1PositionX = CAMERA_WIDTH - textureRegionPlayer1.getWidth();
		final int player1PositionY = (CAMERA_HEIGHT / 2) - this.textureRegionPlayer1.getHeight() / 2;
		this.spritePlayer1 = new Sprite(player1PositionX, player1PositionY, this.textureRegionPlayer1);
		this.bodyPlayer1 = PhysicsFactory.createBoxBody(this.physicWorld, this.spritePlayer1, BodyType.StaticBody, FIXTURE_PLAYERS);
		scene.attachChild(spritePlayer1);
		this.physicWorld.registerPhysicsConnector(new PhysicsConnector(this.spritePlayer1, this.bodyPlayer1, true, true));
		this.shapeTouchPlayer1 = new Rectangle((CAMERA_WIDTH / 2) + 100, 10,CAMERA_WIDTH - ((CAMERA_WIDTH / 2) + 100), CAMERA_HEIGHT) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				float touchY = pSceneTouchEvent.getY();
				final float minimumPosY = (spritePlayer1.getHeight() / 2) + 10;
				final float maximumPosY = CAMERA_HEIGHT
						- (spritePlayer1.getHeight() / 2) - 10;
				switch (pSceneTouchEvent.getAction()) {
				case TouchEvent.ACTION_MOVE:
					if (touchY < minimumPosY)
						touchY = minimumPosY;
					if (touchY > maximumPosY)
						touchY = maximumPosY;
					Vector2 newPosition = new Vector2(
							bodyPlayer1.getPosition().x, touchY / PTM_RATIO);
					bodyPlayer1.setTransform(newPosition, 0);
					break;
				}
				return true;
			}
		};
		scene.registerTouchArea(shapeTouchPlayer1);
		
		/**
		 * Middle line
		 */
		final Sprite middleLine = new Sprite(CAMERA_WIDTH / 2, 0, this.textureRegionMiddleLine);
		scene.attachChild(middleLine);
		
		/**
		 * Ball
		 */
		this.spriteBall = new Sprite((CAMERA_WIDTH / 2)	- (this.textureRegionBall.getWidth() / 2), (CAMERA_HEIGHT / 2) - 
				(this.textureRegionBall.getHeight() / 2),this.textureRegionBall);
		this.bodyBall = PhysicsFactory.createCircleBody(this.physicWorld,spriteBall, BodyType.DynamicBody, FIXTURE_BALL);
		scene.attachChild(spriteBall);
		this.physicWorld.registerPhysicsConnector(new PhysicsConnector(spriteBall, bodyBall, true, true));
		this.bodyBall.setLinearVelocity(5, 5);
		activeBall = true;
		
		
		
		scene.setTouchAreaBindingEnabled(true);
		menuScreen.getEngine().setScene(scene);
		Log.e("scene game", "OK");
	}

	@Override
	public void beginContact(Contact contact) {
		Body bodyContact1 = contact.getFixtureA().getBody();
		Body bodyContact2 = contact.getFixtureB().getBody();
		if (bodyContact1.equals(bodyBall) || bodyContact2.equals(bodyBall)) {
			if (bodyContact1.equals(bodyBarRight) || bodyContact2.equals(bodyBarRight)) {
				
				removeBall = true;
				timePlaying.setVisible(false);
				final Scene scene = menuScreen.getEngine().getScene();
				NumberFormat tp = NumberFormat.getInstance();
				tp.setMinimumIntegerDigits(1);
				tp.setMaximumIntegerDigits(10);
				
				tp.setMinimumFractionDigits(1);
				tp.setMaximumFractionDigits(1);
				final String textVictory = new String("Your time: " + tp.format(GameSinglePlayer.this.tempo).toString() + " s");
				final Text text = new TickerText((CAMERA_WIDTH / 2) - (textVictory.length() / 2) * 17,(CAMERA_HEIGHT / 2) - 30, this.fontVictory,textVictory, HorizontalAlign.CENTER, 10);
				text.registerEntityModifier(
					new SequenceEntityModifier(
						new ParallelEntityModifier(
							new AlphaModifier(2, 0.0f, 1.0f),
							new ScaleModifier(2, 0.5f, 1.5f)
						)									
					)
				);
				text.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
				scene.attachChild(text);				
			}
			else{
				pingSound.play();
			}
		}		
		menuScreen.runOnUpdateThread(runBall());

	}
	
	private Runnable runBall() {
		if (runBall == null) {
			this.runBall = new Runnable() {
				@Override
				public void run() {
					if (removeBall) {
						activeBall = false;
						bodyBall.setTransform((CAMERA_WIDTH / PTM_RATIO) + 10,
								(CAMERA_HEIGHT / PTM_RATIO) + 10, 0);
						removeBall = false;
						bodyBall.setLinearVelocity(0f,0f);
						if (resetBall) {
							bodyBall.setTransform((CAMERA_WIDTH / 2)
									/ PTM_RATIO, (CAMERA_HEIGHT / 2)
									/ PTM_RATIO, 0f);							
							if (playerTime == 1) {
								bodyBall.setLinearVelocity(10, 17);
							} else {
								bodyBall.setLinearVelocity(-10, 17);
							}
							resetBall = false;
							activeBall = true;
						}
					}
				}
			};
		}
		return this.runBall;
	}

	@Override
	public void endContact(Contact contact) {
		// Limita velocidades: 4 < vel < 50
		Vector2 speedBall = bodyBall.getLinearVelocity();
		if (speedBall.len() > MAXIMUM_BALL_SPEED) {
			speedX = (MAXIMUM_BALL_SPEED / speedBall.len()) * speedBall.x;
			speedY = (MAXIMUM_BALL_SPEED / speedBall.len()) * speedBall.y;
			refreshVelocity = true;
		}
		if (Math.abs(speedBall.x) < MINIMUM_BALL_SPEED) {
			if (speedBall.x < 0) {
				speedX = speedBall.x - MINIMUM_BALL_SPEED;
			} else {
				speedX = speedBall.x + MINIMUM_BALL_SPEED;
			}
			speedY = speedBall.y;
			refreshVelocity = true;
		}
		menuScreen.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				if (refreshVelocity) {
					if (activeBall) {
						bodyBall.setLinearVelocity(new Vector2(speedX, speedY));
						refreshVelocity = false;
					}
				}
			}
		});

	}
	
	

	@Override
	public void preSolve(Contact pContact) {
	}

	@Override
	public void postSolve(Contact pContact) {

	}
	
	public int getCAMERA_WIDTH() { return CAMERA_WIDTH; }
	public int getCAMERA_HEIGHT() { return CAMERA_HEIGHT; }
	public void setCAMERA_WIDTH(int cAMERA_WIDTH) { CAMERA_WIDTH = cAMERA_WIDTH;}	
	public void setCAMERA_HEIGHT(int cAMERA_HEIGHT) { CAMERA_HEIGHT = cAMERA_HEIGHT; }
	
	public void setTextureBackground(Texture t) { this.textureBackground = t; }
	public void setTexturePlayer1(Texture t) {	this.texturePlayer1 = t; }
	public void setTextureBall(Texture t) { this.textureBall = t; }
	public void setTextureScore(Texture t) { this.textureScore = t; }
	public void setTextureVictory(Texture t) {	this.textureVictory = t; }
	public void setTexturePause(Texture t) { this.texturePause = t; }
	public void setTextureBarRight(Texture t) { this.textureBarRight = t; }
	public void setTextureMiddleLine(Texture t) { this.textureMiddleLine = t; }
	public void setTextureBGScore(Texture t) { this.textureBGScore = t; }
	public void setTextureBackgroundPause(Texture t) { this.textureBackgroundPause= t; }
	public void setTexturePauseContinue(Texture t) { this.texturePauseContinue = t; }
	public void setTexturePauseNewGame(Texture t) { this.texturePauseNewGame = t; }
	public void setTexturePauseRestart(Texture t) { this.texturePauseRestart = t; }
	public void setTexturePauseMainMenu(Texture t) { this.texturePauseMainMenu = t; }	
	public void setTextureRegionBackground(TextureRegion tr) {	this.textureRegionBackground = tr; }
	public void setTextureRegionPlayer1(TextureRegion tr) { this.textureRegionPlayer1 = tr; }
	public void setTextureRegionBall(TextureRegion tr) {	this.textureRegionBall = tr; }
	public void setTextureRegionPause(TextureRegion tr) { this.textureRegionPause = tr;}
	public void setTextureRegionBarRight(TextureRegion tr) { this.textureRegionBarRight = tr; }
	public void setTextureRegionMiddleLine(TextureRegion tr) { this.textureRegionMiddleLine = tr; }
	public void setTextureRegionBGScore(TextureRegion tr) { this.textureRegionBGScore = tr; }
	public void setTextureRegionBackgroundPause(TextureRegion tr) { this.textureRegionBackgroundPause = tr; }
	public void setTextureRegionPauseContinue(TextureRegion tr) { this.textureRegionPauseContinue = tr; }
	public void setTextureRegionPauseNewGame(TextureRegion tr) { this.textureRegionPauseNewGame = tr; }
	public void setTextureRegionPauseRestart(TextureRegion tr) { this.textureRegionPauseRestart = tr; }
	public void setTextureRegionPauseMainMenu(TextureRegion tr) { this.textureRegionPauseMainMenu = tr; }	
	public void setFontScore(Font fontScore) { this.fontScore = fontScore; }
	public void setFontVictory(Font fontVictory) { this.fontVictory = fontVictory; }
	public void setChoiceMap(String choiceMap) { this.choiceMap = choiceMap; }	
	
	public Texture getTextureBackground() { return textureBackground; }	
	public Texture getTexturePlayer1() { return texturePlayer1;	}	
	public Texture getTextureBall() { return textureBall; }	
	public Texture getTextureScore() { return textureScore; }	
	public Texture getTextureVictory() { return textureVictory; }
	public Texture getTexturePause() { return texturePause; }
	public Texture getTextureBarRight() { return textureBarRight; }
	public Texture getTextureMiddleLine() { return textureMiddleLine; }
	public Texture getTextureBGScore() { return textureBGScore; }
	public Texture getTextureBackgroundPause() { return textureBackgroundPause; }
	public Texture getTexturePauseContinue() { return texturePauseContinue; }
	public Texture getTexturePauseNewGame() { return texturePauseNewGame; }
	public Texture getTexturePauseRestart() { return texturePauseRestart; }
	public Texture getTexturePauseMainMenu() { return texturePauseMainMenu; }
	
	public TextureRegion getTextureRegionBackground() {	return textureRegionBackground; }	
	public TextureRegion getTextureRegionPlayer1() { return textureRegionPlayer1; }
	public TextureRegion getTextureRegionBall() { return textureRegionBall;	}
	public TextureRegion getTextureRegionPause() { return textureRegionPause; }
	public TextureRegion getTextureRegionBarRight() { return textureRegionBarRight; }
	public TextureRegion getTextureRegionMiddleLine() { return textureRegionMiddleLine; }
	public TextureRegion getTextureRegionBGScore() { return textureRegionBGScore; }
	public TextureRegion getTextureRegionBackgroundPause() { return textureRegionBackgroundPause; }
	public TextureRegion getTextureRegionPauseContinue() { return textureRegionPauseContinue; }
	public TextureRegion getTextureRegionPauseNewGame() { return textureRegionPauseNewGame; }
	public TextureRegion getTextureRegionPauseRestart() { return textureRegionPauseRestart; }
	public TextureRegion getTextureRegionPauseMainMenu() { return textureRegionPauseMainMenu; }
	public Font getFontScore() { return fontScore; }	
	public Font getFontVictory() { return fontVictory; }	
	public String getChoiceMap() { return choiceMap; }
	
	public void GameMenu(){
		if (menuScreen.getEngine().isRunning()) {
			if(menuScreen.gameRunning){
				scene.setChildScene(this.pauseGameScene, false, true, true);
				menuScreen.getEngine().stop();
			}			
		} else {
			if(menuScreen.gameRunning){
				this.scene.clearChildScene();
				menuScreen.getEngine().start();				
			}

		}
		
	}
	
	public void CreateGameMenu(){
		int posX, posY, hCameraH, hCameraV;
		this.pauseGameScene = new CameraScene(1, this.menuScreen.camera);
		/**
		 * Background
		 */		
		final Sprite background = new Sprite(0, 0, this.textureRegionBackgroundPause);
		float scalaXBG = (float)CAMERA_WIDTH / (float)this.textureRegionBackgroundPause.getWidth();
		float scalaYBG = (float)CAMERA_HEIGHT / (float)this.textureRegionBackgroundPause.getHeight();
		background.setScaleCenter(0f,0f);
		background.setScaleX(scalaXBG);
		background.setScaleY(scalaYBG);
		this.pauseGameScene.attachChild(background);
		
		/**
		 * Button Continue
		 */
		hCameraH = CAMERA_WIDTH / 2;
		hCameraV = CAMERA_HEIGHT / 2;
		posX = hCameraH - middleTextureRegionHorizontalSizeByTwo(textureRegionPauseContinue);
		posY = hCameraV - this.textureRegionPauseContinue.getHeight() - middleTextureRegionVerticalSizeByTwo(textureRegionPauseContinue);
		final Sprite buttonPauseContinue = new Sprite(posX,posY,textureRegionPauseContinue);
		this.pauseGameScene.attachChild(buttonPauseContinue);
		
		/**
		 * Button New Game
		 */
		posX = hCameraH - middleTextureRegionHorizontalSizeByTwo(textureRegionPauseNewGame);
		posY = hCameraV - middleTextureRegionVerticalSizeByTwo(textureRegionPauseNewGame);
		final Sprite buttonPauseNewGame = new Sprite(posX,posY,textureRegionPauseNewGame);
		this.pauseGameScene.attachChild(buttonPauseNewGame);
		
		/**
		 * Button Restart
		 */
		posX = hCameraH - middleTextureRegionHorizontalSizeByTwo(textureRegionPauseRestart);
		posY = hCameraV + middleTextureRegionVerticalSizeByTwo(textureRegionPauseRestart);
		final Sprite buttonPauseRestart = new Sprite(posX,posY,textureRegionPauseRestart);
		this.pauseGameScene.attachChild(buttonPauseRestart);
		
		this.pauseGameScene.setBackgroundEnabled(false);
	}
	
	public int middleTextureRegionHorizontalSizeByTwo(TextureRegion tr){
		return (tr.getWidth() / 2);
	}
	
	public int middleTextureRegionVerticalSizeByTwo(TextureRegion tr){
		return (tr.getHeight() / 2);
	}
	
}
