package com.Dragonfly.LDDrumming;

import java.io.IOException;

import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.audio.sound.SoundFactory;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.layer.Layer;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnAreaTouchListener;
import org.anddev.andengine.entity.scene.Scene.ITouchArea;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.anddev.andengine.entity.scene.background.SpriteBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.input.touch.controller.MultiTouch;
import org.anddev.andengine.extension.input.touch.controller.MultiTouchController;
import org.anddev.andengine.extension.input.touch.exception.MultiTouchException;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.Texture.ITextureStateListener;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.source.AssetTextureSource;
import org.anddev.andengine.util.Debug;

import android.widget.Toast;

public class LD_Drumming extends BaseExample {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;

	// ===========================================================
	// Fields
	// ===========================================================

	private Camera mCamera;
	private Texture mTexture;
	private TextureRegion mBottomTextureRegion;
	private TextureRegion mTopTextureRegion;
	private TextureRegion mBackTextureRegion;
	private TextureRegion mBlueCircle;
	private TextureRegion mRedCircle;
	private TextureRegion mBlueCircleClear;
	private TextureRegion mRedCircleClear;
	private Sound mCenterSound;
	private Sound mFarSound;

	private SpriteBackground lion;
	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public Engine onLoadEngine() {
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		final Engine engine = new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera).setNeedsSound(true));
		
		try {
			if(MultiTouch.isSupported(this)) {
				engine.setTouchController(new MultiTouchController());
			} else {
				Toast.makeText(this, "Sorry your device does NOT support MultiTouch!\n\n(Falling back to SingleTouch.)", Toast.LENGTH_LONG).show();
			}
		} catch (final MultiTouchException e) {
			Toast.makeText(this, "Sorry your Android Version does NOT support MultiTouch!\n\n(Falling back to SingleTouch.)", Toast.LENGTH_LONG).show();
		}
		
		return engine;
	}

	@Override
	public void onLoadResources() { 
		this.mTexture = new Texture(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA, new ITextureStateListener.TextureStateAdapter());
		TextureRegionFactory.setAssetBasePath("gfx/");
		this.mTopTextureRegion = TextureRegionFactory.createFromAsset(this.mTexture, this, "redButton.png", 0, 100);
		this.mBottomTextureRegion = TextureRegionFactory.createFromAsset(this.mTexture, this, "blueButton.png", 0, 0);
		this.mBlueCircle = TextureRegionFactory.createFromAsset(this.mTexture, this, "blueCircle.png", 0, 200);
		this.mRedCircle = TextureRegionFactory.createFromAsset(this.mTexture, this, "redCircle.png", 0, 250);
		this.mBlueCircleClear = TextureRegionFactory.createFromAsset(this.mTexture, this, "blueCircleClear.png", 0, 300);
		this.mRedCircleClear = TextureRegionFactory.createFromAsset(this.mTexture, this, "redCircleClear.png", 0, 350);
		this.mBackTextureRegion = TextureRegionFactory.createFromAsset(this.mTexture, this, "background.png", 0, 400);
		SoundFactory.setAssetBasePath("mfx/");
		try {
			this.mCenterSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "center2.ogg");
			this.mFarSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "far.ogg");
		} catch (final IOException e) {
			Debug.e(e);
		}

		this.mEngine.getTextureManager().loadTexture(this.mTexture);
	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene(1);
		
		Sprite lion = new Sprite(0, 0, mBackTextureRegion);
		scene.setBackground(new SpriteBackground(lion));
		
		final int x1 = (CAMERA_WIDTH/10); 
		final int y1 = (CAMERA_HEIGHT - this.mBottomTextureRegion.getHeight()) - 10;
		
		final int x2 = 0;
		final int y2 = (y1 - this.mTopTextureRegion.getHeight()) - 10;
		
		final int x3 = CAMERA_WIDTH - (CAMERA_WIDTH/10)- this.mBottomTextureRegion.getWidth(); 
		final int y3 = (CAMERA_HEIGHT - this.mBottomTextureRegion.getHeight()) - 10;
		
		final int x4 = CAMERA_WIDTH - this.mBottomTextureRegion.getWidth();
		final int y4 = (y1 - this.mTopTextureRegion.getHeight()) - 10;
		
		final int xBC1 = CAMERA_WIDTH/2 - this.mBlueCircle.getWidth();
		final int yBC1 = CAMERA_HEIGHT/20;
		
		final int xBC2 = CAMERA_WIDTH/2;
		final int yBC2 = CAMERA_HEIGHT/20;
		
		final int xRC1 = CAMERA_WIDTH/2 - 2*this.mRedCircle.getWidth();
		final int yRC1 = CAMERA_HEIGHT/20;
		
		final int xRC2 = CAMERA_WIDTH/2 + this.mRedCircle.getWidth();
		final int yRC2 = CAMERA_HEIGHT/20;
		
		this.hit(scene, this.mBottomTextureRegion,this.mCenterSound , x1, y1);
		this.hit(scene, this.mTopTextureRegion,this.mFarSound , x2, y2);
		this.hit(scene, this.mBottomTextureRegion,this.mCenterSound , x3, y3);
		this.hit(scene, this.mTopTextureRegion,this.mFarSound , x4, y4);
		
		final Sprite BC1 = new Sprite(xBC1, yBC1, this.mBlueCircleClear);
		final Sprite BC2 = new Sprite(xBC2, yBC2, this.mBlueCircleClear);
		final Sprite RC1 = new Sprite(xRC1, yRC1, this.mRedCircleClear);
		final Sprite RC2 = new Sprite(xRC2, yRC2, this.mRedCircleClear);
		scene.getLastChild().attachChild(BC1);
		scene.getLastChild().attachChild(BC2);
		scene.getLastChild().attachChild(RC1);
		scene.getLastChild().attachChild(RC2);
		
		return scene;
	}

	@Override
	public void onLoadComplete() {

	}

	// ===========================================================
	// Methods
	// ===========================================================

	private void hit(final Scene pScene, final TextureRegion pic, final Sound sound, final int pX, final int pY)
	{
		final Sprite sprite = new Sprite(pX, pY, pic) {
			boolean touched = false;

			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				switch(pSceneTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					if(!this.touched)
					{
						this.touched = true;
						sound.play();
					}
					break;
				case TouchEvent.ACTION_UP:
					if(this.touched)
					{
						this.touched = false;
					}
					break;
				}
				return true;
			}
		};

		pScene.getLastChild().attachChild(sprite);
		pScene.registerTouchArea(sprite);
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
