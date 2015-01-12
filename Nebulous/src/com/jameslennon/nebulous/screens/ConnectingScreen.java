package com.jameslennon.nebulous.screens;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.net.NetworkManager;
import com.jameslennon.nebulous.net.NetworkManager.ConnectionListener;
import com.jameslennon.nebulous.util.StarBackground;

public class ConnectingScreen extends AbstractScreen {

	private Label textLabel;
	private Thread connThread;

	public ConnectingScreen(Nebulous g) {
		super(g);
	}

	@Override
	public void resize(int width, int height) {
		boolean mobile = Gdx.app.getType() == ApplicationType.Android;
		super.resize(width, height);
		StarBackground.initialize(stage, width, height);
		textLabel = new Label("Connecting to Server...", skin);
		stage.addActor(textLabel);
		if (connThread != null && connThread.isAlive()) {
			connThread.interrupt();
		}
		connThread = new Thread(new Runnable() {

			@Override
			public void run() {
				textLabel.setText("Connecting to Server...");
				NetworkManager.connect(new ConnectionListener() {
					@Override
					public void onConnect(boolean success, byte[] data) {
						if (success) {
							if (data[0] == 1) {
								Gdx.app.log(Nebulous.log, "success connecting!");
								next();
							} else {
								textLabel.setText("Username already in use");
								Gdx.app.log(Nebulous.log, "Used username");
							}
						} else {
							// textLabel.setText("Unable to connect to server");
							// Gdx.app.postRunnable(new Runnable() {
							//
							// @Override
							// public void run() {
							// game.setState(Nebulous.STATE_MENU);
							// }
							// });
							Gdx.app.log(Nebulous.log, "Failed to connect!");
						}
					}
				});
			}
		});

		TextButton back = new TextButton("Back", skin);
		back.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				back();
			}

		});
		back.setSize(75, 50);
		back.setPosition(5, stage.getHeight() - back.getHeight() - 5);
		stage.addActor(back);
		connThread.start();
	}

	protected void connect() {
		connThread.start();
	}

	@Override
	public void hide() {
		super.hide();
		NetworkManager.stopConnecting();
	}

	public void setMessage(String msg) {
		textLabel.setText(msg);
	}

	private synchronized void next() {
		textLabel.setText("Connected");
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				game.setState(Nebulous.STATE_LOBBY);
			}
		});
	}

	public void back() {
		game.setState(Nebulous.STATE_MENU);
	}

}
