package com.jameslennon.nebulous.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.jameslennon.nebulous.Nebulous;

public abstract class AbstractScreen implements Screen {

	protected Stage stage;
	protected final Nebulous game;
	protected final SpriteBatch batch;
	protected final Skin skin;
	protected float xscale, yscale;

	public AbstractScreen(Nebulous g) {
		game = g;
		if (Gdx.graphics.getWidth() >= Nebulous.APP_WIDTH
				&& Gdx.graphics.getHeight() >= Nebulous.APP_HEIGHT) {
			stage = new Stage(new ExtendViewport(Nebulous.APP_WIDTH,
					Nebulous.APP_HEIGHT));
		} else {
			float h = Gdx.graphics.getHeight();
			float w = h * 16 / 10;
			stage = new Stage(new ExtendViewport(w, h));
		}
		// new ScalingViewport(Scaling.fill, 0, 0));
		batch = new SpriteBatch();
		skin = new Skin(Gdx.files.internal("layout/uiskin.json"));
	}

	@Override
	public void render(float delta) {
		// if(Gdx.gl20==null){
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// }else{
		// Gdx.gl20.glClearColor(0f, 0f, 0f, 1f);
		// Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// }
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
//		stage.getCamera().position.set(width/2, height/2, 0);
		// if (width >= Nebulous.APP_WIDTH && height >= Nebulous.APP_HEIGHT) {
		// stage.getViewport().update(width, height, true);
		// } else {
		// // width = (int) (height * 16f/10f);
		// stage.getViewport().update(width, height, true);
		// }
	}

	@Override
	public void show() {
		Gdx.input.setOnscreenKeyboardVisible(false);
		InputAdapter backAdapter = new InputAdapter() {

			@Override
			public boolean keyDown(int keycode) {
				if (keycode == Keys.BACK) {
					Gdx.app.log(Nebulous.log, "back");
					back();
					return true;
				}
				return false;
			}

		};
		InputMultiplexer im = new InputMultiplexer(backAdapter, stage);
		Gdx.input.setInputProcessor(im);
		Gdx.input.setCatchBackKey(true);
//		Gdx.app.log(Nebulous.log, "showing screen "
//				+ this.getClass().getSimpleName());
	}

	public abstract void back();

	@Override
	public void hide() {
//		Gdx.app.log(Nebulous.log, "hiding screen "
//				+ this.getClass().getSimpleName());
		Gdx.input.setOnscreenKeyboardVisible(false);
//		stage.addAction(Actions.sequence(Actions.fadeOut(.5f), Actions.run(new Runnable() {
//			
//			@Override
//			public void run() {
//				Gdx.app.postRunnable(new Runnable() {
//					
//					@Override
//					public void run() {
//						stage.clear();
//					}
//				});
//			}
//		})));
		stage.clear();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		skin.dispose();
		batch.dispose();
		stage.dispose();
	}
	
	public void next(Runnable r) {
		stage.addAction(Actions.sequence(Actions.fadeOut(.25f), Actions.run(r)));
	}

}