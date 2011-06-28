package br.com.jera.jerapong;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class JeraPongGame extends BaseGameActivity implements IOnSceneTouchListener, ContactListener {
    
	/** ######## GLOBAL ######## **/
	
	private int CAMERA_WIDTH = 0;
	private int CAMERA_HEIGHT = 0;
	
	/** ######## GLOBAL ######## **/
	
	/** ######## GAME ######## **/
	
	private Texture textureBackground;
	private Texture texturePlayer1;
	private Texture texturePlayer2;
	private Texture textureBall;
	
	
	private TextureRegion textureRegionBackground;
	private TextureRegion textureRegionPlayer1;
	private TextureRegion textureRegionPlayer2;
	private TextureRegion textureRegionBall;
	
	private PhysicsWorld physicWorld;
	private static final FixtureDef FIXTURE_PLAYERS = PhysicsFactory.createFixtureDef(10000000000f, 1.2f, 0f);
	private static final FixtureDef FIXTURE_BALL = PhysicsFactory.createFixtureDef(2f, 1f, 0f);
	
	private Sprite spritePlayer1;
    private Body bodyPlayer1;
    
    private Sprite spritePlayer2;
    private Body bodyPlayer2;
    
    private Sprite spriteBall;
    private Body bodyBall;
    
    final float PHYSICS_RATE = 21.81818182f;
    final float MAXIMUM_BALL_SPEED = 50f;        
	
	final int playerBorderOffset = 100;
	
	/** ######## GAME ######## **/
	
	@Override
	public Engine onLoadEngine() {
		
		CAMERA_HEIGHT = getWindowManager().getDefaultDisplay().getHeight();
		CAMERA_WIDTH = getWindowManager().getDefaultDisplay().getWidth();
		
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		engineOptions.getTouchOptions().setRunOnUpdateThread(true);
		
		return new Engine(engineOptions);
	}

	@Override
	public void onLoadResources() {
		
		 this.textureBackground = new Texture(1024, 1024, TextureOptions.DEFAULT);
         this.texturePlayer1 = new Texture(256,256,TextureOptions.DEFAULT);
         this.texturePlayer2 = new Texture(256,256,TextureOptions.DEFAULT);
         this.textureBall = new Texture(64,64,TextureOptions.DEFAULT);
         
         this.textureRegionBackground = TextureRegionFactory.createFromAsset(this.textureBackground, this, "gfx/background.png",0,0);
         this.textureRegionPlayer1 = TextureRegionFactory.createFromAsset(this.texturePlayer1, this, "gfx/player.png",0,0);
         this.textureRegionPlayer2 = TextureRegionFactory.createFromAsset(this.texturePlayer2, this, "gfx/player.png",0,0);
         this.textureRegionBall = TextureRegionFactory.createFromAsset(this.textureBall, this, "gfx/ball.png",0,0);
         
         this.mEngine.getTextureManager().loadTexture(this.textureBackground);
         this.mEngine.getTextureManager().loadTexture(this.texturePlayer1);
         this.mEngine.getTextureManager().loadTexture(this.texturePlayer2);
         this.mEngine.getTextureManager().loadTexture(this.textureBall);
		
	}

	@Override
	public Scene onLoadScene() {
		
		this.textureBackground = new Texture(1024, 1024, TextureOptions.DEFAULT);
        this.texturePlayer1 = new Texture(256,256,TextureOptions.DEFAULT);
        this.texturePlayer2 = new Texture(256,256,TextureOptions.DEFAULT);
        this.textureBall = new Texture(64,64,TextureOptions.DEFAULT);
        
        this.textureRegionBackground = TextureRegionFactory.createFromAsset(this.textureBackground, this, "gfx/background.png",0,0);
        this.textureRegionPlayer1 = TextureRegionFactory.createFromAsset(this.texturePlayer1, this, "gfx/player.png",0,0);
        this.textureRegionPlayer2 = TextureRegionFactory.createFromAsset(this.texturePlayer2, this, "gfx/player.png",0,0);
        this.textureRegionBall = TextureRegionFactory.createFromAsset(this.textureBall, this, "gfx/ball.png",0,0);
        
        this.mEngine.getTextureManager().loadTexture(this.textureBackground);
        this.mEngine.getTextureManager().loadTexture(this.texturePlayer1);
        this.mEngine.getTextureManager().loadTexture(this.texturePlayer2);
        this.mEngine.getTextureManager().loadTexture(this.textureBall);
        this.mEngine.registerUpdateHandler(new FPSLogger());
        final Scene scene = new Scene(2);
        scene.setOnSceneTouchListener(this);
        //scene.setBackground(new ColorBackground(1f, 0, 0));
        Log.e("world fisics","loading");
        this.physicWorld = new PhysicsWorld(new Vector2(0,0),false);//SensorManager.GRAVITY_EARTH),false);
        this.physicWorld.setContactListener(this);
        Log.e("world fisics", "ok");
        
        final Shape ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2);
        final Shape roof = new Rectangle(0, 0, CAMERA_WIDTH, 2);
        final Shape left = new Rectangle(0, 0, 2, CAMERA_HEIGHT);
        final Shape right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT);
        
        final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(1000000000f, 1f, 0.1f);
        PhysicsFactory.createBoxBody(this.physicWorld, ground, BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(this.physicWorld, roof, BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(this.physicWorld, left, BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(this.physicWorld, right, BodyType.StaticBody, wallFixtureDef);

        scene.getFirstChild().attachChild(ground);
        scene.getFirstChild().attachChild(roof);
        scene.getFirstChild().attachChild(left);
        scene.getFirstChild().attachChild(right);

        scene.registerUpdateHandler(this.physicWorld);
        
        //---BackGround---
        final Sprite background = new Sprite(0, 0, this.textureRegionBackground);
        scene.attachChild(background);
        
        //--Player 1--
        this.spritePlayer1 = new Sprite(playerBorderOffset, (CAMERA_HEIGHT / 2) - this.textureRegionPlayer1.getHeight() / 2, this.textureRegionPlayer1);
        this.bodyPlayer1 = PhysicsFactory.createBoxBody(this.physicWorld,this.spritePlayer1,BodyType.DynamicBody,FIXTURE_PLAYERS);                
        scene.getLastChild().attachChild(spritePlayer1);
        this.physicWorld.registerPhysicsConnector(new PhysicsConnector(this.spritePlayer1, this.bodyPlayer1, true, true));
        this.bodyPlayer1.setFixedRotation(true);
        
        //--Player 2--
        this.spritePlayer2 = new Sprite(CAMERA_WIDTH - playerBorderOffset - textureRegionPlayer2.getWidth(), (CAMERA_HEIGHT / 2) - this.textureRegionPlayer2.getHeight() / 2, this.textureRegionPlayer2);
        this.bodyPlayer2 = PhysicsFactory.createBoxBody(this.physicWorld,this.spritePlayer2,BodyType.DynamicBody,FIXTURE_PLAYERS);                
        scene.getLastChild().attachChild(spritePlayer2);
        this.physicWorld.registerPhysicsConnector(new PhysicsConnector(this.spritePlayer2, this.bodyPlayer2, true, true));
        this.bodyPlayer2.setFixedRotation(true);
		
        //---Ball---
        this.spriteBall = new Sprite((CAMERA_WIDTH / 2) - (this.textureRegionBall.getWidth() / 2), (CAMERA_HEIGHT / 2) - (this.textureRegionBall.getHeight() / 2), this.textureRegionBall);
        this.bodyBall = PhysicsFactory.createCircleBody(this.physicWorld,spriteBall,BodyType.DynamicBody,FIXTURE_BALL);
        scene.getLastChild().attachChild(spriteBall);
        this.physicWorld.registerPhysicsConnector(new PhysicsConnector(spriteBall, bodyBall, true, true));
        this.bodyBall.setLinearVelocity(70,0);
        
		return scene;
	}

	@Override
	public void onLoadComplete() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
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
    }
	
	public Vector2 ColisionWall(Sprite sprite, Body body, float limitY, float positionY){
        Vector2 finalPosition = new Vector2(0,0);
        
        finalPosition.x = body.getPosition().x;
        
        if(positionY < 1){
                finalPosition.y = 1;
        }
        else if(positionY > (limitY - (sprite.getHeight() / PHYSICS_RATE) - 2)){ // -2 is the width of the wall
                finalPosition.y = limitY - (sprite.getHeight() / PHYSICS_RATE) - 2;
        }
        else{
                finalPosition.y = positionY;
        }
        return finalPosition;                
}

	@Override
	public void beginContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endContact(Contact contact) {
		
		Vector2 speed1 = contact.getFixtureA().getBody().getLinearVelocity();
        Vector2 speed2 = contact.getFixtureB().getBody().getLinearVelocity();
        if(speed1.len() > MAXIMUM_BALL_SPEED){
                float speedX = (MAXIMUM_BALL_SPEED / speed1.len()) * speed1.x;
                float speedY = (MAXIMUM_BALL_SPEED / speed1.len()) * speed1.y;
                contact.getFixtureA().getBody().setLinearVelocity(new Vector2(speedX,speedY));
        }
        if(speed2.len() > MAXIMUM_BALL_SPEED){
                float speedX = (MAXIMUM_BALL_SPEED / speed2.len()) * speed2.x;
                float speedY = (MAXIMUM_BALL_SPEED / speed2.len()) * speed2.y;
                contact.getFixtureB().getBody().setLinearVelocity(new Vector2(speedX,speedY));
        }
		
	}

	@Override
	public void preSolve(Contact pContact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact pContact) {
		// TODO Auto-generated method stub
		
	}
	
}