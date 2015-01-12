package com.jameslennon.nebulous.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;

public class StartingGameScreen extends AbstractScreen {

	public StartingGameScreen(Nebulous g) {
		super(g);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		stage.clear();
		Image circImage = new Image(new Texture(
				Gdx.files.internal("res/splashCircle.png")));
		circImage.addAction(Actions.forever(Actions.rotateBy(-360, 5)));
		circImage.setOrigin(162, 162);
		circImage.setPosition(stage.getWidth() / 2 - circImage.getWidth() / 2,
				stage.getHeight() / 2 - circImage.getHeight() / 2 + 20);
		stage.addActor(circImage);

		Label loading = new Label("Starting Game...", new LabelStyle(
				Globals.getFont(), Color.WHITE));
		loading.setPosition(stage.getWidth() / 2 - loading.getWidth() / 2, 30);
		stage.addActor(loading);
	}

	@Override
	public void show() {
		super.show();
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void back() {
		Gdx.app.exit();
	}

}
