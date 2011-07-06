package br.com.jera.jerapong;

import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.opengl.texture.source.AssetTextureSource;
import org.anddev.andengine.opengl.texture.source.ITextureSource;

import android.app.Activity;

public class SplashScreen extends BaseSplashPong {
		
	private static final int SPLASH_DURATION = 3;
	private static final float SPLASH_SCALE_FROM = 0.8f;

	@Override
	protected ScreenOrientation getScreenOrientation() {
		return ScreenOrientation.LANDSCAPE;
	}

	@Override
	protected ITextureSource onGetSplashTextureSource() {
		return new AssetTextureSource(this,"gfx/splash/splash_jera.png");
	}

	@Override
	protected float getSplashDuration() {
		return SPLASH_DURATION;
	}
	
	@Override
	protected float getSplashScaleFrom(){
		return SPLASH_SCALE_FROM;
	}

	@Override
	protected Class<? extends Activity> getFollowUpActivity() {
		return MenuScreen.class;
	}

}
