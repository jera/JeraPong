package br.com.jera.jerapong;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.AlphaModifier;
import org.anddev.andengine.entity.modifier.ParallelEntityModifier;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.modifier.SequenceEntityModifier;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.entity.text.TickerText;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.input.touch.controller.MultiTouch;
import org.anddev.andengine.extension.input.touch.controller.MultiTouchController;
import org.anddev.andengine.extension.input.touch.exception.MultiTouchException;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.HorizontalAlign;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class JeraPongGame extends BaseGameActivity implements /*IOnSceneTouchListener,*/ ContactListener {

	/** ######## GLOBAL ######## **/

	private int CAMERA_WIDTH = 0;
	private int CAMERA_HEIGHT = 0;

	/** ######## GLOBAL ######## **/

	/** ######## GAME ######## **/

	private Texture textureBackground;
	private Texture texturePlayer1;
	private Texture texturePlayer2;
	private Texture textureBall;
	private Texture textureScore;
	private Texture textureVictory;


	//private TextureRegion textureRegionBackground;
	private TextureRegion textureRegionPlayer1;
	private TextureRegion textureRegionPlayer2;
	private TextureRegion textureRegionBall;

	private PhysicsWorld physicWorld;
	private static final FixtureDef FIXTURE_PLAYERS = PhysicsFactory.createFixtureDef(10f, 1.2f, 0f);
	private static final FixtureDef FIXTURE_BALL = PhysicsFactory.createFixtureDef(1f, 1f, 0f); //densidade,restituição,frição

	private Sprite spritePlayer1;
	private Body bodyPlayer1;

	private Sprite spritePlayer2;
	private Body bodyPlayer2;

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

	final int PLAYER_BORDER_OFFSET = 100;
	boolean removeBall = false;
	boolean resetBall = false;


	/** ######## GAME ######## **/

	@Override
	public Engine onLoadEngine() {

		CAMERA_HEIGHT = getWindowManager().getDefaultDisplay().getHeight();
		CAMERA_WIDTH = getWindowManager().getDefaultDisplay().getWidth();

		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		final Engine engine = new Engine(engineOptions); 
		//engineOptions.getTouchOptions().setRunOnUpdateThread(true);

		try {
			if(MultiTouch.isSupported(this)) {
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

		this.textureBackground = new Texture(1024, 1024, TextureOptions.DEFAULT);
		this.texturePlayer1 = new Texture(256,256,TextureOptions.DEFAULT);
		this.texturePlayer2 = new Texture(256,256,TextureOptions.DEFAULT);
		this.textureBall = new Texture(64,64,TextureOptions.DEFAULT);
		this.textureScore = new Texture(256,256,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.textureVictory = new Texture(512,512,TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		//this.textureRegionBackground = TextureRegionFactory.createFromAsset(this.textureBackground, this, "gfx/background.png",0,0);
		this.textureRegionPlayer1 = TextureRegionFactory.createFromAsset(this.texturePlayer1, this, "gfx/player.png",0,0);
		this.textureRegionPlayer2 = TextureRegionFactory.createFromAsset(this.texturePlayer2, this, "gfx/player.png",0,0);
		this.textureRegionBall = TextureRegionFactory.createFromAsset(this.textureBall, this, "gfx/ball.png",0,0);		
		this.fontScore = new Font(this.textureScore, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 48, true, Color.WHITE);
		this.fontVictory = new Font(this.textureVictory, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 30, true, Color.WHITE);

		this.mEngine.getTextureManager().loadTexture(this.textureBackground);
		this.mEngine.getTextureManager().loadTexture(this.texturePlayer1);
		this.mEngine.getTextureManager().loadTexture(this.texturePlayer2);
		this.mEngine.getTextureManager().loadTexture(this.textureBall);
		this.mEngine.getTextureManager().loadTexture(this.textureScore);
		this.mEngine.getTextureManager().loadTexture(this.textureVictory);
		this.mEngine.getFontManager().loadFont(this.fontScore);
		this.mEngine.getFontManager().loadFont(this.fontVictory);

	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		final Scene scene = new Scene(2);
		scene.setOnAreaTouchTraversalFrontToBack();

		this.physicWorld = new PhysicsWorld(new Vector2(0,0),false);
		this.physicWorld.setContactListener(this);

		final Shape ground = new Rectangle(0, CAMERA_HEIGHT - 10, CAMERA_WIDTH, 10);
		final Shape roof = new Rectangle(0, 0, CAMERA_WIDTH, 10);
		final Shape left = new Rectangle(0, 0, 2, CAMERA_HEIGHT);
		final Shape right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(10f, 1f, 1.5f);
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
		//final Sprite background = new Sprite(0, 0, this.textureRegionBackground);
		//scene.attachChild(background);
		scene.setBackground(new ColorBackground(0f,0f,0f));

		scorePlayer1 = new ChangeableText((CAMERA_WIDTH / 2) - 50,30,this.fontScore,"0","0".length());
		scorePlayer2 = new ChangeableText((CAMERA_WIDTH / 2) + 20,30,this.fontScore,"0","0".length());

		scene.getLastChild().attachChild(scorePlayer1);		
		scene.getLastChild().attachChild(scorePlayer2);

		//Player 1
		//final int player1PositionX = PLAYER_BORDER_OFFSET;
		final int player1PositionX = 10;
		final int player1PositionY = (CAMERA_HEIGHT / 2) - this.textureRegionPlayer1.getHeight() / 2;
		this.spritePlayer1 = new Sprite(player1PositionX,player1PositionY, this.textureRegionPlayer1){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				switch(pSceneTouchEvent.getAction()) {					
				case TouchEvent.ACTION_MOVE:
					Vector2 newPosition = new Vector2(bodyPlayer1.getPosition().x, pSceneTouchEvent.getY() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
					bodyPlayer1.setTransform(newPosition, 0);												
					break;
				}
				return true;
			}
		};
		this.bodyPlayer1 = PhysicsFactory.createBoxBody(this.physicWorld,this.spritePlayer1,BodyType.StaticBody,FIXTURE_PLAYERS);
		scene.registerTouchArea(spritePlayer1);
		scene.getLastChild().attachChild(spritePlayer1);		
		this.physicWorld.registerPhysicsConnector(new PhysicsConnector(this.spritePlayer1, this.bodyPlayer1, true, true));
		this.bodyPlayer1.setFixedRotation(true);

		//--Player 2--
		final int player2PositionX = CAMERA_WIDTH - textureRegionPlayer2.getWidth() - 10;
		final int player2PositionY =  (CAMERA_HEIGHT / 2) - this.textureRegionPlayer2.getHeight() / 2;
		this.textureRegionPlayer2.setFlippedHorizontal(true);
		this.spritePlayer2 = new Sprite(player2PositionX,player2PositionY, this.textureRegionPlayer2){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				switch(pSceneTouchEvent.getAction()) {					
				case TouchEvent.ACTION_MOVE:
					Vector2 newPosition = new Vector2(bodyPlayer2.getPosition().x, pSceneTouchEvent.getY() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);						
					bodyPlayer2.setTransform(newPosition, 0);						
					break;					
				}
				return true;
			}
		};		
		this.bodyPlayer2 = PhysicsFactory.createBoxBody(this.physicWorld,this.spritePlayer2,BodyType.StaticBody,FIXTURE_PLAYERS);
		scene.registerTouchArea(spritePlayer2);
		scene.getLastChild().attachChild(spritePlayer2);		
		this.physicWorld.registerPhysicsConnector(new PhysicsConnector(this.spritePlayer2, this.bodyPlayer2, true, true));
		this.bodyPlayer2.setFixedRotation(true);

		//---Ball---
		this.spriteBall = new Sprite((CAMERA_WIDTH / 2) - (this.textureRegionBall.getWidth() / 2), (CAMERA_HEIGHT / 2) - (this.textureRegionBall.getHeight() / 2), this.textureRegionBall);
		this.bodyBall = PhysicsFactory.createCircleBody(this.physicWorld,spriteBall,BodyType.DynamicBody,FIXTURE_BALL);
		scene.getLastChild().attachChild(spriteBall);
		this.physicWorld.registerPhysicsConnector(new PhysicsConnector(spriteBall, bodyBall, true, true));
		this.bodyBall.setLinearVelocity(70,0);
		//this.bodyBall.applyLinearImpulse(new Vector2(20,5),this.bodyBall.getPosition());

		scene.setTouchAreaBindingEnabled(true);

		return scene;
	}



	@Override
	public void onLoadComplete() {

	}

	/*@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		float positionYToPutPlayer;
		float positionTouchX;
		float positionTouchY;
		float screenLimitY;

		if(this.physicWorld != null){
			positionTouchX = pSceneTouchEvent.getX();
			positionTouchY = pSceneTouchEvent.getY();
			positionYToPutPlayer = positionTouchY / PHYSICS_RATE;
			screenLimitY = this.CAMERA_HEIGHT / PHYSICS_RATE;                        
			if(positionTouchX < CAMERA_WIDTH / 2){
				if(positionTouchX < this.spritePlayer1.getX() + this.spritePlayer1.getWidth()){
					positionYToPutPlayer -= (this.spritePlayer1.getHeight() / PHYSICS_RATE) / 2f;
					this.bodyPlayer1.setTransform(ColisionWall(spritePlayer1, bodyPlayer1, screenLimitY, positionYToPutPlayer),0);
				}
			}
			else if(positionTouchX >= CAMERA_WIDTH / 2){
				if(positionTouchX > this.spritePlayer2.getX()){
					positionYToPutPlayer -= (this.spritePlayer2.getHeight() / PHYSICS_RATE) / 2f;
					this.bodyPlayer2.setTransform(ColisionWall(spritePlayer2, bodyPlayer2, screenLimitY, positionYToPutPlayer),0);
				}

			}
		}
		return false;
	}*/

	/*public Vector2 ColisionWall(Sprite sprite, Body body, float limitY, float positionY){
		Vector2 finalPosition = new Vector2(0,0);

		finalPosition.x = body.getPosition().x;

		if(positionY < 1){
			finalPosition.y = 1;
		}
		else if(positionY > (limitY - (sprite.getHeight() / PHYSICS_RATE) - WALL_WIDTH)){ // -2 is the width of the wall
			finalPosition.y = limitY - (sprite.getHeight() / PHYSICS_RATE) - WALL_WIDTH;
		}
		else{
			finalPosition.y = positionY;
		}
		return finalPosition;                
	}*/

	@Override
	public void beginContact(Contact contact) {		
		Body bodyContact1 = contact.getFixtureA().getBody();
		Body bodyContact2 = contact.getFixtureB().getBody();
		if(bodyContact1.equals(bodyBall) || bodyContact2.equals(bodyBall)){
			if(bodyContact1.equals(bodyLeft) || bodyContact2.equals(bodyLeft)){
				this.scorePlayer2.setText("" + ++this.pointsPlayer2);
				if(this.pointsPlayer2 >= 7){
					removeBall = true;
					final Scene scene = mEngine.getScene();
					final String textVictory = new String("Player 2 has won the match!");
					//this.pointsPlayer1 = 0;
					//this.pointsPlayer2 = 0;
					//this.scorePlayer1.setText("" + this.pointsPlayer1);
					//this.scorePlayer2.setText("" + this.pointsPlayer2);
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
				}				
			}else if(bodyContact1.equals(bodyRight) || bodyContact2.equals(bodyRight)){
				this.scorePlayer1.setText("" + ++this.pointsPlayer1);
				if(this.pointsPlayer1 >= 7){
					removeBall = true;
					final Scene scene = mEngine.getScene();
					final String textVictory = new String("Player 1 has won the match!");
					//this.pointsPlayer1 = 0;
					//this.pointsPlayer2 = 0;
					//this.scorePlayer1.setText("" + this.pointsPlayer1);
					//this.scorePlayer2.setText("" + this.pointsPlayer2);
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
				}
			}			
		}		
		this.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {		    	 
				if(removeBall){
					final Scene scene = mEngine.getScene();
					final PhysicsConnector physicsConnectorBall = physicWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(spriteBall);
					physicWorld.unregisterPhysicsConnector(physicsConnectorBall);
					physicWorld.destroyBody(physicsConnectorBall.getBody());
					scene.unregisterTouchArea(spriteBall);
					scene.getLastChild().detachChild(spriteBall);
					removeBall = false;
					if(resetBall){
						spriteBall.setPosition((CAMERA_WIDTH / 2) - (textureRegionBall.getWidth() / 2), (CAMERA_HEIGHT / 2) - (textureRegionBall.getHeight() / 2));
						bodyBall = PhysicsFactory.createCircleBody(physicWorld,spriteBall,BodyType.DynamicBody,FIXTURE_BALL);
						scene.getLastChild().attachChild(spriteBall);
						physicWorld.registerPhysicsConnector(new PhysicsConnector(spriteBall, bodyBall, true, true));
						bodyBall.setLinearVelocity(50f,10f);
						resetBall = false;
					}					
				}
			}
		});

	}

	@Override
	public void endContact(Contact contact) {
		//Limita velocidades: 4 < vel < 50		 
		Vector2 speedBall = bodyBall.getLinearVelocity();		
		Log.e("vel"," " + speedBall.x + " - " + speedBall.y);
		if(speedBall.len() > MAXIMUM_BALL_SPEED){
			speedX = (MAXIMUM_BALL_SPEED / speedBall.len()) * speedBall.x;
			speedY = (MAXIMUM_BALL_SPEED / speedBall.len()) * speedBall.y;
			refreshVelocity = true;			
		}
		if(Math.abs(speedBall.x) < MINIMUM_BALL_SPEED){
			if(speedBall.x < 0) speedX = MINIMUM_BALL_SPEED * -1f;
			else speedX = MINIMUM_BALL_SPEED;
			speedY = bodyBall.getLinearVelocity().y;
			refreshVelocity = true;
		}
		this.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				if(refreshVelocity){					
					Log.e("vel"," " + speedX + " - " + speedY);
					bodyBall.setLinearVelocity(new Vector2(speedX,speedY));					
					refreshVelocity = false;
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

}