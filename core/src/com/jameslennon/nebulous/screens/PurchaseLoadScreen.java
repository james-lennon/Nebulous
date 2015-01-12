package com.jameslennon.nebulous.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;

public class PurchaseLoadScreen extends AbstractScreen {

	public PurchaseLoadScreen(Nebulous g) {
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

		LabelStyle style = new LabelStyle(Globals.getFont(), Color.WHITE);

		Label loading = new Label("Accessing iTunes Store...", style);
		loading.setPosition(stage.getWidth() / 2 - loading.getWidth() / 2, 30);
		stage.addActor(loading);

		Label back = new Label("Back", style);
		back.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				back();
			}

		});
		// back.setSize(75, 50);
		back.setPosition(5, stage.getHeight() - back.getHeight() - 5);
		stage.addActor(back);
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
		game.setState(Nebulous.STATE_MENU);
	}

}
