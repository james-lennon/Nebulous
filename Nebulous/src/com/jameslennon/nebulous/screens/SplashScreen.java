package com.jameslennon.nebulous.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.util.ParticleEffectActor;

public class SplashScreen extends AbstractScreen {

	private long start;

	public SplashScreen(Nebulous g) {
		super(g);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		stage.clear();
		// Image img = new Image(splashTextureRegion);
		/*
		 * Image img = new Image(new TextureRegion(splashTexture, 0, 0,
		 * Nebulous.APP_WIDTH, Nebulous.APP_HEIGHT));
		 * img.setScaling(Scaling.stretch); img.setAlign(Align.top |
		 * Align.right); img.setWidth((float) (imgw)); img.setHeight((float)
		 * (imgh)); img.setOrigin(img.getWidth() / 2, img.getHeight() / 2);
		 * img.setColor(1, 1, 1, 0); SequenceAction saAction =
		 * Actions.sequence(Actions.fadeIn(.75f), Actions.delay(1.75f),
		 * Actions.fadeOut(.75f), Actions.run(new Runnable() {
		 * 
		 * @Override public void run() { next(); } })); img.addAction(saAction);
		 * img.addAction(Actions.scaleBy(.1f, .1f, 3.25f)); //
		 * stage.addActor(img);
		 */

		// StarBackground.initialize(stage, width, height);
		// ship = new Image(Images.getImage("ship"));
		// ship.setOrigin(ship.getWidth() / 2, ship.getHeight() / 2);
		// ang = 0;
		// stage.addActor(ship);
		// ParticleEffect pe = new ParticleEffect();
		// pe.load(Gdx.files.internal("data/Trail.p"), Images.getAtlas());
		// pea = new ParticleEffectActor(pe, -1000, -1000);
		// stage.addActor(pea);

		Image circImage = new Image(new Texture(
				Gdx.files.internal("res/splashCircle.png")));
		circImage.addAction(Actions.forever(Actions.rotateBy(-360, 5)));
		circImage.setOrigin(162, 162);
		circImage.setPosition(stage.getWidth() / 2 - circImage.getWidth() / 2,
				stage.getHeight() / 2 - circImage.getHeight() / 2 + 20);
		stage.addActor(circImage);

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
				Gdx.files.internal("layout/Planer_Reg.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 50;
		BitmapFont font25 = generator.generateFont(parameter);
		Globals.setFont(font25);
		generator.dispose();
		// generator.generateFont(new FreeTypeFontParameter())

		Label loading = new Label("Loading...", new LabelStyle(font25,
				Color.WHITE));
		// loading.setFontScale(2.0f);
		loading.setPosition(stage.getWidth() / 2 - loading.getWidth() / 2, 30);
		stage.addActor(loading);
	}

	@Override
	public void show() {
		super.show();
		start = System.currentTimeMillis();
		// Load Stuff Here

	}

	@Override
	public void render(float delta) {
		super.render(delta);
		// ang += 2 * MathUtils.PI * delta;
		// float dx = MathUtils.cos(ang) * 100, dy = MathUtils.sin(ang) * 100;
		// ship.setPosition(imgw / 2 + dx, imgh / 2 + dy);
		// ship.setRotation((ang) * MathUtils.radiansToDegrees);
		// float ox = MathUtils.cos(ang - MathUtils.PI / 2), oy = MathUtils
		// .sin(ang - MathUtils.PI / 2);
		// pea.setPosition(
		// (ship.getX() + ship.getWidth() / 2) + ox * ship.getHeight() / 2,
		// (ship.getY() + ship.getHeight() / 2) + oy * ship.getHeight()
		// / 2);
		if (System.currentTimeMillis() - start > 4000) {
			next();
		}
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	public void next() {
		// pea.remove();
		// if (!UserData.exists()) {
		// game.setState(Nebulous.STATE_LOGIN);
		// } else {
		game.setState(Nebulous.STATE_MENU);
		// }
	}

	@Override
	public void back() {
		Gdx.app.exit();
	}

}
