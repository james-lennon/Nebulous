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
import com.jameslennon.nebulous.net.BluetoothCallback;

public class BluetoothSearchScreen extends AbstractScreen {

	private Label loading;
	private LabelStyle style;

	public BluetoothSearchScreen(Nebulous g) {
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
		style = new LabelStyle(Globals.getFont(), Color.WHITE);

		message("Finding Players; Tap to Host...");

		Label msg = new Label("*Make sure WiFi or Bluetooth is on!", skin);
		msg.setPosition(stage.getWidth() / 2 - msg.getWidth() / 2,
				stage.getHeight() - msg.getHeight() - 10);
		stage.addActor(msg);

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

		stage.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				switchToHost();
			}

		});
	}

	protected void switchToHost() {
		message("Hosting Game...");
		Nebulous.bluetooth.startServer();
	}

	@Override
	public void show() {
		super.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				Nebulous.bluetooth.setCallback(new BluetoothCallback() {

					@Override
					public void onFindHost() {
						message("Found Host; joining...");
					}
				});
				Nebulous.bluetooth.startClient();
			}
		}).start();
	}

	public void message(String s) {
		if (loading != null)
			loading.remove();
		loading = new Label(s, style);
		loading.setPosition(stage.getWidth() / 2 - loading.getWidth() / 2, 30);
		stage.addActor(loading);
	}

	@Override
	public void hide() {
		super.hide();
	}

	@Override
	public void dispose() {
		super.dispose();
		Nebulous.bluetooth.disconnect();
	}

	@Override
	public void back() {
		game.setState(Nebulous.STATE_CHOOSE_GAME);
		Nebulous.bluetooth.disconnect();
	}

}
