package br.com.jera.jpong;

import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.entity.modifier.ColorModifier;
import org.anddev.andengine.entity.modifier.ParallelEntityModifier;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.modifier.SequenceEntityModifier;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.CameraScene;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import android.util.Log;
import br.com.jeramobstats.JeraAgent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class GameMultiPlayer implements /*IOnSceneTouchListener,*/ ContactListener {

	/** ######## GLOBAL ######## **/

	private int CAMERA_WIDTH;
	private int CAMERA_HEIGHT;
	private int HALF_CAMERA_WIDTH;
	private int HALF_CAMERA_HEIGHT;
	private float PTM_RATIO;

	private MenuScreen menuScreen;

	/** ######## GLOBAL ######## **/

	/** ######## GAME ######## **/

	private Texture textureBackground;
	private Texture texturePlayer1;
	private Texture texturePlayer2;
	private Texture textureBall;
	private Texture textureScore;
	private Texture textureVictory;
	private Texture textureReadySetGo;
	private Texture texturePause;
	private Texture textureBarRight;
	private Texture textureBarLeft;
	private Texture textureMiddleLine;
	private Texture textureBGScore;
	private Texture textureBackgroundPause;
	private Texture texturePauseContinue;
	private Texture texturePauseNewGame;
	private Texture texturePauseMainMenu;
	private Texture textureWin;
	private Texture textureLoose;

	private TextureRegion textureRegionBackground;
	private TextureRegion textureRegionPlayer1;
	private TextureRegion textureRegionPlayer2;
	private TextureRegion textureRegionBall;
	private TextureRegion textureRegionPause;
	private TextureRegion textureRegionBarRight;
	private TextureRegion textureRegionBarLeft;
	private TextureRegion textureRegionMiddleLine;
	private TextureRegion textureRegionBGScore;
	private TextureRegion textureRegionBackgroundPause;
	private TextureRegion textureRegionPauseContinue;
	private TextureRegion textureRegionPauseNewGame;
	private TextureRegion textureRegionPauseMainMenu;
	private TextureRegion textureRegionWin;
	private TextureRegion textureRegionLoose;

	private PhysicsWorld physicWorld;
	private static final FixtureDef FIXTURE_PLAYERS = PhysicsFactory.createFixtureDef(10f, 1.14f, 0f);
	private static final FixtureDef FIXTURE_BALL = PhysicsFactory.createFixtureDef(1f, 1f, 0f); //densidade,restituição,frição

	private Sprite spritePlayer1;
	private Shape shapeTouchPlayer1;
	private Body bodyPlayer1;

	private Sprite spritePlayer2;
	private Shape shapeTouchPlayer2;
	private Body bodyPlayer2;

	private Sprite spriteBarRight;
	private Body bodyBarRight;	

	private Sprite spriteBarLeft;
	private Body bodyBarLeft;

	private Sprite spriteBall;
	private Body bodyBall;

	Sprite buttonNewGame;
	Sprite buttonPauseContinue;

	private Font fontScore;
	private Font fontVictory;
	private Font fontReadySetGo;
	private int pointsPlayer1 = 0;
	private int pointsPlayer2 = 0;
	private ChangeableText scorePlayer1;
	private ChangeableText scorePlayer2;
	private ChangeableText readySetGo;

	final float MAXIMUM_BALL_SPEED = 60f;
	final float MINIMUM_BALL_SPEED = 15f;
	final float WALL_WIDTH = 2;
	float speedX = 0;
	float speedY = 0;
	boolean refreshVelocity = false;
	boolean activeBall = false;
	int playerTime = 0;

	final int PLAYER_BORDER_OFFSET = 100;
	boolean removeBall = false;
	boolean resetBall = false;

	Scene scene;
	CameraScene pauseGameScene;
	CameraScene endGameScene;
	public Sound pingSound;
	public Sound finalSound;
	boolean finalGameSound = false;
	int timerReadySetGo;
	
	private Runnable initialBallVelocityLeft;
	private Runnable initialBallVelocityRight;
	private Runnable movePlayer1;
	private Runnable movePlayer2;
	private Runnable playFinalGameSound;
	private Runnable removeResetBall;
	private Runnable refreshLinearVelocity;
	private Runnable initialBallPosition;
	
	Vector2 newPositionPlayer1 = new Vector2();
	Vector2 newPositionPlayer2 = new Vector2();


	/** ######## GAME ######## **/


	public GameMultiPlayer(MenuScreen menuScreen) {
		this.menuScreen = menuScreen;
	}

	public void GameScene() {		
		CreateGameMenu();

		HALF_CAMERA_WIDTH = CAMERA_WIDTH / 2;
		HALF_CAMERA_HEIGHT = CAMERA_HEIGHT / 2;

		scene = new Scene(3);
		scene.setOnAreaTouchTraversalFrontToBack();

		this.physicWorld = new FixedStepPhysicsWorld(50,new Vector2(0,0),false);//PhysicsWorld(new Vector2(0,0),false);
		this.physicWorld.setContactListener(this);
		this.PTM_RATIO = PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;

		final Shape ground = new Rectangle(0, CAMERA_HEIGHT - 10, CAMERA_WIDTH, 10);
		final Shape roof = new Rectangle(0, 0, CAMERA_WIDTH, 10);
		final Shape left = new Rectangle(0, 0, 2, CAMERA_HEIGHT);
		final Shape right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(10f, 1f, 0f);
		PhysicsFactory.createBoxBody(this.physicWorld, ground, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.physicWorld, roof, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.physicWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.physicWorld, right, BodyType.StaticBody, wallFixtureDef);

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

		scorePlayer1 = new ChangeableText((CAMERA_WIDTH / 2) - 50,30,this.fontScore,"0","0".length());
		scorePlayer2 = new ChangeableText((CAMERA_WIDTH / 2) + 20,30,this.fontScore,"0","0".length());
		readySetGo = new ChangeableText((CAMERA_WIDTH / 2) - 32,(CAMERA_HEIGHT / 2) - 25,this.fontReadySetGo,"Tap","Tap".length());
		//readySetGo.setPosition(HALF_CAMERA_WIDTH - readySetGo.getWidth() / 2,HALF_CAMERA_HEIGHT - readySetGo.getHeight() / 2);
		//readySetGo.setVisible(false);

		scene.attachChild(scorePlayer1);		
		scene.attachChild(scorePlayer2);		

		/**
		 * Bar Right
		 */
		int positionX = CAMERA_WIDTH - textureRegionBarRight.getWidth();
		int positionY = 0;
		this.spriteBarRight = new Sprite(positionX,positionY,this.textureRegionBarRight);
		this.bodyBarRight = PhysicsFactory.createBoxBody(this.physicWorld, this.spriteBarRight, BodyType.StaticBody, wallFixtureDef);
		scene.attachChild(spriteBarRight);

		/**
		 * Bar Left
		 */
		this.spriteBarLeft = new Sprite(0,0,this.textureRegionBarLeft);
		this.bodyBarLeft = PhysicsFactory.createBoxBody(this.physicWorld, this.spriteBarLeft, BodyType.StaticBody, wallFixtureDef);
		scene.attachChild(spriteBarLeft);

		/**
		 * Player Right (1)
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
					newPositionPlayer1.x = bodyPlayer1.getPosition().x;
					newPositionPlayer1.y = touchY / PTM_RATIO;
					menuScreen.runOnUpdateThread(movePlayer1());
					break;
				}
				return true;
			}
		};
		scene.registerTouchArea(shapeTouchPlayer1);

		/**
		 * Player Left (2)
		 */
		final int player2PositionX = 0;
		final int player2PositionY = (CAMERA_HEIGHT / 2) - this.textureRegionPlayer1.getHeight() / 2;
		this.spritePlayer2 = new Sprite(player2PositionX, player2PositionY, this.textureRegionPlayer2);
		this.bodyPlayer2 = PhysicsFactory.createBoxBody(this.physicWorld, this.spritePlayer2, BodyType.StaticBody, FIXTURE_PLAYERS);
		scene.attachChild(spritePlayer2);
		this.physicWorld.registerPhysicsConnector(new PhysicsConnector(this.spritePlayer2, this.bodyPlayer2, true, true));
		this.shapeTouchPlayer2 = new Rectangle(0,0,CAMERA_WIDTH - ((CAMERA_WIDTH / 2) + 100),CAMERA_HEIGHT){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				float touchY = pSceneTouchEvent.getY();
				final float minimumPosY = (spritePlayer2.getHeight() / 2) + 10;
				final float maximumPosY = CAMERA_HEIGHT - (spritePlayer2.getHeight() / 2) - 10;
				switch(pSceneTouchEvent.getAction()) {
				case TouchEvent.ACTION_MOVE:
					if(touchY < minimumPosY) touchY = minimumPosY;
					if(touchY > maximumPosY) touchY = maximumPosY;
					newPositionPlayer2.x = bodyPlayer2.getPosition().x;
					newPositionPlayer2.y = touchY / PTM_RATIO;
					menuScreen.runOnUpdateThread(movePlayer2());
					break;
				}
				return true;
			}
		};
		scene.registerTouchArea(shapeTouchPlayer2);

		/**
		 * Ball
		 */
		playerTime = 1;
		this.spriteBall = new Sprite((CAMERA_WIDTH / 2) - (this.textureRegionBall.getWidth() / 2), (CAMERA_HEIGHT / 2) - (this.textureRegionBall.getHeight() / 2), this.textureRegionBall){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
				final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
					readySetGo.setVisible(false);
					if(playerTime == 1){
						menuScreen.runOnUpdateThread(initialBallVelocityLeft());
						//bodyBall.setLinearVelocity(-8,12);
					}else{
						menuScreen.runOnUpdateThread(initialBallVelocityRight());
						//bodyBall.setLinearVelocity(8,12);
					}
					activeBall = true;
				return false;
			}
		};
		scene.registerTouchArea(spriteBall);
		this.bodyBall = PhysicsFactory.createCircleBody(this.physicWorld,spriteBall,BodyType.DynamicBody,FIXTURE_BALL);
		scene.attachChild(spriteBall);
		this.physicWorld.registerPhysicsConnector(new PhysicsConnector(spriteBall, bodyBall, true, true));			

		scene.attachChild(readySetGo);
		scene.setTouchAreaBindingEnabled(true);

		menuScreen.getEngine().setScene(scene);

		Log.e("scene game", "OK");
	}
	
	private Runnable movePlayer1(){
		if(movePlayer1 == null){
			this.movePlayer1 = new Runnable(){
				@Override
				public void run(){
					bodyPlayer1.setTransform(newPositionPlayer1, 0);
				}
			};
		}
		return this.movePlayer1;
	}
	
	private Runnable movePlayer2(){
		if(movePlayer2 == null){
			this.movePlayer2 = new Runnable(){
				@Override
				public void run(){
					bodyPlayer2.setTransform(newPositionPlayer2, 0);
				}
			};
		}
		return this.movePlayer2;
	}
	
	private Runnable initialBallVelocityLeft(){
		if(initialBallVelocityLeft == null){
			this.initialBallVelocityLeft = new Runnable(){
				@Override
				public void run(){
					bodyBall.setLinearVelocity(-17,15);
				}
			};
		}
		return this.initialBallVelocityLeft;
	}
	
	private Runnable initialBallVelocityRight(){
		if(initialBallVelocityRight == null){
			this.initialBallVelocityRight = new Runnable(){
				@Override
				public void run(){
					bodyBall.setLinearVelocity(-17,15);
				}
			};
		}
		return this.initialBallVelocityRight;
	}

	@Override
	public void beginContact(Contact contact) {
		final int vibrateTime = 60;
		Body bodyContact1 = contact.getFixtureA().getBody();
		Body bodyContact2 = contact.getFixtureB().getBody();
		if(bodyContact1.equals(bodyBall) || bodyContact2.equals(bodyBall)){
			if(bodyContact1.equals(bodyBarLeft) || bodyContact2.equals(bodyBarLeft)){
				menuScreen.v.vibrate(vibrateTime);
				this.scorePlayer2.setText("" + ++this.pointsPlayer2);
				this.scorePlayer2.registerEntityModifier(
						new SequenceEntityModifier(
								new ParallelEntityModifier(
										new ScaleModifier(0.3f,1,4f),
										new ColorModifier(0.3f,1,0,1,0,1,0)
								),
								new ParallelEntityModifier(
										new ScaleModifier(0.3f,4f,1),
										new ColorModifier(0.3f,0,1,0,1,0,1)
								)
						)
				);
				Log.e("player2","touch");
				removeBall = true;
				if(this.pointsPlayer2 >= 7){
					finalGameSound = true;
					menuScreen.gameRunning = false;
					CreateEndGameMenu(2);
					scene.setChildScene(this.endGameScene, false, true, true);
				}
				else{
					readySetGo.setVisible(true);
					resetBall = true;
					playerTime = 1;					
				}				
			}else if(bodyContact1.equals(bodyBarRight) || bodyContact2.equals(bodyBarRight)){
				menuScreen.v.vibrate(vibrateTime);
				this.scorePlayer1.setText("" + ++this.pointsPlayer1);
				this.scorePlayer1.registerEntityModifier(
						new SequenceEntityModifier(
							new ParallelEntityModifier(
								new ScaleModifier(0.3f,1,4f),
								new ColorModifier(0.3f,1,0,1,0,1,0)
							),
							new ParallelEntityModifier(
								new ScaleModifier(0.3f,4f,1),
								new ColorModifier(0.3f,0,1,0,1,0,1)
							)
						)
				);
				Log.e("player1","touch");
				removeBall = true;
				if(this.pointsPlayer1 >= 7){
					finalGameSound = true;
					menuScreen.gameRunning = false;
					CreateEndGameMenu(1);
					scene.setChildScene(this.endGameScene, false, true, true);
				}
				else{
					readySetGo.setVisible(true);
					resetBall = true;
					playerTime = 2;
				}
			}
			if(menuScreen.sound == 1){
				pingSound.play();
			}
		}		
		menuScreen.runOnUpdateThread(removeResetBall());
		menuScreen.runOnUpdateThread(playFinalGameSound());
	}
	
	public Runnable playFinalGameSound(){
		if(playFinalGameSound == null){
			playFinalGameSound = new Runnable(){
				@Override
				public void run(){
					if(finalGameSound){
						if(menuScreen.sound == 1){
							finalSound.play();
							finalGameSound = false;
						}
					}
				}
			};
		}
		return this.playFinalGameSound;
	}
	
	public Runnable removeResetBall(){
		if(removeResetBall == null){
			removeResetBall = new Runnable(){
				@Override
				public void run(){
					if(removeBall){
						activeBall = false;
						bodyBall.setTransform((CAMERA_WIDTH / PTM_RATIO) + 10,(CAMERA_HEIGHT / PTM_RATIO) + 10, 0);
						bodyBall.setLinearVelocity(0f,0f);
						removeBall = false;
						if(resetBall){
							bodyBall.setTransform(HALF_CAMERA_WIDTH / PTM_RATIO, HALF_CAMERA_HEIGHT / PTM_RATIO,0);
							resetBall = false;
						}
					}
				}
			};
		}
		return this.removeResetBall;
	}

	@Override
	public void endContact(Contact contact) {
		//Limita velocidades: 4 < vel < 50
		Vector2 speedBall = bodyBall.getLinearVelocity();
		if(speedBall.len() > MAXIMUM_BALL_SPEED){
			speedX = (MAXIMUM_BALL_SPEED / speedBall.len()) * speedBall.x;
			speedY = (MAXIMUM_BALL_SPEED / speedBall.len()) * speedBall.y;
			refreshVelocity = true;
		}
		/*if (Math.abs(speedBall.y) < MINIMUM_BALL_SPEED) {
			if (speedBall.y < 0) {
				speedY = speedBall.y - MINIMUM_BALL_SPEED;
			} else {
				speedY = speedBall.y + MINIMUM_BALL_SPEED;
			}
			if(!refreshVelocity) speedX = speedBall.x;
			refreshVelocity = true;
		}*/
		menuScreen.runOnUpdateThread(refreshLinearVelocity());

	}
	
	public Runnable refreshLinearVelocity(){
		if(refreshLinearVelocity == null){
			refreshLinearVelocity = new Runnable(){
				@Override
				public void run(){
					if(refreshVelocity){
						if(activeBall){
							bodyBall.setLinearVelocity(new Vector2(speedX,speedY));
							refreshVelocity = false;
						}
					}
				}
			};
		}
		return this.refreshLinearVelocity;
	}


	@Override
	public void preSolve(Contact pContact) {
	}

	@Override
	public void postSolve(Contact pContact) {
	}		

	public void GameMenu(){
		if(menuScreen.gameRunning){
			menuScreen.gameRunning = false;
			if(pauseGameScene.getChildCount() == 4){
				pauseGameScene.attachChild(buttonPauseContinue);
			}
			scene.setChildScene(this.pauseGameScene, false, true, true);
		}
	}

	public int middleTextureRegionHorizontalSizeByTwo(TextureRegion tr){
		return (tr.getWidth() / 2);
	}

	public int middleTextureRegionVerticalSizeByTwo(TextureRegion tr){
		return (tr.getHeight() / 2);
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
		posY = hCameraV - this.textureRegionPauseContinue.getHeight() - middleTextureRegionVerticalSizeByTwo(textureRegionPauseContinue) - 15;
		buttonPauseContinue = new Sprite(posX,posY,textureRegionPauseContinue){
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				scene.clearChildScene();
				menuScreen.gameRunning = true;
				return false;
			};
		};
		this.pauseGameScene.attachChild(buttonPauseContinue);
		this.pauseGameScene.registerTouchArea(buttonPauseContinue);

		/**
		 * Button New Game
		 */
		posX = hCameraH - middleTextureRegionHorizontalSizeByTwo(textureRegionPauseNewGame);
		posY = hCameraV - middleTextureRegionVerticalSizeByTwo(textureRegionPauseNewGame);
		final Sprite buttonPauseNewGame = new Sprite(posX,posY,textureRegionPauseNewGame){
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				JeraAgent.logEvent("MP_TO_NEW_GAME");
				scene.clearChildScene();
				menuScreen.gameRunning = true;
				pointsPlayer1 = 0;
				pointsPlayer2 = 0;
				scorePlayer1.setText("0");
				scorePlayer2.setText("0");
				readySetGo.setVisible(true);
				menuScreen.runOnUpdateThread(initialBallPosition());			
				return false;
			};
		};
		this.pauseGameScene.registerTouchArea(buttonPauseNewGame);
		this.pauseGameScene.attachChild(buttonPauseNewGame);

		/**
		 * Button Main Menu
		 */
		posX = hCameraH - middleTextureRegionHorizontalSizeByTwo(textureRegionPauseMainMenu);
		posY = hCameraV + middleTextureRegionVerticalSizeByTwo(textureRegionPauseMainMenu) + 15;
		final Sprite buttonPauseMainMenu = new Sprite(posX,posY,textureRegionPauseMainMenu){
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				JeraAgent.logEvent("MP_TO_MAIN_MENU");
				scene.clearChildScene();
				menuScreen.getEngine().setScene(menuScreen.scene);
				return false;
			};
		};
		this.pauseGameScene.registerTouchArea(buttonPauseMainMenu);
		this.pauseGameScene.attachChild(buttonPauseMainMenu);

		this.pauseGameScene.setBackgroundEnabled(false);
		this.pauseGameScene.setTouchAreaBindingEnabled(true);
	}
	
	public Runnable initialBallPosition(){
		if(initialBallPosition == null){
			initialBallPosition = new Runnable(){
				@Override
				public void run(){
					bodyBall.setTransform(HALF_CAMERA_WIDTH / PTM_RATIO, HALF_CAMERA_HEIGHT / PTM_RATIO, 0f);					
				}
			};
		}
		return this.initialBallPosition;
	}

	public void CreateEndGameMenu(int winner){
		int posXWin, posYWin, posXLoose, posYLoose, posTemp;
		int hCameraH = CAMERA_WIDTH / 2;
		int hCameraV = CAMERA_HEIGHT / 2;

		this.endGameScene = new CameraScene(1, this.menuScreen.camera);		

		//Positions if player1 win
		posXLoose = hCameraH + 40;
		posYLoose = hCameraV - middleTextureRegionVerticalSizeByTwo(textureRegionLoose);
		posXWin = hCameraH - textureRegionWin.getWidth() - 40;
		posYWin = hCameraV - middleTextureRegionVerticalSizeByTwo(textureRegionWin);

		//Positions if player2 win
		if(winner == 2){
			posTemp = posXWin;
			posXWin = posXLoose;
			posXLoose = posTemp;
			posTemp = posYWin;
			posYWin = posYLoose;
			posYLoose = posTemp;
		}

		//Adding sprites
		final Sprite spriteWin = new Sprite(posXWin,posYWin,textureRegionWin){
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				scene.clearChildScene();
				if(pauseGameScene.getChildCount() == 5){
					pauseGameScene.detachChild(buttonPauseContinue);
				}
				scene.setChildScene(pauseGameScene, false, true, true);
				return false;
			};
		};
		final Sprite spriteLoose = new Sprite(posXLoose,posYLoose,textureRegionLoose){
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				scene.clearChildScene();
				if(pauseGameScene.getChildCount() == 5){
					pauseGameScene.detachChild(buttonPauseContinue);
				}
				scene.setChildScene(pauseGameScene, false, true, true);
				return false;
			};
		};

		//Rotating
		if(winner == 1){
			spriteWin.setRotation(90);
			spriteLoose.setRotation(-90);
		}else{
			spriteWin.setRotation(-90);
			spriteLoose.setRotation(90);
		}

		this.endGameScene.registerTouchArea(spriteWin);
		this.endGameScene.registerTouchArea(spriteLoose);
		this.endGameScene.attachChild(spriteWin);
		this.endGameScene.attachChild(spriteLoose);

		/*final int posXContinue = hCameraH - middleTextureRegionHorizontalSizeByTwo(textureRegionPauseContinue);
		final int posYContinue = hCameraV + this.textureRegionPauseContinue.getHeight() + 80;
		final Sprite buttonContinue = new Sprite(posXContinue,posYContinue,textureRegionPauseContinue){
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				scene.clearChildScene();
				if(pauseGameScene.getChildCount() == 5){
					pauseGameScene.detachChild(buttonPauseContinue);
				}
				scene.setChildScene(pauseGameScene, false, true, true);
				return false;
			};
		};
		this.endGameScene.registerTouchArea(buttonContinue);
		this.endGameScene.attachChild(buttonContinue);*/

		this.endGameScene.setBackgroundEnabled(false);
	}

	public void setCAMERA_WIDTH(int cAMERA_WIDTH) { CAMERA_WIDTH = cAMERA_WIDTH; }
	public void setCAMERA_HEIGHT(int cAMERA_HEIGHT) { CAMERA_HEIGHT = cAMERA_HEIGHT; }
	public int getCAMERA_WIDTH() { return CAMERA_WIDTH; }
	public int getCAMERA_HEIGHT() { return CAMERA_HEIGHT; }	

	public void setTextureBackground(Texture t) { this.textureBackground = t; }
	public void setTexturePlayer1(Texture t) {	this.texturePlayer1 = t; }
	public void setTexturePlayer2(Texture t) {	this.texturePlayer2 = t; }
	public void setTextureWin(Texture t) {	this.textureWin = t; }
	public void setTextureLoose(Texture t) {	this.textureLoose = t; }	
	public void setTextureBall(Texture t) { this.textureBall = t; }
	public void setTextureScore(Texture t) { this.textureScore = t; }
	public void setTextureReadySetGo(Texture t) { this.textureReadySetGo = t; }
	public void setTextureVictory(Texture t) {	this.textureVictory = t; }
	public void setTexturePause(Texture t) { this.texturePause = t; }
	public void setTextureBarRight(Texture t) { this.textureBarRight = t; }
	public void setTextureBarLeft(Texture t) { this.textureBarLeft = t; }
	public void setTextureMiddleLine(Texture t) { this.textureMiddleLine = t; }
	public void setTextureBGScore(Texture t) { this.textureBGScore = t; }
	public void setTextureBackgroundPause(Texture t) { this.textureBackgroundPause= t; }
	public void setTexturePauseContinue(Texture t) { this.texturePauseContinue = t; }
	public void setTexturePauseNewGame(Texture t) { this.texturePauseNewGame = t; }
	public void setTexturePauseMainMenu(Texture t) { this.texturePauseMainMenu = t; }	
	public void setTextureRegionBackground(TextureRegion tr) {	this.textureRegionBackground = tr; }
	public void setTextureRegionPlayer1(TextureRegion tr) { this.textureRegionPlayer1 = tr; }
	public void setTextureRegionPlayer2(TextureRegion tr) { this.textureRegionPlayer2 = tr; }
	public void setTextureRegionWin(TextureRegion tr) { this.textureRegionWin = tr; }
	public void setTextureRegionLoose(TextureRegion tr) { this.textureRegionLoose = tr; }
	public void setTextureRegionBall(TextureRegion tr) {	this.textureRegionBall = tr; }
	public void setTextureRegionPause(TextureRegion tr) { this.textureRegionPause = tr;}
	public void setTextureRegionBarRight(TextureRegion tr) { this.textureRegionBarRight = tr; }
	public void setTextureRegionBarLeft(TextureRegion tr) { this.textureRegionBarLeft = tr; }
	public void setTextureRegionMiddleLine(TextureRegion tr) { this.textureRegionMiddleLine = tr; }
	public void setTextureRegionBGScore(TextureRegion tr) { this.textureRegionBGScore = tr; }
	public void setTextureRegionBackgroundPause(TextureRegion tr) { this.textureRegionBackgroundPause = tr; }
	public void setTextureRegionPauseContinue(TextureRegion tr) { this.textureRegionPauseContinue = tr; }
	public void setTextureRegionPauseNewGame(TextureRegion tr) { this.textureRegionPauseNewGame = tr; }
	public void setTextureRegionPauseMainMenu(TextureRegion tr) { this.textureRegionPauseMainMenu = tr; }	
	public void setFontScore(Font fontScore) { this.fontScore = fontScore; }
	public void setFontVictory(Font fontVictory) { this.fontVictory = fontVictory; }
	public void setFontReadySetGo(Font fontReadySetGo) { this.fontReadySetGo = fontReadySetGo; }

	public Texture getTextureBackground() { return textureBackground; }	
	public Texture getTexturePlayer1() { return texturePlayer1;	}
	public Texture getTexturePlayer2() { return texturePlayer2;	}
	public Texture getTextureWin() { return textureWin;	}
	public Texture getTextureLoose() { return textureLoose;	}
	public Texture getTextureBall() { return textureBall; }	
	public Texture getTextureScore() { return textureScore; }
	public Texture getTextureReadySetGo() { return textureReadySetGo; }
	public Texture getTextureVictory() { return textureVictory; }
	public Texture getTexturePause() { return texturePause; }
	public Texture getTextureBarRight() { return textureBarRight; }
	public Texture getTextureBarLeft() { return textureBarLeft; }
	public Texture getTextureMiddleLine() { return textureMiddleLine; }
	public Texture getTextureBGScore() { return textureBGScore; }
	public Texture getTextureBackgroundPause() { return textureBackgroundPause; }
	public Texture getTexturePauseContinue() { return texturePauseContinue; }
	public Texture getTexturePauseNewGame() { return texturePauseNewGame; }
	public Texture getTexturePauseMainMenu() { return texturePauseMainMenu; }

	public TextureRegion getTextureRegionBackground() {	return textureRegionBackground; }	
	public TextureRegion getTextureRegionPlayer1() { return textureRegionPlayer1; }
	public TextureRegion getTextureRegionPlayer2() { return textureRegionPlayer2; }
	public TextureRegion getTextureRegionWin() { return textureRegionWin; }
	public TextureRegion getTextureRegionLoose() { return textureRegionLoose; }
	public TextureRegion getTextureRegionBall() { return textureRegionBall;	}
	public TextureRegion getTextureRegionPause() { return textureRegionPause; }
	public TextureRegion getTextureRegionBarRight() { return textureRegionBarRight; }
	public TextureRegion getTextureRegionBarLeft() { return textureRegionBarLeft; }
	public TextureRegion getTextureRegionMiddleLine() { return textureRegionMiddleLine; }
	public TextureRegion getTextureRegionBGScore() { return textureRegionBGScore; }
	public TextureRegion getTextureRegionBackgroundPause() { return textureRegionBackgroundPause; }
	public TextureRegion getTextureRegionPauseContinue() { return textureRegionPauseContinue; }
	public TextureRegion getTextureRegionPauseNewGame() { return textureRegionPauseNewGame; }
	public TextureRegion getTextureRegionPauseMainMenu() { return textureRegionPauseMainMenu; }
	public Font getFontScore() { return fontScore; }	
	public Font getFontVictory() { return fontVictory; }
	public Font getFontReadySetGo() { return fontReadySetGo; }

}
