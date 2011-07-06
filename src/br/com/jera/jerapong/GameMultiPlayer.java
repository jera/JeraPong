package br.com.jera.jerapong;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.entity.modifier.AlphaModifier;
import org.anddev.andengine.entity.modifier.ParallelEntityModifier;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.modifier.SequenceEntityModifier;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.CameraScene;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.entity.text.TickerText;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
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

public class GameMultiPlayer implements /*IOnSceneTouchListener,*/ ContactListener {

	/** ######## GLOBAL ######## **/

	private int CAMERA_WIDTH;
	private int CAMERA_HEIGHT;
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
	private Texture texturePause;
	private Texture textureBarRight;
	private Texture textureBarLeft;
	private Texture textureMiddleLine;
	private Texture textureBGScore;

	private TextureRegion textureRegionBackground;
	private TextureRegion textureRegionPlayer1;
	private TextureRegion textureRegionPlayer2;
	private TextureRegion textureRegionBall;
	private TextureRegion textureRegionPause;
	private TextureRegion textureRegionBarRight;
	private TextureRegion textureRegionBarLeft;
	private TextureRegion textureRegionMiddleLine;
	private TextureRegion textureRegionBGScore;

	private PhysicsWorld physicWorld;
	private static final FixtureDef FIXTURE_PLAYERS = PhysicsFactory.createFixtureDef(10f, 1.2f, 0f);
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

	private Font fontScore;
	private Font fontVictory;
	private int pointsPlayer1 = 0;
	private int pointsPlayer2 = 0;
	private ChangeableText scorePlayer1;
	private ChangeableText scorePlayer2;

	private Body bodyLeft;
	private Body bodyRight;

	final float MAXIMUM_BALL_SPEED = 40f;
	final float MINIMUM_BALL_SPEED = 5f;
	final float WALL_WIDTH = 2;
	float speedX = 0;
	float speedY = 0;
	boolean refreshVelocity = false;
	boolean activeBall = false;
	int playerTime;

	final int PLAYER_BORDER_OFFSET = 100;
	boolean removeBall = false;
	boolean resetBall = false;
	
	CameraScene pauseGameScene;
	public Sound pingSound;
	
	/** ######## GAME ######## **/
	
	
	public GameMultiPlayer(MenuScreen menuScreen) {
		this.menuScreen = menuScreen;
	}
	
	public void GameScene() {
		this.pauseGameScene = new CameraScene(1, this.menuScreen.camera);
		final int x = CAMERA_WIDTH / 2 - this.textureRegionPause.getWidth() / 2;
		final int y = CAMERA_HEIGHT / 2 - this.textureRegionPause.getHeight() / 2;
		final Sprite pausedSprite = new Sprite(x, y, this.textureRegionPause);
		this.pauseGameScene.getLastChild().attachChild(pausedSprite);
		this.pauseGameScene.setBackgroundEnabled(false);
		
		final Scene scene = new Scene(2);
		scene.setOnAreaTouchTraversalFrontToBack();

		this.physicWorld = new PhysicsWorld(new Vector2(0,0),false);
		this.physicWorld.setContactListener(this);
		this.PTM_RATIO = PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;

		final Shape ground = new Rectangle(0, CAMERA_HEIGHT - 10, CAMERA_WIDTH, 10);
		final Shape roof = new Rectangle(0, 0, CAMERA_WIDTH, 10);
		final Shape left = new Rectangle(0, 0, 2, CAMERA_HEIGHT);
		final Shape right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(10f, 1f, 0f);
		PhysicsFactory.createBoxBody(this.physicWorld, ground, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.physicWorld, roof, BodyType.StaticBody, wallFixtureDef);
		this.bodyLeft = PhysicsFactory.createBoxBody(this.physicWorld, left, BodyType.StaticBody, wallFixtureDef);
		this.bodyRight = PhysicsFactory.createBoxBody(this.physicWorld, right, BodyType.StaticBody, wallFixtureDef);

		scene.getFirstChild().attachChild(ground);
		scene.getFirstChild().attachChild(roof);
		scene.getFirstChild().attachChild(left);
		scene.getFirstChild().attachChild(right);

		scene.registerUpdateHandler(this.physicWorld);

		//---BackGround---
		final Sprite background = new Sprite(0, 0, this.textureRegionBackground);
		scene.attachChild(background);

		scorePlayer1 = new ChangeableText((CAMERA_WIDTH / 2) - 50,30,this.fontScore,"0","0".length());
		scorePlayer2 = new ChangeableText((CAMERA_WIDTH / 2) + 20,30,this.fontScore,"0","0".length());

		scene.getLastChild().attachChild(scorePlayer1);		
		scene.getLastChild().attachChild(scorePlayer2);
		
		/**
		 * Bar Right
		 */
		int positionX = CAMERA_WIDTH - textureRegionBarRight.getWidth();
		int positionY = 0;
		this.spriteBarRight = new Sprite(positionX,positionY,this.textureRegionBarRight);
		this.bodyBarRight = PhysicsFactory.createBoxBody(this.physicWorld, this.spriteBarRight, BodyType.StaticBody, wallFixtureDef);
		scene.getLastChild().attachChild(spriteBarRight);
		
		/**
		 * Bar Left
		 */
		this.spriteBarLeft = new Sprite(0,0,this.textureRegionBarLeft);
		this.bodyBarLeft = PhysicsFactory.createBoxBody(this.physicWorld, this.spriteBarLeft, BodyType.StaticBody, wallFixtureDef);
		scene.getLastChild().attachChild(spriteBarLeft);

		/**
		 * Player Right (1)
		 */
		final int player1PositionX = CAMERA_WIDTH - textureRegionPlayer1.getWidth();
		final int player1PositionY = (CAMERA_HEIGHT / 2) - this.textureRegionPlayer1.getHeight() / 2;
		this.spritePlayer1 = new Sprite(player1PositionX, player1PositionY, this.textureRegionPlayer1);
		this.bodyPlayer1 = PhysicsFactory.createBoxBody(this.physicWorld, this.spritePlayer1, BodyType.StaticBody, FIXTURE_PLAYERS);
		scene.getLastChild().attachChild(spritePlayer1);
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
		 * Player Left (2)
		 */
		final int player2PositionX = 0;
		final int player2PositionY = (CAMERA_HEIGHT / 2) - this.textureRegionPlayer1.getHeight() / 2;
		this.spritePlayer2 = new Sprite(player2PositionX, player2PositionY, this.textureRegionPlayer2);
		this.bodyPlayer2 = PhysicsFactory.createBoxBody(this.physicWorld, this.spritePlayer2, BodyType.StaticBody, FIXTURE_PLAYERS);
		scene.getLastChild().attachChild(spritePlayer2);
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
						Vector2 newPosition = new Vector2(bodyPlayer2.getPosition().x, touchY / PTM_RATIO);
						bodyPlayer2.setTransform(newPosition, 0);
					break;
				}
				return true;
			}
		};
		scene.registerTouchArea(shapeTouchPlayer2);
		
		/**
		 * Middle line
		 */
		/**
		 * Middle line
		 */
		final Sprite middleLine = new Sprite(CAMERA_WIDTH / 2, 0, this.textureRegionMiddleLine);
		scene.getLastChild().attachChild(middleLine);		
		
		/**
		 * Ball
		 */
		this.spriteBall = new Sprite((CAMERA_WIDTH / 2) - (this.textureRegionBall.getWidth() / 2), (CAMERA_HEIGHT / 2) - (this.textureRegionBall.getHeight() / 2), this.textureRegionBall);
		this.bodyBall = PhysicsFactory.createCircleBody(this.physicWorld,spriteBall,BodyType.DynamicBody,FIXTURE_BALL);
		scene.getLastChild().attachChild(spriteBall);
		this.physicWorld.registerPhysicsConnector(new PhysicsConnector(spriteBall, bodyBall, true, true));
		this.bodyBall.setLinearVelocity(5,5);
		activeBall = true;
		//this.bodyBall.applyLinearImpulse(new Vector2(20,5),this.bodyBall.getPosition());

		scene.setTouchAreaBindingEnabled(true);

		menuScreen.getEngine().setScene(scene);
		
		Log.e("scene game", "OK");
	}

	@Override
	public void beginContact(Contact contact) {		
		Body bodyContact1 = contact.getFixtureA().getBody();
		Body bodyContact2 = contact.getFixtureB().getBody();
		if(bodyContact1.equals(bodyBall) || bodyContact2.equals(bodyBall)){
			if(bodyContact1.equals(bodyLeft) || bodyContact2.equals(bodyLeft)){
				this.scorePlayer2.setText("" + ++this.pointsPlayer2);
				
				if(this.pointsPlayer2 >= 7){
					removeBall = true;
					final Scene scene = menuScreen.getEngine().getScene();
					final String textVictory = new String("Player 2 has won the match!");
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
					scene.getLastChild().attachChild(text);
				}
				else{
					removeBall = true;
					resetBall = true;
					playerTime = 1;
				}				
			}else if(bodyContact1.equals(bodyRight) || bodyContact2.equals(bodyRight)){
				this.scorePlayer1.setText("" + ++this.pointsPlayer1);
				if(this.pointsPlayer1 >= 7){
					removeBall = true;
					final Scene scene = menuScreen.getEngine().getScene();
					final String textVictory = new String("Player 1 has won the match!");
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
					scene.getLastChild().attachChild(text);
				}
				else{
					removeBall = true;
					resetBall = true;
					playerTime = 2;
				}
			}
			pingSound.play();
		}		
		menuScreen.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {		    	 
				if(removeBall){
					activeBall = false;
					bodyBall.setTransform((CAMERA_WIDTH / PTM_RATIO) + 10,(CAMERA_HEIGHT / PTM_RATIO) + 10, 0);
					removeBall = false;
					if(resetBall){
						bodyBall.setTransform((CAMERA_WIDTH / 2) / PTM_RATIO, (CAMERA_HEIGHT / 2) / PTM_RATIO, 0f);
						if(playerTime == 1){
							bodyBall.setLinearVelocity(10,17);
						}
						else{
							bodyBall.setLinearVelocity(-10,17);
						}
						
						resetBall = false;
						activeBall = true;
					}
				}
			}
		});

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
		if(Math.abs(speedBall.x) < MINIMUM_BALL_SPEED){
			if(speedBall.x < 0){
				speedX = speedBall.x - MINIMUM_BALL_SPEED;
			}else{
				speedX = speedBall.x + MINIMUM_BALL_SPEED;
			}
			speedY = speedBall.y;
			refreshVelocity = true;
		}
		menuScreen.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				if(refreshVelocity){
					if(activeBall){
						bodyBall.setLinearVelocity(new Vector2(speedX,speedY));
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

	public void setCAMERA_WIDTH(int cAMERA_WIDTH) { CAMERA_WIDTH = cAMERA_WIDTH; }	
	public void setCAMERA_HEIGHT(int cAMERA_HEIGHT) { CAMERA_HEIGHT = cAMERA_HEIGHT; }
	public int getCAMERA_WIDTH() { return CAMERA_WIDTH; }
	public int getCAMERA_HEIGHT() { return CAMERA_HEIGHT; }	
	
	public Texture getTextureBackground() {	return textureBackground; }	
	public Texture getTexturePlayer1() { return texturePlayer1; }
	public Texture getTexturePlayer2() { return texturePlayer2; }
	public Texture getTextureBall() { return textureBall; }
	public Texture getTextureScore() { return textureScore;	}
	public Texture getTextureVictory() { return textureVictory;	}
	public Texture getTexturePause() { return texturePause; }
	public Texture getTextureBarRight() { return textureBarRight; }
	public Texture getTextureBarLeft() { return textureBarLeft; }
	public Texture getTextureMiddleLine() { return textureMiddleLine; }
	public Texture getTextureBGScore() { return textureBGScore; }
	public TextureRegion getTextureRegionBackground() {	return textureRegionBackground; }
	public TextureRegion getTextureRegionPlayer1() { return textureRegionPlayer1; }
	public TextureRegion getTextureRegionPlayer2() { return textureRegionPlayer2; }
	public TextureRegion getTextureRegionBall() { return textureRegionBall; }
	public TextureRegion getTextureRegionPause() { return textureRegionPause; }
	public TextureRegion getTextureRegionBarRight() { return textureRegionBarRight; }
	public TextureRegion getTextureRegionBarLeft() { return textureRegionBarLeft; }
	public TextureRegion getTextureRegionMiddleLine() { return textureRegionMiddleLine; }
	public TextureRegion getTextureRegionBGScore() { return textureRegionBGScore; }
	public Font getFontVictory() { return fontVictory; }
	public Font getFontScore() { return fontScore; }
	public void setTexturePlayer1(Texture texturePlayer1) { this.texturePlayer1 = texturePlayer1; }
	public void setTexturePlayer2(Texture texturePlayer2) { this.texturePlayer2 = texturePlayer2; }
	public void setTextureBackground(Texture textureBackground) { this.textureBackground = textureBackground; }		
	public void setTextureBall(Texture textureBall) { this.textureBall = textureBall; }	
	public void setTextureScore(Texture textureScore) { this.textureScore = textureScore; }	
	public void setTextureVictory(Texture textureVictory) {	this.textureVictory = textureVictory; }
	public void setTexturePause(Texture texturePause) {	this.texturePause = texturePause; }
	public void setTextureBarRight(Texture t) { this.textureBarRight = t; }
	public void setTextureBarLeft(Texture t) { this.textureBarLeft = t; }
	public void setTextureMiddleLine(Texture t) { this.textureMiddleLine = t; }
	public void setTextureBGScore(Texture t) { this.textureBGScore = t; }
	public void setTextureRegionBackground(TextureRegion textureRegionBackground) { this.textureRegionBackground = textureRegionBackground; }	
	public void setTextureRegionPlayer1(TextureRegion textureRegionPlayer1) { this.textureRegionPlayer1 = textureRegionPlayer1; }	
	public void setTextureRegionPlayer2(TextureRegion textureRegionPlayer2) { this.textureRegionPlayer2 = textureRegionPlayer2;	}	
	public void setTextureRegionBall(TextureRegion textureRegionBall) {	this.textureRegionBall = textureRegionBall; }	
	public void setTextureRegionPause(TextureRegion textureRegionPause) {	this.textureRegionPause = textureRegionPause; }
	public void setTextureRegionBarRight(TextureRegion tr) { this.textureRegionBarRight = tr; }
	public void setTextureRegionBarLeft(TextureRegion tr) { this.textureRegionBarLeft = tr; }
	public void setTextureRegionMiddleLine(TextureRegion tr) { this.textureRegionMiddleLine = tr; }
	public void setTextureRegionBGScore(TextureRegion tr) { this.textureRegionBGScore = tr; }
	public void setFontScore(Font fontScore) { this.fontScore = fontScore; }	
	public void setFontVictory(Font fontVictory) { this.fontVictory = fontVictory; }
	
	public void Pause(){
		if (menuScreen.getEngine().isRunning()) {
			if(menuScreen.gameRunning){
				//scene.setChildScene(this.pauseGameScene, false, true, true);
				menuScreen.getEngine().stop();
			}			
		} else {
			if(menuScreen.gameRunning){
				//this.scene.clearChildScene();
				menuScreen.getEngine().start();				
			}

		}		
	}	
}