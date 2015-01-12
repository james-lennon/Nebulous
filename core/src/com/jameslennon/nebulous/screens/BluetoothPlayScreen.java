package com.jameslennon.nebulous.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.game.grid.GridMap;
import com.jameslennon.nebulous.util.MiniMap;

public class BluetoothPlayScreen extends PlayScreen {

	private Label timeLabel, kills, deaths;
	private Vector2 vel;
	private Vector2 pos;
	private long endtime;

	public BluetoothPlayScreen(Nebulous g) {
		super(g);
	}

	@Override
	public void show() {
		super.show();
		endtime = System.currentTimeMillis() + Nebulous.bluetooth.getLength();
		gm = GridMap.loadFromFile(Gdx.files.internal("data/"
				+ Nebulous.bluetooth.getMap() + ".map"));
		Globals.setMap(gm);

		Nebulous.bluetooth.getLobby().initShips(gm, stage);
		s = Nebulous.bluetooth.getLobby().getMembers()
				.get((int) Nebulous.bluetooth.getPlayerId()).getShip();
		s.setVibrate(true);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		Nebulous.bluetooth.getLobby().showShips(stage);

		Table t = new Table(skin);

		timeLabel = new Label("Time Left", skin);
		timeLabel.setFontScale(2);
		t.add(timeLabel).colspan(2).row();
		kills = new Label("Kills: 0", skin);
		deaths = new Label("Deaths: 0", skin);
		t.add(kills).pad(5).colspan(2).row();
		t.add(deaths).pad(5).colspan(2);
		t.setX(guiStage.getCamera().viewportWidth / 2);
		t.setY(guiStage.getCamera().viewportHeight - t.getHeight() - 70);
		guiStage.addActor(t);
		mm = new MiniMap(guiStage);
		Globals.setMiniMap(mm);
	}

	@Override
	public void resume() {
		super.resume();
		Nebulous.bluetooth.getLobby().resumeShips();
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		Nebulous.bluetooth.getLobby().update();
		pos = s.getBody().getPosition();
		vel = s.getBody().getLinearVelocity();
		Nebulous.bluetooth.sendPosToGame(pos.x, pos.y, vel.x, vel.y, s
				.getBody().getAngle(), s.getBody().getAngularVelocity());
		long diff = (endtime - System.currentTimeMillis()) / 1000;
		timeLabel.setText(String.format("%d:%02d", diff / 60, diff % 60));

		kills.setText("Kills: " + s.getPeer().getKills());
		deaths.setText("Deaths: " + s.getPeer().getDeaths());
		if (diff <= 0) {
			// end game
			nebulous.setState(Nebulous.STATE_BT_OVER);
		}

	}

	@Override
	public void hide() {
		super.hide();
		new Thread(new Runnable() {

			@Override
			public void run() {
				Nebulous.bluetooth.disconnect();
			}
		}).start();
	}

}
